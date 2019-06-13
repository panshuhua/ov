-- 审计员角色分配 2019-6-10

-- DDL：删除无用表
DROP TABLE IF EXISTS `user`;

-- DDL：新建一张审计员表
CREATE TABLE IF NOT EXISTS `x_audit_user`
(
  `id`          int(11) unsigned NOT NULL AUTO_INCREMENT,
  `sys_user_id` varchar(50)      NOT NULL COMMENT '审计员id',
  `user_gid`    varchar(32)      NOT NULL COMMENT '待审计用户gid',
  `create_time` datetime         NOT NULL,
  `update_time` datetime         NOT NULL,
  `enable_flag` char(1) DEFAULT 'Y' COMMENT '有效标志位',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  DEFAULT CHARSET = utf8mb4 COMMENT ='审计员可审计用户';

-- DDL：为用户表和角色表增加字段
ALTER TABLE `sys_role`
  ADD `enable_flag` char(1) DEFAULT 'Y' COMMENT '有效标志位';
ALTER TABLE `sys_role_user`
  ADD `enable_flag` char(1) DEFAULT 'Y' COMMENT '有效标志位';
ALTER TABLE `sys_user`
  ADD `enable_flag` char(1) DEFAULT 'Y' COMMENT '有效标志位';

-- DDL：修改索引先删除
ALTER TABLE sys_user
  DROP INDEX username;
-- 再以修改后的内容创建同名索引
CREATE INDEX username ON sys_user (username);

-- 增加审核说明
ALTER TABLE x_user_info
  ADD `refuse_reason` varchar(512) CHARACTER SET utf8 DEFAULT NULL COMMENT '审核拒绝原因';
ALTER TABLE x_user_info
  ADD `refuse_type` char(1) DEFAULT NULL COMMENT '审核类型：0人工审核 1 自动审核';
ALTER TABLE x_user_info
  ADD `audit_time` datetime DEFAULT NULL COMMENT '审核时间';

-- DML：增加角色
-- INSERT INTO `sys_role` VALUES ('3', 'ovayAdmin', 'OVAY系统超级管理员权限', NOW(), NOW(), 'Y');
-- INSERT INTO `sys_role` VALUES ('4', 'ovayAudit', '审核系统使用权限', NOW(), NOW(), 'Y');

-- 新增：社交类app个数表
CREATE TABLE `x_user_app_num`
(
  `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_gid`    varchar(32)      NOT NULL COMMENT '用户gid',
  `app_num`     int(10)     DEFAULT NULL COMMENT '社交类app的个数',
  `enable_flag` char(1)     DEFAULT 'Y',
  `update_date` varchar(50) DEFAULT NULL COMMENT '上传日期',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8mb4;

-- x_config表风控规则数据修改：添加社交类app规则
UPDATE `x_config`
SET `id`='11',
    `type`='riskManage',
    `lang`= NULL,
    `content`='{\"enable\": true,\"audit\":{\"age\":\"18~50\",\"gps\":\"0~1\",\"macCode\":\"0~1\",\"contact\":\"10~\",\"majorRelation\":\"0~2\",\"appNum\":\"0~\"},\"loan\":{\"age\":\"18~50\",\"gps\":\"0~1\",\"macCode\":\"0~1\",\"contact\":\"6~\",\"majorRelation\":\"0~2\",\"overdueDay\":\"0~30\",\"overdueDay2\":\"0~5\",\"appNum\":\"0~\"}}',
    `description`='风控配置：audit授权配置，loan借款配置，enable是否启动风控'
WHERE (`id` = '11');


-- 更新 审核状态 配置
