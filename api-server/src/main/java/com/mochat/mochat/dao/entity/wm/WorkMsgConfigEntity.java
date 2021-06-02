package com.mochat.mochat.dao.entity.wm;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author: yangpengwei
 * @time: 2020/11/13 4:16 下午
 * @description 会话内容存档配置
 */
@Data
@TableName("mc_work_message_config")
@EqualsAndHashCode(callSuper = false)
public class WorkMsgConfigEntity extends Model<WorkMsgConfigEntity> {

    /**
     * id : 2
     * corp_id : 2
     * chat_admin : 负责人
     * chat_admin_phone : 13512345678
     * chat_admin_idcard : 320526198903260418
     * chat_apply_status : 4
     * chat_rsa_key : {"version": "3", "publicKey": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoaHkUFMi2Tfy6dq6QHIQ\n0i71bzYZDvF1tIpfcYxp157vzzEd9DEgdy5vZLerFpcrjSWUauhe14st3TW8V+LL\nEHNlMBMnq+cSBVWpcor/eVP6gsR9UMUDVG9EFQmaSiPhjLhnND+0wzvpHsJ9Q1So\ny/GTOVYmhzP854Vn6zz79WBmEGv3spCEw/LZh1llMdcNTF/FEXNng+ngP3quVApN\nIT2QxdJfaxWtRpdD/Bsk4fXHhQQvi7kOPjiGGQfPR/ucZMOOpsND13EiFjBfB39h\ntK71Vul+fn8569ZlA4/Dz9DFCs71zy9lFx4j7IR6IX3Qh0LCGjavx+8rQVfhJR2b\nywIDAQAB\n-----END PUBLIC KEY-----\n", "privateKey": "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQChoeRQUyLZN/Lp\n2rpAchDSLvVvNhkO8XW0il9xjGnXnu/PMR30MSB3Lm9kt6sWlyuNJZRq6F7Xiy3d\nNbxX4ssQc2UwEyer5xIFValyiv95U/qCxH1QxQNUb0QVCZpKI+GMuGc0P7TDO+ke\nwn1DVKjL8ZM5ViaHM/znhWfrPPv1YGYQa/eykITD8tmHWWUx1w1MX8URc2eD6eA/\neq5UCk0hPZDF0l9rFa1Gl0P8GyTh9ceFBC+LuQ4+OIYZB89H+5xkw46mw0PXcSIW\nMF8Hf2G0rvVW6X5+fznr1mUDj8PP0MUKzvXPL2UXHiPshHohfdCHQsIaNq/H7ytB\nV+ElHZvLAgMBAAECggEAdOTlfyYxDNHGz1QN4W/dNHUbsbt5MfJ1AwYT8sXvAi8D\nFmBaXnFtHmSp0Th39pWLvL/R7+NwPAh/Of2T4ie2XoZmRveDeHJb+Rmzu5BFJVUT\n1qEOVGy3dMgnf+N6/2WlyKTQltv/+jaI6WMVPq2qihyDMjNZF+0EWdiEiH+JZTKU\n5pD+Hgw3rUnyKRBr8ohSSfb9wFAp5zHC/4Elo+qK68rggjLZSlAudWc2yx9zoR0F\nXHN3xY6OqAVUjW8mVTeh4OoUVMOq2dhknA8yKB+SaOmIF2OQU5CNa72njKBbJFqX\njY4b5s4UBYQEjnWmOQIApOQWc1b4xT6OQ9pC44NAeQKBgQDVxesGGW80/xZ33Lfc\nb5w3abwkXJRzJPVNBVh8/0KkdTTiZI33doAeNdDVkkdJTZ8Mfq787fswQCRs1iA5\n5DMyS1LRi5Z2mFKi2/W3g257yfbZ2cVhGssXiiCmJihAJvaQGX3qiPtJgHTfDq7R\nKUtyulPtbkZW8v4J4x+ReAltvwKBgQDBj1Id/uW/se0k9Qd4gbz7wcK5UtAW3yun\nxZHd6NYTVTM8SO7VG2qLh624RdUurzeMBWTYABcPqgD0KtKFKwQ/joLlaoH0GZRe\nuH5iUc83gStlSoMg69BuYrVxCEID3nzSII1BBZsAVLfJeU/hOYc8s991Bm72BBTq\ndRdVPvVs9QKBgQC5rA+32nuGklCXhvOC2MXcM1AgXPDrGZydaxkyaBsf8FUglang\n1+HrW16pQUX9WxfbqGSgdqsHe46noUuYPKZi9p1WN2FWXHLZ1SaUX+mkRwTKrqUK\n9FSVe3lkFhw7rBvXiOyeup9XdeUMoBoivTU0zrV6sZ8Sgjc3qzeOB65YzQKBgQCW\n3Weq24clb8Af4yAMjErJi4+DtQleGlpDbRmxdg6NiQXjQhFtZu4XjQwZKczsk0Nf\nTsD4HrHa0q3log7uHsYz55ccy3Q410suvOw9I/i4EWTKkVY9ba/KqgMz87TIoTx2\n50pR2NWkX2PUlEpviNBgvu43n2kTNcE7sUIbfnP6lQKBgFvju3kiOqDfBjxeySOx\nyeEHNoh+Tk6IEHO1g9gzi3UYRTtMWOsxS0nA+/A4o5UczJ8cyoPR8jejffYCXLEm\nZTJdzsh5k5PCLeekbZc/8jJuzAgtKpJLBOcegNx0+0EIjtXcjtxYPooLKPkFicaz\nC8fq4jl9US6Jy3aHrCn4AMmF\n-----END PRIVATE KEY-----\n"}
     * chat_secret : K1djMDoN5WQFNg2aKH0BwmVMARwfdxVpUwDhWHnYlDw
     * chat_status : 1
     * created_at : 4/11/2020 22:33:16
     * updated_at : 5/11/2020 19:30:19
     * deleted_at :
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer corpId;
    private String chatAdmin;
    private String chatAdminPhone;
    private String chatAdminIdcard;
    private Integer chatApplyStatus;
    private String chatRsaKey;
    private String chatSecret;
    private Integer chatStatus;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedAt;
    @TableLogic
	private Date deletedAt;

}
