/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gbc.mc.controller;

import com.gbc.mc.common.CommonModel;
import com.gbc.mc.zp.ZPFormatResponse;
import com.gbc.mc.zp.ZalopayAPI;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author haint3
 */
public class ZPCreateOrderController extends HttpServlet {
    protected static final Logger logger = Logger.getLogger(ZPCreateOrderController.class);
    private static Gson _gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }
    
    private void handle(HttpServletRequest req, HttpServletResponse resp) {
        try {
            processs(req, resp);
        } catch (Exception ex) {
            logger.error(getClass().getSimpleName() + ".handle: " + ex.getMessage(), ex);
        }
    }
    
    private void processs(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("ZPCreateOrderController.process");
        String data = req.getParameter(ZPFormatResponse.DATA) != null ? req.getParameter(ZPFormatResponse.DATA) : "";
        String content = "";
        CommonModel.prepareHeader(resp, CommonModel.HEADER_JS);     
        
        ZalopayAPI zpGatewayAPI= new ZalopayAPI();
        content = zpGatewayAPI.createOrder(data);
        
        CommonModel.out(content, resp);
    }
}
