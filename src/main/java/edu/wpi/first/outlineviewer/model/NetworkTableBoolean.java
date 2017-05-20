package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.SimpleBooleanProperty;

import static com.google.common.base.Preconditions.checkNotNull;

public class NetworkTableBoolean extends NetworkTableData {

  private final SimpleBooleanProperty value;

  /**
   * Create a new NetworkTableBoolean.
   *
   * @param key The key
   * @param value The value
   */
  public NetworkTableBoolean(String key, boolean value) {
    super(key, "Boolean");
    checkNotNull(value);

    this.value = new SimpleBooleanProperty(value);
  }

  @Override
  public SimpleBooleanProperty valueProperty() {
    return value;
  }

}
