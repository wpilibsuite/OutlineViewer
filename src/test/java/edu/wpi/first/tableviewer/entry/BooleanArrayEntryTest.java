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
    assertEquals("Boolean[100]", new BooleanArrayEntry("", new boolean[100]).getType());
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
