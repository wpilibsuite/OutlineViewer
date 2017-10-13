package edu.wpi.first.outlineviewer.view.dialog;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * Dialog for adding string arrays to network tables.
 */
public abstract class AddEntryArrayDialog<T, A> extends AddEntryDialog<A> {

  protected ListView<T> list;

  AddEntryArrayDialog(String typeName) {
    super(typeName);
  }

  @Override
  protected Node createCustomControl() {
    list = new ListView<>();
    list.setEditable(true);
    list.setCellFactory(getCellFactory());
    list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    Button addButton = new Button("+");
    addButton.setPrefWidth(40);
    addButton.setOnAction(event -> list.getItems().add(getDefaultItem()));

    return new VBox(8, list, addButton);
  }

  /**
   * Sets the initial values in the array.
   */
  public abstract void setInitial(A initialValues);

  /**
   * Get the initial value of new rows added to the array.
   */
  protected abstract T getDefaultItem();

  protected abstract Callback<ListView<T>, ListCell<T>> getCellFactory();

}
