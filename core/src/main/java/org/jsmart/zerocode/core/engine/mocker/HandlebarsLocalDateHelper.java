package org.jsmart.zerocode.core.engine.mocker;

import com.github.jknack.handlebars.Options;
import com.github.tomakehurst.wiremock.extension.responsetemplating.helpers.DateOffset;
import com.github.tomakehurst.wiremock.extension.responsetemplating.helpers.HandlebarsHelper;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class HandlebarsLocalDateHelper extends HandlebarsHelper {
    public Object apply(Object context, Options options) {
        String offset = options.hash("offset", null);
        Date date = new Date();
        if (offset != null) {
            date = (new DateOffset(offset)).shift(date);
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
