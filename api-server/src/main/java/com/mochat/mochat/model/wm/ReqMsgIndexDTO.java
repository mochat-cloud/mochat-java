package com.mochat.mochat.model.wm;

import com.mochat.mochat.common.api.ReqPageDto;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.config.ex.ParamException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/1 10:50 上午
 * @description index (聊天信息) 接口的请求参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReqMsgIndexDTO extends ReqPageDto {

    @NotNull(message = "员工 id 不能为 null")
    private Integer workEmployeeId;

    @NotNull(message = "聊天对象类型不能为 null")
    @Range(min = 0, max = 2, message = "聊天对象类型无效")
    private Integer toUserType;

    @NotNull(message = "聊天对象的 Id 不能为 null")
    private Integer toUserId;

    /**
     * 可选参数
     */
    @NotNull(message = "聊天内容类型无效")
    private Integer type;
    private String content;
    private String dateTimeStart;
    private String dateTimeEnd;

    public Integer getType() {
        type = null == type ? 0 : type;
        boolean b = type < 0 || (type > 7 && type < 100) || type > 100;
        if (b) {
            throw new ParamException(400001, "聊天内容类型无效");
        }
        return type;
    }

    public String getContent() {
        content = null == content ? "" : content;
        return content;
    }

    public String getDateTimeStart() {
        if (null != dateTimeStart && !dateTimeStart.isEmpty()) {
            return "" + DateUtils.getMillsByS1(dateTimeStart);
        }
        return null;
    }

    public String getDateTimeEnd() {
        if (null != dateTimeEnd && !dateTimeEnd.isEmpty()) {
            return "" + DateUtils.getMillsByS1(dateTimeEnd);
        }
        return null;
    }

}



