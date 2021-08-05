package org.xstudio.plugin.idea.util;


import com.intellij.openapi.diagnostic.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JavaUtil {
    private static final Logger logger = Logger.getInstance(JavaUtil.class);

    private JavaUtil() {
        super();
    }

    /**
     * 判断是否存在某属性的 get方法
     *
     * @param methods
     * @param fieldGetMet
     * @return boolean
     */
    public static boolean checkGetMet(Method[] methods, String fieldGetMet) {
        for (Method met : methods) {
            if (fieldGetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param obj       操作的对象
     * @param fieldName 操作的属性值
     * @param value     设置的值
     */
    public static void setter(Object obj, String fieldName, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = getSetterMethodName(fieldName);
        Method setMethod = obj.getClass().getMethod(methodName, value.getClass());
        if (null != setMethod) {
            setMethod.invoke(obj, value);
        }
    }

    /**
     * getter方法
     *
     * @param obj       对象
     * @param fieldName 字段
     */
    public static Object getter(Object obj, String fieldName) {
        String methodName = getGetterMethodName(fieldName);
        try {
            Method getMethod = obj.getClass().getMethod(methodName);
            return getMethod.invoke(obj);
        } catch (Exception e) {
            try {
                methodName = getGetterMethodName(fieldName, true);
                Method getMethod = obj.getClass().getMethod(methodName);
                return getMethod.invoke(obj);
            } catch (Exception e2) {
                try {
                    return obj.getClass().getDeclaredField(fieldName).get(obj);
                } catch (Exception e1) {
                    logger.error("获取{} get方法失败", fieldName);
                }
            }
        }
        return null;
    }

    private static String getGetterMethodName(String property, boolean b) {
        StringBuilder sb = new StringBuilder();

        sb.append(property);
        if (Character.isLowerCase(sb.charAt(0))) {
            if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }
        if (b) {
            sb.insert(0, "is");
        } else {
            sb.insert(0, "get");
        }

        return sb.toString();
    }

    public static String getGetterMethodName(String property) {
        return getGetterMethodName(property, false);
    }


    public static String getSetterMethodName(String property) {
        StringBuilder sb = new StringBuilder();

        sb.append(property);
        boolean upperFirstCharacter = Character.isLowerCase(sb.charAt(0)) && (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1)));
        if (upperFirstCharacter) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }

        sb.insert(0, "set");

        return sb.toString();
    }
}
