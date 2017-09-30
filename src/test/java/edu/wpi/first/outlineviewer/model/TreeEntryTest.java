package edu.wpi.first.outlineviewer.model;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.outlineviewer.NetworkTableUtilities;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class TreeEntryTest {

  @Test
  void getNetworkTableEntryTest() {
    NetworkTableEntry entry = NetworkTableUtilities.getNetworkTableInstance().getEntry("/TestKey");

    assertEquals(entry, new TreeEntry(entry).getNetworkTableEntry());
  }

  @Test
  void networkTableEntryPropertyTest() {
    NetworkTableEntry entry = NetworkTableUtilities.getNetworkTableInstance().getEntry("/TestKey");

    assertEquals(entry, new TreeEntry(entry).networkTableEntryProperty().get());
  }
}
