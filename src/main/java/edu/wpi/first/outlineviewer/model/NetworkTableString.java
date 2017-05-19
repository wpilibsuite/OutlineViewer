package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.SimpleStringProperty;

public class NetworkTableString extends NetworkTableData {

  private final SimpleStringProperty value;

  public NetworkTableString(String path, String value) {
    super(path);

    this.value = new SimpleStringProperty(value);
  }

  @Override
  public SimpleStringProperty valueProperty() {
    return value;
  }

}
