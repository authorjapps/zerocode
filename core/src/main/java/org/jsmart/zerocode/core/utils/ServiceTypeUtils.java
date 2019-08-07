package org.jsmart.zerocode.core.utils;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.jsmart.zerocode.core.domain.ZerocodeConstants.KAFKA;

import org.apache.commons.lang.StringUtils;

public class ServiceTypeUtils {

  public static ServiceType serviceType(String serviceName, String methodName) {
    ServiceType serviceType;

    if (StringUtils.isEmpty(serviceName) || isEmpty(methodName)) {
      serviceType = ServiceType.NONE;

    } else if (serviceName != null && serviceName.contains("/")) {
      serviceType = ServiceType.REST_CALL;

    } else if (serviceName != null && serviceName.contains(KAFKA)) {
      serviceType = ServiceType.KAFKA_CALL;

    } else {
      serviceType = ServiceType.JAVA_CALL;
    }

    return serviceType;
  }
}
