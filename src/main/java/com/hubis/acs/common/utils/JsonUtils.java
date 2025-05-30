package com.hubis.acs.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.common.handler.exception.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.Message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils {

	public static String getMessageValue(JSONObject message, String key, boolean isMandatory) throws CustomException
	{
		String value = getMessageValue(message, key);
				
		if (CommonUtils.isNullOrEmpty(value))
		{
			if (isMandatory)
			{
//				logger.error("Parameter Not Found [" + key + "]");
				CommonUtils.validateMandatoryParameter(key, value);
			}
		}
				
		return value;
	}
	
	public static String getMessageValue(JSONObject message, String key, String defaultValue)
	{
		String value = getMessageValue(message, key);
		
		if (CommonUtils.isNullOrEmpty(value))
			value = defaultValue;
		
		return value;
	}
	
	public static String getMessageValue(JSONObject message, String key)
	{
		String res = getJsonString(message, key);
		
//		logger.info("Get Parameter Value, [" + key + "=" + res + "]");
		
		return res; 
	}
	public static Double getMessageValueByDouble(JSONObject message, String key)
	{
		Double res = getJsonDouble(message, key);
		
//		logger.info("Get Parameter Value, [" + key + "=" + res + "]");
		
		return res; 
	}

	public static JSONObject getMessageObject(JSONObject message, String key)
	{
		JSONObject res = null;
		
		try
		{
			res = getJsonObject(message, key); 
		}
		catch (Exception e) {}
		
		return res;
	}
	
	public static JSONObject getMessageObject(JSONArray message, int index)
	{
		JSONObject res = null;
		
		try
		{
			res = getJsonObject(message, index); 
		}
		catch (Exception e) {}
		
		return res;
	}

	public static JSONArray getMessageList(JSONObject message, String key)
	{
		JSONArray res = null;
		
		try
		{
			res = getJsonArray(message, key);
		}
		catch (Exception e) {}
		
		return res;
	}
	
	public static String getJsonString(JSONObject json, String key)
	{
		String res = "";
		
		try
		{
			Object o = json.get(key);
			
			if (isJSONNull(o))
				return res;
			
			if (isArray(o))
				return res;
			
			res = ConvertUtils.toString(o); 
		}
		catch (Exception e) {}
		
		return res;
	}
	
	public static Double getJsonDouble(JSONObject json, String key)
	{
		Double res = 0.0;
		
		try
		{
			Object o = json.get(key);
			
			if (isJSONNull(o))
				return res;
			
			if (isArray(o))
				return res;
			
			res = ConvertUtils.toDouble(o); 
		}
		catch (Exception e) {}
		
		return res;
	}
	
	public static String getJsonString(JSONArray jsonList, int index, String key)
	{
		String res = "";
		
		try
		{
			res = jsonList.getJSONObject(index).getString(key);
		}
		catch (Exception e) {}
		
		return res;
	}
	
	public static JSONObject getJsonObject(JSONObject json, String key)
	{
		JSONObject res = null;
		
		try
		{
			res = json.getJSONObject(key);
			
			if (CommonUtils.isNullOrEmpty(res))
				return null;
		}
		catch (Exception e) {}
		
		return res;
	}

	public static JSONObject getJsonObject(JSONArray jsonList, int index)
	{
		JSONObject res = null;
		
		try
		{
			res = jsonList.getJSONObject(index);

			if (CommonUtils.isNullOrEmpty(res))
				return null;
		}
		catch (Exception e) {}
		
		return res;
	}
	
	public static JSONArray getJsonArray(JSONObject json, String key)
	{
		JSONArray res = null;
		
		try
		{
			res = json.getJSONArray(key);
		}
		catch (Exception e) {}
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getJsonItem(Object json, Object...key)
	{
		try
		{
			Object res = json;
			
			for (int i=0; i<key.length; i++)
			{
				if (key[i] instanceof String)
				{
					if (isObject(res))
					{
						res = ((JSONObject) res).get((String) key[i]);
					}
					else
					{
						return null;
					}
				}
				else if (key[i] instanceof Integer)
				{
					if (isArray(res))
					{
						res = ((JSONArray) res).get(ConvertUtils.toInt(ConvertUtils.toString(key[i])));
					}
					else 
					{
						return null;
					}
				}
			}

			return (T) res;
		}
		catch (Exception e) {}
		
		return null;
	}
	
	public static Object findJsonItem(Object json, String key)
	{
		Object res = null;
		
		try
		{
			res = ((JSONObject) json).get(key);
		}
		catch (Exception e) {}

		if (!CommonUtils.isNullOrEmpty(res))
			return res;
		
		if (isObject(json))
		{
			JSONObject jo = (JSONObject) json;
			
			res = getJsonObject(jo, key);

			if (!CommonUtils.isNullOrEmpty(res))
				return res;
			
			for (Object o : jo.keySet())
			{
				String k = ConvertUtils.toString(o);
				
				Object obj = jo.get(k);
				
				if (isArray(obj))
				{
					res = findJsonItem((JSONArray) obj, key);
				}
				else if (isObject(obj))
				{
					res = findJsonItem((JSONObject) obj, key);
				}
				else 
				{
					res = ((JSONObject) json).get(key);
				}
				
				if (CommonUtils.isNullOrEmpty(res))
					continue;
				
				break;
			}	
		}
		else if (isArray(json))
		{
			JSONArray ja = (JSONArray) json;
			
			for (int i=0; i<ja.length(); i++)
			{
				Object obj = ja.get(i);
				
				if (isArray(obj))
				{
					res = findJsonItem((JSONArray) obj, key);
				}
				else if (isObject(obj))
				{
					res = findJsonItem((JSONObject) obj, key);
				}
				else 
				{
					res = ((JSONObject) json).get(key);
				}
				
				if (CommonUtils.isNullOrEmpty(res))
					continue;
				
				break;
			}
		}
		else
		{
			res = ((JSONObject) json).get(key);
		}

		return res;
	}
	
	public static Object getJsonObjectByIndex(Object obj, int...indexes)
	{
		try
		{
			Object data = obj;

            for (int index : indexes) {
                if (data instanceof JSONObject o) {
                    data = o.get((String) o.keySet().toArray()[0]);
                }

                if (data instanceof JSONArray) {
                    int idx = index;

                    JSONObject o = (JSONObject) ((JSONArray) data).get(idx);
                    data = o.get((String) o.keySet().toArray()[0]);
                } else {
                    data = null;
                    break;
                }
            }
			
			return data;
		} 
		catch(Exception ex) {}
		
		return null;
	}
	
	public static void putJsonObject(JSONObject json, String key, Object value)
	{
		try
		{
			json.put(key, value);
		}
		catch (Exception e) {}
	}

	public static void putAllJsonObject(JSONObject json, JSONObject value)
	{
		try {
			for (String key : value.keySet()) {
				putJsonObject(json, key, value.get(key)); // 각 키-값 쌍을 추가
			}
		} catch (Exception e) {
			e.printStackTrace(); // 예외 처리
		}
	}
	
	public static void addJsonArray(JSONArray listJson, Object value)
	{
		try
		{
			listJson.put(value);
		}
		catch (Exception e) {}
	}

	
	public static JSONObject toJson(String jsonStr)
	{
		try
		{
			return new JSONObject(jsonStr);
		}
		catch (Exception e) {}
		
		return null;
	}
	
	public static String toString(JSONObject json)
	{
		String res = "";
		
		try
		{
			res = json.toString();
		}
		catch (Exception e) {}
		
		return res;
	}
	
	
	public static Map<String, Object> toMap(JSONObject jsonObj)
	{
		try 
		{
			return toMap(toString(jsonObj));	
		} 
		catch (Exception e) {}

	    return null;
	}
	
	public static Map<String, Object> toMap(String jsonStr) 
	{	
		try
		{
	        return new ObjectMapper().readValue(jsonStr, new TypeReference<HashMap<String, Object>>(){});
		}
		catch (Exception e) {}

        return null;
	}
	
	public static String getIndentedStyle(String jsonStr)
	{
		try
		{
	        ObjectMapper mapper = new ObjectMapper();
	
	        if (null != jsonStr && !"".equals(jsonStr)) 
	        {
	        	Object json = mapper.readValue(jsonStr, Object.class);

	            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
	        }
		}
		catch (Exception e) {}

        return "";
	}
		
	public static JSONObject makeJsonMessageHeaderToEI(EventInfo eventInfo)
	{
		JSONObject json = new JSONObject();
		putJsonObject(json, BaseConstants.TAG_NAME.RequestId, eventInfo.getRequestId());
		putJsonObject(json, BaseConstants.TAG_NAME.WorkId, eventInfo.getWorkId());
		//putJsonObject(json, BaseConstants.TAG_NAME.EventName, eventInfo.getWorkId());
		//putJsonObject(json, BaseConstants.TAG_NAME.SystemID, "");
		putJsonObject(json, BaseConstants.TAG_NAME.TransactionId, eventInfo.getTransactionId());
		//putJsonObject(json, BaseConstants.TAG_NAME.Time, TimeUtils.getTimeString(eventInfo.getTime()));
		
		return json;
	}
	
	public static JSONObject makeJsonMessageHeaderToUI(EventInfo eventInfo)
	{
		JSONObject json = new JSONObject();
		putJsonObject(json, BaseConstants.TAG_NAME.RequestId, eventInfo.getRequestId());
		putJsonObject(json, BaseConstants.TAG_NAME.WorkId, eventInfo.getWorkId());
		putJsonObject(json, BaseConstants.TAG_NAME.TransactionId, eventInfo.getTransactionId());
		return json;
	}
	
	public static boolean isJSONNull(Object obj) {
		return !isJSONObject(obj) && jsonEquals(obj);
	}

	// 객체가 JSONObject인지 체크하는 메서드
	private static boolean isJSONObject(Object obj) {
		return obj instanceof JSONObject;
	}

	// 객체가 null 또는 JSONObject의 null 객체인지 체크하는 메서드
	public static boolean jsonEquals(Object object) {
		return object == null ||
				(isJSONObject(object) && ((JSONObject) object).isEmpty()) || // JSONObject가 비어있는지 체크
				org.json.JSONObject.NULL.equals(object) ||
				"null".equals(object);
	}

	public static boolean isArray(Object obj)
	{
		return obj != null && obj.getClass().isArray() || obj instanceof Collection || obj instanceof JSONArray;
	}

	public static boolean isObject(Object obj)
	{
		return obj != null;
	}

	public static JSONObject validationMessageToJsonObject(Message<?> message)
	{
		try {
			Object payload = message.getPayload();
			String jsonStr;

			if (payload instanceof String) {
				jsonStr = (String) payload;
			} else {
				// JSON 문자열로 안전 변환
				jsonStr = new ObjectMapper().writeValueAsString(payload);
			}

			JSONObject reqMsg = JsonUtils.toJson(jsonStr);
			if (reqMsg == null) {
				throw new RuntimeException("Invalid JSON");
			}

			return reqMsg;
		}catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
}
