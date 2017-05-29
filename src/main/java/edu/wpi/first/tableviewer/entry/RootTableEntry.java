package edu.wpi.first.tableviewer.entry;

/**
 * An entry representing the root table. This has no value and the key is always "/".
 */
public class RootTableEntry extends TableEntry {

  public RootTableEntry() {
    super("/");
  }

}
