package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.NetworkTableUtils;
import edu.wpi.first.outlineviewer.model.TableEntry;
import edu.wpi.first.outlineviewer.model.TableValueEntry;
import edu.wpi.first.wpilibj.networktables.EntryListener;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.networktables.NetworkTableValue;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A tree table view specifically for viewing network table entries. This is filterable
 * to allow metadata to be hidden and for entries to be searchable.
 */
public class NetworkTableTree extends FilterableTreeTable<TableEntry> implements EntryListener {

  // node comparators
  private static final Comparator<TreeItem<TableEntry>> BRANCHES_FIRST
      = (o1, o2) -> o1.isLeaf() ? (o2.isLeaf() ? 0 : 1) : -1;
  private static final Comparator<TreeItem<TableEntry>> ALPHABETICAL
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
  public void valueChanged(NetworkTableEntry entry, NetworkTableValue value, int flags) {
    if (!Platform.isFxApplicationThread()) {
      Platform.runLater(() -> valueChanged(entry, value, flags));
      return;
    }

    boolean deleted = (flags & NetworkTable.NOTIFY_DELETE) != 0;
    List<String> pathElements
        = Stream.of(entry.getName().split(String.valueOf(NetworkTable.PATH_SEPARATOR)))
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());

    TreeItem<TableEntry> current = getRealRoot();
    TreeItem<TableEntry> parent = current;
    StringBuilder path = new StringBuilder();

    for (int i = 0; i < pathElements.size(); i++) {
      String pathElement = pathElements.get(i);
      path.append(NetworkTable.PATH_SEPARATOR).append(pathElement);
      parent = current;
      current = current.getChildren()
          .stream()
          .filter(item
              -> NetworkTableUtils.normalize(item.getValue().getKey()).equals(path.toString()))
          .findFirst()
          .orElse(null);

      if (deleted) {
        if (current == null) {
          break;
        } else if (i == pathElements.size() - 1) {
          // last
          parent.getChildren().remove(current);
        }
      } else if (i == pathElements.size() - 1) {
        if (current == null) {
          current = new TreeItem<>(new TableValueEntry(entry, value));
          parent.getChildren().add(current);
        } else {
          current.getValue().valueProperty().setValue(value);
        }
      } else if (current == null) {
        current = new TreeItem<>(new TableEntry(path.toString()));
        current.setExpanded(true);
        parent.getChildren().add(current);
      }
    }

    // Remove any empty subtables
    if (deleted) {
      for (TreeItem<TableEntry> item = parent; item != getRealRoot();) {
        if (!(item.getValue() instanceof TableValueEntry) && item.getChildren().isEmpty()) {
          TreeItem<TableEntry> next = item.getParent();
          item.getParent().getChildren().remove(item);
          item = next;
        } else {
          // No more empty tables, bail
          break;
        }
      }
    }

    sort();
    updateItemsFromFilter();
  }

  /**
   * Sorts tree nodes recursively in order of branches before leaves, then alphabetically.
   *
   * @param node the root node to sort
   */
  private void sort(TreeItem<TableEntry> node) {
    if (!node.isLeaf()) {
      boolean wasExpanded = node.isExpanded();
      FXCollections.sort(node.getChildren(), BRANCHES_FIRST.thenComparing(ALPHABETICAL));
      node.getChildren().forEach(this::sort);
      node.setExpanded(wasExpanded);
    }
  }
}
