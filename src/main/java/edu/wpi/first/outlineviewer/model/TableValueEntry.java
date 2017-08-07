package edu.wpi.first.outlineviewer.model;

import edu.wpi.first.outlineviewer.NetworkTableUtils;
import edu.wpi.first.wpilibj.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.networktables.NetworkTableValue;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Objects;

/**
 * Represents an model in network tables.
 */
public class TableValueEntry extends TableEntry {

  private final ObjectProperty<NetworkTableEntry> networkTableEntry
      = new SimpleObjectProperty<>(this, "entry", null);
  private final ObjectProperty<NetworkTableValue> networkTableValue
      = new SimpleObjectProperty<>(this, "value", null);

  /**
   * Creates an entry.
   */
  public TableValueEntry(NetworkTableEntry entry, NetworkTableValue value) {
    networkTableEntry.addListener((__, oldValue, newValue) -> Objects.requireNonNull(newValue));
    networkTableValue.addListener((__, oldValue, newValue) -> Objects.requireNonNull(newValue));

    networkTableEntry.setValue(entry);
    networkTableValue.setValue(value);

    this.key.bind(Bindings.createStringBinding(
        () -> networkTableEntry.get().getName(), networkTableEntry));
    this.value.bind(Bindings.createObjectBinding(
        () -> networkTableValue.get().getValue(), networkTableValue));
    this.type.bind(Bindings.createObjectBinding(
        () -> networkTableValue.get().getType().toString(), networkTableValue));
  }

  public final NetworkTableEntry getNetworkTableEntry() {
    return networkTableEntry.get();
  }

  public final ObjectProperty<NetworkTableEntry> networkTableEntryProperty() {
    return networkTableEntry;
  }

  public final NetworkTableValue getNetworkTableValue() {
    return networkTableValue.get();
  }

  public final ObjectProperty<NetworkTableValue> networkTableValueProperty() {
    return networkTableValue;
  }

}
