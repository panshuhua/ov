/**
 * ApiBulkBlockReturn.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class ApiBulkBlockReturn  implements java.io.Serializable {
    private int error_code;

    private String error_detail;

    private String progCode;

    private String blackList;

    private int doublicNumbers;

    private int wrongNumbers;

    private org.tempuri.MsisdnInfo[] detail;

    public ApiBulkBlockReturn() {
    }

    public ApiBulkBlockReturn(
           int error_code,
           String error_detail,
           String progCode,
           String blackList,
           int doublicNumbers,
           int wrongNumbers,
           org.tempuri.MsisdnInfo[] detail) {
           this.error_code = error_code;
           this.error_detail = error_detail;
           this.progCode = progCode;
           this.blackList = blackList;
           this.doublicNumbers = doublicNumbers;
           this.wrongNumbers = wrongNumbers;
           this.detail = detail;
    }


    /**
     * Gets the error_code value for this ApiBulkBlockReturn.
     *
     * @return error_code
     */
    public int getError_code() {
        return error_code;
    }


    /**
     * Sets the error_code value for this ApiBulkBlockReturn.
     *
     * @param error_code
     */
    public void setError_code(int error_code) {
        this.error_code = error_code;
    }


    /**
     * Gets the error_detail value for this ApiBulkBlockReturn.
     *
     * @return error_detail
     */
    public String getError_detail() {
        return error_detail;
    }


    /**
     * Sets the error_detail value for this ApiBulkBlockReturn.
     *
     * @param error_detail
     */
    public void setError_detail(String error_detail) {
        this.error_detail = error_detail;
    }


    /**
     * Gets the progCode value for this ApiBulkBlockReturn.
     *
     * @return progCode
     */
    public String getProgCode() {
        return progCode;
    }


    /**
     * Sets the progCode value for this ApiBulkBlockReturn.
     *
     * @param progCode
     */
    public void setProgCode(String progCode) {
        this.progCode = progCode;
    }


    /**
     * Gets the blackList value for this ApiBulkBlockReturn.
     *
     * @return blackList
     */
    public String getBlackList() {
        return blackList;
    }


    /**
     * Sets the blackList value for this ApiBulkBlockReturn.
     *
     * @param blackList
     */
    public void setBlackList(String blackList) {
        this.blackList = blackList;
    }


    /**
     * Gets the doublicNumbers value for this ApiBulkBlockReturn.
     *
     * @return doublicNumbers
     */
    public int getDoublicNumbers() {
        return doublicNumbers;
    }


    /**
     * Sets the doublicNumbers value for this ApiBulkBlockReturn.
     *
     * @param doublicNumbers
     */
    public void setDoublicNumbers(int doublicNumbers) {
        this.doublicNumbers = doublicNumbers;
    }


    /**
     * Gets the wrongNumbers value for this ApiBulkBlockReturn.
     *
     * @return wrongNumbers
     */
    public int getWrongNumbers() {
        return wrongNumbers;
    }


    /**
     * Sets the wrongNumbers value for this ApiBulkBlockReturn.
     *
     * @param wrongNumbers
     */
    public void setWrongNumbers(int wrongNumbers) {
        this.wrongNumbers = wrongNumbers;
    }


    /**
     * Gets the detail value for this ApiBulkBlockReturn.
     *
     * @return detail
     */
    public org.tempuri.MsisdnInfo[] getDetail() {
        return detail;
    }


    /**
     * Sets the detail value for this ApiBulkBlockReturn.
     *
     * @param detail
     */
    public void setDetail(org.tempuri.MsisdnInfo[] detail) {
        this.detail = detail;
    }

    private Object __equalsCalc = null;
    @Override
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ApiBulkBlockReturn)) {
            return false;
        }
        ApiBulkBlockReturn other = (ApiBulkBlockReturn) obj;
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
            ((this.progCode==null && other.getProgCode()==null) ||
             (this.progCode!=null &&
              this.progCode.equals(other.getProgCode()))) &&
            ((this.blackList==null && other.getBlackList()==null) ||
             (this.blackList!=null &&
              this.blackList.equals(other.getBlackList()))) &&
            this.doublicNumbers == other.getDoublicNumbers() &&
            this.wrongNumbers == other.getWrongNumbers() &&
            ((this.detail==null && other.getDetail()==null) ||
             (this.detail!=null &&
              java.util.Arrays.equals(this.detail, other.getDetail())));
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
        _hashCode += getError_code();
        if (getError_detail() != null) {
            _hashCode += getError_detail().hashCode();
        }
        if (getProgCode() != null) {
            _hashCode += getProgCode().hashCode();
        }
        if (getBlackList() != null) {
            _hashCode += getBlackList().hashCode();
        }
        _hashCode += getDoublicNumbers();
        _hashCode += getWrongNumbers();
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
        new org.apache.axis.description.TypeDesc(ApiBulkBlockReturn.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.tempuri.org/", "ApiBulkBlockReturn"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("error_code");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "error_code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
        elemField.setFieldName("progCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "progCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("blackList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "blackList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("doublicNumbers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "doublicNumbers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wrongNumbers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "wrongNumbers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("detail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "detail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://org.tempuri.org/", "msisdnInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://org.tempuri.org/", "msisdnInfo"));
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
