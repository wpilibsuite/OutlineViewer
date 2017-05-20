package edu.wpi.first.outlineviewer.model;

import com.google.common.collect.Lists;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Optional;
import java.util.Queue;

import static com.google.common.base.Preconditions.checkNotNull;

public class NetworkTableData {

  private NetworkTableData parent;
  private final ReadOnlyStringWrapper key;
  private final ReadOnlyStringWrapper type;
  private final ObservableMap<String, NetworkTableData> children;

  /**
   * Create a new NetworkTableData.
   *
   * @param key The key
   */
  public NetworkTableData(String key) {
    this(key, "");
  }

  /**
   * Create a new NetworkTableData.
   *
   * @param key The key
   * @param type The type
   */
  protected NetworkTableData(String key, String type) {
    checkNotNull(key);
    checkNotNull(type);

    this.key = new ReadOnlyStringWrapper(key);
    this.type = new ReadOnlyStringWrapper(type);
    children = FXCollections.observableHashMap();
  }

  public Property valueProperty() {
    return new SimpleStringProperty("");
  }

  public ReadOnlyStringProperty typeProperty() {
    return type.getReadOnlyProperty();
  }

  public ReadOnlyStringProperty keyProperty() {
    return key.getReadOnlyProperty();
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

  /**
   * Sees if the data within this structure is metadata (i.e. has a key
   * bookended by tildes ("~") and is in all caps). Used to show/hide metadata
   * leaves in branches.
   */
  public boolean isMetadata() {
    return isMetadata(key.get());
  }

  /**
   * Sees if the provided key is metadata (i.e. has a key
   * bookended by tildes ("~") and is in all caps). Used to show/hide metadata
   * leaves in branches.
   */
  public static boolean isMetadata(String key) {
    return key.matches("~[A-Z]*~");
  }

  /**
   * Create a new piece of NetworkTableData.
   *
   * @param key The key
   * @param value The value
   * @return The created data
   */
  public static NetworkTableData createNetworkTableData(String key, Object value) {
    checkNotNull(key);
    checkNotNull(value);

    if (isMetadata(key)) {
      return new NetworkTableData(key, "~METADATA~");
    } else if (value instanceof Boolean) {
      return new NetworkTableBoolean(key, (boolean) value);
    } else if (value instanceof Double) {
      return new NetworkTableNumber(key, (double) value);
    } else if (value instanceof String) {
      return new NetworkTableString(key, (String) value);
    }
    return new NetworkTableData(key, "ERROR");
  }
}
