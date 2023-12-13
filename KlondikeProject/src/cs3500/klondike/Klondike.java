package cs3500.klondike;

import java.io.InputStreamReader;

import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw04.LimitedDrawKlondike;
import cs3500.klondike.model.hw04.WhiteheadKlondike;

/**
 * Klondike implements the main method which is where the klondike program begins.
 */
public final class Klondike {

  /**
   * The main method which is where the klondike program begins.
   */
  public static void main(String[] args) {
    KlondikeModel model;
    int piles = 7;
    int draw = 3;
    KlondikeTextualController control =
            new KlondikeTextualController(new InputStreamReader(System.in), System.out);
    if (args.length > 0) {
      try {
        if (args[0].equals("limited")) {
          if (args.length > 1) {
            model = new LimitedDrawKlondike(Integer.parseInt(args[1]));
            if (args.length > 2) {
              piles = Integer.parseInt(args[2]);
            }
            if (args.length > 3) {
              draw = Integer.parseInt(args[3]);
            }
            control.playGame(model, model.getDeck(), false, piles, draw);
          } else {
            throw new IllegalArgumentException("Limited needs arg");
          }
        } else {
          if (args.length > 1) {
            piles = Integer.parseInt(args[1]);
          }
          if (args.length > 2) {
            draw = Integer.parseInt(args[2]);
          }
          if (args[0].equals("whitehead")) {
            model = new WhiteheadKlondike();
            control.playGame(model, model.getDeck(), false, piles, draw);
          } else if (args[0].equals("basic")) {
            model = new BasicKlondike();
            control.playGame(model, model.getDeck(), false, piles, draw);
          } else {
            throw new IllegalArgumentException("invalid game type");
          }
        }
      } catch (NumberFormatException | IllegalStateException ignored) {
        // ignores errors so main doesn't crash.
      }
    } else {
      throw new IllegalArgumentException("Must pass arguments");
    }
  }
}
