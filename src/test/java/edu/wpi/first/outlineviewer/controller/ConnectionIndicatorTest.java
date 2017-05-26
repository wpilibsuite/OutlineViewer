package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.wpilibj.networktables.ConnectionInfo;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.scene.text.Text;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ConnectionIndicatorTest extends OutlineViewerControllerTest {

  private static final Runnable SERVER = () -> {
    NetworkTable.shutdown();
    NetworkTable.setServerMode();
    NetworkTable.initialize();
  };

  private static final Runnable CLIENT = () -> {
    NetworkTable.shutdown();
    NetworkTable.setClientMode();
    NetworkTable.initialize();
  };

  private final boolean connected;
  private final ConnectionInfo conn;
  private final String expected;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {SERVER, true, new ConnectionInfo("", "", 0, 0, 0), "Number of Clients: 0"},
        {CLIENT, false, new ConnectionInfo("", "", 0, 0, 0), "Disconnected"},
        {CLIENT, true, new ConnectionInfo("", "localhost", 0, 0, 0), "Connected: localhost"}
    });
  }

  public ConnectionIndicatorTest(Runnable networkTableSetup, boolean connected, ConnectionInfo conn,
                                 String expected) throws InterruptedException {
    this.connected = connected;
    this.conn = conn;
    this.expected = expected;

    networkTableSetup.run();
    Thread.sleep(150);
  }

  @Test
  public void testConnectionIndicator() throws InterruptedException {
    outlineViewerController.clf.apply(0, connected, conn);

    Text text = lookup("#connectionIndicator").query();
    assertEquals(expected, text.getText());
  }
}
