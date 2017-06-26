package edu.wpi.first.outlineviewer.model;

import edu.wpi.first.outlineviewer.NetworkTableUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents an model in network tables. This has a key, which never changes, and a value which can
 * change.
 */
public abstract class Entry<T> {

  private static final Predicate<String> isMetadata = s -> s.matches(".*~[A-Z]*~");

  private final StringProperty key = new SimpleStringProperty(this, "key", "");
  private final ObjectProperty<T> value = new SimpleObjectProperty<>(this, "value", null);
  private final StringProperty type = new SimpleStringProperty(this, "type", "");

  /**
   * Creates an model for the given key-value pair.
   *
   * @param key   the absolute key of the model
   * @param value the initial value of the model
   * @param <T>   the type of values in the model
   * @return an model for the given key-value pair
   * @throws IllegalArgumentException if the value is an unsupported type
   */
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
    if (value instanceof byte[]) {
      return (Entry<T>) new RawBytesEntry(key, (byte[]) value);
    }
    if (value instanceof boolean[]) {
      return (Entry<T>) new BooleanArrayEntry(key, (boolean[]) value);
    }
    if (value instanceof double[]) {
      return (Entry<T>) new NumberArrayEntry(key, (double[]) value);
    }
    if (value instanceof String[]) {
      return (Entry<T>) new StringArrayEntry(key, (String[]) value);
    }
    throw new IllegalArgumentException("Unsupported type: " + value.getClass().getSimpleName());
  }

  /**
   * Creates an model with the given key and no value.
   *
   * @param key the key of the model
   */
  protected Entry(String key) {
    Objects.requireNonNull(key, "key");
    this.key.setValue(NetworkTableUtils.normalize(key));

    this.value.addListener((__, oldValue, newValue) -> Objects.requireNonNull(newValue, "value"));
    this.type.addListener((__, oldType, newType) -> Objects.requireNonNull(newType, "type"));
    type.bind(Bindings.createStringBinding(() -> {
      if (getValue() == null) {
        return "";
      } else {
        return getTypeString(getValue());
      }
    }, value));
  }

  /**
   * Creates an model with the given key and value.
   *
   * @param key   the key of the model
   * @param value the value of the model
   */
  protected Entry(String key, T value) {
    this(key);
    Objects.requireNonNull(value, "value");
    setValue(value);
  }

  /**
   * Checks if this model is metadata.
   */
  public boolean isMetadata() {
    return isMetadata.test(getKey());
  }

  /**
   * Gets a string representing the type of value in this model based on the current value. For
   * example, array-based entries may want to show the number of elements in the value array
   * eg "Number[10]".
   *
   * @param value the current value of this model
   * @return a string representing the type of the current value in this model.
   */
  // TODO: Does this need a value argument?
  protected abstract String getTypeString(T value);

  /**
   * Gets a string representing the value in this model.
   */
  public String getDisplayString() {
    return getValue().toString();
  }

  /**
   * Gets the key for this model.
   */
  public String getKey() {
    return key.get();
  }

  /**
   * Gets the value of this model.
   */
  public final T getValue() {
    return value.get();
  }

  /**
   * Sets the value of this model. This may not be null.
   *
   * @param value the new value for this model
   */
  public final void setValue(T value) {
    this.value.set(value);
  }

  /**
   * Gets a string representing the type of data in this model.
   */
  public String getType() {
    return type.get();
  }

  @Override
  public String toString() {
    return String.format("%s(key=%s, value=%s, type=%s)",
                         getClass().getSimpleName(),
                         getKey(), getDisplayString(), getType());
  }

}
