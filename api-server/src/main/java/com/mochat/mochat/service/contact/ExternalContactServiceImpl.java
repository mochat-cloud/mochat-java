package com.mochat.mochat.service.contact;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.impl.IContactService;
import com.mochat.mochat.service.impl.ICorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhaojinjian
 * @ClassName 外部客户
 * @Description TODO
 * @createTime 2020/12/7 18:13
 */
@Service
@EnableAsync
public class ExternalContactServiceImpl implements IExternalContactService {
    @Autowired
    @Lazy
    private IContactService workContactService;
    @Autowired
    @Lazy
    private IWorkEmployeeService workEmployeeService;
    @Autowired
    private ICorpService corpServiceImpl;
    private String charset = "utf-8";

    /**
     * @description 修改客户备注信息
     * @author zhaojinjian
     * @createTime 2020/12/7 18:25
     */
    @Override
    @Async
    public void updateRemark(Integer empId, Integer corpId, Integer contactId, String remark, String description) {
        String wxContactId = workContactService.getWxExternalUserId(contactId);
        String wxEmpId = workEmployeeService.getById(empId).getWxUserId();
        Map<String, Object> mark_tag_parem = new HashMap<>();
        mark_tag_parem.put("userid", wxEmpId);
        mark_tag_parem.put("external_userid", wxContactId);
        if (remark != null && remark.length() > 0) {
            mark_tag_parem.put("remark", remark);
        }
        if (description != null && description.length() > 0) {
            mark_tag_parem.put("description", description);
        }
        WxApiUtils.updateExternalContact(corpId, mark_tag_parem);
    }

    /**
     * @description
     * @author zhaojinjian
     * @createTime 2020/12/23 15:51
     */
    @Async
    @Override
    public void uploadContactAvatar(Map<String, String> filePathMap) {
        AliyunOssUtils.multipleUpLoad(filePathMap);
    }

    /**
     * @description 根据企业获取成员下所有客户列表
     * @author zhaojinjian
     * @createTime 2020/12/8 11:31
     */
    @Override
    public JSONArray getExternalUserId(String userId, Integer corpId) {
        String respJsonStr = WxApiUtils.getExternalContactList(corpId, userId);
        JSONObject obj = JSON.parseObject(respJsonStr);
        if (!obj.isEmpty() && obj.get("errcode").equals(0)) {
            return obj.getJSONArray("external_userid");
        }
        return null;
    }

    /**
     * @description 根据微信成员Id获取成员的客户id
     * @author zhaojinjian
     * @createTime 2020/12/18 14:45
     */
    @Override
    public JSONArray getAllExternalUserId(List<String> userIds, Integer corpId) {
        JSONArray list = new JSONArray();
        if (userIds != null && userIds.size() > 0) {
            ExecutorService executorService = Executors.newFixedThreadPool(userIds.size());
            try {
                // 计数器大小定义为集合大小，避免处理不一致导致主线程无限等待
                CountDownLatch countDownLatch = new CountDownLatch(userIds.size());
                // 循环处理Array
                userIds.parallelStream().forEach(userId -> {
                    // 任务提交线程池
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            String external_userid = userId.toString();
                            // 处理用户数据
                            JSONArray jsonResult = getExternalUserId(userId, corpId);
                            if (jsonResult != null && jsonResult.size() > 0) {
                                list.addAll(jsonResult);
                            }
                        } finally {
                            countDownLatch.countDown();
                        }
                        return 1;
                    }, executorService);
                });
                // 主线程等待所有子线程都执行完成时，恢复执行主线程
                countDownLatch.await();
                // 关闭线程池
                executorService.shutdown();
            } catch (Exception e) {
                System.out.println("异常日志");
            } finally {
                return list;
            }
        }
        return list;
    }

    /**
     * @description 获取客户详细信息
     * @author zhaojinjian
     * @createTime 2020/12/16 18:27
     */
    @Override
    public JSONObject getExternalContact(String externalUserId, Integer corpId) {
        String result = WxApiUtils.getExternalContactInfo(corpId, externalUserId);
        if (result.isEmpty()) {
            return new JSONObject();
        }
        return JSON.parseObject(result);
    }

    /**
     * @return Map<String, JSONObject> 格式map 外部联系人的userid(external_userid)
     * @description 获取多个客户详细信息（微信）
     * @author zhaojinjian
     * @createTime 2020/12/16 18:31
     */
    @Override
    public Map<String, JSONObject> getExternalContactMap(List<Object> array, Integer corpId) {
        Map<String, JSONObject> list = new HashMap<>();
        if (array.isEmpty()) {
            return list;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(array.size());
        try {
            // 计数器大小定义为集合大小，避免处理不一致导致主线程无限等待
            CountDownLatch countDownLatch = new CountDownLatch(array.size());
            // 循环处理Array
            array.parallelStream().forEach(userId -> {
                // 任务提交线程池
                CompletableFuture.supplyAsync(() -> {
                    try {
                        String external_userid = userId.toString();
                        // 处理用户数据
                        JSONObject jsonResult = getExternalContact(external_userid, corpId);
                        if (!jsonResult.isEmpty()) {
                            list.put(external_userid, jsonResult);
                        }
                    } finally {
                        countDownLatch.countDown();
                    }
                    return 1;
                }, executorService);
            });
            // 主线程等待所有子线程都执行完成时，恢复执行主线程
            countDownLatch.await();
            // 关闭线程池
            executorService.shutdown();
        } catch (Exception e) {
            System.out.println("异常日志");
        } finally {
            return list;
        }
    }

}
