package com.newcoder.community.util;

import com.alibaba.fastjson2.JSONObject;
import com.mysql.cj.util.StringUtils;
import org.springframework.util.DigestUtils;

import java.security.DigestException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonUtil {

    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    //MD5加密：只能加密不能解密
    //key是加过盐的密码
    public static String md5(String key){
        if(StringUtils.isNullOrEmpty(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

}
