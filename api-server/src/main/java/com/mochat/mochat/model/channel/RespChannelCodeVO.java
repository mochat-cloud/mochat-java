package com.mochat.mochat.model.channel;

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
    private BaseInfoVO baseInfo;

    /**
     * drainageEmployee
     */
    private DrainageEmployeeVO drainageEmployee;

    /**
     * welcomeMessage
     */
    private WelcomeMessageVO welcomeMessage;

    @NoArgsConstructor
    @Data
    public static class BaseInfoVO {

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
