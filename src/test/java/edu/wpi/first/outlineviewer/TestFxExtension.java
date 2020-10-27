package edu.wpi.first.outlineviewer;

import java.io.IOException;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.wpiutil.CombinedRuntimeLoader;
import edu.wpi.first.wpiutil.WPIUtilJNI;

/**
 * Recommended by: https://github.com/javafxports/openjdk-jfx/issues/66#issuecomment-468370664
 */
public class TestFxExtension implements BeforeAllCallback {
  private static ExtensionContext getRoot(ExtensionContext context) {
    return context.getParent().map(TestFxExtension::getRoot).orElse(context);
  }

  @Override
  public void beforeAll(ExtensionContext context) throws IOException {
    WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
    NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
    CombinedRuntimeLoader.loadLibraries(OutlineViewer.class, "wpiutiljni", "ntcorejni");

    getRoot(context).getStore(ExtensionContext.Namespace.GLOBAL).getOrComputeIfAbsent("", key -> {
      if (System.getProperty("os.name").startsWith("Windows")) {
        System.load("C:\\Windows\\System32\\WindowsCodecs.dll");
      }
      return true;
    }, Boolean.class);
  }
}
