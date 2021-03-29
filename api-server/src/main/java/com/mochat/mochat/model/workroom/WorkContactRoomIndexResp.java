package com.mochat.mochat.model.workroom;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    private PageVO page;
    private List list;

    @Data
    public  class PageVO {
        private long perPage;
        private long total;
        private long totalPage;
    }

    private  PageVO transFromPage(Page page) {
        PageVO pageVO = new PageVO();
        pageVO.setPerPage(page.getSize());
        pageVO.setTotal(page.getTotal());
        pageVO.setTotalPage(page.getPages());
        return pageVO;
    }

    public  WorkContactRoomIndexResp getInstance(Page page,WorkContactRoomIndexResp workContactRoomIndexResp) {
        PageVO pageVO = transFromPage(page);
        List list = page.getRecords();
        workContactRoomIndexResp.setPage(pageVO);
        workContactRoomIndexResp.setList(list);
        return workContactRoomIndexResp;
    }

}
