package com.example.demo;

import java.util.concurrent.TimeoutException;

import com.codahale.metrics.MetricRegistry;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Component
public class Routes extends RouteBuilder {

  @Autowired
  ResilienceLoadBalance lb;


  @Autowired
  CircuitBreakerRegistry registry;



      @Override
      public void configure() throws Exception {

    getContext().addRoutePolicyFactory(new MicrometerRoutePolicyFactory());       
    rest("/camel").get().
        route().log("Testing")
        .onException(CallNotPermittedException.class)
        .process((ex) -> log.info("Handling CircuitBreaker Open"))
        .handled(false)
        .end()
        .loadBalance(lb) 
        .process((ex) -> { 
          log.error("Nao vamos printar");
          throw new TimeoutException("timeout");
        } )
        .end();

        rest("/open")
        .get()
        .route()
        .process((ex) -> {
          log.info("Opening Circuit Breaker");

          registry.circuitBreaker("basic").transitionToForcedOpenState();

        })
        .end();

        rest("/close")
        .get()
        .route()
        .process((ex) -> {

          log.info("Closing Circuit Breaker");
          registry.circuitBreaker("basic").transitionToClosedState();

        })
        .end();



      }
}