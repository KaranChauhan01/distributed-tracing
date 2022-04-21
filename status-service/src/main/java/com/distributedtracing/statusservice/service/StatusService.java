package com.distributedtracing.statusservice.service;

import com.distributedtracing.statusservice.model.ResponseMessage;
import com.distributedtracing.statusservice.util.LogHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class StatusService {

  public Mono<ResponseMessage> getResponse() {
    return Mono
        .just(new ResponseMessage("Hello there, have a nice day!!"))
        .doOnEach(LogHelper.logOnNext(
          res -> log.info("Successfully sent back response")
        ));
  }
}
