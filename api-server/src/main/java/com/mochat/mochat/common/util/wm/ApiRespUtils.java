package com.mochat.mochat.common.util.wm;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.model.RespPageVO;
import com.mochat.mochat.config.ex.BaseException;
import com.mochat.mochat.model.ApiRespVO;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2020/12/2 1:14 下午
 * @description ApiResp 工具类辅助创建 ApiRespVO
 */
public class ApiRespUtils {

    public static ApiRespVO ok() {
        return new ApiRespVO(200, "成功", "");
    }

    public static ApiRespVO ok(Object obj) {
        return new ApiRespVO(200, "成功", obj);
    }

    public static ApiRespVO ex(BaseException e) {
        ApiRespVO apiResp = new ApiRespVO();
        apiResp.setCode(e.getCode());
        apiResp.setMsg(e.getMsg());
        apiResp.setData("");
        return apiResp;
    }

    public static ApiRespVO getApiRespByPage(Page page) {
        RespPageVO respPageVO = RespPageVO.getInstance(page);
        return new ApiRespVO(200,"成功", respPageVO);
    }

    public static void initPage(Page page, RequestPage requestPage) {
        page.setCurrent(requestPage.getPage());
        page.setSize(requestPage.getPerPage());
    }

    public static <T> Page<T> initPage(RequestPage req) {
        return new Page<T>(req.getPage(), req.getPerPage());
    }

    public static <T, M> Page<M> transPage(Page<T> page, List<M> list) {
        Page<M> pageVo = new Page<>(page.getCurrent(), page.getSize());
        pageVo.setRecords(list);
        pageVo.setTotal(page.getTotal());
        pageVo.setPages(page.getPages());
        return pageVo;
    }

    public static <T, M> Page<M> transPage(Page<T> page) {
        Page<M> pageVo = new Page<>(page.getCurrent(), page.getSize());
        return pageVo;
    }

}
