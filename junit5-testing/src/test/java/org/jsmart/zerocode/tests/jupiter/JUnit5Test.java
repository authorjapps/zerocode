package org.jsmart.zerocode.tests.jupiter;

import org.jsmart.zerocode.tests.postgres.ExtensionA;
import org.jsmart.zerocode.tests.postgres.ExtensionB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({ExtensionA.class, ExtensionB.class})
public class JUnit5Test {

    @Test
    public void testX() {
        out.println("*JUnit5 ---> testX()");
        assertTrue(2 == 2); //jupiter assert
    }

    @Test
    public void testY() {
        out.println("*JUnit5 ---> testY()");
        assertTrue(2 == 2); //jupiter assert
    }

}