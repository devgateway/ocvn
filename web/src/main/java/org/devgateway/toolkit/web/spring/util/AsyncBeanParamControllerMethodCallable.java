package org.devgateway.toolkit.web.spring.util;

import org.devgateway.toolkit.web.spring.AsyncControllerLookupService;

/**
 * @see AsyncControllerLookupService
 * @author mihai
 *
 * @param <RET>
 * @param <BEANPARAM>
 */
public abstract class AsyncBeanParamControllerMethodCallable<RET, BEANPARAM> {
    public abstract RET invokeControllerMethod(BEANPARAM filter);
}