package edu.wpi.first.outlineviewer;

import org.junit.After;
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
public class NetworkTableUtilsTest {

  public static class MainTest extends UtilityClassTest {

    public MainTest() {
      super(NetworkTableUtils.class);
    }

    @Before
    @After
    public void createNewInstance() {
      NetworkTableUtils.createNewNetworkTableInstance();
    }

    @Test
    public void isRunningTest() {
      NetworkTableUtils.setServer(9999);

      assertTrue(NetworkTableUtils.isRunning());
    }

    @Test
    public void isNotRunningTest() {
      NetworkTableUtils.setServer(9999);
      NetworkTableUtils.shutdown();

      assertFalse(NetworkTableUtils.isRunning());
    }

    @Test
    public void startingTest() {
      NetworkTableUtils.setClient("localhost", 9999); // Should never connect
      assertTrue(NetworkTableUtils.starting());
    }

    @Test
    public void testServer() {
      NetworkTableUtils.setServer(9999);

      assertTrue(NetworkTableUtils.isServer());
    }

    @Test
    public void testClient() {
      NetworkTableUtils.setClient("localhost", 9999);

      assertTrue(NetworkTableUtils.isClient());
    }

    @Test
    public void testTeamClient() {
      // TODO: This could be a better test if ntcore provided a way to access the IP list
      NetworkTableUtils.setClient("190", 9999);

      assertTrue(NetworkTableUtils.isClient());
    }

    @Test
    public void testDeleteKey() {
      final String key = "/NetworkTableUtilsTest::testDeleteKey";
      NetworkTableUtils.getRootTable().getEntry(key).setString("dummy");

      NetworkTableUtils.delete(key);
      assertFalse(NetworkTableUtils.getRootTable().containsKey(key));
    }

    @Test
    public void testDeleteSubtable() {
      String first = "/a/b/c";
      String second = "/a/b/d";
      String third = "/a/c";
      NetworkTableUtils.getRootTable().getEntry(first).setString("");
      NetworkTableUtils.getRootTable().getEntry(second).setString("");
      NetworkTableUtils.getRootTable().getEntry(third).setString("");

      NetworkTableUtils.delete("/a/b");
      assertTrue(!NetworkTableUtils.getRootTable().containsKey(first)
          && !NetworkTableUtils.getRootTable().containsKey(second)
          && NetworkTableUtils.getRootTable().containsKey(third));
    }
  }

  @RunWith(Parameterized.class)
  public static class NetworkTableUtilsConcatTest {

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

  @RunWith(Parameterized.class)
  public static class NetworkTableUtilsNormalizeTest {

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

  @RunWith(Parameterized.class)
  public static class NetworkTableUtilsSimpleKeyTest {

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
}
