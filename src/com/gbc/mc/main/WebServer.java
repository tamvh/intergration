/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gbc.mc.main;

import com.gbc.mc.common.Config;
import com.gbc.mc.controller.NotifyController;
import com.gbc.mc.controller.PaymentCallbackController;
import com.gbc.mc.controller.ZPCreateOrderController;
import com.gbc.mc.controller.ZPCreateOrderTokenController;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 *
 * @author diepth
 */
public class WebServer implements Runnable{
    
    private static final Logger logger = Logger.getLogger(WebServer.class);
    private Server server = new Server();
    private static WebServer _instance = null;
    private static final Lock createLock_ = new ReentrantLock();
    
    public static WebServer getInstance() {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new WebServer();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }
    
    @Override
    public void run() {
        try {
            int http_port = Integer.valueOf(Config.getParam("server", "http_port"));
            int ws_port = Integer.valueOf(Config.getParam("server", "ws_port"));
            
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(http_port);
            connector.setIdleTimeout(30000);
            
            ServerConnector connectorWS = new ServerConnector(server);
            connectorWS.setPort(ws_port);
            connectorWS.setIdleTimeout(30000);
            
            server.setConnectors(new Connector[]{connector, connectorWS});
            logger.info("Start server...");
            
            ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
            servletContext.setContextPath("/");
            
            servletContext.addServlet(ZPCreateOrderController.class, "/mcdemo/zp/createorder/*");
            servletContext.addServlet(ZPCreateOrderTokenController.class, "/mcdemo/zp/createordertk/*");
            servletContext.addServlet(PaymentCallbackController.class, "/mcdemo/zp/payment/callback/*");

            WebSocketHandler wsHandler = new WebSocketHandler() {
                @Override
                public void configure(WebSocketServletFactory factory) {
                    factory.register(NotifyController.class);
                }
            };
            
            ContextHandler wsContextHandler = new ContextHandler();
            wsContextHandler.setHandler(wsHandler);
            wsContextHandler.setContextPath("/mcdemo/zp/ntf/*");
            
            ResourceHandler resource_handler = new ResourceHandler();
            resource_handler.setResourceBase("./static/");
            ContextHandler resourceContext = new ContextHandler();
            resourceContext.setContextPath("/static");
            resourceContext.setHandler(resource_handler);
            
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{resourceContext, wsContextHandler, servletContext, new DefaultHandler()});
            server.setHandler(handlers);
            
            server.start();
            server.join();
        } catch (Exception e) {
            logger.error("Cannot start web server: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void stop() throws Exception {
        server.stop();
    }
}
