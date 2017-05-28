package edu.wpi.first.tableviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences;

public class Main extends Application {

  public static final Preferences preferences = Preferences.userNodeForPackage(Main.class);

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    FXMLLoader prefsLoader = new FXMLLoader(getClass().getResource("StartupPreferences.fxml"));
    FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
    Pane prefsRoot = prefsLoader.load();
    Pane mainWindow = mainLoader.load();
    StartupPreferencesController prefsController = prefsLoader.getController();
    MainWindowController mainWindowController = mainLoader.getController();

    primaryStage.setScene(new Scene(prefsRoot));
    primaryStage.centerOnScreen();

    prefsController.startedProperty().addListener((obs, old, start) -> {
      if (start) {
        primaryStage.sizeToScene();
        primaryStage.setMinHeight(mainWindow.getMinHeight() + 32);
        primaryStage.setMinWidth(mainWindow.getMinWidth() + 32);
        mainWindowController.updateConnectionLabel(false, null);
        if (preferences.getBoolean("show_metadata", false)) {
          mainWindowController.showMetadata();
        } else {
          mainWindowController.hideMetadata();
        }
        primaryStage.setScene(new Scene(mainWindow));
        primaryStage.centerOnScreen();
      }
    });
    primaryStage.setTitle("Network Table Viewer");
    primaryStage.setOnCloseRequest(e -> System.exit(0));
    primaryStage.show();
  }

}
