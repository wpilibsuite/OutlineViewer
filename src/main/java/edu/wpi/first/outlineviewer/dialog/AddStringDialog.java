package edu.wpi.first.outlineviewer.dialog;

import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * A dialog for editing or adding string entries in network tables.
 */
public class AddStringDialog extends AddEntryDialog<String> {

  private TextField valueField;

  public AddStringDialog() {
    super("String");
    getDialogPane().getStyleClass().add("add-string-dialog");
    valueField.setPromptText("Value");
  }

  @Override
  protected Node createCustomControl() {
    valueField = new TextField("");
    valueField.setId("valueField");
    return valueField;
  }

  @Override
  protected String getData() {
    return valueField.getText();
  }

}
