package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.outlineviewer.NetworkTableUtilities;
import edu.wpi.first.outlineviewer.model.TreeRow;
import edu.wpi.first.outlineviewer.model.NetworkTableTreeRow;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class NetworkTableTreeTest extends ApplicationTest {

  private NetworkTableTree tree;
  private TreeItem<TreeRow> root;

  @AfterEach
  void shutdown() {
    NetworkTableUtilities.shutdown();
  }

  @Override
  public void start(Stage stage) throws Exception {
    NetworkTableUtilities.createNewNetworkTableInstance();

    tree = new NetworkTableTree(NetworkTableUtilities.getNetworkTableInstance());
    root = new TreeItem<>(
        new NetworkTableTreeRow(NetworkTableUtilities.getNetworkTableInstance().getTable("")));
    root.setExpanded(true);
    tree.setRoot(root);
    stage.setScene(new Scene(tree));
    stage.show();
  }

  @Test
  void testsetNetworkTableInstance() {
    NetworkTableUtilities.createNewNetworkTableInstance();

    tree.setNetworkTableInstance(NetworkTableUtilities.getNetworkTableInstance());
  }

  @Nested
  class SimpleEntryTests {

    @Test
    void testAddEntryKey() {
      NetworkTableEntry entry = NetworkTableUtilities.getNetworkTableInstance().getEntry("key");
      entry.setString("testAddEntryKey");

      waitForNtcoreEvents();
      waitForFxEvents();

      assertEquals(entry.getName(), root.getChildren().get(0).getValue().getKey());
    }

    @Test
    void testAddEntryValue() {
      NetworkTableEntry entry = NetworkTableUtilities.getNetworkTableInstance().getEntry("key");
      entry.setString("testAddEntryValue");

      waitForNtcoreEvents();
      waitForFxEvents();

      assertEquals(entry.getValue().getString(), root.getChildren().get(0).getValue().getValue());
    }

    @Test
    void testUpdateEntryValue() {
      NetworkTableEntry entry = NetworkTableUtilities.getNetworkTableInstance().getEntry("key");
      entry.setString("testAddEntryValue");

      waitForNtcoreEvents();
      waitForFxEvents();

      entry.setString("updatedValue");

      waitForNtcoreEvents();
      waitForFxEvents();

      assertEquals(entry.getValue().getString(), root.getChildren().get(0).getValue().getValue());
    }

    @Test
    void testDeleteEntry() {
      NetworkTableEntry entry = NetworkTableUtilities.getNetworkTableInstance().getEntry("key");
      entry.setString("testDeleteEntry");

      waitForNtcoreEvents();
      waitForFxEvents();

      entry.delete();

      waitForNtcoreEvents();
      waitForFxEvents();

      assertEquals(0, root.getChildren().size());
    }
  }

  @Nested
  class NestedEntryTests {

    @Test
    void testAddNested() {
      NetworkTableEntry entry
          = NetworkTableUtilities.getNetworkTableInstance().getEntry("nested/key");
      entry.setString("some value");

      waitForNtcoreEvents();
      waitForFxEvents();

      assertAll(
          () -> assertEquals(1, root.getChildren().size()),
          () -> assertEquals("/nested/", root.getChildren().get(0).getValue().getKey()),
          () -> assertEquals(1, root.getChildren().get(0).getChildren().size()),
          () -> assertEquals("nested/key",
              root.getChildren().get(0).getChildren().get(0).getValue().getKey())
      );
    }

    @Test
    void testDeleteNested() {
      NetworkTableEntry entry
          = NetworkTableUtilities.getNetworkTableInstance().getEntry("nested/key");
      entry.setString("some value");

      waitForNtcoreEvents();
      waitForFxEvents();

      entry.delete();

      waitForNtcoreEvents();
      waitForFxEvents();

      assertAll(
          () -> assertEquals(1, root.getChildren().size()),
          () -> assertEquals("/nested/", root.getChildren().get(0).getValue().getKey()),
          () -> assertEquals(0, root.getChildren().get(0).getChildren().size())
      );
    }
  }

  /**
   * Waits for ntcore listeners to be fired. This is a <i>blocking operation</i>.
   */
  private static void waitForNtcoreEvents() {
    NetworkTableUtilities.getNetworkTableInstance().waitForEntryListenerQueue(3);
  }

}
