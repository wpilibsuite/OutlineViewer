package edu.wpi.first.outlineviewer;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class TableEntry {

  protected final ReadOnlyStringWrapper key;
  protected final SimpleObjectProperty value;
  protected final SimpleStringProperty type;

  private final TreeItem<TableEntry> treeItem;

  private static final char PATH_SEPARATOR = '/';

  /**
   * Create a new TableEntry.
   *
   * @param key The NetworkTable key associated with this entry
   * @param value The NetworkTable value associated with this entry
   * @param type The NetworkTable type associated with this entry
   */
  public TableEntry(final String key,
                    final Object value,
                    final String type) {
    checkNotNull(key, "A key must be provided to create a TableEntry");
    checkNotNull(value, "An value must be provided to create a TableEntry");
    checkNotNull(type, "A type must be provided to create a TableEntry");

    this.key = new ReadOnlyStringWrapper(key);
    this.value = new SimpleObjectProperty(value);
    this.type = new SimpleStringProperty(type);

    treeItem = new TreeItem<>(this);
  }

  public SimpleStringProperty getKey() {
    return key;
  }

  public SimpleObjectProperty getValue() {
    return value;
  }

  public SimpleStringProperty getType() {
    return type;
  }

  public TreeItem<TableEntry> getTreeItem() {
    return treeItem;
  }

  /**
   * Gets a String representing the NetworkTable path to this entry.
   */
  public String getNetworkTablePath() {
    if (isRoot()) {
      return "";
    }
    return getTreeItem().getParent().getValue().getNetworkTablePath() + PATH_SEPARATOR
        + getKey().getValue();
  }

  public boolean isRoot() {
    return getTreeItem().getParent() == null;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "\t" + getKey().getValue();
  }

  /**
   * Sees if the data within this structure is metadata (i.e. has a key
   * bookended by tildes ("~") and is in all caps). Used to show/hide metadata
   * leaves in branches.
   */
  public boolean isMetadata() {
    return isMetadata(key.getValue());
  }


  /**
   * Checks if the provided key indicates the value is metadata i.e. has a key
   * bookended by tildes ("~") and is in all caps). Used to show/hide metadata
   * leaves in branches.
   *
   * @param key The key to check
   * @return If the key represents metadata
   */
  public static boolean isMetadata(final String key) {
    return key.startsWith("~")
        && key.endsWith("~")
        && key.toUpperCase().equals(key);
  }
}
