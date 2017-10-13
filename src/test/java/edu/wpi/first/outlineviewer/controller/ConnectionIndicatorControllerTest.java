package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.outlineviewer.NetworkTableUtilities;
import edu.wpi.first.outlineviewer.OutlineViewer;
import edu.wpi.first.outlineviewer.Preferences;
import java.util.stream.Stream;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectionIndicatorControllerTest extends ApplicationTest {

  private ConnectionIndicatorController controller;
  private NetworkTableInstance mockNetworkTableInstance;

  @Override
  public void start(Stage stage) throws Exception {
    NetworkTableUtilities.shutdown();

    mockNetworkTableInstance = mock(NetworkTableInstance.class);

    FXMLLoader loader = new FXMLLoader(OutlineViewer.class
        .getResource("ConnectionIndicator.fxml"));
    loader.setControllerFactory(controllerClass
        -> new ConnectionIndicatorController(mockNetworkTableInstance));
    Pane connectionIndicator = loader.load();
    controller = loader.getController();
    stage.setScene(new Scene(connectionIndicator));
    stage.show();
  }

  @ParameterizedTest
  @ValueSource(ints = {NetworkTableInstance.kNetModeNone, NetworkTableInstance.kNetModeFailure})
  void testGeneralFailure(int networkMode) {
    when(mockNetworkTableInstance.getNetworkMode()).thenReturn(networkMode);
    controller.updateConnectionLabel();
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals("Something went terribly wrong", getConnectionLabel().getText());
  }

  private static Stream<Arguments> serverArguments() {
    return Stream.of(
        Arguments.of("Could not run server", NetworkTableInstance.kNetModeFailure),
        Arguments.of("Starting server...", NetworkTableInstance.kNetModeStarting),
        Arguments.of("Running server (No clients)", 0)
    );
  }

  @ParameterizedTest
  @MethodSource("serverArguments")
  void testServer(String expectedText, int networkMode) {
    when(mockNetworkTableInstance.getNetworkMode())
        .thenReturn(NetworkTableInstance.kNetModeServer | networkMode);
    controller.updateConnectionLabel();
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals(expectedText, getConnectionLabel().getText());
  }

  private static Stream<Arguments> clientArguments() {
    return Stream.of(
        Arguments.of("No connection to " + Preferences.getIp(),
            NetworkTableInstance.kNetModeFailure),
        Arguments.of("Connecting to " + Preferences.getIp() + "...",
            NetworkTableInstance.kNetModeStarting),
        Arguments.of("Connected to server at " + Preferences.getIp(), 0)
    );
  }

  @ParameterizedTest
  @MethodSource("clientArguments")
  void testClient(String expectedText, int networkMode) {
    when(mockNetworkTableInstance.getNetworkMode())
        .thenReturn(NetworkTableInstance.kNetModeClient | networkMode);
    controller.updateConnectionLabel();
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals(expectedText, getConnectionLabel().getText());
  }
  private Label getConnectionLabel() {
    return lookup(".connection-label").query();
  }

}
