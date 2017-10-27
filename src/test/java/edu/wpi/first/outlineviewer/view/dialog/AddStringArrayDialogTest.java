package edu.wpi.first.outlineviewer.view.dialog;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import edu.wpi.first.outlineviewer.view.EditableTextFieldListCell;
import edu.wpi.first.outlineviewer.view.IndexedValue;
import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.control.ListViewMatchers;

class AddStringArrayDialogTest extends AddEntryArrayDialogTest<AddStringArrayDialog> {

  AddStringArrayDialogTest() {
    super(AddStringArrayDialog::new);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testInitialValue() {
    final String[] test = new String[]{"", "A String", "And another!"};
    dialog.setInitial(test);

    Assertions.assertEquals(
        Arrays.stream(test)
            .collect(Collectors.toList()),
        ((ListView<IndexedValue<String>>) lookup(".list-view").query())
            .getItems().stream()
            .map(IndexedValue::getValue)
            .collect(Collectors.toList()));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testGetData() {
    final String[] test = new String[]{"", "A String", "And another!"};
    dialog.setInitial(test);

    assertArrayEquals(test, dialog.getData());
  }

  @Test
  void testToStringConverter() {
    final String test = "A String!";
    ListView<IndexedValue<String>> listView = lookup(ListViewMatchers.isEmpty()).query();
    clickOn("+");

    doubleClickOn((Node) from(listView).lookup(".list-cell").query()).press(KeyCode.DELETE)
        .write(test).type(KeyCode.ENTER);

    assertEquals(test, listView.getItems().get(0).getValue());
  }

  @Test
  @SuppressWarnings("unchecked")
  void testDragDrop() {
    final String[] test = new String[]{"A", "B", "C"};
    dialog.setInitial(test);
    waitForFxEvents();

    drag("C").dropTo("A");

    Assertions.assertArrayEquals(
        new String[]{"C", "A", "B"},
        ((ListView<IndexedValue<String>>) lookup(".list-view").query())
            .getItems().stream()
            .map(IndexedValue::getValue)
            .toArray(String[]::new));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testSaveOnCommitEdit() {
    final String[] test = new String[]{"A"};
    dialog.setInitial(test);
    waitForFxEvents();

    doubleClickOn("A").write("B");
    clickOn(lookup(".text-field-list-cell")
        .match(match -> ((EditableTextFieldListCell) match).getItem() == null)
        .queryAll()
        .stream()
        .sorted((node, t1) -> (int)(node.getLayoutY() - t1.getLayoutY()))
        .collect(Collectors.toList())
        .get(1));
    waitForFxEvents();

    Assertions.assertArrayEquals(
        new String[]{"B"},
        ((ListView<IndexedValue<String>>) lookup(".list-view").query())
            .getItems().stream()
            .map(IndexedValue::getValue)
            .toArray(String[]::new));
  }

}
