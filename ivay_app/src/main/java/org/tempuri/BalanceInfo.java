/**
 * BalanceInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class BalanceInfo  implements java.io.Serializable {
    private int error_code;

    private String error_detail;

    private String accountName;

    private String status;

    private double balance;

    public BalanceInfo() {
    }

    public BalanceInfo(
           int error_code,
           String error_detail,
           String accountName,
           String status,
           double balance) {
           this.error_code = error_code;
           this.error_detail = error_detail;
           this.accountName = accountName;
           this.status = status;
           this.balance = balance;
    }


    /**
     * Gets the error_code value for this BalanceInfo.
     *
     * @return error_code
     */
    public int getError_code() {
        return error_code;
    }


    /**
     * Sets the error_code value for this BalanceInfo.
     *
     * @param error_code
     */
    public void setError_code(int error_code) {
        this.error_code = error_code;
    }


    /**
     * Gets the error_detail value for this BalanceInfo.
     *
     * @return error_detail
     */
    public String getError_detail() {
        return error_detail;
    }


    /**
     * Sets the error_detail value for this BalanceInfo.
     *
     * @param error_detail
     */
    public void setError_detail(String error_detail) {
        this.error_detail = error_detail;
    }


    /**
     * Gets the accountName value for this BalanceInfo.
     *
     * @return accountName
     */
    public String getAccountName() {
        return accountName;
    }


    /**
     * Sets the accountName value for this BalanceInfo.
     *
     * @param accountName
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }


    /**
     * Gets the status value for this BalanceInfo.
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this BalanceInfo.
     *
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * Gets the balance value for this BalanceInfo.
     *
     * @return balance
     */
    public double getBalance() {
        return balance;
    }


    /**
     * Sets the balance value for this BalanceInfo.
     *
     * @param balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    private Object __equalsCalc = null;
    @Override
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof BalanceInfo)) {
            return false;
        }
        BalanceInfo other = (BalanceInfo) obj;
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
            ((this.accountName==null && other.getAccountName()==null) ||
             (this.accountName!=null &&
              this.accountName.equals(other.getAccountName()))) &&
            ((this.status==null && other.getStatus()==null) ||
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            this.balance == other.getBalance();
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
        if (getAccountName() != null) {
            _hashCode += getAccountName().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        _hashCode += new Double(getBalance()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BalanceInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.tempuri.org/", "BalanceInfo"));
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
        elemField.setFieldName("accountName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "accountName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://org.tempuri.org/", "balance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
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
