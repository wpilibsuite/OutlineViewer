package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public interface NetworkTableData {

  StringProperty pathProperty();

  default Property valueProperty() {
    return new SimpleStringProperty("Default Value");
  }

}
