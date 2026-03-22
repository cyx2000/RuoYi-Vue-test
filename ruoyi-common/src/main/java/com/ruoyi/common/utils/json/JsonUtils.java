package com.ruoyi.common.utils.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruoyi.common.exception.UtilException;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ser.FilterProvider;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;
import tools.jackson.databind.type.CollectionType;

/**
 * 处理Json交互
 *
 * @author winter123
 */
public class JsonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    public final static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        // 遇到没有定义的字段，反序列化不要报错。
        objectMapper.deserializationConfig().with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * 将对象转换成json字符串，返回格式如：“{"hi":"world"}”
     * @param obj
     * @return String
     */
    public static String toJsonStr(Object obj) {
        String jsonResult = null;
        try {
            jsonResult = objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new UtilException(e);
        }
        return jsonResult;
    }

    /**
     * 排除指定的属性后将对象转换成json字符串，如原始数据：{"pwd":123, "id":2}，
     * excludeParamNames 数组中包含pwd，最后输出：{"id":2}。
     *
     * @param object
     * @param excludeParamNames 序列化排除的字段属性数组
     * @return String
     */
    public static String toJsonStr(Object object, String[] excludeParamNames) {
        try {
            FilterProvider filters = new SimpleFilterProvider()
                .addFilter("excludeFilter", SimpleBeanPropertyFilter.serializeAllExcept(excludeParamNames));
           return objectMapper.writer(filters).writeValueAsString(object);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new UtilException(e);
        }
    }

    /**
     * 封装操作json对象的类
     *
     */
    public class JSONObject extends LinkedHashMap<String, Object> {

        /**
         * 获取字符串结果
         *
         * @param target
         * @param inKey
         * @return null或者字符串结果
         */
        public String getString(String inKey) {
            Object value = this.get(inKey);
            if(value instanceof String strValue) {
                return strValue;
            }
            return null;
        }

        /**
         * 获取long整型结果
         *
         * @param target
         * @param key
         * @return 返回默认值或者long结果
         */
        public long getLongValue(String key) {
            return getLongValue(key, 0L);
        }

        public long getLongValue(String key, long defaultValue) {
            Object value = this.get(key);
            if (value == null) {
                return defaultValue;
            } else if (value instanceof Number numValue) {
                return numValue.longValue();
            } else if (value instanceof String strValue) {
                String str = strValue.trim();
                if (!str.isEmpty() && !"null".equalsIgnoreCase(str)) {
                    return str.indexOf(46) != -1 ? (long)Double.parseDouble(str) : Long.parseLong(str);
                }
            }
            return defaultValue;
        }
    }

    /**
     * 获取json对象
     *
     * @param jsonStr
     * @return JSONObject对象
     */
    public static JSONObject getJsonObj(String jsonStr) {
        if(jsonStr == null) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonStr, JSONObject.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new UtilException(e);
        }
    }

    /**
     * 将json字符串数组反序列化成List数组。
     *
     * @param <T>
     * @param jsonStr
     * @param elementClass
     * @return T类型的List数组
     */
    @SuppressWarnings("hiding")
    public static <T> List<T> jsonArrayToList(String jsonStr, Class<T> elementClass) {
        try {
            CollectionType listType = objectMapper.getTypeFactory()
                .constructCollectionType(ArrayList.class, elementClass);
            return objectMapper.readValue(jsonStr, listType);
        } catch(Exception e) {
            LOGGER.error(e.getMessage());
            throw new UtilException(e);
        }
    }

}
