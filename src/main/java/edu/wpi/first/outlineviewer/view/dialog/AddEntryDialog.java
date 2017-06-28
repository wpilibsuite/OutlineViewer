package edu.wpi.first.outlineviewer.view.dialog;

import edu.wpi.first.outlineviewer.model.Entry;
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
 * A type of dialog for adding or editing entries in network tables.
 */
public abstract class AddEntryDialog<T> extends Dialog<Entry<T>> {


  public static final ButtonType ADD = new ButtonType("Add", ButtonBar.ButtonData.APPLY);

  private final TextField keyField;
  private final BooleanProperty disableKey = new SimpleBooleanProperty(this, "disableKey", false);

  protected AddEntryDialog(String typeName) {
    super();
    getDialogPane().getStyleClass().add("add-model-dialog");
    setTitle("Add " + typeName);

    final Label keyLabel = new Label("Key");
    keyField = new TextField();
    keyField.setId("keyField");
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
    getDialogPane().getStylesheets().add("/edu/wpi/first/outlineviewer/styles.css");
    getDialogPane().getButtonTypes().addAll(ADD, ButtonType.CANCEL);

    // disable the "Add" button if the key is empty
    Button addButton = (Button) getDialogPane().lookupButton(ADD);
    addButton.disableProperty().bind(
        Bindings.createBooleanBinding(() -> keyField.getText().isEmpty() && !isDisableKey(),
                                      keyField.textProperty(), disableKey));

    addButton.setDefaultButton(true);

    setResultConverter(buttonType -> {
      if (buttonType == ADD) {
        return Entry.entryFor(keyField.getText(), getData());
      } else {
        return null;
      }
    });

    showingProperty().addListener(__ -> {
      if (isShowing()) {
        DialogUtils.center(getDialogPane().getScene().getWindow());
      }
    });
  }

  /**
   * Creates a control to use to edit the types of data in the entry. This could be as simple
   * as a text field (see {@link AddStringDialog}) or a fully custom pane
   * (see {@link AddStringArrayDialog}).
   *
   * @implNote all controls/nodes should be created here, and <i>not</i> in the constructor. This
   *           method is called before a subclass' constructor, which could lead to
   *           NullPointerExceptions if the controls are initialized in the constructor.
   */
  protected abstract Node createCustomControl();

  /**
   * Gets the current data.
   */
  protected abstract T getData();

  public void setKey(String key) {
    keyField.setText(key);
  }

  public final boolean isDisableKey() {
    return disableKey.get();
  }

  public BooleanProperty disableKeyProperty() {
    return disableKey;
  }

  public void setDisableKey(boolean disableKey) {
    this.disableKey.set(disableKey);
  }

}
