/**
 * This file is part of MoChat.
 * @link     https://mo.chat
 * @document https://mochat.wiki
 * @contact  group@mo.chat
 * @license  https://github.com/mochat-cloud/mochat-java/blob/master/LICENSE
 */

package com.mochat.mochat;

import com.mochat.mochat.common.annotion.LoginToken;
import com.mochat.mochat.model.ServiceSuccessCode;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@Validated
public class ServiceController extends RuntimeException{

    @RequestMapping("/health")
    @LoginToken
    @ResponseBody
    public ServiceSuccessCode writeByResp(HttpServletResponse resp) {

        ServiceSuccessCode user = new ServiceSuccessCode();
        user.setStatus("success");
        return user;
    }

    @RequestMapping(value="health1")
    @LoginToken
    @ResponseBody
    public  String   writeByResp1(@RequestParam(value="id") Integer user1t) {
        return null;
    }

}
