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
public class PreferencesDialog extends Dialog<PreferencesDialog.PrefsResult> {

  public enum PrefsResult {
    CLIENT, SERVER, NONE
  }

  private static final ButtonType server = new ButtonType("Start Server");
  private static final ButtonType client = new ButtonType("Start Client");

  public PreferencesDialog() throws IOException {
    FXMLLoader loader = new FXMLLoader(PreferencesController.class.getResource("Preferences.fxml"));
    Pane prefsPane = loader.load();
    PreferencesController controller = loader.getController();
    getDialogPane().setContent(prefsPane);
    getDialogPane().getButtonTypes().addAll(client, server, ButtonType.CANCEL);
    setResultConverter(x -> {
      if (x == server) {
        controller.startServer();
        return PrefsResult.SERVER;
      } else if (x == client) {
        controller.startClient();
        return PrefsResult.CLIENT;
      } else {
        controller.cancel();
        return null;
      }
    });
  }

}
