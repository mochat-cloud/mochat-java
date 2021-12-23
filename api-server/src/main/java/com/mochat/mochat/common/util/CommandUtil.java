/**
 * This file is part of MoChat.
 * @link     https://mo.chat
 * @document https://mochat.wiki
 * @contact  group@mo.chat
 * @license  https://github.com/mochat-cloud/mochat-java/blob/master/LICENSE
 */

package com.mochat.mochat.common.util;

import java.util.concurrent.TimeUnit;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/11/24 9:47 上午
 * @description cmd 工具类, 用于执行 cmd 命令行
 */
public class CommandUtil {

    public static void exeCmd(String commandStr) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(commandStr);
            process.waitFor(10, TimeUnit.SECONDS);
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != process) {
                process.destroy();
            }
            Runtime.getRuntime().freeMemory();
        }
    }

    public static void amrToMp3(String amrPath, String outMp3Path) {
        exeCmd(Helper.getAmrToMp3Cmd(amrPath, outMp3Path));
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2020/11/24 5:56 下午
     * @description 辅助类
     */
    private static class Helper {
        /**
         * @author: Ypw / ypwcode@163.com
         * @time: 2020/11/24 5:56 下午
         * @description 辅助生成 ffmpeg 的 amr 转 mp3 命令
         */
        public static String getAmrToMp3Cmd(String amrPath, String outMp3Path) {
            // MAC 系统下 需要 ffmpeg 的完整路径
            return "ffmpeg -i " + amrPath + " " + outMp3Path;
        }
    }
}
