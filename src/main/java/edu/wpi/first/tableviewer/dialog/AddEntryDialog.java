package edu.wpi.first.tableviewer.dialog;

import edu.wpi.first.tableviewer.entry.Entry;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 *
 */
public abstract class AddEntryDialog<T> extends Dialog<Entry<T>> {


  private static final ButtonType add = new ButtonType("Add", ButtonBar.ButtonData.APPLY);

  private final TextField keyField;
  private final BooleanProperty disableKey = new SimpleBooleanProperty(this, "disableKey", false);

  protected AddEntryDialog(String typeName) {
    super();
    setTitle("Add " + typeName);

    Label keyLabel = new Label("Key");
    keyField = new TextField();
    keyField.disableProperty().bind(disableKey);
    Platform.runLater(keyField::requestFocus);
    keyField.setPromptText("key");

    GridPane gridPane = new GridPane();
    gridPane.setAlignment(Pos.CENTER_LEFT);
    gridPane.setHgap(8);
    gridPane.setVgap(8);

    gridPane.add(keyLabel, 0, 0, 1, 1);
    gridPane.add(keyField, 1, 0, 1, 1);
    gridPane.add(new Label("Value"), 0, 1, 1, 1);
    gridPane.add(createCustomControl(), 1, 1, 1, 1);

    getDialogPane().setContent(gridPane);
    getDialogPane().getButtonTypes().addAll(add, ButtonType.CANCEL);

    // disable the "Add" button if the key is empty
    Button addButton = (Button) getDialogPane().lookupButton(add);
    addButton.disableProperty().bind(
        Bindings.createBooleanBinding(() -> keyField.getText().isEmpty() && !isDisableKey(), keyField.textProperty(), disableKey));

    addButton.setDefaultButton(true);

    setResultConverter(buttonType -> {
      if (buttonType == add) {
        return Entry.entryFor(keyField.getText(), getData());
      } else {
        return null;
      }
    });

    showingProperty().addListener(__ -> {
      if (isShowing()) {
        Dialogs.center(getDialogPane().getScene().getWindow());
      }
    });
  }

  protected abstract Node createCustomControl();

  protected abstract T getData();

  public void setKey(String key) {
    keyField.setText(key);
  }

  public boolean isDisableKey() {
    return disableKey.get();
  }

  public BooleanProperty disableKeyProperty() {
    return disableKey;
  }

  public void setDisableKey(boolean disableKey) {
    this.disableKey.set(disableKey);
  }

}
