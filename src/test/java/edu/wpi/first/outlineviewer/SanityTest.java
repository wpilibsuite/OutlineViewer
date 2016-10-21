package edu.wpi.first.outlineviewer;

import com.google.common.testing.AbstractPackageSanityTests;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

import org.junit.Before;

import java.util.Arrays;

public class SanityTest extends AbstractPackageSanityTests {

  @SuppressWarnings("JavadocMethod")
  public SanityTest() {
    super();
    ignoreClasses(c -> Arrays.asList(
        Main.class,
        PreferencesController.class
    ).contains(c));
  }

  @Before
  public void initialize() {
    NetworkTable.shutdown();
  }
}
