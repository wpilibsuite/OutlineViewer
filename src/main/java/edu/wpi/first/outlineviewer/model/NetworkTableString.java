package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.SimpleStringProperty;

public class NetworkTableString extends NetworkTableData {

  private final SimpleStringProperty value;

  /**
   * Create a new NetworkTableString.
   *
   * @param key The key
   * @param value The value
   */
  public NetworkTableString(String key, String value) {
    super(key);

    this.value = new SimpleStringProperty(value);
  }

  @Override
  public SimpleStringProperty valueProperty() {
    return value;
  }

}
