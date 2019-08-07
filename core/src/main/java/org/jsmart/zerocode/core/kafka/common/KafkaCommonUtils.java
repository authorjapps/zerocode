package org.jsmart.zerocode.core.kafka.common;

import static org.jsmart.zerocode.core.utils.TokenUtils.resolveKnownTokens;

import java.util.Properties;

public class KafkaCommonUtils {

  public static void resolveValuePlaceHolders(Properties properties) {
    properties
        .keySet()
        .forEach(
            key -> {
              String value = properties.getProperty(key.toString());
              String resolvedValue = resolveKnownTokens(value);
              if (!value.equals(resolvedValue)) {
                properties.put(key, resolvedValue);
              }
            });
  }
}
