package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.outlineviewer.NetworkTableUtilities;
import edu.wpi.first.outlineviewer.model.TreeRow;
import edu.wpi.first.outlineviewer.model.NetworkTableTreeRow;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class NetworkTableTreeTest extends ApplicationTest {

  private TreeItem<TreeRow> root;

  @AfterEach
  void shutdown() {
    NetworkTableUtilities.shutdown();
  }

  @Override
  public void start(Stage stage) throws Exception {
    NetworkTableUtilities.createNewNetworkTableInstance();

    NetworkTableTree tree = new NetworkTableTree(NetworkTableUtilities.getNetworkTableInstance());
    root = new TreeItem<>(
        new NetworkTableTreeRow(NetworkTableUtilities.getNetworkTableInstance().getTable("")));
    root.setExpanded(true);
    tree.setRoot(root);
    stage.setScene(new Scene(tree));
    stage.show();
  }

  @Test
  void testAddSimpleEntry() {
    final String key = "/key";
    final String value = "testAddSimpleEntry";
    NetworkTableUtilities.getNetworkTableInstance().getEntry(key).setString(value);

    waitForNtcoreEvents();
    waitForFxEvents();

    assertEquals(1, root.getChildren().size());
    TreeItem<TreeRow> item = root.getChildren().get(0);

    assertEquals(key, item.getValue().getKey());
    assertEquals(value, item.getValue().getValue());
  }

  @Test
  void testAddNested() {
    final String tableName = "/nested";
    final String entryName = "/key";
    final String value = "testAddNested";
    NetworkTableUtilities.getNetworkTableInstance()
        .getEntry(NetworkTableUtilities.concat(tableName, entryName))
        .setString(value);

    waitForNtcoreEvents();
    waitForFxEvents();

    assertEquals(1, root.getChildren().size());
    TreeItem<TreeRow> tableEntry = root.getChildren().get(0);
    assertThat(tableEntry.getValue(), instanceOf(NetworkTableTreeRow.class));
    //assertThat(tableEntry.getValue().getKey(), is(tableName));
    assertEquals(1, tableEntry.getChildren().size());

    TreeItem<TreeRow> realEntry = tableEntry.getChildren().get(0);
    TreeRow entry = realEntry.getValue();
    assertEquals(NetworkTableUtilities.concat(tableName, entryName), entry.getKey());
    assertEquals(value, entry.getValue());
  }

  @Test
  void testDeleteSimpleEntry() {
    final String key = "/key";
    testAddSimpleEntry();
    NetworkTableUtilities.getNetworkTableInstance().getEntry(key).delete();

    waitForNtcoreEvents();
    waitForFxEvents();
    assertEquals(0, root.getChildren().size());
  }

  @Test
  void testDeleteNestedEntryWithSiblings() {
    final String keyToDelete = "/nested/deleteme";
    final String keyToKeep = "/nested/keepme";
    NetworkTableEntry entry = NetworkTableUtilities.getNetworkTableInstance().getEntry(keyToDelete);
    entry.setString("");
    NetworkTableUtilities.getNetworkTableInstance().getEntry(keyToKeep).setString("");

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
        .waitForEntryListenerQueue(NetworkTableUtilities.getNetworkTableInstance().getHandle(), -1);
  }

}
