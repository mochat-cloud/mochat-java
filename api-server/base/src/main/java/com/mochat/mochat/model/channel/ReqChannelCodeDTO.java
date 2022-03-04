package com.mochat.mochat.model.channel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqChannelCodeDTO {

    /**
     * channelCodeId
     */
    private Integer channelCodeId;
    /**
     * baseInfo
     */
    private BaseInfoDTO baseInfo;
    /**
     * drainageEmployee
     */
    private DrainageEmployeeDTO drainageEmployee;
    /**
     * welcomeMessage
     */
    private WelcomeMessageDTO welcomeMessage;

    /**
     * BaseInfoDTO
     */
    @NoArgsConstructor
    @Data
    public static class BaseInfoDTO {
        /**
         * groupId
         */
        private Integer groupId;
        /**
         * name
         */
        private String name;
        /**
         * autoAddFriend
         */
        private Integer autoAddFriend;
        /**
         * tags
         */
        private List<Integer> tags;
    }

}
