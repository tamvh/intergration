/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gbc.mc.zp;

import com.gbc.mc.common.CommonFunction;
import com.gbc.mc.hmacutil.HMACUtil;
import com.gbc.mc.zp.define.ZPDefineName;
import com.google.gson.JsonObject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;

/**
 *
 * @author haint3
 */
public class ZPDataVerifyHelper {
    private static ZPDataVerifyHelper _instance = null;
    private static final Lock createLock_ = new ReentrantLock();
    protected final Logger logger = Logger.getLogger(this.getClass());
    
    public static ZPDataVerifyHelper getInstance()  {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new ZPDataVerifyHelper();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }
    
    public boolean checkCallbackValid(JsonObject jobj) {
        String dataInJson = jobj.get(ZPDefineName.DATA).getAsString();
        String macInJson = jobj.get(ZPDefineName.MAC).getAsString();
        
        String macCal = HMACUtil.HMacHexStringEncode(ZalopayAPI._hmacAlgorithm, ZalopayAPI._key2, dataInJson);
        if (macCal.compareTo(macInJson) == 0) {
            return true;
        }
        
        return false;
    }
}
