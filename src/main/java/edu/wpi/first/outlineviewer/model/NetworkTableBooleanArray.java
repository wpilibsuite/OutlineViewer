package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

public class NetworkTableBooleanArray extends NetworkTableData {

  private final SimpleObjectProperty<boolean[]> value;

  /**
   * Create a new NetworkTableString.
   *
   * @param key The key
   * @param value The value
   */
  public NetworkTableBooleanArray(String key, boolean[] value) {
    super(key, "Boolean[]");
    checkNotNull(value);

    this.value = new SimpleObjectProperty<>(value);
  }

  @Override
  public SimpleStringProperty valueProperty() {
    return new ReadOnlyStringWrapper(Arrays.toString(value.get()));
  }

}
