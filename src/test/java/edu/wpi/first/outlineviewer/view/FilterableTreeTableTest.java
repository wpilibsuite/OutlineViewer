package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.FxHelper;
import edu.wpi.first.outlineviewer.model.RootTableEntry;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class FilterableTreeTableTest extends ApplicationTest {

  private FilterableTreeTable<String> treeTable;

  @Override
  public void start(Stage stage) throws Exception {
    treeTable = new FilterableTreeTable<>();
    treeTable.getColumns().add(new TreeTableColumn<>("Title"));
    treeTable.setRowFactory(param -> new TreeTableRow<>());

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

  /*
  @Test
  public void selectionRetainedOnUpdate() throws InterruptedException {
    FxHelper.runAndWait(() -> treeTable.setRoot(new TreeItem<>("Root")));
    TreeTableColumn<String, String> column = new TreeTableColumn<>("Text");
    column.setCellFactory(param -> new ReadOnlyStringWrapper(param);
    treeTable.getColumns().add(new TreeTableColumn<>("Text"));
    treeTable.setRowFactory(param -> new TreeTableRow<>());

    TreeItem<String> a = new TreeItem<>("A");
    TreeItem<String> b = new TreeItem<>("B");
    TreeItem<String> c = new TreeItem<>("C");
    treeTable.getRealRoot().getChildren().add(a);
    treeTable.getRealRoot().getChildren().add(b);
    treeTable.getRealRoot().getChildren().add(c);

    Thread.sleep(5000);

    clickOn("B");
    c.setValue("D");

    assertTrue(treeTable.getSelectionModel().getSelectedCells().stream().map(TreeTablePosition::getTreeItem).anyMatch(item -> item.equals(b)));
  }
  */

}
