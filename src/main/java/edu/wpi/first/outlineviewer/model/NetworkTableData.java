package edu.wpi.first.outlineviewer.model;

import com.google.common.collect.Lists;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

import static com.google.common.base.Preconditions.checkNotNull;

public class NetworkTableData {

  private NetworkTableData parent;
  private final SimpleStringProperty key;
  private final ObservableMap<String, NetworkTableData> children;

  /**
   * Create a new NetworkTableData.
   *
   * @param key The key
   */
  public NetworkTableData(String key) {
    checkNotNull(key);

    this.key = new SimpleStringProperty(key);
    children = FXCollections.observableHashMap();
  }

  public Property valueProperty() {
    return new SimpleStringProperty("");
  }

  public SimpleStringProperty keyProperty() {
    return key;
  }

  /**
   * Add a child.
   *
   * @param child The child to add.
   */
  public void addChild(NetworkTableData child) {
    checkNotNull(child);

    children.put(child.keyProperty().get(), child);
    child.setParent(this);
  }

  private void setParent(NetworkTableData parent) {
    this.parent = parent;
  }

  /**
   * Remove this piece of data from the tree.  If its parent does not have anymore children, remove
   * the parent.
   */
  public void remove() {
    if (parent != null) {
      parent.getChildren().remove(key.get());
      if (parent.getChildren().isEmpty()) {
        parent.remove();
      }
    }
  }

  public ObservableMap<String, NetworkTableData> getChildren() {
    return children;
  }

  /**
   * Walks a queue of keys to get a child.
   *
   * @param keys The keys to walk.
   * @return The child
   */
  public Optional<NetworkTableData> getChild(String... keys) {
    return getChild(Lists.newLinkedList(Lists.newArrayList(keys)));
  }

  /**
   * Walks a queue of keys to get a child.
   *
   * @param keys The keys to walk.
   * @return The child
   */
  public Optional<NetworkTableData> getChild(Queue<String> keys) {
    if (keys.size() > 1 && children.containsKey(keys.peek())) {
      return children.get(keys.poll()).getChild(keys);
    }
    return Optional.ofNullable(children.get(keys.poll()));
  }
}
