package com.mochat.mochat.model.workroom;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class WorkRoomAutoPullDetailVO {

    /**
     * workRoomAutoPullId
     */
    @JSONField(name = "workRoomAutoPullId")
    private Integer workRoomAutoPullId;
    /**
     * qrcodeName
     */
    @JSONField(name = "qrcodeName")
    private String qrcodeName;
    /**
     * qrcodeUrl
     */
    @JSONField(name = "qrcodeUrl")
    private String qrcodeUrl;
    /**
     * isVerified
     */
    @JSONField(name = "isVerified")
    private Integer isVerified;
    /**
     * roomNum
     */
    @JSONField(name = "roomNum")
    private Integer roomNum;
    /**
     * leadingWords
     */
    @JSONField(name = "leadingWords")
    private String leadingWords;
    /**
     * createdAt
     */
    @JSONField(name = "createdAt")
    private String createdAt;
    /**
     * employees
     */
    @JSONField(name = "employees")
    private List<EmployeesDTO> employees;
    /**
     * tags
     */
    @JSONField(name = "tags")
    private List<TagsDTO> tags;
    /**
     * selectedTags
     */
    @JSONField(name = "selectedTags")
    private List<Integer> selectedTags;
    /**
     * rooms
     */
    @JSONField(name = "rooms")
    private List<RoomsDTO> rooms;

    /**
     * EmployeesDTO
     */
    @NoArgsConstructor
    @Data
    public static class EmployeesDTO {
        /**
         * employeeId
         */ /**
         * employeeId : 1
         * employeeName :
         */

        @JSONField(name = "employeeId")
        private Integer employeeId;
        /**
         * employeeName
         */
        @JSONField(name = "employeeName")
        private String employeeName;
    }

    /**
     * TagsDTO
     */
    @NoArgsConstructor
    @Data
    public static class TagsDTO {
        /**
         * groupId
         */ /**
         * groupId : 1
         * groupName :
         * list : [{"tagId":1,"tagName":"","isSelected":1}]
         */

        @JSONField(name = "groupId")
        private Integer groupId;
        /**
         * groupName
         */
        @JSONField(name = "groupName")
        private String groupName;
        /**
         * list
         */
        @JSONField(name = "list")
        private List<ListDTO> list;

        /**
         * ListDTO
         */
        @NoArgsConstructor
        @Data
        public static class ListDTO {
            /**
             * tagId
             */ /**
             * tagId : 1
             * tagName :
             * isSelected : 1
             */

            @JSONField(name = "tagId")
            private Integer tagId;
            /**
             * tagName
             */
            @JSONField(name = "tagName")
            private String tagName;
            /**
             * isSelected
             */
            @JSONField(name = "isSelected")
            private Integer isSelected;
        }
    }

    /**
     * RoomsDTO
     */
    @NoArgsConstructor
    @Data
    public static class RoomsDTO {
        /**
         * roomId
         */ /**
         * roomId : 1
         * roomName : 北京地区一群
         * roomMax : 1
         * maxNum : 1
         * num : 1
         * state : 1
         * roomQrcodeUrl : /www/xx.jpg
         * longRoomQrcodeUrl : 1
         */

        @JSONField(name = "roomId")
        private Integer roomId;
        /**
         * roomName
         */
        @JSONField(name = "roomName")
        private String roomName;
        /**
         * roomMax
         */
        @JSONField(name = "roomMax")
        private Integer roomMax;
        /**
         * maxNum
         */
        @JSONField(name = "maxNum")
        private Integer maxNum;
        /**
         * num
         */
        @JSONField(name = "num")
        private Integer num;
        /**
         * state
         */
        @JSONField(name = "state")
        private Integer state;
        /**
         * roomQrcodeUrl
         */
        @JSONField(name = "roomQrcodeUrl")
        private String roomQrcodeUrl;
        /**
         * longRoomQrcodeUrl
         */
        @JSONField(name = "longRoomQrcodeUrl")
        private String longRoomQrcodeUrl;
    }
}
