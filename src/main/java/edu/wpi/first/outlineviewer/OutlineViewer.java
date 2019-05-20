package edu.wpi.first.outlineviewer;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import picocli.CommandLine;

import edu.wpi.first.outlineviewer.view.dialog.PreferencesDialog;

public class OutlineViewer extends Application {

  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  private static final ButtonType START = new ButtonType("Start", ButtonData.OK_DONE);
  private static final ButtonType QUIT = new ButtonType("Quit", ButtonData.CANCEL_CLOSE);

  @Override
  public void start(Stage primaryStage) throws IOException {
    AutoUpdater updater = new AutoUpdater();

    PreferencesDialog preferencesDialog = new PreferencesDialog(QUIT, START);
    if (getParameters().getRaw().size() > 0) {
      OutlineViewerCli cli = new OutlineViewerCli();
      CommandLine commandLine = new CommandLine(cli);
      commandLine.execute(getParameters().getRaw().toArray(new String[0]));

      Integer result = commandLine.<Integer>getExecutionResult();
      if (result == null || result != 0) {
        Platform.exit();
        return;
      }
    } else {
      if (!preferencesDialog.showAndWait().orElse(false)) {
        Platform.exit();
        return;
      }
      preferencesDialog.getController().save();
    }

    updater.init();

    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
    Pane mainWindow = loader.load();

    primaryStage.setTitle("OutlineViewer");
    primaryStage.setScene(new Scene(mainWindow));
    primaryStage.show();
  }

  /**
   * The version of this build.
   *
   * @return String representing the version
   */
  static String[] getVersion() {
    return new String[]{
        "OutlineViewer " + OutlineViewer.class.getPackage().getImplementationVersion(),
        "java " + System.getProperty("java.version")
    };
  }
}
