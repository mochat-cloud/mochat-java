package com.mochat.mochat.model.contactfield;

import java.util.List;

/**
 * @author zhaojinjian
 * @ClassName BatchUpdateContactFieldModel.java
 * @Description 批量修改高级属性
 * @createTime 2020/12/16 11:48
 */
public class BatchUpdateContactFieldModel {
    private List<UpdateContactFieldModel> update;
    private List<Integer> destroy;

    public List<UpdateContactFieldModel> getUpdate() {
        return update;
    }

    public void setUpdate(List<UpdateContactFieldModel> update) {
        this.update = update;
    }

    public List<Integer> getDestroy() {
        return destroy;
    }

    public void setDestroy(List<Integer> destroy) {
        this.destroy = destroy;
    }
}
