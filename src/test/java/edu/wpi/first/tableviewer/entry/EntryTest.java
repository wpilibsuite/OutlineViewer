package edu.wpi.first.tableviewer.entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class EntryTest {

  @Parameterized.Parameters(name = "{index}: {1} instanceof {0}")
  public static Object[][] params() {
    return new Object[][]{
        new Object[]{BooleanEntry.class, Entry.entryFor("key", false)},
        new Object[]{BooleanArrayEntry.class, Entry.entryFor("key", new boolean[0])},
        new Object[]{NumberEntry.class, Entry.entryFor("key", 0)},
        new Object[]{NumberEntry.class, Entry.entryFor("key", 0.0)},
        new Object[]{NumberEntry.class, Entry.entryFor("key", 0L)},
        new Object[]{NumberArrayEntry.class, Entry.entryFor("key", new double[0])},
        new Object[]{RawBytesEntry.class, Entry.entryFor("key", new byte[0])},
        new Object[]{StringEntry.class, Entry.entryFor("key", "some string")},
        new Object[]{StringArrayEntry.class, Entry.entryFor("key", new String[0])}
    };
  }

  private final Class<?> expectedEntryType;
  private final Entry<?> actualEntry;

  public EntryTest(Class<?> expectedEntryType, Entry<?> actualEntry) {
    this.expectedEntryType = expectedEntryType;
    this.actualEntry = actualEntry;
  }

  @Test
  public void test() {
    assertEquals(expectedEntryType, actualEntry.getClass());
  }

}
