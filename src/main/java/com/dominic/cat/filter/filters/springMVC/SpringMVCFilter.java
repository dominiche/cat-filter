package com.dominic.cat.filter.filters.springMVC;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dominic.cat.filter.property.CatFilterProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Create by dominic on 2018/12/25 14:08.
 */
@Component
public class SpringMVCFilter implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CatInterceptor());
    }

    class CatInterceptor implements HandlerInterceptor {

        private ThreadLocal<Transaction> transactionThreadLocal = new ThreadLocal<>();

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String uri = request.getRequestURI();

            if (CatFilterProperties.enableSpringMVCFilter) {
                String type = "URL"; //or "SpringMVC"?...
                Transaction t = Cat.newTransaction(type, uri);
                transactionThreadLocal.set(t);

                logRequestClientInfo(request, type);
                logRequestPayload(request, type);
                return true;
            }

            return true;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            Transaction transaction = transactionThreadLocal.get();
            if (transaction != null) {
                transactionThreadLocal.remove();
                if (ex != null) {
                    transaction.setStatus(ex);
                    Cat.logError(ex); //不加的话没有异常详情
                } else {
                    transaction.setStatus(Transaction.SUCCESS);
                }
                transaction.complete();
            }
        }

        private void logRequestClientInfo(HttpServletRequest req, String type) {
            StringBuilder sb = new StringBuilder(1024);
            String ip;
            String ipForwarded = req.getHeader("x-forwarded-for");

            if (ipForwarded == null) {
                ip = req.getRemoteAddr();
            } else {
                ip = ipForwarded;
            }

            sb.append("IPS=").append(ip);
            sb.append("&VirtualIP=").append(req.getRemoteAddr());
            sb.append("&Server=").append(req.getServerName());
            sb.append("&Referer=").append(req.getHeader("referer"));
            sb.append("&Agent=").append(req.getHeader("user-agent"));

            Cat.logEvent(type, type + ".Server", Message.SUCCESS, sb.toString());
        }

        private void logRequestPayload(HttpServletRequest request, String type) {
            String method =request.getProtocol() + " " + request.getMethod() + " " + request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                method = method + "?" + queryString;
            }
            Cat.logEvent(type, type + ".Method", Message.SUCCESS, method);
        }
    }
}
