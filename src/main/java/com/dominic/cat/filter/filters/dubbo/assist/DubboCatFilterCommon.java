package com.dominic.cat.filter.filters.dubbo.assist;

import com.alibaba.dubbo.remoting.TimeoutException;
import com.alibaba.dubbo.rpc.RpcException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.internal.AbstractMessage;

/**
 * Create by dominic on 2018/12/27 17:42.
 */
public class DubboCatFilterCommon {
    public static void logTimeoutEvent(String transactionName, Transaction transaction, Throwable e) {
        //记录超时event
        if (RpcException.class == e.getClass()) {
            Throwable caseBy = e.getCause();
            if (caseBy != null && caseBy.getClass() == TimeoutException.class) {
                Event event = Cat.newEvent(DubboConstant.CONSUMER_TIMEOUT, transactionName);
                event.setStatus(e);
                ((AbstractMessage) event).setCompleted(true);
                transaction.addChild(event);
            }
        }
    }
}
