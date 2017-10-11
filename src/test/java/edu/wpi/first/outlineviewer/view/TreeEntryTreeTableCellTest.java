package edu.wpi.first.outlineviewer.view;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import edu.wpi.first.outlineviewer.FxHelper;
import edu.wpi.first.outlineviewer.model.TreeRow;
import java.util.Arrays;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class TreeEntryTreeTableCellTest extends ApplicationTest {

  TreeItem<TreeRow> root;
  TreeTableColumn<TreeRow, Object> valueColumn;
  TreeTableView<TreeRow> treeTableView;

  @Override
  public void start(Stage stage) throws Exception {
    root = new TreeItem<>(new TreeRow(""));
    root.setExpanded(true);

    valueColumn = new TreeTableColumn<>("Values");
    valueColumn.setEditable(true);
    valueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
    valueColumn.setCellFactory(param -> new TreeEntryTreeTableCell<>());

    treeTableView = new TreeTableView<>(root);
    treeTableView.setEditable(true);
    treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
    treeTableView.getColumns().add(valueColumn);
    stage.setScene(new Scene(treeTableView));
    stage.show();
  }

  @Test
  @Disabled
  void invalidTest() throws Exception {
    TreeRow row = new TreeRow("", new Object());
    TreeItem<TreeRow> item = new TreeItem<>(row);
    root.getChildren().add(new TreeItem<>(row));

    waitForFxEvents();

    FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

    Thread.sleep(5000);
  }

  @Nested
  class BooleanTests {

    private TreeRow row;

    @BeforeEach
    void setup() {
      row = new TreeRow("", false);
      root.getChildren().add(new TreeItem<>(row));

      waitForFxEvents();
    }

    @Test
    void testSet() {
      clickOn(".check-box");

      waitForFxEvents();

      assertTrue((Boolean) row.getValue());
    }
  }

  @Nested
  class StringTests {

    private TreeItem<TreeRow> item;
    private TreeRow row;

    @BeforeEach
    void setup() {
      row = new TreeRow("", "string");
      item = new TreeItem<>(row);
      root.getChildren().add(item);

      waitForFxEvents();
    }

    @Test
    void testSet() {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      type(KeyCode.DELETE).write("else").type(KeyCode.ENTER);
      waitForFxEvents();

      assertEquals("else", row.getValue());
    }

    @Test
    void testCancelEdit() {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      type(KeyCode.DELETE).write("else").type(KeyCode.ESCAPE);
      waitForFxEvents();

      assertEquals("string", row.getValue());
    }
  }

  @Nested
  class NumberTests {

    private TreeItem<TreeRow> item;
    private TreeRow row;

    @BeforeEach
    void setup() {
      row = new TreeRow("", 123.456);
      item = new TreeItem<>(row);
      root.getChildren().add(item);

      waitForFxEvents();
    }

    @Test
    void testSet() {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      type(KeyCode.DELETE).write("654.321").type(KeyCode.ENTER);
      waitForFxEvents();

      assertEquals(654.321, row.getValue());
    }

    @Test
    void testCancelEdit() {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      type(KeyCode.DELETE).write("654.321").type(KeyCode.ESCAPE);
      waitForFxEvents();

      assertEquals(123.456, row.getValue());
    }

    @Test
    void testInvalidNumber() {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      type(KeyCode.DELETE).write("aString").type(KeyCode.ENTER);
      waitForFxEvents();

      assertEquals(123.456, row.getValue());
    }
  }

  @Nested
  class StringArrayTests {

    private TreeItem<TreeRow> item;
    private TreeRow row;

    @BeforeEach
    void setup() {
      row = new TreeRow("", new String[]{"A", "B", "C"});
      item = new TreeItem<>(row);
      root.getChildren().add(item);

      waitForFxEvents();
    }

    @AfterEach
    void cleanUp() {
      listWindows().forEach(window -> FxHelper.runAndWait(window::hide));
    }

    @Test
    void testOpenDialog() throws Exception {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      assertTrue(lookup(".add-model-dialog").query().isVisible());
    }

    @Test
    void testCancelEdit() {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      FxHelper.runAndWait(() -> window(0).hide());

      assertArrayEquals(new String[]{"A", "B", "C"}, (String[]) row.getValue());
    }

    // TODO: Commit test
  }

  @Nested
  class NumberArrayTests {

    private TreeItem<TreeRow> item;
    private TreeRow row;

    @BeforeEach
    void setup() {
      row = new TreeRow("", new Number[]{1.0, 2, 0.3});
      item = new TreeItem<>(row);
      root.getChildren().add(item);

      waitForFxEvents();
    }

    @AfterEach
    void cleanUp() {
      listWindows().forEach(window -> FxHelper.runAndWait(window::hide));
    }

    @Test
    void testOpenDialog() throws Exception {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      assertTrue(lookup(".add-model-dialog").query().isVisible());
    }

    @Test
    void testCancelEdit() {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      FxHelper.runAndWait(() -> window(0).hide());

      assertArrayEquals(new Number[]{1.0, 2, 0.3}, (Number[]) row.getValue());
    }

    // TODO: Commit test
  }

  @Nested
  class BooleanArrayTests {

    private TreeItem<TreeRow> item;
    private TreeRow row;

    @BeforeEach
    void setup() {
      row = new TreeRow("", new boolean[]{false, true, false});
      item = new TreeItem<>(row);
      root.getChildren().add(item);

      waitForFxEvents();
    }

    @AfterEach
    void cleanUp() {
      listWindows().forEach(window -> FxHelper.runAndWait(window::hide));
    }

    @Test
    void testOpenDialog() throws Exception {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      assertTrue(lookup(".add-model-dialog").query().isVisible());
    }

    @Test
    void testCancelEdit() {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      FxHelper.runAndWait(() -> window(0).hide());

      assertTrue(Arrays.equals(new boolean[]{false, true, false}, (boolean[]) row.getValue()));
    }

    // TODO: Commit test
  }

  @Nested
  class ByteArrayTests {

    private TreeItem<TreeRow> item;
    private TreeRow row;

    @BeforeEach
    void setup() {
      row = new TreeRow("", new byte[]{0x0, 0xF, 0x5});
      item = new TreeItem<>(row);
      root.getChildren().add(item);

      waitForFxEvents();
    }

    @AfterEach
    void cleanUp() {
      listWindows().forEach(window -> FxHelper.runAndWait(window::hide));
    }

    @Test
    void testOpenDialog() throws Exception {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      assertTrue(lookup(".add-model-dialog").query().isVisible());
    }

    @Test
    void testCancelEdit() {
      FxHelper.runAndWait(() -> treeTableView.edit(treeTableView.getRow(item), valueColumn));

      FxHelper.runAndWait(() -> window(0).hide());

      assertTrue(Arrays.equals(new byte[]{0x0, 0xF, 0x5}, (byte[]) row.getValue()));
    }

    // TODO: Commit test
  }

}
