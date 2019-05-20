package edu.wpi.first.outlineviewer;

import java.io.PrintWriter;

import com.google.common.io.ByteStreams;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OutlineViewerCliTest {

  @Test
  void helpNotDefault() {
    OutlineViewerCli cli = new OutlineViewerCli();
    CommandLine commandLine = getCommandLine(cli);

    commandLine.execute();
    assertFalse(cli.isUsageHelpRequested());
  }

  @ParameterizedTest
  @ValueSource(strings = { "-h", "--help" })
  void helpTest(String arg) {
    OutlineViewerCli cli = new OutlineViewerCli();
    CommandLine commandLine = getCommandLine(cli);

    commandLine.execute(arg);
    assertTrue(cli.isUsageHelpRequested());
  }

  @Test
  void versionNotDefault() {
    OutlineViewerCli cli = new OutlineViewerCli();
    CommandLine commandLine = getCommandLine(cli);

    commandLine.execute();
    assertFalse(cli.isVersionInfoRequested());
  }

  @ParameterizedTest
  @ValueSource(strings = { "-V", "--version" })
  void versionTest(String arg) {
    OutlineViewerCli cli = new OutlineViewerCli();
    CommandLine commandLine = getCommandLine(cli);

    commandLine.execute(arg);
    assertTrue(cli.isVersionInfoRequested());
  }

  @Test
  void serverModeNotDefault() {
    OutlineViewerCli cli = new OutlineViewerCli();
    CommandLine commandLine = getCommandLine(cli);

    commandLine.execute();
    assertFalse(cli.isServerMode());
  }

  @ParameterizedTest
  @ValueSource(strings = { "-s", "--server" })
  void serverModeTest(String arg) {
    Preferences.setServer(false);
    OutlineViewerCli cli = new OutlineViewerCli();
    CommandLine commandLine = getCommandLine(cli);

    commandLine.execute(arg);
    assertAll(
        () -> assertTrue(cli.isServerMode()),
        () -> assertTrue(Preferences.isServer())
    );
  }

  @ParameterizedTest
  @ValueSource(strings = { "-c=localhost", "--client=localhost" })
  void clientModeTest(String arg) {
    Preferences.setServer(false);
    OutlineViewerCli cli = new OutlineViewerCli();
    CommandLine commandLine = getCommandLine(cli);

    commandLine.execute(arg);
    assertAll(
        () -> assertEquals("localhost", cli.getServerLocation()),
        () -> assertFalse(cli.isServerMode()),
        () -> assertFalse(Preferences.isServer())
    );
  }

  @Test
  void serverAndClientModeIllegal() {
    OutlineViewerCli cli = new OutlineViewerCli();
    CommandLine commandLine = getCommandLine(cli);

    int result = commandLine.execute("-c", "-s");
    assertEquals(CommandLine.ExitCode.USAGE, result);
  }

  private CommandLine getCommandLine(OutlineViewerCli cli) {
    CommandLine commandLine = new CommandLine(cli);
    commandLine.setOut(new PrintWriter(ByteStreams.nullOutputStream()));
    commandLine.setErr(new PrintWriter(ByteStreams.nullOutputStream()));
    return commandLine;
  }
}
