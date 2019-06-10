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

