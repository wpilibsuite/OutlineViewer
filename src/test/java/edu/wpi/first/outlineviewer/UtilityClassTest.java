package edu.wpi.first.outlineviewer;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
abstract class UtilityClassTest {

  private final Class clazz;

  UtilityClassTest(Class clazz) {
    this.clazz = clazz;
  }

  @Test
  void testConstructorPrivate() {
    Constructor constructor = clazz.getDeclaredConstructors()[0];

    assertFalse(constructor.isAccessible());
  }

  @Test
  void testConstructorReflection() throws Throwable {
    assertThrows(InvocationTargetException.class, () -> {
      Constructor constructor = clazz.getDeclaredConstructors()[0];
      constructor.setAccessible(true);
      constructor.newInstance();
    });
  }
}
