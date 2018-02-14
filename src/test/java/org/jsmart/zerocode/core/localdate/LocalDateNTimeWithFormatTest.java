package org.jsmart.zerocode.core.localdate;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * See:
 * https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
 * Look at the section below in the same page -
 All letters 'A' to 'Z' and 'a' to 'z' are reserved as pattern letters. The following pattern letters are defined:

 Symbol  Meaning                     Presentation      Examples
 ------  -------                     ------------      -------
 G       era                         text              AD; Anno Domini; A
 u       year                        year              2004; 04
 y       year-of-era                 year              2004; 04
 D       day-of-year                 number            189
 M/L     month-of-year               number/text       7; 07; Jul; July; J
 d       day-of-month                number            10
 .
 .
 .
 . more...
 */
public class LocalDateNTimeWithFormatTest {

    /**
     *
     #### date.toString(): 2018-02-11
     #### formattedString: 2018-02-11  // "uuuu-MM-dd"
     #### formattedString: 2018 02 11  // "uuuu MM dd"
     #### formattedString: 2018        // "yyyy"
     #### formattedString: 2018-Feb-11 // "uuuu-MMM-dd"
     #### formattedString: 2018-02-11  // "uuuu-LL-dd"
     */
    @Test
    public void testLocalDate_format() throws Exception {

        LocalDate date = LocalDate.now();
        // LocalDateTime date = LocalDateTime.now(); //<--- Also all assertions passes with LocalDateTime
        assertThat(date.toString(), notNullValue());
        System.out.println("#### date.toString(): " + date.toString()); //2018-02-11 : Default

        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd"); //2018-02-11
        String formattedString = localDate.format(formatter);
        assertThat(formattedString, notNullValue());
        System.out.println("#### formattedString: " + formattedString);

        localDate = LocalDate.now();
        formatter = DateTimeFormatter.ofPattern("uuuu MM dd"); //2018 02 11
        formattedString = localDate.format(formatter);
        assertThat(formattedString, notNullValue());
        System.out.println("#### formattedString: " + formattedString);

        formatter = DateTimeFormatter.ofPattern("yyyy"); //2018
        formattedString = localDate.format(formatter);
        assertThat(formattedString, notNullValue());
        System.out.println("#### formattedString: " + formattedString);

        formatter = DateTimeFormatter.ofPattern("uuuu-MMM-dd"); //2018-Feb-11
        formattedString = localDate.format(formatter);
        assertThat(formattedString, notNullValue());
        System.out.println("#### formattedString: " + formattedString);

        formatter = DateTimeFormatter.ofPattern("uuuu-LL-dd"); //2018-02-11
        formattedString = localDate.format(formatter);
        assertThat(formattedString, notNullValue());
        System.out.println("#### formattedString: " + formattedString);

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); //2018-02-11
        formattedString = localDate.format(formatter);
        assertThat(formattedString, notNullValue());
        System.out.println("#### formattedString: " + formattedString);

    }

    /**
     *
     The following are same time, printed in different format:

     #### date.toString(): 2018-02-11T21:31:20.989          // .toString()
     #### formattedString: 2018-02-11T21:31:21.041000000    // "uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSS"
     #### formattedString: 2018-02-11T21:31:21.41000000     // "uuuu-MM-dd'T'HH:mm:ss.n"
     #### formattedString: 2018-02-11T21:31:21.041000000    // "uuuu-MM-dd'T'HH:mm:ss.nnnnnnnnn"
     #### formattedString: 2018-02-11T21:31:21.77481041     // "uuuu-MM-dd'T'HH:mm:ss.A"
     #### formattedString: 2018-02-14                       // "uuuu-MM-dd" or "yyyy-MM-dd"

     See here more:
     ==> https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html

     H       hour-of-day (0-23)          number            0
     m       minute-of-hour              number            30
     s       second-of-minute            number            55
     S       fraction-of-second          fraction          978
     A       milli-of-day                number            1234
     n       nano-of-second              number            987654321
     N       nano-of-day                 number            1234000000

     */
    @Test
    public void testLocalDateTime_format() throws Exception {

        LocalDateTime date = LocalDateTime.now();
        assertThat(date.toString(), notNullValue());
        System.out.println("#### date.toString(): " + date.toString()); //2018-02-11T21:17:54.817 : Default

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSS"); //2018-02-11T21:25:14.055000000 <-- nine digits, extra 0
        String formattedString = localDateTime.format(formatter);
        assertThat(formattedString, notNullValue());
        System.out.println("#### formattedString: " + formattedString);

        formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.n"); //2018-02-11T21:25:14.55000000 <-- 8 digit
        formattedString = localDateTime.format(formatter);
        assertThat(formattedString, notNullValue());
        System.out.println("#### formattedString: " + formattedString);


        formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.nnnnnnnnn"); //2018-02-11T21:25:14.055000000 <-- nine digits, extra 0
        formattedString = localDateTime.format(formatter);
        assertThat(formattedString, notNullValue());
        System.out.println("#### formattedString: " + formattedString);

        formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.A"); //2018-02-11T21:25:14.055000000 <-- nine digits, extra 0
        formattedString = localDateTime.format(formatter);
        assertThat(formattedString, notNullValue());
        System.out.println("#### formattedString: " + formattedString);

        formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.N"); //2018-02-11T21:25:14.055000000 <-- nine digits, extra 0
        formattedString = localDateTime.format(formatter);
        assertThat(formattedString, notNullValue());
        System.out.println("#### formattedString: " + formattedString);

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); //2018-02-14 yyyy is same as uuuu in this example
        formattedString = localDateTime.format(formatter);
        assertThat(formattedString, notNullValue());
        System.out.println("#### formattedString: " + formattedString);

    }
}
