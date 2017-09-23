package edu.wpi.first.outlineviewer;

import edu.wpi.first.networktables.NetworkTableEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class NetworkTableUtilitiesTest {

  public static class MainTest extends UtilityClassTest {

    public MainTest() {
      super(NetworkTableUtilities.class);
    }

    @Before
    public void createNewInstance() {
      NetworkTableUtilities.createNewNetworkTableInstance();
    }

    @Test
    public void isRunningTest() {
      NetworkTableUtilities.setServer(9999);

      assertTrue(NetworkTableUtilities.isRunning());
    }

    @Test
    public void startingTest() {
      NetworkTableUtilities.setClient("localhost", 9999); // Should never connect
      assertTrue(NetworkTableUtilities.starting());
    }

    @Test
    public void testServer() {
      NetworkTableUtilities.setServer(9999);

      assertTrue(NetworkTableUtilities.isServer());
    }

    @Test
    public void testClient() {
      NetworkTableUtilities.setClient("localhost", 9999);

      assertTrue(NetworkTableUtilities.isClient());
    }

    @Test
    public void testTeamClient() {
      // TODO: This could be a better test if ntcore provided a way to access the IP list
      NetworkTableUtilities.setClient("190", 9999);

      assertTrue(NetworkTableUtilities.isClient());
    }

    @Test
    public void testDeleteKey() {
      final String key = "/NetworkTableUtilitiesTest::testDeleteKey";
      NetworkTableEntry entry = NetworkTableUtilities.getNetworkTableInstance()
          .getEntry(key);
      entry.setString("dummy");

      NetworkTableUtilities.delete(key);
      assertFalse(entry.exists());
    }

    @Test
    public void testDeleteSubtable() {
      NetworkTableEntry first = NetworkTableUtilities.getNetworkTableInstance().getEntry("/a/b/c");
      NetworkTableEntry second = NetworkTableUtilities.getNetworkTableInstance().getEntry("/a/b/d");
      NetworkTableEntry  third = NetworkTableUtilities.getNetworkTableInstance().getEntry("/a/c");
      first.setString("");
      second.setString("");
      third.setString("");

      NetworkTableUtilities.delete("/a/b");

      assertTrue(!first.exists() && !second.exists() && third.exists());
    }
  }

  @RunWith(Parameterized.class)
  public static class NetworkTableUtilitiesConcatTest {

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

    public NetworkTableUtilitiesConcatTest(String expectedResult, String test1, String test2,
                                           String... test3) {
      this.expectedResult = expectedResult;
      this.test1 = test1;
      this.test2 = test2;
      this.test3 = test3;
    }

    @Test
    public void testConcat() {
      assertEquals(expectedResult, NetworkTableUtilities.concat(test1, test2, test3));
    }
  }

  @RunWith(Parameterized.class)
  public static class NetworkTableUtilitiesNormalizeTest {

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

    public NetworkTableUtilitiesNormalizeTest(String expectedResult, String test) {
      this.expectedResult = expectedResult;
      this.test = test;
    }

    @Test
    public void testNormalize() {
      assertEquals(expectedResult, NetworkTableUtilities.normalize(test));
    }
  }

  @RunWith(Parameterized.class)
  public static class NetworkTableUtilitiesSimpleKeyTest {

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

    public NetworkTableUtilitiesSimpleKeyTest(String expectedResult, String test) {
      this.expectedResult = expectedResult;
      this.test = test;
    }

    @Test
    public void testSimpleKey() {
      assertEquals(expectedResult, NetworkTableUtilities.simpleKey(test));
    }
  }
}
