package com.mochat.mochat.controller;

import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.annotion.LoginToken;
import com.mochat.mochat.common.util.JwtUtils;
import com.mochat.mochat.common.util.RedisUtil;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.dao.entity.UserEntity;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.service.impl.ISubSystemService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @description:登录操作
 * @author: Huayu
 * @time: 2020/11/20 10:38
 */
@RestController
@RequestMapping("/user")
public class LoginController {

    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private ISubSystemService subSystemServiceImpl;

    @Autowired
    private IWorkEmployeeService workEmployeeServiceImpl;

    /**
     * @description:登录
     * @return:
     * @author: Huayu
     * @time: 2020/11/22 13:02
     */
    @PostMapping(value="/auth")
    @LoginToken
     public ApiRespVO login(@RequestBody(required=false) Map<String,Object> data, HttpServletRequest request)  {
        String phone = (String) data.get("phone");
        String password = (String) data.get("password");
        System.out.println("phone==========="+phone);
        System.out.println("password================"+password);
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntityList  = subSystemServiceImpl.login(phone,password);
        if (userEntityList == null) {
            throw new CommonException(100013,"登录失败,用户不存在");
        }
        String token = JwtUtils.createToken(36000000, userEntityList);
        RedisUtil.set("mc:user.token"+token, "1");
        logger.info("登录成功，token=>>>>>>>>>>>>>>"+token);
        jsonObject.put("token",token);
        jsonObject.put("expire",36000000);
        return ApiRespUtils.ok(jsonObject);
    }

    /**
     *
     *
     * @description: 登出
     * @return:
     * @author: Huayu
     * @time: 2020/11/22 13:04
     */
    @PutMapping("logout")
    public ApiRespVO loginOut(@RequestHeader String Authorization){
        logger.info("删除用户的token"+Authorization);
        String userId = JwtUtils.parseToken(Authorization).get("userId").toString();
        Authorization = Authorization.substring(Authorization.indexOf(" ") + 1);
        RedisUtil.del("mc:user.token"+Authorization);
        logger.info("用户"+"id为"+userId+">>>>>>>>>>>>>>>>>>>已登出");
        return  ApiRespUtils.ok("");
    }
}

