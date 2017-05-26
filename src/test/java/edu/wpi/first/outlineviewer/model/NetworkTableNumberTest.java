package edu.wpi.first.outlineviewer.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NetworkTableNumberTest {

  @Test
  public void testNetworkTableNumber() {
    final double value = 123.456;
    NetworkTableData data = NetworkTableData.createNetworkTableData("", value);

    assertEquals(value, ((NetworkTableNumber) data).valueProperty().getValue(), 0.001);
  }
}
