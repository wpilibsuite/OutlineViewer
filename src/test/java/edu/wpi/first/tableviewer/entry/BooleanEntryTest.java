package edu.wpi.first.tableviewer.entry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BooleanEntryTest {

  @Test
  public void testConstructor() {
    BooleanEntry entry = new BooleanEntry("key", false);
    assertEquals("/key", entry.getKey());
    assertEquals(false, entry.getValue());
    assertEquals("false", entry.getDisplayString());
    assertEquals("Boolean", entry.getType());
  }

}
