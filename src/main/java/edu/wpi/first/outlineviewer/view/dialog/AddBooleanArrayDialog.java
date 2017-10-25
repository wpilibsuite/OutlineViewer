package edu.wpi.first.outlineviewer.view.dialog;

import edu.wpi.first.outlineviewer.view.ToggleSwitchListCell;
import java.util.stream.Collectors;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * Dialog for adding boolean arrays to network tables.
 */
public class AddBooleanArrayDialog extends AddEntryArrayDialog<Pair<Integer, Boolean>, Boolean[]> {

  public AddBooleanArrayDialog() {
    super("Boolean Array");
  }

  @Override
  @SuppressWarnings("PMD.UseVarargs")
  public void setInitial(Boolean[] initialValues) {
    list.getItems().clear();
    int index = 0;
    for (Boolean value : initialValues) {
      list.getItems().add(new Pair<>(index++, value));
    }
  }

  @Override
  protected Pair<Integer, Boolean> getDefaultItem() {
    return new Pair<>(0, false);
  }

  @Override
  protected Callback<ListView<Pair<Integer, Boolean>>, ListCell<Pair<Integer, Boolean>>>
      getCellFactory() {
    return __ -> new ToggleSwitchListCell();
  }

  @Override
  protected Boolean[] getData() {
    //return Booleans.toArray(list.getItems());
    return list.getItems().stream()
        .map(Pair::getValue)
        .collect(Collectors.toList()).toArray(new Boolean[list.getItems().size()]);
  }

}
