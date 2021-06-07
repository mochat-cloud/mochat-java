package com.mochat.mochat.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mochat.mochat.common.constant.Const;
import com.mochat.mochat.common.em.workmessage.MsgTypeEnum;
import com.mochat.mochat.common.util.CommandUtil;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.RSAUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.common.util.wm.WorkMsgHelper;
import com.mochat.mochat.dao.entity.wm.WorkMsgEntity;
import com.mochat.mochat.dao.entity.wm.WorkMsgIndexEntity;
import com.mochat.mochat.dao.mapper.wm.WorkMsgIndexMapper;
import com.mochat.mochat.dao.mapper.wm.WorkMsgMapper;
import com.mochat.mochat.model.wm.ChatRsaKeyModel;
import com.mochat.mochat.model.wm.CorpMsgTO;
import com.mochat.mochat.model.wm.DataResultModel;
import com.tencent.wework.Finance;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: yangpengwei
 * @time: 2020/11/13 3:38 下午
 * @description 企业微信会话内存存档工具类
 */
@Component
public class WorkMsgBackUpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkMsgBackUpUtil.class);
    private static final String MSG_ACTION_SWITCH = "switch";
    private static final int LIMIT = 100;
    private static final long TIMEOUT = 10 * 60 * 1000;

    private static WorkMsgMapper workMsgMapper;

    private static WorkMsgIndexMapper workMsgIndexMapper;

    @Autowired
    public void setWorkMsgMapper(WorkMsgMapper workMsgMapper) {
        WorkMsgBackUpUtil.workMsgMapper = workMsgMapper;
    }

    @Autowired
    public void setWorkMsgIndexMapper(WorkMsgIndexMapper workMsgIndexMapper) {
        WorkMsgBackUpUtil.workMsgIndexMapper = workMsgIndexMapper;
    }

    /**
     * 从企业微信拉取数据保存到数据库, 一次拉取 100 条
     *
     * @param entity 企业会话内容存档配置信息
     * @param seq    最后一条数据的 seq
     * @return 是否拉取完成所有数据
     */
    public static boolean insertMsg(CorpMsgTO entity, int seq) {
        long sdk = Finance.NewSdk();
        int result = Finance.Init(sdk, entity.getWxCorpId(), entity.getChatSecret());
        if (result == 0) {
            LOGGER.debug("init SDK 成功 >>>>> " + entity.toString());
        } else {
            LOGGER.debug("init SDK 失败 >>>>> <<<<< " + entity.toString());
            return false;
        }

        ArrayList<WorkMsgEntity> msgEntities = new ArrayList<>();
        ArrayList<WorkMsgIndexEntity> msgIndexEntities = new ArrayList<>();
        Map<String, WorkMsgIndexEntity> msgIndexEntityMap = new ConcurrentHashMap<>();

        long slice = Finance.NewSlice();
        long ret = Finance.GetChatData(sdk, seq, LIMIT, "", "", TIMEOUT, slice);
        if (ret != 0) {
            LOGGER.debug("拉取数据失败，错误码：" + ret);
        } else {
            LOGGER.debug("拉取数据成功 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            String contentJson = Finance.GetContentFromSlice(slice);
            Finance.FreeSlice(slice);
            LOGGER.debug(contentJson);
            LOGGER.debug("拉取数据成功 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

            // 解析原始数据
            DataResultModel dataResultModel = JSON.parseObject(contentJson, DataResultModel.class);
            int code = dataResultModel.getErrcode();
            if (0 == code) {
                List<DataResultModel.ChatdataDTO> chatdataDTOList = dataResultModel.getChatdata();
                if (null != chatdataDTOList && chatdataDTOList.size() > 0) {
                    // 获取私钥
                    String keyJson = entity.getChatRsaKey();

                    ChatRsaKeyModel rsaKeyModel = JSON.parseObject(keyJson, ChatRsaKeyModel.class);
                    String privateKey = rsaKeyModel.getPrivateKey();
                    int version = rsaKeyModel.getVersion();

                    int corpId = entity.getCorpId();
                    WorkMsgEntity workMsgEntity;
                    WorkMsgIndexEntity workMsgIndexEntity;
                    for (int i = 0; i < chatdataDTOList.size(); i++) {
                        DataResultModel.ChatdataDTO chatdataDTO = chatdataDTOList.get(i);
                        if (chatdataDTO.getPublickey_ver() != version) {
                            continue;
                        }
                        String decryptDataJson = decryptData(sdk, chatdataDTO, privateKey);
                        JSONObject jsonObject = JSON.parseObject(decryptDataJson);
                        String action = getStringValue(jsonObject, "action");
                        if (action.isEmpty() || MSG_ACTION_SWITCH.equals(action)) {
                            continue;
                        }
                        workMsgEntity = transChatModelToWorkMsg(sdk, jsonObject, corpId, chatdataDTO.getSeq());
                        msgEntities.add(workMsgEntity);

                        // 正向
                        workMsgIndexEntity = transWorkMsgToIndex(workMsgEntity);
                        msgIndexEntities.add(workMsgIndexEntity);
                        if (!msgIndexEntityMap.containsKey(workMsgIndexEntity.getFlag())) {
                            msgIndexEntityMap.put(workMsgIndexEntity.getFlag(), workMsgIndexEntity);
                        }

                        // 反向
                        workMsgIndexEntity = reverseWorkMsgIndex(workMsgIndexEntity);
                        msgIndexEntities.add(workMsgIndexEntity);
                        if (!msgIndexEntityMap.containsKey(workMsgIndexEntity.getFlag())) {
                            msgIndexEntityMap.put(workMsgIndexEntity.getFlag(), workMsgIndexEntity);
                        }
                    }
                } else {
                    return false;
                }
            } else {
                LOGGER.debug("会话内容存档数据拉取失败 >>>>>>>>>>>>>>>>>>>>>>>>>>>");
                LOGGER.debug(dataResultModel.getErrmsg());
                LOGGER.debug("会话内容存档数据拉取失败 >>>>>>>>>>>>>>>>>>>>>>>>>>>");
            }
            LOGGER.debug("会话内容存档数据存储 >>>>>>>>>>>>>>>>>>>>>>>>>>>");
            LOGGER.debug("" + msgEntities.size());
            LOGGER.debug(msgEntities.toString());
            LOGGER.debug("会话内容存档数据存储 >>>>>>>>>>>>>>>>>>>>>>>>>>>");
            if (msgEntities.size() > 0) {
                workMsgMapper.insertByMap(WorkMsgHelper.getTableName(entity.getCorpId()), msgEntities);
            }
            if (msgIndexEntityMap.size() > 0) {
                Set<String> flags = msgIndexEntityMap.keySet();
                List<WorkMsgIndexEntity> existFlags = workMsgIndexMapper.selectList(
                        new QueryWrapper<WorkMsgIndexEntity>()
                                .select("flag")
                                .in("flag", flags)
                );
                for (WorkMsgIndexEntity existIndex : existFlags) {
                    msgIndexEntityMap.remove(existIndex.getFlag());
                }

                List<WorkMsgIndexEntity> list = new ArrayList<>(msgIndexEntityMap.values());
                workMsgIndexMapper.insertMsgIndex(list);
            }
        }

        // 释放 sdk
        Finance.DestroySdk(sdk);

        return msgEntities.size() == LIMIT;
    }

    /**
     * 数据解密
     *
     * @param chatdataDTO 未解密数据
     * @param privateKey  私钥
     * @return 解密后字符串
     */
    private static String decryptData(long sdk, DataResultModel.ChatdataDTO chatdataDTO, String privateKey) {
        String encryptRandomKey = chatdataDTO.getEncrypt_random_key();
        String encryptChatMsg = chatdataDTO.getEncrypt_chat_msg();
        String decryptRandomKey = RSAUtils.decryptByPriKey(encryptRandomKey, privateKey);

        long slice = Finance.NewSlice();
        long ret = Finance.DecryptData(sdk, decryptRandomKey, encryptChatMsg, slice);
        if (ret != 0) {
            LOGGER.debug("会话内容存档数据拉取失败 >>>>>>>>>>>>>>>>>>>>>>>>>>>");
            LOGGER.debug("数据解密错误码：" + ret);
            LOGGER.debug("会话内容存档数据拉取失败 >>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        String decryptChatMsg = Finance.GetContentFromSlice(slice);
        LOGGER.warn("数据解密内容 :" + decryptChatMsg);
        Finance.FreeSlice(slice);

        return decryptChatMsg;
    }

    /**
     * 对获取 jsonObject 进行转换以便于数据库存储
     *
     * @param jsonObject jsonObject 数据对象
     * @param corpId     企业 id
     * @param seq        seq
     */
    private static WorkMsgEntity transChatModelToWorkMsg(long sdk, JSONObject jsonObject, int corpId, int seq) {
        String msgType = getStringValue(jsonObject, "msgtype", "text");
        int msgTypeCode = MsgTypeEnum.valueOf(msgType.toUpperCase()).getCode();

        String actionStr = getStringValue(jsonObject, "action");
        int action;
        if ("switch".equals(actionStr)) {
            action = 2;
        } else if ("recall".equals(actionStr)) {
            action = 1;
        } else {
            action = 0;
        }

        String content;
        JSONObject tempJsonObj;
        if ("external_redpacket".equals(msgType)) {
            content = getStringValue(jsonObject, "redpacket");
        } else if ("voip_doc_share".equals(msgType)) {
            tempJsonObj = jsonObject.getJSONObject("voip_doc_share");
            tempJsonObj.put("voipid", getStringValue(jsonObject, "voipid"));
            content = tempJsonObj.toString();
        } else if ("meeting_voice_call".equals(msgType)) {
            tempJsonObj = jsonObject.getJSONObject("meeting_voice_call");
            tempJsonObj.put("voiceid", getStringValue(jsonObject, "voiceid"));
            content = tempJsonObj.toString();
        } else if ("news".equals(msgType) || "markdown".equals(msgType)) {
            content = getStringValue(jsonObject, "info");
        } else if ("docmsg".equals(msgType)) {
            content = getStringValue(jsonObject, "doc");
        } else {
            content = getStringValue(jsonObject, msgType);
        }

        content = onContentMachine(sdk, msgType, content);

        JSONArray toList = jsonObject.getJSONArray("tolist");
        int toListSize = toList.size();
        int toType = 0;
        if (toListSize == 1) {
            String toWxUserId = toList.getString(0);
            if (isContact(toWxUserId)) {
                toType = 1;
            } else {
                // 内部联系人
                toType = 0;
            }
        } else {
            // 群聊
            toType = 2;
        }

        WorkMsgEntity workMsgEntity = new WorkMsgEntity();
        workMsgEntity.setCorpId(corpId);
        workMsgEntity.setSeq(seq);
        workMsgEntity.setMsgId(getStringValue(jsonObject, "msgid"));
        workMsgEntity.setAction(action);
        workMsgEntity.setFrom(getStringValue(jsonObject, "from"));
        workMsgEntity.setTolist(getStringValue(jsonObject, "tolist"));
        workMsgEntity.setTolistType(toType);
        workMsgEntity.setMsgType(msgTypeCode);
        workMsgEntity.setContent(content);
        workMsgEntity.setMsgTime(getStringValue(jsonObject, "msgtime"));
        workMsgEntity.setWxRoomId(getStringValue(jsonObject, "roomid"));

        LOGGER.debug("workMsg", workMsgEntity.toString());
        return workMsgEntity;
    }

    /**
     * 根据 WorkMsgEntity 创建 WorkMsgIndexEntity 索引对象
     *
     * @param workMsgEntity
     * @return WorkMsgIndexEntity 索引对象
     */
    private static WorkMsgIndexEntity transWorkMsgToIndex(WorkMsgEntity workMsgEntity) {
        int corpId = workMsgEntity.getCorpId();
        int toType = workMsgEntity.getTolistType();

        int fromId;
        String fromWxUserId = workMsgEntity.getFrom();
        Integer fId;
        if (isContact(fromWxUserId)) {
            fId = workMsgIndexMapper.selectContactId(corpId, fromWxUserId);
        } else {
            fId = workMsgIndexMapper.selectEmployeeId(corpId, fromWxUserId);
        }
        fromId = null == fId ? 0 : fId;

        int toId = 0;
        JSONArray toList = JSON.parseArray(workMsgEntity.getTolist());
        int toListSize = toList.size();
        if (toListSize == 1) {
            String toWxUserId = toList.getString(0);
            Integer tId;
            if (isContact(toWxUserId)) {
                // 外部联系人
                tId = workMsgIndexMapper.selectContactId(corpId, toWxUserId);
            } else {
                // 内部联系人
                tId = workMsgIndexMapper.selectEmployeeId(corpId, toWxUserId);
            }
            toId = null == tId ? 0 : tId;
        } else if (toListSize > 1) {
            // 群聊
            String toWxUserId = toList.getString(0);
            Integer rId = workMsgIndexMapper.selectRoomId(corpId, toWxUserId);
            toId = null == rId ? 0 : rId;
        }

        WorkMsgIndexEntity indexEntity = new WorkMsgIndexEntity();
        indexEntity.setCorpId(corpId);
        indexEntity.setToId(toId);
        indexEntity.setToType(toType);
        indexEntity.setFromId(fromId);
        indexEntity.setFlag(fromId + "-" + toType + "-" + toId);
        return indexEntity;
    }

    /**
     * 根据 WorkMsgEntity 创建 WorkMsgIndexEntity 索引对象
     *
     * @param workMsgEntity
     * @return WorkMsgIndexEntity 索引对象
     */
    private static WorkMsgIndexEntity reverseWorkMsgIndex(WorkMsgIndexEntity workMsgEntity) {
        int corpId = workMsgEntity.getCorpId();
        int fromId = workMsgEntity.getToId();
        int toId = workMsgEntity.getFromId();
        int toType = workMsgEntity.getToType();
        String flag = fromId + "-" + toType + "-" + toId;

        WorkMsgIndexEntity entity = new WorkMsgIndexEntity();
        entity.setCorpId(corpId);
        entity.setFromId(fromId);
        entity.setToId(toId);
        entity.setToType(toType);
        entity.setFlag(flag);
        return entity;
    }

    public static boolean isContact(String wxUserID) {
        if (StringUtils.hasLength(wxUserID) && wxUserID.length() > 2) {
            String flag = wxUserID.substring(0,2);
            return flag.contains("wo") || wxUserID.contains("wm");
        } else {
            return false;
        }
    }

    /**
     * 对内容里包含 item 的字符串进行适配处理
     *
     * @param msgType 消息类型
     * @param content 消息体
     * @return 适配后的消息体
     */
    private static String onContentMachine(long sdk, String msgType, String content) {
        JSONObject jsonObject = JSON.parseObject(content);
        LOGGER.error(">>>>>><<<<<<: " + jsonObject);
        if ("emotion".equalsIgnoreCase(msgType)) {
            jsonObject.put("emotionType", jsonObject.getIntValue("type"));
            LOGGER.error(">>>>>><<<<<< emotion: " + jsonObject);
        }
        jsonObject.put("type", msgType);
        onSaveFileOfContent(sdk, msgType, jsonObject);
        if (jsonObject.containsKey("item")) {
            JSONArray jsonArray = jsonObject.getJSONArray("item");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                msgType = jsonObject1.getString("type").replace("ChatRecord", "");
                Map<String, Object> map = jsonObject1.getJSONObject("content").getInnerMap();
                map.put("type", msgType);
                jsonObject1.remove("content");
                jsonObject1.fluentPutAll(map);
                onSaveFileOfContent(sdk, msgType, jsonObject1);
            }
        }
        return jsonObject.toJSONString();
    }

    /**
     * 保存文件并上传到阿里云
     *
     * @param msgType
     * @param jsonObject
     */
    private static void onSaveFileOfContent(long sdk, String msgType, JSONObject jsonObject) {
        if (jsonObject.containsKey("sdkfileid")) {
            String sdkFileId = jsonObject.getString("sdkfileid");
            String fileSuffix;
            if ("image".equalsIgnoreCase(msgType)) {
                fileSuffix = ".jpg";
            } else if ("voice".equalsIgnoreCase(msgType)) {
                fileSuffix = ".amr";
            } else if ("video".equalsIgnoreCase(msgType)) {
                fileSuffix = ".mp4";
            } else if ("file".equalsIgnoreCase(msgType)) {
                fileSuffix = "." + jsonObject.getString("fileext");
            } else if ("emotion".equalsIgnoreCase(msgType)) {
                LOGGER.error("emotion: " + jsonObject.toJSONString());
                int type = jsonObject.getIntValue("emotionType");
                fileSuffix = 1 == type ? ".gif" : ".png";
            } else {
                fileSuffix = ".txt";
            }

            try {
                String filePathOss = onSaveFileAndUpload(sdk, sdkFileId, fileSuffix);
                jsonObject.put("ossPath", filePathOss);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存文件并上传到阿里云
     *
     * @param sdkFileId
     * @param fileSuffix 文件类型
     */
    private static String onSaveFileAndUpload(long sdk, String sdkFileId, String fileSuffix) throws IOException {
        // 拼接文件名
        String fileName = DateUtils.formatS7(System.currentTimeMillis())
                + "/"
                + new Random().nextInt(100)
                + "/"
                + DigestUtils.md5DigestAsHex(sdkFileId.getBytes())
                + fileSuffix;

        // 将文件保存至本地
        File file = new File(Const.TEMP_FILE_DIR, fileName);
        onWriteToFile(sdk, file, sdkFileId, "");

        // amr 格式音频转换
        if (".amr".equals(fileSuffix)) {
            String amrPath = file.getAbsolutePath();
            String mp3Path = amrPath.replace(fileSuffix, ".mp3");
            CommandUtil.amrToMp3(amrPath, mp3Path);
            File mp3File = new File(mp3Path);
            if (mp3File.exists()) {
                file = mp3File;
                fileName = fileName.replace(fileSuffix, ".mp3");
            } else {
                LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
                LOGGER.debug(" amr 转 mp3 转换失败");
                LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
            }
        }

        // 文件上传至阿里云
        AliyunOssUtils.upLoad(file, fileName);
        return fileName;
    }

    /**
     * 将微信文件保存到本地, 微信文件数据采取的是分片拉取
     *
     * @param file
     * @param sdkFileId
     * @param indexBuf  偏移量
     */
    private static void onWriteToFile(long sdk, File file, String sdkFileId, String indexBuf) {
        long mediaData = Finance.NewMediaData();
        int ret = Finance.GetMediaData(sdk, indexBuf, sdkFileId, "", "", TIMEOUT, mediaData);
        if (ret != 0) {
            LOGGER.warn("获取媒体数据 " + ret);
        } else {
            LOGGER.debug("upload media " + sdkFileId);
            byte[] b = Finance.GetData(mediaData);
            boolean isFinish = Finance.IsMediaDataFinish(mediaData) > 0;
            String outIndex = Finance.GetOutIndexBuf(mediaData);
            try {
                FileUtils.writeByteArrayToFile(file, b, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Finance.FreeMediaData(mediaData);
            if (isFinish) {
                LOGGER.debug("upload media finish " + sdkFileId);
            } else {
                onWriteToFile(sdk, file, sdkFileId, outIndex);
            }
        }
    }

    private static String getStringValue(JSONObject jsonObject, String key) {
        return getStringValue(jsonObject, key, "");
    }

    private static String getStringValue(JSONObject jsonObject, String key, String defaultValue) {
        String value = jsonObject.getString(key);
        return null == value ? defaultValue : value;
    }

}
