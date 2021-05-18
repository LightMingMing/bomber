package com.bomber.engine.monitor;

import com.bomber.engine.model.BomberContext;
import com.bomber.engine.model.Result;

/**
 * 通知
 *
 * @author MingMing Zhao
 */
public interface TestingNotifier extends ListenerRegistry {

    boolean fireStarted(BomberContext ctx);

    void firePaused(BomberContext ctx);

    void fireFailed(BomberContext ctx, Throwable e);

    void fireMetric(BomberContext ctx, int doneRequests);

    void fireCompleted(BomberContext ctx);

    void fireEachExecute(BomberContext ctx, Result result);

}