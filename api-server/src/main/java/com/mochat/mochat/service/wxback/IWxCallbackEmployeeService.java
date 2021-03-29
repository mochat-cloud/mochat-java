package com.mochat.mochat.service.wxback;

public interface IWxCallbackEmployeeService {

    String CHANGE_TYPE_DEPARTMENT_CREATE = "create_party";
    String CHANGE_TYPE_DEPARTMENT_UPDATE = "update_party";
    String CHANGE_TYPE_DEPARTMENT_DELETE = "delete_party";

    String CHANGE_TYPE_EMPLOYEE_CREATE = "create_user";
    String CHANGE_TYPE_EMPLOYEE_UPDATE = "update_user";
    String CHANGE_TYPE_EMPLOYEE_DELETE = "delete_user";
    //region 客户回调类型
    String ADD_EXTERNAL_CONTACT = "add_external_contact";
    String EDIT_EXTERNAL_CONTACT = "edit_external_contact";
    String DEL_EXTERNAL_CONTACT = "del_external_contact";
    String DEL_FOLLOW_USER = "del_follow_user";
    //endregion

    String dispatchEvent(String dataJson);

}
