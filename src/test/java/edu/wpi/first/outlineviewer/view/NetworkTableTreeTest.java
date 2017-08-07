package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.NetworkTableUtils;
import edu.wpi.first.outlineviewer.model.TableValueEntry;
import edu.wpi.first.wpilibj.tables.ITable;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class NetworkTableTreeTest extends ApplicationTest {

  private NetworkTableTree tree;
  private TreeItem<TableValueEntry> root;

  @Override
  public void start(Stage stage) throws Exception {
    tree = new NetworkTableTree();
    root = new TreeItem<>(new RootTableEntry());
    root.setExpanded(true);
    tree.setRoot(root);
    stage.setScene(new Scene(tree));
    stage.show();
  }

  @Test
  public void testAddSimpleEntry() {
    final String key = "/key";
    final String value = "testAddSimpleEntry";
    tree.updateFromNetworkTables(key, value, 0);
    waitForFxEvents();
    assertEquals(1, root.getChildren().size());
    TreeItem<TableValueEntry> item = root.getChildren().get(0);
    assertEquals(key, item.getValue().getKey());
    assertEquals(value, item.getValue().getValue());
  }

  @Test
  public void testAddNested() {
    final String tableName = "/nested";
    final String entryName = "/key";
    final String value = "testAddNested";
    tree.updateFromNetworkTables(NetworkTableUtils.concat(tableName, entryName), value, 0);
    waitForFxEvents();

    assertEquals(1, root.getChildren().size());
    TreeItem<TableValueEntry> tableEntry = root.getChildren().get(0);
    assertThat(tableEntry.getValue(), instanceOf(TableValueEntry.class));
    assertThat(tableEntry.getValue().getKey(), is(tableName));
    assertEquals(1, tableEntry.getChildren().size());

    TreeItem<TableValueEntry> realEntry = tableEntry.getChildren().get(0);
    TableValueEntry entry = realEntry.getValue();
    assertThat(entry, instanceOf(StringEntry.class));
    assertEquals(NetworkTableUtils.concat(tableName, entryName), entry.getKey());
    assertEquals(value, entry.getValue());
  }

  @Test
  public void testDeleteSimpleEntry() {
    final String key = "/key";
    testAddSimpleEntry();
    tree.updateFromNetworkTables(key, null, ITable.NOTIFY_DELETE); // value should be irrelevant
    waitForFxEvents();
    assertEquals(0, root.getChildren().size());
  }

  @Test
  public void testDeleteNestedEntry() {
    final String key = "/a/very/nested/key";
    tree.updateFromNetworkTables(key, "", 0);
    waitForFxEvents();
    tree.updateFromNetworkTables(key, null, ITable.NOTIFY_DELETE); // value should be irrelevant
    waitForFxEvents();
    root.getChildren().forEach(System.out::println);
    assertEquals(0, root.getChildren().size());
  }

  @Test
  public void testDeleteNestedEntryWithSiblings() {
    final String keyToDelete = "/nested/deleteme";
    final String keyToKeep = "/nested/keepme";
    tree.updateFromNetworkTables(keyToDelete, "", 0);
    tree.updateFromNetworkTables(keyToKeep, "", 0);
    waitForFxEvents();

    tree.updateFromNetworkTables(keyToDelete, null, ITable.NOTIFY_DELETE);
    waitForFxEvents();

    assertEquals(1, root.getChildren().size());
    TreeItem<TableValueEntry> tableItem = root.getChildren().get(0);
    assertEquals(1, tableItem.getChildren().size());
    TableValueEntry onlyChild = tableItem.getChildren().get(0).getValue();
    assertEquals(keyToKeep, onlyChild.getKey());
  }

}
