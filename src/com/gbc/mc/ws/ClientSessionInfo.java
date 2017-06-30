/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gbc.mc.ws;

import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author diepth
 */
public class ClientSessionInfo {
    private Session _session = null;
    private String  _deviceId = "";
    
    public ClientSessionInfo(Session session, String deviceId) {
        _session = session;
        _deviceId = deviceId;
    }

    public Session getSession() {
        return _session;
    }

    public void setSession(Session _session) {
        this._session = _session;
    }
    
    public String getDeviceId() {
        return _deviceId;
    }

    public void setDeviceId(String _deviceId) {
        this._deviceId = _deviceId;
    }
}
