/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gbc.mc.zp;

import com.gbc.mc.common.CommonFunction;
import com.gbc.mc.common.Config;
import com.gbc.mc.common.HttpHelper;
import com.gbc.mc.zp.define.ZPReturnMessage;
import com.gbc.mc.zp.define.ZPReturnCode;
import com.gbc.mc.common.JsonParserUtil;
import com.gbc.mc.controller.NotifyController;
import com.gbc.mc.hmacutil.HMACUtil;
import com.gbc.mc.ws.WSDefine;
import com.gbc.mc.ws.WSMessageType;
import com.gbc.mc.ws.WSResponse;
import com.gbc.mc.zp.define.ZPDefineName;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author haint3
 */
public class ZalopayAPI {

    protected static final Logger logger = Logger.getLogger(ZalopayAPI.class);
    private static Gson _gson = new Gson();
    
    private static String _createOrderTokenUrl = "https://zpstaging.zing.vn/v001/zp/createorder/";
    
    private static long _appId              = 0;
    private static String _appUser          = "";
    private static String _key1             = "";
    public static String _key2              = "";
    
    public static String _hmacAlgorithm     = "HmacSHA256";

    public static boolean initialize() {
        _createOrderTokenUrl = Config.getParam("zalopay", "create_order_url");
        _hmacAlgorithm = Config.getParam("zalopay", "hmac_algorithm");
        _appUser = Config.getParam("zalopay", "appuser");
        _appId = Long.valueOf(Config.getParam("zalopay", "appid"));
        _key1 = Config.getParam("zalopay", "key1");
        _key2 = Config.getParam("zalopay", "key2");
        
        return true;
    }
     
    public String genAppTransId() {
        return CommonFunction.getCurrentDateTimeStringFormat("yyMMddHHmmss");
    }
    
    public String createOrder(String data) {
        logger.info("ZalopayAPI.createOrder: data = " + data);
        String content = "";
        
        JsonObject orderJobj = JsonParserUtil.parseJsonObject(data);
        if (orderJobj == null) {
            content = ZPFormatResponse.format(ZPReturnCode.CREATE_ORDER_FAIL, ZPReturnMessage.CREATE_ORDER_DATA_INVALID);
            return content;
        }
        
        String amount = orderJobj.has("amount") ? orderJobj.get("amount").getAsString() : "";
        JsonArray arrItems = orderJobj.has("items") ? orderJobj.get("items").getAsJsonArray() : null;
        
        String appTransId = genAppTransId();
        String appTime = String.valueOf(CommonFunction.getCurrentDateTimeNum());
        String embedData = "123456";
        String items = _gson.toJson(arrItems);
        
        String description = "Bán Hàng";
        
        StringBuilder hmacInput = new StringBuilder();
        hmacInput.append(_appId).append("|")
                .append(appTransId).append("|")
                .append(_appUser).append("|")
                .append(amount).append("|")
                .append(appTime).append("|")
                .append(embedData).append("|")
                .append(items);
        System.out.println("HMAC input: " + hmacInput.toString());
        String calHmac = HMACUtil.HMacHexStringEncode(_hmacAlgorithm, _key1, hmacInput.toString());
        
        JsonObject orderObject = new JsonObject();
        orderObject.addProperty("appid", _appId);
        orderObject.addProperty("appuser", _appUser);
        orderObject.addProperty("apptime", appTime);
        orderObject.addProperty("amount", amount);
        orderObject.addProperty("apptransid", appTransId);
        orderObject.addProperty("embeddata", embedData);
        orderObject.addProperty("item", items);
        orderObject.addProperty("description", description);
        orderObject.addProperty("mac", calHmac);
        
        content = ZPFormatResponse.format(ZPReturnCode.CREATE_ORDER_SUCCESS, ZPReturnMessage.CREATE_ORDER_SUCCESS, "qrinfo", _gson.toJson(orderObject));
        logger.info("ZalopayAPI.createOrder: response = " + content);
        return content;
    }
    
    public String createOrderToken(Map<String, String> paramMap) {
        String content = "";
        try {
            logger.info("ZalopayAPI.createOrder: param = ");
            logger.info(paramMap);
            
            String amount = paramMap.get("amount");
            String items = paramMap.get("items");
            
            String appTransId = genAppTransId();
            String appTime = String.valueOf(CommonFunction.getCurrentDateTimeNum());
            String embedData = "123456";
            
            String description = "Bán Hàng";
            
            StringBuilder hmacInput = new StringBuilder();
            hmacInput.append(_appId).append("|")
                    .append(appTransId).append("|")
                    .append(_appUser).append("|")
                    .append(amount).append("|")
                    .append(appTime).append("|")
                    .append(embedData).append("|")
                    .append(items);
            System.out.println("HMAC input: " + hmacInput.toString());
            String calHmac = HMACUtil.HMacHexStringEncode(_hmacAlgorithm, _key1, hmacInput.toString());
            
            paramMap.put("appid", String.valueOf(_appId));
            paramMap.put("appuser", _appUser);
            paramMap.put("apptime", appTime);
            paramMap.put("amount", amount);
            paramMap.put("apptransid", appTransId);
            paramMap.put("embeddata", embedData);
            paramMap.put("item", items);
            paramMap.put("description", description);
            paramMap.put("mac", calHmac);
            
            logger.info("param map:");
            logger.info(paramMap);
            String rs = HttpHelper.sendHttpPostFormData(_createOrderTokenUrl, paramMap, 20000);
            logger.info("response from zp: " + rs);
            JsonObject objTemp = JsonParserUtil.parseJsonObject(rs);
            objTemp.addProperty("appid", _appId);
            content = ZPFormatResponse.format(ZPReturnCode.CREATE_ORDER_SUCCESS, ZPReturnMessage.CREATE_ORDER_SUCCESS, "qrinfo", _gson.toJson(objTemp));
            
        } catch (IOException ex) {
            logger.error("ZalopayAPI.createOrderToken: ex = " + ex.getMessage(), ex);
            content = ZPFormatResponse.format(ZPReturnCode.CREATE_ORDER_FAIL, ZPReturnMessage.CREATE_ORDER_SYSTEM_ERROR);
        }
        
        logger.info("ZalopayAPI.createOrderToken: response = " + content);
        return content;
    }

    public String processPaymentCallback(String data) {
        String content = "";
        logger.info("ZalopayAPI.processPaymentCallback: data = " + data);
        
        JsonObject jobjData = JsonParserUtil.parseJsonObject(data);
        if (jobjData == null) {
            logger.warn("ZPGatewayAPI.processPaymentCallback: json data invalid: " + data);
            content = ZPFormatResponse.format(ZPReturnCode.PAYMENT_DATA_INVALID, ZPReturnMessage.PAYMENT_DATA_INVALID);
            return content;
        }

        boolean ok = ZPDataVerifyHelper.getInstance().checkCallbackValid(jobjData);
        if (ok == false) {
            logger.warn("ZPGatewayAPI.processPaymentCallback: check json data invalid: " + data);
            content = ZPFormatResponse.format(ZPReturnCode.PAYMENT_DATA_INVALID, ZPReturnMessage.PAYMENT_DATA_INVALID);
            return content;
        }

        /*Update payment status
        * Do something ....
        */
        
        //Prepare data send to ws client
        String dataInJson = jobjData.get(ZPDefineName.DATA).getAsString();
        JsonObject jobjDataCallBack = JsonParserUtil.parseJsonObject(dataInJson);
        String appTransId = jobjDataCallBack.get(ZPDefineName.APP_TRANS_ID).getAsString();
        JsonObject resJobj = new JsonObject();
        resJobj.addProperty("msg_type", WSMessageType.S2C_PAYMENT_DONE);
        JsonObject dataJobj = new JsonObject();
        dataJobj.addProperty(ZPFormatResponse.RETURN_CODE, ZPReturnCode.PAYMENT_SUCCESS);
        dataJobj.addProperty(ZPFormatResponse.RETURN_MESSAGE, ZPReturnMessage.PAYMENT_SUCCESS);
        dataJobj.addProperty(ZPDefineName.APP_TRANS_ID, appTransId);
        resJobj.add("dt", dataJobj);
        
        //For demo: send all client
        NotifyController.sendMessageToAllClient(resJobj);
        
        content = ZPFormatResponse.format(1, "Giao dịch thành công");
        
        logger.info("ZalopayAPI.processPaymentCallback: response = " + content);
        return content;
    }

    public String doRefund(String data) {
        
        return "";
    }
    
}
