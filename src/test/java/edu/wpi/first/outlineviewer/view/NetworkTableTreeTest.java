package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.outlineviewer.NetworkTableUtils;
import edu.wpi.first.outlineviewer.model.TreeRow;
import edu.wpi.first.outlineviewer.model.TreeTableEntry;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class NetworkTableTreeTest extends ApplicationTest {

  private NetworkTableTree tree;
  private TreeItem<TreeRow> root;

  @After
  public void shutdown() {
    NetworkTableUtils.shutdown();
  }

  @Override
  public void start(Stage stage) throws Exception {
    NetworkTableUtils.createNewNetworkTableInstance();

    tree = new NetworkTableTree(NetworkTableUtils.getNetworkTableInstance());
    root = new TreeItem<>(
        new TreeTableEntry(NetworkTableUtils.getNetworkTableInstance().getTable("")));
    root.setExpanded(true);
    tree.setRoot(root);
    stage.setScene(new Scene(tree));
    stage.show();
  }

  @Test
  public void testAddSimpleEntry() {
    final String key = "/key";
    final String value = "testAddSimpleEntry";
    NetworkTableUtils.getNetworkTableInstance().getEntry(key).setString(value);

    waitForNtcoreEvents();
    waitForFxEvents();

    assertEquals(1, root.getChildren().size());
    TreeItem<TreeRow> item = root.getChildren().get(0);

    assertEquals(key, item.getValue().getKey());
    assertEquals(value, item.getValue().getValue());
  }

  @Test
  public void testAddNested() {
    final String tableName = "/nested";
    final String entryName = "/key";
    final String value = "testAddNested";
    NetworkTableUtils.getNetworkTableInstance().getEntry(NetworkTableUtils.concat(tableName, entryName)).setString(value);

    waitForNtcoreEvents();
    waitForFxEvents();

    assertEquals(1, root.getChildren().size());
    TreeItem<TreeRow> tableEntry = root.getChildren().get(0);
    assertThat(tableEntry.getValue(), instanceOf(TreeTableEntry.class));
    //assertThat(tableEntry.getValue().getKey(), is(tableName));
    assertEquals(1, tableEntry.getChildren().size());

    TreeItem<TreeRow> realEntry = tableEntry.getChildren().get(0);
    TreeRow entry = realEntry.getValue();
    assertEquals(NetworkTableUtils.concat(tableName, entryName), entry.getKey());
    assertEquals(value, entry.getValue());
  }

  @Test
  public void testDeleteSimpleEntry() {
    final String key = "/key";
    testAddSimpleEntry();
    NetworkTableUtils.getNetworkTableInstance().getEntry(key).delete();

    waitForNtcoreEvents();
    waitForFxEvents();
    assertEquals(0, root.getChildren().size());
  }

  @Test
  public void testDeleteNestedEntryWithSiblings() {
    final String keyToDelete = "/nested/deleteme";
    final String keyToKeep = "/nested/keepme";
    NetworkTableEntry entry = NetworkTableUtils.getNetworkTableInstance().getEntry(keyToDelete);
    entry.setString("");
    NetworkTableUtils.getNetworkTableInstance().getEntry(keyToKeep).setString("");

    waitForNtcoreEvents();
    waitForFxEvents();

    entry.delete();
    waitForNtcoreEvents();
    waitForFxEvents();

    assertEquals(1, root.getChildren().size());
    TreeItem<TreeRow> tableItem = root.getChildren().get(0);
    assertEquals(1, tableItem.getChildren().size());
    TreeRow onlyChild = tableItem.getChildren().get(0).getValue();
    assertEquals(keyToKeep, onlyChild.getKey());
  }

  /**
   * Waits for ntcore listeners to be fired. This is a <i>blocking operation</i>.
   */
  private void waitForNtcoreEvents() {
    NetworkTablesJNI
        .waitForEntryListenerQueue(NetworkTableUtils.getNetworkTableInstance().getHandle(), -1);
  }

}
