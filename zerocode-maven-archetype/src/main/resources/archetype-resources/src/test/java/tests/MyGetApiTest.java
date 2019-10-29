/**
 *
 * @author MavenAutoGen
 */

package ${groupId}.tests;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("hostconfig_ci.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class MyGetApiTest {

    @Test
    @Scenario("tests/get_api_200.json")
    public void testGet() throws Exception {

    }
}