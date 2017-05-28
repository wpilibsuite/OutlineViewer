package edu.wpi.first.tableviewer.dialog;

import com.sun.javafx.stage.StageHelper;
import javafx.stage.Window;

/**
 *
 */
public class Dialogs {

  /**
   * Centers a window relative to the primary stage.
   *
   * @param window the window to center
   */
  public static void center(Window window) {
    Window stage = StageHelper.getStages().get(0);
    double x = stage.getX() + stage.getWidth() / 2;
    double y = stage.getY() + stage.getHeight() / 2;
    window.setX(x - window.getWidth() / 2);
    window.setY(y - window.getHeight() / 2);
  }

}
