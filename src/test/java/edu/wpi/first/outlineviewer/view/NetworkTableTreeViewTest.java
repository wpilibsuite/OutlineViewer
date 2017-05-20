package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.model.NetworkTableData;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

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
    rootData.getChildren().add(otherData);

    Assert.assertEquals(otherData, view.getRoot().getChildren().get(0).getValue());
  }

}
