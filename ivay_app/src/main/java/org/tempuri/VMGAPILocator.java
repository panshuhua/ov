/**
 * VMGAPILocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class VMGAPILocator extends org.apache.axis.client.Service implements org.tempuri.VMGAPI {

    public VMGAPILocator() {
    }


    public VMGAPILocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public VMGAPILocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for VMGAPISoap
    private String VMGAPISoap_address = "http://brandsms.vn:8018/VMGAPI.asmx";

    @Override
    public String getVMGAPISoapAddress() {
        return VMGAPISoap_address;
    }

    // The WSDD service name defaults to the port name.
    private String VMGAPISoapWSDDServiceName = "VMGAPISoap";

    public String getVMGAPISoapWSDDServiceName() {
        return VMGAPISoapWSDDServiceName;
    }

    public void setVMGAPISoapWSDDServiceName(String name) {
        VMGAPISoapWSDDServiceName = name;
    }

    @Override
    public org.tempuri.VMGAPISoap getVMGAPISoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(VMGAPISoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getVMGAPISoap(endpoint);
    }

    @Override
    public org.tempuri.VMGAPISoap getVMGAPISoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.VMGAPISoapStub _stub = new org.tempuri.VMGAPISoapStub(portAddress, this);
            _stub.setPortName(getVMGAPISoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setVMGAPISoapEndpointAddress(String address) {
        VMGAPISoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.tempuri.VMGAPISoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.VMGAPISoapStub _stub = new org.tempuri.VMGAPISoapStub(new java.net.URL(VMGAPISoap_address), this);
                _stub.setPortName(getVMGAPISoapWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("VMGAPISoap".equals(inputPortName)) {
            return getVMGAPISoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    @Override
    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://org.tempuri.org/", "VMGAPI");
    }

    private java.util.HashSet ports = null;

    @Override
    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://org.tempuri.org/", "VMGAPISoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {

if ("VMGAPISoap".equals(portName)) {
            setVMGAPISoapEndpointAddress(address);
        }
        else
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
