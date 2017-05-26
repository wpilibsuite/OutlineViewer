package edu.wpi.first.outlineviewer.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class NetworkTableBooleanArrayTest {

  @Test
  public void testNetworkTableBooleanArray() {
    final boolean[] value = new boolean[]{false};
    NetworkTableData data = NetworkTableData.createNetworkTableData("", value);

    assertEquals(Arrays.toString(value),
        ((NetworkTableBooleanArray) data).valueProperty().getValue());
  }
}
