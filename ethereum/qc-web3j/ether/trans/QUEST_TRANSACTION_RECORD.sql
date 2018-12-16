--create Table QUEST_TRANSACTION_RECORD
CREATE TABLE QUEST_TRANSACTION_RECORD(
    ID INT unsigned NOT NULL AUTO_INCREMENT COMMENT '流水ID，自增',
    USER_ID VARCHAR(32) NOT NULL COMMENT '领币用户ID',
    VALUE DECIMAL(12,8) DEFAULT 0 NOT null COMMENT '领取五彩石数量',
    STATUS CHAR(1) NOT null COMMENT '领取交易状态：0：领取失败、1：成功领取',
    TRANSACTION_HASH VARCHAR(64) COMMENT '交易Hash',
    CREATE_TIME DATETIME NOT null COMMENT '领取时间',
    UPDATE_TIME DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY(ID)
);
ALTER TABLE QUEST_TRANSACTION_RECORD
ADD INDEX QUEST_TRANSACTION_RECORD_INDEX_CREATETIME(CREATE_TIME);