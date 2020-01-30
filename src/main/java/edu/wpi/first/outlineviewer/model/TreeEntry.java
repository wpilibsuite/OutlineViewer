package edu.wpi.first.outlineviewer.model;

import edu.wpi.first.networktables.NetworkTableEntry;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Objects;

/**
 * Represents an model in network tables.
 */
public class TreeEntry extends TreeRow {

  private final ObjectProperty<NetworkTableEntry> networkTableEntry
      = new SimpleObjectProperty<>(this, "entry", null);

  /**
   * Creates an entry.
   */
  public TreeEntry(NetworkTableEntry entry) {
    networkTableEntry.addListener((__, oldValue, newValue) -> Objects.requireNonNull(newValue));

    networkTableEntry.setValue(entry);

    this.key.bind(Bindings.createStringBinding(
        () -> networkTableEntry.get().getName(), networkTableEntry, key));
    this.value.bind(Bindings.createObjectBinding(
        () -> networkTableEntry.get().getValue().getValue(), networkTableEntry));
    this.type.bind(Bindings.createObjectBinding(
        () -> networkTableEntry.get().getValue().getType().toString(), networkTableEntry));
    this.lastUpdated.bind(Bindings.createLongBinding(
        () -> networkTableEntry.get().getValue().getTime(), networkTableEntry));
  }

  public final NetworkTableEntry getNetworkTableEntry() {
    return networkTableEntry.get();
  }

  public final ObjectProperty<NetworkTableEntry> networkTableEntryProperty() {
    return networkTableEntry;
  }

}
