package edu.wpi.first.outlineviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import org.junit.After;
import org.junit.Test;

import static edu.wpi.first.outlineviewer.NetworkTableUtils.concat;
import static edu.wpi.first.outlineviewer.NetworkTableUtils.delete;
import static edu.wpi.first.outlineviewer.NetworkTableUtils.normalize;
import static edu.wpi.first.outlineviewer.NetworkTableUtils.simpleKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NetworkTableUtilsTest extends UtilityClassTest {

  public NetworkTableUtilsTest() {
    super(NetworkTableUtils.class);
  }

  @After
  public void after() {
    NetworkTableUtils.shutdown();
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
