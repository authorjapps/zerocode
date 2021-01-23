package org.jsmart.zerocode.testhelp.tests.helloworldparameterizedcsv;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldParameterizedCsvTest {

    @Test
    @Scenario("parameterized_csv/hello_world_test_parameterized_csv.json")
    public void testGetByUserNames_csv() throws Exception {
    }

    @Test
    @Scenario("parameterized_csv/hello_world_test_parameterized_csv_source_files.json")
    public void testGetByUserNames_csvSourceFiles() throws Exception {
    }

    @Test
    @Scenario("parameterized_csv/hello_world_test_parameterized_csv_source_file_ignore_header.json")
    public void testGetByUserNames_csvSourceFiles_ignoringHeader() throws Exception {
    }

}
