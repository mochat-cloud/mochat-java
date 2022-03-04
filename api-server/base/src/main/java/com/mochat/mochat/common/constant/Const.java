/**
 * This file is part of MoChat.
 * @link     https://mo.chat
 * @document https://mochat.wiki
 * @contact  group@mo.chat
 * @license  https://github.com/mochat-cloud/mochat-java/blob/master/LICENSE
 */

package com.mochat.mochat.common.constant;

//系统常量
public interface Const {
    String CONTACT_SECRET=""; //客户密钥
    String URL_REQUEST_ADDRESS = "https://qyapi.weixin.qq.com/cgi-bin";
    String SIMPLE_JWT_SECRET = "3S6ybWbSy&LFAlp";//JWT秘钥
    String SIMPLE_JWT_PREFIX = "mc_jwt_";//JWT前缀
    String TEMP_FILE_DIR = System.getProperty("java.io.tmpdir");//目录前缀
}
