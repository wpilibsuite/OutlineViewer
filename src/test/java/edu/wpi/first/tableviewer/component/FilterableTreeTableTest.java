package edu.wpi.first.tableviewer.component;

import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.*;

public class FilterableTreeTableTest extends ApplicationTest {

  private Stage stage;
  private FilterableTreeTable treeTable;

  @Override
  public void start(Stage stage) throws Exception {
    this.stage = stage;
    treeTable = new FilterableTreeTable();
    stage.setScene(new Scene(treeTable));
    stage.show();
  }

  @Override
  public void stop() throws Exception {
    stage.close();
  }

  @Test
  public void testRootIsSetNoFilter() {
    TreeItem root = new TreeItem();
    treeTable.setRoot(root);
    assertEquals(root, treeTable.getRealRoot());
  }

  @Test
  public void testRootIsSetWithFilter() {
    TreeItem root = new TreeItem();
    treeTable.setRoot(root);
    treeTable.setFilter(x -> true);
    assertEquals(root, treeTable.getRealRoot());
    assertFalse(root == treeTable.getRoot());
  }

}
