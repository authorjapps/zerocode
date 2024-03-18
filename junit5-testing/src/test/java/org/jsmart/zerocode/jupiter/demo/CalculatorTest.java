package org.jsmart.zerocode.jupiter.demo;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CalculatorTest {

    Calculator calculator = new Calculator();

    @Test
    @DisplayName("1 + 1 = 2")
    void addsTwoNumbers() {
        assertEquals(2, calculator.add(1, 1), "1 + 1 should equal 2");
    }

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({
            "1,    2,   3",
            "11,  22, 33"
    })
    void add(int first, int second, int expectedResult) {
        Calculator calculator = new Calculator();
        assertEquals(expectedResult, calculator.add(first, second),
                () -> first + " + " + second + " should equal " + expectedResult);
    }

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({
            "1,    2,   3",
            "11,  22, 33",
            "Hello World,  How, Hello World How"
    })
    void conCat(Object first, Object second, Object expectedResult) {
        Calculator calculator = new Calculator();
        System.out.println(first + "+" + second + "=" + expectedResult);
    }

    @ParameterizedTest(name = "run #{index} with [{arguments}]")
    @ValueSource(strings = {"Hello", "JUnit"})
    void withValueSource(String word) {
        assertNotNull(word);
    }

    @Test
    void testParamResolver() {
        String WelcomeMessage="Hello ${firstName} ${lastName}!";
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("firstName", "Peter");
        valuesMap.put("lastName", "Osi");
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String message = sub.replace(WelcomeMessage);
        assertEquals("Hello Peter Osi!", message);
    }
}
