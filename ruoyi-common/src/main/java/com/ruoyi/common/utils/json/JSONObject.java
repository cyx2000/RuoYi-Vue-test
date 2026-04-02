package com.ruoyi.common.utils.json;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 封装操作json对象的类
 *
 * @author winter123
 */
public class JSONObject extends LinkedHashMap<String, Object> {
    public JSONObject() {
        super();
    }

    public JSONObject(int initialCapacity) {
        super(initialCapacity);
    }

    public JSONObject(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public JSONObject(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public JSONObject(Map<String, Object> map) {
        super(map);
    }

    /**
     * 获取字符串结果
     *
     * @param target
     * @param inKey
     * @return null或者字符串结果
     */
    public String getString(String inKey) {
        Object value = super.get(inKey);
        if(value instanceof String strValue) {
            return strValue;
        }
        if(value instanceof Integer intValue) {
            return intValue.toString();
        }
        if(value instanceof Long longValue) {
            return longValue.toString();
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
        Object value = super.get(key);
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
