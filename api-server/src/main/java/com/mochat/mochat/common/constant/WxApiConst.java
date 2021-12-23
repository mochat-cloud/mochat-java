/**
 * This file is part of MoChat.
 * @link     https://mo.chat
 * @document https://mochat.wiki
 * @contact  group@mo.chat
 * @license  https://github.com/mochat-cloud/mochat-java/blob/master/LICENSE
 */

package com.mochat.mochat.common.constant;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/12 4:54 下午
 * @description 企业微信 api
 * <p>
 * 企业微信 api 访问地址: https://work.weixin.qq.com/api/doc/90000/90135/90664
 */
public interface WxApiConst {

    String API_BASE = "https://qyapi.weixin.qq.com/cgi-bin";

    /**
     * 微信 AccessToken url
     * <p>
     * GET https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRET
     * <p>
     * corpid: 企业ID，获取方式参考：术语说明-corpid
     * corpsecret: 应用的凭证密钥，获取方式参考：术语说明-secret
     */
    String API_ACCESS_TOKEN = API_BASE + "/gettoken";

    /**
     * 微信 获取部门列表 url
     * <p>
     * GET https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN&id=ID
     * <p>
     * access_token: 调用接口凭证
     * id: 部门id。获取指定部门及其下的子部门（以及及子部门的子部门等等，递归）。 如果不填，默认获取全量组织架构
     */
    String API_DEPARTMENT_LIST = API_BASE + "/department/list";

    /**
     * 微信 获取部门成员详情 url
     * <p>
     * GET https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=ACCESS_TOKEN&department_id=DEPARTMENT_ID&fetch_child=FETCH_CHILD
     * <p>
     * access_token: 调用接口凭证
     * department_id: 获取的部门id
     * fetch_child: 1/0：是否递归获取子部门下面的成员
     */
    String API_USER_LIST = API_BASE + "/user/list";

    /**
     * 微信 获取成员详情 url
     * <p>
     * GET https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE
     * <p>
     * access_token: 调用接口凭证
     * code: 通过成员授权获取到的code，最大为512字节。每次成员授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     */
    String API_USER_INFO = API_BASE + "/user/getuserinfo";

    /**
     * 微信 获取配置了客户联系功能的成员列表
     * <p>
     * GET https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_follow_user_list?access_token=ACCESS_TOKEN
     * <p>
     * access_token: 调用接口凭证
     */
    String API_FOLLOW_USER_LIST = API_BASE + "/externalcontact/get_follow_user_list";

    /**
     * 微信 获取联系客户统计数据
     * <p>
     * POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_user_behavior_data?access_token=ACCESS_TOKEN
     * <p>
     * access_token: 调用接口凭证
     */
    String API_USER_BEHAVIOR_DATA = API_BASE + "/externalcontact/get_user_behavior_data";

    /**
     * 微信 获取企业标签库
     * <p>
     * POST 请求地址:https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_corp_tag_list?access_token=ACCESS_TOKEN
     * <p>
     * access_token: 调用接口凭证
     */
    String API_GET_CORP_TAG_LIST = API_BASE + "/externalcontact/get_corp_tag_list";

    /**
     * 微信 添加企业客户标签
     * <p>
     * POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_corp_tag?access_token=ACCESS_TOKEN
     * <p>
     * access_token: 调用接口凭证
     */
    String API_ADD_CORP_TAG = API_BASE + "/externalcontact/add_corp_tag";

    /**
     * 微信 编辑企业客户标签
     * <p>
     * POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_corp_tag?access_token=ACCESS_TOKEN
     * <p>
     * access_token: 调用接口凭证
     */
    String API_EDIT_CORP_TAG = API_BASE + "/externalcontact/edit_corp_tag";

    /**
     * 微信 删除企业客户标签
     * <p>
     * POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_corp_tag?access_token=ACCESS_TOKEN
     * <p>
     * access_token: 调用接口凭证
     */
    String API_DEL_CORP_TAG = API_BASE + "/externalcontact/del_corp_tag";

    /**
     * 微信 配置客户联系「联系我」方式
     * <p>
     * POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_contact_way?access_token=ACCESS_TOKEN
     * <p>
     * access_token: 调用接口凭证
     */
    String API_ADD_CONTACT_WAY = API_BASE + "/externalcontact/add_contact_way";

    /**
     * 微信 更新客户联系「联系我」方式
     * <p>
     * POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/update_contact_way?access_token=ACCESS_TOKEN
     * <p>
     * access_token: 调用接口凭证
     */
    String API_UPDATE_CONTACT_WAY = API_BASE + "/externalcontact/update_contact_way";

    /**
     * 微信 获取客户群列表
     * <p>
     * POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/list?access_token=ACCESS_TOKEN
     * <p>
     * access_token: 调用接口凭证
     */
    String API_GET_WORKROOM_LIST = API_BASE + "/externalcontact/groupchat/list";

    /**
     * 微信 获取客户群详情
     * <p>
     * POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/get?access_token=ACCESS_TOKEN
     * <p>
     * access_token: 调用接口凭证
     */
    String API_GET_WORKROOM_INFO = API_BASE + "/externalcontact/groupchat/get";

    /**
     * 微信 修改客户备注信息
     * <p>
     * POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/remark?access_token=ACCESS_TOKEN
     * <p>
     * access_token: 调用接口凭证
     */
    String API_EDIT_EXTERNAL_REMARK = API_BASE + "/externalcontact/remark";

    /**
     * 微信 获取客户列表
     * <p>
     * GET https://qyapi.weixin.qq.com/cgi-bin/externalcontact/list?access_token=ACCESS_TOKEN&userid=USERID
     * <p>
     * access_token: 调用接口凭证
     */
    String API_GET_EXTERNAL_LIST = API_BASE + "/externalcontact/list";

    /**
     * 微信 获取客户详情
     * <p>
     * GET https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get?access_token=ACCESS_TOKEN&external_userid=EXTERNAL_USERID
     * <p>
     * access_token: 调用接口凭证
     */
    String API_GET_EXTERNAL_INFO = API_BASE + "/externalcontact/get";

    /**
     * 微信 发送欢迎语
     * <p>
     * POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/send_welcome_msg?access_token=ACCESS_TOKEN
     * <p>
     * access_token: 调用接口凭证
     */
    String API_ADD_WELCOME_MSG = API_BASE + "externalcontact/send_welcome_msg";


    /**
     * 上传临时素材
     * <p>
     * POST https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE
     * <p>
     * access_token: 调用接口凭证
     * type: 文件类型, 经测试可不传
     */
    String API_UPLOAD_FILE_TO_TEMP = API_BASE + "/media/upload";

    /**
     * 企业微信 - 获取指定的应用详情
     * <p>
     * https://qyapi.weixin.qq.com/cgi-bin/agent/get?access_token=ACCESS_TOKEN&agentid=AGENTID
     */
    String API_GET_AGENT_INFO = API_BASE + "/agent/get";

    /**
     * 企业微信 - 获取企业的jsapi_ticket
     * <p>
     * https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=ACCESS_TOKEN
     */
    String API_GET_JSAPI_TICKET_CORP = API_BASE + "/get_jsapi_ticket";

    /**
     * 企业微信 - 获取应用的jsapi_ticket
     * <p>
     * https://qyapi.weixin.qq.com/cgi-bin/ticket/get?access_token=ACCESS_TOKEN&type=agent_config
     */
    String API_GET_JSAPI_TICKET_APP = API_BASE + "/ticket/get";
}
