package edu.wpi.first.tableviewer.component;

import edu.wpi.first.tableviewer.Filterable;
import edu.wpi.first.tableviewer.entry.Entry;
import edu.wpi.first.tableviewer.entry.TableEntry;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.tables.ITable;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A tree table view specifically for viewing network table entries. This is filterable
 * to allow metadata to be hidden and for entries to be searchable.
 */
public class NetworkTableTree extends TreeTableView<Entry> implements Filterable<Entry> {

  private final ObjectProperty<Predicate<Entry>> filter
      = new SimpleObjectProperty<>(this, "filter", null);

  // node comparators
  private static final Comparator<TreeItem<Entry>> branchesFirst
      = (o1, o2) -> o1.isLeaf() ? (o2.isLeaf() ? 0 : 1) : -1;
  private static final Comparator<TreeItem<Entry>> alphabetical
      = Comparator.comparing(i -> i.getValue().getKey());

  private TreeItem<Entry> realRoot = null;

  /**
   * Creates a new network table tree. The tree is set up to show changes to network tables
   * in real time.
   */
  public NetworkTableTree() {
    super();
    rootProperty().addListener(__ -> {
      if (getFilter() == null || realRoot == null) {
        realRoot = getRoot();
      }
    });
    filter.addListener(__ -> updateItemsFromFilter());
    setSortPolicy(param -> {
      if (realRoot != null) {
        sort(realRoot);
        return true;
      }
      return false;
    });
    NetworkTablesJNI
        .addEntryListener("", (uid, key, value, flags) -> makeBranches(key, value, flags), 0xFF);
  }

  /**
   * Updates the items in this tree to only show items that pass the filter.
   */
  public void updateItemsFromFilter() {
    if (realRoot == null) {
      // no items
      return;
    }
    TreeItem<Entry> root = realRoot;
    TreeItem<Entry> filteredRoot = new TreeItem<>(root.getValue(), root.getGraphic());
    filteredRoot.setExpanded(root.isExpanded());
    setRoot(filter(root, getFilter(), filteredRoot));
  }

  private static <T> TreeItem<T> filter(TreeItem<T> allRoot,
                                        Predicate<? super T> filter,
                                        TreeItem<T> filteredRoot) {
    if (filter == null) {
      // no filter
      return allRoot;
    }
    for (TreeItem<T> child : allRoot.getChildren()) {
      // Copy the child to avoid mucking with the real tree
      TreeItem<T> filteredChild = new TreeItem<>();
      filteredChild.setValue(child.getValue());
      filteredChild.setExpanded(child.isExpanded());
      // Recurse; filter children
      filteredChild = filter(child, filter, filteredChild);
      if (matches(child, filter)) {
        filteredRoot.getChildren().add(filteredChild);
      }
    }
    return filteredRoot;
  }

  private static <T> boolean matches(TreeItem<T> treeItem, Predicate<? super T> predicate) {
    if (treeItem.isLeaf()) {
      return predicate.test(treeItem.getValue());
    } else {
      return treeItem.getChildren().stream().anyMatch(c -> matches(c, predicate));
    }
  }

  @Override
  public void setFilter(Predicate<Entry> filter) {
    this.filter.setValue(filter);
  }

  @Override
  public Predicate<Entry> getFilter() {
    return filter.getValue();
  }

  @SuppressWarnings("unchecked")
  private void makeBranches(String key, Object value, int flags) {
    if (!Platform.isFxApplicationThread()) {
      Platform.runLater(() -> makeBranches(key, value, flags));
      return;
    }
    boolean deleted = (flags & ITable.NOTIFY_DELETE) != 0;
    List<String> pathElements = Stream.of(key.split("/"))
                                      .filter(s -> !s.isEmpty())
                                      .collect(Collectors.toList());
    TreeItem<Entry> current = realRoot;
    TreeItem<Entry> parent;
    StringBuilder path = new StringBuilder();
    for (int i = 0; i < pathElements.size(); i++) {
      String pathElement = pathElements.get(i);
      path.append("/").append(pathElement);
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
