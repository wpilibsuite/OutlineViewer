package edu.wpi.first.tableviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Controller for the connection indicator at the bottom of the main window.
 */
public class ConnectionIndicatorController {

  private static final PseudoClass CLIENT = PseudoClass.getPseudoClass("client");
  private static final PseudoClass SERVER = PseudoClass.getPseudoClass("server");
  private static final PseudoClass FAILED = PseudoClass.getPseudoClass("failed");

  @FXML
  private Pane root;
  @FXML
  private Label connectionLabel;

  @FXML
  private void initialize() {
    NetworkTablesJNI.addConnectionListener((uid, connected, conn) -> updateConnectionLabel(), true);
    Prefs.serverProperty().addListener(__ -> updateConnectionLabel());
    Executors.newSingleThreadScheduledExecutor(r -> {
      Thread thread = new Thread(r);
      thread.setDaemon(true);
      return thread;
    }).scheduleAtFixedRate(this::updateConnectionLabel, 0L, 1000, TimeUnit.MILLISECONDS);
  }

  /**
   * Updates the connection label based on the current network mode of ntcore.
   */
  private void updateConnectionLabel() {
    if (!Platform.isFxApplicationThread()) {
      Platform.runLater(this::updateConnectionLabel);
      return;
    }
    if (NetworkTableUtils.isRunning()) {
      if (NetworkTableUtils.isServer()) {
        if (NetworkTableUtils.failed()) {
          serverFail();
        } else if (NetworkTableUtils.starting()) {
          serverStarting();
        } else { // success
          serverSuccess();
        }
      } else if (NetworkTableUtils.isClient()) {
        if (NetworkTableUtils.failed()) {
          clientFail();
        } else if (NetworkTableUtils.starting()) {
          clientStarting();
        } else { // success
          clientSuccess();
        }
      } else {
        System.out.println("Running, but not in server or client mode");
        generalFailure();
      }
    } else {
      System.out.println("Not running anything");
      generalFailure();
    }
  }

  private void clientStarting() {
    connectionLabel.setText("Connecting to " + Prefs.getResolvedAddress() + "...");
    root.pseudoClassStateChanged(CLIENT, true);
    root.pseudoClassStateChanged(SERVER, false);
    root.pseudoClassStateChanged(FAILED, false);
  }

  private void clientFail() {
    String text = "No connection";
    String addr = Prefs.getResolvedAddress();
    if (addr != null) {
      text += " to " + addr;
    }
    connectionLabel.setText(text);
    root.pseudoClassStateChanged(CLIENT, true);
    root.pseudoClassStateChanged(SERVER, false);
    root.pseudoClassStateChanged(FAILED, true);
  }

  private void clientSuccess() {
    connectionLabel.setText("Connected to server at " + Prefs.getResolvedAddress());
    root.pseudoClassStateChanged(CLIENT, true);
    root.pseudoClassStateChanged(SERVER, false);
    root.pseudoClassStateChanged(FAILED, false);
  }

  private void serverStarting() {
    connectionLabel.setText("Starting server...");
    root.pseudoClassStateChanged(CLIENT, false);
    root.pseudoClassStateChanged(SERVER, true);
    root.pseudoClassStateChanged(FAILED, false);
  }

  private void serverFail() {
    connectionLabel.setText("Could not run server");
    root.pseudoClassStateChanged(CLIENT, false);
    root.pseudoClassStateChanged(SERVER, true);
    root.pseudoClassStateChanged(FAILED, true);
  }

  private void serverSuccess() {
    String text = "Running server";
    int numClients = NetworkTablesJNI.getConnections().length;
    switch (numClients) {
      case 0:
        text += " (No clients)";
        break;
      case 1:
        text += " (1 client)";
        break;
      default:
        text += " (" + numClients + " clients)";
        break;
    }
    connectionLabel.setText(text);
    root.pseudoClassStateChanged(CLIENT, false);
    root.pseudoClassStateChanged(SERVER, true);
    root.pseudoClassStateChanged(FAILED, false);
  }

  private void generalFailure() {
    connectionLabel.setText("Something went terribly wrong");
    root.pseudoClassStateChanged(CLIENT, false);
    root.pseudoClassStateChanged(SERVER, false);
    root.pseudoClassStateChanged(FAILED, true);
  }

}
