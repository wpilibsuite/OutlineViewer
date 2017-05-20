package edu.wpi.first.outlineviewer;

import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;

public class OutlineViewerTest extends ApplicationTest {

  private final OutlineViewer outlineViewer = new OutlineViewer();

  @Override
  public void init() throws Exception {
    outlineViewer.init();
  }

  @Override
  public void start(Stage stage) throws Exception {
    outlineViewer.start(stage);
  }

}
