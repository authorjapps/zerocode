package org.jsmart.zerocode.cli.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationUtils.class);

    private static final String ANNOTATION_DATA = "annotationData";
    private static final String ANNOTATIONS = "annotations";

    public static void alterClassAnnotation(Class<?> targetClass,
                                            Class<? extends Annotation> targetAnnotation,
                                            Annotation targetValue) {
        try {
            Method method = Class.class.getDeclaredMethod(ANNOTATION_DATA, null);
            method.setAccessible(true);

            Object annotationData = method.invoke(targetClass);

            Field annotations = annotationData.getClass().getDeclaredField(ANNOTATIONS);
            annotations.setAccessible(true);

            Map<Class<? extends Annotation>, Annotation> annotationMap
                    = (Map<Class<? extends Annotation>, Annotation>) annotations.get(annotationData);

            annotationMap.put(targetAnnotation, targetValue);

        } catch (Exception e) {
            String msg = "Exception occurred during ClassAnnotation handling";
            LOGGER.error(msg + e);
            throw new RuntimeException(msg + e);
        }
    }

    public static void alterMethodAnnotation(Class<?> targetClass,
                                             Class<? extends Annotation> targetAnnotation,
                                             Annotation targetValue,
                                             String methodName) {
        try {
            Method method = targetClass.getDeclaredMethod(methodName);
            method.setAccessible(true);

            method.getDeclaredAnnotations();
            Class<?> superclass = method.getClass().getSuperclass();
            Field declaredField = superclass.getDeclaredField("declaredAnnotations");
            declaredField.setAccessible(true);
            Map<Class<? extends Annotation>, Annotation> annotationMap
                    = (Map<Class<? extends Annotation>, Annotation>) declaredField
                    .get(method);

            annotationMap.put(targetAnnotation, targetValue);

        } catch (Exception e) {
            String msg = "Exception occurred during MethodAnnotation handling";
            LOGGER.error(msg + e);
            throw new RuntimeException(msg + e);
        }
    }

}