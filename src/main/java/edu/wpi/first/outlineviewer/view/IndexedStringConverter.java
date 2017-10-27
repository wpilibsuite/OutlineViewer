package edu.wpi.first.outlineviewer.view;

import javafx.util.StringConverter;

/**
 * A StringConverter that also requires an index to convert from a String to a T. This class works
 * hand-in-hand with IndexedValue.
 * @param <T> Data conversion type
 */
public abstract class IndexedStringConverter<T> extends StringConverter<IndexedValue<T>> {

  /**
   * Converts the object provided into its string form.
   * Format of the returned string is defined by the specific converter.
   * @return a string representation of the object passed in.
   */
  public abstract String toString(IndexedValue<T> object);

  /**
   * Converts the string provided into an object defined by the specific converter.
   * Format of the string and type of the resulting object is defined by the specific converter.
   * @return an object representation of the string passed in.
   */
  public abstract IndexedValue<T> fromString(Integer index, String string);

}
