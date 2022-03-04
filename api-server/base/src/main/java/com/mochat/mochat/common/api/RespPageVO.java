package com.mochat.mochat.common.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: yangpengwei
 * @time: 2020/11/26 6:21 下午
 * @description 接口响应通用 Page
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
