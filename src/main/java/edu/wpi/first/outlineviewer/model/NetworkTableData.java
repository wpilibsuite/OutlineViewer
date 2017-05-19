package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NetworkTableData {

  private final SimpleStringProperty path;
  private final ObservableList<NetworkTableData> children;

  public NetworkTableData(String path) {
    this.path = new SimpleStringProperty(path);
    children = FXCollections.observableArrayList();
  }

  public Property valueProperty() {
    return new SimpleStringProperty("");
  }

  public SimpleStringProperty pathProperty() {
    return path;
  }

  public ObservableList<NetworkTableData> getChildren() {
    return children;
  }
}
