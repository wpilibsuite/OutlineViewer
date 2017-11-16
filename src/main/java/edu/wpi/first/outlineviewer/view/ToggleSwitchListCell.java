package edu.wpi.first.outlineviewer.view;

import javafx.beans.binding.Bindings;
import org.controlsfx.control.ToggleSwitch;

/**
 * A list cell for displaying and editing boolean values that uses a toggle switch as a control.
 */
public class ToggleSwitchListCell extends DraggableCell<IndexedValue<Boolean>> {

  private final ToggleSwitch toggleSwitch = new ToggleSwitch();

  /**
   * Creates a new ToggleSwitch list cell.
   */
  public ToggleSwitchListCell() {
    getStyleClass().add("toggle-switch-list-cell");
    listViewProperty().addListener((observable, oldValue, newValue) -> {
      editableProperty().unbind();
      editableProperty().bind(newValue.editableProperty());
    });
    toggleSwitch.selectedProperty().addListener((__, wasSelected, isSelected) -> {
      getListView().edit(getIndex());
      commitEdit(new IndexedValue<>(getIndex(), isSelected));
    });
    toggleSwitch.setMaxWidth(1);
    textProperty().bind(Bindings.createStringBinding(this::createText, itemProperty()));
  }

  private String createText() {
    if (getItem() == null) {
      return "";
    }
    return toggleSwitch.isSelected() ? "True" : "False";
  }

  @Override
  protected void updateItem(IndexedValue<Boolean> item, boolean empty) {
    super.updateItem(item, empty);

    if (item == null || empty) {
      setGraphic(null);
    } else {
      toggleSwitch.setSelected(item.getValue());
      setGraphic(toggleSwitch);
    }
  }

}
