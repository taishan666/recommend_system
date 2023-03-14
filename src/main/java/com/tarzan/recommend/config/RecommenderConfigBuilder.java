package com.tarzan.recommend.config;

import org.lenskit.LenskitConfiguration;


/**
 * @author Lenovo
 */
public class RecommenderConfigBuilder {

    /**
     * 获取LenskitConfiguration
     *
     * @param type 推荐类型
     * @return ITokenGranter
     */
    public static LenskitConfiguration getConfig(String type) {
        LenskitConfiguration config = RecommenderConfiguration.createConfig(type);
        if (config == null) {
            return null;
        } else {
            return config;
        }
    }
}
