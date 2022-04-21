package com.distributedtracing.statusservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.distributedtracing.statusservice.model.Constants.CONTEXT_MAP;
import static com.distributedtracing.statusservice.model.Constants.TRACER;

@Component
@Slf4j
public class TraceWebFilter implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String tracer = exchange.getRequest().getHeaders().getOrDefault(TRACER, List.of("")).get(0);
    if(tracer.isEmpty()) {
      tracer = UUID.randomUUID().toString();
      exchange = exchange.mutate().request(
                    exchange.getRequest().mutate()
                      .header(TRACER, tracer)
                      .build()
                ).build();
    }
    String finalTracer = tracer;
    return chain.filter(exchange)
        .contextWrite(
            ctx -> ctx.put(CONTEXT_MAP, Map.of(TRACER, finalTracer))
        );
  }
}
