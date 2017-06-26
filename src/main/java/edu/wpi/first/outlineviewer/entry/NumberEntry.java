package edu.wpi.first.outlineviewer.entry;

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

  @Override
  public String getDisplayString() {
    double value = getValue().doubleValue();
    if (value == (int) value) {
      return Integer.toString((int) value);
    } else {
      return Double.toString(value);
    }
  }

}
