package edu.wpi.first.outlineviewer.dialog.cell;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import org.controlsfx.control.ToggleSwitch;

/**
 * A list cell for displaying and editing boolean values that uses a toggle switch as a control.
 */
public class ToggleSwitchListCell extends ListCell<Boolean> {

  private final ToggleSwitch toggleSwitch = new ToggleSwitch();

  /**
   * Creates a new ToggleSwitch list cell.
   */
  public ToggleSwitchListCell() {
    getStyleClass().add("toggle-switch-list-cell");
    toggleSwitch.selectedProperty().addListener((__, wasSelected, isSelected) -> {
      if (!isEditing()) {
        getListView().edit(getIndex());
      }
      commitEdit(isSelected);
    });
    toggleSwitch.setMaxWidth(1);
    textProperty().bind(
        Bindings.createStringBinding(
            this::createText,
            itemProperty()));
  }

  private String createText() {
    if (getItem() == null) {
      return "";
    }
    return toggleSwitch.isSelected() ? "True" : "False";
  }

  @Override
  protected void updateItem(Boolean item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setGraphic(null);
      return;
    }
    toggleSwitch.setSelected(item);
    setGraphic(toggleSwitch);
  }

}
