package com.mochat.mochat.model.dept;

import lombok.Data;

import java.util.List;

@Data
public class DeptPageItemVO {
    private Integer id;
    private String departmentPath;
    private Integer departmentId;
    private String name;
    private String level;
    private Integer parentId;
    private List<DeptPageItemVO> children;
}
