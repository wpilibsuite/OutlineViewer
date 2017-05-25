package edu.wpi.first.outlineviewer.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NetworkTableStringTest {

  @Test
  public void testNetworkTableString() {
    final String value = "Some string";
    NetworkTableData data = NetworkTableData.createNetworkTableData("String Key", value);

    assertEquals(value, ((NetworkTableString) data).valueProperty().getValue());
  }
}
