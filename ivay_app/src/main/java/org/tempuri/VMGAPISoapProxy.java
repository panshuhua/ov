package org.tempuri;

import java.rmi.RemoteException;

public class VMGAPISoapProxy implements org.tempuri.VMGAPISoap {
  private String _endpoint = null;
  private org.tempuri.VMGAPISoap vMGAPISoap = null;
  
  public VMGAPISoapProxy() {
    _initVMGAPISoapProxy();
  }
  
  public VMGAPISoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initVMGAPISoapProxy();
  }
  
  private void _initVMGAPISoapProxy() {
    try {
      vMGAPISoap = (new org.tempuri.VMGAPILocator()).getVMGAPISoap();
      if (vMGAPISoap != null) {
        if (_endpoint != null) {
          ((javax.xml.rpc.Stub)vMGAPISoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        } else {
          _endpoint = (String)((javax.xml.rpc.Stub)vMGAPISoap)._getProperty("javax.xml.rpc.service.endpoint.address");
        }
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (vMGAPISoap != null) {
      ((javax.xml.rpc.Stub)vMGAPISoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    }
    
  }
  
  public org.tempuri.VMGAPISoap getVMGAPISoap() {
    if (vMGAPISoap == null) {
      _initVMGAPISoapProxy();
    }
    return vMGAPISoap;
  }
  
  @Override
  public org.tempuri.ApiAdsReturn adsSendSms(String[] msisdns, String alias, String message, String sendTime, String authenticateUser, String authenticatePass) throws RemoteException{
    if (vMGAPISoap == null) {
      _initVMGAPISoapProxy();
    }
    return vMGAPISoap.adsSendSms(msisdns, alias, message, sendTime, authenticateUser, authenticatePass);
  }

  @Override
  public org.tempuri.ApiBulkReturn adsGPCSendSms(String msisdn, String alias, String message, String sendTime, String authenticateUser, String authenticatePass) throws RemoteException{
    if (vMGAPISoap == null) {
      _initVMGAPISoapProxy();
    }
    return vMGAPISoap.adsGPCSendSms(msisdn, alias, message, sendTime, authenticateUser, authenticatePass);
  }

  @Override
  public org.tempuri.ApiBulkReturn bulkSendSms(String msisdn, String alias, String message, String sendTime, String authenticateUser, String authenticatePass) throws RemoteException{
    if (vMGAPISoap == null) {
      _initVMGAPISoapProxy();
    }
    return vMGAPISoap.bulkSendSms(msisdn, alias, message, sendTime, authenticateUser, authenticatePass);
  }

  @Override
  public org.tempuri.ApiBulkReturn bulkSendSmsWithRequestId(String requestId, String msisdn, String alias, String message, String sendTime, String authenticateUser, String authenticatePass) throws RemoteException{
    if (vMGAPISoap == null) {
      _initVMGAPISoapProxy();
    }
    return vMGAPISoap.bulkSendSmsWithRequestId(requestId, msisdn, alias, message, sendTime, authenticateUser, authenticatePass);
  }

  @Override
  public org.tempuri.ApiBulkBlockReturn bulkMessageBlockReciver(String[] msisdns, String alias, String message, String sendTime, String authenticateUser, String authenticatePass) throws RemoteException{
    if (vMGAPISoap == null) {
      _initVMGAPISoapProxy();
    }
    return vMGAPISoap.bulkMessageBlockReciver(msisdns, alias, message, sendTime, authenticateUser, authenticatePass);
  }

  @Override
  public org.tempuri.ApiBulkReturn bulkSendSmsTest(String msisdn, String alias, String message, String sendTime, String authenticateUser, String authenticatePass) throws RemoteException{
    if (vMGAPISoap == null) {
      _initVMGAPISoapProxy();
    }
    return vMGAPISoap.bulkSendSmsTest(msisdn, alias, message, sendTime, authenticateUser, authenticatePass);
  }

  @Override
  public org.tempuri.BalanceInfo getBalance(String authenticateUser, String authenticatePass) throws RemoteException{
    if (vMGAPISoap == null) {
      _initVMGAPISoapProxy();
    }
    return vMGAPISoap.getBalance(authenticateUser, authenticatePass);
  }
  
  
  public static void main(String[] args) {
	System.out.println("发送");
	VMGAPISoapProxy proxy= new VMGAPISoapProxy("http://brandsms.vn:8018/VMGAPI.asmx");
	
	String msisdn="0779989078";
	String alias="VMGtest";
	String message="xingbuxing";
	String sendTime="";
	String authenticateUser="vmgtest1";
	String authenticatePass="vmG@123b";
	
	
	try {
		ApiBulkReturn ret = proxy.bulkSendSms(msisdn, alias, message, sendTime, authenticateUser, authenticatePass);
		
		System.out.println("返回："+ ret.getError_code());
		System.out.println("返回："+ ret.getError_detail());
	} catch (RemoteException e) {
		e.printStackTrace();
	}
	
	
}
  
}