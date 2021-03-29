package com.mochat.mochat.common.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * @author: yangpengwei
 * @time: 2020/11/19 3:51 下午
 * @description 封装一些 Json 相关的操作
 */
public class JsonUtils {

    /**
     * @author: yangpengwei
     * @time: 2020/11/19 3:51 下午
     * @description 查询所有节点是否包含目标 key
     */
    public static boolean hasAllNode(String json, String key) {
        return hasAllNode(new JSONObject(json), key);
    }

    public static boolean hasAllNode(JSONObject jsonObject, String key) {
        Iterator<String> keys = jsonObject.keys();
        boolean result = false;
        while (keys.hasNext() && !result) {
            String k = keys.next();
            if (key.equals(k)) {
                return true;
            } else {
                Object object = jsonObject.get(k);
                if (object instanceof JSONObject) {
                    result = hasAllNode((JSONObject) object, key);
                }
                if (object instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) object;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        result = hasAllNode(jsonArray.get(i), key);
                    }
                }
            }
        }
        return result;
    }

    public static boolean hasAllNode(Object object, String key) {
        if (object instanceof JSONObject) {
            return hasAllNode((JSONObject) object, key);
        } else if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            for (int i = 0; i < jsonArray.length(); i++) {
                return hasAllNode(jsonArray.get(i), key);
            }
        }
        return false;
    }

    /**
     * @author: yangpengwei
     * @time: 2020/11/19 3:52 下午
     * @description 获取目标 key 所在的 jsonObject
     */
    public static JSONObject getJsonObjectHasKey(String json, String key) {
        return getJsonObjectHasKey(new JSONObject(json), key);
    }

    /**
     * @author: yangpengwei
     * @time: 2020/11/19 3:52 下午
     * @description 获取目标 key 所在的 jsonObject
     * 注: 目标 key 所在的 jsonObject 进行增删操作后, 会反应到根 jsonObject
     */
    public static JSONObject getJsonObjectHasKey(JSONObject jsonObject, String key) {
        Iterator<String> keys = jsonObject.keys();
        JSONObject result = null;
        while (keys.hasNext()) {
            String k = keys.next();
            if (key.equals(k)) {
                result = jsonObject;
            } else {
                Object object = jsonObject.get(k);
                if (object instanceof JSONObject) {
                    result = getJsonObjectHasKey((JSONObject) object, key);
                }
                if (object instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) object;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        result = getJsonObjectHasKey(jsonArray.get(i), key);
                    }
                }
            }
            if (null != result && !result.isEmpty()) {
                return result;
            }
        }
        return result;
    }

    public static JSONObject getJsonObjectHasKey(Object object, String key) {
        if (object instanceof JSONObject) {
            return getJsonObjectHasKey((JSONObject) object, key);
        } else if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            for (int i = 0; i < jsonArray.length(); i++) {
                return getJsonObjectHasKey(jsonArray.get(i), key);
            }
        }
        return null;
    }
}
