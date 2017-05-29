package edu.wpi.first.tableviewer.dialog;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;

/**
 *
 */
public class AddNumberArrayDialog extends AddEntryDialog<double[]> {

  private ListView<Double> list;

  public AddNumberArrayDialog() {
    super("Number Array");
    getDialogPane().setMaxHeight(300);
  }

  @Override
  protected Node createCustomControl() {
    list = new ListView<>();
    list.setEditable(true);
    list.setCellFactory(__ -> new TextFieldListCell<>(DoubleToStringConverter.INSTANCE));
    list.setOnKeyPressed(e -> {
      KeyCode code = e.getCode();
      if (code == KeyCode.DELETE) {
        removeSelected();
      }
    });

    Button add = new Button("+");
    add.setPrefWidth(40);
    add.setOnAction(__ -> list.getItems().add(0.0));

    return new VBox(8, list, add);
  }

  public void setInitial(double[] initialValues) {
    list.getItems().clear();
    for (double value : initialValues) {
      list.getItems().add(value);
    }
  }

  private void removeSelected() {
    list.getItems().removeAll(list.getSelectionModel().getSelectedItems());
  }

  @Override
  protected double[] getData() {
    return toPrimitiveArray(list.getItems());
  }

  private static double[] toPrimitiveArray(List<Double> list) {
    double[] arr = new double[list.size()];
    for (int i = 0; i < list.size(); i++) {
      arr[i] = list.get(i);
    }
    return arr;
  }

  private static final class DoubleToStringConverter extends StringConverter<Double> {

    public static final StringConverter<Double> INSTANCE = new DoubleToStringConverter();

    @Override
    public String toString(Double d) {
      if (d.doubleValue() == d.intValue()) {
        return String.valueOf(d.intValue());
      } else {
        return String.valueOf(d.doubleValue());
      }
    }

    @Override
    public Double fromString(String string) {
      return Double.parseDouble(string);
    }

  }

}
