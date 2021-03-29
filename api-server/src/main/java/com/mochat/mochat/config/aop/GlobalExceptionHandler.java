package com.mochat.mochat.config.aop;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.validation.ToolValidated;
import com.mochat.mochat.model.ApiRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.TreeSet;

/**
 * @description:校验类
 * @author: Huayu
 * @time: 2020/11/27 15:26
 */
@Slf4j
@ControllerAdvice()
@Order(198)
public class GlobalExceptionHandler {

    /**
     * 功能:处理普通参数校验失败的异常
     *
     * @param ex ConstraintViolationException
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<Object> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        ApiRespVO messageBean = new ApiRespVO();
        // 设置验证结果状态码
        messageBean.setCode(RespErrCodeEnum.INVALID_PARAMS.getCode());

        // 使用TreeSet是为了让输出的内容有序输出(默认验证的顺序是随机的)
        Set<String> errorInfoSet = new TreeSet<String>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        if (!violations.isEmpty()) {
            for (ConstraintViolation<?> item : violations) {
                log.error(" >>>>>>> 参数错误: " + item.getPropertyPath());
                // 遍历错误字段信息
                errorInfoSet.add(item.getMessage());
            }

            StringBuffer sbf = new StringBuffer();
            for (String errorInfo : errorInfoSet) {
                sbf.append(errorInfo);
                sbf.append(",");
            }
            messageBean.setMsg(sbf.substring(0, sbf.length() - 1));
        }

        return ResponseEntity.status(RespErrCodeEnum.INVALID_PARAMS.getStatus()).body(messageBean);
    }

    /**
     * 功能: 处理实体类参数校验失败的异常
     * @return
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ApiRespVO BindExceptionHandler(BindException bindingResult,HttpServletResponse resp) {
        // 验证参数信息是否有效
        ApiRespVO messageBean = ToolValidated.myValidate(bindingResult,resp);
        return messageBean;
    }

}
