package edu.wpi.first.outlineviewer.view.dialog;

import com.google.common.primitives.Booleans;
import edu.wpi.first.outlineviewer.view.IndexedValue;
import edu.wpi.first.outlineviewer.view.ToggleSwitchListCell;
import java.util.stream.Collectors;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Dialog for adding boolean arrays to network tables.
 */
public class AddBooleanArrayDialog extends AddEntryArrayDialog<IndexedValue<Boolean>, boolean[]> {

  public AddBooleanArrayDialog() {
    super("Boolean Array");
  }

  @Override
  @SuppressWarnings({"PMD.UseVarargs", "PMD.AvoidInstantiatingObjectsInLoops"})
  public void setInitial(boolean[] initialValues) {
    list.getItems().clear();
    int index = 0;
    for (Boolean value : initialValues) {
      list.getItems().add(new IndexedValue<>(index++, value));
    }
  }

  @Override
  protected IndexedValue<Boolean> getDefaultItem() {
    return new IndexedValue<>(list.getItems().size() + 1, false);
  }

  @Override
  protected Callback<ListView<IndexedValue<Boolean>>, ListCell<IndexedValue<Boolean>>>
      getCellFactory() {
    return __ -> new ToggleSwitchListCell();
  }

  @Override
  protected boolean[] getData() {
    return Booleans.toArray(list.getItems()
        .stream()
        .map(IndexedValue::getValue)
        .collect(Collectors.toList()));
  }

}
