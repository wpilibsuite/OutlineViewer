package edu.wpi.first.outlineviewer.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class NetworkTableNumberArrayTest {

  @Test
  public void testNetworkTableNumberArray() {
    final double[] value = new double[]{13.0};
    NetworkTableData data = NetworkTableData.createNetworkTableData("", value);

    assertEquals(Arrays.toString(value),
        ((NetworkTableNumberArray) data).valueProperty().getValue());
  }
}
