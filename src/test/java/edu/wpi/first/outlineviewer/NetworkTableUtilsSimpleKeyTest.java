package edu.wpi.first.outlineviewer;

import edu.wpi.first.outlineviewer.model.TableEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class NetworkTableUtilsSimpleKeyTest {

  private final String expectedResult;
  private final String test;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {"simple", "simple"},
        {"simple", "one/two/many/simple"},
        {"simple", "//////an/////awful/key////simple"},
        {"Root", ""},
        {"Root", "/"}
    });
  }

  public NetworkTableUtilsSimpleKeyTest(String expectedResult, String test) {
    this.expectedResult = expectedResult;
    this.test = test;
  }

  @Test
  public void testSimpleKey() {
    assertEquals(expectedResult, NetworkTableUtils.simpleKey(test));
  }
}
