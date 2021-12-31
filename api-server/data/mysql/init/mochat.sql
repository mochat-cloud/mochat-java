CREATE DATABASE IF NOT EXISTS mochat DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

USE mochat;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mc_business_log
-- ----------------------------
DROP TABLE IF EXISTS `mc_business_log`;
CREATE TABLE `mc_business_log` (
                                   `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                   `business_id` int(11) NOT NULL DEFAULT '0' COMMENT '相应业务id',
                                   `params` json DEFAULT NULL COMMENT '参数',
                                   `event` smallint(6) NOT NULL DEFAULT '4' COMMENT '事件',
                                   `operation_id` int(11) NOT NULL DEFAULT '0' COMMENT '操作人id（mc_work_employee.id）',
                                   `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                   `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业务日志表';

-- ----------------------------
-- Records of mc_business_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_channel_code
-- ----------------------------
DROP TABLE IF EXISTS `mc_channel_code`;
CREATE TABLE `mc_channel_code` (
                                   `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                   `corp_id` int(11) NOT NULL DEFAULT '0' COMMENT '企业id',
                                   `group_id` int(11) NOT NULL DEFAULT '0' COMMENT '渠道码分组id（mc_channel_code_group.id）',
                                   `name` varchar(30) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '活码名称',
                                   `qrcode_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '二维码地址',
                                   `wx_config_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '二维码凭证',
                                   `auto_add_friend` tinyint(4) NOT NULL DEFAULT '0' COMMENT '自动添加好友（1.开启，2.关闭）',
                                   `tags` json NOT NULL COMMENT '客户标签',
                                   `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '类型（1.单人，2.多人）',
                                   `drainage_employee` json NOT NULL COMMENT '引流成员设置',
                                   `welcome_message` json NOT NULL COMMENT '欢迎语设置',
                                   `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                   `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                   `deleted_at` timestamp NULL DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='渠道码表';

-- ----------------------------
-- Records of mc_channel_code
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_channel_code_group
-- ----------------------------
DROP TABLE IF EXISTS `mc_channel_code_group`;
CREATE TABLE `mc_channel_code_group` (
                                         `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                         `corp_id` int(11) NOT NULL COMMENT '企业id',
                                         `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分组名称',
                                         `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                         `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                         `deleted_at` timestamp NULL DEFAULT NULL,
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='渠道码-分组表';

-- ----------------------------
-- Records of mc_channel_code_group
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_chat_tool
-- ----------------------------
DROP TABLE IF EXISTS `mc_chat_tool`;
CREATE TABLE `mc_chat_tool` (
                                `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                `page_name` varchar(10) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '侧边栏页面名称',
                                `page_flag` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '侧边栏页面标识',
                                `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `updated_at` timestamp NULL DEFAULT NULL,
                                `deleted_at` timestamp NULL DEFAULT NULL,
                                `status` tinyint(4) DEFAULT '1' COMMENT '状态 0否 1是',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业侧边工具栏';

-- ----------------------------
-- Records of mc_chat_tool
-- ----------------------------
BEGIN;
INSERT INTO `mc_chat_tool` VALUES (1, '客户画像', 'customer', '2020-12-30 17:12:02', NULL, NULL, 1);
INSERT INTO `mc_chat_tool` VALUES (2, '素材库', 'mediumGroup', '2020-12-30 17:12:02', NULL, NULL, 1);
COMMIT;

-- ----------------------------
-- Table structure for mc_contact_employee_process
-- ----------------------------
DROP TABLE IF EXISTS `mc_contact_employee_process`;
CREATE TABLE `mc_contact_employee_process` (
                                               `id` int(11) unsigned NOT NULL,
                                               `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID（corp.id）',
                                               `employee_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '员工ID（mc_work_employee.id）',
                                               `contact_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '外部联系人ID（mc_work_contact.id）',
                                               `contact_process_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '跟进流程ID',
                                               `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '跟进内容',
                                               `file_url` json DEFAULT NULL COMMENT '附件地址',
                                               `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                               `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                               `deleted_at` timestamp NULL DEFAULT NULL,
                                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通讯录-客户-跟进记录(中间表) ';

-- ----------------------------
-- Records of mc_contact_employee_process
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_contact_employee_track
-- ----------------------------
DROP TABLE IF EXISTS `mc_contact_employee_track`;
CREATE TABLE `mc_contact_employee_track` (
                                             `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                             `employee_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '通讯录ID(mc_work_employee.id)',
                                             `contact_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '外部联系人ID work_contact.id',
                                             `event` tinyint(4) NOT NULL DEFAULT '0' COMMENT '事件',
                                             `content` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '内容',
                                             `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID corp.id',
                                             `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                             `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                             `deleted_at` timestamp NULL DEFAULT NULL,
                                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通讯录 - 客户 - 轨迹互动';

-- ----------------------------
-- Records of mc_contact_employee_track
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_contact_field
-- ----------------------------
DROP TABLE IF EXISTS `mc_contact_field`;
CREATE TABLE `mc_contact_field` (
                                    `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                    `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字段标识 input-name',
                                    `label` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字段名称 input-label',
                                    `type` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '字段类型 input-type 0text 1radio 2 checkbox 3select 4file 5date 6dateTime 7number 8rate',
                                    `options` json DEFAULT NULL COMMENT '字段可选值 input-options',
                                    `order` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '排序',
                                    `status` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '状态 0不展示 1展示',
                                    `is_sys` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否为系统字段 0否1是',
                                    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                    `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                    `deleted_at` timestamp NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户高级属性';

-- ----------------------------
-- Records of mc_contact_field
-- ----------------------------
BEGIN;
INSERT INTO `mc_contact_field` VALUES (1, 'phone', '手机号', 9, '[]', 0, 1, 1, '2020-12-29 19:39:45', NULL, NULL);
INSERT INTO `mc_contact_field` VALUES (2, 'name', '姓名', 0, '[]', 0, 1, 1, '2020-12-29 19:39:45', NULL, NULL);
INSERT INTO `mc_contact_field` VALUES (3, 'gender', '性别', 1, '[\"男\", \"女\", \"未知\"]', 0, 1, 1, '2020-12-29 19:39:45', NULL, NULL);
INSERT INTO `mc_contact_field` VALUES (4, 'birthday', '生日', 6, '[]', 0, 1, 1, '2020-12-29 19:39:45', NULL, NULL);
INSERT INTO `mc_contact_field` VALUES (5, 'age', '年龄', 8, '[]', 0, 1, 1, '2020-12-29 19:39:45', NULL, NULL);
INSERT INTO `mc_contact_field` VALUES (6, 'QQ', 'QQ', 0, '[]', 0, 1, 1, '2020-12-29 19:39:45', NULL, NULL);
INSERT INTO `mc_contact_field` VALUES (7, 'email', '邮箱', 10, '[]', 0, 1, 1, '2020-12-29 19:39:45', NULL, NULL);
INSERT INTO `mc_contact_field` VALUES (8, 'hobby', '爱好', 2, '[\"游戏\", \"阅读\", \"音乐\", \"运动\", \"动漫\", \"旅行\", \"家居\", \"曲艺\", \"宠物\", \"美食\", \"娱乐\", \"电影\", \"电视剧\", \"健康养生\", \"数码\", \"其他\"]', 0, 1, 1, '2020-12-29 19:39:45', NULL, NULL);
INSERT INTO `mc_contact_field` VALUES (9, 'education', '学历', 3, '[\"博士\", \"硕士\", \"大学\", \"大专\", \"高中\", \"初中\", \"小学\", \"其他\"]', 0, 1, 1, '2020-12-29 19:39:45', NULL, NULL);
INSERT INTO `mc_contact_field` VALUES (10, 'annualIncome', '年收入', 3, '[\"5万以下\", \"5万-15万\", \"15万-30万\", \"30万以上\", \"50-100万\", \"100万-200万\", \"200万-500万\", \"500万-1000万\", \"1000万-5000万\"]', 0, 1, 1, '2020-12-29 19:39:45', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for mc_contact_field_pivot
-- ----------------------------
DROP TABLE IF EXISTS `mc_contact_field_pivot`;
CREATE TABLE `mc_contact_field_pivot` (
                                          `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                          `contact_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '客户表ID（work_contact.id）',
                                          `contact_field_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '高级属性表ID(contact_field.id）',
                                          `value` text COLLATE utf8mb4_unicode_ci COMMENT '高级属性值',
                                          `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                          `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                          `deleted_at` timestamp NULL DEFAULT NULL,
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='(客户-高级属性-中间表)用户画像';

-- ----------------------------
-- Records of mc_contact_field_pivot
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_contact_process
-- ----------------------------
DROP TABLE IF EXISTS `mc_contact_process`;
CREATE TABLE `mc_contact_process` (
                                      `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                      `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'corp表id',
                                      `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '名称',
                                      `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '描述',
                                      `order` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '排序',
                                      `created_at` timestamp NULL DEFAULT NULL,
                                      `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                      `deleted_at` timestamp NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户跟进状态';

-- ----------------------------
-- Records of mc_contact_process
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_corp
-- ----------------------------
DROP TABLE IF EXISTS `mc_corp`;
CREATE TABLE `mc_corp` (
                           `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                           `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '企业名称',
                           `wx_corpid` char(18) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '企业微信ID',
                           `social_code` char(18) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '企业代码(企业统一社会信用代码)',
                           `employee_secret` char(43) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '企业通讯录secret',
                           `event_callback` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '事件回调地址',
                           `contact_secret` char(43) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '企业外部联系人secret',
                           `token` char(32) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '回调token',
                           `encoding_aes_key` char(43) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '回调消息加密串',
                           `created_at` timestamp NULL DEFAULT NULL,
                           `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                           `deleted_at` timestamp NULL DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业';

-- ----------------------------
-- Records of mc_corp
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_corp_day_data
-- ----------------------------
DROP TABLE IF EXISTS `mc_corp_day_data`;
CREATE TABLE `mc_corp_day_data` (
                                    `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                    `corp_id` int(11) NOT NULL DEFAULT '0' COMMENT '企业id',
                                    `add_contact_num` int(10) NOT NULL DEFAULT '0' COMMENT '新增客户数',
                                    `add_room_num` int(10) NOT NULL DEFAULT '0' COMMENT '新增社群数',
                                    `add_into_room_num` int(10) NOT NULL DEFAULT '0' COMMENT '新增入群数',
                                    `loss_contact_num` int(10) NOT NULL DEFAULT '0' COMMENT '流失客户数',
                                    `quit_room_num` int(10) NOT NULL DEFAULT '0' COMMENT '退群数',
                                    `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日期',
                                    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                    `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业日数据';

-- ----------------------------
-- Records of mc_corp_day_data
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_greeting
-- ----------------------------
DROP TABLE IF EXISTS `mc_greeting`;
CREATE TABLE `mc_greeting` (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `corp_id` int(11) NOT NULL DEFAULT '0' COMMENT '企业ID',
                               `type` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '欢迎语类型',
                               `words` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '欢迎语文本',
                               `medium_id` int(11) NOT NULL DEFAULT '0' COMMENT '欢迎语素材',
                               `range_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '适用成员类型【1-全部成员(默认)】',
                               `employees` json NOT NULL COMMENT '适用成员',
                               `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                               `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                               `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='欢迎语';

-- ----------------------------
-- Records of mc_greeting
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_medium
-- ----------------------------
DROP TABLE IF EXISTS `mc_medium`;
CREATE TABLE `mc_medium` (
                             `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                             `media_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '素材媒体标识[有效期3天]',
                             `last_upload_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '上一次微信素材上传的时间戳',
                             `type` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '类型 1文本、2图片、3音频、4视频、5小程序、6文件素材',
                             `is_sync` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否同步素材库(1-同步2-不同步，默认:1)',
                             `content` json NOT NULL COMMENT '具体内容:',
                             `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID(mc_corp.id)',
                             `medium_group_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '素材分组ID medium_group.id',
                             `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '上传者ID',
                             `user_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '上传者名称',
                             `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                             `deleted_at` timestamp NULL DEFAULT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='素材库 ';

-- ----------------------------
-- Records of mc_medium
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_medium_group
-- ----------------------------
DROP TABLE IF EXISTS `mc_medium_group`;
CREATE TABLE `mc_medium_group` (
                                   `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                   `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID',
                                   `name` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '名称',
                                   `order` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '排序',
                                   `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                   `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                   `deleted_at` timestamp NULL DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='素材库-分组';

-- ----------------------------
-- Records of mc_medium_group
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_migrations
-- ----------------------------
DROP TABLE IF EXISTS `mc_migrations`;
CREATE TABLE `mc_migrations` (
                                 `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                 `migration` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `batch` int(11) NOT NULL,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of mc_migrations
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_plugin
-- ----------------------------
DROP TABLE IF EXISTS `mc_plugin`;
CREATE TABLE `mc_plugin` (
                             `id` int(10) NOT NULL,
                             `corp_id` int(11) NOT NULL DEFAULT '0' COMMENT '企业id',
                             `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '插件名称',
                             `version` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '版本号',
                             `content` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '简介',
                             `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（1-启用，2-禁用）',
                             `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                             `deleted_at` timestamp NULL DEFAULT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='插件表';

-- ----------------------------
-- Records of mc_plugin
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_rbac_menu
-- ----------------------------
DROP TABLE IF EXISTS `mc_rbac_menu`;
CREATE TABLE `mc_rbac_menu` (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `parent_id` int(11) NOT NULL COMMENT '父级ID',
                                `name` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '名称',
                                `level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '菜单等级【1-一级菜单2-二级菜单···】',
                                `path` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'ID路径【id-id-id】',
                                `icon` varchar(100) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '图标标识',
                                `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态【1-启动(默认)2-禁用】',
                                `link_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '链接类型【1-内部链接(默认)2-外部链接】',
                                `is_page_menu` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否为页面菜单 1-是 2-否',
                                `link_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '链接地址【pathinfo#method】',
                                `data_permission` tinyint(1) NOT NULL DEFAULT '1' COMMENT '数据权限 【1-启用 2不启用（查看企业下数据）】',
                                `operate_id` int(11) NOT NULL DEFAULT '0' COMMENT '操作人ID【mc_user.id】',
                                `operate_name` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '操作人姓名【mc_user.name】',
                                `sort` int(11) DEFAULT '99' COMMENT '排序',
                                `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                                `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=226 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- ----------------------------
-- Records of mc_rbac_menu
-- ----------------------------
BEGIN;
INSERT INTO `mc_rbac_menu` VALUES (1, 0, '企微管理', 1, '#1#', '', 1, 1, 1, '_baseSysManager', 2, 0, '系统', 99, '2020-12-31 19:22:04', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (2, 1, '引流获取', 2, '#1#-#2#', 'line-chart', 1, 1, 1, '_baseShunt', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (3, 2, '渠道活码', 3, '#1#-#2#-#3#', '', 1, 1, 1, '/channelCode/index', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (4, 3, '新建渠道码', 4, '#1#-#2#-#3#-#4#', '', 1, 1, 1, '/channelCode/store', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (5, 4, '确定(按钮)', 5, '#1#-#2#-#3#-#4#-#5#', '', 1, 1, 1, '/channelCode/store@confirm', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (6, 3, '统计(按钮)', 4, '#1#-#2#-#3#-#6#', '', 1, 1, 1, '/channelCode/statistics', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (7, 3, '查询(按钮)', 4, '#1#-#2#-#3#-#7#', '', 1, 1, 1, '/channelCode/index@search', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (8, 3, '修改分组(按钮)', 4, '#1#-#2#-#3#-#8#', '', 1, 1, 1, '/channelCode/index@editGroup', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (9, 3, '新建分组(按钮)', 4, '#1#-#2#-#3#-#9#', '', 1, 1, 1, '/channelCode/index@add', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (10, 3, '编辑(按钮)', 4, '#1#-#2#-#3#-#10#', '', 1, 1, 1, '/channelCode/index@edit', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (11, 3, '客户(按钮)', 4, '#1#-#2#-#3#-#11#', '', 1, 1, 1, '/channelCode/index@customer', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (12, 3, '下载(按钮)', 4, '#1#-#2#-#3#-#12#', '', 1, 1, 1, '/channelCode/index@download', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (13, 3, '移动(按钮)', 4, '#1#-#2#-#3#-#13#', '', 1, 1, 1, '/channelCode/index@move', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (14, 1, '客户转化', 2, '#1#-#14#', 'pie-chart', 1, 1, 1, '/contact/transfer', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (15, 14, '好友欢迎语', 3, '#1#-#14#-#15#', '', 1, 1, 1, '/greeting/index', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (16, 15, '新建欢迎语', 4, '#1#-#14#-#15#-#16#', '', 1, 1, 1, '/greeting/store', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (17, 16, '创建欢迎语(按钮)', 5, '#1#-#14#-#15#-#16#-#17#', '', 1, 1, 1, '/greeting/store@add', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (18, 15, '编辑(按钮)', 4, '#1#-#14#-#15#-#18#', '', 1, 1, 1, '/greeting/index@edit', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (19, 15, '删除(按钮)', 4, '#1#-#14#-#15#-#19#', '', 1, 1, 1, '/greeting/index@delete', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (20, 14, '素材库', 3, '#1#-#14#-#20#', '', 1, 1, 1, '/mediumGroup/index', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (21, 20, '修改分组(按钮)', 4, '#1#-#14#-#20#-#21#', '', 1, 1, 1, '/mediumGroup/index@editGroup', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (22, 20, '新增分组(按钮)', 4, '#1#-#14#-#20#-#22#', '', 1, 1, 1, '/mediumGroup/index@addGroup', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (23, 20, '编辑(按钮)', 4, '#1#-#14#-#20#-#23#', '', 1, 1, 1, '/mediumGroup/index@edit', 2, 0, '系统', 99, '2020-12-31 19:22:05', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (24, 20, '移动(按钮)', 4, '#1#-#14#-#20#-#24#', '', 1, 1, 1, '/mediumGroup/index@move', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (25, 20, '删除(按钮)', 4, '#1#-#14#-#20#-#25#', '', 1, 1, 1, '/mediumGroup/index@delete', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (26, 20, '添加(按钮)', 4, '#1#-#14#-#20#-#26#', '', 1, 1, 1, '/mediumGroup/index@add', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (27, 1, '客户管理', 2, '#1#-#27#', 'solution', 1, 1, 1, '_baseContactManage', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (28, 27, '客户列表', 3, '#1#-#27#-#28#', '', 1, 1, 1, '/workContact/index', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (29, 28, '客户详情', 4, '#1#-#27#-#28#-#29#', '', 1, 1, 1, '/workContact/contactFieldPivot', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (30, 29, '互动轨迹(按钮)', 5, '#1#-#27#-#28#-#29#-#30#', '', 1, 1, 1, '/workContact/contactFieldPivot@track', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (31, 29, '用户画像(按钮)', 5, '#1#-#27#-#28#-#29#-#31#', '', 1, 1, 1, '/workContact/contactFieldPivot@detail', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (32, 29, '编辑(按钮)', 5, '#1#-#27#-#28#-#29#-#32#', '', 1, 1, 1, '/workContact/contactFieldPivot@edit', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (33, 29, '编辑基础信息(按钮)', 5, '#1#-#27#-#28#-#29#-#33#', '', 1, 1, 1, '/workContact/contactFieldPivot@update', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (34, 28, '查询(按钮)', 4, '#1#-#27#-#28#-#34#', '', 1, 1, 1, '/workContact/index@search', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (35, 28, '同步客户(按钮)', 4, '#1#-#27#-#28#-#35#', '', 1, 1, 1, '/workContact/index@sync', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (36, 27, '客户资料卡', 3, '#1#-#27#-#36#', '', 1, 1, 1, '/contactField/index', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (37, 36, '高级属性(按钮)', 4, '#1#-#27#-#36#-#37#', '', 1, 1, 1, '/contactField/index@advanced', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (38, 36, '全部状态(按钮)', 4, '#1#-#27#-#36#-#38#', '', 1, 1, 1, '/contactField/index@all', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (39, 36, '新增属性(按钮)', 4, '#1#-#27#-#36#-#39#', '', 1, 1, 1, '/contactField/index@add', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (40, 36, '批量修改(按钮)', 4, '#1#-#27#-#36#-#40#', '', 1, 1, 1, '/contactField/index@batch', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (41, 36, '编辑(按钮)', 4, '#1#-#27#-#36#-#41#', '', 1, 1, 1, '/contactField/index@edit', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (42, 36, '关闭(按钮)', 4, '#1#-#27#-#36#-#42#', '', 1, 1, 1, '/contactField/index@close', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (43, 27, '流失提醒', 3, '#1#-#27#-#43#', '', 1, 1, 1, '/lossContact/index', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (44, 43, '选择部门成员(按钮)', 4, '#1#-#27#-#43#-#44#', '', 1, 1, 1, '/lossContact/index@choose', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (45, 27, '客户标签', 3, '#1#-#27#-#45#', '', 1, 1, 1, '/workContactTag/index', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (46, 45, '同步企业微信通讯录(按钮)', 4, '#1#-#27#-#45#-#46#', '', 1, 1, 1, '/workContactTag/index@sync', 2, 0, '系统', 99, '2020-12-31 19:22:06', '2020-12-31 19:22:06', NULL);
INSERT INTO `mc_rbac_menu` VALUES (47, 45, '修改分组(按钮)', 4, '#1#-#27#-#45#-#47#', '', 1, 1, 1, '/workContactTag/index@editGroup', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (48, 45, '新增分组(按钮)', 4, '#1#-#27#-#45#-#48#', '', 1, 1, 1, '/workContactTag/index@addGroup', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (49, 45, '删除标签(按钮)', 4, '#1#-#27#-#45#-#49#', '', 1, 1, 1, '/workContactTag/index@deleteTag', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (50, 45, '新建标签(按钮)', 4, '#1#-#27#-#45#-#50#', '', 1, 1, 1, '/workContactTag/index@add', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (51, 45, '编辑(按钮)', 4, '#1#-#27#-#45#-#51#', '', 1, 1, 1, '/workContactTag/index@edit', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (52, 45, '删除(按钮)', 4, '#1#-#27#-#45#-#52#', '', 1, 1, 1, '/workContactTag/index@delete', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (53, 45, '移动标签(按钮)', 4, '#1#-#27#-#45#-#53#', '', 1, 1, 1, '/workContactTag/index@move', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (54, 1, '客户群运营', 2, '#1#-#54#', 'team', 1, 1, 1, '_baseContactRoomManage', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (55, 54, '客户群列表', 3, '#1#-#54#-#55#', '', 1, 1, 1, '/workRoom/index', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (56, 55, '群统计', 4, '#1#-#54#-#55#-#56#', '', 1, 1, 1, '/workRoom/statistics', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (57, 55, '增加分组(按钮)', 4, '#1#-#54#-#55#-#57#', '', 1, 1, 1, '/workRoom/index@add', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (58, 55, '编辑分组(按钮)', 4, '#1#-#54#-#55#-#58#', '', 1, 1, 1, '/workRoom/index@edit', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (59, 55, '查找(按钮)', 4, '#1#-#54#-#55#-#59#', '', 1, 1, 1, '/workRoom/index@search', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (60, 55, '批量修改分组(按钮)', 4, '#1#-#54#-#55#-#60#', '', 1, 1, 1, '/workRoom/index@batch', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (61, 55, '同步群(按钮)', 4, '#1#-#54#-#55#-#61#', '', 1, 1, 1, '/workRoom/index@sync', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (62, 55, '群成员(按钮)', 4, '#1#-#54#-#55#-#62#', '', 1, 1, 1, '/workRoom/index@member', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (63, 55, '移动分组(按钮)', 4, '#1#-#54#-#55#-#63#', '', 1, 1, 1, '/workRoom/index@move', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (64, 54, '自动拉群', 3, '#1#-#54#-#64#', '', 1, 1, 1, '/workRoomAutoPull/index', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (65, 64, '新建拉群', 4, '#1#-#54#-#64#-#65#', '', 1, 1, 1, '/workRoomAutoPull/store', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (66, 65, '提交(按钮)', 5, '#1#-#54#-#64#-#65#-#66#', '', 1, 1, 1, '/workRoomAutoPull/store@submit', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (67, 64, '查找(按钮)', 4, '#1#-#54#-#64#-#67#', '', 1, 1, 1, '/workRoomAutoPull/index@search', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (68, 64, '详情(按钮)', 4, '#1#-#54#-#64#-#68#', '', 1, 1, 1, '/workRoomAutoPull/index@detail', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (69, 64, '编辑(按钮)', 4, '#1#-#54#-#64#-#69#', '', 1, 1, 1, '/workRoomAutoPull/index@edit', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:07', NULL);
INSERT INTO `mc_rbac_menu` VALUES (70, 64, '下载(按钮)', 4, '#1#-#54#-#64#-#70#', '', 1, 1, 1, '/workRoomAutoPull/index@download', 2, 0, '系统', 99, '2020-12-31 19:22:07', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (71, 1, '聊天侧边栏', 2, '#1#-#71#', 'message', 1, 1, 1, '_chatTool', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (72, 71, '聊天侧边栏(废弃)(废弃)', 3, '#1#-#71#-#72#', '', 1, 1, 1, '/chatTool/config', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2021-12-31 15:40:41', NULL);
INSERT INTO `mc_rbac_menu` VALUES (73, 72, '上传文件(按钮)(废弃)(废弃)', 4, '#1#-#71#-#72#-#73#', '', 1, 1, 1, '/chatTool/config@upload', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2021-12-31 15:40:41', NULL);
INSERT INTO `mc_rbac_menu` VALUES (74, 1, '企业风控', 2, '#1#-#74#', 'radar-chart', 1, 1, 1, '_baseRiskControl', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (75, 74, '消息存档', 3, '#1#-#74#-#75#', '', 1, 1, 1, '/workMessage/index', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (76, 75, '聊天记录查看', 4, '#1#-#74#-#75#-#76#', '', 1, 1, 1, '/workMessage/toUsers', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (77, 74, '敏感词词库', 3, '#1#-#74#-#77#', '', 1, 1, 1, '/sensitiveWords/index', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (78, 77, '查询(按钮)', 4, '#1#-#74#-#77#-#78#', '', 1, 1, 1, '/sensitiveWords/index@search', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (79, 77, '修改分组(按钮)', 4, '#1#-#74#-#77#-#79#', '', 1, 1, 1, '/sensitiveWords/index@edit', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (80, 77, '新建分组(按钮)', 4, '#1#-#74#-#77#-#80#', '', 1, 1, 1, '/sensitiveWords/index@add', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (81, 77, '新建敏感词(按钮)', 4, '#1#-#74#-#77#-#81#', '', 1, 1, 1, '/sensitiveWords/index@addWord', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (82, 77, '关闭(按钮)', 4, '#1#-#74#-#77#-#82#', '', 1, 1, 1, '/sensitiveWords/index@close', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (83, 77, '删除(按钮)', 4, '#1#-#74#-#77#-#83#', '', 1, 1, 1, '/sensitiveWords/index@delete', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (84, 77, '移动(按钮)', 4, '#1#-#74#-#77#-#84#', '', 1, 1, 1, '/sensitiveWords/index@move', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (85, 74, '敏感词监控', 3, '#1#-#74#-#85#', '', 1, 1, 1, '/sensitiveWordsMonitor/index', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (86, 85, '查询(按钮)', 4, '#1#-#74#-#85#-#86#', '', 1, 1, 1, '/sensitiveWordsMonitor/index@search', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (87, 85, '对话详情(按钮)', 4, '#1#-#74#-#85#-#87#', '', 1, 1, 1, '/sensitiveWordsMonitor/index@detail', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (88, 74, '消息存档配置', 3, '#1#-#74#-#88#', '', 1, 1, 1, '/workMessageConfig/corpShow', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (89, 88, '查找(按钮)', 4, '#1#-#74#-#88#-#89#', '', 1, 1, 1, '/workMessageConfig/corpShow@search', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (90, 88, '查看(按钮)', 4, '#1#-#74#-#88#-#90#', '', 1, 1, 1, '/workMessageConfig/corpShow@check', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (91, 88, '列表操作', 4, '#1#-#74#-#88#-#91#', '', 1, 1, 1, '/workMessageConfig/corpIndex#get', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (92, 88, '查看操作', 4, '#1#-#74#-#88#-#92#', '', 1, 1, 1, '/workMessageConfig/corpShow#get', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:08', NULL);
INSERT INTO `mc_rbac_menu` VALUES (93, 88, '保存操作', 4, '#1#-#74#-#88#-#93#', '', 1, 1, 1, '/workMessageConfig/corpStore#post', 2, 0, '系统', 99, '2020-12-31 19:22:08', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (94, 0, '系统设置', 1, '#94#', '', 1, 1, 1, '_baseSysIndex', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (95, 94, '企业管理', 2, '#94#-#95#', 'cluster', 1, 1, 1, '_baseSysConfig', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (96, 95, '成员管理', 3, '#94#-#95#-#96#', '', 1, 1, 1, '/workEmployee/index', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (97, 96, '条件筛选(按钮)', 4, '#94#-#95#-#96#-#97#', '', 1, 1, 1, '/workEmployee/index@search', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (98, 96, '同步企业微信通讯录(按钮)', 4, '#94#-#95#-#96#-#98#', '', 1, 1, 1, '/workEmployee/index@sync', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (99, 95, '账号管理', 3, '#94#-#95#-#99#', '', 1, 1, 1, '/user/index', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (100, 99, '查询(按钮)', 4, '#94#-#95#-#99#-#100#', '', 1, 1, 1, '/user/index@search', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (101, 99, '添加(按钮)', 4, '#94#-#95#-#99#-#101#', '', 1, 1, 1, '/user/index@add', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (102, 99, '修改(按钮)', 4, '#94#-#95#-#99#-#102#', '', 1, 1, 1, '/user/index@edit', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (103, 95, '修改密码', 3, '#94#-#95#-#103#', '', 1, 1, 1, '/passwordUpdate/index', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (104, 103, '保存(按钮)', 4, '#94#-#95#-#103#-#104#', '', 1, 1, 1, '/passwordUpdate/index@save', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (105, 95, '授权管理', 3, '#94#-#95#-#105#', '', 1, 1, 1, '/corp/index', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (106, 105, '查找(按钮)', 4, '#94#-#95#-#105#-#106#', '', 1, 1, 1, '/corp/index@search', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (107, 105, '添加企业微信号(按钮)', 4, '#94#-#95#-#105#-#107#', '', 1, 1, 1, '/corp/index@addwx', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (108, 105, '查看(按钮)', 4, '#94#-#95#-#105#-#108#', '', 1, 1, 1, '/corp/index@check', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (109, 105, '修改(按钮)', 4, '#94#-#95#-#105#-#109#', '', 1, 1, 1, '/corp/index@edit', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (110, 95, '角色管理', 3, '#94#-#95#-#110#', '', 1, 1, 1, '/role/index', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (111, 110, '权限设置', 4, '#94#-#95#-#110#-#111#', '', 1, 1, 1, '/role/permissionShow', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (112, 111, '保存权限(按钮)', 5, '#94#-#95#-#110#-#111#-#112#', '', 1, 1, 1, '/role/permissionShow@save', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (113, 110, '查询(按钮)', 4, '#94#-#95#-#110#-#113#', '', 1, 1, 1, '/role/index@search', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (114, 110, '添加(按钮)', 4, '#94#-#95#-#110#-#114#', '', 1, 1, 1, '/role/index@add', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (115, 110, '编辑(按钮)', 4, '#94#-#95#-#110#-#115#', '', 1, 1, 1, '/role/index@edit', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (116, 110, '复制权限(按钮)', 4, '#94#-#95#-#110#-#116#', '', 1, 1, 1, '/role/index@copy', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (117, 110, '删除(按钮)', 4, '#94#-#95#-#110#-#117#', '', 1, 1, 1, '/role/index@delete', 2, 0, '系统', 99, '2020-12-31 19:22:09', '2020-12-31 19:22:09', NULL);
INSERT INTO `mc_rbac_menu` VALUES (118, 110, '查看角色人员(按钮)', 4, '#94#-#95#-#110#-#118#', '', 1, 1, 1, '/role/index@checkMember', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (119, 110, '启动(按钮)', 4, '#94#-#95#-#110#-#119#', '', 1, 1, 1, '/role/index@use', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (120, 95, '菜单管理', 3, '#94#-#95#-#120#', '', 1, 1, 1, '/menu/index', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (121, 120, '查询(按钮)', 4, '#94#-#95#-#120#-#121#', '', 1, 1, 1, '/menu/index@search', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (122, 120, '添加(按钮)', 4, '#94#-#95#-#120#-#122#', '', 1, 1, 1, '/menu/index@add', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (123, 120, '编辑(按钮)', 4, '#94#-#95#-#120#-#123#', '', 1, 1, 1, '/menu/index@edit', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (124, 95, '组织架构', 3, '#94#-#95#-#124#', '', 1, 1, 1, '/department/index', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (125, 124, '查询(按钮)', 4, '#94#-#95#-#124#-#125#', '', 1, 1, 1, '/department/index@search', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (126, 124, '查看成员(按钮)', 4, '#94#-#95#-#124#-#126#', '', 1, 1, 1, '/department/index@check', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (127, 124, '同步企业微信通讯录(按钮)', 4, '#94#-#95#-#124#-#127#', '', 1, 1, 1, '/department/index@sync', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (128, 20, '素材列表操作', 4, '#1#-#14#-#20#-#128#', '', 1, 1, 2, '/medium/index#get', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (129, 20, '素材详情操作', 4, '#1#-#14#-#20#-#129#', '', 1, 1, 2, '/medium/show#get', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (130, 20, '素材添加操作', 4, '#1#-#14#-#20#-#130#', '', 1, 1, 2, '/medium/store#post', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (131, 20, '素材修改操作', 4, '#1#-#14#-#20#-#131#', '', 1, 1, 2, '/medium/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (132, 20, '素材删除操作', 4, '#1#-#14#-#20#-#132#', '', 1, 1, 2, '/medium/destroy#delete', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (133, 20, '素材分组详情操作', 4, '#1#-#14#-#20#-#133#', '', 1, 1, 2, '/mediumGroup/show#get', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (134, 20, '素材分组列表操作', 4, '#1#-#14#-#20#-#134#', '', 1, 1, 2, '/mediumGroup/index#get', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (135, 20, '素材分组删除操作', 4, '#1#-#14#-#20#-#135#', '', 1, 1, 2, '/mediumGroup/destroy#delete', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (136, 20, '素材分组添加操作', 4, '#1#-#14#-#20#-#136#', '', 1, 1, 2, '/mediumGroup/store#post', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (137, 20, '素材移动分组操作', 4, '#1#-#14#-#20#-#137#', '', 1, 1, 2, '/medium/groupUpdate#put', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (138, 20, '素材分组修改操作', 4, '#1#-#14#-#20#-#138#', '', 1, 1, 2, '/mediumGroup/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (139, 76, '聊天内容列表操作', 5, '#1#-#74#-#75#-#76#-#139#', '', 1, 1, 2, '/workMessage/toUsers#get', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (140, 76, '聊天内容详情操作', 5, '#1#-#74#-#75#-#76#-#140#', '', 1, 1, 2, '/workMessage/index#get', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (141, 75, '聊天配置详情操作', 4, '#1#-#74#-#75#-#141#', '', 1, 1, 2, '/workMessageConfig/stepCreate#get', 2, 0, '系统', 99, '2020-12-31 19:22:10', '2020-12-31 19:22:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (142, 75, '聊天配置编辑操作', 4, '#1#-#74#-#75#-#142#', '', 1, 1, 2, '/workMessageConfig/stepUpdate#put', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (143, 105, '企业授信列表操作', 4, '#94#-#95#-#105#-#143#', '', 1, 1, 2, '/corp/index#get', 1, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (144, 105, '新建企业微信授信操作', 4, '#94#-#95#-#105#-#144#', '', 1, 1, 2, '/corp/store#post', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (145, 105, '查看企业授信操作', 4, '#94#-#95#-#105#-#145#', '', 1, 1, 2, '/corp/show#get', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (146, 105, '更新企业微信授信操作', 4, '#94#-#95#-#105#-#146#', '', 1, 1, 2, '/corp/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (147, 99, '子账户列表操作', 4, '#94#-#95#-#99#-#147#', '', 1, 1, 2, '/user/index#get', 1, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (148, 99, '子账户详情操作', 4, '#94#-#95#-#99#-#148#', '', 1, 1, 2, '/user/show#get', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (149, 99, '新建子账户操作', 4, '#94#-#95#-#99#-#149#', '', 1, 1, 2, '/user/store#post', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (150, 99, '更新子账户操作', 4, '#94#-#95#-#99#-#150#', '', 1, 1, 2, '/user/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (151, 56, '统计分页操作', 5, '#1#-#54#-#55#-#56#-#151#', '', 1, 1, 2, '/workRoom/statisticsIndex#get', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (152, 56, '统计折线图操作', 5, '#1#-#54#-#55#-#56#-#152#', '', 1, 1, 2, '/workRoom/statistics#get', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (153, 55, '客户群列表操作', 4, '#1#-#54#-#55#-#153#', '', 1, 1, 2, '/workRoom/index#get', 1, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (154, 55, '同步群操作', 4, '#1#-#54#-#55#-#154#', '', 1, 1, 2, '/workRoom/syn#put', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (155, 55, '群成员操作', 4, '#1#-#54#-#55#-#155#', '', 1, 1, 2, '/workContactRoom/index#get', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (156, 55, '批量修改分组操作', 4, '#1#-#54#-#55#-#156#', '', 1, 1, 2, '/workRoom/batchUpdate#put', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (157, 65, '新建自动拉群操作', 5, '#1#-#54#-#64#-#65#-#157#', '', 1, 1, 2, '/workRoomAutoPull/store#post', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (158, 64, '自动拉群列表操作', 4, '#1#-#54#-#64#-#158#', '', 1, 1, 2, '/workRoomAutoPull/index#get', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (159, 64, '自动拉群详情操作', 4, '#1#-#54#-#64#-#159#', '', 1, 1, 2, '/workRoomAutoPull/show#get', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (160, 64, '更新自动拉群操作', 4, '#1#-#54#-#64#-#160#', '', 1, 1, 2, '/workRoomAutoPull/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (161, 85, '敏感词监控列表操作', 4, '#1#-#74#-#85#-#161#', '', 1, 1, 2, '/sensitiveWordsMonitor/index#get', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (162, 85, '敏感词监控详情操作', 4, '#1#-#74#-#85#-#162#', '', 1, 1, 2, '/sensitiveWordsMonitor/show#get', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (163, 15, '欢迎语列表操作', 4, '#1#-#14#-#15#-#163#', '', 1, 1, 2, '/greeting/index#get', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (164, 15, '欢迎语详情操作', 4, '#1#-#14#-#15#-#164#', '', 1, 1, 2, '/greeting/show#get', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (165, 15, '更新欢迎语操作', 4, '#1#-#14#-#15#-#165#', '', 1, 1, 2, '/greeting/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (166, 15, '删除欢迎语操作', 4, '#1#-#14#-#15#-#166#', '', 1, 1, 2, '/greeting/destroy#delete', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (167, 16, '创建欢迎语操作', 5, '#1#-#14#-#15#-#16#-#167#', '', 1, 1, 2, '/greeting/store#post', 2, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:11', NULL);
INSERT INTO `mc_rbac_menu` VALUES (168, 28, '客户列表操作', 4, '#1#-#27#-#28#-#168#', '', 1, 1, 2, '/workContact/index#get', 1, 0, '系统', 99, '2020-12-31 19:22:11', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (169, 28, '同步客户操作', 4, '#1#-#27#-#28#-#169#', '', 1, 1, 2, '/workContact/synContact#put', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (170, 29, '查看详情操作', 5, '#1#-#27#-#28#-#29#-#170#', '', 1, 1, 2, '/workContact/show#get', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (171, 29, '编辑基础信息操作', 5, '#1#-#27#-#28#-#29#-#171#', '', 1, 1, 2, '/workContact/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (172, 29, '互动轨迹操作', 5, '#1#-#27#-#28#-#29#-#172#', '', 1, 1, 2, '/workContact/track#get', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (173, 29, '用户画像操作', 5, '#1#-#27#-#28#-#29#-#173#', '', 1, 1, 2, '/contactFieldPivot/index#get', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (174, 29, '编辑用户画像操作', 5, '#1#-#27#-#28#-#29#-#174#', '', 1, 1, 2, '/contactFieldPivot/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (175, 36, '高级属性列表操作', 4, '#1#-#27#-#36#-#175#', '', 1, 1, 2, '/contactField/index#get', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (176, 36, '新增属性操作', 4, '#1#-#27#-#36#-#176#', '', 1, 1, 2, '/contactField/store#post', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (177, 36, '批量修改操作', 4, '#1#-#27#-#36#-#177#', '', 1, 1, 2, '/contactField/batchUpdate#put', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (178, 36, '编辑操作', 4, '#1#-#27#-#36#-#178#', '', 1, 1, 2, '/contactField/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (179, 36, '删除操作', 4, '#1#-#27#-#36#-#179#', '', 1, 1, 2, '/contactField/destroy#delete', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (180, 36, '关闭操作', 4, '#1#-#27#-#36#-#180#', '', 1, 1, 2, '/contactField/statusUpdate#put', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (181, 43, '流失客户操作', 4, '#1#-#27#-#43#-#181#', '', 1, 1, 2, '/workContact/lossContact#get', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (182, 45, '同步企业微信标签操作', 4, '#1#-#27#-#45#-#182#', '', 1, 1, 2, '/workContactTag/synContactTag#put', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (183, 45, '标签操作', 4, '#1#-#27#-#45#-#183#', '', 1, 1, 2, '/workContactTag/destroy#delete', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (184, 3, '渠道码列表操作', 4, '#1#-#2#-#3#-#184#', '', 1, 1, 2, '/channelCode/index#get', 1, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (185, 3, '渠道码详情操作', 4, '#1#-#2#-#3#-#185#', '', 1, 1, 2, '/channelCode/show#get', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (186, 3, '编辑渠道码操作', 4, '#1#-#2#-#3#-#186#', '', 1, 1, 2, '/channelCode/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (187, 3, '客户操作', 4, '#1#-#2#-#3#-#187#', '', 1, 1, 2, '/channelCode/contact#get', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (188, 4, '新建渠道码操作', 5, '#1#-#2#-#3#-#4#-#188#', '', 1, 1, 2, '/channelCode/store#post', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (189, 6, '统计分页数据操作', 5, '#1#-#2#-#3#-#6#-#189#', '', 1, 1, 2, '/channelCode/statisticsIndex#get', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (190, 6, '统计折线图操作', 5, '#1#-#2#-#3#-#6#-#190#', '', 1, 1, 2, '/channelCode/statistics#get', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (191, 124, '部门列表操作', 4, '#94#-#95#-#124#-#191#', '', 1, 1, 1, '/workDepartment/pageIndex#get', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (192, 124, '查看成员操作', 4, '#94#-#95#-#124#-#192#', '', 1, 1, 1, '/workDepartment/showEmployee#get', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:12', NULL);
INSERT INTO `mc_rbac_menu` VALUES (193, 110, '查看人员操作', 4, '#94#-#95#-#110#-#193#', '', 1, 1, 1, '/role/showEmployee#get', 2, 0, '系统', 99, '2020-12-31 19:22:12', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (194, 110, '角色状态修改操作', 4, '#94#-#95#-#110#-#194#', '', 1, 1, 1, '/role/statusUpdate#put', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (195, 110, '角色列表操作', 4, '#94#-#95#-#110#-#195#', '', 1, 1, 1, '/role/index#get', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (196, 110, '角色添加操作', 4, '#94#-#95#-#110#-#196#', '', 1, 1, 1, '/role/store#post', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (197, 110, '角色编辑操作', 4, '#94#-#95#-#110#-#197#', '', 1, 1, 1, '/role/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (198, 110, '角色详情操作', 4, '#94#-#95#-#110#-#198#', '', 1, 1, 1, '/role/show#get', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (199, 110, '角色删除操作', 4, '#94#-#95#-#110#-#199#', '', 1, 1, 1, '/role/destroy#delete', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (200, 111, '个人菜单列表操作', 5, '#94#-#95#-#110#-#111#-#200#', '', 1, 1, 1, '/role/permissionByUser#get', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (201, 111, '权限查看操作', 5, '#94#-#95#-#110#-#111#-#201#', '', 1, 1, 1, '/role/permissionShow#get', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (202, 111, '权限保存操作', 5, '#94#-#95#-#110#-#111#-#202#', '', 1, 1, 1, '/role/permissionStore#post', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (203, 120, '菜单列表操作', 4, '#94#-#95#-#120#-#203#', '', 1, 1, 1, '/menu/index#get', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (204, 120, '菜单添加操作', 4, '#94#-#95#-#120#-#204#', '', 1, 1, 1, '/menu/store#post', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (205, 120, '菜单修改操作', 4, '#94#-#95#-#120#-#205#', '', 1, 1, 1, '/menu/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (206, 120, '菜单详情操作', 4, '#94#-#95#-#120#-#206#', '', 1, 1, 1, '/menu/show#get', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (207, 120, '菜单移除操作', 4, '#94#-#95#-#120#-#207#', '', 1, 1, 1, '/menu/destroy#delete', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (208, 120, '菜单状态操作操作', 4, '#94#-#95#-#120#-#208#', '', 1, 1, 1, '/menu/statusUpdate#put', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (209, 77, '敏感词词库列表操作', 4, '#1#-#74#-#77#-#209#', '', 1, 1, 1, '/sensitiveWord/index#get', 1, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (210, 77, '敏感词添加操作', 4, '#1#-#74#-#77#-#210#', '', 1, 1, 1, '/sensitiveWord/store#post', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (211, 77, '敏感词删除操作', 4, '#1#-#74#-#77#-#211#', '', 1, 1, 1, '/sensitiveWord/destroy#delete', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (212, 77, '敏感词移动操作', 4, '#1#-#74#-#77#-#212#', '', 1, 1, 1, '/sensitiveWord/move#put', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (213, 77, '敏感词关闭操作', 4, '#1#-#74#-#77#-#213#', '', 1, 1, 1, '/sensitiveWord/statusUpdate#put', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (214, 77, '敏感词分组添加操作', 4, '#1#-#74#-#77#-#214#', '', 1, 1, 1, '/sensitiveWordGroup/store#post', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (215, 77, '敏感词分组修改操作', 4, '#1#-#74#-#77#-#215#', '', 1, 1, 1, '/sensitiveWordGroup/update#put', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:13', NULL);
INSERT INTO `mc_rbac_menu` VALUES (216, 96, '通讯录列表操作', 4, '#94#-#95#-#96#-#216#', '', 1, 1, 1, '/workEmployee/index#get', 2, 0, '系统', 99, '2020-12-31 19:22:13', '2020-12-31 19:22:14', NULL);
INSERT INTO `mc_rbac_menu` VALUES (217, 96, '通讯录同步操作', 4, '#94#-#95#-#96#-#217#', '', 1, 1, 1, '/workEmployee/synEmployee#put', 2, 0, '系统', 99, '2020-12-31 19:22:14', '2020-12-31 19:22:14', NULL);
INSERT INTO `mc_rbac_menu` VALUES (218, 1, '系统首页', 2, '#1#-#218#', 'home', 1, 1, 1, 'path/1610968617', 1, 1, '系统', 98, '2020-12-31 19:22:04', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (219, 218, '系统首页', 3, '#1#-#218#-#219#', '', 1, 1, 1, '/corpData/index', 2, 1, '系统', 98, '2020-12-31 19:22:04', '2020-12-31 19:22:05', NULL);
INSERT INTO `mc_rbac_menu` VALUES (220, 71, '用户画像', 3, '#1#-#71#-#220#', '', 1, 1, 1, '/chatTool/customer', 2, 1, '', 99, '2021-02-05 11:35:55', '2021-02-05 11:35:55', NULL);
INSERT INTO `mc_rbac_menu` VALUES (221, 71, '聊天增强', 3, '#1#-#71#-#221#', '', 1, 1, 1, '/chatTool/enhance', 2, 1, '', 99, '2021-02-05 11:36:44', '2021-02-05 11:36:45', NULL);
INSERT INTO `mc_rbac_menu` VALUES (222, 14, '用户搜索添加', 3, '#1#-#14#-#222#', '', 1, 1, 1, '/greeting/userSearch', 2, 1, '', 99, '2021-02-05 11:38:10', '2021-02-05 11:38:10', NULL);
INSERT INTO `mc_rbac_menu` VALUES (223, 71, '用户画像', 3, '#1#-#71#-#220#', '', 1, 1, 1, '/chatTool/customer', 2, 1, '', 99, '2021-02-05 11:35:55', '2021-02-05 11:35:55', NULL);
INSERT INTO `mc_rbac_menu` VALUES (224, 71, '聊天增强', 3, '#1#-#71#-#221#', '', 1, 1, 1, '/chatTool/enhance', 2, 1, '', 99, '2021-02-05 11:36:44', '2021-02-05 11:36:45', NULL);
INSERT INTO `mc_rbac_menu` VALUES (225, 14, '用户搜索添加', 3, '#1#-#14#-#222#', '', 1, 1, 1, '/greeting/userSearch', 2, 1, '', 99, '2021-02-05 11:38:10', '2021-02-05 11:38:10', NULL);
COMMIT;

-- ----------------------------
-- Table structure for mc_rbac_role
-- ----------------------------
DROP TABLE IF EXISTS `mc_rbac_role`;
CREATE TABLE `mc_rbac_role` (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `tenant_id` int(11) NOT NULL COMMENT '租户ID【mc_tenant.id】',
                                `name` varchar(25) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '角色名称',
                                `remarks` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '角色描述',
                                `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态【1-启动(默认)2-禁用】',
                                `operate_id` int(11) NOT NULL COMMENT '操作人ID【mc_user.id】',
                                `operate_name` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '操作人ID【mc_user.name】',
                                `data_permission` json DEFAULT NULL COMMENT '企业部门数据权限，例子[{`corpId`: `1`, `permissionType`: 1}] // 1-是(所选择企业)本用户部门 2-否 （本用户）\n',
                                `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                                `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ----------------------------
-- Records of mc_rbac_role
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_rbac_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `mc_rbac_role_menu`;
CREATE TABLE `mc_rbac_role_menu` (
                                     `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `role_id` int(11) NOT NULL DEFAULT '0' COMMENT '角色ID【mc_rbac_role.id】',
                                     `menu_id` int(11) NOT NULL DEFAULT '0' COMMENT '菜单ID【mc_rbac_menu.id】',
                                     `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色-权限对应表';

-- ----------------------------
-- Records of mc_rbac_role_menu
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_rbac_user_role
-- ----------------------------
DROP TABLE IF EXISTS `mc_rbac_user_role`;
CREATE TABLE `mc_rbac_user_role` (
                                     `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID【mc_user.id】',
                                     `role_id` int(11) NOT NULL DEFAULT '0' COMMENT '角色ID【mc_rbac_role.id】',
                                     `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                                     `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ----------------------------
-- Records of mc_rbac_user_role
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_sensitive_word
-- ----------------------------
DROP TABLE IF EXISTS `mc_sensitive_word`;
CREATE TABLE `mc_sensitive_word` (
                                     `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                     `corp_id` int(11) NOT NULL DEFAULT '0' COMMENT '企业id',
                                     `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '敏感词名称',
                                     `group_id` int(11) NOT NULL DEFAULT '0' COMMENT '智能风控分组id（mc_sensitive_word_group.id）',
                                     `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（1-开启，2-关闭）',
                                     `employee_num` tinyint(4) NOT NULL DEFAULT '0' COMMENT '员工触发次数',
                                     `contact_num` tinyint(4) NOT NULL DEFAULT '0' COMMENT '客户触发次数',
                                     `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                     `deleted_at` timestamp NULL DEFAULT NULL,
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能风控-敏感词词库表';

-- ----------------------------
-- Records of mc_sensitive_word
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_sensitive_word_group
-- ----------------------------
DROP TABLE IF EXISTS `mc_sensitive_word_group`;
CREATE TABLE `mc_sensitive_word_group` (
                                           `id` int(10) NOT NULL AUTO_INCREMENT,
                                           `corp_id` int(11) NOT NULL DEFAULT '0' COMMENT '企业id',
                                           `user_id` int(11) NOT NULL COMMENT '用户id(mc_user.id)',
                                           `employee_id` int(11) NOT NULL COMMENT '员工id （mc_work_employee.id)',
                                           `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '分组名称',
                                           `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                           `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                           `deleted_at` timestamp NULL DEFAULT NULL,
                                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能风控-分组表';

-- ----------------------------
-- Records of mc_sensitive_word_group
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_sensitive_word_monitor
-- ----------------------------
DROP TABLE IF EXISTS `mc_sensitive_word_monitor`;
CREATE TABLE `mc_sensitive_word_monitor` (
                                             `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                             `corp_id` int(11) NOT NULL DEFAULT '0' COMMENT '企业id',
                                             `sensitive_word_id` int(11) NOT NULL DEFAULT '0' COMMENT '敏感词词库表id(mc_sensitive_word.id)',
                                             `sensitive_word_name` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '敏感词词库表名称(mc_sensitive_word.name)',
                                             `source` tinyint(4) NOT NULL DEFAULT '0' COMMENT '触发来源【1-客户2-员工】',
                                             `trigger_id` int(11) NOT NULL DEFAULT '0' COMMENT '触发人id',
                                             `trigger_name` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '触发人名称',
                                             `trigger_time` timestamp NULL DEFAULT NULL COMMENT '触发时间',
                                             `receiver_type` tinyint(4) NOT NULL COMMENT '接收者类型【1-成员2-外部联系人3-群聊】',
                                             `receiver_id` int(11) NOT NULL DEFAULT '0' COMMENT '接收者ID',
                                             `receiver_name` varchar(100) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '接收者名称',
                                             `work_message_id` int(11) NOT NULL DEFAULT '0' COMMENT '触发消息ID【mc_work_message.id】',
                                             `chat_content` json DEFAULT NULL COMMENT '会话内容',
                                             `created_at` timestamp NULL DEFAULT NULL,
                                             `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                             `deleted_at` timestamp NULL DEFAULT NULL,
                                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能风控-敏感词监控表';

-- ----------------------------
-- Records of mc_sensitive_word_monitor
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_sys_log
-- ----------------------------
DROP TABLE IF EXISTS `mc_sys_log`;
CREATE TABLE `mc_sys_log` (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `url_path` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '请求链接【/user/create】',
                              `method` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求方法【get|post|put】',
                              `query` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'GET参数',
                              `body` json NOT NULL COMMENT 'body参数',
                              `menu_id` int(11) NOT NULL COMMENT '菜单ID【mc_rbac_menu.id】',
                              `menu_name` varchar(50) CHARACTER SET utf8 NOT NULL COMMENT '菜单名称【mc_rbac_menu.name】',
                              `operate_id` int(11) NOT NULL DEFAULT '0' COMMENT '操作人ID【mc_user.id】',
                              `operate_name` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '操作人姓名【mc_user.name】',
                              `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                              `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                              `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志';

-- ----------------------------
-- Records of mc_sys_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_tenant
-- ----------------------------
DROP TABLE IF EXISTS `mc_tenant`;
CREATE TABLE `mc_tenant` (
                             `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `name` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '租户名称',
                             `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '租户状态[1-正常2-停用]',
                             `logo` varchar(100) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '租户Logo地址',
                             `login_background` varchar(100) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '登录页背景图地址',
                             `url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '网站地址',
                             `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
                             `copyright` varchar(100) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '租户版权',
                             `server_ips` json DEFAULT NULL COMMENT '服务器IPs',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- ----------------------------
-- Records of mc_tenant
-- ----------------------------
BEGIN;
INSERT INTO `mc_tenant` VALUES (1, '', 1, '', '', '', '2020-11-04 17:12:26', '2020-11-05 18:14:23', NULL, '', '[]');
COMMIT;

-- ----------------------------
-- Table structure for mc_user
-- ----------------------------
DROP TABLE IF EXISTS `mc_user`;
CREATE TABLE `mc_user` (
                           `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                           `phone` char(11) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '手机号',
                           `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '密码',
                           `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '姓名',
                           `gender` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '性别 0未定义 1男 2女',
                           `department` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '部门',
                           `position` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '职务',
                           `login_time` timestamp NULL DEFAULT NULL COMMENT '上一次登陆时间',
                           `status` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '状态 0未启用 1正常 2禁用',
                           `tenant_id` int(11) NOT NULL DEFAULT '1' COMMENT '租户ID(mc_tenant.id)',
                           `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                           `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                           `deleted_at` timestamp NULL DEFAULT NULL,
                           `isSuperAdmin` tinyint(1) DEFAULT '0' COMMENT '是否为超级管理员 - 0否1是',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='(子账户)系统管理员';

-- ----------------------------
-- Records of mc_user
-- ----------------------------
BEGIN;
INSERT INTO `mc_user` VALUES (1, '18888888888', '$2y$10$KAyFrpuELdoLeObnl/cZ5.OUjiKb96zCA/Sr8sbTFohPp5Aa.xQ32', '刘小二', 1, '', '', '2021-03-31 17:33:16', 1, 1, '2020-11-04 17:00:28', '2021-03-23 14:18:23', NULL, 1);
COMMIT;

-- ----------------------------
-- Table structure for mc_work_agent
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_agent`;
CREATE TABLE `mc_work_agent` (
                                 `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                 `corp_id` int(11) NOT NULL COMMENT '企业ID',
                                 `wx_agent_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信应用ID',
                                 `wx_secret` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信应用secret',
                                 `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '应用名称',
                                 `square_logo_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '应用方形头像',
                                 `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '应用详情',
                                 `close` tinyint(4) NOT NULL DEFAULT '0' COMMENT '应用是否被停用 0否1是',
                                 `redirect_domain` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '应用可信域名',
                                 `report_location_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '应用是否打开地理位置上报 0：不上报；1：进入会话上报；',
                                 `is_reportenter` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否上报用户进入应用事件。0：不接收；1：接收',
                                 `home_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '应用主页url',
                                 `type` tinyint(4) NOT NULL COMMENT '应用类型 1-侧边栏 2-会话消息 3-工作台',
                                 `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `updated_at` timestamp NULL DEFAULT NULL,
                                 `deleted_at` timestamp NULL DEFAULT NULL,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业应用表';

-- ----------------------------
-- Records of mc_work_agent
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_contact
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_contact`;
CREATE TABLE `mc_work_contact` (
                                   `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                   `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID（mc_crop.id）',
                                   `wx_external_userid` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '外部联系人external_userid',
                                   `name` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '外部联系人姓名',
                                   `nick_name` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '外部联系人昵称',
                                   `avatar` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '外部联系人的头像',
                                   `follow_up_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '跟进状态（1.未跟进 2.跟进中 3.已拒绝 4.已成交 5.已复购）',
                                   `type` tinyint(4) unsigned NOT NULL DEFAULT '1' COMMENT '外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户',
                                   `gender` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '外部联系人性别 0-未知 1-男性 2-女性',
                                   `unionid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '外部联系人在微信开放平台的唯一身份标识（微信unionid）',
                                   `position` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '外部联系人的职位，如果外部企业或用户选择隐藏职位，则不返回，仅当联系人类型是企业微信用户时有此字段\n',
                                   `corp_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '外部联系人所在企业的简称，仅当联系人类型是企业微信用户时有此字段\n',
                                   `corp_full_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '外部联系人所在企业的主体名称',
                                   `external_profile` json DEFAULT NULL COMMENT '外部联系人的自定义展示信息',
                                   `business_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '外部联系人编号',
                                   `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                   `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                   `deleted_at` timestamp NULL DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='联系人表（客户列表）';

-- ----------------------------
-- Records of mc_work_contact
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_contact_employee
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_contact_employee`;
CREATE TABLE `mc_work_contact_employee` (
                                            `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                            `employee_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '通讯录表ID（work_employee.id）',
                                            `contact_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '客户表ID（work_contact.id）',
                                            `remark` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '员工对此外部联系人的备注',
                                            `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '员工对此外部联系人的描述',
                                            `remark_corp_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '员工对此客户备注的企业名称',
                                            `remark_mobiles` json DEFAULT NULL COMMENT '员工对此客户备注的手机号码',
                                            `add_way` int(11) unsigned NOT NULL COMMENT '表示添加客户的来源\n0\n未知来源\n1\n扫描二维码\n2\n搜索手机号\n3\n名片分享\n4\n群聊\n5\n手机通讯录\n6\n微信联系人\n7\n来自微信的添加好友申请\n8\n安装第三方应用时自动添加的客服人员\n9\n搜索邮箱\n201\n内部成员共享\n202\n管理员/负责人分配\n',
                                            `oper_userid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '发起添加的userid，如果成员主动添加，为成员的userid；如果是客户主动添加，则为客户的外部联系人userid；如果是内部成员共享/管理员分配，则为对应的成员/管理员userid\n',
                                            `state` varchar(30) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '企业自定义的state参数，用于区分客户具体是通过哪个「联系我」添加，由企业通过创建「联系我」方式指定\n',
                                            `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID（corp.id）',
                                            `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1.正常 2.删除 3.拉黑',
                                            `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '员工添加此外部联系人的时间',
                                            `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                            `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                            `deleted_at` timestamp NULL DEFAULT NULL,
                                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通讯录 - 客户 中间表';

-- ----------------------------
-- Records of mc_work_contact_employee
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_contact_room
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_contact_room`;
CREATE TABLE `mc_work_contact_room` (
                                        `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                        `wx_user_id` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT '',
                                        `contact_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '客户表id（work_contact.id）',
                                        `employee_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '员工ID (work_employee.id)',
                                        `unionid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '仅当群成员类型是微信用户（包括企业成员未添加好友），且企业或第三方服务商绑定了微信开发者ID有此字段',
                                        `room_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '客户群表id（work_room.id）',
                                        `join_scene` tinyint(1) unsigned DEFAULT '3' COMMENT '入群方式1 - 由成员邀请入群（直接邀请入群）2 - 由成员邀请入群（通过邀请链接入群）3 - 通过扫描群二维码入群\n1 - 由成员邀请入群（直接邀请入群）\n2 - 由成员邀请入群（通过邀请链接入群）\n3 - 通过扫描群二维码入群',
                                        `type` tinyint(4) unsigned NOT NULL DEFAULT '1' COMMENT '成员类型（1 - 企业成员 2 - 外部联系人）\n1 - 企业成员\n2 - 外部联系人',
                                        `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '成员状态。1 - 正常2 -退群',
                                        `join_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入群时间',
                                        `out_time` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '退群时间',
                                        `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                        `deleted_at` timestamp NULL DEFAULT NULL,
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户 - 客户群 关联表';

-- ----------------------------
-- Records of mc_work_contact_room
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_contact_tag
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_contact_tag`;
CREATE TABLE `mc_work_contact_tag` (
                                       `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '企业标签ID',
                                       `wx_contact_tag_id` varchar(60) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '微信企业标签ID',
                                       `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID （mc_corp.id）',
                                       `name` varchar(30) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '标签名称',
                                       `order` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '排序',
                                       `contact_tag_group_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '客户标签分组ID（mc_work_contract_tag_group.id）',
                                       `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                       `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                       `deleted_at` timestamp NULL DEFAULT NULL,
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户标签';

-- ----------------------------
-- Records of mc_work_contact_tag
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_contact_tag_group
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_contact_tag_group`;
CREATE TABLE `mc_work_contact_tag_group` (
                                             `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                             `wx_group_id` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信企业标签分组ID',
                                             `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID （mc_corp.id）',
                                             `group_name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客户标签分组名称',
                                             `order` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '排序',
                                             `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                             `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                             `deleted_at` timestamp NULL DEFAULT NULL,
                                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户标签 - 分组';

-- ----------------------------
-- Records of mc_work_contact_tag_group
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_contact_tag_pivot
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_contact_tag_pivot`;
CREATE TABLE `mc_work_contact_tag_pivot` (
                                             `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                             `contact_id` int(11) unsigned NOT NULL COMMENT '客户表ID（work_contact.id）',
                                             `employee_id` int(11) NOT NULL DEFAULT '0' COMMENT '员工表id（work_employee.id）',
                                             `contact_tag_id` int(11) unsigned NOT NULL COMMENT '客户标签表ID（work_contact_tag.id）',
                                             `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '该成员添加此外部联系人所打标签类型, 1-企业设置, 2-用户自定义',
                                             `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                             `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                             `deleted_at` timestamp NULL DEFAULT NULL,
                                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户-标签关联表';

-- ----------------------------
-- Records of mc_work_contact_tag_pivot
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_department
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_department`;
CREATE TABLE `mc_work_department` (
                                      `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '部门ID',
                                      `wx_department_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '微信部门自增ID',
                                      `corp_id` int(11) unsigned NOT NULL COMMENT '企业表ID（mc_corp.id）',
                                      `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '部门名称',
                                      `parent_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '父部门ID',
                                      `wx_parentid` int(11) unsigned NOT NULL COMMENT '微信父部门ID',
                                      `order` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '排序',
                                      `level` tinyint(4) NOT NULL DEFAULT '0' COMMENT '部门级别',
                                      `path` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT ' ' COMMENT '父ID路径【#id#-#id#】',
                                      `created_at` timestamp NULL DEFAULT NULL,
                                      `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                      `deleted_at` timestamp NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='(通讯录)部门管理';

-- ----------------------------
-- Records of mc_work_department
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_employee
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_employee`;
CREATE TABLE `mc_work_employee` (
                                    `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                    `wx_user_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'wx.userId',
                                    `corp_id` int(11) NOT NULL DEFAULT '0' COMMENT '所属企业corpid（mc_corp.id）',
                                    `name` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '名称',
                                    `mobile` char(11) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '手机号',
                                    `position` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '职位信息',
                                    `gender` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '性别。0表示未定义，1表示男性，2表示女性',
                                    `email` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '邮箱',
                                    `avatar` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '头像url',
                                    `thumb_avatar` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '头像缩略图',
                                    `telephone` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '座机',
                                    `alias` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '别名',
                                    `extattr` json DEFAULT NULL COMMENT '扩展属性',
                                    `status` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业',
                                    `qr_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '员工二维码',
                                    `external_profile` json DEFAULT NULL COMMENT '员工对外属性',
                                    `external_position` varchar(32) CHARACTER SET utf8 DEFAULT '' COMMENT '员工对外职位',
                                    `address` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '地址',
                                    `open_user_id` char(64) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '全局唯一id',
                                    `wx_main_department_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '微信端主部门ID',
                                    `main_department_id` int(11) NOT NULL DEFAULT '0' COMMENT '主部门id(mc_work_department.id)',
                                    `log_user_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '子账户ID(mc_user.id)',
                                    `contact_auth` tinyint(1) NOT NULL DEFAULT '2' COMMENT '是否配置外部联系人权限（1.是 2.否）',
                                    `created_at` timestamp NULL DEFAULT NULL,
                                    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                    `deleted_at` timestamp NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业通讯录';

-- ----------------------------
-- Records of mc_work_employee
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_employee_department
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_employee_department`;
CREATE TABLE `mc_work_employee_department` (
                                               `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                               `employee_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '通讯录员工(mc_work_department.id)',
                                               `department_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '通讯录部门ID (mc_work_department.id)',
                                               `is_leader_in_dept` tinyint(2) NOT NULL DEFAULT '0' COMMENT '所在的部门内是否为上级',
                                               `order` int(10) NOT NULL DEFAULT '0' COMMENT '排序',
                                               `created_at` timestamp NULL DEFAULT NULL,
                                               `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                               `deleted_at` timestamp NULL DEFAULT NULL,
                                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='(通讯录 - 通讯录部门)中间表';

-- ----------------------------
-- Records of mc_work_employee_department
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_employee_statistic
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_employee_statistic`;
CREATE TABLE `mc_work_employee_statistic` (
                                              `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                              `corp_id` int(10) DEFAULT NULL,
                                              `employee_id` int(10) NOT NULL COMMENT '成员id (mc_work_employee.id)',
                                              `new_apply_cnt` int(10) NOT NULL COMMENT '发起申请数成员通过「搜索手机号」、「扫一扫」、「从微信好友中添加」、「从群聊中添加」、「添加共享、分配给我的客户」、「添加单向、双向删除好友关系的好友」、「从新的联系人推荐中添加」等渠道主动向客户发起的好友申请数量',
                                              `new_contact_cnt` int(10) NOT NULL COMMENT '新增客户数',
                                              `chat_cnt` int(10) NOT NULL COMMENT '聊天总数',
                                              `message_cnt` int(10) NOT NULL COMMENT '发送消息数',
                                              `reply_percentage` int(10) NOT NULL COMMENT '已回复聊天占比',
                                              `avg_reply_time` int(10) NOT NULL COMMENT '平均首次回复时长',
                                              `negative_feedback_cnt` int(10) NOT NULL COMMENT '删除/拉黑成员的客户数',
                                              `syn_time` timestamp NULL DEFAULT NULL COMMENT '同步时间',
                                              `created_at` timestamp NULL DEFAULT NULL,
                                              `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                              `deleted_at` timestamp NULL DEFAULT NULL,
                                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成员统计表';

-- ----------------------------
-- Records of mc_work_employee_statistic
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_employee_tag
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_employee_tag`;
CREATE TABLE `mc_work_employee_tag` (
                                        `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                                        `wx_tagid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '微信通许录标签 id',
                                        `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID（mc_corp.id）',
                                        `tag_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '标签名称',
                                        `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                        `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                        `deleted_at` timestamp NULL DEFAULT NULL,
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='(通讯录)标签';

-- ----------------------------
-- Records of mc_work_employee_tag
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_employee_tag_pivot
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_employee_tag_pivot`;
CREATE TABLE `mc_work_employee_tag_pivot` (
                                              `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                              `employee_id` int(11) unsigned NOT NULL COMMENT '通讯录员工ID',
                                              `tag_id` int(11) unsigned NOT NULL COMMENT 'wx标签ID',
                                              `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                              `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='(通讯录 - 标签)中间表';

-- ----------------------------
-- Records of mc_work_employee_tag_pivot
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_message_1
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_message_1`;
CREATE TABLE `mc_work_message_1` (
                                     `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                     `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID （mc_corp.id）',
                                     `seq` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息的seq值，标识消息的序号',
                                     `msg_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息唯一标识',
                                     `action` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '消息动作，0.send(发送消息) 1.recall(撤回消息) 2.switch(切换企业日志)',
                                     `from` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息发送方id。同一企业内容为userid，非相同企业/机器人为external_userid',
                                     `tolist` json NOT NULL COMMENT '消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid',
                                     `tolist_id` json DEFAULT NULL COMMENT '接收方ID',
                                     `tolist_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接收方类型 0通讯录 1外部联系人 2群',
                                     `msg_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '文本消息类型，包括text、image、...',
                                     `content` json DEFAULT NULL COMMENT '文本内容：详细见wx文档',
                                     `msg_time` char(13) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '消息发送时间戳，utc时间，ms单位',
                                     `updated_at` timestamp NULL DEFAULT NULL,
                                     `deleted_at` timestamp NULL DEFAULT NULL,
                                     `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     `wx_room_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信群id。如果是单聊则为空',
                                     `room_id` int(11) DEFAULT '0' COMMENT '群ID',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `msgid` (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话内容存档';

-- ----------------------------
-- Records of mc_work_message_1
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_message_10
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_message_10`;
CREATE TABLE `mc_work_message_10` (
                                      `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                      `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID （mc_corp.id）',
                                      `seq` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息的seq值，标识消息的序号',
                                      `msg_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息唯一标识',
                                      `action` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '消息动作，0.send(发送消息) 1.recall(撤回消息) 2.switch(切换企业日志)',
                                      `from` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息发送方id。同一企业内容为userid，非相同企业/机器人为external_userid',
                                      `tolist` json NOT NULL COMMENT '消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid',
                                      `tolist_id` json DEFAULT NULL COMMENT '接收方ID',
                                      `tolist_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接收方类型 0通讯录 1外部联系人 2群',
                                      `msg_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '文本消息类型，包括text、image、...',
                                      `content` json DEFAULT NULL COMMENT '文本内容：详细见wx文档',
                                      `msg_time` char(13) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '消息发送时间戳，utc时间，ms单位',
                                      `updated_at` timestamp NULL DEFAULT NULL,
                                      `deleted_at` timestamp NULL DEFAULT NULL,
                                      `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                      `wx_room_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信群id。如果是单聊则为空',
                                      `room_id` int(11) DEFAULT '0' COMMENT '群ID',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `msgid` (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话内容存档';

-- ----------------------------
-- Records of mc_work_message_10
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_message_2
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_message_2`;
CREATE TABLE `mc_work_message_2` (
                                     `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                     `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID （mc_corp.id）',
                                     `seq` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息的seq值，标识消息的序号',
                                     `msg_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息唯一标识',
                                     `action` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '消息动作，0.send(发送消息) 1.recall(撤回消息) 2.switch(切换企业日志)',
                                     `from` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息发送方id。同一企业内容为userid，非相同企业/机器人为external_userid',
                                     `tolist` json NOT NULL COMMENT '消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid',
                                     `tolist_id` json DEFAULT NULL COMMENT '接收方ID',
                                     `tolist_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接收方类型 0通讯录 1外部联系人 2群',
                                     `msg_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '文本消息类型，包括text、image、...',
                                     `content` json DEFAULT NULL COMMENT '文本内容：详细见wx文档',
                                     `msg_time` char(13) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '消息发送时间戳，utc时间，ms单位',
                                     `updated_at` timestamp NULL DEFAULT NULL,
                                     `deleted_at` timestamp NULL DEFAULT NULL,
                                     `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     `wx_room_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信群id。如果是单聊则为空',
                                     `room_id` int(11) DEFAULT '0' COMMENT '群ID',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `msgid` (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话内容存档';

-- ----------------------------
-- Records of mc_work_message_2
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_message_3
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_message_3`;
CREATE TABLE `mc_work_message_3` (
                                     `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                     `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID （mc_corp.id）',
                                     `seq` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息的seq值，标识消息的序号',
                                     `msg_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息唯一标识',
                                     `action` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '消息动作，0.send(发送消息) 1.recall(撤回消息) 2.switch(切换企业日志)',
                                     `from` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息发送方id。同一企业内容为userid，非相同企业/机器人为external_userid',
                                     `tolist` json NOT NULL COMMENT '消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid',
                                     `tolist_id` json DEFAULT NULL COMMENT '接收方ID',
                                     `tolist_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接收方类型 0通讯录 1外部联系人 2群',
                                     `msg_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '文本消息类型，包括text、image、...',
                                     `content` json DEFAULT NULL COMMENT '文本内容：详细见wx文档',
                                     `msg_time` char(13) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '消息发送时间戳，utc时间，ms单位',
                                     `updated_at` timestamp NULL DEFAULT NULL,
                                     `deleted_at` timestamp NULL DEFAULT NULL,
                                     `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     `wx_room_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信群id。如果是单聊则为空',
                                     `room_id` int(11) DEFAULT '0' COMMENT '群ID',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `msgid` (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话内容存档';

-- ----------------------------
-- Records of mc_work_message_3
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_message_4
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_message_4`;
CREATE TABLE `mc_work_message_4` (
                                     `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                     `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID （mc_corp.id）',
                                     `seq` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息的seq值，标识消息的序号',
                                     `msg_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息唯一标识',
                                     `action` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '消息动作，0.send(发送消息) 1.recall(撤回消息) 2.switch(切换企业日志)',
                                     `from` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息发送方id。同一企业内容为userid，非相同企业/机器人为external_userid',
                                     `tolist` json NOT NULL COMMENT '消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid',
                                     `tolist_id` json DEFAULT NULL COMMENT '接收方ID',
                                     `tolist_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接收方类型 0通讯录 1外部联系人 2群',
                                     `msg_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '文本消息类型，包括text、image、...',
                                     `content` json DEFAULT NULL COMMENT '文本内容：详细见wx文档',
                                     `msg_time` char(13) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '消息发送时间戳，utc时间，ms单位',
                                     `updated_at` timestamp NULL DEFAULT NULL,
                                     `deleted_at` timestamp NULL DEFAULT NULL,
                                     `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     `wx_room_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信群id。如果是单聊则为空',
                                     `room_id` int(11) DEFAULT '0' COMMENT '群ID',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `msgid` (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话内容存档';

-- ----------------------------
-- Records of mc_work_message_4
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_message_5
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_message_5`;
CREATE TABLE `mc_work_message_5` (
                                     `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                     `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID （mc_corp.id）',
                                     `seq` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息的seq值，标识消息的序号',
                                     `msg_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息唯一标识',
                                     `action` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '消息动作，0.send(发送消息) 1.recall(撤回消息) 2.switch(切换企业日志)',
                                     `from` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息发送方id。同一企业内容为userid，非相同企业/机器人为external_userid',
                                     `tolist` json NOT NULL COMMENT '消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid',
                                     `tolist_id` json DEFAULT NULL COMMENT '接收方ID',
                                     `tolist_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接收方类型 0通讯录 1外部联系人 2群',
                                     `msg_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '文本消息类型，包括text、image、...',
                                     `content` json DEFAULT NULL COMMENT '文本内容：详细见wx文档',
                                     `msg_time` char(13) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '消息发送时间戳，utc时间，ms单位',
                                     `updated_at` timestamp NULL DEFAULT NULL,
                                     `deleted_at` timestamp NULL DEFAULT NULL,
                                     `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     `wx_room_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信群id。如果是单聊则为空',
                                     `room_id` int(11) DEFAULT '0' COMMENT '群ID',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `msgid` (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话内容存档';

-- ----------------------------
-- Records of mc_work_message_5
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_message_6
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_message_6`;
CREATE TABLE `mc_work_message_6` (
                                     `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                     `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID （mc_corp.id）',
                                     `seq` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息的seq值，标识消息的序号',
                                     `msg_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息唯一标识',
                                     `action` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '消息动作，0.send(发送消息) 1.recall(撤回消息) 2.switch(切换企业日志)',
                                     `from` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息发送方id。同一企业内容为userid，非相同企业/机器人为external_userid',
                                     `tolist` json NOT NULL COMMENT '消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid',
                                     `tolist_id` json DEFAULT NULL COMMENT '接收方ID',
                                     `tolist_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接收方类型 0通讯录 1外部联系人 2群',
                                     `msg_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '文本消息类型，包括text、image、...',
                                     `content` json DEFAULT NULL COMMENT '文本内容：详细见wx文档',
                                     `msg_time` char(13) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '消息发送时间戳，utc时间，ms单位',
                                     `updated_at` timestamp NULL DEFAULT NULL,
                                     `deleted_at` timestamp NULL DEFAULT NULL,
                                     `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     `wx_room_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信群id。如果是单聊则为空',
                                     `room_id` int(11) DEFAULT '0' COMMENT '群ID',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `msgid` (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话内容存档';

-- ----------------------------
-- Records of mc_work_message_6
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_message_7
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_message_7`;
CREATE TABLE `mc_work_message_7` (
                                     `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                     `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID （mc_corp.id）',
                                     `seq` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息的seq值，标识消息的序号',
                                     `msg_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息唯一标识',
                                     `action` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '消息动作，0.send(发送消息) 1.recall(撤回消息) 2.switch(切换企业日志)',
                                     `from` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息发送方id。同一企业内容为userid，非相同企业/机器人为external_userid',
                                     `tolist` json NOT NULL COMMENT '消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid',
                                     `tolist_id` json DEFAULT NULL COMMENT '接收方ID',
                                     `tolist_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接收方类型 0通讯录 1外部联系人 2群',
                                     `msg_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '文本消息类型，包括text、image、...',
                                     `content` json DEFAULT NULL COMMENT '文本内容：详细见wx文档',
                                     `msg_time` char(13) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '消息发送时间戳，utc时间，ms单位',
                                     `updated_at` timestamp NULL DEFAULT NULL,
                                     `deleted_at` timestamp NULL DEFAULT NULL,
                                     `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     `wx_room_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信群id。如果是单聊则为空',
                                     `room_id` int(11) DEFAULT '0' COMMENT '群ID',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `msgid` (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话内容存档';

-- ----------------------------
-- Records of mc_work_message_7
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_message_8
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_message_8`;
CREATE TABLE `mc_work_message_8` (
                                     `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                     `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID （mc_corp.id）',
                                     `seq` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息的seq值，标识消息的序号',
                                     `msg_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息唯一标识',
                                     `action` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '消息动作，0.send(发送消息) 1.recall(撤回消息) 2.switch(切换企业日志)',
                                     `from` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息发送方id。同一企业内容为userid，非相同企业/机器人为external_userid',
                                     `tolist` json NOT NULL COMMENT '消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid',
                                     `tolist_id` json DEFAULT NULL COMMENT '接收方ID',
                                     `tolist_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接收方类型 0通讯录 1外部联系人 2群',
                                     `msg_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '文本消息类型，包括text、image、...',
                                     `content` json DEFAULT NULL COMMENT '文本内容：详细见wx文档',
                                     `msg_time` char(13) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '消息发送时间戳，utc时间，ms单位',
                                     `updated_at` timestamp NULL DEFAULT NULL,
                                     `deleted_at` timestamp NULL DEFAULT NULL,
                                     `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     `wx_room_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信群id。如果是单聊则为空',
                                     `room_id` int(11) DEFAULT '0' COMMENT '群ID',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `msgid` (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话内容存档';

-- ----------------------------
-- Records of mc_work_message_8
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_message_9
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_message_9`;
CREATE TABLE `mc_work_message_9` (
                                     `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                     `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID （mc_corp.id）',
                                     `seq` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息的seq值，标识消息的序号',
                                     `msg_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息唯一标识',
                                     `action` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '消息动作，0.send(发送消息) 1.recall(撤回消息) 2.switch(切换企业日志)',
                                     `from` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息发送方id。同一企业内容为userid，非相同企业/机器人为external_userid',
                                     `tolist` json NOT NULL COMMENT '消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid',
                                     `tolist_id` json DEFAULT NULL COMMENT '接收方ID',
                                     `tolist_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接收方类型 0通讯录 1外部联系人 2群',
                                     `msg_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '文本消息类型，包括text、image、...',
                                     `content` json DEFAULT NULL COMMENT '文本内容：详细见wx文档',
                                     `msg_time` char(13) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '消息发送时间戳，utc时间，ms单位',
                                     `updated_at` timestamp NULL DEFAULT NULL,
                                     `deleted_at` timestamp NULL DEFAULT NULL,
                                     `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     `wx_room_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信群id。如果是单聊则为空',
                                     `room_id` int(11) DEFAULT '0' COMMENT '群ID',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `msgid` (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话内容存档';

-- ----------------------------
-- Records of mc_work_message_9
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_message_config
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_message_config`;
CREATE TABLE `mc_work_message_config` (
                                          `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                          `corp_id` int(11) unsigned DEFAULT '0' COMMENT '企业ID',
                                          `chat_admin` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '(会话内容)企业管理员名称',
                                          `chat_admin_phone` char(11) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '(会话内容)企业管理员手机号',
                                          `chat_admin_idcard` char(18) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '(会话内容)管理员身份证',
                                          `chat_apply_status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '(会话内容)申请进度 0未申请 1填写企业信息 2添加客服提交资料 3配置后台 4完成',
                                          `chat_rsa_key` json DEFAULT NULL COMMENT '(会话内容)公、私钥，例如：{`public_key`: `公钥`,`private_key`: `私钥`,`version`: `版本号`}',
                                          `chat_secret` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '(会话内容)密钥',
                                          `chat_status` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '(会话内容)存档状态 0不存储 1存储',
                                          `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                          `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                          `deleted_at` timestamp NULL DEFAULT NULL,
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话内容存档 - 配置';

-- ----------------------------
-- Records of mc_work_message_config
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_message_index
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_message_index`;
CREATE TABLE `mc_work_message_index` (
                                         `id` int(11) NOT NULL AUTO_INCREMENT,
                                         `corp_id` int(11) NOT NULL COMMENT '企业表ID',
                                         `to_id` int(11) NOT NULL COMMENT '接收方ID',
                                         `to_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接收方类型 0员工 1外部联系人 2群',
                                         `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                         `updated_at` timestamp NULL DEFAULT NULL,
                                         `from_id` int(11) NOT NULL COMMENT '发送方ID(员工ID)',
                                         `flag` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT '',
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话内容存档-信息检索';

-- ----------------------------
-- Records of mc_work_message_index
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_room
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_room`;
CREATE TABLE `mc_work_room` (
                                `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                `corp_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '企业表ID（mc_corp.id）',
                                `wx_chat_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客户群ID',
                                `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客户群名称',
                                `owner_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '群主ID（work_employee.id）',
                                `notice` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '群公告',
                                `status` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '客户群状态（0 - 正常 1 - 跟进人离职 2 - 离职继承中 3 - 离职继承完成）',
                                `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '群创建时间',
                                `room_max` int(10) NOT NULL DEFAULT '0' COMMENT '群成员上限',
                                `room_group_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '分组id（work_room_group.id）',
                                `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                `deleted_at` timestamp NULL DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户群表';

-- ----------------------------
-- Records of mc_work_room
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_room_auto_pull
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_room_auto_pull`;
CREATE TABLE `mc_work_room_auto_pull` (
                                          `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                          `corp_id` int(11) unsigned NOT NULL COMMENT '企业表ID(mc_corp.id)',
                                          `qrcode_name` varchar(30) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '二维码名称',
                                          `qrcode_url` varchar(100) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '二维码地址',
                                          `wx_config_id` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '二维码凭证',
                                          `is_verified` tinyint(4) unsigned NOT NULL DEFAULT '2' COMMENT '添加验证 （1:需验证 2:直接通过）',
                                          `leading_words` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '入群引导语',
                                          `tags` json NOT NULL COMMENT '群标签 [{`tag_id`: `1`,`type`: 1,`tag_name`:`标签`,group_id:`1` ,group_name`:分组名称}]',
                                          `employees` json NOT NULL COMMENT '使用成员[{`id`: `1`,name`:`成员`}]',
                                          `rooms` json NOT NULL COMMENT '群[{`id`: `1`,`type`: 1,`name`:`成员`,room_max:''群上限''}]',
                                          `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                          `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                          `deleted_at` timestamp NULL DEFAULT NULL,
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='自动拉群表';

-- ----------------------------
-- Records of mc_work_room_auto_pull
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_room_group
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_room_group`;
CREATE TABLE `mc_work_room_group` (
                                      `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                      `corp_id` int(11) unsigned NOT NULL COMMENT '企业表ID（mc_corp.id）',
                                      `name` varchar(15) CHARACTER SET utf8 NOT NULL COMMENT '分组名称',
                                      `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                      `deleted_at` timestamp NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户群分组管理表';

-- ----------------------------
-- Records of mc_work_room_group
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mc_work_update_time
-- ----------------------------
DROP TABLE IF EXISTS `mc_work_update_time`;
CREATE TABLE `mc_work_update_time` (
                                       `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                       `corp_id` int(11) NOT NULL DEFAULT '0' COMMENT '企业表ID（mc_crop.id）',
                                       `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '类型（1.通讯录，2.客户，3.标签，4.部门 5.会放内容存档 6.企业数据）',
                                       `last_update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次同步时间',
                                       `error_msg` json DEFAULT NULL COMMENT '错误信息',
                                       `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='同步时间表';

-- ----------------------------
-- Records of mc_work_update_time
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
