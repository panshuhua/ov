/**
 * ApiBulkReturn.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class ApiBulkReturn  implements java.io.Serializable {
    private long error_code;

    private String error_detail;

    private long messageId;

    private String requestId;

    public ApiBulkReturn() {
    }

    public ApiBulkReturn(
           long error_code,
           String error_detail,
           long messageId,
           String requestId) {
           this.error_code = error_code;
           this.error_detail = error_detail;
           this.messageId = messageId;
           this.requestId = requestId;
    }


    /**
     * Gets the error_code value for this ApiBulkReturn.
     *
     * @return error_code
     */
    public long getError_code() {
        return error_code;
    }


    /**
     * Sets the error_code value for this ApiBulkReturn.
     *
     * @param error_code
     */
    public void setError_code(long error_code) {
        this.error_code = error_code;
    }


    /**
     * Gets the error_detail value for this ApiBulkReturn.
     *
     * @return error_detail
     */
    public String getError_detail() {
        return error_detail;
    }


    /**
     * Sets the error_detail value for this ApiBulkReturn.
     *
     * @param error_detail
     */
    public void setError_detail(String error_detail) {
        this.error_detail = error_detail;
    }


    /**
     * Gets the messageId value for this ApiBulkReturn.
     *
     * @return messageId
     */
    public long getMessageId() {
        return messageId;
    }


    /**
     * Sets the messageId value for this ApiBulkReturn.
     *
     * @param messageId
     */
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }


    /**
     * Gets the requestId value for this ApiBulkReturn.
     *
     * @return requestId
     */
    public String getRequestId() {
        return requestId;
    }


    /**
     * Sets the requestId value for this ApiBulkReturn.
     *
     * @param requestId
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    private Object __equalsCalc = null;
    @Override
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ApiBulkReturn)) {
            return false;
        }
        ApiBulkReturn other = (ApiBulkReturn) obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            this.error_code == other.getError_code() &&
            ((this.error_detail==null && other.getError_detail()==null) ||
             (this.error_detail!=null &&
              this.error_detail.equals(other.getError_detail()))) &&
            this.messageId == other.getMessageId() &&
            ((this.requestId==null && other.getRequestId()==null) ||
             (this.requestId!=null &&
              this.requestId.equals(other.getRequestId())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    @Override
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += new Long(getError_code()).hashCode();
        if (getError_detail() != null) {
            _hashCode += getError_detail().hashCode();
        }
        _hashCode += new Long(getMessageId()).hashCode();
        if (getRequestId() != null) {
            _hashCode += getRequestId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ApiBulkReturn.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.tempuri.org/", "ApiBulkReturn"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("error_code");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "error_code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("error_detail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "error_detail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messageId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "messageId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "requestId"));
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
