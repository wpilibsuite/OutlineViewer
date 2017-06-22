package edu.wpi.first.tableviewer;

import edu.wpi.first.tableviewer.dialog.PreferencesDialog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

  private static final ButtonType start = new ButtonType("Start");

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    AutoUpdater updater = new AutoUpdater();
    PreferencesDialog preferencesDialog
        = new PreferencesDialog("Preferences", start, ButtonType.CANCEL);
    preferencesDialog.showAndWait()
                     .filter(bt -> start != bt)
                     .ifPresent(__ -> {
                       System.out.println("Cancelled");
                       System.exit(0);
                     });
    preferencesDialog.getController().start();
    updater.init();

    FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
    Pane mainWindow = mainLoader.load();

    primaryStage.setTitle("Network Table Viewer");
    primaryStage.setOnCloseRequest(e -> System.exit(0));
    primaryStage.setScene(new Scene(mainWindow));
    primaryStage.show();
    primaryStage.setMinWidth(600);
    primaryStage.setMinHeight(450);
    primaryStage.centerOnScreen();
  }

}
