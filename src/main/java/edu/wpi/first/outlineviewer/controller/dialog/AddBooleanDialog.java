package edu.wpi.first.outlineviewer.controller.dialog;

import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.ToggleSwitch;

/**
 * A dialog for adding or editing boolean values in network tables.
 */
public class AddBooleanDialog extends AddEntryDialog<Boolean> {

  private ToggleSwitch toggleSwitch;

  public AddBooleanDialog() {
    super("Boolean");
    getDialogPane().getStyleClass().add("add-boolean-dialog");
  }

  @Override
  protected Node createCustomControl() {
    toggleSwitch = new ToggleSwitch();
    toggleSwitch.textProperty().bind(
        Bindings.createStringBinding(() -> String.valueOf(toggleSwitch.isSelected()),
                                     toggleSwitch.selectedProperty()));
    toggleSwitch.setId("toggleSwitch");
    toggleSwitch.setMinWidth(0);
    toggleSwitch.setMaxWidth(70);
    GridPane.setHalignment(toggleSwitch, HPos.RIGHT);
    return toggleSwitch;
  }

  @Override
  protected Boolean getData() {
    return toggleSwitch.isSelected();
  }

}
