package com.hubis.acs.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.util.*;

public class ConvertUtils {

    public static boolean toBoolean(String string)
    {
        return Boolean.parseBoolean(string);
    }

    public static boolean toBooleanExtended(String string) {
        if (string == null) return false;
        String s = string.trim().toLowerCase();
        return s.equals("true") || s.equals("1") || s.equals("y");
    }

    public static String toString(Object obj)
    {
        String res = "";

        try
        {
            res = obj.toString();
            res = res.trim();
        }
        catch(Exception e) {}

        return res;
    }

    public static Integer toInteger(Object obj)
    {
        try
        {
            return Integer.parseInt(toString(obj));
        }
        catch (Exception e) {}

        return null;
    }

    public static int toInt(Object obj)
    {
        int res = 0;

        try
        {
            res = Integer.parseInt(toString(obj));
        }
        catch(Exception e) {}

        return res;
    }

    public static long toLong(Object obj)
    {
        long res = 0L;

        try
        {
            res = Long.parseLong(toString(obj));
        }
        catch(Exception e) {}

        return res;
    }

    public static double toDouble(Object obj)
    {
        double res = 0D;

        try
        {
            return Double.parseDouble(toString(obj));
        }
        catch(Exception e) {}

        return res;
    }

    public static float toFloat(Object obj)
    {
        float res = 0F;

        try
        {
            return Float.parseFloat(toString(obj));
        }
        catch(Exception e) {}

        return res;
    }

    public static String toCamelCase(String str)
    {
        String res = "";

        StringTokenizer st = new StringTokenizer(str, "-|_|.|,");

        int i=0;
        while (st.hasMoreTokens())
        {
            String s = st.nextToken();

            if (s.length() > 0)
            {
                if( i == 0 )
                    res += s.substring(0, 1).toLowerCase();
                else
                    res += s.substring(0, 1).toUpperCase();

                res += s.substring(1, s.length()).toLowerCase();
            }
            i++;
        }

        return res;
    }

    public static String toPascalCase(String str)
    {
        String res = "";

        StringTokenizer st = new StringTokenizer(str, "-|_|.|,");

        while (st.hasMoreTokens())
        {
            String s = st.nextToken();

            if (s.length() > 0)
            {
                res += s.substring(0, 1).toUpperCase();
                res += s.substring(1, s.length()).toLowerCase();
            }
        }

        return res;
    }

    public static Class<?> toJavaType(String dbType)
    {
        dbType = dbType.toUpperCase();

        if(dbType.indexOf("CHAR") > -1 || dbType.equals("CLOB") || dbType.equals("TEXT") || dbType.indexOf("VARCHAR") > -1 || dbType.equals("LONGTEXT"))
            return String.class;
        else if(dbType.indexOf("INT") > -1 || dbType.equals("SMALLINT") || dbType.equals("TINYINT") )
            return Integer.class;
        else if(dbType.equals("BIGINT") )
            return Long.class;
        else if(dbType.equals("FLOAT") || dbType.indexOf("NUMBER") > -1 )
            return Float.class;
        else if(dbType.equals("DOUBLE") || dbType.indexOf("DECIMAL") > -1 || dbType.indexOf("NUMERIC") > -1)
            return Double.class;
        else if(dbType.equals("DATE") || dbType.startsWith("TIMESTAMP") || dbType.indexOf("DATETIME") > -1)
            return Date.class;
        else
            return String.class;
    }

    public static String parseString(Object obj)
    {
        String res = "";

        try
        {
            res = obj.toString();
            res = res.trim();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return res;
    }

    public static int parseInt(String str)
    {
        int res = 0;

        try
        {
            res = Integer.parseInt(str);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return res;
    }

    public static String convertJsonToPretty(JSONObject json)
    {
        return convertJsonToStr(json, true);
    }

    public static String convertJsonToRawFormat(JSONObject json)
    {
        return convertJsonToStr(json, false);
    }

    public static String convertJsonToStr(JSONObject json)
    {
        return convertJsonToStr(json, true);
    }

    @SuppressWarnings("deprecation")
    public static String convertJsonToStr(JSONObject json, boolean isPretty)
    {
        String jsonStr = "";

        try
        {
            jsonStr = json.toString();

            if (isPretty)
            {
                ObjectMapper mapper = new ObjectMapper();

                if (!CommonUtils.isNullOrEmpty(jsonStr))
                {
                    Object o = mapper.readValue(jsonStr, Object.class);

                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
                }
            }

            return jsonStr;
        }
        catch (Exception e) {}

        return jsonStr;
    }

    public static Map<String, Object> convertJsonToMap(JSONObject json)
    {
        try
        {
            String jsonStr = convertJsonToStr(json);
            return new ObjectMapper().readValue(jsonStr, new TypeReference<Map<String, Object>>(){});
        }
        catch (Exception e) {}

        return null;
    }

    public static JSONObject convertMapToJson(Map<String, Object> map)
    {
        try
        {
            JSONObject json = new JSONObject();

            map.forEach((k, v) -> JsonUtils.putJsonObject(json, k, v));

            return json;
        }
        catch (Exception e) {}

        return null;
    }

    public static List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<>();

        if(obj == null)
            return list;

        if (obj.getClass().isArray())
            list = Arrays.asList((Object[])obj);

        else if (obj instanceof Collection<?>)
            list = new ArrayList<>((Collection<?>)obj);

        return list;
    }

    public static List<String> convertObjectToStringList(Object obj) {
        List<?> intermediateList = new ArrayList<>();

        if(obj == null)
            return new ArrayList<>();

        if (obj.getClass().isArray())
            intermediateList = Arrays.asList((Object[])obj);
        else if (obj instanceof Collection<?>)
            intermediateList = new ArrayList<>((Collection<?>)obj);

        // 중간 리스트를 ArrayList<String>으로 변환
        List<String> stringList = new ArrayList<>();
        for (Object element : intermediateList) {
            stringList.add(String.valueOf(element)); // 객체를 문자열로 변환하여 추가
        }

        return stringList;
    }
}
