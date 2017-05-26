package edu.wpi.first.outlineviewer.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class NetworkTableRawTest {

  @Test
  public void testNetworkTableRaw() {
    final byte[] value = new byte[]{0x00};
    NetworkTableData data = NetworkTableData.createNetworkTableData("", value);

    assertEquals(Arrays.toString(value),
        ((NetworkTableRaw) data).valueProperty().getValue());
  }
}
