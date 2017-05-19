package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.SimpleBooleanProperty;

public class NetworkTableBoolean extends NetworkTableData {

  private final SimpleBooleanProperty value;

  /**
   * Create a new NetworkTableBoolean.
   *
   * @param key The key
   * @param value The value
   */
  public NetworkTableBoolean(String key, boolean value) {
    super(key);

    this.value = new SimpleBooleanProperty(value);
  }

  @Override
  public SimpleBooleanProperty valueProperty() {
    return value;
  }

}
