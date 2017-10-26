package edu.wpi.first.outlineviewer.view.dialog;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import edu.wpi.first.outlineviewer.view.EditableTextFieldListCell;
import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.KeyCode;
import javafx.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.control.ListViewMatchers;
import org.testfx.util.WaitForAsyncUtils;

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
        ((ListView<Pair<Integer, String>>) lookup(".list-view").query())
            .getItems().stream()
            .map(Pair::getValue)
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
    ListView<Pair<Integer, String>> listView = lookup(ListViewMatchers.isEmpty()).query();
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

    Assertions.assertEquals(
        Arrays.stream(new String[]{"C", "A", "B"})
            .collect(Collectors.toList()),
        ((ListView<Pair<Integer, String>>) lookup(".list-view").query())
            .getItems().stream()
            .map(Pair::getValue)
            .collect(Collectors.toList()));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testSaveOnCommitEdit() {
    final String[] test = new String[]{"A"};
    dialog.setInitial(test);
    waitForFxEvents();

    doubleClickOn("A").write("B");
    clickOn((Node) lookup(".text-field-list-cell").match(match -> ((EditableTextFieldListCell) match).getItem() == null).query());

    Assertions.assertEquals(
        Arrays.stream(new String[]{"B"})
            .collect(Collectors.toList()),
        ((ListView<Pair<Integer, String>>) lookup(".list-view").query())
            .getItems().stream()
            .map(Pair::getValue)
            .collect(Collectors.toList()));
  }

}
