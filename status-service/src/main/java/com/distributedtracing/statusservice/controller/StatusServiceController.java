package com.distributedtracing.statusservice.controller;

import com.distributedtracing.statusservice.model.ResponseMessage;
import com.distributedtracing.statusservice.service.StatusService;
import com.distributedtracing.statusservice.util.LogHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Slf4j
public class StatusServiceController {

  private StatusService service;

  @GetMapping("/success")
  @ResponseBody
  public Mono<ResponseMessage> success(@RequestHeader(name = "tracer", required = false) String tracer) {
    return service.getResponse().doOnEach(LogHelper.logOnNext(
       res -> log.debug("Request received on '/success' endpoint with tracer: " + tracer)
    ));
  }

  @GetMapping("/fail")
  @ResponseBody
  public Mono<ResponseMessage> fail(@RequestHeader(name = "tracer", required = false) String tracer) {
    return service.getResponse().doOnEach(LogHelper.logOnNext(
        res -> log.debug("Request received on '/fail' endpoint with tracer: " + tracer)
    ));
  }
}
