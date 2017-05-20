package edu.wpi.first.outlineviewer.model;

import com.google.common.testing.AbstractPackageSanityTests;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.junit.Before;

public class PackageSanityTests extends AbstractPackageSanityTests {

  @Before
  public void before() {
    NetworkTable.shutdown();
  }

}
