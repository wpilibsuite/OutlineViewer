package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.SimpleDoubleProperty;

import static com.google.common.base.Preconditions.checkNotNull;

public class NetworkTableNumber extends NetworkTableData {

  private final SimpleDoubleProperty value;

  /**
   * Create a new NetworkTableNumber.
   *
   * @param key The key
   * @param value The value
   */
  public NetworkTableNumber(String key, double value) {
    super(key);
    checkNotNull(value);

    this.value = new SimpleDoubleProperty(value);
  }

  @Override
  public SimpleDoubleProperty valueProperty() {
    return value;
  }

}
