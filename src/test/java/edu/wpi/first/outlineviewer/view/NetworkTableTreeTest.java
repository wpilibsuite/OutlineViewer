package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.NetworkTableUtils;
import edu.wpi.first.outlineviewer.model.TreeRow;
import edu.wpi.first.outlineviewer.model.TreeEntry;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
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
    root = new TreeItem<>(new TreeRow(""));
    root.setExpanded(true);
    tree.setRoot(root);
    stage.setScene(new Scene(tree));
    stage.show();
  }

  @Test
  public void testAddSimpleEntry() {
    final String key = "/key";
    final String value = "testAddSimpleEntry";
    tree.getNetworkTable().putString(key, value);
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
    tree.getNetworkTable().putString(NetworkTableUtils.concat(tableName, entryName), value);
    waitForNtcoreEvents();
    waitForFxEvents();

    assertEquals(1, root.getChildren().size());
    TreeItem<TreeRow> tableEntry = root.getChildren().get(0);
    assertThat(tableEntry.getValue(), instanceOf(TreeEntry.class));
    assertThat(tableEntry.getValue().getKey(), is(tableName));
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
    tree.getNetworkTable().delete(key);
    waitForNtcoreEvents();
    waitForFxEvents();
    assertEquals(0, root.getChildren().size());
  }

  @Test
  public void testDeleteNestedEntry() {
    final String key = "/a/very/nested/key";
    tree.getNetworkTable().putString(key, "");
    waitForNtcoreEvents();
    waitForFxEvents();
    tree.getNetworkTable().delete(key);
    waitForNtcoreEvents();
    waitForFxEvents();
    root.getChildren().forEach(System.out::println);
    assertEquals(0, root.getChildren().size());
  }

  @Test
  public void testDeleteNestedEntryWithSiblings() {
    final String keyToDelete = "/nested/deleteme";
    final String keyToKeep = "/nested/keepme";
    tree.getNetworkTable().putString(keyToDelete, "");
    tree.getNetworkTable().putString(keyToKeep, "");
    waitForNtcoreEvents();
    waitForFxEvents();

    tree.getNetworkTable().delete(keyToDelete);
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
    CompletableFuture<?> future = new CompletableFuture<>();
    final String indexKey = "waitForNtcoreEvents";

    int listenerOneHandle = NetworkTableUtils.getNetworkTableInstance().addEntryListener(indexKey,
        (entry, value, flags) -> NetworkTableUtils.getRootTable().delete(indexKey),
        NetworkTable.NOTIFY_NEW | NetworkTable.NOTIFY_LOCAL);
    int listenerTwoHandle = NetworkTableUtils.getNetworkTableInstance().addEntryListener(indexKey,
        (entry, value, flags) -> future.complete(null),
        NetworkTable.NOTIFY_DELETE | NetworkTable.NOTIFY_LOCAL);

    /*
     * This works because all notifications are put into a single queue and are processed by a
     * single thread.
     *
     * https://github.com/wpilibsuite/shuffleboard/pull/118#issuecomment-321374691
     */
    NetworkTableUtils.getRootTable().putBoolean(indexKey, false);
    future.join();

    NetworkTableUtils.getNetworkTableInstance().removeEntryListener(listenerOneHandle);
    NetworkTableUtils.getNetworkTableInstance().removeEntryListener(listenerTwoHandle);
  }

}
