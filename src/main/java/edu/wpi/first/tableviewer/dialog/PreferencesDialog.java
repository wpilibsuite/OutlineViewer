package edu.wpi.first.tableviewer.dialog;

import edu.wpi.first.tableviewer.PreferencesController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * A dialog for changing the app preferences.
 */
public class PreferencesDialog extends Dialog<ButtonType> {

  private final PreferencesController controller;

  /**
   * Creates a new preferences dialog with the given title and button types.
   *
   * @param title       the title of the dialog
   * @param buttonTypes the types of buttons for the dialog to use
   * @throws IOException if the preferences FXML could not be loaded
   */
  public PreferencesDialog(String title, ButtonType... buttonTypes) throws IOException {
    setTitle(title);
    FXMLLoader loader = new FXMLLoader(
        PreferencesController.class.getResource("Preferences.fxml"));
    Pane prefsPane = loader.load();
    controller = loader.getController();
    getDialogPane().setContent(prefsPane);
    getDialogPane().getButtonTypes().addAll(buttonTypes);
    setResultConverter(x -> x);
  }

  /**
   * Gets the controller for the pane.
   */
  public PreferencesController getController() {
    return controller;
  }

}
