package edu.wpi.first.outlineviewer.model;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NetworkTableDataTest {

  @Before
  public void before() {
    NetworkTable.shutdown();
  }

  @Test
  public void testDefaultValue() {
    NetworkTableData data = new NetworkTableData("");

    Assert.assertEquals("Default value was not empty", "", data.valueProperty().getValue());
  }

  @Test
  public void testKey() {
    final String key = "Some key";
    NetworkTableData data = new NetworkTableData(key);

    Assert.assertEquals("Default value was not empty", key, data.keyProperty().get());
  }

  @Test
  public void testChildrenEmpty() {
    NetworkTableData data = new NetworkTableData("");

    Assert.assertTrue("Default children was not empty", data.getChildren().isEmpty());
  }

  @Test
  public void testNetworkTableBoolean() {
    final boolean value = false;
    NetworkTableBoolean data = new NetworkTableBoolean("Boolean Key", value);

    Assert.assertEquals(value, data.valueProperty().get());
  }

  @Test
  public void testNetworkTableNumber() {
    final double value = 123.456;
    NetworkTableNumber data = new NetworkTableNumber("Number Key", value);

    Assert.assertEquals(value, data.valueProperty().get(), 0.0001);
  }

  @Test
  public void testNetworkTableString() {
    final String value = "Some string";
    NetworkTableString data = new NetworkTableString("String Key", value);

    Assert.assertEquals(value, data.valueProperty().get());
  }
}
