package edu.wpi.first.outlineviewer;

import com.google.common.testing.AbstractPackageSanityTests;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

import org.junit.Before;

import java.util.Arrays;

public class PackageSanityTests extends AbstractPackageSanityTests {

  @SuppressWarnings("JavadocMethod")
  public PackageSanityTests() {
    super();
    ignoreClasses(c -> Arrays.asList(
        OutlineViewer.class
    ).contains(c));
  }

  @Before
  public void before() {
    NetworkTable.shutdown();
  }

}
