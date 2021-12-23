package com.mochat.mochat.common.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * response 默认分页 VO
 *
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/12/21
 * @see ApiRespUtils
 * @see ApiRespVO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespPageVO {
    private long perPage = 1;
    private long total = 0;
    private long totalPage = 0;

    public static RespPageVO get(Page<?> page) {
        RespPageVO pageVO = new RespPageVO();
        pageVO.setPerPage(page.getSize());
        pageVO.setTotal(page.getTotal());
        pageVO.setTotalPage(page.getPages());
        return pageVO;
    }
}
