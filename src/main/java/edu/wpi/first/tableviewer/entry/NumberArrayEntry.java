package edu.wpi.first.tableviewer.entry;

import java.util.Arrays;

/**
 * An entry containing an array of numbers.
 */
public class NumberArrayEntry extends Entry<double[]> {

  @SuppressWarnings("PMD.UseVarargs")
  public NumberArrayEntry(String key, double[] value) {
    super(key, value);
  }

  @Override
  @SuppressWarnings("PMD.UseVarargs")
  protected String getTypeString(double[] value) {
    return String.format("Number[%d]", value.length);
  }

  @Override
  public String getDisplayString() {
    return Arrays.toString(getValue());
  }
}
