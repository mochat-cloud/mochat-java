package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.ContactFieldPivotEntity;
import com.mochat.mochat.model.contactfieldpivot.ContactFieldPivotVO;
import com.mochat.mochat.model.contactfieldpivot.UpdateContactFieldPivotModel;

import java.util.List;

/**
 * @author zhaojinjian
 * @ClassName IWorkContactFieldPivotService.java
 * @Description TODO
 * @createTime 2020/12/24 16:47
 */
public interface IWorkContactFieldPivotService extends IService<ContactFieldPivotEntity> {

    List<ContactFieldPivotVO> getContactFieldPivotList(Integer contactId);

    boolean updateContactFieldPivot(UpdateContactFieldPivotModel param);
}
