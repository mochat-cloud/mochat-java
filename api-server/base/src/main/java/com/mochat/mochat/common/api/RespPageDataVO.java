package com.mochat.mochat.common.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * response 默认分页 VO
 *
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/12/21
 *
 * @see ApiRespUtils
 * @see ApiRespVO
 */
@Data
public class RespPageDataVO {

    private RespPageVO page;
    private List<?> list;

    public static RespPageDataVO getInstance(Page<?> page) {
        RespPageDataVO respPageDataVO = new RespPageDataVO();
        RespPageVO pageVO = RespPageVO.get(page);
        List<?> list = page.getRecords();
        respPageDataVO.setPage(pageVO);
        respPageDataVO.setList(list);
        return respPageDataVO;
    }

}
