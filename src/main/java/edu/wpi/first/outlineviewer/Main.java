package edu.wpi.first.outlineviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("Preferences.fxml"));
    Parent preferences = loader.load();
    Stage preferencesStage = new Stage();
    preferencesStage.setTitle("Preferences");
    preferencesStage.setScene(new Scene(preferences));
    preferencesStage.showAndWait();

    if (((PreferencesController) loader.getController()).getResult() != 0) {
      Parent root = FXMLLoader.load(getClass().getResource("TableViewer.fxml"));
      primaryStage.setTitle("Outline Viewer");
      primaryStage.setScene(new Scene(root));
      primaryStage.show();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
