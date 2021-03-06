## 日期: 2021-06-02 说明: 仅对 2021-06-02 之前的 mochat.sql 进行升级补充
SET FOREIGN_KEY_CHECKS=0;
ALTER TABLE `mc_channel_code` MODIFY COLUMN `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '活码名称' AFTER `group_id`;
ALTER TABLE `mc_chat_tool` MODIFY COLUMN `page_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '侧边栏页面名称' AFTER `id`;
ALTER TABLE `mc_chat_tool` MODIFY COLUMN `page_flag` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '侧边栏页面标识' AFTER `page_name`;
ALTER TABLE `mc_corp` MODIFY COLUMN `wx_corpid` char(18) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '企业微信ID' AFTER `name`;
ALTER TABLE `mc_corp` MODIFY COLUMN `social_code` char(18) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '企业代码(企业统一社会信用代码)' AFTER `wx_corpid`;
ALTER TABLE `mc_corp` MODIFY COLUMN `employee_secret` char(43) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '企业通讯录secret' AFTER `social_code`;
ALTER TABLE `mc_corp` MODIFY COLUMN `contact_secret` char(43) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '企业外部联系人secret' AFTER `event_callback`;
ALTER TABLE `mc_corp` MODIFY COLUMN `token` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '回调token' AFTER `contact_secret`;
ALTER TABLE `mc_corp` MODIFY COLUMN `encoding_aes_key` char(43) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '回调消息加密串' AFTER `token`;
ALTER TABLE `mc_corp` DROP COLUMN `tenant_id`;
ALTER TABLE `mc_greeting` MODIFY COLUMN `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '欢迎语类型' AFTER `corp_id`;
ALTER TABLE `mc_medium_group` MODIFY COLUMN `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '名称' AFTER `corp_id`;
ALTER TABLE `mc_rbac_menu` AUTO_INCREMENT = 220;
ALTER TABLE `mc_rbac_menu` MODIFY COLUMN `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '名称' AFTER `parent_id`;
ALTER TABLE `mc_rbac_menu` MODIFY COLUMN `icon` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '图标标识' AFTER `path`;
ALTER TABLE `mc_rbac_menu` MODIFY COLUMN `operate_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '操作人姓名【mc_user.name】' AFTER `operate_id`;
ALTER TABLE `mc_rbac_role` MODIFY COLUMN `name` varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '角色名称' AFTER `tenant_id`;
ALTER TABLE `mc_rbac_role` MODIFY COLUMN `operate_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '操作人ID【mc_user.name】' AFTER `operate_id`;
ALTER TABLE `mc_sensitive_word_monitor` MODIFY COLUMN `sensitive_word_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '敏感词词库表名称(mc_sensitive_word.name)' AFTER `sensitive_word_id`;
ALTER TABLE `mc_sensitive_word_monitor` MODIFY COLUMN `trigger_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '触发人名称' AFTER `trigger_id`;
ALTER TABLE `mc_sensitive_word_monitor` MODIFY COLUMN `receiver_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '接收者名称' AFTER `receiver_id`;
ALTER TABLE `mc_sys_log` MODIFY COLUMN `menu_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称【mc_rbac_menu.name】' AFTER `menu_id`;
ALTER TABLE `mc_sys_log` MODIFY COLUMN `operate_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '操作人姓名【mc_user.name】' AFTER `operate_id`;
ALTER TABLE `mc_tenant` AUTO_INCREMENT = 2;
ALTER TABLE `mc_tenant` MODIFY COLUMN `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称' AFTER `id`;
ALTER TABLE `mc_tenant` MODIFY COLUMN `logo` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户Logo地址' AFTER `status`;
ALTER TABLE `mc_tenant` MODIFY COLUMN `login_background` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '登录页背景图地址' AFTER `logo`;
ALTER TABLE `mc_tenant` MODIFY COLUMN `copyright` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户版权' AFTER `deleted_at`;
ALTER TABLE `mc_user` AUTO_INCREMENT = 2;
ALTER TABLE `mc_work_agent` AUTO_INCREMENT = 0;
ALTER TABLE `mc_work_agent` DROP COLUMN `type`;
ALTER TABLE `mc_work_contact` MODIFY COLUMN `wx_external_userid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '外部联系人external_userid' AFTER `corp_id`;
ALTER TABLE `mc_work_contact` MODIFY COLUMN `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '外部联系人姓名' AFTER `wx_external_userid`;
ALTER TABLE `mc_work_contact` MODIFY COLUMN `nick_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '外部联系人昵称' AFTER `name`;
ALTER TABLE `mc_work_contact_employee` MODIFY COLUMN `state` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '企业自定义的state参数，用于区分客户具体是通过哪个「联系我」添加，由企业通过创建「联系我」方式指定\n' AFTER `oper_userid`;
ALTER TABLE `mc_work_contact_room` MODIFY COLUMN `wx_user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' AFTER `id`;
ALTER TABLE `mc_work_contact_tag` MODIFY COLUMN `wx_contact_tag_id` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '微信企业标签ID' AFTER `id`;
ALTER TABLE `mc_work_contact_tag` MODIFY COLUMN `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '标签名称' AFTER `corp_id`;
ALTER TABLE `mc_work_employee` MODIFY COLUMN `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '名称' AFTER `corp_id`;
ALTER TABLE `mc_work_employee` MODIFY COLUMN `position` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '职位信息' AFTER `mobile`;
ALTER TABLE `mc_work_employee` MODIFY COLUMN `email` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '邮箱' AFTER `gender`;
ALTER TABLE `mc_work_employee` MODIFY COLUMN `telephone` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '座机' AFTER `thumb_avatar`;
ALTER TABLE `mc_work_employee` MODIFY COLUMN `alias` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '别名' AFTER `telephone`;
ALTER TABLE `mc_work_employee` MODIFY COLUMN `external_position` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '员工对外职位' AFTER `external_profile`;
ALTER TABLE `mc_work_employee` MODIFY COLUMN `address` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '地址' AFTER `external_position`;
ALTER TABLE `mc_work_employee` MODIFY COLUMN `open_user_id` char(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '全局唯一id' AFTER `address`;
ALTER TABLE `mc_work_room` MODIFY COLUMN `notice` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '群公告' AFTER `owner_id`;
ALTER TABLE `mc_work_room_auto_pull` MODIFY COLUMN `qrcode_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '二维码名称' AFTER `corp_id`;
ALTER TABLE `mc_work_room_auto_pull` MODIFY COLUMN `qrcode_url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '二维码地址' AFTER `qrcode_name`;
ALTER TABLE `mc_work_room_auto_pull` MODIFY COLUMN `wx_config_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '二维码凭证' AFTER `qrcode_url`;
ALTER TABLE `mc_work_room_group` MODIFY COLUMN `name` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分组名称' AFTER `corp_id`;

ALTER TABLE `mc_work_agent` ADD COLUMN `type` TINYINT ( 4 ) NOT NULL COMMENT '应用类型 1-侧边栏 2-会话消息 3-工作台' AFTER `home_url`;
INSERT INTO `mc_rbac_menu` (`parent_id`, `name`, `level`, `path`, `icon`, `status`, `link_type`, `is_page_menu`, `link_url`, `data_permission`, `operate_id`, `operate_name`, `sort`, `created_at`, `updated_at`, `deleted_at`) VALUES (71, '用户画像', 3, '#1#-#71#-#220#', '', 1, 1, 1, '/chatTool/customer', 2, 1, '', 99, '2021-02-05 11:35:55', '2021-02-05 11:35:55', NULL);
INSERT INTO `mc_rbac_menu` (`parent_id`, `name`, `level`, `path`, `icon`, `status`, `link_type`, `is_page_menu`, `link_url`, `data_permission`, `operate_id`, `operate_name`, `sort`, `created_at`, `updated_at`, `deleted_at`) VALUES (71, '聊天增强', 3, '#1#-#71#-#221#', '', 1, 1, 1, '/chatTool/enhance', 2, 1, '', 99, '2021-02-05 11:36:44', '2021-02-05 11:36:45', NULL);
INSERT INTO `mc_rbac_menu` (`parent_id`, `name`, `level`, `path`, `icon`, `status`, `link_type`, `is_page_menu`, `link_url`, `data_permission`, `operate_id`, `operate_name`, `sort`, `created_at`, `updated_at`, `deleted_at`) VALUES (14, '用户搜索添加', 3, '#1#-#14#-#222#', '', 1, 1, 1, '/greeting/userSearch', 2, 1, '', 99, '2021-02-05 11:38:10', '2021-02-05 11:38:10', NULL);
UPDATE mc_rbac_menu SET `name` = CONCAT(`name`,'(废弃)') WHERE link_url IN ('/chatTool/config', '/chatTool/config@upload');

SET FOREIGN_KEY_CHECKS=1;