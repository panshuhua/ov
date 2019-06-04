/**
 * APIAdsSendMT.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class APIAdsSendMT  implements java.io.Serializable {
    private String telco;

    private long error_code;

    private String error_detail;

    private String prog_code;

    private int total_sub;

    private int total_sms;

    public APIAdsSendMT() {
    }

    public APIAdsSendMT(
           String telco,
           long error_code,
           String error_detail,
           String prog_code,
           int total_sub,
           int total_sms) {
           this.telco = telco;
           this.error_code = error_code;
           this.error_detail = error_detail;
           this.prog_code = prog_code;
           this.total_sub = total_sub;
           this.total_sms = total_sms;
    }


    /**
     * Gets the telco value for this APIAdsSendMT.
     *
     * @return telco
     */
    public String getTelco() {
        return telco;
    }


    /**
     * Sets the telco value for this APIAdsSendMT.
     *
     * @param telco
     */
    public void setTelco(String telco) {
        this.telco = telco;
    }


    /**
     * Gets the error_code value for this APIAdsSendMT.
     *
     * @return error_code
     */
    public long getError_code() {
        return error_code;
    }


    /**
     * Sets the error_code value for this APIAdsSendMT.
     *
     * @param error_code
     */
    public void setError_code(long error_code) {
        this.error_code = error_code;
    }


    /**
     * Gets the error_detail value for this APIAdsSendMT.
     *
     * @return error_detail
     */
    public String getError_detail() {
        return error_detail;
    }


    /**
     * Sets the error_detail value for this APIAdsSendMT.
     *
     * @param error_detail
     */
    public void setError_detail(String error_detail) {
        this.error_detail = error_detail;
    }


    /**
     * Gets the prog_code value for this APIAdsSendMT.
     *
     * @return prog_code
     */
    public String getProg_code() {
        return prog_code;
    }


    /**
     * Sets the prog_code value for this APIAdsSendMT.
     *
     * @param prog_code
     */
    public void setProg_code(String prog_code) {
        this.prog_code = prog_code;
    }


    /**
     * Gets the total_sub value for this APIAdsSendMT.
     *
     * @return total_sub
     */
    public int getTotal_sub() {
        return total_sub;
    }


    /**
     * Sets the total_sub value for this APIAdsSendMT.
     *
     * @param total_sub
     */
    public void setTotal_sub(int total_sub) {
        this.total_sub = total_sub;
    }


    /**
     * Gets the total_sms value for this APIAdsSendMT.
     *
     * @return total_sms
     */
    public int getTotal_sms() {
        return total_sms;
    }


    /**
     * Sets the total_sms value for this APIAdsSendMT.
     *
     * @param total_sms
     */
    public void setTotal_sms(int total_sms) {
        this.total_sms = total_sms;
    }

    private Object __equalsCalc = null;
    @Override
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof APIAdsSendMT)) {
            return false;
        }
        APIAdsSendMT other = (APIAdsSendMT) obj;
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
            ((this.telco==null && other.getTelco()==null) ||
             (this.telco!=null &&
              this.telco.equals(other.getTelco()))) &&
            this.error_code == other.getError_code() &&
            ((this.error_detail==null && other.getError_detail()==null) ||
             (this.error_detail!=null &&
              this.error_detail.equals(other.getError_detail()))) &&
            ((this.prog_code==null && other.getProg_code()==null) ||
             (this.prog_code!=null &&
              this.prog_code.equals(other.getProg_code()))) &&
            this.total_sub == other.getTotal_sub() &&
            this.total_sms == other.getTotal_sms();
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
        if (getTelco() != null) {
            _hashCode += getTelco().hashCode();
        }
        _hashCode += new Long(getError_code()).hashCode();
        if (getError_detail() != null) {
            _hashCode += getError_detail().hashCode();
        }
        if (getProg_code() != null) {
            _hashCode += getProg_code().hashCode();
        }
        _hashCode += getTotal_sub();
        _hashCode += getTotal_sms();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(APIAdsSendMT.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.tempuri.org/", "APIAdsSendMT"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telco");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "telco"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("prog_code");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "prog_code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("total_sub");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "total_sub"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("total_sms");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "total_sms"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
