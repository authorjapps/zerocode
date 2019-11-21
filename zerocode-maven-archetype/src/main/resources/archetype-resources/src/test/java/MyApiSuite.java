package ${groupId};

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ${groupId}.tests.MyGetApiTest;
import ${groupId}.tests.MyPutApiTest;
import ${groupId}.tests.MyPostApiTest;

@RunWith(Suite.class)
@SuiteClasses({MyGetApiTest.class, MyPostApiTest.class, MyPutApiTest.class})
public class MyApiSuite {

}
