/**
 * MsisdnInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class MsisdnInfo  implements java.io.Serializable {
    private String msisdn;

    private int isSuccess;

    private String msg;

    private String messageid;

    public MsisdnInfo() {
    }

    public MsisdnInfo(
           String msisdn,
           int isSuccess,
           String msg,
           String messageid) {
           this.msisdn = msisdn;
           this.isSuccess = isSuccess;
           this.msg = msg;
           this.messageid = messageid;
    }


    /**
     * Gets the msisdn value for this MsisdnInfo.
     *
     * @return msisdn
     */
    public String getMsisdn() {
        return msisdn;
    }


    /**
     * Sets the msisdn value for this MsisdnInfo.
     *
     * @param msisdn
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }


    /**
     * Gets the isSuccess value for this MsisdnInfo.
     *
     * @return isSuccess
     */
    public int getIsSuccess() {
        return isSuccess;
    }


    /**
     * Sets the isSuccess value for this MsisdnInfo.
     *
     * @param isSuccess
     */
    public void setIsSuccess(int isSuccess) {
        this.isSuccess = isSuccess;
    }


    /**
     * Gets the msg value for this MsisdnInfo.
     *
     * @return msg
     */
    public String getMsg() {
        return msg;
    }


    /**
     * Sets the msg value for this MsisdnInfo.
     *
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }


    /**
     * Gets the messageid value for this MsisdnInfo.
     *
     * @return messageid
     */
    public String getMessageid() {
        return messageid;
    }


    /**
     * Sets the messageid value for this MsisdnInfo.
     *
     * @param messageid
     */
    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof MsisdnInfo)) return false;
        MsisdnInfo other = (MsisdnInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.msisdn==null && other.getMsisdn()==null) ||
             (this.msisdn!=null &&
              this.msisdn.equals(other.getMsisdn()))) &&
            this.isSuccess == other.getIsSuccess() &&
            ((this.msg==null && other.getMsg()==null) ||
             (this.msg!=null &&
              this.msg.equals(other.getMsg()))) &&
            ((this.messageid==null && other.getMessageid()==null) ||
             (this.messageid!=null &&
              this.messageid.equals(other.getMessageid())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getMsisdn() != null) {
            _hashCode += getMsisdn().hashCode();
        }
        _hashCode += getIsSuccess();
        if (getMsg() != null) {
            _hashCode += getMsg().hashCode();
        }
        if (getMessageid() != null) {
            _hashCode += getMessageid().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MsisdnInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.tempuri.org/", "msisdnInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msisdn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "msisdn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isSuccess");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "isSuccess"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msg");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "msg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messageid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "messageid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
