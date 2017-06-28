package edu.wpi.first.outlineviewer.view.dialog;

import com.google.common.primitives.Booleans;
import edu.wpi.first.outlineviewer.view.ToggleSwitchListCell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Dialog for adding boolean arrays to network tables.
 */
public class AddBooleanArrayDialog extends AddEntryArrayDialog<Boolean, boolean[]> {

  public AddBooleanArrayDialog() {
    super("Boolean Array");
  }

  @Override
  @SuppressWarnings("PMD.UseVarargs")
  public void setInitial(boolean[] initialValues) {
    list.getItems().clear();
    for (boolean value : initialValues) {
      list.getItems().add(value);
    }
  }

  @Override
  protected Boolean getDefaultItem() {
    return false;
  }

  @Override
  protected Callback<ListView<Boolean>, ListCell<Boolean>> getCellFactory() {
    return __ -> new ToggleSwitchListCell();
  }

  @Override
  protected boolean[] getData() {
    return Booleans.toArray(list.getItems());
  }

}
