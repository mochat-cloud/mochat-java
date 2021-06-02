package com.mochat.mochat.common.util.wm;

/**
 * @author: yangpengwei
 * @time: 2020/11/19 6:04 下午
 * @description 会话内容存档辅助类
 */
public class WorkMsgHelper {

    /**
     * 通过 corpId 取模获取数据库表名
     * 注: 仅用于会话内容存档
     *
     * @param corpId 企业 id
     * @return 表名
     */
    public static String getTableName(long corpId) {
        return "mc_work_message_" + (corpId % 10);
    }
}
