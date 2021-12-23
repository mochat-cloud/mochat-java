package com.mochat.mochat.config.ex;

import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.ex.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * @description: 校验类
 * @author: Huayu
 * @time: 2020/11/27 15:26
 */
@Slf4j
@RestControllerAdvice()
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exceptionHandler(Exception e) {
        e.printStackTrace();
        ApiRespVO apiResp = new ApiRespVO();
        apiResp.setCode(RespErrCodeEnum.SERVER_ERROR.getCode());
        apiResp.setMsg("服务器异常");
        return ResponseEntity.status(RespErrCodeEnum.SERVER_ERROR.getStatus()).body(apiResp);
    }

    @ExceptionHandler(BaseException.class)
    private ResponseEntity<Object> exceptionHandler(BaseException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiRespUtils.ex(e));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> bindExceptionHandler(BindException e) {
        ApiRespVO vo = ApiRespUtils.ex(RespErrCodeEnum.INVALID_PARAMS);

        if(e.hasErrors()) {
            List<FieldError> fieldErrorList = e.getFieldErrors();
            StringBuilder sb = new StringBuilder();
            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage()).append(",");
            }
            vo.setMsg(sb.substring(0, sb.length() - 1));
        }

        return ResponseEntity.status(RespErrCodeEnum.INVALID_PARAMS.getStatus()).body(vo);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        ApiRespVO vo = ApiRespUtils.ex(RespErrCodeEnum.INVALID_PARAMS);

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<?> item : violations) {
                sb.append(item.getMessage()).append(",");
            }
            vo.setMsg(sb.substring(0, sb.length() - 1));
        }

        return ResponseEntity.status(RespErrCodeEnum.INVALID_PARAMS.getStatus()).body(vo);
    }

}
