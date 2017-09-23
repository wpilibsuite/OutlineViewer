package edu.wpi.first.outlineviewer.view.dialog;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.control.ListViewMatchers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

class AddStringArrayDialogTest extends AddEntryArrayDialogTest<AddStringArrayDialog> {

  AddStringArrayDialogTest() {
    super(AddStringArrayDialog::new);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testInitialValue() {
    final String[] test = new String[]{"", "A String", "And another!"};
    dialog.setInitial(test);

    assertArrayEquals(test, ((ListView) lookup(".list-view").query()).getItems().toArray());
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
    ListView listView = lookup(ListViewMatchers.isEmpty()).query();
    clickOn("+");

    doubleClickOn((Node) from(listView).lookup(".list-cell").query()).press(KeyCode.DELETE)
        .write(test).type(KeyCode.ENTER);

    assertEquals(test, listView.getItems().get(0));
  }

}
