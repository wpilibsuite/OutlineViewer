package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.model.NetworkTableData;
import edu.wpi.first.outlineviewer.model.NetworkTableString;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NetworkTableTreeViewTest extends ApplicationTest {

  private NetworkTableTreeView view;

  @Override
  public void start(Stage stage) throws Exception {
    view = new NetworkTableTreeView();
    Scene scene = new Scene(view);
    stage.setScene(scene);
    stage.show();
  }

  @Before
  public void before() {
    NetworkTable.shutdown();
  }

  @Test
  public void testListenerAddToTree() {
    NetworkTableData rootData = new NetworkTableData("");
    Platform.runLater(() -> view.setRootData(rootData));
    WaitForAsyncUtils.waitForFxEvents();

    NetworkTableData otherData = new NetworkTableData("Other");
    rootData.addChild(otherData);

    assertEquals(otherData, view.getRoot().getChildren().get(0).getValue());
  }

  @Test
  public void testListenerRemoveFromTree() {
    NetworkTableData rootData = new NetworkTableData("");
    NetworkTableData otherData = new NetworkTableData("Other");
    rootData.addChild(otherData);

    Platform.runLater(() -> view.setRootData(rootData));
    WaitForAsyncUtils.waitForFxEvents();

    otherData.remove();
    assertTrue(view.getRoot().getChildren().isEmpty());
  }

  @Test
  public void testUpdateValue() {
    NetworkTableData rootData = new NetworkTableData("");
    NetworkTableString otherData = new NetworkTableString("key", "1");
    rootData.addChild(otherData);
    Platform.runLater(() -> view.setRootData(rootData));
    WaitForAsyncUtils.waitForFxEvents();

    rootData.setOrCreateChild(NetworkTableData.getKeyPath("key"), "2");

    assertEquals(Arrays.toString(view.getRoot().getChildren().toArray()),1,
        view.getRoot().getChildren().size());
  }

}
