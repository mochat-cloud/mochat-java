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
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.impl.ISubSystemService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;

@RestController
@Validated
public class ServiceController extends RuntimeException{
    //private final static Logger logger = LoggerFactory.getLogger(HellowController.class);

//    @Autowired
//    private WorkEmployeeMapper workEmployeeMapper;


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
        //List<WorkEmployeeEntity> id = workEmployeeServiceImpl.getWorkEmployeeByUserId("1");
        //System.out.println(id.get(0).toString());
        return null;
    }


}
