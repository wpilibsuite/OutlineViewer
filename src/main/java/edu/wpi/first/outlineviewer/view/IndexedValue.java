package edu.wpi.first.outlineviewer.view;

import java.io.Serializable;
import javafx.util.Pair;

/**
 * Represents a value with an index. Essentially a wrapper for a Pair of an Integer and a T, so that
 * data in a ListView can have its index attached to it. This means that the hashcodes for two
 * otherwise identical ListView elements are different, because their indices are different.
 * @param <T> Data storage type
 */
public class IndexedValue<T> implements Serializable {
  private Pair<Integer, T> data;

  public IndexedValue(T val) {
    this(0, val);
  }

  public IndexedValue(Integer index, T val) {
    data = new Pair<>(index, val);
  }

  public IndexedValue(Pair<Integer, T> data) {
    this.data = data;
  }

  public Pair<Integer, T> getData() {
    return data;
  }

  public Integer getIndex() {
    return data.getKey();
  }

  public T getValue() {
    return data.getValue();
  }

  public void setIndex(Integer index) {
    data = new Pair<>(index, data.getValue());
  }

  public void setValue(T val) {
    data = new Pair<>(data.getKey(), val);
  }

  public void setData(Pair<Integer, T> data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return data.toString();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    IndexedValue<?> that = (IndexedValue<?>) other;

    return data == null ? that.data == null : data.equals(that.data);
  }

  @Override
  public int hashCode() {
    return data == null ? 0 : data.hashCode();
  }
}
