//package org.jsmart.zerocode.core.engine.executor.javaapi;
//
//public class CustomHello {
//    public static void main(String[] args) {
//        new CustomHello().exec();
//    }
//
//    public void exec() {
//        try {
//            java.lang.reflect.Method method;
//            Object result;
//            Class<?> clazz;
//            clazz = Class.forName("org.jsmart.zerocode.core.engine.executor.javaapi.MyCustomComparator");
//            method = clazz.getMethod("strLen", String.class);
//            result = method.invoke(MyCustomComparator.class, "Hello");
//            System.out.println("result ++++ " + result);
//
//            method = clazz.getMethod("strLen", String.class, String.class);
//            result = method.invoke(MyCustomComparator.class, "Hello", "Hello2");
//            System.out.println("result2 ++++ " + result);
//
//            clazz = Class.forName("java.lang.Thread");
//            method = clazz.getMethod("sleep", long.class);
//            result = method.invoke(Thread.class, 5000L);
//            System.out.println("Slept ++++ " + result);
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//}
