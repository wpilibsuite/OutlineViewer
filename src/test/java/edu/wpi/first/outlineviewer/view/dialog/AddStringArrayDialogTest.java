package edu.wpi.first.outlineviewer.view.dialog;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.util.Pair;
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

}
