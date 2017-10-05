package edu.wpi.first.outlineviewer.view.dialog;

import edu.wpi.first.outlineviewer.controller.PreferencesController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;
import java.util.Arrays;

/**
 * A dialog for changing the app preferences.
 */
public class PreferencesDialog extends Dialog<Boolean> {

  private final PreferencesController controller;

  /**
   * Creates a new preferences dialog with the given title and button types.
   *
   * @param buttonTypes the types of buttons for the dialog to use
   * @throws IOException if the preferences FXML could not be loaded
   */
  public PreferencesDialog(ButtonType... buttonTypes) throws IOException {
    setTitle("Preferences");

    FXMLLoader loader
        = new FXMLLoader(PreferencesDialog.class.getResource("PreferencesDialog.fxml"));
    getDialogPane().setContent(loader.load());
    getDialogPane().getButtonTypes().addAll(buttonTypes);
    setResultConverter(buttonType -> !buttonType.getButtonData().isCancelButton());
    controller = loader.getController();

    //Bind the disabled property of each button to the valid port property of the controller
    //so user can't save with an invalid port
    Arrays.stream(buttonTypes)
          .forEach(btn -> getDialogPane()
                  .lookupButton(btn)
                  .disableProperty().bind(controller.validPortProperty()));
  }

  public PreferencesController getController() {
    return controller;
  }
}
