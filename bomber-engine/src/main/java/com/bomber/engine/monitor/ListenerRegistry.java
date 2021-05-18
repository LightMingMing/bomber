package com.bomber.engine.monitor;

/**
 * 监听器注册
 *
 * @author MingMing Zhao
 */
public interface ListenerRegistry {

    void register(TestingListener... listeners);
}
