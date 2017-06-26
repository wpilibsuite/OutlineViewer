package edu.wpi.first.outlineviewer.entry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RawBytesEntryTest {

  @Test
  public void testDisplayString() {
    byte[] bytes = {0, 1, 127, (byte) 128, (byte) 254, (byte) 255};
    RawBytesEntry entry = new RawBytesEntry("", bytes);
    String expected = "[0x00, 0x01, 0x7F, 0x80, 0xFE, 0xFF]";
    assertEquals(expected, entry.getDisplayString());
  }

}
