package org.jsmart.zerocode.core.zzignored.trick;

import org.jsmart.zerocode.core.runner.Notifier;

public class NotifierFuncMain {

    /*
     * overloaded functions
     */
    public <A, B, C, D, E, R> R callAdd(A a, B b, C c, D d, E e, Notifier<A, B, C, D, E, R> notifier){

        R result = notifier.apply(a, b, c, d, e);

        return result;
    }

    public <A, B, C, D, E, R> R callAdd2(A a, B b, C c, Notifier<A, B, C, D, E, R> notifier){

        R result = notifier.apply(a, b, c, null, null);

        return result;
    }

    public <A, B, C, D, E, R> R callAdd3(A a, B b, Notifier<A, B, C, D, E, R> notifier){

        R result = notifier.apply(a, b, null, null, null);

        return result;
    }


    public Integer addThreeNums(Integer int1, Integer int2, Integer int3, Integer int4, Integer int5){

        return int1 + int2 + int3;
    }

    public Boolean addThreeNumsSucceeded(Integer int1, Integer int2, Integer int3, String str4, Float flt5){

        return false;
    }

    public static void main(String[] args) {

        NotifierFuncMain adderImpl = new NotifierFuncMain();

        final int result = adderImpl.callAdd(1, 2, 3, 4, null, adderImpl::addThreeNums);

        Boolean isSuccess = adderImpl.callAdd2(1, 2, 3, adderImpl::addThreeNumsSucceeded);
    }
}
