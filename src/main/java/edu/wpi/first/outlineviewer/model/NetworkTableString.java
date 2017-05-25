package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.SimpleStringProperty;

import static com.google.common.base.Preconditions.checkNotNull;

public class NetworkTableString extends NetworkTableData {

  private final SimpleStringProperty value;

  /**
   * Create a new NetworkTableString.
   *
   * @param key The key
   * @param value The value
   */
  public NetworkTableString(String key, String value) {
    super(key, "String");
    checkNotNull(value);

    this.value = new SimpleStringProperty(value);
  }

  @Override
  public SimpleStringProperty valueProperty() {
    return value;
  }

}
