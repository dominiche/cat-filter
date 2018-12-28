package com.dominic.cat.filter.filters.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
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
 * Create by dominic on 2018/12/27 15:50.
 */
@Activate(group = {Constants.CONSUMER}, order = -9000)
public class ConsumerCatFilter implements Filter {
    private static final ThreadLocal<Cat.Context> CAT_CONTEXT = new ThreadLocal<>();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (!CatFilterProperties.enableDubboFilter) {
            return invoker.invoke(invocation);
        }

        URL url = invoker.getUrl();
        String transactionName = invoker.getInterface().getSimpleName() + "." + invocation.getMethodName();
        Transaction transaction = Cat.newTransaction(DubboConstant.CONSUMER_TYPE, transactionName);
        Result result = null;
        try {
            Cat.Context context = DubboCatContext.getContext(CAT_CONTEXT);
            logConsumerEvent(url, transaction);
            Cat.logRemoteCallClient(context);

            DubboCatContext.setRpcAttachment(context);
            RpcContext.getContext().setAttachment(DubboConstant.DUBBO_PARAM_INVOKER_APP_NAME, Cat.getManager().getDomain());
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

    private void logConsumerEvent(URL url, Transaction transaction) {
        Event appNameEvent = Cat.newEvent(DubboConstant.CONSUMER_APPLICATION, getProviderAppName(url));
        appNameEvent.setStatus(Event.SUCCESS);
        ((AbstractMessage) appNameEvent).setCompleted(true);
        transaction.addChild(appNameEvent);

        String server = url.getProtocol() + "://" + url.getAddress();
        Event serverEvent = Cat.newEvent(DubboConstant.CONSUMER_SERVER, server);
        serverEvent.setStatus(Event.SUCCESS);
        ((AbstractMessage) serverEvent).setCompleted(true);
        transaction.addChild(serverEvent);
    }

    private String getProviderAppName(URL url) {
        String appName = url.getParameter(Constants.APPLICATION_KEY);
        if (appName == null || "".equals(appName)) {
            String interfaceName = url.getParameter(Constants.INTERFACE_KEY);
            appName = interfaceName.substring(0, interfaceName.lastIndexOf('.'));
        }
        return appName;
    }
}
