package edu.wpi.first.tableviewer.dialog;

import edu.wpi.first.tableviewer.dialog.cell.ToggleSwitchListCell;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Dialog for adding boolean arrays to network tables.
 */
public class AddBooleanArrayDialog extends AddEntryDialog<boolean[]> {

  private ListView<Boolean> list;

  public AddBooleanArrayDialog() {
    super("Boolean Array");
    getDialogPane().setMaxHeight(300);
  }

  @Override
  protected Node createCustomControl() {
    list = new ListView<>();
    list.setEditable(true);
    list.setCellFactory(__ -> new ToggleSwitchListCell());
    list.setOnKeyPressed(e -> {
      KeyCode code = e.getCode();
      if (code == KeyCode.DELETE) {
        removeSelected();
      }
    });

    Button add = new Button("+");
    add.setPrefWidth(40);
    add.setOnAction(__ -> list.getItems().add(false));

    return new VBox(8, list, add);
  }

  private void removeSelected() {
    list.getItems().removeAll(list.getSelectionModel().getSelectedItems());
  }

  @Override
  protected boolean[] getData() {
    return toPrimitiveArray(list.getItems());
  }

  private static boolean[] toPrimitiveArray(List<Boolean> list) {
    boolean[] arr = new boolean[list.size()];
    for (int i = 0; i < list.size(); i++) {
      arr[i] = list.get(i);
    }
    return arr;
  }

}
