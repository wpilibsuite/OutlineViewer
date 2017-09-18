package edu.wpi.first.outlineviewer.model;

import edu.wpi.first.networktables.NetworkTable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Objects;

/**
 * Represents a subtable in network tables.
 */
public class TreeTableEntry extends TreeRow {

  private final ObjectProperty<NetworkTable> networkTable
      = new SimpleObjectProperty<>(this, "table", null);

  /**
   * Creates an entry.
   */
  public TreeTableEntry(NetworkTable table) {
    networkTable.addListener((__, oldValue, newValue) -> Objects.requireNonNull(newValue));

    networkTable.setValue(table);

    this.key.bind(Bindings.createStringBinding(() -> networkTable.getValue().getPath(),
        networkTable, key));
  }

  public final NetworkTable getNetworkTable() {
    return networkTable.get();
  }

  public final ObjectProperty<NetworkTable> networkTableProperty() {
    return networkTable;
  }

}
