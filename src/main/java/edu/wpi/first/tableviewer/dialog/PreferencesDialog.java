package edu.wpi.first.tableviewer.dialog;

import edu.wpi.first.tableviewer.PreferencesController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 *
 */
public class PreferencesDialog extends Dialog<ButtonType> {

  private static final ButtonType start = new ButtonType("Start");

  private final PreferencesController controller;

  public PreferencesDialog(String title, ButtonType... buttonTypes) throws IOException {
    setTitle(title);
    FXMLLoader loader = new FXMLLoader(PreferencesController.class.getResource("Preferences.fxml"));
    Pane prefsPane = loader.load();
    controller = loader.getController();
    getDialogPane().setContent(prefsPane);
    getDialogPane().getButtonTypes().addAll(buttonTypes);
    setResultConverter(x -> x);
  }

  public PreferencesController getController() {
    return controller;
  }

}
