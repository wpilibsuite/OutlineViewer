package edu.wpi.first.tableviewer;

import edu.wpi.first.tableviewer.dialog.PreferencesDialog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    Optional<PreferencesDialog.PrefsResult> prefsResult = new PreferencesDialog().showAndWait();
    if (!prefsResult.isPresent()) {
      System.out.println("Cancelled");
      System.exit(0);
    }
    FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
    Pane mainWindow = mainLoader.load();
    MainWindowController mainWindowController = mainLoader.getController();
    mainWindowController.updateConnectionLabel(false, null);


    primaryStage.setTitle("Network Table Viewer");
    primaryStage.setOnCloseRequest(e -> System.exit(0));
    primaryStage.setScene(new Scene(mainWindow));
    primaryStage.show();
    primaryStage.centerOnScreen();
  }

}
