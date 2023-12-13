package cs3500.klondike.controller;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.view.KlondikeTextualView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Controller class for Klondike game.
 */
public class KlondikeTextualController implements cs3500.klondike.controller.KlondikeController {

  private final Appendable a;
  private final Scanner s;
  private boolean quit;

  /**
   * Constructor for the KlondikeTextualController glass.
   */
  public KlondikeTextualController(Readable r, Appendable a) {
    if (r == null || a == null) {
      throw new IllegalArgumentException("Arguments cannot be null");
    } else {
      this.a = a;
      s = new Scanner(r);
    }
  }

  /**
   * The primary method for beginning and playing a game.
   *
   * @param model    The game of solitaire to be played
   * @param deck     The deck of cards to be used
   * @param shuffle  Whether to shuffle the deck or not
   * @param numPiles How many piles should be in the initial deal
   * @param numDraw  How many draw cards should be visible
   * @throws IllegalArgumentException if the model is null
   * @throws IllegalStateException    if the game cannot be started,
   *                                  or if the controller cannot interact with the player.
   */
  @Override
  public void playGame(KlondikeModel model, List<Card> deck, boolean shuffle,
                       int numPiles, int numDraw)
          throws IllegalArgumentException, IllegalStateException {
    if (model == null) {
      throw new IllegalArgumentException("model cannot be null");
    } else {
      int winCon = deck.size();
      quit = false;
      try {
        model.startGame(deck, shuffle, numPiles, numDraw);
      } catch (IllegalArgumentException | IllegalStateException e) {
        throw new IllegalStateException("startGame errors.");
      }
      try {
        KlondikeTextualView ktv = new KlondikeTextualView(model, a);
        // plays the game until quit or game over.
        keepPlayingGame(ktv, model);
        if (quit) {
          a.append("Game quit!\n");
          a.append("State of game when quit:\n");
          renderAndAppendScore(ktv, model);
        } else {
          if (model.getScore() == winCon) {
            ktv.render();
            a.append("\n");
            a.append("You win!" + "\n");
          } else {
            ktv.render();
            a.append("\n");
            a.append("Game over. Score: " + model.getScore() + "\n");
          }
        }
      } catch (IOException | NoSuchElementException e) {
        throw new IllegalStateException("cannot append to appendable or read with reader");
      }
    }
  }

  /**
   * Helper method for playGame.
   *
   * @param ktv is the KlondikeTextualView to be used for rendering
   * @param model is the current working model
   */
  private void keepPlayingGame(KlondikeTextualView ktv, KlondikeModel model) {
    try {
      while (!model.isGameOver() && !quit) {
        // Print the current game state
        renderAndAppendScore(ktv, model);
        String next = s.next();
        while (!(next.equals("mpp") || next.equals("md") || next.equals("mpf")
                || next.equals("mdf") || next.equals("dd") || next.equalsIgnoreCase("q"))) {
          a.append("Invalid move. Play again. Invalid command letter.\n");
          renderAndAppendScore(ktv, model);
          next = s.next();
        }
        playGameHelper(model, next);
      }
    } catch (IOException | NoSuchElementException e) {
      throw new IllegalStateException("cannot append to appendable or read with reader");
    }
  }

  private void renderAndAppendScore(KlondikeTextualView ktv, KlondikeModel model) {
    try {
      ktv.render();
      a.append("\n");
      a.append("Score: " + model.getScore() + "\n");
    } catch (IOException e) {
      throw new IllegalStateException("cannot append to appendable or read with reader");
    }
  }

  /**
   * Helper method for pLayGame.
   *
   * @param model The game of solitaire to be played
   * @param next  String from reader
   * @throws IllegalStateException if the scanner cant read input
   */
  private void playGameHelper(KlondikeModel model, String next)
          throws IllegalStateException {
    ArrayList<Integer> args;
    try {
      try {
        if (next.equals("mpp")) {
          args = argGetter(3);
          if (!quit) {
            model.movePile(args.get(0) - 1, args.get(1), args.get(2) - 1);
          }
        } else if (next.equals("md")) {
          args = argGetter(1);
          if (!quit) {
            model.moveDraw(args.get(0) - 1);
          }
        } else if (next.equals("mpf")) {
          args = argGetter(2);
          if (!quit) {
            model.moveToFoundation(args.get(0) - 1, args.get(1) - 1);
          }
        } else if (next.equals("mdf")) {
          args = argGetter(1);
          if (!quit) {
            model.moveDrawToFoundation(args.get(0) - 1);
          }
        } else if (next.equals("dd")) {
          if (!quit) {
            model.discardDraw();
          }
        } else {
          quit = true;
        }
      } catch (IllegalStateException e) {
        a.append("Invalid move. Play again. The move didn't follow game rules.\n");
      } catch (IllegalArgumentException e) {
        a.append("Invalid move. Play again. Illegal arguments.\n");
      }
    } catch (IOException | NoSuchElementException e) {
      throw new IllegalStateException("IO exception");
    }
  }

  /**
   * Validates arguments for command one by one.
   *
   * @param num int representing number of expected arguments.
   */
  private ArrayList<Integer> argGetter(int num) {
    ArrayList<Integer> args = new ArrayList<Integer>();
    int currentArg;
    boolean validArg;
    for (int i = 0; i < num; i++) {
      if (quit) {
        break;
      }
      validArg = false;
      while (!validArg) {
        if (quit) {
          break;
        }
        if (s.hasNextInt()) {
          currentArg = s.nextInt();
          if (currentArg >= 0) {
            validArg = true;
            args.add(currentArg);
          }
        } else {
          String next = s.next();
          if (next.equalsIgnoreCase("q")) {
            quit = true;
          }
        }
      }
    }
    return args;
  }
}
