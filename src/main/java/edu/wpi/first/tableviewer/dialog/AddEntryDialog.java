package edu.wpi.first.tableviewer.dialog;

import com.sun.javafx.stage.StageHelper;
import edu.wpi.first.tableviewer.NetworkTableUtils;
import edu.wpi.first.tableviewer.TableEntryData;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

/**
 *
 */
public abstract class AddEntryDialog<T> extends Dialog<TableEntryData> {

  private static final ButtonType add = new ButtonType("Add", ButtonBar.ButtonData.APPLY);

  protected AddEntryDialog(String typeName) {
    super();
    setTitle("Add " + typeName);

    Label keyLabel = new Label("Key");
    TextField keyField = new TextField();
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
        Bindings.createBooleanBinding(() -> keyField.getText().isEmpty(), keyField.textProperty()));

    addButton.setDefaultButton(true);

    setResultConverter(buttonType -> {
      if (buttonType == add) {
        return new TableEntryData(NetworkTableUtils.normalize(keyField.getText()), getData());
      } else {
        return null;
      }
    });

    showingProperty().addListener(__ -> {
      if (isShowing()) {
        center(getDialogPane().getScene().getWindow());
      }
    });
  }

  protected abstract Node createCustomControl();

  protected abstract T getData();

  /**
   * Centers a window relative to the primary stage.
   *
   * @param window the window to center
   */
  public static void center(Window window) {
    Window stage = StageHelper.getStages().get(0);
    double x = stage.getX() + stage.getWidth() / 2;
    double y = stage.getY() + stage.getHeight() / 2;
    window.setX(x - window.getWidth() / 2);
    window.setY(y - window.getHeight() / 2);
  }

}
