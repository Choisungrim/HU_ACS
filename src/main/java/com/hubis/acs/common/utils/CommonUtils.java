package com.hubis.acs.common.utils;

import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.common.handler.exception.CustomException;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommonUtils {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public static String getMessageValue(Map<String, Object> message, String key, boolean isMandatory) throws CustomException
    {
        String value = getMessageValue(message, key);

        if (isNullOrEmpty(value))
        {
            if (isMandatory)
            {
                logger.error("Parameter Not Found [" + key + "]");
                validateMandatoryParameter(key, value);
            }
        }

        return value;
    }

    public static String getMessageValue(Map<String, Object> message, String key, String defaultValue)
    {
        String value = getMessageValue(message, key);

        if (isNullOrEmpty(value))
            value = defaultValue;

        return value;
    }

    public static String getMessageValue(Map<String, Object> message, String key)
    {
        String res = getMapString(message, key);

        // logger.info("Get Parameter Value, [" + key + "=" + res + "]");

        return res;
    }

    public static Map<String, Object> getMessageItem(Map<String, Object> message, String key)
    {
        Map<String, Object> res = null;

        try
        {
            res = (Map<String, Object>) getMapObject(message, key);
        }
        catch (Exception e) {}

        return res;
    }

    public static List<Map<String, Object>> getMessageList(Map<String, Object> message, String key)
    {
        List<Map<String, Object>> res = null;

        try
        {
            res = (List<Map<String, Object>>) getMapObject(message, key);
        }
        catch (Exception e) {}

        return res;
    }


    public static String getMapStringFromObj(List<Object> objList, int rowIdx, String colName)
    {
        String res = "";

        try
        {
            res = getMapStringFromObj(getMapItemFromObj(objList, rowIdx), colName);
        }
        catch (Exception e) {}

        return res;
    }

    public static Object getMapItemFromObj(List<Object> objList, int rowIdx)
    {
        Object res = null;

        try
        {
            res = objList.get(rowIdx);
        }
        catch (Exception e) {}

        return res;
    }

    public static Map<String, Object> getMapItemFromObj(Object obj)
    {
        Map<String, Object> res = null;

        try
        {
            res = (Map<String, Object>)obj;
        }
        catch (Exception e) {}

        return res;
    }

    public static String getMapStringFromObj(Object obj, String key)
    {
        String res = "";

        try
        {
            res = getMapString(getMapItemFromObj(obj), key);
        }
        catch (Exception e) {}

        return res;
    }

    public static String getMapString(List<Map<String, Object>> mapList, int rowIdx, String colName)
    {
        String res = "";

        try
        {
            res = getMapString(getMapItem(mapList, rowIdx), colName);
        }
        catch (Exception e) {}

        return res;
    }

    public static Map<String, Object> getMapItem(List<Map<String, Object>> objList, int rowIdx)
    {
        Map<String, Object> res = null;

        try
        {
            res = objList.get(rowIdx);
        }
        catch (Exception e) {}

        return res;
    }

    public static String getMapString(Map<String, Object> map, String key)
    {
        String res = "";

        try
        {
            res = ConvertUtils.toString(getMapObject(map, key));

            if (isNullOrEmpty(res))
                res = "";
        }
        catch (Exception e) {}

        return res;
    }

    public static Object getMapObject(Map<String, Object> map, String key)
    {
        Object res = null;

        try
        {
            res = map.get(key);
        }
        catch (Exception e) {}

        return res;
    }

    public static void PutMap(Map<String, Object> map, String key, Object value)
    {
        map.put(key, value==null?"":value);
    }


    @SuppressWarnings("rawtypes")
    public static Map cloneMap(Map map)
    {
        try
        {
            Map res = map.getClass().newInstance();

            res.putAll(map);

            return res;
        }
        catch (Exception e) {}

        return null;
    }


    public static boolean isNullOrEmpty(List<?> listObj)
    {
        if (listObj == null)
            return true;

        if (listObj.size() == 0)
            return true;

        return false;
    }

    public static boolean isNullOrEmpty(Set<?> listObj)
    {
        if (listObj == null)
            return true;

        if (listObj.size() == 0)
            return true;

        return false;
    }

    @SuppressWarnings("rawtypes")
    public static boolean isNullOrEmpty(Map map)
    {
        if( map == null )
            return true;
        return map.isEmpty();
    }

    public static boolean isNullOrEmpty(Object obj)
    {
        if (obj == null)
            return true;

        if (obj instanceof CharSequence && ((CharSequence) obj).length() == 0)
            return true;

        if (obj instanceof Collection && ((Collection<?>) obj).isEmpty())
            return true;

        if (obj instanceof Map && ((Map<?, ?>) obj).isEmpty())
            return true;

        if (obj instanceof String[] && ((String[]) obj).length == 0)
            return true;

        return false;
    }

    public static boolean isNullOrEmpty(JSONObject json)
    {
        if (json == null)
            return true;

        if (json.isEmpty())
            return true;

        return false;
    }

    public static boolean isNullOrEmpty(String str)
    {
        if (str == null)
            return true;

        if (str.equals(""))
            return true;

        return false;
    }

    public static boolean isNumeric(String str)
    {
        return str != null && str.matches("[-+]?\\d*\\.?\\d+");
    }


    public static boolean equalsIgnoreCase(String str, String suffix)
    {
        return str.toUpperCase().endsWith(suffix.toUpperCase());
    }


    public static void validateMandatoryParameter(String key, String value) throws CustomException
    {
        if (!isNullOrEmpty(value))
            return;

        throw new CustomException("COM_ERR_001", key);
    }

    public static void validateVariableCudFlag(String value) throws Exception, CustomException {
        switch (value)
        {
            case "C":
                return;

            case "U":
                return;

            case "D":
                return;
            case "N":
                return;
            case "F":
                return;
            default :
                throw new CustomException("COM_ERR_002", value);
        }
    }

    public static void validateVariableIsUsable(String value) throws Exception, CustomException {
        switch (value)
        {
            case "Y":
                return;

            case "N":
                return;

            default :
                throw new CustomException("COM_ERR_003", value);
        }
    }
    public static String getProperty(Properties properties, String key)
    {
        String res = "";

        try
        {
            res = properties.getProperty(key);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return res;
    }

    private final static Function<Map<String, Object>, Predicate<Map<String, Object>>> fnFilterByMap = filterData -> sourceData ->
    {
        for (Map.Entry<String, Object> filter : filterData.entrySet())
        {
            if (!CommonUtils.getMapString(sourceData, filter.getKey()).equals(ConvertUtils.toString(filter.getValue())))
                return false;
        }

        return true;
    };

    public static List<Map<String, Object>> getFilterMapList(List<Map<String, Object>> lstMapData, Map<String, Object> mapFilter)
    {
        try
        {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

            res.addAll(lstMapData.stream().filter(fnFilterByMap.apply(mapFilter)).collect(Collectors.toList()));

            return res;
        }
        catch (Exception e) {}

        return null;
    }

    public static void getSortedList(List<Map<String, Object>> lstData, String... lstOrderBy)
    {
        getSortedList(lstData, Arrays.asList(lstOrderBy));
    }

    public static void getSortedList(List<Map<String, Object>> lstData, List<String> lstOrderBy)
    {
        if (isNullOrEmpty(lstData))
            return;

        if (isNullOrEmpty(lstOrderBy))
            return;

        try
        {
            Comparator<Map<String, Object>> comparator = (m1, m2) ->
            {
                int compare = 0;

                for (String orderBy : lstOrderBy)
                {
                    if (compare == 0)
                    {
                        String key = orderBy.toUpperCase().trim().replace(" " + "DESC", "").replace(" " + "ASC", "");
                        boolean isDesc = orderBy.toUpperCase().trim().endsWith(" " + "DESC");

                        compare = getCompareTo(isDesc?m2:m1, isDesc?m1:m2, key);
                    }
                }

                return compare;
            };

            lstData.sort(comparator);
        }
        catch (Exception e) {}
    }

    private static int getCompareTo(Map<String, Object> m1, Map<String, Object> m2, String key)
    {
        try
        {
            Class<?> clazz = m1.get(key).getClass();

            if (clazz.equals(Integer.class) || clazz.equals(BigDecimal.class))
            {
                int value1 = ConvertUtils.toInt(CommonUtils.getMapString(m1, key));
                int value2 = ConvertUtils.toInt(CommonUtils.getMapString(m2, key));

                if (value1 < value2)
                    return -1;

                if (value1 > value2)
                    return 1;

                return 0;
            }

            if (clazz.equals(Double.class))
            {
                double value1 = ConvertUtils.toDouble(CommonUtils.getMapString(m1, key));
                double value2 = ConvertUtils.toDouble(CommonUtils.getMapString(m2, key));

                if (value1 < value2)
                    return -1;

                if (value1 > value2)
                    return 1;

                return 0;
            }

            if (clazz.equals(Float.class))
            {
                float value1 = ConvertUtils.toFloat(CommonUtils.getMapString(m1, key));
                float value2 = ConvertUtils.toFloat(CommonUtils.getMapString(m2, key));

                if (value1 < value2)
                    return -1;

                if (value1 > value2)
                    return 1;

                return 0;
            }

            if (clazz.equals(Long.class))
            {
                long value1 = ConvertUtils.toLong(CommonUtils.getMapString(m1, key));
                long value2 = ConvertUtils.toLong(CommonUtils.getMapString(m2, key));

                if (value1 < value2)
                    return -1;

                if (value1 > value2)
                    return 1;

                return 0;
            }

            String value1 = CommonUtils.getMapString(m1, key);
            String value2 = CommonUtils.getMapString(m2, key);

            return value1.compareTo(value2);
        }
        catch (Exception e) {}

        return 0;
    }

    public static List<Class<?>> getClassListByPackage(String pkg)
    {
        List<Class<?>> res = new ArrayList<Class<?>>();
        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage(pkg)) // ✅ 패키지 경로 직접 검색
                    .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()) // ✅ 더 많은 타입 검색 가능
                    .filterInputsBy(new FilterBuilder().includePackage(pkg)));

            Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);

            for (Class<?> clazz : classes) {
                res.add(clazz);
            }
        } catch (Exception e) {
            logger.error("Error loading classes from package: " + pkg, e);
            return new ArrayList<Class<?>>();

        }
        return res;

    }
    public static void putMapObject(Map<String, Object> map, String key, Object value)
    {
        map.put(key, value==null?"":value);
    }

    public static void writeFile(String fullPath, String contents)
    {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPath), "UTF-8"))) {
            writer.write(contents);
            writer.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void writeFile(String fullPath, byte[] contents) throws Exception {

        int readCount = 0;
        try (ByteArrayInputStream bin = new ByteArrayInputStream(contents);
             FileOutputStream fos = new FileOutputStream(fullPath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            byte[] outBuffer = new byte[65536];
            while ((readCount = bin.read(outBuffer)) > 0) {
                bos.write(outBuffer, 0, readCount);
            }
            bos.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static EventInfo makeEventInfo(String strSiteId, String strActivity, String strModifier, String strWorkGroupId, String strWorkId, String strRequestId, String strComment )
    {
        EventInfo eventInfo = new EventInfoBuilder()
                .addSiteId(strSiteId)
                .addUserId(strModifier)
                .addComments(strComment)
                .addActivity(strActivity)
                .addWorkId(strWorkId)
                .addWorkGroupId(strWorkGroupId)
                .addRequestId(strRequestId)
                .build();

        return eventInfo;
    }

    public static <T> T setEventInfoToVO(T vo, EventInfo eventInfo) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Method setComments = vo.getClass().getMethod("setComments", String.class);
        setComments.invoke(vo, eventInfo.getComments());

        Method setActivity = vo.getClass().getMethod("setActivity", String.class);
        setActivity.invoke(vo, eventInfo.getActivity());

        Method setModifier = vo.getClass().getMethod("setModifier", String.class);
        setModifier.invoke(vo, eventInfo.getUserId());

        Method setModifyTime = vo.getClass().getMethod("setModifyTime", Date.class);
        setModifyTime.invoke(vo, eventInfo.getTime());

        Method setTransId = vo.getClass().getMethod("setTransId", String.class);
        setTransId.invoke(vo, eventInfo.getTransactionId());

        Method setLastEventTime = vo.getClass().getMethod("setLastEventTime", Date.class);
        setLastEventTime.invoke(vo, eventInfo.getTime());

        return vo;
    }
}
