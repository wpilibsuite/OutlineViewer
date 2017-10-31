package edu.wpi.first.outlineviewer.view;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

class IndexedValueTest {

  @Test
  void testIsEqual() {
    IndexedValue<String> val1 = new IndexedValue<>(0, "Foo");
    IndexedValue<String> val2 = new IndexedValue<>(0, "Foo");
    assertTrue(val1.equals(val2));
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
    assertTrue(val1.hashCode() == val2.hashCode());
  }

  @Test
  void testHashCodeIsNotEqual() {
    IndexedValue<String> val1 = new IndexedValue<>(0, "Foo");
    IndexedValue<String> val2 = new IndexedValue<>(0, "Bar");
    assertFalse(val1.hashCode() == val2.hashCode());
  }

  @Test
  void testHashCodeIsNotEqualIndex() {
    IndexedValue<String> val1 = new IndexedValue<>(0, "Foo");
    IndexedValue<String> val2 = new IndexedValue<>(1, "Foo");
    assertFalse(val1.hashCode() == val2.hashCode());
  }

}
