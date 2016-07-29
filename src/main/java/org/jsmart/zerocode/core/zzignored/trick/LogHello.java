package org.jsmart.zerocode.core.zzignored.trick;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHello {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogHello.class);

    public static void main(String[] args) {
        LOGGER.info("###Hello - " + LogHello.class.getName());
    }
}
