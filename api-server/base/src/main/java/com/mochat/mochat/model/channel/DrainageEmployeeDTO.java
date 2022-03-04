package com.mochat.mochat.model.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DrainageEmployeeDTO
 */
@NoArgsConstructor
@Data
public class DrainageEmployeeDTO {

    /**
     * type
     */ 
    private Integer type;
    /**
     * specialPeriod
     */
    private SpecialPeriodDTO specialPeriod;
    /**
     * addMax
     */
    private AddMaxDTO addMax;
    /**
     * employees
     */
    private List<EmployeesDTO> employees;

    /**
     * SpecialPeriodDTO
     */
    @NoArgsConstructor
    @Data
    public static class SpecialPeriodDTO {
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
                 * employeeSelect
                 */
                private EmployeeSelectDTO employeeSelect;
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
     * AddMaxDTO
     */
    @NoArgsConstructor
    @Data
    public static class AddMaxDTO {
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
     * EmployeesDTO
     */
    @NoArgsConstructor
    @Data
    public static class EmployeesDTO {
        /**
         * week
         */
        private Integer week;
        /**
         * weekText
         */
        private String weekText;
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
             * endTime
             */
            private String endTime;
            /**
             * startTime
             */
            private String startTime;
            /**
             * employeeSelect
             */
            private EmployeeSelectDTO employeeSelect;
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
