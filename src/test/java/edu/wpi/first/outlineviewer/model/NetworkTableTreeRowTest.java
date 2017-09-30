package edu.wpi.first.outlineviewer.model;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.outlineviewer.NetworkTableUtilities;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class NetworkTableTreeRowTest {

  @Test
  void getNetworkTableTest() {
    NetworkTable table = NetworkTableUtilities.getNetworkTableInstance().getTable("a");

    assertEquals(table, new NetworkTableTreeRow(table).getNetworkTable());
  }

  @Test
  void networkTablePropertyTest() {
    NetworkTable table = NetworkTableUtilities.getNetworkTableInstance().getTable("a");

    assertEquals(table, new NetworkTableTreeRow(table).networkTableProperty().get());
  }
}
