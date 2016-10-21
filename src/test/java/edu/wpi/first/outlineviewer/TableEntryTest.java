package edu.wpi.first.outlineviewer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TableEntryTest {

  private String key;
  private boolean expectedResult;

  public TableEntryTest(String key, boolean expectedResult) {
    this.key = key;
    this.expectedResult = expectedResult;
  }

  /**
   * Parameters for testing the metadata checker.
   */
  @Parameterized.Parameters
  public static Collection metaDataTestCases() {
    return Arrays.asList(new Object[][] {
        { "~ METADATA ~", true },
        { "METADATA", false },
        { "metadata", false },
        { "~ metadata ~", false },
        { "~ METADATA", false },
        { "METADATA ~", false }
    });
  }

  @Test
  public void testMetaDataCheck() {
    assertEquals(expectedResult, TableEntry.isMetadata(key));
  }
}
