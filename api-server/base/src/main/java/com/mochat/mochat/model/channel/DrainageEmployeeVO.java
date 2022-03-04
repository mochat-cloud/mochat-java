package com.mochat.mochat.model.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DrainageEmployeeVO {

    /**
     * type
     */
    private Integer type;
    /**
     * addMax
     */
    private AddMaxVO addMax;
    /**
     * employees
     */
    private List<EmployeesVO> employees;
    /**
     * specialPeriod
     */
    private SpecialPeriodVO specialPeriod;

    /**
     * AddMaxDTO
     */
    @NoArgsConstructor
    @Data
    public static class AddMaxVO {
        /**
         * status
         */
        private Integer status;
        /**
         * employees
         */
        private List<EmployeesDTO> employees;
        /**
         * spareEmployeeIds
         */
        private List<Integer> spareEmployeeIds;
        /**
         * spareEmployeeName
         */
        private List<String> spareEmployeeName;

        /**
         * EmployeesDTO
         */
        @NoArgsConstructor
        @Data
        public static class EmployeesDTO {
            /**
             * max
             */
            private String max;
            /**
             * employeeId
             */
            private Integer employeeId;
            /**
             * employeeName
             */
            private String employeeName;
        }
    }

    /**
     * SpecialPeriodDTO
     */
    @NoArgsConstructor
    @Data
    public static class SpecialPeriodVO {
        /**
         * detail
         */
        private List<DetailDTO> detail;
        /**
         * status
         */
        private Integer status;

        /**
         * DetailDTO
         */
        @NoArgsConstructor
        @Data
        public static class DetailDTO {
            /**
             * endDate
             */
            private String endDate;
            /**
             * timeSlot
             */
            private List<TimeSlotDTO> timeSlot;
            /**
             * startDate
             */
            private String startDate;
            /**
             * dataString
             */
            private List<String> dataString;

            /**
             * TimeSlotDTO
             */
            @NoArgsConstructor
            @Data
            public static class TimeSlotDTO {
                /**
                 * endTime
                 */
                private String endTime;
                /**
                 * startTime
                 */
                private String startTime;
                /**
                 * employeeId
                 */
                private List<Integer> employeeId;
                /**
                 * departmentId
                 */
                private List<Integer> departmentId;
                /**
                 * selectMembers
                 */
                private List<String> selectMembers;
                /**
                 * employeeSelect
                 */
                private EmployeeSelectDTO employeeSelect;

                /**
                 * EmployeeSelectDTO
                 */
                @NoArgsConstructor
                @Data
                public static class EmployeeSelectDTO {
                    /**
                     * key
                     */
                    private String key;
                    /**
                     * label
                     */
                    private String label;
                }
            }
        }
    }

    /**
     * EmployeesDTO
     */
    @NoArgsConstructor
    @Data
    public static class EmployeesVO {
        /**
         * week
         */
        private Integer week;
        /**
         * timeSlot
         */
        private List<TimeSlotDTO> timeSlot;
        /**
         * weekText
         */
        private String weekText;

        /**
         * TimeSlotDTO
         */
        @NoArgsConstructor
        @Data
        public static class TimeSlotDTO {
            /**
             * endTime
             */
            private String endTime;
            /**
             * startTime
             */
            private String startTime;
            /**
             * employeeId
             */
            private List<Integer> employeeId;
            /**
             * departmentId
             */
            private List<Integer> departmentId;
            /**
             * selectMembers
             */
            private List<String> selectMembers;
            /**
             * departmentName
             */
            private List<String> departmentName;
            /**
             * employeeSelect
             */
            private EmployeeSelectDTO employeeSelect;
            /**
             * departmentSelect
             */
            private List<DepartmentSelectDTO> departmentSelect;
            /**
             * selectDepartment
             */
            private List<String> selectDepartment;

            /**
             * EmployeeSelectDTO
             */
            @NoArgsConstructor
            @Data
            public static class EmployeeSelectDTO {
                /**
                 * key
                 */
                private String key;
                /**
                 * label
                 */
                private String label;
            }

            /**
             * DepartmentSelectDTO
             */
            @NoArgsConstructor
            @Data
            public static class DepartmentSelectDTO {
                /**
                 * label
                 */
                private String label;
                /**
                 * value
                 */
                private Integer value;
            }
        }
    }
}
