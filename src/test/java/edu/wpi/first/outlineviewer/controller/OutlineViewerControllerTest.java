package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Before;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;

public abstract class OutlineViewerControllerTest extends ApplicationTest {

  protected Stage stage;

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("OutlineViewer.fxml"));
    Parent preferences = loader.load();
    Scene scene = new Scene(preferences);
    stage.setScene(scene);
    stage.show();

    this.stage = stage;
  }

  @Before
  public void before() {
    NetworkTable.shutdown();
  }

}
