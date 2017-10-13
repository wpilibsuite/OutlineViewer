package edu.wpi.first.outlineviewer;

import edu.wpi.first.networktables.NetworkTableEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class NetworkTableUtilitiesTest extends UtilityClassTest {

  NetworkTableUtilitiesTest() {
    super(NetworkTableUtilities.class);
  }

  @BeforeEach
  void createNewInstance() {
    NetworkTableUtilities.createNewNetworkTableInstance();
  }

  @Test
  void isRunningTest() {
    NetworkTableUtilities.setServer(9999);

    assertTrue(NetworkTableUtilities.isRunning(NetworkTableUtilities.getNetworkTableInstance()));
  }

  @Test
  void startingTest() {
    NetworkTableUtilities.setClient("localhost", 9999); // Should never connect
    assertTrue(NetworkTableUtilities.starting(NetworkTableUtilities.getNetworkTableInstance()));
  }

  @Test
  void testServer() {
    NetworkTableUtilities.setServer(9999);

    assertTrue(NetworkTableUtilities.isServer(NetworkTableUtilities.getNetworkTableInstance()));
  }

  @Test
  void testClient() {
    NetworkTableUtilities.setClient("localhost", 9999);

    assertTrue(NetworkTableUtilities.isClient(NetworkTableUtilities.getNetworkTableInstance()));
  }

  @Test
  void testTeamClient() {
    // TODO: This could be a better test if ntcore provided a way to access the IP list
    NetworkTableUtilities.setClient("190", 9999);

    assertTrue(NetworkTableUtilities.isClient(NetworkTableUtilities.getNetworkTableInstance()));
  }

  @Test
  void testDeleteKey() {
    final String key = "/NetworkTableUtilitiesTest::testDeleteKey";
    NetworkTableEntry entry = NetworkTableUtilities.getNetworkTableInstance().getEntry(key);
    entry.setString("dummy");

    NetworkTableUtilities.delete(key);
    assertFalse(entry.exists());
  }

  @Test
  void testDeleteSubtable() {
    NetworkTableEntry first = NetworkTableUtilities.getNetworkTableInstance().getEntry("/a/b/c");
    NetworkTableEntry second = NetworkTableUtilities.getNetworkTableInstance().getEntry("/a/b/d");
    NetworkTableEntry  third = NetworkTableUtilities.getNetworkTableInstance().getEntry("/a/c");
    first.setString("");
    second.setString("");
    third.setString("");

    NetworkTableUtilities.delete("/a/b");

    assertTrue(!first.exists() && !second.exists() && third.exists());
  }

  private static Stream<Arguments> concatArguments() {
    return Stream.of(
        Arguments.of("/foo/bar", "foo", "bar", new String[]{}),
        Arguments.of("/one/two/three/four", "one", "two", new String[]{"three", "four"}),
        Arguments.of("/one/two", "/////one////", "///two", new String[]{})
    );
  }

  @ParameterizedTest
  @MethodSource("concatArguments")
  void testConcat(String expectedResult, String arg0, String arg1, String... args) {
    assertEquals(expectedResult, NetworkTableUtilities.concat(arg0, arg1, args));
  }

  private static Stream<Arguments> normalizeArguments() {
    return Stream.of(
        Arguments.of("/", ""),
        Arguments.of("/", "//////////////////"),
        Arguments.of("/this/doesn't/need/to/be/normalized", "/this/doesn't/need/to/be/normalized"),
        Arguments.of("/no/leading/slash", "no/leading/slash"),
        Arguments.of("/what/an/awful/key/", "//////what////an/awful/////key///")
    );
  }

  @ParameterizedTest
  @MethodSource("normalizeArguments")
  void testNormalize(String expectedResult, String test) {
    assertEquals(expectedResult, NetworkTableUtilities.normalize(test));
  }

  private static Stream<Arguments> simpleKeyArguments() {
    return Stream.of(
        Arguments.of("simple", "simple"),
        Arguments.of("simple", "one/two/many/simple"),
        Arguments.of("simple", "//////an/////awful/key////simple"),
        Arguments.of("Root", ""),
        Arguments.of("Root", "/")
    );
  }

  @ParameterizedTest
  @MethodSource("simpleKeyArguments")
  void testSimpleKey(String expectedResult, String test) {
    assertEquals(expectedResult, NetworkTableUtilities.simpleKey(test));
  }
}
