package com.mochat.mochat.common.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2020/11/26 6:21 下午
 * @description 接口响应通用 Page
 */
@Data
public class RespPageVO {

    private PageVO page;
    private List list;

    @Data
    public static class PageVO {
        private Long perPage;
        private Long total;
        private Long totalPage;
    }

    private static PageVO transFromPage(Page page) {
        PageVO pageVO = new PageVO();
        pageVO.setPerPage(page.getSize());
        pageVO.setTotal(page.getTotal());
        pageVO.setTotalPage(page.getPages());
        return pageVO;
    }

    public static RespPageVO getInstance(Page page) {
        RespPageVO respPageVO = new RespPageVO();
        PageVO pageVO = transFromPage(page);
        List list = page.getRecords();
        respPageVO.setPage(pageVO);
        respPageVO.setList(list);
        return respPageVO;
    }

    public static RespPageVO getInstance(Page page, List list) {
        RespPageVO respPageVO = new RespPageVO();
        PageVO pageVO = transFromPage(page);
        respPageVO.setPage(pageVO);
        respPageVO.setList(list);
        return respPageVO;
    }

}
