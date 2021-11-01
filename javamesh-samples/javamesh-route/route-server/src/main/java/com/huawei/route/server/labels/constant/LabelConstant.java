/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.route.server.labels.constant;

/**
 * 表签相关的常量
 *
 * @author zhanghu
 * @since 2021-05-25
 */
public class LabelConstant {
    /**
     * redis键分割符
     */
    public static final String SEPARATOR = ":";

    /**
     * 通配符
     */
    public static final String WILDCARD = "*";

    /**
     * zookeeper node分割符
     */
    public static final String ZK_SEPARATOR = "/";

    /**
     * 标签业务主键前缀
     */
    public static final String GENERAL_PAAS = "general-paas";

    /**
     * redis中标签组的key
     */
    public static final String XPAAS_LABEL_GROUPS = GENERAL_PAAS + SEPARATOR + "labelGroup";

    /**
     * redis中标签组存储的标签名的hashKey
     */
    public static final String LABELS = "labels";

    /**
     * 标签存放值的key
     */
    public static final String VALUE_OF_LABEL = "value";

    /**
     * 标签生效、失效的标识
     */
    public static final String VALID_MARKING = "on";

    /**
     * 值为true的字符串
     */
    public static final String TRUE_STRING = "true";

    /**
     * 错误码 -1
     */
    public static final int ERROR_CODE = -1;

    /**
     * 配置的描述key
     */
    public static final String CONFIGURATION_DESCRIPTION = "description";

    /**
     * 服务名集合的标识
     */
    public static final String SERVICE_NAMES_MARKING = "serviceNames";

    /**
     * 服务名标识
     */
    public static final String SERVICE_NAME_MARKING = "serviceName";

    /**
     * 实例名标识
     */
    public static final String INSTANCE_NAME_MARKING = "instanceName";

    /**
     * 标签组名标识
     */
    public static final String LABEL_GROUP_NAME_MARKING = "labelGroupName";

    /**
     * 标签名标识
     */
    public static final String LABEL_NAME_MARKING = "labelName";

    /**
     * 实例名的集合标识
     */
    public static final String INSTANCE_NAMES = "instanceNames";

    /**
     * 标签组的key标识
     */
    public static final String LABEL_GROUPS = "labelGroups";

    /**
     * 标签生效后存储的redis的key
     */
    public static final String TEMP_STRING = "temp";

    /**
     * 标签生效失效的redis key前缀
     */
    public static final String VALID = "valid";

    public static final int ERROR_CODE_ONE = 1;

    public static final int SUCCESS_CODE = 0;

    /**
     * 无权限操作
     */
    public static final int PERMISSION_ERROR_CODE = 2;
    public static final String PERMISSION_ERROR_MSG = "无权限操作";

    /**
     * netty ip
     */
    public static final String NETTY_IP = "netty.ip";

    /**
     * netty port
     */
    public static final String NETTY_PORT = "netty.port";

    public static final String UPDATE_TIMESTAMP = "updateTimeStamp";

    /**
     * 英文、数字、下划线、中划线的正则表达式
     */
    public static final String PATTERN_WITHOUT_CHINESE = "^[A-Za-z0-9_\\-]+$";

    /**
     * 中文、英文、数字、下划线、中划线的正则表达式
     */
    public static final String PATTERN_WITH_CHINESE = "^[\\u4E00-\\u9FA5A-Za-z0-9_\\-]+$";

    /**
     * 无损演练：agent-drill的标签key
     */
    public static final String AGENT_DRILL_LABEL_KEY = "agent-drill";

    private LabelConstant() {
    }
}
