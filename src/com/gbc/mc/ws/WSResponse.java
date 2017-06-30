/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gbc.mc.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 * @author haint3
 */
public class WSResponse {
    
    private static final Gson _gson = new GsonBuilder().create();
    
    public static String format(int msgType, int error, String msg) {
                
        if (error == 0 && msg.equals("")) {
            msg = "No error";
        }
        
        JsonObject json = new JsonObject();
        json.addProperty("msg_type", msgType);
        json.addProperty("err", error);
        json.addProperty("msg", msg);
        
        return _gson.toJson(json);
    }
    
    public static String format(int msgType, int error, String msg, String data) {
                
        if (error == 0 && msg.equals("")) {
            msg = "No error";
        }
        
        JsonObject json = new JsonObject();
        json.addProperty("msg_type", msgType);
        json.addProperty("err", error);
        json.addProperty("msg", msg);
        json.addProperty("dt", data);
        
        return _gson.toJson(json);
    }
    
    public static String format(int msgType, int error, String msg, Object objData) {
        
        if (error == 0 && msg.equals("")) {
            msg = "No error";
        }
        
        JsonObject json = new JsonObject();
        json.addProperty("msg_type", msgType);
        json.addProperty("err", error);
        json.addProperty("msg", msg);
        json.add("dt", _gson.toJsonTree(objData));
        
        return _gson.toJson(json);
    }
    
    public static String format(int msgType, int error, String msg, String objName, Object objData) {
        
        if (error == 0 && msg.equals("")) {
            msg = "No error";
        }
        
        JsonObject json = new JsonObject();
        json.addProperty("msg_type", msgType);
        json.addProperty("err", error);
        json.addProperty("msg", msg);
        JsonObject jsonParent = new JsonObject();
        jsonParent.add(objName, _gson.toJsonTree(objData));
        json.add("dt", jsonParent);
        
        return _gson.toJson(json);
    }
    
    public static String format(int msgType, int error, String msg, String objName1, Object objData1, String objName2, Object objData2) {
        
        if (error == 0 && msg.equals("")) {
            msg = "No error";
        }
        
        JsonObject json = new JsonObject();
        json.addProperty("msg_type", msgType);
        json.addProperty("err", error);
        json.addProperty("msg", msg);
        JsonObject jsonParent = new JsonObject();
        jsonParent.add(objName1, _gson.toJsonTree(objData1));
        jsonParent.add(objName2, _gson.toJsonTree(objData2));
        json.add("dt", jsonParent);
        
        return _gson.toJson(json);
    }
    
    public static String format(int msgType, int error, String msg, JsonElement jsonEle) {
        
        if (error == 0 && msg.equals("")) {
            msg = "No error";
        }
       
        JsonObject json = new JsonObject();
        json.addProperty("msg_type", msgType);
        json.addProperty("err", error);
        json.addProperty("msg", msg);
        json.add("dt", jsonEle);
        return _gson.toJson(json);
    }
}
