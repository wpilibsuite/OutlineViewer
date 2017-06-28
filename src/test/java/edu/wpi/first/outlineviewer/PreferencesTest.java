package edu.wpi.first.outlineviewer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PreferencesTest extends UtilityClassTest {

  public PreferencesTest() {
    super(Preferences.class);
  }

  @Test
  public void metadataPropertyTest() {
    Preferences.setShowMetaData(false);
    Preferences.setShowMetaData(true);

    assertTrue(Preferences.isShowMetaData());
  }

  @Test
  public void isServerPropertyTest() {
    Preferences.setServer(false);
    Preferences.setServer(true);

    assertTrue(Preferences.isServer());
  }

  @Test
  public void ipPropertyTest() {
    final String test = "190";
    Preferences.setIp("");
    Preferences.setIp(test);

    assertEquals(test, Preferences.getIp());
  }

  @Test
  public void portPropertyTest() {
    final int test = 9999;
    Preferences.setPort(1735);
    Preferences.setPort(test);

    assertEquals(test, Preferences.getPort());
  }
}
