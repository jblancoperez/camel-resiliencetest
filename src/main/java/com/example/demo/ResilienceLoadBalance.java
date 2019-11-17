package com.example.demo;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.camel.AsyncCallback;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.processor.loadbalancer.LoadBalancerSupport;
import org.apache.camel.util.AsyncProcessorHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;

@Component
public class ResilienceLoadBalance extends LoadBalancerSupport {

    private final CircuitBreaker cBreaker;

    public ResilienceLoadBalance(CircuitBreakerRegistry registry) {
        cBreaker = registry.circuitBreaker("basic");
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        AsyncProcessorHelper.process(this, exchange);
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        CheckedRunnable protectedCall = 
        CircuitBreaker.decorateCheckedRunnable(cBreaker, () -> getProcessors().get(0).process(exchange));
        Try.run(protectedCall).andFinally(() ->
        {
            log.warn("Finally");
            callback.done(true);
    }
        ).get();
    
        return true;
    }

}