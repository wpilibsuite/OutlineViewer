package edu.wpi.first.outlineviewer.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.util.stream.Collectors;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class EditableTextFieldListCellTest extends ApplicationTest {

  private ListView<IndexedValue<String>> listView;

  @Override
  public void start(Stage stage) throws Exception {
    listView = new ListView<>();
    listView.setCellFactory(__ -> new EditableTextFieldListCell<>(
        new IndexedStringConverter<String>() {
          @Override
          public String toString(IndexedValue<String> object) {
            return object.getValue();
          }

          @Override
          public IndexedValue<String> fromString(Integer index, String string) {
            return new IndexedValue<>(index, string);
          }

          @Override
          public IndexedValue<String> fromString(String string) {
            return fromString(0, string);
          }
        }));

    listView.setEditable(true);
    listView.getItems().add(new IndexedValue<>("String"));

    stage.setScene(new Scene(listView));
    stage.show();
  }

  @Test
  void testNonEditableListView() {
    listView.setEditable(false);
    EditableTextFieldListCell<IndexedValue<String>> cell = lookup(".text-field-list-cell")
        .query();

    doubleClickOn(cell).write("test");
    waitForFxEvents();

    assertFalse(cell.isEditing());
  }

  @Test
  void testNonEditableCell() {
    listView.setEditable(true);
    EditableTextFieldListCell<IndexedValue<String>> cell = lookup(".text-field-list-cell")
        .query();
    cell.setEditable(false);

    doubleClickOn(cell).write("test");
    waitForFxEvents();

    assertFalse(cell.isEditing());
  }

  @Test
  void testEditableListView() {
    listView.setEditable(true);
    EditableTextFieldListCell<IndexedValue<String>> cell = lookup(".text-field-list-cell")
        .query();

    doubleClickOn(cell).write("test");
    waitForFxEvents();

    assertTrue(cell.isEditing());
  }

  @Test
  void testCommitEdit() {
    listView.setEditable(true);
    EditableTextFieldListCell<IndexedValue<String>> cell = lookup(".text-field-list-cell")
        .query();

    doubleClickOn(cell)
        .write("test")
        .clickOn(lookup(".text-field-list-cell")
            .match(match -> ((EditableTextFieldListCell) match).getItem() == null)
            .queryAll()
            .stream()
            .sorted((node, t1) -> (int) (node.getLayoutY() - t1.getLayoutY()))
            .collect(Collectors.toList())
            .get(1));
    waitForFxEvents();

    assertEquals("test", cell.getTextField().getText());
  }

}
