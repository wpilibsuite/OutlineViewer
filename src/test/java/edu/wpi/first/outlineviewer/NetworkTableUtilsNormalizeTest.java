package edu.wpi.first.outlineviewer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class NetworkTableUtilsNormalizeTest {

  private final String expectedResult;
  private final String test;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {"/", ""},
        {"/", "//////////////////"},
        {"/this/doesn't/need/to/be/normalized", "/this/doesn't/need/to/be/normalized"},
        {"/no/leading/slash", "no/leading/slash"},
        {"/what/an/awful/key/", "//////what////an/awful/////key///"}
    });
  }

  public NetworkTableUtilsNormalizeTest(String expectedResult, String test) {
    this.expectedResult = expectedResult;
    this.test = test;
  }

  @Test
  public void testNormalize() {
    assertEquals(expectedResult, NetworkTableUtils.normalize(test));
  }
}
