package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.EntryNotification;
import edu.wpi.first.outlineviewer.NetworkTableUtilities;
import edu.wpi.first.outlineviewer.TypeUtilities;
import edu.wpi.first.outlineviewer.model.TreeRow;
import edu.wpi.first.outlineviewer.model.TreeEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.outlineviewer.model.NetworkTableTreeRow;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A tree table view specifically for viewing network table entries. This is filterable
 * to allow metadata to be hidden and for entries to be searchable.
 */
public class NetworkTableTree extends TreeTableView<TreeRow>
    implements Consumer<EntryNotification> {

  private int listenerHandle = -1;

  /**
   * Creates a new network table tree. The tree is set up to show changes to network tables
   * in real time.
   */
  public NetworkTableTree() {
    this(NetworkTableUtilities.getNetworkTableInstance());
  }

  /**
   * Creates a new network table tree. The tree is set up to show changes to network tables
   * in real time.
   */
  public NetworkTableTree(NetworkTableInstance networkTableInstance) {
    super();

    setNetworkTableInstance(networkTableInstance);
  }

  /**
   * Set the NetworkTableInstance this table represents.
   *
   * @param networkTableInstance The instance
   */
  public final void setNetworkTableInstance(NetworkTableInstance networkTableInstance) {
    Objects.requireNonNull(networkTableInstance);

    if (listenerHandle != -1) {
      networkTableInstance.removeEntryListener(listenerHandle);
    }
    listenerHandle = networkTableInstance.addEntryListener("", this, 0xFF);
  }

  @Override
  @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.ConfusingTernary"})
  public void accept(EntryNotification entryNotification) {
    if (!Platform.isFxApplicationThread()) {
      Platform.runLater(() -> accept(entryNotification));
      return;
    }

    LinkedList<String> pathElements
        = Stream.of(entryNotification.getEntry().getName()
        .split(String.valueOf(NetworkTable.PATH_SEPARATOR)))
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toCollection(LinkedList::new));

    String finalPathElement = pathElements.pollLast();

    //TreeItem<TreeRow> current = getRealRoot();
    TreeItem<TreeRow> current = getRoot();
    StringBuilder path = new StringBuilder();
    path.append(NetworkTable.PATH_SEPARATOR);

    // Loop through every path element of the new value
    for (String node : pathElements) {
      path.append(node).append(NetworkTable.PATH_SEPARATOR);
      // Check to see if the row already exists
      Optional<TreeItem<TreeRow>> next = current.getChildren()
          .stream()
          .filter(item -> NetworkTableUtilities.normalize(item.getValue().getKey())
              .equals(path.toString()))
          .findFirst();
      if (next.isPresent()) {
        // If it does exist, then get that row
        current = next.get();
      } else {
        // Otherwise, create a new row and add it to the tree.
        TreeItem<TreeRow> newTable = new TreeItem<>(
            new NetworkTableTreeRow(NetworkTableUtilities.getNetworkTableInstance()
                .getTable(path.toString())));
        newTable.setExpanded(true);
        current.getChildren().add(newTable);
        current = newTable;
      }
    }

    path.append(finalPathElement);
    Optional<TreeItem<TreeRow>> row = current.getChildren().stream()
        .filter(item -> path.toString()
            .equals(NetworkTableUtilities.normalize(item.getValue().getKey())))
        .findFirst();
    if ((entryNotification.flags & EntryListenerFlags.kDelete) != 0) {
      // Delete the value
      if (row.isPresent()) {
        current.getChildren().remove(row.get());
      }
    } else if (row.isPresent()) {
      // Value already exists; update the value
      TypeUtilities.optionalCast(row.get().getValue(), TreeEntry.class).ifPresent(entry
          -> entry.networkTableEntryProperty().setValue(entryNotification.getEntry()));
    } else {
      // New value
      current.getChildren().add(new TreeItem<>(new TreeEntry(entryNotification.getEntry())));
    }

    sort();
  }
}
