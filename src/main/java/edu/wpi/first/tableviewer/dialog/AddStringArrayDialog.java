package edu.wpi.first.tableviewer.dialog;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Dialog for adding string arrays to network tables.
 */
public class AddStringArrayDialog extends AddEntryDialog<String[]> {

  private ListView<String> list;

  public AddStringArrayDialog() {
    super("String Array");
    getDialogPane().setMaxHeight(300);
  }

  @Override
  protected Node createCustomControl() {
    list = new ListView<>();
    list.setEditable(true);
    list.setCellFactory(__ -> new TextFieldListCell<>(StringToStringConverter.INSTANCE));
    list.setOnKeyPressed(e -> {
      KeyCode code = e.getCode();
      if (code == KeyCode.DELETE) {
        removeSelected();
      }
    });

    Button add = new Button("+");
    add.setPrefWidth(40);
    add.setOnAction(__ -> list.getItems().add("change me!"));

    return new VBox(8, list, add);
  }

  private void removeSelected() {
    list.getItems().removeAll(list.getSelectionModel().getSelectedItems());
  }

  @Override
  protected String[] getData() {
    List<String> items = list.getItems();
    return items.toArray(new String[items.size()]);
  }

  private static final class StringToStringConverter extends StringConverter<String> {

    public static final StringConverter<String> INSTANCE = new StringToStringConverter();

    @Override
    public String toString(String string) {
      return string;
    }

    @Override
    public String fromString(String string) {
      return string;
    }

  }

}
