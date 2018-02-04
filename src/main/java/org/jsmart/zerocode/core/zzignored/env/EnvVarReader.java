package org.jsmart.zerocode.core.zzignored.env;

import java.util.Map;

public class EnvVarReader {

    public static void main(String[] args) {
        final Map<String, String> envMap = System.getenv();
        System.out.println("### getEnv ENV_NAME: " + System.getenv("ENV_NAME"));
        System.out.println("### getProp ENV_NAME: " + System.getProperty("ENV_NAME"));

        System.out.println("envMap: " + envMap);

        System.out.println("getProperty : " + System.getProperty("JAVA_HOME"));
        System.out.println("getEnv : " + System.getenv("JAVA_HOME"));
        System.out.println("getEnv : " + System.getenv("XYXYXY"));


        System.setProperty("NEW_VAR", "aNewValue");
        System.out.println("\n\ngetProperty : " + System.getProperty("NEW_VAR"));
        System.out.println("getEnv : " + System.getenv("NEW_VAR"));

    }
}
