package edu.wpi.first.outlineviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import org.junit.Test;

import static edu.wpi.first.outlineviewer.NetworkTableUtils.concat;
import static edu.wpi.first.outlineviewer.NetworkTableUtils.delete;
import static edu.wpi.first.outlineviewer.NetworkTableUtils.normalize;
import static edu.wpi.first.outlineviewer.NetworkTableUtils.simpleKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NetworkTableUtilsTest {

  @Test
  public void testNormalizeEmpty() {
    assertEquals("/", normalize(""));
  }

  @Test
  public void testNormalizeAllSlashes() {
    assertEquals("/", normalize("//////////////////"));
  }

  @Test
  public void testNormalizeNOP() {
    final String ok = "/this/doesn't/need/to/be/normalized";
    assertEquals(ok, normalize(ok));
  }

  @Test
  public void testNormalizeAddLeadingSlash() {
    final String key = "no/leading/slash";
    assertEquals("/" + key, normalize(key));
  }

  @Test
  public void testNormalizeAwfulString() {
    final String awful = "//////what////an/awful/////key///";
    assertEquals("/what/an/awful/key/", normalize(awful));
  }

  @Test
  public void testConcatTwo() {
    assertEquals("/foo/bar", concat("foo", "bar"));
  }

  @Test
  public void testConcatMany() {
    assertEquals("/one/two/three/four", concat("one", "two", "three", "four"));
  }

  @Test
  public void testConcatWithSlashes() {
    assertEquals("/one/two", concat("/////one////", "///two"));
  }

  @Test
  public void testSimpleKeySimple() {
    assertEquals("simple", simpleKey("simple"));
  }

  @Test
  public void testSimpleKeyComplex() {
    assertEquals("simple", simpleKey("one/two/many/simple"));
  }

  @Test
  public void testSimpleKeyAwful() {
    assertEquals("simple", simpleKey("//////an/////awful/key////simple"));
  }

  @Test
  public void testDeleteKey() {
    final String key = "/NetworkTableUtilsTest::testDeleteKey";
    NetworkTablesJNI.putString(key, "dummy");

    delete(key);
    assertFalse(NetworkTablesJNI.containsKey(key));
  }

  @Test
  public void testDeleteSubtable() {
    String first = "/a/b/c";
    String second = "/a/b/d";
    String third = "/a/c";
    NetworkTablesJNI.putString(first, "");
    NetworkTablesJNI.putString(second, "");
    NetworkTablesJNI.putString(third, "");

    delete("/a/b");
    assertTrue(!NetworkTablesJNI.containsKey(first)
        && !NetworkTablesJNI.containsKey(second)
        && NetworkTablesJNI.containsKey(third));
  }

}
