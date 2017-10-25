package edu.wpi.first.outlineviewer.view.dialog;

import edu.wpi.first.outlineviewer.view.ToggleSwitchListCell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Dialog for adding boolean arrays to network tables.
 */
public class AddBooleanArrayDialog extends AddEntryArrayDialog<Boolean, Boolean[]> {

  public AddBooleanArrayDialog() {
    super("Boolean Array");
  }

  @Override
  @SuppressWarnings("PMD.UseVarargs")
  public void setInitial(Boolean[] initialValues) {
    list.getItems().clear();
    for (Boolean value : initialValues) {
      list.getItems().add(value);
    }
  }

  @Override
  protected Boolean getDefaultItem() {
    return false;
  }

  @Override
  protected Callback<ListView<Boolean>, ListCell<Boolean>> getCellFactory() {
    return param -> new ToggleSwitchListCell();
//    return __ -> new ToggleSwitchListCell();
  }

  @Override
  protected Boolean[] getData() {
//    return Booleans.toArray(list.getItems());
    return list.getItems().toArray(new Boolean[list.getItems().size()]);
  }

}
