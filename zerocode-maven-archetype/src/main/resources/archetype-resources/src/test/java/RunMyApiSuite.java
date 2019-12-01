package ${groupId};

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import ${groupId}.MyApiSuite;

public class RunMyApiSuite {
    public static void main (String[] args){
        Result result = JUnitCore.runClasses(MyApiSuite.class);
        for (Failure failure : result.getFailures()){
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }
}
