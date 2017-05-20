package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static com.google.common.base.Preconditions.checkNotNull;

public class NetworkTableData {

  private final SimpleStringProperty key;
  private final ObservableList<NetworkTableData> children;

  /**
   * Create a new NetworkTableData.
   *
   * @param key The key
   */
  public NetworkTableData(String key) {
    checkNotNull(key);

    this.key = new SimpleStringProperty(key);
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
