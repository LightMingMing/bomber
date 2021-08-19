CREATE DATABASE IF NOT EXISTS bomber2;

DROP TABLE IF EXISTS workspace;
CREATE TABLE workspace
(
    id          INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(32)  NOT NULL COMMENT '名称',
    author      VARCHAR(16)  NOT NULL COMMENT '作者',
    description VARCHAR(255) NOT NULL DEFAULT '' COMMENT '描述',
    createDate  DATETIME     NOT NULL DEFAULT NOW() COMMENT '创建时间',
    modifyDate  DATETIME     NOT NULL DEFAULT NOW() ON UPDATE NOW() COMMENT '修改时间'
) COMMENT '工作空间';

DROP TABLE IF EXISTS http_group;
CREATE TABLE http_group
(
    id          INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    workspaceId INT         NOT NULL COMMENT '工作空间ID',
    name        VARCHAR(32) NOT NULL COMMENT '名称'
) COMMENT '组';

DROP TABLE IF EXISTS function_configure;
CREATE TABLE function_configure
(
    id             INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    groupId        INT         NOT NULL COMMENT '组ID',
    orderNumber    INT         NOT NULL COMMENT '序号',
    name           VARCHAR(32) NOT NULL COMMENT '名称',
    functionName   VARCHAR(32) NOT NULL COMMENT '函数名',
    argumentValues JSON        NOT NULL COMMENT '参数值',
    enabled        BIT         NOT NULL COMMENT '是否启用'
) COMMENT '函数配置';

DROP TABLE IF EXISTS http_sample;
CREATE TABLE http_sample
(
    id          INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    groupId     INT         NOT NULL COMMENT '组ID',
    orderNumber INT         NOT NULL COMMENT '序号',
    name        VARCHAR(32) NOT NULL COMMENT '名称',
    method      VARCHAR(16) NOT NULL COMMENT '方法',
    url         VARCHAR(64) NOT NULL COMMENT 'URL',
    headers     JSON        NOT NULL COMMENT '请求头',
    body        TEXT        NOT NULL COMMENT '请求体',
    assertions  JSON        NOT NULL COMMENT '断言',
    enabled     BIT         NOT NULL COMMENT '是否启用',
    createDate  DATETIME    NOT NULL DEFAULT NOW() COMMENT '创建时间',
    modifyDate  DATETIME    NOT NULL DEFAULT NOW() ON UPDATE NOW() COMMENT '修改时间'
) COMMENT 'HTTP请求脚本';

DROP TABLE IF EXISTS testing_record;
CREATE TABLE testing_record
(
    id                BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
    httpSampleId      INT           NOT NULL COMMENT 'HTTP请求脚本ID',
    name              VARCHAR(32)   NOT NULL COMMENT '名称',
    threadGroups      VARCHAR(255)  NOT NULL COMMENT '线程组',
    threadGroupCursor INT           NOT NULL COMMENT '线程组游标',
    activeThreads     INT           NOT NULL COMMENT '活动线程数',
    scope             VARCHAR(16)   NOT NULL COMMENT '变量作用域',
    beginUserIndex    INT           NOT NULL COMMENT '初始用户索引',
    iterations        INT           NOT NULL COMMENT '迭代数',
    currentIteration  INT           NOT NULL COMMENT '当前迭代数',
    requestsPerThread INT           NOT NULL COMMENT '每线程请求数',
    status            VARCHAR(16)   NOT NULL COMMENT '状态',
    createTime        DATETIME      NOT NULL DEFAULT NOW() COMMENT '创建时间',
    startTime         DATETIME      NULL COMMENT '开始时间',
    endTime           DATETIME      NULL COMMENT '结束时间',
    remark            VARCHAR(1024) NOT NULL DEFAULT '' COMMENT '备注'
) COMMENT '测试记录';

DROP TABLE IF EXISTS summary_report;
CREATE TABLE summary_report
(
    id               BIGINT   NOT NULL PRIMARY KEY AUTO_INCREMENT,
    testingRecordId  BIGINT   NOT NULL COMMENT '测试记录ID',
    numberOfThreads  INT      NOT NULL COMMENT '线程数',
    numberOfRequests INT      NOT NULL COMMENT '请求数',
    tps              DOUBLE   NOT NULL COMMENT '吞吐量',
    avg              DOUBLE   NOT NULL COMMENT '平均响应时间',
    min              DOUBLE   NOT NULL COMMENT '最小响应时间',
    max              DOUBLE   NOT NULL COMMENT '最大响应时间',
    top25            DOUBLE   NOT NULL COMMENT 'TOP25% 响应时间',
    top50            DOUBLE   NOT NULL COMMENT 'TOP50% 响应时间',
    top75            DOUBLE   NOT NULL COMMENT 'TOP75% 响应时间',
    top90            DOUBLE   NOT NULL COMMENT 'TOP90% 响应时间',
    top95            DOUBLE   NOT NULL COMMENT 'TOP95% 响应时间',
    top99            DOUBLE   NOT NULL COMMENT 'TOP99% 响应时间',
    stdDev           DOUBLE   NOT NULL COMMENT '标准方差',
    req1xx           INT      NOT NULL COMMENT '1xx状态码请求数',
    req2xx           INT      NOT NULL COMMENT '2xx状态码请求数',
    req3xx           INT      NOT NULL COMMENT '3xx状态码请求数',
    req4xx           INT      NOT NULL COMMENT '4xx状态码请求数',
    req5xx           INT      NOT NULL COMMENT '5xx状态码请求数',
    other            INT      NOT NULL COMMENT '其它请求数',
    errorCount       INT      NOT NULL COMMENT '错误请求数',
    startTime        DATETIME NOT NULL COMMENT '开始时间',
    endTime          DATETIME NOT NULL COMMENT '结束时间'
) COMMENT '概要报告';