/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.flowcontrol.adapte.cse.datasource;

import com.alibaba.csp.sentinel.datasource.AbstractDataSource;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.Rule;
import com.huawei.apm.core.lubanops.bootstrap.log.LogFactory;
import com.huawei.flowcontrol.adapte.cse.rule.AbstractRule;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * CSE-kie规则数据
 *
 * @author zhouss
 * @since 2021-11-24
 */
public class CseKieDataSource<S extends AbstractRule, R extends Rule> extends AbstractDataSource<List<S>, List<R>> {
    private static final Logger LOGGER = LogFactory.getLogger();

    private List<S> configData;

    public CseKieDataSource(Converter<List<S>, List<R>> parser) {
        super(parser);
    }

    /**
     * 更新配置
     *
     * @param config 配置
     */
    public void updateConfig(List<S> config) {
        configData = config;
        try {
            loadConfig();
        } catch (Exception ex) {
            LOGGER.warning(String.format(Locale.ENGLISH, "Loaded config failed, %s", ex.getMessage()));
        }
    }

    @Override
    public List<S> readSource() throws Exception {
        return configData;
    }

    @Override
    public void close() throws Exception {

    }
}
