package com.github.idosu.reflect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

/**
 * TODO: desc
 *
 * @author
 *      <br>28 Jan 2017 idosu
 */
public abstract class ClassesTest {
    @RunWith(Parameterized.class)
    public static class getTypeParameter extends ClassesTest {
        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        @interface Expected {
            Class<?> value();
        }


        public @Parameter(0) Class<?> implementor;
        public @Parameter(1) Class<?> expected;
        //public @Parameter Class<?> classToSearch;
        //public @Parameter int typeParameterIndex;

        @Parameters(name="{index} - from class {0} expected {1}")
        public static Iterable<Object[]> params() {
            return stream(getTypeParameter.class.getDeclaredClasses())
                .filter(x -> x.isAnnotationPresent(Expected.class))
                .map(x -> new Object[] {
                    x,
                    x.getAnnotation(Expected.class).value()
                })
                .collect(toList());
        }

        @Test
        public void test() {
            assertEquals(
                Object.class,
                Classes.getTypeParameter(Search.class, 0, implementor)
            );
        }

        interface Search<T> { }

        // Combinations like ( Search<String>, Search<Object> ) or ( Search, Search<Object> ) are not allowed

        // Interfaces

        @Expected(Object.class)
        interface InterfaceRaw extends Search { }

        @Expected(Object.class)
        interface InterfaceObject extends Search<Object> { }

        @Expected(String.class)
        interface InterfaceString extends Search<String> { }

        @Expected(Object.class)
        interface InterfaceSonObject extends InterfaceObject { }

        @Expected(String.class)
        interface InterfaceSonString extends InterfaceString { }

        // ? extends Object
        interface InterfaceGeneric<T> extends Search<T> { }

        // ? extends Object
        interface InterfaceGenericOtherName<U> extends Search<U> { }

        // ? extends String
        interface InterfaceGenericBounds<T extends String> extends Search<T> { }

        @Expected(String.class)
        interface InterfaceOtherLocationClass extends InterfaceOtherLocation<Objects, String> { }
        interface InterfaceOtherLocation<T1, T2> extends Search<T2> { }

        // Classes

        @Expected(Object.class)
        class ClassRaw implements Search { }

        @Expected(Object.class)
        class ClassObject implements Search<Object> { }

        @Expected(String.class)
        class ClassString implements Search<String> { }

        @Expected(Object.class)
        class ClassSonObject extends ClassObject { }

        @Expected(String.class)
        class ClassSonString extends ClassString { }

        // ? extends Object
        class ClassGeneric<T> implements Search<T> { }

        // ? extends Object
        class ClassGenericOtherName<U> implements Search<U> { }

        // ? extends String
        class ClassGenericBounds<T extends String> implements Search<T> { }

        // String
        @Expected(String.class)
        class ClassOtherLocationClass extends ClassOtherLocation<Objects, String> { }
        class ClassOtherLocation<T1, T2> implements Search<T2> { }
        
        // Interfaces & Classes

        @Expected(Object.class)
        class ICRaw extends ClassRaw implements InterfaceRaw { }

        @Expected(Object.class)
        class ICObject extends ClassObject implements InterfaceObject { }

        @Expected(String.class)
        class ICString extends ClassString implements InterfaceString { }

        @Expected(String.class)
        class ICMulti extends ClassString implements InterfaceString, Search<String> { }
    }
}