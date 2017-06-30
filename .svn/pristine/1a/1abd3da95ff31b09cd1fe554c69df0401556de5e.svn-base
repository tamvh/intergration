/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gbc.mc.controller;


import com.gbc.mc.common.CommonFunction;
import com.gbc.mc.common.CommonModel;
import com.gbc.mc.common.JsonParserUtil;
import com.gbc.mc.ws.ClientSessionInfo;
import com.gbc.mc.ws.WSDefine;
import com.gbc.mc.ws.WSMessageType;
import com.gbc.mc.ws.WSResponse;
import com.gbc.mc.zp.define.ZPDefineName;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 *
 * @author haint3
 */
@WebSocket
public class NotifyController {
    
    private static final Logger logger = Logger.getLogger(NotifyController.class);
    private static final Gson gson = new Gson();
    private static final Map<String, ClientSessionInfo> _clientSessionMap = Collections.synchronizedMap(new LinkedHashMap<String, ClientSessionInfo>());
    
    @OnWebSocketConnect
    public void onConnect(Session session) {
        Map<String, List<String>> params = session.getUpgradeRequest().getParameterMap();
        
        String deviceId = "";
        List<String> deviceIdList = params.get(WSDefine.DEVICE_ID);
        if (deviceIdList == null || deviceIdList.isEmpty()) {
            session.close();
            return;
        } else {
            deviceId = deviceIdList.get(0);
        }
        
        
        List<HttpCookie> listCookie = new ArrayList<>();
        listCookie.add(new HttpCookie(WSDefine.DEVICE_ID, deviceId));
        session.getUpgradeRequest().setCookies(listCookie);
        
        logger.info("NotifyController.onConnect: client deviceId = " + deviceId);
        
        ClientSessionInfo oldClient = _clientSessionMap.get(deviceId);
        if (oldClient != null) {
            logger.info("NotifyController.onConnect: close old client deviceId = " + deviceId);
            oldClient.getSession().close();
        }
        
        _clientSessionMap.put(deviceId, new ClientSessionInfo(session, deviceId));
        session.setIdleTimeout(10*60*1000);
    }
    
    @OnWebSocketClose
    public void onClose(Session session, int status, String reason) {
        session.close();
        removeSession(session);
    }

    @OnWebSocketMessage
    public void onText(Session session, String message) {
        JsonObject jobj = JsonParserUtil.parseJsonObject(message);
        if (jobj == null || jobj.has(WSDefine.MESSAGE_TYPE) == false) {
            return;
        }
        
        int msgType = jobj.get(WSDefine.MESSAGE_TYPE).getAsInt();
        if (msgType == WSMessageType.C2S_PING) {
            sendPongMessage(session);
        }
    }
    
    public void removeSession(Session session) {
        List<HttpCookie> listCookie = session.getUpgradeRequest().getCookies();
        for (HttpCookie cookie : listCookie) {
            if (cookie.getName().compareToIgnoreCase(WSDefine.DEVICE_ID) == 0) {
                _clientSessionMap.remove(cookie.getValue());
                break;
            }
        }
    }
    
    private void sendPongMessage(Session session) {
        String response = WSResponse.format(WSMessageType.S2C_PONG, 0, "Pong from server");
        sendMessage(session, response);
    }
    
    private boolean sendMessage(Session session, String data) {
        try {
            session.getRemote().sendString(data);
        } catch (IOException ex) {
            logger.error("NotifyController.sendMessage: " + ex.getMessage(), ex);
            return false;
        }
        
        return true;
    }
    
    public static boolean sendMessageToAllClient(Object dataObject) {
        String data = gson.toJson(dataObject);
        _clientSessionMap.forEach( (key, value) -> {
            sendMessageToClient(key, data);
        });
        
        return true;
    }
    
    public static boolean sendMessageToClient(String deviceId, Object dataObject) {
        return sendMessageToClient(deviceId, gson.toJson(dataObject));
    }
    
    public static boolean sendMessageToClient(String deviceId, String data) {
        if (_clientSessionMap.containsKey(deviceId)) {
            ClientSessionInfo clientSession = _clientSessionMap.get(deviceId);
            if (clientSession != null) {
                try {
                    logger.info("NotifyController.sendMessageToClient: response to client deviceId = " + deviceId + ", data = " + data);
                    clientSession.getSession().getRemote().sendString(data);
                    return true;
                } catch (IOException ex) {
                    logger.error("NotifyController.sendMessageToClient: " + ex.getMessage(), ex);
                }
            }
        }
        
        return false;
    }
}
