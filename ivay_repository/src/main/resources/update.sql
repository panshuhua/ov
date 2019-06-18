-- 审计员角色分配 2019-6-10

-- DDL：删除无用表
DROP TABLE IF EXISTS `user`;

-- DDL：新建一张审计员表
CREATE TABLE IF NOT EXISTS `x_audit_user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `sys_user_id` varchar(50) NOT NULL COMMENT '审计员id',
  `user_gid` varchar(32) NOT NULL COMMENT '待审计用户gid',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `enable_flag` char(1) DEFAULT 'Y' COMMENT '有效标志位',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='审计员可审计用户';

-- DDL：为用户表和角色表增加字段
ALTER TABLE `sys_role` ADD `enable_flag` char(1) DEFAULT 'Y' COMMENT '有效标志位';
ALTER TABLE `enable_flag` ADD `enable_flag` char(1) DEFAULT 'Y' COMMENT '有效标志位';
ALTER TABLE `sys_user` ADD `enable_flag` char(1) DEFAULT 'Y' COMMENT '有效标志位';

-- DDL：修改索引先删除
ALTER TABLE sys_user DROP INDEX username;
-- 再以修改后的内容创建同名索引
CREATE INDEX username ON sys_user (username);

-- DML：增加角色
INSERT INTO `sys_role` VALUES ('2', 'ovayAdmin', 'OVAY系统超级管理员权限', NOW(), NOW(), 'Y');
INSERT INTO `sys_role` VALUES ('3', 'ovayAudit', '审核系统使用权限', NOW(), NOW(), 'Y');

-- 新增：社交类app个数表
CREATE TABLE `x_user_app_num` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_gid` varchar(32) NOT NULL COMMENT '用户gid',
  `app_num` int(10) DEFAULT NULL COMMENT '社交类app的个数',
  `enable_flag` char(1) DEFAULT 'Y',
  `update_date` varchar(50) DEFAULT NULL COMMENT '上传日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;

-- x_config表风控规则数据修改：添加社交类app规则
UPDATE `x_config` SET `id`='11', `type`='riskManage', `lang`=NULL, `content`='{\"enable\": true,\"audit\":{\"age\":\"18~50\",\"gps\":\"0~1\",\"macCode\":\"0~1\",\"contact\":\"10~\",\"majorRelation\":\"0~2\",\"appNum\":\"0~\"},\"loan\":{\"age\":\"18~50\",\"gps\":\"0~1\",\"macCode\":\"0~1\",\"contact\":\"6~\",\"majorRelation\":\"0~2\",\"overdueDay\":\"0~30\",\"overdueDay2\":\"0~5\",\"appNum\":\"0~\"}}', `description`='风控配置：audit授权配置，loan借款配置，enable是否启动风控' WHERE (`id`='11');


-- 增加审核说明
ALTER TABLE x_user_info ADD `refuse_reason` varchar(512) CHARACTER SET utf8 DEFAULT NULL COMMENT '审核拒绝原因';
ALTER TABLE x_user_info ADD `refuse_type` char(1) DEFAULT NULL COMMENT '审核类型：0人工审核 1 自动审核';
ALTER TABLE x_user_info ADD `audit_time` datetime DEFAULT NULL COMMENT '审核时间';


-- 更新 审核状态 配置

-- x_baokim_transfers_info表添加字段-ebay使用字段

ALTER TABLE `x_baokim_transfers_info` ADD `contract_number` varchar(50) DEFAULT NULL COMMENT 'ebay: contract number of customer';
ALTER TABLE `x_baokim_transfers_info` ADD `extend` varchar(500) DEFAULT NULL COMMENT 'ebay: extend msg';
ALTER TABLE `x_baokim_transfers_info` ADD `sub_error_code` int(6) DEFAULT NULL COMMENT 'ebay';
ALTER TABLE `x_baokim_transfers_info` ADD `sub_error_message` DEFAULT NULL varchar(500) COMMENT 'ebay';
ALTER TABLE `x_baokim_transfers_info` ADD `reason` varchar(500) DEFAULT NULL COMMENT 'ebay';


-- 新建表：用户风控信息表
CREATE TABLE `x_user_risk` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_gid` varchar(32) NOT NULL COMMENT '用户gid',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `enable_flag` char(1) DEFAULT 'Y',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `mac_address` varchar(36) DEFAULT NULL COMMENT 'mac地址',
  `phone_brand` varchar(32) DEFAULT NULL COMMENT '手机品牌',
  `traffic_way` char(1) DEFAULT NULL COMMENT '【手机流量类型：0:wifi;1:2G流量;2:3G流量;3:4G流量】',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;


-- sys_logs可删除后再导入
CREATE TABLE `sys_logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_gid` varchar(32) DEFAULT NULL,
  `module` varchar(50) DEFAULT NULL COMMENT '模块名',
  `flag` tinyint(4) NOT NULL DEFAULT '1' COMMENT '成功失败',
  `remark` text COMMENT '备注',
  `createTime` datetime NOT NULL,
  `phone` varchar(11) DEFAULT NULL,
  `request_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userId` (`user_gid`),
  KEY `createTime` (`createTime`)
) ENGINE=InnoDB AUTO_INCREMENT=379 DEFAULT CHARSET=utf8mb4;


-- 新增-版本更新表
CREATE TABLE `x_version_update` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_gid` varchar(32) DEFAULT NULL COMMENT '用户gid',
  `version_number` varchar(36) DEFAULT NULL COMMENT '版本号',
  `version_content` varchar(500) DEFAULT NULL COMMENT '更新内容',
  `need_update` char(1) DEFAULT NULL COMMENT '是否必要更新(1:是，0:否)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 日志表新增字段
ALTER TABLE `sys_logs` ADD `code` varchar(10) DEFAULT NULL COMMENT '返回状态码';




