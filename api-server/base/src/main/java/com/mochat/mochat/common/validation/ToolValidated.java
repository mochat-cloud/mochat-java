package com.mochat.mochat.common.validation;

import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.api.ApiRespVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/11/27 16:56
 */
public class ToolValidated {
    private static final Logger LOG = LoggerFactory.getLogger(ToolValidated.class);

    // 实际使用建议将编码信息放置在一个单独的文件中统一管理
    /**
     * 操作成功
     */
    public static String SUCCESS = "200";


    // =================== Spring validated (建议使用) ===================
    /**
     * 功能:验证参数信息是否有效
     *
     * @return
     */
    public static ApiRespVO myValidate(BindingResult e, HttpServletResponse resp) {
        ApiRespVO messageBean = new ApiRespVO();
        if(e.hasErrors()) {
            // 设置验证结果状态码
            messageBean.setCode(RespErrCodeEnum.INVALID_PARAMS.getCode());
            // 获取错误字段信息集合
            List<FieldError> fieldErrorList = e.getFieldErrors();

            // 使用TreeSet是为了让输出的内容有序输出(默认验证的顺序是随机的)
            Set<String> errorInfoSet = new TreeSet<String>();
            for (FieldError fieldError : fieldErrorList) {
                // 遍历错误字段信息
                errorInfoSet.add(fieldError.getDefaultMessage());
                LOG.debug("[{}.{}]{}", fieldError.getObjectName() , fieldError.getField(), fieldError.getDefaultMessage());
            }

            StringBuffer sbf = new StringBuffer();
            for (String errorInfo : errorInfoSet) {
                sbf.append(errorInfo);
                sbf.append(",");
            }
            messageBean.setMsg(sbf.substring(0, sbf.length() - 1));
            messageBean.setData("");
        }
        resp.setStatus(RespErrCodeEnum.INVALID_PARAMS.getStatus());
        return messageBean;

    }

    // =================== 自定义验证方法 ===================

    /**
     * 功能:验证参数是否完整,不为NULL或空</h5>
     *
     * @param obj
     * @return
     */
    public static boolean validateParams(Object... obj) {
        boolean flag = true;
        for (int i = 0; i < obj.length; i++) {
            if (null == obj[i] || "".equals(obj[i])) {
                LOG.debug("参数信息[{}],第{}个参数不能为NULL或空", JSONObject.toJSONString(obj), i+1);
                flag = false;
                break;
            }
        }

        return flag;
    }

    /**
     * 功能:验证参数是否完整,不为NULL或空</h5>
     *
     * @param obj
     * @return
     */
    public static ApiRespVO validateParamsExt(Object... obj) {
        ApiRespVO messageBean = new ApiRespVO();
        for (int i = 0; i < obj.length; i++) {
            if (null == obj[i] || "".equals(obj[i])) {
                LOG.debug("参数信息[{}],第{}个参数不能为NULL或空", JSONObject.toJSONString(obj), i+1);
                messageBean.setCode(RespErrCodeEnum.INVALID_PARAMS.getCode());
                messageBean.setMsg("参数信息不完整");
                break;
            }
        }
        return messageBean;
    }

    /**
     * 功能:验证参数是否完整,不为NULL或空</h5>
     *
     * @param obj
     * @return
     */
    public static boolean isAllParamsNull(Object... obj) {
        boolean ret  = true;
        for (int i = 0; i < obj.length; i++) {
            if (null == obj[i] || "".equals(obj[i])) {
            }else {
                ret = false;
                break;
            }
        }
        return ret;
    }

    public static void main(String[] args) {
        validateParamsExt(1,"2","",null);
    }

}
