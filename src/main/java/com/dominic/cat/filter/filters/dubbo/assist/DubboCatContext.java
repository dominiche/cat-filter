package com.dominic.cat.filter.filters.dubbo.assist;

import com.alibaba.dubbo.rpc.RpcContext;
import com.dianping.cat.Cat;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by dominic on 2018/12/27 15:54.
 */
public class DubboCatContext implements Cat.Context {
    private DubboCatContext() {
    }

    private Map<String, String> properties = new HashMap<>();

    @Override
    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }


    public static Cat.Context getContext(final ThreadLocal<Cat.Context> contextThreadLocal) {
        Cat.Context context = contextThreadLocal.get();
        if (context == null) {
            context = new DubboCatContext();
            Map<String, String> attachments = RpcContext.getContext().getAttachments();
            if (attachments != null && attachments.size() > 0) {
                for (Map.Entry<String, String> entry : attachments.entrySet()) {
                    if (Cat.Context.CHILD.equals(entry.getKey()) || Cat.Context.ROOT.equals(entry.getKey()) || Cat.Context.PARENT.equals(entry.getKey())) {
                        context.addProperty(entry.getKey(), entry.getValue());
                    }
                }
            }
            contextThreadLocal.set(context);
        }
        return context;
    }

    public static void setRpcAttachment(Cat.Context context) {
        RpcContext.getContext().setAttachment(Cat.Context.ROOT, context.getProperty(Cat.Context.ROOT));
        RpcContext.getContext().setAttachment(Cat.Context.CHILD, context.getProperty(Cat.Context.CHILD));
        RpcContext.getContext().setAttachment(Cat.Context.PARENT, context.getProperty(Cat.Context.PARENT));
    }
}
