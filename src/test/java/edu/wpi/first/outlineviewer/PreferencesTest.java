package edu.wpi.first.outlineviewer;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class PreferencesTest extends UtilityClassTest {

  PreferencesTest() {
    super(Preferences.class);
  }

  @Test
  void isServerPropertyTest() {
    Preferences.setServer(false);
    Preferences.setServer(true);

    assertTrue(Preferences.isServer());
  }

  @Test
  void ipPropertyTest() {
    final String test = "190";
    Preferences.setIp("");
    Preferences.setIp(test);

    assertEquals(test, Preferences.getIp());
  }

  @Test
  void portPropertyTest() {
    final int test = 9999;
    Preferences.setPort(1735);
    Preferences.setPort(test);

    assertEquals(test, Preferences.getPort());
  }
}
