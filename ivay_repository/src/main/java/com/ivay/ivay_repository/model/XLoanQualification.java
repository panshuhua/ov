package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("风控系统的校验实体")
public class XLoanQualification {
    @ApiModelProperty("年龄")
    public int age;
    @ApiModelProperty("身份证使用次数")
    public int oneIdentityCardNum;
    public String identityCard;
    @ApiModelProperty("联系人个数")
    public int contactsNum;
    @ApiModelProperty("同一设备注册的个数")
    public int onePhoneNum;
    public String macCode;
    @ApiModelProperty("同一GPS的个数")
    public int oneGpsNum;
    public BigDecimal longitude;
    public BigDecimal latitude;
    @ApiModelProperty("同一亲密联系人的个数")
    public int oneMajorPhoneNum;
    public String majorPhone;

    @ApiModelProperty("最大逾期天数")
    public int maxOverdueDay;
    @ApiModelProperty("逾期中")
    public boolean overdueFlag;
    @ApiModelProperty("最近一笔还款的逾期天数")
    public int lastOverdueDay;

    @ApiModelProperty("社交app数量")
    public int appCount;
    public List<String> appList;
    @ApiModelProperty("姓名与绑卡姓名的编辑距离")
    public int nameDistance;
    public String name;
    public String cardName;
    @ApiModelProperty("黑名单")
    public boolean blackFlag;
}
