package edu.wpi.first.tableviewer.dialog;

import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 *
 */
public class AddStringDialog extends AddEntryDialog<String> {

  private TextField valueField;

  public AddStringDialog() {
    super("String");
    valueField.setPromptText("Value");
  }

  @Override
  protected Node createCustomControl() {
    valueField = new TextField("");
    return valueField;
  }

  @Override
  protected String getData() {
    return valueField.getText();
  }

}