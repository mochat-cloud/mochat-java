package com.mochat.mochat.controller.workroom;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.WorkContactRoomEntity;
import com.mochat.mochat.dao.entity.workroom.WorkRoomGroupEntity;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.model.workroom.WorkRoomIndexModel;
import com.mochat.mochat.model.workroom.WorkRoomStatisticsIndexReq;
import com.mochat.mochat.model.workroom.WorkRoomStatisticsIndexResp;
import com.mochat.mochat.model.workroom.WorkRoomStatisticsResp;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.IWorkContactRoomService;
import com.mochat.mochat.service.workroom.IWorkRoomGroupService;
import com.mochat.mochat.service.workroom.IWorkRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:客户群
 * @author: Huayu
 * @time: 2020/12/8 14:26
 */
@RestController
@Validated
@RequestMapping("/workRoom")
public class WorkRoomController {

    @Autowired
    private IWorkRoomService workRoomServiceImpl;

    @Autowired
    private IWorkContactRoomService workContactRoomServiceImpl;

    @Autowired
    private IWorkRoomGroupService workRoomGroupServiceImpl;


    /**
     * @description:客户群管理-列表
     * @return:
     * @author: Huayu
     * @time: 2020/12/11 15:24
     */
    @GetMapping("/index")
    public ApiRespVO workRoomIndex(WorkRoomIndexModel workRoomIndexModel, @RequestAttribute ReqPerEnum permission) {
        return ApiRespUtils.getApiRespByPage(workRoomServiceImpl.getWorkRoomList(workRoomIndexModel, permission));
    }

    /**
     * @description:客户群管理-同步群
     * @return:
     * @author: Huayu
     * @time: 2020/12/11 15:28
     */
    @PutMapping("/syn")
    public ApiRespVO workRoomSync(@RequestBody String corpId, HttpServletRequest request) {
        if (corpId == null || corpId.equals("")) {
            throw new CommonException(100013, "未选择登录企业，不可操作");
        }
        Integer corpIds = AccountService.getCorpId();
        //同步企业客户群聊
        boolean flag = false;
        try {
            flag = workRoomServiceImpl.syncWorkRoomIndex(corpIds, null, 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (flag) {
            return ApiRespUtils.getApiRespOfOk("");
        }
        throw new CommonException(100013, "该客户群数据错误");
    }

    /**
     * @description:统计分页数据
     * @return:
     * @author: Huayu
     * @time: 2020/12/22 8:59
     */
    @GetMapping("statisticsIndex")
    public ApiRespVO statisticsIndex(@Validated WorkRoomStatisticsIndexReq workRoomStatisticsIndex) throws ParseException {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        //参数校验-当type = 1(按天统计)时间必传
        if (workRoomStatisticsIndex.getType().equals(1)) {
            if (workRoomStatisticsIndex.getStartTime().length() == 0
                    || workRoomStatisticsIndex.getEndTime().length() == 0) {
                throw new CommonException(100013, "按天统计开始和结束时间必传");
            }
        }
        //检索该群聊所有成员信息
        List<WorkContactRoomEntity> workContactRoomEntityList = workContactRoomServiceImpl.getWorkContactRoomsByRoomId(workRoomStatisticsIndex.getWorkRoomId());
        if (workContactRoomEntityList.size() == 0) {
            throw new CommonException(100013, "该客户群数据错误");
        }
        //根据统计类型组织列表结构
        Map<String, Object> map = formData(workRoomStatisticsIndex, new WorkRoomStatisticsIndexResp());
        List<WorkRoomStatisticsIndexResp> workRoomStatisticsIndexRespList = new ArrayList<WorkRoomStatisticsIndexResp>();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
        DateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
        Integer addNum = 0;
        Integer outNum = 0;
        Integer total = 0;
        Integer outTotal = 0;
        //归纳数据
        for (String key : map.keySet()) {
            WorkRoomStatisticsIndexResp workRoomStatisticsIndexResp = new WorkRoomStatisticsIndexResp();
            //归纳数据
            for (int i = 0; i < workContactRoomEntityList.size(); i++) {
                WorkContactRoomEntity workContactRoomEntity = workContactRoomEntityList.get(i);
                String inKey = null;
                String outKey = null;
                String tsJoinStr = sdf1.format(workContactRoomEntity.getJoinTime());
                String tsOutStr = "";
                if (workContactRoomEntity.getOutTime() != null && workContactRoomEntity.getOutTime().length() != 0) {
                    tsOutStr = sdf1.format(sdf.parse(workContactRoomEntity.getOutTime()));
                }
                if (workRoomStatisticsIndex.getType().equals(1) || workRoomStatisticsIndex.getType().equals(2)) {   //按天统计||按自然周统计
                    inKey = tsJoinStr;
                    if (!tsOutStr.equals("")) {
                        outKey = workContactRoomEntity.getOutTime().length() == 0 ? "outTime" : tsOutStr;
                    }
                } else {//按自然年统计
                    inKey = sdfMonth.format(sdfMonth.parse(tsJoinStr));
                    if (!tsOutStr.equals("")) {
                        outKey = workContactRoomEntity.getOutTime().length() == 0 ? "outTime" : sdfMonth.format(tsOutStr);
                    }
                }
                //入群
                if (key.equals(inKey)) {
                    addNum++;
                }
                //退群
                if (outKey != null && key.equals(outKey)) {
                    outNum++;
                }
                Date keyDate = sdf1.parse(key);
                date = workContactRoomEntity.getJoinTime();
                //Date inKeyDate = sdf1.parse(inKey);
                //Date outKeyDate = sdf1.parse(outKey);
                if (date.getTime() <= keyDate.getTime()) {
                    total++;
                }
                if (workContactRoomEntity.getOutTime().length() != 0) {
                    Date outDate = sdf1.parse(workContactRoomEntity.getOutTime());
                    if (workContactRoomEntity.getStatus().equals(2) && outDate.getTime() <= keyDate.getTime()) {
                        outTotal++;
                    }
                }
            }
            workRoomStatisticsIndexResp.setTime(key);
            workRoomStatisticsIndexResp.setTotal(total);
            workRoomStatisticsIndexResp.setOutTotal(outTotal);
            workRoomStatisticsIndexResp.setAddNum(addNum.toString());
            workRoomStatisticsIndexResp.setOutNum(outNum.toString());
            workRoomStatisticsIndexRespList.add(workRoomStatisticsIndexResp);
            total = 0;
            outTotal = 0;
        }
        Page<WorkRoomStatisticsIndexResp> page = new Page<>();
        RequestPage requestPage = new RequestPage();
        requestPage.setPage(1);
        requestPage.setPerPage(10);
        ApiRespUtils.initPage(page, requestPage);
        //根据时间排序
        List<WorkRoomStatisticsIndexResp> newSortList = workRoomStatisticsIndexRespList.stream().sorted(Comparator.comparing(WorkRoomStatisticsIndexResp::getTime))
                .collect(Collectors.toList());
        page.setRecords(newSortList);
        page.setTotal(newSortList.size());
        return ApiRespUtils.getApiRespByPage(page);
    }


    /**
     * @description:组装分页列表数据
     * @return:
     * @author: Huayu
     * @time: 2020/12/12 16:30
     */
    private Map<String, Object> formData(WorkRoomStatisticsIndexReq workRoomStatisticsIndex, Object obj) throws ParseException {
        if (obj instanceof WorkRoomStatisticsIndexResp) {
            obj = new WorkRoomStatisticsIndexResp();
        }
        if (obj instanceof WorkRoomStatisticsResp) {
            obj = new WorkRoomStatisticsResp();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar st = Calendar.getInstance();
        Calendar ed = Calendar.getInstance();
        Map<String, Object> map = new HashMap<String, Object>();
        if (workRoomStatisticsIndex.getType().equals(1)) {//按天统计
            String start = workRoomStatisticsIndex.getStartTime();
            String end = workRoomStatisticsIndex.getEndTime();
            st.setTime(sdf.parse(start));
            if (end.length() > 0) {
                ed.setTime(sdf.parse(end));
            }
            while (!st.after(ed)) {
                //WorkRoomStatisticsIndexResp workRoomStatisticsIndexResp = new WorkRoomStatisticsIndexResp();
                map.put(sdf.format(st.getTime()), obj);
                st.add(Calendar.DAY_OF_YEAR, 1);
            }

        } else if (workRoomStatisticsIndex.getType().equals(2)) {
            st.setTime(new Date());//按自然周统计
            for (int i = 1; i <= 7; i++) {
                // WorkRoomStatisticsIndexResp workRoomStatisticsIndexResp = new WorkRoomStatisticsIndexResp();
                map.put(sdf.format(st.getTime()), obj);
                st.add(Calendar.DAY_OF_YEAR, -1);
            }

        } else if (workRoomStatisticsIndex.getType().equals(3)) {        //按自然年统计
            for (int i = 1; i <= 12; i++) {
                // WorkRoomStatisticsIndexResp workRoomStatisticsIndexResp = new WorkRoomStatisticsIndexResp();
                map.put(sdf.format(st.getTime()), obj);
                st.add(Calendar.MONTH, -1);
            }
        }
        return map;
    }


    /**
     * @description:统计折线图
     * @return:
     * @author: Huayu
     * @time: 2020/12/14 14:30
     */
    @GetMapping("/statistics")
    public ApiRespVO statistics(@Validated WorkRoomStatisticsIndexReq workRoomStatisticsIndexReq) throws ParseException {
        //参数校验-当type = 1(按天统计)时间必传
        if (workRoomStatisticsIndexReq.getType().equals(1)) {
            if (workRoomStatisticsIndexReq.getStartTime().equals("")
                    || workRoomStatisticsIndexReq.getEndTime().equals("")) {
                throw new CommonException(100013, "按天统计开始和结束时间必传");
            }
        }
        //检索该群聊所有成员信息
        String clStr = "id,status,join_time,out_time";
        List<WorkContactRoomEntity> workContactRoomEntityList = workContactRoomServiceImpl.getWorkContactRoomsByRoomId(workRoomStatisticsIndexReq.getWorkRoomId());
        if (workContactRoomEntityList.size() == 0) {
            throw new CommonException(100013, "该客户群数据错误");

        }
        //根据统计类型组织列表结构
        Map<String, Object> map = formData(workRoomStatisticsIndexReq, new WorkRoomStatisticsIndexResp());
        //归纳数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat sdfYear = new SimpleDateFormat("yyyy-MM");
        Date date = new Date();
        Integer addNum = 0;    // 今日新增成员数
        Integer listAddNum = 0;// 列表内-新增成员数
        Integer outNum = 0;    // 今日退群成员数
        Integer listOutNum = 0;// 列表内-新增退群成员数
        Integer outTotal = 0;  // 累计退群成员数
        Integer addNumRange = 0; // 当前时间段新增成员数
        Integer outNumRange = 0;// 当前时间段退群成员数
        WorkRoomStatisticsResp workRoomStatisticsResp = new WorkRoomStatisticsResp();
        List<WorkRoomStatisticsIndexResp> workRoomStatisticsIndexRespList = new ArrayList<WorkRoomStatisticsIndexResp>();
        for (String key : map.keySet()) {
            WorkRoomStatisticsIndexResp workRoomStatisticsIndexResp = new WorkRoomStatisticsIndexResp();
            for (int i = 0; i < workContactRoomEntityList.size(); i++) {
                addNum = 0;
                listAddNum = 0;
                outNum = 0;
                listOutNum = 0;
                outTotal = 0;
                WorkContactRoomEntity workContactRoomEntity = workContactRoomEntityList.get(i);
                Integer i1 = workContactRoomEntity.getType();
                String inKey = null;
                String outKey = null;
                String tsJoinStr = sdf.format(workContactRoomEntity.getJoinTime());
                String tsOutStr = workContactRoomEntity.getOutTime();
                if (workRoomStatisticsIndexReq.getType().equals(1) || workRoomStatisticsIndexReq.getType().equals(2)) {   //按天统计||按自然周统计
                    inKey = tsJoinStr;
                    if (!tsOutStr.equals("")) {
                        outKey = workContactRoomEntity.getOutTime() == null ? "outTime" : sdfYear.format(sdfYear.parse(tsOutStr));
                    }
                } else {//按自然年统计
                    inKey = sdfYear.format(sdfYear.parse(tsJoinStr));
                    if (!tsOutStr.equals("")) {
                        outKey = workContactRoomEntity.getOutTime() == null ? "outTime" : sdfYear.format(sdfYear.parse(tsOutStr));
                    }
                }
                //今日入群
                if (workContactRoomEntity.getJoinTime() == date) {
                    addNum++;
                }
                //列表内统计-入群
                if (key.equals(inKey)) {
                    listAddNum++;
                    addNumRange++;
                }
                if (workContactRoomEntity.getStatus().equals(2)) {
                    //今日退群
                    if ((workContactRoomEntity.getOutTime() != null) && (date == sdf.parse(workContactRoomEntity.getOutTime()))) {
                        outNum++;
                    }
                    outTotal++;
                    //列表内统计-退群
                    if (key.equals(outKey)) {
                        listOutNum++;
                        outNumRange++;
                    }
                }
            }
            //组装返回数据
            workRoomStatisticsIndexResp.setTime(key);
            workRoomStatisticsIndexResp.setAddNum(String.valueOf(listAddNum));
            workRoomStatisticsIndexResp.setOutNum(String.valueOf(listOutNum));
            workRoomStatisticsIndexRespList.add(workRoomStatisticsIndexResp);
            workRoomStatisticsResp.setAddNum(addNum);
            workRoomStatisticsResp.setOutNum(outNum);
            workRoomStatisticsResp.setTotal(workContactRoomEntityList.size());
            workRoomStatisticsResp.setOutTotal(outTotal);
            workRoomStatisticsResp.setAddNumRange(addNumRange);
            workRoomStatisticsResp.setOutNumRange(outNumRange);
        }
        //根据时间排序
        List<WorkRoomStatisticsIndexResp> newSortList = workRoomStatisticsIndexRespList.stream().sorted(Comparator.comparing(WorkRoomStatisticsIndexResp::getTime))
                .collect(Collectors.toList());
        workRoomStatisticsResp.setList(newSortList);
        return ApiRespUtils.getApiRespOfOk(workRoomStatisticsResp);
    }


    @PutMapping("/batchUpdate")
    public ApiRespVO batchUpdate(@RequestBody JSONObject req) {
        int workRoomGroupId = req.getIntValue("workRoomGroupId");
        String workRoomIds = req.getString("workRoomIds");
        if (!StringUtils.hasLength(workRoomIds)) {
            throw new ParamException("workRoomIds 不能为空");
        }

        int corpId = AccountService.getCorpId();

        //验证客户群分组的有效性
        if (workRoomGroupId != 0) {
            WorkRoomGroupEntity workRoomGroupEntity = workRoomGroupServiceImpl.getWorkRoomGroupById(workRoomGroupId);
            if (workRoomGroupEntity == null) {
                throw new CommonException(100013, "该分组信息不存在，不可操作");
            }
            if (!workRoomGroupEntity.getCorpId().equals(corpId)) {
                throw new CommonException(100013, "该分组不归属当前登录企业，不可操作");
            }
        }

        String[] roomIdArr = workRoomIds.split(",");
        //数据入表
        for (String roomId : roomIdArr) {
            workRoomServiceImpl.updateWorkRoomsByRoomGroupId(Integer.valueOf(roomId), workRoomGroupId);
        }
        return ApiRespUtils.getApiRespOfOk();
    }

    /**
     * @description 客户 - 群聊列表下拉框
     * @author zhaojinjian
     * @createTime 2020/12/30 16:19
     */
    @GetMapping("/roomIndex")
    public ApiRespVO getRoomIndex(String name, Integer roomGroupId) {
        Integer corpId = AccountService.getCorpId();
        if (corpId == null) {
            throw new CommonException(100013, "未选择登录企业，不可操作");
        }
        return ApiRespUtils.getApiRespOfOk(workRoomServiceImpl.getWorkRoomSelectData(corpId, name, roomGroupId));
    }
}
