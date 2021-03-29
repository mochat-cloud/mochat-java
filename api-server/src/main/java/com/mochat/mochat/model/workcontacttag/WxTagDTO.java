package com.mochat.mochat.model.workcontacttag;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class WxTagDTO {

    /**
     * groupId
     */
    @JSONField(name = "group_id")
    private String groupId;
    /**
     * groupName
     */
    @JSONField(name = "group_name")
    private String groupName;
    /**
     * createTime
     */
    @JSONField(name = "create_time")
    private Integer createTime;
    /**
     * order
     */
    @JSONField(name = "order")
    private Integer order;
    /**
     * tag
     */
    @JSONField(name = "tag")
    private List<TagDTO> tag;

    /**
     * TagDTO
     */
    @NoArgsConstructor
    @Data
    public static class TagDTO {
        /**
         * id
         */
        @JSONField(name = "id")
        private String id;
        /**
         * name
         */
        @JSONField(name = "name")
        private String name;
        /**
         * createTime
         */
        @JSONField(name = "create_time")
        private Integer createTime;
        /**
         * order
         */
        @JSONField(name = "order")
        private Integer order;
    }
}
