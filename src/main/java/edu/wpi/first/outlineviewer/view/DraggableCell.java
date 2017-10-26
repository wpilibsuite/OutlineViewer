package edu.wpi.first.outlineviewer.view;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DraggableCell<T> extends ListCell<T> {

  private static final DataFormat T_FORMAT = new DataFormat("GENERIC");

  public DraggableCell() {
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
        List<T> items = new ArrayList<>(getListView().getItems());
        int draggedIdx = items.indexOf(content);
        int thisIdx = items.indexOf(getItem());

        items.remove(draggedIdx);
        items.add(thisIdx, content);
        getListView().setItems(FXCollections.observableList(items));

        success = true;
      }

      event.setDropCompleted(success);
      event.consume();
    });

    setOnDragDone(DragEvent::consume);
  }

}
