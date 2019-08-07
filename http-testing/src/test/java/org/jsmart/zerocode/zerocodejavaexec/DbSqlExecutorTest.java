package org.jsmart.zerocode.zerocodejavaexec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.jsmart.zerocode.zerocodejavaexec.pojo.DbResult;
import org.junit.Test;

public class DbSqlExecutorTest {

  @Test
  public void testJavaMethod_exec() throws JsonProcessingException {
    DbSqlExecutor executor = new DbSqlExecutor();

    // ---------------------------------------------------------------------------
    // Call to the setters - Only needed during Unit testing
    // not needed while running via `@RunWith(ZeroCodeUnitRunner.class)`, because
    // the framework sets the values from the `@TargetEnv("my_web_app.properties")`
    // via Guice injection
    // ---------------------------------------------------------------------------
    executor.setDbUserName("localappuser");
    executor.setDbPassword("pass00rd");

    Map<String, List<DbResult>> resultMap =
        executor.fetchDbCustomers("select id, name from customers");

    ObjectMapper objectMapper = new ObjectMapper();

    String json = objectMapper.writeValueAsString(resultMap);

    assertThat(
        json,
        is("{\"results\":[{\"id\":1,\"name\":\"Elon Musk\"},{\"id\":2,\"name\":\"Jeff Bezos\"}]}"));
    System.out.println("json: " + json);
  }
}
