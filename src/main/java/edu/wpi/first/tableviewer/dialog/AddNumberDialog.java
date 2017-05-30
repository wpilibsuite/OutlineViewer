package edu.wpi.first.tableviewer.dialog;

import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * A dialog for adding or editing number entries in network tables.
 */
public class AddNumberDialog extends AddEntryDialog<Number> {

  private TextField numberField;

  public AddNumberDialog() {
    super("Number");
  }

  @Override
  protected Node createCustomControl() {
    numberField = new TextField("0");
    numberField.setId("numberField");
    return numberField;
  }

  @Override
  protected Number getData() {
    try {
      return Double.parseDouble(numberField.getText());
    } catch (NumberFormatException e) {
      return null;
    }
  }

}
