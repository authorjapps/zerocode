package org.jsmart.zerocode.core.runner;

@FunctionalInterface
public interface Notifier<A, B, C, D, E, R> {
    /*
     * R is Return, but does not have to be the last in the list nor named R.
     */
    R apply (A a, B b, C c, D d, E e);
}