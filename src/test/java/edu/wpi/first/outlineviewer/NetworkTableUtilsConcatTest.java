package edu.wpi.first.outlineviewer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class NetworkTableUtilsConcatTest {

  private final String expectedResult;
  private final String test1;
  private final String test2;
  private final String[] test3;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {"/foo/bar", "foo", "bar", new String[]{}},
        {"/one/two/three/four", "one", "two", new String[]{"three", "four"}},
        {"/one/two", "/////one////", "///two", new String[]{}}
    });
  }

  public NetworkTableUtilsConcatTest(String expectedResult, String test1, String test2,
                                     String... test3) {
    this.expectedResult = expectedResult;
    this.test1 = test1;
    this.test2 = test2;
    this.test3 = test3;
  }

  @Test
  public void testConcat() {
    assertEquals(expectedResult, NetworkTableUtils.concat(test1, test2, test3));
  }
}
