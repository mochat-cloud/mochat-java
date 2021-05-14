package com.mochat.mochat.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochat.mochat.dao.entity.WorkContactEntity;
import com.mochat.mochat.model.workcontact.ContactData;
import com.mochat.mochat.model.workcontact.GetContactRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContactMapper extends BaseMapper<WorkContactEntity> {
    List<ContactData>  getEmployeeContact(@Param("parem") GetContactRequest parem, @Param("empIds") String [] empIds);
}
