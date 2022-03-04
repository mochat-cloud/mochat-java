package com.mochat.mochat.model.channel;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class WelcomeMessageVO {

    /**
     * scanCodePush
     */
    private Integer scanCodePush;
    /**
     * messageDetail
     */
    private List<JSONObject> messageDetail;

}
