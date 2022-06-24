package com.nfc_read_write.nfc_read_write_plugin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ResultUtil implements Serializable {
    private Integer code = 200;
    private String message ="请求成功";
    private Object data;
    public ResultUtil() {
    }

    public ResultUtil(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResultUtil(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultUtil(String message, Object data) {
        this.message = message;
        this.data = data;
    }
    public ResultUtil(String message ) {
        this.message = message;
    }
    public ResultUtil(Object data ) {
        this.data = data;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    public static Map<String,Object> ok( ) {
        return new ResultUtil( ).toMap();
    }
    public static Map<String,Object> ok(String message) {
        return new ResultUtil(message).toMap();
    }
    public  static Map<String,Object> ok(String message,Object data) {
        return new ResultUtil(message,data).toMap();
    }
    public static Map<String,Object> ok(Integer code, String message, Object data) {
        return new ResultUtil(code,message,data).toMap();
    }


    public static Map<String,Object> fail(String code, String message) {
        return new ResultUtil(code,message).toMap();
    }
    public static Map<String,Object> fail(  String message) {
        return new ResultUtil(message).toMap();
    }
    public  Map<String,Object> toMap()  {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code",this.code);
        map.put("message",this.message);
        map.put("data",this.data);

        return map;
    }

    @Override
    public String toString() {
        return "ResultUtil{" +
                "code=" + code +
                ", msg='" + message + '\'' +
                ", data=" + data +
                '}';
    }


}
