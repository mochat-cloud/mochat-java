package com.mochat.mochat.model.workroom;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class WorkRoomAutoPullVO {

    /**
     * workRoomAutoPullId
     */
    @JSONField(name = "workRoomAutoPullId")
    private Integer workRoomAutoPullId;
    /**
     * qrcodeUrl
     */
    @JSONField(name = "qrcodeUrl")
    private String qrcodeUrl;
    /**
     * qrcodeName
     */
    @JSONField(name = "qrcodeName")
    private String qrcodeName;
    /**
     * contactNum
     */
    @JSONField(name = "contactNum")
    private Integer contactNum;
    /**
     * createdAt
     */
    @JSONField(name = "createdAt")
    private String createdAt;
    /**
     * employees
     */
    @JSONField(name = "employees")
    private List<String> employees;
    /**
     * tags
     */
    @JSONField(name = "tags")
    private List<String> tags;
    /**
     * rooms
     */
    @JSONField(name = "rooms")
    private List<RoomsDTO> rooms;

    /**
     * RoomsDTO
     */
    @NoArgsConstructor
    @Data
    public static class RoomsDTO {
        /**
         * roomName
         */ /**
         * roomName : 北京地区一群
         * stateText : 1
         */

        @JSONField(name = "roomName")
        private String roomName;
        /**
         * stateText
         */
        @JSONField(name = "stateText")
        private String stateText;
    }
}
