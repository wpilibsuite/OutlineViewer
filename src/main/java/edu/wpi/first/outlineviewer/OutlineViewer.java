package edu.wpi.first.outlineviewer;

import edu.wpi.first.outlineviewer.view.dialog.PreferencesDialog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class OutlineViewer extends Application {

  private static final ButtonType START = new ButtonType("Start", ButtonData.OK_DONE);
  private static final ButtonType QUIT = new ButtonType("Quit", ButtonData.CANCEL_CLOSE);

  @Override
  public void start(Stage primaryStage) throws IOException {
    AutoUpdater updater = new AutoUpdater();

    PreferencesDialog preferencesDialog = new PreferencesDialog(QUIT, START);
    if (preferencesDialog.showAndWait().orElse(false)) {
      preferencesDialog.getController().save();
      updater.init();

      FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
      Pane mainWindow = loader.load();

      primaryStage.setTitle("OutlineViewer");
      primaryStage.setScene(new Scene(mainWindow));
      primaryStage.show();
    }
  }

}
