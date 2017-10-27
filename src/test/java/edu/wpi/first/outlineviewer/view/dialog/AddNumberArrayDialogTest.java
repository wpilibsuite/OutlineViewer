package edu.wpi.first.outlineviewer.view.dialog;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import com.google.common.primitives.Doubles;
import edu.wpi.first.outlineviewer.view.EditableTextFieldListCell;
import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.control.ListViewMatchers;

class AddNumberArrayDialogTest extends AddEntryArrayDialogTest<AddNumberArrayDialog> {

  AddNumberArrayDialogTest() {
    super(AddNumberArrayDialog::new);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testInitialValue() {
    final double[] test = new double[]{1.0, 5.5, 3.14, -19.01};
    dialog.setInitial(test);

    assertArrayEquals(test,
        Doubles.toArray(((ListView<Pair<Integer, Double>>) lookup(".list-view").query())
            .getItems().stream()
            .map(Pair::getValue)
            .collect(Collectors.toList())));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testGetData() {
    final double[] test = new double[]{1.0, 5.5, 3.14, -19.01};
    dialog.setInitial(test);

    assertArrayEquals(test, dialog.getData(), 0.0);
  }

  @Test
  void testToStringConverter() {
    final double test = 654.321;
    ListView<Pair<Integer, Double>> listView = lookup(ListViewMatchers.isEmpty()).query();
    clickOn("+");

    doubleClickOn((Node) from(listView).lookup(".list-cell").query()).press(KeyCode.DELETE)
        .write(String.valueOf(test)).type(KeyCode.ENTER);

    assertEquals(Double.valueOf(test), listView.getItems().get(0).getValue());
  }

  @Test
  @SuppressWarnings("unchecked")
  void testDragDrop() {
    final double[] test = new double[]{1.0, 5.5, 4.0, -19.01};
    dialog.setInitial(test);
    waitForFxEvents();

    drag("-19.01").dropTo("1");

    Assertions.assertEquals(
        Arrays.stream(new Double[]{-19.01, 1.0, 5.5, 4.0})
            .collect(Collectors.toList()),
        ((ListView<Pair<Integer, Double>>) lookup(".list-view").query())
            .getItems().stream()
            .map(Pair::getValue)
            .collect(Collectors.toList()));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testSaveOnCommitEdit() {
    final double[] test = new double[]{1.0};
    dialog.setInitial(test);
    waitForFxEvents();

    doubleClickOn("1").write("2.0");
    clickOn((Node) lookup(".text-field-list-cell")
        .match(match -> ((EditableTextFieldListCell) match).getItem() == null).query());
    waitForFxEvents();

    Assertions.assertEquals(
        Arrays.stream(new Double[]{2.0})
            .collect(Collectors.toList()),
        ((ListView<Pair<Integer, Double>>) lookup(".list-view").query())
            .getItems().stream()
            .map(Pair::getValue)
            .collect(Collectors.toList()));
  }

}
