/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gbc.mc.zp.define;

/**
 *
 * @author haint3
 */
public class ZPReturnCode {
    
    //create order
    public static final int CREATE_ORDER_SUCCESS            = 1;
    public static final int CREATE_ORDER_FAIL               = 0;
    
    //paymnet
    public static final int PAYMENT_CREATE_ORDER            = -1;
    public static final int PAYMENT_SUCCESS                 = 1;
    public static final int PAYMENT_COINCIDENCE_TRANS_ID    = 2;
    public static final int PAYMENT_RECALLBACK              = 0;
    public static final int PAYMENT_REFUND_FOR_USER         = 3;
    public static final int PAYMENT_DATA_INVALID            = 4;
    public static final int PAYMENT_SYSTEM_ERROR            = 5;
    public static final int PAYMENT_CANCEL                  = 6;
    
    
}
