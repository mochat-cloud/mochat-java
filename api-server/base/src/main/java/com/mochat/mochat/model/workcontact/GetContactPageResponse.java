package com.mochat.mochat.model.workcontact;

import com.mochat.mochat.dao.vo.ContactDataVo;
import lombok.Data;

import java.util.List;

/**
 * @description: 获取客户列表分页后返回集合
 * @author: zhaojinjian
 * @create: 2020-11-26 14:39
 **/
@Data
public class GetContactPageResponse {
    /**
     * 最后一次同步客户时间
     */
    private String syncContactTime;
    /**
     * 分页信息
     */
    private ContactPage page;
    /**
     * 客户集合列表
     */
    private List<ContactDataVo> list;

    /**
     * 分页信息类
     */
    @Data
    public static class ContactPage {
        /**
         *每页显示数
         */
        private Long perPage;
        /**
         *总条数
         */
        private Long total;
        /**
         *总页数
         */
        private Long totalPage;
    }


}
/**
 * 客户列表实体类
 */

