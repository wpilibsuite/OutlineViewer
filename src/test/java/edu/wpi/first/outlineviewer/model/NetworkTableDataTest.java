package edu.wpi.first.outlineviewer.model;

import com.google.common.collect.Lists;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.junit.Before;
import org.junit.Test;

import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NetworkTableDataTest {

  @Before
  public void before() {
    NetworkTable.shutdown();
  }

  @Test
  public void testDefaultValue() {
    NetworkTableData data = new NetworkTableData("");

    assertEquals("Default value was not empty", "", data.valueProperty().getValue());
  }

  @Test
  public void testKey() {
    final String key = "Some key";
    NetworkTableData data = new NetworkTableData(key);

    assertEquals("Default value was not empty", key, data.keyProperty().get());
  }

  @Test
  public void testChildrenEmpty() {
    NetworkTableData data = new NetworkTableData("");

    assertTrue("Default children was not empty", data.getChildren().isEmpty());
  }

  @Test
  public void testNetworkTableBoolean() {
    final boolean value = false;
    NetworkTableBoolean data = new NetworkTableBoolean("Boolean Key", value);

    assertEquals(value, data.valueProperty().get());
  }

  @Test
  public void testNetworkTableNumber() {
    final double value = 123.456;
    NetworkTableNumber data = new NetworkTableNumber("Number Key", value);

    assertEquals(value, data.valueProperty().get(), 0.0001);
  }

  @Test
  public void testNetworkTableString() {
    final String value = "Some string";
    NetworkTableString data = new NetworkTableString("String Key", value);

    assertEquals(value, data.valueProperty().get());
  }

  @Test
  public void testAddChild() {
    NetworkTableData data = new NetworkTableData("");
    data.addChild(new NetworkTableData("Other"));

    assertTrue(data.getChildren().containsKey("Other"));
  }

  @Test
  public void testRemove() {
    NetworkTableData data = new NetworkTableData("");
    NetworkTableData other = new NetworkTableData("Other 1");
    data.addChild(other);
    data.addChild(new NetworkTableData("Other 2"));

    other.remove();

    assertFalse(data.getChildren().containsKey("Other 1"));
  }

  @Test
  public void testRemoveParent() {
    NetworkTableData data = new NetworkTableData("");
    NetworkTableData parent = new NetworkTableData("Parent");
    NetworkTableData child = new NetworkTableData("Child");
    data.addChild(parent);
    parent.addChild(child);

    child.remove();

    assertFalse(data.getChildren().containsKey("Parent"));
  }


  @Test
  public void testGetChildPresent() {
    NetworkTableData data = new NetworkTableData("");
    NetworkTableData parent = new NetworkTableData("Parent");
    NetworkTableData child = new NetworkTableData("Child");
    data.addChild(parent);
    parent.addChild(child);

    Queue<String> keys = Lists.newLinkedList(Lists.newArrayList("Parent", "Child"));

    assertEquals(child, data.getChild(keys).get());
  }

  @Test
  public void testGetChildNotPresent() {
    NetworkTableData data = new NetworkTableData("");
    NetworkTableData parent = new NetworkTableData("Parent");
    data.addChild(parent);

    Queue<String> keys = Lists.newLinkedList(Lists.newArrayList("Parent", "Child"));

    assertFalse(data.getChild(keys).isPresent());
  }

  @Test
  public void testGetChildParentNotPresent() {
    NetworkTableData data = new NetworkTableData("");
    NetworkTableData parent = new NetworkTableData("Parent");
    NetworkTableData child = new NetworkTableData("Child");
    data.addChild(parent);
    parent.addChild(child);

    Queue<String> keys = Lists.newLinkedList(Lists.newArrayList("Not-Parent", "Child"));

    assertFalse(data.getChild(keys).isPresent());
  }
}
