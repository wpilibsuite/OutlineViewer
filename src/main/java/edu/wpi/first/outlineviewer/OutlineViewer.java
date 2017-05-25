package edu.wpi.first.outlineviewer;

import edu.wpi.first.outlineviewer.controller.OutlineViewerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public class OutlineViewer extends Application {

  private Pane root;

  @Override
  public void init() throws IOException {
    FXMLLoader loader
        = new FXMLLoader(OutlineViewerController.class.getResource("OutlineViewer.fxml"));
    root = loader.load();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    checkNotNull(primaryStage);
    checkNotNull(root);

    primaryStage.setScene(new Scene(root));
    primaryStage.setTitle("OutlineViewer");
    primaryStage.show();
  }
}
