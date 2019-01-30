package com.coder.enhance;

import org.apache.ibatis.session.Configuration;

/**
 * @author jeffy
 * @date 2019/1/25
 **/
@FunctionalInterface
public interface ConfigurationCustomizer {
    void customize(Configuration configuration);
}
