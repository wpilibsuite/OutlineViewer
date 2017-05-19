package edu.wpi.first.outlineviewer.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;

public abstract class PreferencesControllerTest extends ApplicationTest {

  protected Stage stage;

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"));
    Parent preferences = loader.load();
    Scene scene = new Scene(preferences);
    stage.setScene(scene);
    stage.show();

    this.stage = stage;
  }

}
