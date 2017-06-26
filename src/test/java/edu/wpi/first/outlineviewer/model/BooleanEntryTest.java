package edu.wpi.first.outlineviewer.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BooleanEntryTest {

  @Test
  public void testConstructor() {
    BooleanEntry entry = new BooleanEntry("key", false);
    assertEquals("/key", entry.getKey());
    assertFalse(entry.getValue());
    assertEquals("false", entry.getDisplayString());
    assertEquals("Boolean", entry.getType());
  }

}
