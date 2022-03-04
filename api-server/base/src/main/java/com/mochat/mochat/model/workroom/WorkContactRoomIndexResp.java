package com.mochat.mochat.model.workroom;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochat.mochat.common.api.RespPageVO;
import lombok.Data;

import java.util.List;

/**
 * @description:客户群成员管理-列表
 * @author: Huayu
 * @time: 2020/12/16 9:55
 */
@Data
public class WorkContactRoomIndexResp {
    private Integer memberNum;
    private Integer outRoomNum;
    private RespPageVO page;
    private List<?> list;

    public  WorkContactRoomIndexResp getInstance(Page<?> page,WorkContactRoomIndexResp workContactRoomIndexResp) {
        RespPageVO pageVO = RespPageVO.get(page);
        List<?> list = page.getRecords();
        workContactRoomIndexResp.setPage(pageVO);
        workContactRoomIndexResp.setList(list);
        return workContactRoomIndexResp;
    }

}
