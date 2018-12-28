package com.dominic.cat.filter.filters.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.internal.AbstractMessage;
import com.dominic.cat.filter.filters.dubbo.assist.DubboCatContext;
import com.dominic.cat.filter.filters.dubbo.assist.DubboCatFilterCommon;
import com.dominic.cat.filter.filters.dubbo.assist.DubboConstant;
import com.dominic.cat.filter.property.CatFilterProperties;

/**
 * Create by dominic on 2018/12/27 17:07:09.
 */
@Activate(group = {Constants.PROVIDER}, order = -9000)
public class ProviderCatFilter implements Filter {
    private static final ThreadLocal<Cat.Context> CAT_CONTEXT = new ThreadLocal<>();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (!CatFilterProperties.enableDubboFilter) {
            return invoker.invoke(invocation);
        }

        String transactionName = invoker.getInterface().getSimpleName() + "." + invocation.getMethodName();
        Transaction transaction = Cat.newTransaction(DubboConstant.PROVIDER_TYPE, transactionName);
        Result result = null;
        try {
            Cat.Context context = DubboCatContext.getContext(CAT_CONTEXT);
            logProviderEvent(transaction);
            Cat.logRemoteCallServer(context);

            DubboCatContext.setRpcAttachment(context);
            result = invoker.invoke(invocation);
            if (result.hasException()) {
                DubboCatFilterCommon.logTimeoutEvent(transactionName, transaction, result.getException());
                transaction.setStatus(result.getException().getClass().getSimpleName());
            } else {
                transaction.setStatus(Message.SUCCESS);
            }
            return result;
        } catch (Exception e) {
            Cat.logError(e);
            DubboCatFilterCommon.logTimeoutEvent(transactionName, transaction, e);
            transaction.setStatus(e.getClass().getSimpleName());
            if (result == null) {
                throw e;
            } else {
                return result;
            }
        } finally {
            transaction.complete();
            CAT_CONTEXT.remove();
        }
    }

    private void logProviderEvent(Transaction transaction) {
        String serviceAppName = Cat.getManager().getDomain();
        String invokerAppName = RpcContext.getContext().getAttachment(DubboConstant.DUBBO_PARAM_INVOKER_APP_NAME);
        if (invokerAppName == null || "".equals(invokerAppName)) {
            invokerAppName = RpcContext.getContext().getRemoteHost() + ":" + RpcContext.getContext().getRemotePort();
        }
        String serverApp = serviceAppName + ", invoke from: " + invokerAppName;
        Event appNameEvent = Cat.newEvent(DubboConstant.PROVIDER_APPLICATION, serverApp);
        appNameEvent.setStatus(Event.SUCCESS);
        ((AbstractMessage) appNameEvent).setCompleted(true);
        transaction.addChild(appNameEvent);

        String server = RpcContext.getContext().getRemoteHost() + ":" + RpcContext.getContext().getRemotePort();
        Event serverEvent = Cat.newEvent(DubboConstant.PROVIDER_SERVER, server);
        serverEvent.setStatus(Event.SUCCESS);
        ((AbstractMessage) serverEvent).setCompleted(true);
        transaction.addChild(serverEvent);
    }
}
