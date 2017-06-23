package edu.wpi.first.tableviewer.component;

import edu.wpi.first.tableviewer.entry.Entry;
import edu.wpi.first.tableviewer.entry.TableEntry;
import edu.wpi.first.wpilibj.tables.ITable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A tree table view specifically for viewing network table entries. This is filterable
 * to allow metadata to be hidden and for entries to be searchable.
 */
public class NetworkTableTree extends FilterableTreeTable<Entry> {

  // node comparators
  private static final Comparator<TreeItem<Entry>> branchesFirst
      = (o1, o2) -> o1.isLeaf() ? (o2.isLeaf() ? 0 : 1) : -1;
  private static final Comparator<TreeItem<Entry>> alphabetical
      = Comparator.comparing(i -> i.getValue().getKey());

  /**
   * Creates a new network table tree. The tree is set up to show changes to network tables
   * in real time.
   */
  public NetworkTableTree() {
    super();
    setSortPolicy(param -> {
      if (getRealRoot() != null) {
        sort(getRealRoot());
        return true;
      }
      return false;
    });
  }

  /**
   * Updates this tree based on information about a specific entry in network tables. This is
   * intended to be called directly from a NetworkTablesJNI entry listener function. This method
   * is smart enough to always run on the JavaFX application thread.
   *
   * <p>Example use to update the tree whenever any entry updates:
   * <pre><code>
   * NetworkTablesJNI.addEntryListener(
   *       "",
   *       (uid, key, value, flags) -> updateFromNetworkTables(key, value, flags),
   *       0xFF);
   * </code></pre>
   *
   * @param key   the key of the entry that updated
   * @param value the new value of the update entry
   * @param flags the flags for the updated entry
   */
  @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "unchecked"})
  public void updateFromNetworkTables(String key, Object value, int flags) {
    if (!Platform.isFxApplicationThread()) {
      Platform.runLater(() -> updateFromNetworkTables(key, value, flags));
      return;
    }
    boolean deleted = (flags & ITable.NOTIFY_DELETE) != 0;
    List<String> pathElements = Stream.of(key.split("/"))
                                      .filter(s -> !s.isEmpty())
                                      .collect(Collectors.toList());
    TreeItem<Entry> current = getRealRoot();
    TreeItem<Entry> parent = current;
    StringBuilder path = new StringBuilder();
    for (int i = 0; i < pathElements.size(); i++) {
      String pathElement = pathElements.get(i);
      path.append('/').append(pathElement);
      parent = current;
      current = current.getChildren()
                       .stream()
                       .filter(item -> item.getValue().getKey().equals(path.toString()))
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
          current = new TreeItem<>(Entry.entryFor(key, value));
          parent.getChildren().add(current);
        } else {
          current.getValue().setValue(value);
        }
      } else if (current == null) {
        current = new TreeItem<>(new TableEntry(path.toString()));
        current.setExpanded(true);
        parent.getChildren().add(current);
      }
    }
    // Remove any empty subtables
    if (deleted) {
      for (TreeItem<Entry> item = parent; item != getRealRoot();) {
        if (item.getValue() instanceof TableEntry && item.getChildren().isEmpty()) {
          TreeItem<Entry> next = item.getParent();
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
  private void sort(TreeItem<Entry> node) {
    if (!node.isLeaf()) {
      boolean wasExpanded = node.isExpanded();
      FXCollections.sort(node.getChildren(), branchesFirst.thenComparing(alphabetical));
      node.getChildren().forEach(this::sort);
      node.setExpanded(wasExpanded);
    }
  }

}
