package edu.wpi.first.tableviewer.component;

import edu.wpi.first.tableviewer.FxHelper;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class FilterableTreeTableTest extends ApplicationTest {

  private FilterableTreeTable<String> treeTable;

  @Override
  public void start(Stage stage) throws Exception {
    treeTable = new FilterableTreeTable<>();
    stage.setScene(new Scene(treeTable));
    stage.show();
  }

  @Test
  public void testRootIsSetNoFilter() {
    TreeItem<String> root = new TreeItem<>();
    FxHelper.runAndWait(() -> treeTable.setRoot(root));
    assertSame(root, treeTable.getRealRoot());
  }

  @Test
  public void testRootIsSetWithFilter() {
    TreeItem<String> root = new TreeItem<>();
    FxHelper.runAndWait(() -> treeTable.setRoot(root));
    FxHelper.runAndWait(() -> treeTable.setFilter(x -> true));
    assertNotSame(root, treeTable.getRoot());
  }

}
