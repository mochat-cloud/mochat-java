package com.mochat.mochat.common.util;

import java.util.HashMap;
import java.util.Map;

public class UrlUtils {

    public static Map<String, String> urlSplit(String strUrl) {
        int lastIndex = strUrl.lastIndexOf("?");
        if (lastIndex > 0) {
            String urlParam = strUrl.substring(lastIndex + 1);
            return urlParamSplit(urlParam);
        }
        return urlParamSplit(strUrl);
    }

    public static Map<String, String> urlParamSplit(String strUrlParam) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        if (strUrlParam == null) {
            return mapRequest;
        }
        String[] arrSplit = null;
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                mapRequest.put(arrSplitEqual[0], "");
            }
        }
        return mapRequest;
    }

    public static void main(String... args) {
        String url = "/codeAuth?agentId=1&pageFlag=customer";
        Map map = UrlUtils.urlSplit(url);
        System.out.println(map);
    }
}
