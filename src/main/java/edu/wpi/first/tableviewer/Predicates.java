package edu.wpi.first.tableviewer;

import java.util.function.Predicate;

/**
 * Utility class for {@link Predicate}.
 */
public class Predicates {

  private static final Predicate ALWAYS = x -> true;
  private static final Predicate NEVER = x -> false;

  /**
   * A predicate that always test true for any input.
   *
   * @param <T> the type of data to match
   * @return a predicate that will always {@link Predicate#test(Object) test} true
   */
  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> always() {
    return ALWAYS;
  }

  /**
   * A predicate that always tests false for any input.
   *
   * @param <T> the type of data to match
   * @return a predicate that will always {@link Predicate#test(Object) test} false
   */
  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> never() {
    return NEVER;
  }

}
