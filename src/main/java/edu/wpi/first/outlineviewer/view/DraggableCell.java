package edu.wpi.first.outlineviewer.view;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DraggableCell<T> extends ListCell<T> {

  private static final DataFormat T_FORMAT = new DataFormat("GENERIC");
  private long time;

  public DraggableCell() {
    time = System.nanoTime();

    ListCell<T> thisCell = this;

    setOnDragDetected(event -> {
      if (getItem() == null) {
        return;
      }

      Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
      ClipboardContent content = new ClipboardContent();
      content.put(T_FORMAT, getItem());
      dragboard.setContent(content);
      event.consume();
    });

    setOnDragOver(event -> {
      if (event.getGestureSource() != thisCell && event.getDragboard().hasContent(T_FORMAT)) {
        event.acceptTransferModes(TransferMode.MOVE);
      }

      event.consume();
    });

    setOnDragEntered(event -> {
      if (event.getGestureSource() != thisCell && event.getDragboard().hasContent(T_FORMAT)) {
        setOpacity(0.3);
      }
    });

    setOnDragExited(event -> {
      if (event.getGestureSource() != thisCell && event.getDragboard().hasContent(T_FORMAT)) {
        setOpacity(1);
      }
    });

    setOnDragDropped(event -> {
      if (getItem() == null) {
        return;
      }

      Dragboard db = event.getDragboard();
      boolean success = false;

      if (db.hasContent(T_FORMAT)) {
        T content = (T) db.getContent(T_FORMAT);
        ObservableList<T> items = getListView().getItems();
        int draggedIdx = items.indexOf(content);
        int thisIdx = items.indexOf(getItem());

        System.out.println("drag index: " + draggedIdx + ", this index: " + thisIdx);
        items.forEach(val -> System.out.println(val.hashCode()));

        items.set(draggedIdx, getItem());
        items.set(thisIdx, content);

        success = true;
      }

      event.setDropCompleted(success);
      event.consume();
    });

    setOnDragDone(DragEvent::consume);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DraggableCell<?> that = (DraggableCell<?>) o;

    return time == that.time;
  }

  @Override
  public int hashCode() {
    return (int) (time ^ (time >>> 32));
  }
}
