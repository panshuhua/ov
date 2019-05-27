/**
 * ApiAdsReturn.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class ApiAdsReturn  implements java.io.Serializable {
    private long error_code;

    private String error_detail;

    private String prog_code;

    private String campaign_name;

    private org.tempuri.APIAdsSendMT[] detail;

    public ApiAdsReturn() {
    }

    public ApiAdsReturn(
           long error_code,
           String error_detail,
           String prog_code,
           String campaign_name,
           org.tempuri.APIAdsSendMT[] detail) {
           this.error_code = error_code;
           this.error_detail = error_detail;
           this.prog_code = prog_code;
           this.campaign_name = campaign_name;
           this.detail = detail;
    }


    /**
     * Gets the error_code value for this ApiAdsReturn.
     *
     * @return error_code
     */
    public long getError_code() {
        return error_code;
    }


    /**
     * Sets the error_code value for this ApiAdsReturn.
     *
     * @param error_code
     */
    public void setError_code(long error_code) {
        this.error_code = error_code;
    }


    /**
     * Gets the error_detail value for this ApiAdsReturn.
     *
     * @return error_detail
     */
    public String getError_detail() {
        return error_detail;
    }


    /**
     * Sets the error_detail value for this ApiAdsReturn.
     *
     * @param error_detail
     */
    public void setError_detail(String error_detail) {
        this.error_detail = error_detail;
    }


    /**
     * Gets the prog_code value for this ApiAdsReturn.
     *
     * @return prog_code
     */
    public String getProg_code() {
        return prog_code;
    }


    /**
     * Sets the prog_code value for this ApiAdsReturn.
     *
     * @param prog_code
     */
    public void setProg_code(String prog_code) {
        this.prog_code = prog_code;
    }


    /**
     * Gets the campaign_name value for this ApiAdsReturn.
     *
     * @return campaign_name
     */
    public String getCampaign_name() {
        return campaign_name;
    }


    /**
     * Sets the campaign_name value for this ApiAdsReturn.
     *
     * @param campaign_name
     */
    public void setCampaign_name(String campaign_name) {
        this.campaign_name = campaign_name;
    }


    /**
     * Gets the detail value for this ApiAdsReturn.
     *
     * @return detail
     */
    public org.tempuri.APIAdsSendMT[] getDetail() {
        return detail;
    }


    /**
     * Sets the detail value for this ApiAdsReturn.
     *
     * @param detail
     */
    public void setDetail(org.tempuri.APIAdsSendMT[] detail) {
        this.detail = detail;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ApiAdsReturn)) return false;
        ApiAdsReturn other = (ApiAdsReturn) obj;
        if (obj == null) return false;
        if (this == obj) return true;
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
            ((this.prog_code==null && other.getProg_code()==null) ||
             (this.prog_code!=null &&
              this.prog_code.equals(other.getProg_code()))) &&
            ((this.campaign_name==null && other.getCampaign_name()==null) ||
             (this.campaign_name!=null &&
              this.campaign_name.equals(other.getCampaign_name()))) &&
            ((this.detail==null && other.getDetail()==null) ||
             (this.detail!=null &&
              java.util.Arrays.equals(this.detail, other.getDetail())));
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
        _hashCode += new Long(getError_code()).hashCode();
        if (getError_detail() != null) {
            _hashCode += getError_detail().hashCode();
        }
        if (getProg_code() != null) {
            _hashCode += getProg_code().hashCode();
        }
        if (getCampaign_name() != null) {
            _hashCode += getCampaign_name().hashCode();
        }
        if (getDetail() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDetail());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getDetail(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ApiAdsReturn.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.tempuri.org/", "ApiAdsReturn"));
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
        elemField.setFieldName("prog_code");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "prog_code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("campaign_name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "campaign_name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("detail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "detail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://org.tempuri.org/", "APIAdsSendMT"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://org.tempuri.org/", "APIAdsSendMT"));
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
