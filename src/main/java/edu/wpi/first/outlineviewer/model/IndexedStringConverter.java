package edu.wpi.first.outlineviewer.model;

import javafx.util.Pair;
import javafx.util.StringConverter;

public abstract class IndexedStringConverter<T> extends StringConverter<Pair<Integer, T>> {

  /**
   * Converts the object provided into its string form.
   * Format of the returned string is defined by the specific converter.
   * @return a string representation of the object passed in.
   */
  public abstract String toString(Pair<Integer, T> object);

  /**
   * Converts the string provided into an object defined by the specific converter.
   * Format of the string and type of the resulting object is defined by the specific converter.
   * @return an object representation of the string passed in.
   */
  public abstract Pair<Integer, T> fromString(Integer index, String string);

}
