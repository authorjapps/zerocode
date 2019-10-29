package com.myproject;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.myproject.tests.MyGetApiTest;
import com.myproject.tests.MyPutApiTest;
import com.myproject.tests.MyPostApiTest;

@RunWith(Suite.class)
@SuiteClasses({MyGetApiTest.class, MyPostApiTest.class, MyPutApiTest.class})
public class MyApiSuite {

}
