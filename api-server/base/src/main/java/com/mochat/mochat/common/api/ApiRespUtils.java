package com.mochat.mochat.common.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochat.mochat.common.ex.BaseException;
import com.mochat.mochat.common.ex.IHttpCode;

import java.util.List;

/**
 * ApiRespVO 辅助生成工具类
 *
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/12/21
 *
 * @see ApiRespVO
 * @see RespPageDataVO
 * @see BaseException
 * @see Page
 */
public class ApiRespUtils {

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/12/21
     * @description 生成 默认成功 Api
     */
    public static ApiRespVO ok() {
        return ApiRespVO.builder().code(200).msg("成功").build();
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/12/21
     * @description 根据 data 生成 Api
     */
    public static ApiRespVO ok(Object data) {
        return ApiRespVO.builder().code(200).msg("成功").data(data).build();
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/12/21
     * @description 根据 ex 生成 Api
     */
    public static ApiRespVO ex(IHttpCode e) {
        return ApiRespVO.builder().code(e.getCode()).msg(e.getMsg()).build();
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/12/21
     * @description 根据 vo page 生成 Api
     */
    public static ApiRespVO okPage(Page<?> page) {
        RespPageDataVO respPageDataVO = RespPageDataVO.getInstance(page);
        return ApiRespVO.builder().code(200).msg("成功").data(respPageDataVO).build();
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/12/21
     * @description 初始化用于查询的 page
     */
    public static <T> Page<T> initPage(ReqPageDto req) {
        return new Page<>(req.getPage(), req.getPerPage());
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/12/21
     * @description 将查询过的 page 转成 vo 的 page, 用于 api 返回
     */
    public static <T, M> Page<M> transPage(Page<T> page, List<M> list) {
        Page<M> pageVo = new Page<>(page.getCurrent(), page.getSize());
        pageVo.setRecords(list);
        pageVo.setTotal(page.getTotal());
        pageVo.setPages(page.getPages());
        return pageVo;
    }

}
