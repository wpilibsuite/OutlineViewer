package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.Filterable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

import java.util.function.Predicate;

/**
 * A TableTreeView that allows its displayed content to be filtered according to an arbitrary
 * {@link #setFilter(Predicate) predicate}. The actual content is not modified in any way; only the
 * root item is changed when a filter is in effect. The true root item may be retrieved at any time
 * with {@link #getRealRoot()}.
 *
 * @param <T> the type of items in the tree table
 */
public class FilterableTreeTable<T> extends TreeTableView<T> implements Filterable<T> {

  private final ObjectProperty<Predicate<T>> filter =
      new SimpleObjectProperty<>(this, "filter", null);

  private TreeItem<T> realRoot;

  /**
   * Creates a new filterable tree table.
   */
  public FilterableTreeTable() {
    rootProperty().addListener((__, oldRoot, newRoot) -> {
      if (getFilter() == null || this.realRoot == null) {
        realRoot = newRoot;
      }
    });
    filter.addListener(__ -> updateItemsFromFilter());
  }

  /**
   * Gets the real root of the view. The real root is considered to have changed whenever
   * {@link #setRoot(TreeItem) setRoot} is called as long as there is no filter, or if the real
   * root was not previously set.
   */
  public TreeItem<T> getRealRoot() {
    return realRoot;
  }

  /**
   * Updates the items in this tree to only show items that pass the current filter.
   */
  public final void updateItemsFromFilter() {
    if (realRoot == null) {
      // no items
      return;
    }
    TreeItem<T> root = realRoot;
    TreeItem<T> filteredRoot = new TreeItem<>(root.getValue(), root.getGraphic());
    filteredRoot.expandedProperty().bindBidirectional(root.expandedProperty());
    setRoot(filter(root, getFilter(), filteredRoot));
  }

  /**
   * Recursively filters all children of {@code allRoot} matching the given predicate and adding
   * them to {@code filteredRoot}.
   *
   * @param allRoot the root item to filter on
   * @param filter the filter to use. Any item matching this predicate will be added as a child to
   *               {@code filteredRoot}. An item is considered to have passed this predicate iff
   *               {@code filter.test(item)} returns true, or if {@code filter.test(item)} returns
   *               true for any of its descendants.
   * @param filteredRoot the root item to add filtered items to
   * @param <T> the type of model to filter on
   * @return the root item of the filtered tree
   */
  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  private static <T> TreeItem<T> filter(TreeItem<T> allRoot,
                                        Predicate<T> filter,
                                        TreeItem<T> filteredRoot) {
    if (filter == null) {
      // no filter
      return allRoot;
    }
    for (TreeItem<T> child : allRoot.getChildren()) {
      // Copy the child to avoid mucking with the real tree
      TreeItem<T> filteredChild = new TreeItem<>();
      filteredChild.setValue(child.getValue());
      filteredChild.expandedProperty().bindBidirectional(child.expandedProperty());
      // Recurse; filter children
      filteredChild = filter(child, filter, filteredChild);
      if (matches(child, filter)) {
        filteredRoot.getChildren().add(filteredChild);
      }
    }
    return filteredRoot;
  }

  private static <T> boolean matches(TreeItem<T> treeItem, Predicate<T> predicate) {
    if (treeItem.isLeaf()) {
      return predicate.test(treeItem.getValue());
    } else {
      return treeItem.getChildren().stream().anyMatch(c -> matches(c, predicate));
    }
  }

  @Override
  public void setFilter(Predicate<T> filter) {
    this.filter.set(filter);
  }

  @Override
  public final Predicate<T> getFilter() {
    return filter.get();
  }

}
