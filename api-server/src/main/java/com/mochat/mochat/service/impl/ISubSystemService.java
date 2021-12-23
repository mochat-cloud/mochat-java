package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.dao.entity.UserEntity;
import com.mochat.mochat.model.subsystem.*;

import java.util.List;

/**
 * @description: 子账户管理接口类
 * @author: zhaojinjian
 * @create: 2020-11-17 17:38
 **/
public interface ISubSystemService extends IService<UserEntity> {

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/17 3:18 下午
     * @description 获取子账户列表
     */
    GetSubSystemPage getSubSystemPageList(APIGetSubSystemRequest parem, ReqPerEnum permission);

    boolean saveSubsystem(AddSubSystemRequest parem);

    /**
     * @Description: 更新员工账户登录密码
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/23
     */
    boolean updatePassword(PasswordUpdateRequest parem, int userId);

    boolean update(UpdateSubSystemRequest parem);

    LoginShowRresponse getLoginShowInfo(int userId, int empId);

    //boolean setStatus(int [] serIdsu,int status);
    boolean setStatus(String[] userIds, int status);

    GetSubSystemInfoResponse getSubSystemInfo(int userId);

    List<UserEntity> getUserByPhone(String phoneNumber, String password);

    UserEntity login(String phone, String password);
}
