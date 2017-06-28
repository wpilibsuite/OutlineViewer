package edu.wpi.first.outlineviewer.view.dialog;

import com.google.common.primitives.Doubles;
import javafx.scene.control.ListView;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class AddNumberArrayDialogTest extends AddEntryArrayDialogTest {

  public AddNumberArrayDialogTest() {
    super(AddNumberArrayDialog::new);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testInitialValue() {
    final double[] test = new double[]{1.0, 5.5, 3.14, -19.01};
    ((AddEntryArrayDialog) dialog).setInitial(test);

    assertArrayEquals(test,
        Doubles.toArray(((ListView) lookup(".list-view").query()).getItems()), 0.0);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetData() {
    final double[] test = new double[]{1.0, 5.5, 3.14, -19.01};
    ((AddEntryArrayDialog) dialog).setInitial(test);

    assertArrayEquals(test, (double[]) dialog.getData(), 0.0);
  }

}
