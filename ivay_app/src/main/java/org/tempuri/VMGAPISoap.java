/**
 * VMGAPISoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface VMGAPISoap extends java.rmi.Remote {
    public org.tempuri.ApiAdsReturn adsSendSms(String[] msisdns, String alias, String message, String sendTime, String authenticateUser, String authenticatePass) throws java.rmi.RemoteException;
    public org.tempuri.ApiBulkReturn adsGPCSendSms(String msisdn, String alias, String message, String sendTime, String authenticateUser, String authenticatePass) throws java.rmi.RemoteException;
    public org.tempuri.ApiBulkReturn bulkSendSms(String msisdn, String alias, String message, String sendTime, String authenticateUser, String authenticatePass) throws java.rmi.RemoteException;
    public org.tempuri.ApiBulkReturn bulkSendSmsWithRequestId(String requestId, String msisdn, String alias, String message, String sendTime, String authenticateUser, String authenticatePass) throws java.rmi.RemoteException;
    public org.tempuri.ApiBulkBlockReturn bulkMessageBlockReciver(String[] msisdns, String alias, String message, String sendTime, String authenticateUser, String authenticatePass) throws java.rmi.RemoteException;
    public org.tempuri.ApiBulkReturn bulkSendSmsTest(String msisdn, String alias, String message, String sendTime, String authenticateUser, String authenticatePass) throws java.rmi.RemoteException;
    public org.tempuri.BalanceInfo getBalance(String authenticateUser, String authenticatePass) throws java.rmi.RemoteException;
}
