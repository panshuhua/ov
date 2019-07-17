package com.ivay.ivay_common.utils;

public class SysVariable {
    // 有效标志位: 有效
    public static final String ENABLE_FLAG_YES = "Y";
    // 有效标志位: 无效
    public static final String ENABLE_FLAG_NO = "N";

    // 账号状态: 正常
    public static final String ACCOUNT_STATUS_NORMAL = "0";
    // 账号状态: 冻结
    public static final String ACCOUNT_STATUS_FORBIDDEN = "1";
    // 账号状态: 注销
    public static final String ACCOUNT_STATUS_DEL = "2";

    // 交易密碼設置狀態: 已設置
    public static final String TRANSFER_PWD_HAS = "1";
    // 交易密碼設置狀態: 沒設置
    public static final String TRANSFER_PWD_NONE = "0";

    // 照片类型: 正面照
    public static final String PHOTO_TYPE_FRONT = "1";
    // 照片类型: 侧面照
    public static final String PHOTO_TYPE_BACK = "2";
    // 照片类型: 手持照
    public static final String PHOTO_TYPE_HAND = "3";

    // 绑定银行卡状态
    public static final String CARD_STATUS_DOING = "0";
    public static final String CARD_STATUS_SUCCESS = "1";
    public static final String CARD_STATUS_FAILURE = "2";

    // 用户状态: 初始状态
    public static final String USER_STATUS_INIT = "0";
    // 用户状态: 注册成功
    public static final String USER_STATUS_REGISTRY = "1";
    // 用户状态: 审核中
    public static final String USER_STATUS_CHECKING = "2";
    // 用户状态: 授信成功
    public static final String USER_STATUS_AUTH_SUCCESS = "3";
    // 用户状态: 绑卡成功
    public static final String USER_STATUS_BANKCARD_SUCCESS = "4";
    // 用户状态: 借款成功
    public static final String USER_STATUS_LOAN_SUCCESS = "5";
    // 用户状态: 多次借款
    public static final String USER_STATUS_LOAN_REPEATEDLY = "6";
    // 用户状态: 授信失败
    public static final String USER_STATUS_AUTH_FAIL = "7";
    // 用户状态: 重新审核
    public static final String USER_STATUS_AUTH_RETRY = "8";

    // 借款状态: 借款失败
    public static final int LOAN_STATUS_FAIL = 0;
    // 借款状态: 借款成功
    public static final int LOAN_STATUS_SUCCESS = 1;
    // 借款状态: 等待打款
    public static final int LOAN_STATUS_WAITING = 2;
    // 最小可借额度
    public static final long LOAN_MIN_AMOUNT = 500000;


    // 还款状态: 待还款
    public static final int REPAYMENT_STATUS_NONE = 0;
    // 还款状态: 还款中
    public static final int REPAYMENT_STATUS_DOING = 1;
    // 还款状态: 还款成功
    public static final int REPAYMENT_STATUS_SUCCESS = 2;
    // 还款状态: 还款失败
    public static final int REPAYMENT_STATUS_FAIL = 3;

    // 还款方式: 主动还款
    public static final int REPAYMENT_MODE_NORMAL = 0;

    // 配置模板: 滞纳金配置
    public static final String TEMPLATE_OVERDUE_RATE = "overdueFeeRate";
    // 配置模板: 借款利率配置
    public static final String TEMPLATE_LOAN_RATE = "loanRate";
    // 配置模板: 提額配置
    public static final String TEMPLATE_CREDIT_LIMIT = "creditLimit";
    // 配置模板：发送短信验证码配置
    public static final String TEMPLATE_SEND_PHONEMSG = "sendPhoneMsg";
    // 配置模板：学历配置
    public static final String TEMPLATE_EDUCATION = "education";
    // 配置模板：婚姻状态配置
    public static final String TEMPLATE_MARITAL = "marital";
    // 配置模板：与联系人的关系配置
    public static final String TEMPLATE_RELATION = "relation";
    // 配置模板：性别配置
    public static final String TEMPLATE_SEX = "sex";
    // 配置模板：审核状态配置
    public static final String TEMPLATE_AUDIT_STATUS = "auditStatus";
    // 配置模板: 提額配置
    public static final String TEMPLATE_CREDIT_RISK = "riskManage";
    // 配置模板: 用户风控管理配置
    public static final String TEMPLATE_USER_MANAGE = "userManage";
    // 配置模板：用户状态
    public static final String TEMPLATE_USER_STATUS = "userStatus";
    // 配置模板：借款状态
    public static final String TEMPLATE_LOAN_STATUS = "loanStatus";
    // 配置模板：还款状态
    public static final String TEMPLATE_REPAYMENT_STATUS = "repaymentStatus";
    // 配置模板：baokim还款回调接口是否开启签名校验
    public static final String BAOKIM_NOTICE_SIGNATURE = "baokimNoticeSignature";

    // 接口对接
    public static final String API_ACC_TYPE_0 = "0";
    public static final String API_ACC_TYPE_1 = "1";
    public static final String API_OPERATION_VALIDATE = "9001";
    public static final String API_OPERATION_TRANSFER = "9002";
    public static final String API_OPERATION_TRANSFER_INFO = "9003";
    public static final String API_OPERATION_BALANCE = "9004";
    public static final String API_OPERATION_REGISTER_VIRTUALACCOUNT = "9001";
    public static final String API_OPERATION_UPDATE_VIRTUALACCOUNT = "9002";
    public static final String API_OPERATION_VIRTUALACCOUNT_INFOSEARCH = "9003";
    public static final String API_OPERATION_COLLTRAN_STATUSSEARCH = "9004";
    public static final String API_ACC_TYPE_FIRST = "1";
    public static final String API_ACC_TYPE_SECOND = "2";
    public static final String API_PCODE_REGISTER_VIRTUALACCOUNT = "9000";
    public static final String API_PCODE_UPDATE_VIRTUALACCOUNT = "9001";
    public static final String API_PCODE_MAPPING_CANCELATION = "9002";
    public static final String API_PCODE_VIEW_MAPPIGSTATUS = "9099";
    public static final String API_CONDITION_VIRTUALACCOUNT_ONE = "01";
    public static final String API_CONDITION_VIRTUALACCOUNT_TWO = "02";
    public static final String API_CONDITION_VIRTUALACCOUNT_THREE = "03";
    public static final String API_CONDITION_VIRTUALACCOUNT_FOUR = "04";
    public static final String API_BANK_CODE_EBAY = "WOORIBANK";

    // redis 借款锁前缀
    public static final String REDIS_BORROW_MONEY_PREFIX = "borrrowMoney:";

    // redis 逾期计算锁前缀
    public static final String REDIS_OVERDUE_FEE_PREFIX = "overdueFee:";

    // redis 自动创建虚拟账号锁前缀
    public static final String REDIS_AUTO_CREATEVA_PREFIX = "autoCreateVirtualAccount:";

    // redis 自动发送到期短信/通知提醒锁前缀
    public static final String REDIS_EXPIRE_NOTICE_PREFIX = "expireNotice:";

    // redis 自动发送逾期短信/通知提醒锁前缀
    public static final String REDIS_OVERDUE_NOTICE_PREFIX = "overdueNotice:";

    // redis 保存风控数据
    public static final String REDIS_RISK_APPNUM_PREFIX = "appNum:";
    public static final String REDIS_RISK_CONTACTS_PREFIX = "contacts:";
    public static final String REDIS_RISK_GPS_PREFIX = "gps:";
    public static final String REDIS_RISK_OTHER_PREFIX = "other:";

    // 角色配置
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_OVAY_ADMIN = "ovayAdmin";
    public static final String ROLE_OVAY_AUDIT = "ovayAudit";
    public static final String ROLE_COLLECTION_ADMIN = "collectionAdmin";
    public static final String ROLE_COLLECTION_PERSON = "collectionPerson";

    // 审核拒绝类型
    // 0人工审核
    public static final String AUDIT_REFUSE_TYPE_MANUAL = "0";
    // 1 自动审核
    public static final String AUDIT_REFUSE_TYPE_AUTO = "1";
    // 9 黑名单用户的审核代码
    public static final String AUDIT_BLACK_USER_CODE = "9";
    // 0 拒绝
    public static final int AUDIT_REFUSE = 0;
    // 1 通过
    public static final int AUDIT_PASS = 1;

    // 风控类型
    // 授信
    public static final int RISK_TYPE_AUDIT = 0;
    // 借款
    public static final int RISK_TYPE_LOAN = 1;

    // app事件是否上传
    public static final String APP_EVENT_AUDIT = "0";
    public static final String APP_EVENT_LOAN = "1";
    public static final String APP_EVENT_SUCCESS = "1";
    public static final String APP_EVENT_FAIL = "0";

    // 上传风控数据的类型
    public static final String CONTACT = "contacts"; // 通讯录联系人
    public static final String APPNUM = "appNum"; // 社交类app的个数
    public static final String GPS = "location"; // gps信息
    public static final String OTHER = "otherRiskInfo"; // mac地址/手机品牌/使用什么手机流量

    // 日志记录的参数名
    public static final String PARAM_MOBILE = "mobile";
    public static final String PARAM_OPTTYPE = "optType";
    public static final String PARAM_LOGININFO = "loginInfo";
    public static final String PARAM_NOTICE = "notice";
    public static final String PARAM_REQUEST = "request";
    public static final String RETURN_SUCESS_CODE = "200";
    public static final String METHOD_SENDREGISTERCODE = "sendRegisterCode";

    // 短信验证码登录/注册类型
    public static final String RETURN_TYPE_LOGIN = "login";
    public static final String RETURN_TYPE_REGISTER = "register";

    // 发送通知/短信提醒时的语言
    public static final String LANG_VI = "vi";

    // firebase跳转页面加密短名称
    public static final String PAGE_PERSONALDATA = "WVhSUQ==";
    public static final String PAGE_BINDBANKCARD = "WkhKQw==";
    public static final String PAGE_BANKCARD = "WkhKVUk9";
    public static final String PAGE_FORGETPASSWORD = "WkhKWT0=";
    public static final String PAGE_RESETTRANPWD = "WkhkUw==";
    public static final String PAGE_MINE = "Wlc1PT0=";
    public static final String PAGE_HOME = "WlcxPT0="; // 首页
    public static final String PAGE_GUIDE = "WldSYz0=";
    public static final String PAGE_REGISTER = "Y21WVkk9";
    public static final String PAGE_BILL = "Ykd4PT0="; // 账单页
    public static final String PAGE_BILLDETAIL = "YkdsPQ=="; // 账单详情页 要带参数gid
    public static final String PAGE_PERSONALDATADETAIL = "YkdsbVZR:";
    public static final String PAGE_LOANCONFIRM = "YlhKPQ==";
    public static final String PAGE_LOAN = "Ym1GPT0=";
    public static final String PAGE_LOGIN = "Ym1sdz0=";
    public static final String PAGE_LOANAPPLICATION = "Ym05OU0="; // 借款申请页
    public static final String PAGE_SETTINGS = "YzJkVk09";
    public static final String PAGE_LOANSUCCESS = "YzNOPQ==";
    public static final String PAGE_CREDITSTATUS = "YzNWRA==";
    public static final String PAGE_WEBVIEW = "ZDJWdz09";
    public static final String PAGE_REPAYMENT = "ZEc1R1ZT";
    public static final String PAGE_EMERGENCYCONTACT = "ZEdOUT09";
    public static final String PAGE_CREDIT = "ZEdsSkQ=";
    public static final String PAGE_TABNAV = "ZG1GRlU=";
    public static final String PAGE_HISTORY = "ZVhKQT09";

    // 借还款合作伙伴名称
    public static final String PARTNER_BAOKIM = "baokim";
    public static final String PARTNER_EPAY = "epay";
    // 借还款使用的第3方接口供应商
    public static final String LOAN_REPAYMENT_PARTNER = "LoanRepaymentPartner";

    // 对账类型
    public static final String CHECK_TYPE_LOAN = "1"; // 1-借款
    public static final String CHECK_TYPE_REPAY = "2"; // 2-还款

    // 发送通知短信链接固定前缀
    public static final String PHONEMSG_PREFIX_LINK = "https://ovay.vn/";

    // 发送短信的第3方接口短信平台类型
    public static final String SMS_ZERO = "0";
    public static final String SMS_ONE = "1";
    public static final String SMS_TWO = "2";
    public static final String SMS_THREE = "3";

    // 使用pas国内运营上发送短信的类型
    public static final String SMS_TYPE_CODE = "code"; // 短信验证码
    public static final String SMS_TYPE_NOTICE = "notice"; // 通知

    // 发送短信返回成功的状态
    public static final String SMS_SEND_SUCCESS = "0";

    // 发送短信验证码的操作类型optType
    public static final int OPTTYPE_RESETLOGINPWD = 0; // 0-app忘记密码（需要重置登录密码）
    public static final int OPTTYPE_REGISTER = 1; // 1-app注册设置密码（独立注册）
    public static final int OPTTYPE_NEWEQUIPMENT = 2; // 2-app老用户新设备登录
    public static final int OPTTYPE_SMSCODELOGIN = 3; // 3-使用短信验证码登录
    public static final int OPTTYPE_RESETTRANPWD = 4; // 4-修改交易密码

    // 短信验证码登录操作类型
    public static final String SMS_VERIFYCODE_LOGIN = "1"; // 短信验证码登录(如果不存在该手机号码，实际上就是注册)
    // needCheckVerify:是否老用户新设备登录(1.是，0否）
    public static final String LOGIN_NEWEQUIPMENT = "1";
    public static final String LOGIN_OLDEQUIPMENT = "0";

    // 发送通知消息的类型
    public static final String NOTICE_FIREBASE_MSG = "1"; // 1-firebase消息推送
    public static final String NOTICE_PHONE_MSG = "2"; // 2-手机短信

    // 2分钟内不能重复发送验证码的rediskey后缀
    public static final String SEND_AUTHCODE_SUFFIX = "_send";

}
