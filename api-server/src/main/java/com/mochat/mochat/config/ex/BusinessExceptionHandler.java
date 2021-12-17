package com.mochat.mochat.config.ex;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.model.ApiRespVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Order(199)
public class BusinessExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessExceptionHandler.class);

    /**
     * 通用异常的处理，返回500
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> onEx(Exception e) {
        e.printStackTrace();
        ApiRespVO apiResp = new ApiRespVO();
        apiResp.setCode(RespErrCodeEnum.SERVER_ERROR.getCode());
        apiResp.setMsg("服务器异常");
        LOGGER.error(" >>>>>> 业务异常: ",e);
        return ResponseEntity.status(RespErrCodeEnum.SERVER_ERROR.getStatus()).body(apiResp);
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(ParamException.class)
    @ResponseBody
    private ResponseEntity<Object> onParamEx(ParamException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiRespUtils.ex(e));
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(CommonException.class)
    @ResponseBody
    private ResponseEntity<Object> onCommonEx(CommonException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiRespUtils.ex(e));
    }

    /**
     * token校验异常
     */
    @ExceptionHandler(AuthException.class)
    @ResponseBody
    private ResponseEntity<Object> onAuthEx(AuthException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiRespUtils.ex(e));
    }

}
