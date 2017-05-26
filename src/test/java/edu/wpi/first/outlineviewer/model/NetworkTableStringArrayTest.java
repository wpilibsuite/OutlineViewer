package edu.wpi.first.outlineviewer.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class NetworkTableStringArrayTest {

  @Test
  public void testNetworkTableStringArray() {
    final String[] value = new String[]{""};
    NetworkTableData data = NetworkTableData.createNetworkTableData("", value);

    assertEquals(Arrays.toString(value),
        ((NetworkTableStringArray) data).valueProperty().getValue());
  }
}
