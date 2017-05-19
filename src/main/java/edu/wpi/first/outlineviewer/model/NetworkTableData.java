package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NetworkTableData {

  private final SimpleStringProperty key;
  private final ObservableList<NetworkTableData> children;

  public NetworkTableData(String path) {
    this.key = new SimpleStringProperty(path);
    children = FXCollections.observableArrayList();
  }

  public Property valueProperty() {
    return new SimpleStringProperty("");
  }

  public SimpleStringProperty keyProperty() {
    return key;
  }

  public ObservableList<NetworkTableData> getChildren() {
    return children;
  }
}
