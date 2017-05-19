package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.SimpleStringProperty;

public class NetworkTableParent implements NetworkTableData {

  private final SimpleStringProperty path;

  public NetworkTableParent(String path) {
    this.path = new SimpleStringProperty(path);
  }

  @Override
  public SimpleStringProperty pathProperty() {
    return path;
  }

}
