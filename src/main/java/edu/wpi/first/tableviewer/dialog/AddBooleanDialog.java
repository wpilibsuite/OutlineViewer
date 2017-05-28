package edu.wpi.first.tableviewer.dialog;

import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

/**
 *
 */
public class AddBooleanDialog extends AddEntryDialog<Boolean> {

  private ToggleButton trueButton;

  public AddBooleanDialog() {
    super("Boolean");
  }

  @Override
  protected Node createCustomControl() {
    trueButton = new ToggleButton("True");
    ToggleButton f = new ToggleButton("False");
    trueButton.selectedProperty().addListener(__ -> f.setSelected(!trueButton.isSelected()));
    f.selectedProperty().addListener(__ -> trueButton.setSelected(!f.isSelected()));
    trueButton.setSelected(true);
    return new HBox(2, trueButton, f);
  }

  @Override
  protected Boolean getData() {
    return trueButton.isSelected();
  }

}
