-- 新增 ebay还款虚拟账号表
CREATE TABLE `x_ebay_virtual_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pcode` varchar(4) DEFAULT NULL,
  `merchant_code` varchar(50) DEFAULT NULL,
  `map_id` varchar(32) DEFAULT NULL COMMENT '对应于还款表的order_id：订单id',
  `amount` decimal(15,0) DEFAULT NULL,
  `start_date` varchar(14) DEFAULT NULL,
  `end_date` varchar(14) DEFAULT NULL,
  `condition` varchar(2) DEFAULT NULL,
  `customer_name` varchar(50) DEFAULT NULL,
  `request_id` varchar(50) DEFAULT NULL,
  `response_code` varchar(2) DEFAULT NULL,
  `message` varchar(1024) DEFAULT NULL,
  `account_no` varchar(50) DEFAULT NULL,
  `account_name` varchar(100) DEFAULT NULL,
  `bank_code` varchar(50) DEFAULT NULL,
  `bank_name` varchar(1024) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `enable_flag` char(1) DEFAULT 'Y',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4;

-- 新增 ebay还款回调接口信息表
CREATE TABLE `x_ebay_collection_notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `request_id` varchar(50) DEFAULT NULL,
  `request_time` varchar(20) DEFAULT NULL,
  `bank_tran_time` varchar(20) DEFAULT NULL,
  `reference_id` varchar(50) DEFAULT NULL,
  `map_id` varchar(50) DEFAULT NULL,
  `amount` decimal(15,0) DEFAULT NULL,
  `merchant_code` varchar(50) DEFAULT NULL,
  `fee` decimal(15,0) DEFAULT NULL,
  `va_name` varchar(100) DEFAULT NULL,
  `va_acc` varchar(50) DEFAULT NULL,
  `bank_code` varchar(50) DEFAULT NULL,
  `bank_name` varchar(1024) DEFAULT NULL,
  `enable_flag` char(1) DEFAULT 'Y',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- 配置表添加baokim还款回调接口签名校验开关
INSERT INTO `x_config` (`id`, `type`, `lang`, `content`, `description`) VALUES ('33', 'baokimNoticeSignature', '', '{\"enable\":true}', '宝金回调接口是否开启公钥校验');

-- baokim后台导出的还款记录表
CREATE TABLE `baokim_collection_data` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `transaction_id_baokim` varchar(20) DEFAULT NULL,
  `time_recorded` varchar(20) DEFAULT NULL,
  `account_no` varchar(50) DEFAULT NULL,
  `amount` varchar(20) DEFAULT NULL,
  `account_name` varchar(50) DEFAULT NULL,
  `order_id` varchar(20) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `enable_flag` char(1) DEFAULT 'Y',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=451 DEFAULT CHARSET=utf8mb4;

-- baokim后台导出的借款记录表
CREATE TABLE `baokim_transfer_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trans_time` varchar(20) DEFAULT NULL,
  `baokim_trans_id` varchar(20) DEFAULT NULL,
  `amount` varchar(20) DEFAULT NULL,
  `transfer_real_amount` varchar(20) DEFAULT NULL,
  `card_no` varchar(50) DEFAULT NULL,
  `customer_name` varchar(100) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `enable_flag` char(1) DEFAULT 'Y',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3196 DEFAULT CHARSET=utf8mb4;

-- 对账比对结果表
CREATE TABLE `account_check_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `baokim_count` int(50) DEFAULT NULL,
  `baokim_amount` bigint(100) DEFAULT NULL,
  `ovay_count` int(50) DEFAULT NULL,
  `ovay_amount` bigint(100) DEFAULT NULL,
  `type` char(1) DEFAULT NULL COMMENT '比对类型：1-借款，2-还款',
  `partner` varchar(20) DEFAULT NULL COMMENT '合作伙伴：baokim/ebay',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `enable_flag` char(1) DEFAULT 'Y',
  `stime` varchar(50) DEFAULT NULL COMMENT '统计开始时间',
  `etime` varchar(50) DEFAULT NULL COMMENT '统计结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

-- todo 2019-7-22 菜单角色用户重构
ALTER TABLE sys_permission MODIFY `type` tinyint(1) NOT NULL COMMENT '1菜单 2权限';
alter table sys_role drop column enable_flag;
alter table sys_role_user drop column enable_flag;
alter table sys_user drop column enable_flag;
alter table x_audit_user drop column enable_flag;

-- VTP还款交易信息
CREATE TABLE `vtp_transaction_process_input` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `trans_id` varchar(60) DEFAULT NULL,
  `ref_id` varchar(60) DEFAULT NULL,
  `customer_code` varchar(60) DEFAULT NULL,
  `amount` bigint(50) DEFAULT NULL,
  `fee` bigint(50) DEFAULT NULL,
  `content` varchar(500) DEFAULT NULL,
  `agent_user` varchar(50) DEFAULT NULL,
  `agent_name` varchar(50) DEFAULT NULL,
  `trans_date` varchar(60) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `enable_flag` char(1) DEFAULT 'Y',
  `trans_msg` varchar(100) DEFAULT NULL,
  `trans_status` tinyint(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;





