package com.mochat.mochat.model.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class WelcomeMessageDTO {

    /**
     * scanCodePush
     */
    private Integer scanCodePush;
    /**
     * messageDetail
     */
    private List<MessageDetailDTO> messageDetail;

    /**
     * MessageDetailDTO
     */
    @NoArgsConstructor
    @Data
    public static class MessageDetailDTO {
        /**
         * type 欢迎语类型 （1.通用，2.周期，3.特殊时期）
         */
        private Integer type;

        /**
         * content 媒体内容
         */
        private ContentDTO content;

        /**
         * mediumId 素材库ID
         */
        private String mediumId;

        /**
         * welcomeContent 欢迎语内容
         */
        private String welcomeContent;
        /**
         * status
         */
        private Integer status;
        /**
         * detail
         */
        private List<DetailDTO> detail;

        /**
         * DetailDTO
         */
        @NoArgsConstructor
        @Data
        public static class DetailDTO {
            /**
             * key
             */
            private String key;
            /**
             * endDate
             */
            private String endDate;
            /**
             * startDate
             */
            private String startDate;
            /**
             * timeSlot
             */
            private List<TimeSlotDTO> timeSlot;
            /**
             * dataString
             */
            private List<String> dataString;

            /**
             * chooseCycle
             */
            private List<Integer> chooseCycle;

            /**
             * TimeSlotDTO
             */
            @NoArgsConstructor
            @Data
            public static class TimeSlotDTO {
                /**
                 * content
                 */
                private ContentDTO content;
                /**
                 * endTime
                 */
                private String endTime;
                /**
                 * mediumId
                 */
                private String mediumId;
                /**
                 * startTime
                 */
                private String startTime;
                /**
                 * welcomeContent
                 */
                private String welcomeContent;
            }
        }
    }
}
