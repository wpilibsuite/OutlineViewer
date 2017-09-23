package edu.wpi.first.outlineviewer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

class TypeUtilitiesTest extends UtilityClassTest {

  TypeUtilitiesTest() {
    super(TypeUtilities.class);
  }

  private static Stream<Arguments> optionalCastArguments() {
    return Stream.of(
        Arguments.of(false, Bar.class, new Foo()),
        Arguments.of(true, Bar.class, new Zap()),
        Arguments.of(true, Foo.class, new Zap()),
        Arguments.of(false, Bar.class, new Object())
    );
  }

  @ParameterizedTest
  @MethodSource("optionalCastArguments")
  void optionalCastTest(boolean expected, Class<?> clazz, Object object) {
    assertEquals(expected, TypeUtilities.optionalCast(object, clazz).isPresent());
  }

  private static class Foo {

  }

  private static class Bar extends Foo {

  }

  private static class Zap extends Bar {

  }
}
