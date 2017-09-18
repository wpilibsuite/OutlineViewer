package edu.wpi.first.outlineviewer.model;

import com.google.common.base.MoreObjects;
import edu.wpi.first.outlineviewer.NetworkTableUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents a row in the NetworkTableTree.
 */
public class TreeRow {

  private static final Predicate<String> IS_METADATA = s -> s.matches(".*~[A-Z]*~");

  protected final StringProperty key = new SimpleStringProperty(this, "key", "");
  protected final ObjectProperty<Object> value = new SimpleObjectProperty<>(this, "value", null);
  protected final StringProperty type = new SimpleStringProperty(this, "type", "");

  protected TreeRow() {
    // Users must provide a key!
  }

  /**
   * Creates an entry.
   */
  public TreeRow(String key) {
    Objects.requireNonNull(key);

    this.key.setValue(key);
  }

  /**
   * Creates an entry.
   */
  public TreeRow(String key, Object value) {
    this(key);
    Objects.requireNonNull(value);

    this.value.setValue(value);
  }

  public String getKey() {
    return key.get();
  }

  public StringProperty keyProperty() {
    return key;
  }

  public Object getValue() {
    return value.get();
  }

  public ObjectProperty valueProperty() {
    return value;
  }

  public String getType() {
    return type.get();
  }

  public StringProperty typeProperty() {
    return type;
  }

  /**
   * Checks if this entry is metadata.
   */
  public boolean isMetadata() {
    return IS_METADATA.test(NetworkTableUtils.simpleKey(key.get()));
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("Key", key.getValue())
        .add("Value", value.get())
        .add("Type", typeProperty().getValue())
        .toString();
  }
}
