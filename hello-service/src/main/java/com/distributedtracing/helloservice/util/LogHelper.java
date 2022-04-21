package com.distributedtracing.helloservice.util;

import org.slf4j.MDC;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static com.distributedtracing.helloservice.model.Constants.CONTEXT_MAP;

public final class LogHelper {

  public static <T> Consumer<Signal<T>> logOnNext(Consumer<T> log) {
    return signal -> {
      if (signal.getType() != SignalType.ON_NEXT) return;
      logWithMDC(signal, log);
    };
  }

  public static <T> void logWithMDC(Signal<T> signal, Consumer<T> log) {
    Optional<Map<String, String>> contextMap = signal.getContextView().getOrEmpty(CONTEXT_MAP);
    if (contextMap.isEmpty()) {
      log.accept(signal.get());
    } else {
      MDC.setContextMap(contextMap.get());
      try {
        log.accept(signal.get());
      } finally {
        MDC.clear();
      }
    }
  }

}
