package edu.wpi.first.outlineviewer.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NetworkTableBooleanTest {

  @Test
  public void testNetworkTableBoolean() {
    final boolean value = false;
    NetworkTableData data = NetworkTableData.createNetworkTableData("", value);

    assertEquals(value, ((NetworkTableBoolean) data).valueProperty().getValue());
  }
}
