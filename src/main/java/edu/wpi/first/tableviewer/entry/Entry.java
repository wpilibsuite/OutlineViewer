package edu.wpi.first.tableviewer.entry;

import edu.wpi.first.tableviewer.NetworkTableUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents an entry in network tables. This has a key, which never changes, and a value which can
 * change.
 */
public abstract class Entry<T> {

  private static final Predicate<String> isMetadata = s -> s.matches("^.*/(\\..*|~.*~).*$");

  private final StringProperty key = new SimpleStringProperty(this, "key", "");
  private ObjectProperty<T> value = new SimpleObjectProperty<>(this, "value", null);
  private final StringProperty type = new SimpleStringProperty(this, "type", "");

  @SuppressWarnings("unchecked")
  public static <T> Entry<T> entryFor(String key, T value) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);
    if (value instanceof Boolean) {
      return (Entry<T>) new BooleanEntry(key, (Boolean) value);
    }
    if (value instanceof Number) {
      return (Entry<T>) new NumberEntry(key, (Number) value);
    }
    if (value instanceof String) {
      return (Entry<T>) new StringEntry(key, (String) value);
    }
    throw new IllegalArgumentException("Unsupported type: " + value);
  }

  protected Entry(String key) {
    Objects.requireNonNull(key, "key");
    this.key.setValue(NetworkTableUtils.normalize(key));

    this.value.addListener((__, oldValue, newValue) -> Objects.requireNonNull(newValue, "value"));
    this.type.addListener((__, oldType, newType) -> Objects.requireNonNull(newType, "type"));
    type.bind(Bindings.createStringBinding(() -> getTypeString(getValue()), value));
  }

  protected Entry(String key, T value) {
    this(key);
    Objects.requireNonNull(value, "value");
    setValue(value);
  }

  /**
   * Checks if this entry is metadata.
   */
  public boolean isMetadata() {
    return isMetadata.test(getKey());
  }

  /**
   * Gets a string representing the type of value in this entry based on the current value. For
   * example, array-based entries may want to show the number of elements in the value array
   * eg "Number[10]".
   *
   * @param value the current value of this entry
   * @return a string representing the type of the current value in this entry.
   */
  protected abstract String getTypeString(T value);

  /**
   * Gets a string representing the value in this entry.
   */
  public String getDisplayString() {
    return getValue().toString();
  }

  /**
   * Gets the key for this entry.
   */
  public String getKey() {
    return key.get();
  }

  public ReadOnlyStringProperty keyProperty() {
    return key;
  }

  /**
   * Gets the value of this entry.
   */
  public T getValue() {
    return value.get();
  }

  public ObjectProperty<T> valueProperty() {
    return value;
  }

  /**
   * Sets the value of this entry. This may not be null.
   *
   * @param value the new value for this entry
   */
  public void setValue(T value) {
    this.value.set(value);
  }

  /**
   * Gets a string representing the type of data in this entry.
   */
  public String getType() {
    return type.get();
  }

  public ReadOnlyStringProperty typeProperty() {
    return type;
  }

}
