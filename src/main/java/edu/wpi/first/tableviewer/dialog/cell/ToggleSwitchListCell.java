package edu.wpi.first.tableviewer.dialog.cell;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import org.controlsfx.control.ToggleSwitch;

/**
 * A list cell for displaying and editing boolean values.
 */
public class ToggleSwitchListCell extends ListCell<Boolean> {

  private final ToggleSwitch toggleSwitch = new ToggleSwitch();

  public ToggleSwitchListCell() {
    toggleSwitch.selectedProperty().addListener((__, wasSelected, isSelected) -> {
      if (!isEditing()) {
        getListView().edit(getIndex());
      }
      commitEdit(isSelected);
    });
    toggleSwitch.textProperty().bind(
        Bindings.createStringBinding(() -> String.valueOf(toggleSwitch.isSelected()),
                                     toggleSwitch.selectedProperty()));
  }

  @Override
  protected void updateItem(Boolean item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setText(null);
      setGraphic(null);
      return;
    }
    toggleSwitch.setSelected(item);
    setGraphic(toggleSwitch);
  }

}
