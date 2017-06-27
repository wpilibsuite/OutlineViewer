package edu.wpi.first.outlineviewer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertFalse;

public class UtilityClassTest {

  private final Class clazz;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  public UtilityClassTest(Class clazz) {
    this.clazz = clazz;
  }

  @Test
  public void testConstructorPrivate() {
    Constructor[] c = clazz.getDeclaredConstructors();
    assertFalse(c[0].isAccessible());
  }

  @Test
  @SuppressWarnings("PMD.EmptyCatchBlock")
  public void testConstructorReflection() throws Throwable {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("This is a utility class!");
    Constructor[] c = clazz.getDeclaredConstructors();
    c[0].setAccessible(true);

    try {
      c[0].newInstance();
    } catch (InvocationTargetException | InstantiationException e) {
      throw e.getCause();
    }
  }
}
