package edu.wpi.first.outlineviewer.model;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TreeRowTest {

  @Test
  void keyConstructorTest() {
    assertEquals("Key", new TreeRow("Key").getKey());
  }

  @Test
  void keyConstructorNullTest() {
    assertThrows(NullPointerException.class, () -> new TreeRow(null));
  }

  @Test
  void valueConstructorTest() {
    Object object = new Object();
    TreeRow treeRow = new TreeRow("Key", object);

    assertEquals(object, treeRow.getValue());
  }

  @Test
  void valueConstructorNullTest() {
    assertThrows(NullPointerException.class, () -> new TreeRow("", null));
  }

  @Test
  void keyPropertyTest() {
    assertEquals("Key", new TreeRow("Key").keyProperty().get());
  }

  @Test
  void valuePropertyTest() {
    Object object = new Object();
    assertEquals(object, new TreeRow("Key", object).valueProperty().get());
  }

  @Test
  void getTypeTest() {
    assertEquals("MyType", new FakeTreeRow("MyType").getType());
  }

  @Test
  void toStringTest() {
    assertEquals("TreeRow{Key=key, Value=value, Type=, Last Updated=0}", new TreeRow("key", "value").toString());
  }

  private static class FakeTreeRow extends TreeRow {

    FakeTreeRow(String type) {
      super();

      typeProperty().setValue(type);
    }

  }
}
