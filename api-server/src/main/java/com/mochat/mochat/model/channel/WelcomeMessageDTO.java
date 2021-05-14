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
         * welcomeContent
         */
        private String welcomeContent;

        /**
         * mediumId
         */
        private String mediumId;

        /**
         * status
         */
        private Integer status;

        /**
         * detail
         */
        private List<DetailDTO> detail;

        public int getMediumId() {
            try {
                return Integer.parseInt(mediumId);
            } catch (Exception e) {
                return 0;
            }
        }

        /**
         * DetailDTO
         */
        @NoArgsConstructor
        @Data
        public static class DetailDTO {

            private String startDate;

            private String endDate;

            /**
             * chooseCycle
             */
            private List<Integer> chooseCycle;

            /**
             * timeSlot
             */
            private List<TimeSlotDTO> timeSlot;

            /**
             * TimeSlotDTO
             */
            @NoArgsConstructor
            @Data
            public static class TimeSlotDTO {

                /**
                 * welcomeContent
                 */
                private String welcomeContent;

                /**
                 * mediumId
                 */
                private String mediumId;

                /**
                 * startTime
                 */
                private String startTime;

                /**
                 * endTime
                 */
                private String endTime;

                public int getMediumId() {
                    try {
                        return Integer.parseInt(mediumId);
                    } catch (Exception e) {
                        return 0;
                    }
                }
            }
        }
    }
}
