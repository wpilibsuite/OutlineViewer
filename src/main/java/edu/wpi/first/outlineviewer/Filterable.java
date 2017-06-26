package edu.wpi.first.outlineviewer;

import java.util.function.Predicate;

/**
 * Represents an object with contents that can be filtered.
 */
public interface Filterable<T> {

  /**
   * Sets the filter to use. A value of {@code null} means no filter is applied to this objects
   * contents.
   *
   * @param filter the filter to use
   */
  void setFilter(Predicate<T> filter);

  /**
   * Gets the current filter.
   */
  Predicate<T> getFilter();

}
