package edu.wpi.first.outlineviewer.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;

public abstract class OutlineViewerControllerTest extends ApplicationTest {

  protected OutlineViewerController outlineViewerController;

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader loader
        = new FXMLLoader(OutlineViewerController.class.getResource("OutlineViewer.fxml"));
    Parent outlineViewer = loader.load();
    Scene scene = new Scene(outlineViewer);
    stage.setScene(scene);
    stage.show();

    outlineViewerController = loader.getController();
  }

}
