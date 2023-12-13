package cs3500.klondike.view;

import java.io.IOException;

/** A marker interface for all text-based views, to be used in the Klondike game. */
public interface TextView {

  /**
   * Renders the Klondike as text.
   */
  void render() throws IOException;
}
