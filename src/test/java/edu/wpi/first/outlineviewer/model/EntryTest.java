package edu.wpi.first.outlineviewer.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EntryTest {

  @Test
  @SuppressWarnings("PMD.EmptyCatchBlock")
  public void invalidEntryForTest() {
    try {
      Entry.entryFor("", new Object());
      fail();
    } catch (IllegalArgumentException ex) {
      // We want this exception to be thrown
    }
  }

  @Test
  public void testTableEntryDisplayString() {
    TableEntry entry = new TableEntry("");

    assertEquals("", entry.getDisplayString());
  }

  @Test
  public void testTableEntryTypeString() {
    TableEntry entry = new TableEntry("");

    assertEquals("", entry.getTypeString(null));
  }

  @Test
  public void testRootPath() {
    RootTableEntry root = new RootTableEntry();

    assertEquals("/", root.getKey());
  }

}
