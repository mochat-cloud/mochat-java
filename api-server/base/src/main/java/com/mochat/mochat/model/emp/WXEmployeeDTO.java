package com.mochat.mochat.model.emp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class WXEmployeeDTO {

    /**
     * 成员UserID
     */
    @JSONField(name = "userid")
    private String userid;

    /**
     * 成员名称
     */
    @JSONField(name = "name")
    private String name;

    /**
     * 职务信息
     */
    @JSONField(name = "position")
    private String position;

    /**
     * 对外职务，如果设置了该值，则以此作为对外展示的职务，否则以position来展示
     */
    @JSONField(name = "external_position")
    private String externalPosition;

    /**
     * 全局唯一。对于同一个服务商，不同应用获取到企业内同一个成员的open_userid是相同的，最多64个字节。仅第三方应用可获取
     */
    @JSONField(name = "open_userid")
    private String openUserid;

    /**
     * 手机号码
     */
    @JSONField(name = "mobile")
    private String mobile;

    /**
     * 性别
     */
    @JSONField(name = "gender")
    private Integer gender;

    /**
     * 邮箱
     */
    @JSONField(name = "email")
    private String email;

    /**
     * 头像
     */
    @JSONField(name = "avatar")
    private String avatar;

    /**
     * 激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业
     */
    @JSONField(name = "status")
    private Integer status;

    /**
     * isleader
     */
    @JSONField(name = "isleader")
    private Integer isleader;

    /**
     * 扩展属性
     */
    @JSONField(name = "extattr")
    private String extattr;

    /**
     * 座机
     */
    @JSONField(name = "telephone")
    private String telephone;

    /**
     * 成员对外属性
     */
    @JSONField(name = "external_profile")
    private String externalProfile;

    /**
     * 主部门
     */
    @JSONField(name = "main_department")
    private Integer mainDepartment;

    /**
     * 员工个人二维码，扫描可添加为外部联系人(注意返回的是一个url，可在浏览器上打开该url以展示二维码)
     */
    @JSONField(name = "qr_code")
    private String qrCode;

    /**
     * 别名
     */
    @JSONField(name = "alias")
    private String alias;

    /**
     * 地址
     */
    @JSONField(name = "address")
    private String address;

    /**
     * 头像缩略图
     */
    @JSONField(name = "thumb_avatar")
    private String thumbAvatar;

    /**
     * 成员所属部门id列表，仅返回该应用有查看权限的部门id
     */
    @JSONField(name = "department")
    private List<Integer> department;

    /**
     * 部门内的排序值，默认为0。数量必须和department一致，数值越大排序越前面。值范围是[0, 2^32)
     */
    @JSONField(name = "order")
    private List<Integer> order;

    /**
     * 表示在所在的部门内是否为上级
     */
    @JSONField(name = "is_leader_in_dept")
    private List<Integer> isLeaderInDept;

    public String getThumbAvatar() {
        if (thumbAvatar == null && avatar != null) {
            int index = avatar.lastIndexOf("/");
            return avatar.substring(0, index) + "/100";
        }
        return thumbAvatar;
    }
}
