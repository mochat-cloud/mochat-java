package com.mochat.mochat.model.subsystem;

import lombok.Data;

/**
 * @description: 登录用户信息详情
 * @author: zhaojinjian
 * @create: 2020-11-23 16:18
 **/
@Data
public class LoginShowRresponse {
    private Integer userId;
    private String userPhone;
    private String userName;
    private int userGender;
    private String userDepartment;
    private String userPosition;
    private String userLoginTime;
    private int userStatus;
    private Integer employeeId;
    private String employeeName;
    private String employeeMobile;
    private String employeePosition;
    private int employeeGender;
    private String employeeEmail;
    private String employeeAvatar;
    private String employeeThumbAvatar;
    private String employeeTelephone;
    private String employeeAlias;
    private int employeeStatus;
    private String employeeQrCode;
    private String employeeExternalPosition;
    private String employeeAddress;
    private int corpId;
    private String corpName;

}
