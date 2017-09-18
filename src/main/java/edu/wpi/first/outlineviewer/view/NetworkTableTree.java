package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.EntryNotification;
import edu.wpi.first.outlineviewer.NetworkTableUtils;
import edu.wpi.first.outlineviewer.model.TreeRow;
import edu.wpi.first.outlineviewer.model.TreeEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.outlineviewer.model.TreeTableEntry;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A tree table view specifically for viewing network table entries. This is filterable
 * to allow metadata to be hidden and for entries to be searchable.
 */
public class NetworkTableTree extends FilterableTreeTable<TreeRow>
    implements Consumer<EntryNotification> {

  // node comparators
  private static final Comparator<TreeItem<TreeRow>> BRANCHES_FIRST
      = (o1, o2) -> o1.isLeaf() ? (o2.isLeaf() ? 0 : 1) : -1;
  private static final Comparator<TreeItem<TreeRow>> ALPHABETICAL
      = Comparator.comparing(i -> i.getValue().getKey());

  private NetworkTable networkTable;
  private int listenerHandle = -1;

  /**
   * Creates a new network table tree. The tree is set up to show changes to network tables
   * in real time.
   */
  public NetworkTableTree() {
    this(NetworkTableUtils.getNetworkTableInstance());
  }

  /**
   * Creates a new network table tree. The tree is set up to show changes to network tables
   * in real time.
   */
  public NetworkTableTree(NetworkTableInstance networkTableInstance) {
    super();

    setNetworkTableInstance(networkTableInstance);

    setSortPolicy(param -> {
      if (getRealRoot() != null) {
        sort(getRealRoot());
        return true;
      }
      return false;
    });
  }

  public NetworkTable getNetworkTable() {
    return networkTable;
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
    networkTable = networkTableInstance.getTable("");
  }

  @Override
  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  public void accept(EntryNotification entryNotification) {
    if (!Platform.isFxApplicationThread()) {
      Platform.runLater(() -> accept(entryNotification));
      return;
    }

    //boolean deleted = (entryNotification.flags & EntryListenerFlags.kDelete) != 0;
    LinkedList<String> pathElements
        = Stream.of(entryNotification.getEntry().getName()
        .split(String.valueOf(NetworkTable.PATH_SEPARATOR)))
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toCollection(LinkedList::new));
    System.out.println("pathElements: " + Arrays.toString(pathElements.toArray()));

    String finalPathElement = pathElements.pollLast();

    TreeItem<TreeRow> current = getRealRoot();
    StringBuilder path = new StringBuilder();

    // Loop through every path element of the new value
    for (String node : pathElements) {
      // Check to see if the row already exists
      Optional<TreeItem<TreeRow>> next = current.getChildren()
          .stream()
          .filter(item -> item.getValue().getKey().equals(node))
          .findFirst();
      if (next.isPresent()) {
        System.out.println("Row exists! Getting row: " + node);
        // If it does exist, then get that row
        path.append(node).append(NetworkTable.PATH_SEPARATOR);
        current = next.get();
      } else {
        System.out.println("Creating row: " + node);

        // Otherwise, create a new row and add it to the tree.
        path.append(node).append(NetworkTable.PATH_SEPARATOR);
        TreeItem<TreeRow> newTable = new TreeItem<>(
            new TreeTableEntry(NetworkTableUtils.getRootTable().getSubTable(path.toString())));
        newTable.setExpanded(true);
        current.getChildren().add(newTable);
        current = newTable;
      }
    }

    Optional<TreeItem<TreeRow>> row = current.getChildren()
        .stream()
        .filter(item -> item.getValue().getKey().equals(finalPathElement))
        .findFirst();
    if ((entryNotification.flags & EntryListenerFlags.kDelete) != 0) {
      // Delete the value
      row.ifPresent(current.getChildren()::remove);
    } else if (row.isPresent()) {
      // Value already exists; update the value
      row.get().getValue().valueProperty().setValue(entryNotification.value);
    } else {
      System.out.println("New value: " + entryNotification.getEntry().getName());
      // New value
      current.getChildren().add(new TreeItem<>(new TreeEntry(entryNotification.getEntry())));
    }

//    TreeItem<TreeRow> current = getRealRoot();
//    TreeItem<TreeRow> parent = current;
//    StringBuilder path = new StringBuilder();
//
//    System.out.println("New element: " + Arrays.toString(pathElements.toArray()));
//    for (int i = 0; i < pathElements.size(); i++) {
//      String pathElement = pathElements.get(i);
//      path.append(NetworkTable.PATH_SEPARATOR).append(pathElement);
//      parent = current;
//      current = current.getChildren()
//          .stream()
//          .filter(item
//              -> NetworkTableUtils.normalize(item.getValue().getKey()).equals(path.toString()))
//          .findFirst()
//          .orElse(null);
//
//      if (deleted) {
//        if (current == null) {
//          break;
//        } else if (i == pathElements.size() - 1) {
//          // last
//          parent.getChildren().remove(current);
//        }
//      } else if (i == pathElements.size() - 1) {
//        if (current == null) {
//          current = new TreeItem<>(new TreeEntry(entryNotification.getEntry()));
//          parent.getChildren().add(current);
//        } else {
//          current.getValue().valueProperty().setValue(entryNotification.value);
//        }
//      } else if (current == null) {
//        current = new TreeItem<>(new TreeTableEntry(networkTable.getSubTable(path.toString())));
//        current.setExpanded(true);
//        parent.getChildren().add(current);
//      }
//    }

    // Remove any empty subtables
//    if (deleted) {
//      for (TreeItem<TreeRow> item = parent; item != getRealRoot();) {
//        if (!(item.getValue() instanceof TreeEntry) && item.getChildren().isEmpty()) {
//          TreeItem<TreeRow> next = item.getParent();
//          item.getParent().getChildren().remove(item);
//          item = next;
//        } else {
//          // No more empty tables, bail
//          break;
//        }
//      }
//    }

    sort();
    updateItemsFromFilter();
  }

  /**
   * Sorts tree nodes recursively in order of branches before leaves, then alphabetically.
   *
   * @param node the root node to sort
   */
  private void sort(TreeItem<TreeRow> node) {
    if (!node.isLeaf()) {
      boolean wasExpanded = node.isExpanded();
      FXCollections.sort(node.getChildren(), BRANCHES_FIRST.thenComparing(ALPHABETICAL));
      node.getChildren().forEach(this::sort);
      node.setExpanded(wasExpanded);
    }
  }
}
