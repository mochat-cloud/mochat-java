package com.mochat.mochat.common.util;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/11/9 14:09
 */
public class MessageUtil {

    /**
     * 解析微信发来的请求（XML）.
     *
     * @param msg 消息
     * @return map
     */
    public static Map<String, Object> parseXml(String msg) {
        return parseXml(msg, new ArrayList<>());
    }

    public static Map<String, Object> parseXmlOfWxContact(String msg) {
        List<String> nodeList = new ArrayList<>();
        nodeList.add("extattr");
        nodeList.add("externalattr");
        nodeList.add("external_attr");
        return parseXml(msg, new ArrayList<>());
    }

    public static Map<String, Object> parseXml(String msg, List<String> nodeList) {
        // 将解析结果存储在HashMap中
        Map<String, Object> map = new HashMap<>();

        // 从request中取得输入流
        try (InputStream inputStream = new ByteArrayInputStream(msg.getBytes(StandardCharsets.UTF_8.name()))) {
            // 读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            // 得到xml根元素
            Element root = document.getRootElement();
            parseElement(root, map, nodeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private static void parseElement(Element e, Map<String, Object> map, List<String> nodeList) {
        if (e.elements().size() > 0) {
            List<Element> elements = e.elements();
            if (isList(elements, nodeList)) {
                List<Object> childList = new ArrayList<>();
                map.put(e.getName().toLowerCase(), childList);
                elements.forEach(element -> {
                    Map<String, Object> childMap = new HashMap<>();
                    childList.add(childMap);
                    parseElement(element, childMap, nodeList);
                });
            } else {
                Map<String, Object> childMap = new HashMap<>();
                map.put(e.getName().toLowerCase(), childMap);
                elements.forEach(element -> parseElement(element, childMap, nodeList));
            }
        } else {
            map.put(e.getName().toLowerCase(), e.getTextTrim());
        }
    }

    private static boolean isList(List<Element> elements, List<String> nodeList) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String name = element.getName().toLowerCase();
            if (nodeList.contains(name)) {
                return true;
            }
            if (names.contains(name)) {
                return true;
            } else {
                names.add(name);
            }
        }
        return false;
    }

}
