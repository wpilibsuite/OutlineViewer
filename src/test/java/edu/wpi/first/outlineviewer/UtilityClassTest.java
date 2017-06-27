package edu.wpi.first.outlineviewer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertFalse;

@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class UtilityClassTest {

  private final Class clazz;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  public UtilityClassTest(Class clazz) {
    this.clazz = clazz;
  }

  @Test
  public void testConstructorPrivate() {
    Constructor constructor = clazz.getDeclaredConstructors()[0];

    assertFalse(constructor.isAccessible());
  }

  @Test
  @SuppressWarnings("PMD.EmptyCatchBlock")
  public void testConstructorReflection() throws Throwable {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("This is a utility class!");
    Constructor constructor = clazz.getDeclaredConstructors()[0];
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
    } catch (InvocationTargetException | InstantiationException ex) {
      throw ex.getCause();
    }
  }
}
