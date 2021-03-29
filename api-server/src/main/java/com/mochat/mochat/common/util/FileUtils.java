package com.mochat.mochat.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;

/**
 * @author: yangpengwei
 * @time: 2020/12/11 3:40 下午
 * @description File工具
 * <p>
 * - 用于生成文件保存到阿里云的目录及文件名
 * - 保存文件到本地
 */
public class FileUtils {

    private static final Random RANDOM = new Random();

    private static final String EMPLOYEE_AVATAR = "mochat/test/avatar";
    private static final String EMPLOYEE_THUMB_AVATAR = "mochat/employee/thumb_avatar";
    private static final String EMPLOYEE_QR_CODE = "mochat/employee/qr_code";

    private static final String CONTACT_CONTACT_WAY_QR_CODE = "ContactWayClient/QrCode";

    private static final String CONTACT_AVATAR = "contact/avatar/";

    private static final String FILE_PNG = ".png";

    public static String getFileName(String module, String suffix) {
        return module + getRandomString() + suffix;
    }

    public static String getFileNameOfEmpAvatar() {
        return EMPLOYEE_AVATAR + getRandomString() + FILE_PNG;
    }

    public static String getFileNameOfEmpThumbAvatar() {
        return EMPLOYEE_THUMB_AVATAR + getRandomString() + FILE_PNG;
    }

    public static String getFileNameOfEmpQrCode() {
        return EMPLOYEE_QR_CODE + getRandomString() + FILE_PNG;
    }

    /**
     * 客户自动拉群二维码
     */
    public static String getFileNameOfContactWayQrCode() {
        return CONTACT_CONTACT_WAY_QR_CODE + getRandomString() + FILE_PNG;
    }

    /**
     * @description 获取客户头像存储阿里云路径
     * @author zhaojinjian
     * @createTime 2020/12/23 15:48
     */
    public static String getContactAvatarPath() {
        return CONTACT_AVATAR + getRandomString() + FILE_PNG;
    }

    public static boolean saveFileByInput(File file, InputStream inputStream) {
        try {
            file.getParentFile().mkdirs();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int offset;
            while ((offset = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, offset);
            }
            inputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getRandomString() {
        return getRandomString(6);
    }

    public static String getRandomString(int length) {
        StringBuilder builder = new StringBuilder();
        builder.append(System.currentTimeMillis() * 10);
        for (int i = 0; i < length; i++) {
            builder.append(RANDOM.nextInt(10));
        }
        return builder.toString();
    }
}
