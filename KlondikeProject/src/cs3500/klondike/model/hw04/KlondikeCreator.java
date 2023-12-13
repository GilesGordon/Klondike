package cs3500.klondike.model.hw04;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.KlondikeModel;

/**
 * A factory class that can return an instance of a Klondike game based on the given GameType enum.
 */
public class KlondikeCreator {

  /**
   * An enumeration representing suits of cards.
   */
  public enum GameType {
    BASIC, LIMITED, WHITEHEAD;
  }

  /**
   * Returns an instance of a Klondike game based on the given GameType enum.
   *
   * @param type is the game type to be returned
   * @return the KlondikeModel of the given type
   */
  public static KlondikeModel create(GameType type) {
    if (type.equals(GameType.WHITEHEAD)) {
      return new WhiteheadKlondike();
    } else if (type.equals(GameType.LIMITED)) {
      return new LimitedDrawKlondike(2);
    } else {
      return new BasicKlondike();
    }
  }

  /**
   * returns an instance of a Limited draw Klondike game.
   */
  public static KlondikeModel createLimited(int d) {
    return new LimitedDrawKlondike(d);
  }
}
