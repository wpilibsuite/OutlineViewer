package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.outlineviewer.model.NetworkTableData;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class NetworkTableTest extends OutlineViewerControllerTest {

  @BeforeClass
  public static void beforeClass() {
    NetworkTable.shutdown();

    NetworkTable.setServerMode();
    NetworkTable.initialize();
  }

  @AfterClass
  public static void afterClass() {
    NetworkTable.shutdown();
  }

  @Test
  public void testAddNetworkTableData() throws InterruptedException {
    NetworkTable.getTable("").putString("/A/path/key", "value");
    Thread.sleep(150);

    Assert.assertTrue(outlineViewerController
        .getRootData().getChild(NetworkTableData.getKeyPath("A/path/key")).isPresent());
  }

  @Test
  public void testNetworkTableUpdate() throws InterruptedException {
    final String expected = "value 2";
    NetworkTable.getTable("").putString("/path/key", "value");
    Thread.sleep(150);
    NetworkTable.getTable("").putString("/path/key", expected);
    Thread.sleep(150);

    Assert.assertEquals(expected, outlineViewerController.getRootData()
        .getChild(NetworkTableData.getKeyPath("path/key")).get().valueProperty().getValue());
  }

  @Test
  public void testRemoveNetworkTableData() throws InterruptedException {
    NetworkTable.getTable("").putString("key", "value");
    Thread.sleep(150);
    NetworkTable.getTable("").delete("key");
    Thread.sleep(150);

    Assert.assertFalse(outlineViewerController
        .getRootData().getChild(NetworkTableData.getKeyPath("key")).isPresent());
  }
}
