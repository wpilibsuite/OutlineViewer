package edu.wpi.first.outlineviewer;

import edu.wpi.first.outlineviewer.controller.dialog.PreferencesDialog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class OutlineViewer extends Application {

  private static final ButtonType start = new ButtonType("Start");

  @Override
  public void start(Stage primaryStage) throws IOException {
    AutoUpdater updater = new AutoUpdater();
    PreferencesDialog preferencesDialog
        = new PreferencesDialog("Preferences", start, ButtonType.CANCEL);
    preferencesDialog.showAndWait()
                     .filter(bt -> start != bt)
                     .ifPresent(__ -> System.exit(0));
    preferencesDialog.getController().start();
    updater.init();

    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
    Pane mainWindow = loader.load();

    primaryStage.setTitle("OutlineViewer");
    primaryStage.setScene(new Scene(mainWindow));
    primaryStage.show();
  }

}
