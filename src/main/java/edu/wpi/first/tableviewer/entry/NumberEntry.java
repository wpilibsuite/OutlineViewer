package edu.wpi.first.tableviewer.entry;

/**
 * An entry containing a single number.
 */
public class NumberEntry extends Entry<Number> {

  public NumberEntry(String key, Number value) {
    super(key, value);
  }

  @Override
  protected String getTypeString(Number value) {
    return "Number";
  }

}
