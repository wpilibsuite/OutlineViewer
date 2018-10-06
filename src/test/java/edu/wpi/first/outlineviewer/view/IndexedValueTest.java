package edu.wpi.first.outlineviewer.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import org.junit.jupiter.api.Test;

class IndexedValueTest {

  @Test
  void testIsEqual() {
    IndexedValue<String> val1 = new IndexedValue<>(0, "Foo");
    IndexedValue<String> val2 = new IndexedValue<>(0, "Foo");
    assertEquals(val1, val2);
  }

  @Test
  void testIsNotEqual() {
    IndexedValue<String> val1 = new IndexedValue<>(0, "Foo");
    IndexedValue<String> val2 = new IndexedValue<>(0, "Bar");
    assertFalse(val1.equals(val2));
  }

  @Test
  void testIsNotEqualIndex() {
    IndexedValue<String> val1 = new IndexedValue<>(0, "Foo");
    IndexedValue<String> val2 = new IndexedValue<>(1, "Foo");
    assertFalse(val1.equals(val2));
  }

  @Test
  void testHashCodeIsEqual() {
    IndexedValue<String> val1 = new IndexedValue<>(0, "Foo");
    IndexedValue<String> val2 = new IndexedValue<>(0, "Foo");
    assertEquals(val1.hashCode(), val2.hashCode());
  }

  @Test
  void testHashCodeIsNotEqual() {
    IndexedValue<String> val1 = new IndexedValue<>(0, "Foo");
    IndexedValue<String> val2 = new IndexedValue<>(0, "Bar");
    assertNotSame(val1.hashCode(), val2.hashCode());
  }

  @Test
  void testHashCodeIsNotEqualIndex() {
    IndexedValue<String> val1 = new IndexedValue<>(0, "Foo");
    IndexedValue<String> val2 = new IndexedValue<>(1, "Foo");
    assertNotSame(val1.hashCode(), val2.hashCode());
  }

}
