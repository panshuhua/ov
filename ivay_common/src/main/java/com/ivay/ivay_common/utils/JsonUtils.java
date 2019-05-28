package com.ivay.ivay_common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.LinkedMultiValueMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    // 定义jackson对象
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串。
     *
     * @param data
     * @return
     */
    public static String objectToJson(Object data) {
        try {
            return OBJECT_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData json数据
     * @param beanType 对象中的object类型
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            return OBJECT_MAPPER.readValue(jsonData, beanType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     *
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            return OBJECT_MAPPER.readValue(jsonData, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     *
     * @param jsonData
     * @return
     */
    public static <K, V> Map<K, V> jsonToMap(String jsonData) {
        try {
            return OBJECT_MAPPER.readValue(jsonData, new TypeReference<HashMap<K, V>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对象转map
     *
     * @param obj
     * @return
     */
    public static Map<String, String> objToMap(Object obj) {
        Map<String, String> map = new HashMap<String, String>();
        Field[] fields = obj.getClass().getDeclaredFields();    // 获取f对象对应类中的所有属性域
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            varName = varName.toLowerCase();                    // 将key置为小写，默认为对象的属性
            try {
                boolean accessFlag = fields[i].isAccessible();    // 获取原来的访问控制权限
                fields[i].setAccessible(true);                    // 修改访问控制权限
                Object o = fields[i].get(obj);                    // 获取在对象f中属性fields[i]对应的对象中的变量
                if (o != null) {
                    map.put(varName, o.toString());
                }
                fields[i].setAccessible(accessFlag);            // 恢复访问控制权限
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 对象转LinkedMultiValueMap RestTemplate使用
     *
     * @param obj
     * @return
     */
    public static LinkedMultiValueMap<String, String> objToLinkedMultiValueMap(Object obj) {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        Field[] fields = obj.getClass().getDeclaredFields();    // 获取f对象对应类中的所有属性域
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            //varName = varName.toLowerCase();					// 将key置为小写，默认为对象的属性
            if ("serialVersionUID".equals(varName)) {
                continue;
            }
            try {
                boolean accessFlag = fields[i].isAccessible();    // 获取原来的访问控制权限
                fields[i].setAccessible(true);                    // 修改访问控制权限
                Object o = fields[i].get(obj);                    // 获取在对象f中属性fields[i]对应的对象中的变量
                if (o != null) {
                    map.add(varName, o.toString());
                }
                fields[i].setAccessible(accessFlag);            // 恢复访问控制权限
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }


    public static void main(String[] args) {
//        String str = "{\"14\":0.02}";
//        Map map = jsonToMap(str);
//        System.out.println(map.get("14"));
//        System.out.println(map.get(14));
    }
}
