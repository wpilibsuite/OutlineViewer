package edu.wpi.first.outlineviewer.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MetadataTest {

  private final String testKey;
  private final boolean expectedResult;

  @Parameterized.Parameters
  @SuppressWarnings("JavadocMethod")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {"key", false},
        {"metadata", false},
        {"~metadata", false},
        {"metadata~", false},
        {"~metadata~", false},
        {"METADATA", false},
        {"~METADATA", false},
        {"METADATA~", false},
        {"~METADATA~", true},
        {" ", false},
        {"", false}
    });
  }

  public MetadataTest(String testKey, boolean expectedResult) {
    this.testKey = testKey;
    this.expectedResult = expectedResult;
  }

  @Test
  public void testMetadata() {
    NetworkTableData data = NetworkTableData.createNetworkTableData(testKey, "");

    Assert.assertEquals(expectedResult, data.isMetadata());
  }
}
