package edu.wpi.first.tableviewer.entry;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class BooleanArrayEntryTest {

  @Test
  public void testTypeStringEmpty() {
    assertEquals("Boolean[0]", new BooleanArrayEntry("", new boolean[0]).getType());
  }

  @Test
  public void testTypeString() {
    for (int i = 0; i < 100; i++) {
      assertEquals("Boolean[" + i + "]", new BooleanArrayEntry("", new boolean[i]).getType());
    }
  }

  @Test
  public void testDisplayString() {
    boolean[] array = new boolean[65535];
    for (int i = 0; i < array.length; i++) {
      array[i] = Math.random() < 0.5;
    }
    BooleanArrayEntry entry = new BooleanArrayEntry("", array);
    assertEquals(Arrays.toString(array), entry.getDisplayString());
  }

}
