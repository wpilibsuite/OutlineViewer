package edu.wpi.first.outlineviewer;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Recommended by: https://github.com/javafxports/openjdk-jfx/issues/66#issuecomment-468370664
 */
public class TestFxExtension implements BeforeAllCallback {
  private static ExtensionContext getRoot(ExtensionContext context) {
    return context.getParent().map(TestFxExtension::getRoot).orElse(context);
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    getRoot(context).getStore(ExtensionContext.Namespace.GLOBAL).getOrComputeIfAbsent("", key -> {
      if (System.getProperty("os.name").startsWith("Windows")) {
        System.load("C:\\Windows\\System32\\WindowsCodecs.dll");
      }
      return true;
    }, Boolean.class);
  }
}
