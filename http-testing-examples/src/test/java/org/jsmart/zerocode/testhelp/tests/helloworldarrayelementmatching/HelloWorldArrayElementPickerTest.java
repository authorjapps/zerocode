package org.jsmart.zerocode.testhelp.tests.helloworldarrayelementmatching;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldArrayElementPickerTest {

    @Test
    @JsonTestCase("helloworld_array_dynamic_element/array_element_picker_newway_test.json")
    public void testArrayDynamicElementPick() throws Exception {

    }

    @Test
    @JsonTestCase("helloworld_array_dynamic_element/array_element_picker_old_fashioned_test.json")
    public void testArrayDynamicElementPick_oldFashioned() throws Exception {

    }

}
