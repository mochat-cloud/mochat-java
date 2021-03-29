package com.mochat.mochat.model.emp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WXDepartmentDTO {

    /**
     * id : 2
     * name : 广州研发中心
     * name_en : RDGZ
     * parentid : 1
     * order : 10
     */

     /**
      * 创建的部门id
      */
    @JSONField(name = "id")
    private Integer id;

    /**
     * 部门名称
     */
    @JSONField(name = "name")
    private String name;

    /**
     * 英文名称
     */
    @JSONField(name = "name_en")
    private String nameEn;

    /**
     * 父部门id
     */
    @JSONField(name = "parentid")
    private Integer parentid;

    /**
     * 在父部门中的次序值
     */
    @JSONField(name = "order")
    private Integer order;
}
