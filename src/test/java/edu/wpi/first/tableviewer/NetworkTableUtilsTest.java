package edu.wpi.first.tableviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import org.junit.Test;

import static edu.wpi.first.tableviewer.NetworkTableUtils.concat;
import static edu.wpi.first.tableviewer.NetworkTableUtils.delete;
import static edu.wpi.first.tableviewer.NetworkTableUtils.normalize;
import static edu.wpi.first.tableviewer.NetworkTableUtils.simpleKey;
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
    String ok = "/this/doesn't/need/to/be/normalized";
    assertEquals(ok, normalize(ok));
  }

  @Test
  public void testNormalizeAddLeadingSlash() {
    String key = "no/leading/slash";
    assertEquals("/" + key, normalize(key));
  }

  @Test
  public void testNormalizeAwfulString() {
    String awful = "//////what////an/awful/////key///";
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
    String key = "/NetworkTableUtilsTest::testDeleteKey";
    NetworkTablesJNI.putString(key, "dummy");
    assertTrue(NetworkTablesJNI.containsKey(key));
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
    assertFalse(NetworkTablesJNI.containsKey(first));
    assertFalse(NetworkTablesJNI.containsKey(second));
    assertTrue(NetworkTablesJNI.containsKey(third));
    delete("/a");
    assertFalse(NetworkTablesJNI.containsKey(third));
  }

}
