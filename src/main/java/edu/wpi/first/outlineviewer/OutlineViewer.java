package edu.wpi.first.outlineviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class OutlineViewer extends Application {

  private Parent root;

  @Override
  public void init() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("OutlineViewer.fxml"));
    root = loader.load();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setScene(new Scene(root));
    primaryStage.setTitle("OutlineViewer");
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
