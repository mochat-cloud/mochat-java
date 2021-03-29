package com.mochat.mochat.model.channel;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespChannelCodeVO {

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
         * groupName
         */
        private String groupName;
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
        private List<TagsDTO> tags;
        /**
         * selectedTags
         */
        private List<Integer> selectedTags;

        /**
         * TagsDTO
         */
        @NoArgsConstructor
        @Data
        public static class TagsDTO {
            /**
             * groupId
             */
            private Integer groupId;
            /**
             * groupName
             */
            private String groupName;
            /**
             * list
             */
            private List<ListDTO> list;

            /**
             * ListDTO
             */
            @NoArgsConstructor
            @Data
            public static class ListDTO {
                /**
                 * tagId
                 */
                private Integer tagId;
                /**
                 * tagName
                 */
                private String tagName;
                /**
                 * isSelected
                 */
                private Integer isSelected;
            }
        }
    }

}
