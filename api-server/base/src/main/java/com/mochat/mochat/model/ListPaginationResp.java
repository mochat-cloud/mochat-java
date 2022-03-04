package com.mochat.mochat.model;

import com.mochat.mochat.common.api.RespPageVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description:分页对象
 * @author: Huayu
 * @time: 2020/11/23 16:31
 */
@Data
public class ListPaginationResp implements Serializable {
    private RespPageVO pageModel;
    private List list;

    public ListPaginationResp(RespPageVO pageModel, List list) {
        this.pageModel = pageModel;
        this.list = list;
    }

}
