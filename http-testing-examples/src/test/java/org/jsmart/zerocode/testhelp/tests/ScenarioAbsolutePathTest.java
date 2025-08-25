package org.jsmart.zerocode.testhelp.tests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class ScenarioAbsolutePathTest {

    // Trying in target folder
    private static String folder1File1 = "target/temp/unit_test_files/cherry_pick_tests/folder_a/test_case_1.json";
    private static String sourceResourceFile = "helloworld/hello_world_status_ok_assertions.json";

    @BeforeClass
    public static void start() {
        System.out.println("INSIDE BEFORE");
        Path path = Paths.get(folder1File1);
        try {
            Files.createDirectories(path.getParent());
            String absolutePath = path.toFile().getAbsolutePath();
            File f1 = new File(absolutePath);
            f1.createNewFile();

            InputStream resourceStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(sourceResourceFile);
            if (resourceStream == null) {
                throw new FileNotFoundException(
                        "Resource file '" + sourceResourceFile + "' not found in test resources.");
            }

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = resourceStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }

            String contents = outStream.toString("UTF-8");

            Files.write(path, contents.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException exx) {
            throw new RuntimeException("Create file '" + folder1File1 + "' Exception" + exx);
        }
    }

    @Test
    @Scenario("target/temp/unit_test_files/cherry_pick_tests/folder_a/test_case_1.json")
    public void testAbsolutePathGet() throws Exception {
    }
}
