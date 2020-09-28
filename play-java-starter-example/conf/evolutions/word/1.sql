# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

-- init script create procs
-- Inital script to create stored procedures etc for mysql platform
DROP PROCEDURE IF EXISTS usp_ebean_drop_foreign_keys;

delimiter $$
--
-- PROCEDURE: usp_ebean_drop_foreign_keys TABLE, COLUMN
-- deletes all constraints and foreign keys referring to TABLE.COLUMN
--
CREATE PROCEDURE usp_ebean_drop_foreign_keys(IN p_table_name VARCHAR(255), IN p_column_name VARCHAR(255))
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE c_fk_name CHAR(255);
  DECLARE curs CURSOR FOR SELECT CONSTRAINT_NAME from information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE() and TABLE_NAME = p_table_name and COLUMN_NAME = p_column_name
      AND REFERENCED_TABLE_NAME IS NOT NULL;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  OPEN curs;

  read_loop: LOOP
    FETCH curs INTO c_fk_name;
    IF done THEN
      LEAVE read_loop;
    END IF;
    SET @sql = CONCAT('ALTER TABLE ', p_table_name, ' DROP FOREIGN KEY ', c_fk_name);
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
  END LOOP;

  CLOSE curs;
END
$$

DROP PROCEDURE IF EXISTS usp_ebean_drop_column;

delimiter $$
--
-- PROCEDURE: usp_ebean_drop_column TABLE, COLUMN
-- deletes the column and ensures that all indices and constraints are dropped first
--
CREATE PROCEDURE usp_ebean_drop_column(IN p_table_name VARCHAR(255), IN p_column_name VARCHAR(255))
BEGIN
  CALL usp_ebean_drop_foreign_keys(p_table_name, p_column_name);
  SET @sql = CONCAT('ALTER TABLE ', p_table_name, ' DROP COLUMN ', p_column_name);
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
END
$$
create table config (
  node                          varchar(32) comment '配置节点' not null,
  status                        tinyint unsigned default 1 comment '数据是否有效[0 无效,1 有效]' not null,
  create_time                   datetime(6) default current_timestamp(6) comment '创建时间' not null,
  update_time                   datetime(6) default current_timestamp(6) on update current_timestamp(6) comment '修改时间' not null,
  node_name                     varchar(32) default '' comment '节点名称' not null,
  node_order                    tinyint unsigned default 1 comment '节点次序' not null,
  node_order_seq                varchar(255) default '' comment '节点次序序列' not null,
  node_seq                      varchar(255) default '' comment '节点序列' not null,
  node_level                    tinyint unsigned default 1 comment '节点级别' not null,
  parent                        varchar(32) comment '配置节点',
  constraint pk_config primary key (node)
);

create table word_en (
  id                            bigint auto_increment not null,
  status                        tinyint unsigned default 1 comment '数据是否有效[0 无效,1 有效]' not null,
  create_time                   datetime(6) default current_timestamp(6) comment '创建时间' not null,
  update_time                   datetime(6) default current_timestamp(6) on update current_timestamp(6) comment '修改时间' not null,
  word                          varchar(128) not null default '' comment '单词',
  word_note                     varchar(128) not null default '' comment '单词解释',
  sound_uk                      varchar(32) not null default '' comment '英音',
  sound_us                      varchar(32) not null default '' comment '美音',
  error_num                     integer not null default 0 comment '错误次数',
  remember_mark                 tinyint unsigned not null default 0 comment '是否标记为识记[0 不识记, 1 识记]',
  source                        varchar(32) comment '配置节点',
  constraint uq_word_en_word unique (word),
  constraint pk_word_en primary key (id)
);

create table word_en_article (
  id                            bigint auto_increment not null,
  status                        tinyint unsigned default 1 comment '数据是否有效[0 无效,1 有效]' not null,
  create_time                   datetime(6) default current_timestamp(6) comment '创建时间' not null,
  update_time                   datetime(6) default current_timestamp(6) on update current_timestamp(6) comment '修改时间' not null,
  title                         varchar(64) default '' comment '标题',
  title_note                    varchar(64) default '' comment '标题翻译',
  content                       varchar(2048) default '' comment '内容',
  content_note                  varchar(1024) default '' comment '翻译',
  source                        varchar(32) comment '配置节点',
  remember_mark                 tinyint unsigned default 0 comment '是否标记为识记[0 不识记, 1 识记]' not null,
  constraint pk_word_en_article primary key (id)
);

create table word_en_extend (
  word                          varchar(32) comment '单词' not null,
  type                          varchar(16) comment '词类' not null,
  no                            int comment '序号' not null,
  status                        tinyint unsigned default 1 comment '数据是否有效[0 无效,1 有效]' not null,
  create_time                   datetime(6) default current_timestamp(6) comment '创建时间' not null,
  update_time                   datetime(6) default current_timestamp(6) on update current_timestamp(6) comment '修改时间' not null,
  word_note                     varchar(64) not null default '' comment '单词解释',
  remember_mark                 tinyint unsigned not null default 0 comment '是否标记为识记[0 不识记, 1 识记]',
  constraint pk_word_en_extend primary key (word,type,no)
);

create table word_en_sentence (
  word                          varchar(32) comment '英文单词' not null,
  type                          varchar(16) comment '词类' not null,
  no                            int comment '词类' not null,
  sentence                      varchar(256) comment '例句' not null,
  status                        tinyint unsigned default 1 comment '数据是否有效[0 无效,1 有效]' not null,
  create_time                   datetime(6) default current_timestamp(6) comment '创建时间' not null,
  update_time                   datetime(6) default current_timestamp(6) on update current_timestamp(6) comment '修改时间' not null,
  sentence_note                 varchar(1024) not null default '' comment '例句解释',
  remember_mark                 tinyint unsigned not null default 1 comment '是否标记为识记[0 不识记, 1 识记]' not null,
  constraint pk_word_en_sentence primary key (word,type,no,sentence)
);

create index ix_config_parent on config (parent);
alter table config add constraint fk_config_parent foreign key (parent) references config (node) on delete restrict on update restrict;

create index ix_word_en_source on word_en (source);
alter table word_en add constraint fk_word_en_source foreign key (source) references config (node) on delete restrict on update restrict;

create index ix_word_en_article_source on word_en_article (source);
alter table word_en_article add constraint fk_word_en_article_source foreign key (source) references config (node) on delete restrict on update restrict;

create index ix_word_en_sentence_wordenextend on word_en_sentence (word,type,no);
alter table word_en_sentence add constraint fk_word_en_sentence_wordenextend foreign key (word,type,no) references word_en_extend (word,type,no) on delete restrict on update restrict;


# --- !Downs

alter table config drop foreign key fk_config_parent;
drop index ix_config_parent on config;

alter table word_en drop foreign key fk_word_en_source;
drop index ix_word_en_source on word_en;

alter table word_en_article drop foreign key fk_word_en_article_source;
drop index ix_word_en_article_source on word_en_article;

alter table word_en_sentence drop foreign key fk_word_en_sentence_wordenextend;
drop index ix_word_en_sentence_wordenextend on word_en_sentence;

drop table if exists config;

drop table if exists word_en;

drop table if exists word_en_article;

drop table if exists word_en_extend;

drop table if exists word_en_sentence;

