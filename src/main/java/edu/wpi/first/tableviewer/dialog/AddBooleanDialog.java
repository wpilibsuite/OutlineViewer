package edu.wpi.first.tableviewer.dialog;

import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

/**
 * A dialog for adding or editing boolean values in network tables.
 */
public class AddBooleanDialog extends AddEntryDialog<Boolean> {

  private ToggleButton trueButton;

  public AddBooleanDialog() {
    super("Boolean");
  }

  @Override
  protected Node createCustomControl() {
    trueButton = new ToggleButton("True");
    ToggleButton falseButton = new ToggleButton("False");
    trueButton.selectedProperty()
              .addListener(__ -> falseButton.setSelected(!trueButton.isSelected()));
    falseButton.selectedProperty()
               .addListener(__ -> trueButton.setSelected(!falseButton.isSelected()));
    trueButton.setSelected(true);
    return new HBox(2, trueButton, falseButton);
  }

  @Override
  protected Boolean getData() {
    return trueButton.isSelected();
  }

}
