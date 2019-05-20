package edu.wpi.first.outlineviewer;

import java.util.concurrent.Callable;

import com.google.common.base.MoreObjects;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;

import edu.wpi.first.networktables.NetworkTableInstance;

@Command(sortOptions = false, versionProvider = OutlineViewerCli.VersionProvider.class)
class OutlineViewerCli implements Callable<Integer> {

  @ArgGroup
  private Mode mode = new Mode(); // to prevent nulls

  @Option(names = {"-p", "--port"}, arity = "1",
      defaultValue = "1735",
      description = "The port number to use. Defaults to ${DEFAULT-VALUE}.")
  private int port = NetworkTableInstance.kDefaultPort;

  @Option(names = {"-V", "--version"}, versionHelp = true, description = "Display version info")
  private boolean versionInfoRequested = false;

  @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message")
  private boolean usageHelpRequested = false;

  static class Mode {
    @Option(names = {"-s", "--server"}, arity = "0",
        description = "Start in server mode")
    private boolean serverMode = false;

    @Option(names = {"-c", "--client"}, arity = "0..1",
        defaultValue = "localhost",
        description = "Start in client mode pointed at the provided destination. "
            + "Server location can be an IP address or team number")
    private String serverLocation = "localhost";

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("serverName", serverMode)
          .add("clientName", serverLocation)
          .toString();
    }
  }

  @Override
  public Integer call() {
    Preferences.setServer(mode.serverMode);
    Preferences.setIp(mode.serverLocation);

    if (!Preferences.validatePortNumber(port)) {
      return 1;
    }

    Preferences.setPort(port);
    return 0;
  }

  public boolean isServerMode() {
    return mode.serverMode;
  }

  public String getServerLocation() {
    return mode.serverLocation;
  }

  public int getPort() {
    return port;
  }

  public boolean isVersionInfoRequested() {
    return versionInfoRequested;
  }

  public boolean isUsageHelpRequested() {
    return usageHelpRequested;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("serverMode", mode.serverMode)
        .add("serverLocation", mode.serverLocation)
        .add("port", port)
        .add("versionInfoRequested", versionInfoRequested)
        .add("usageHelpRequested", usageHelpRequested)
        .toString();
  }

  static class VersionProvider implements IVersionProvider {
    @Override
    public String[] getVersion()  {
      return OutlineViewer.getVersion();
    }
  }
}