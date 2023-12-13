package cs3500.klondike.model.hw04;

import java.util.List;

import cs3500.klondike.model.hw02.Card;

/**
 * A class implementing KlondikeModel that is similar to basic Klondike except the max allowed
 * redraws of draw pile cards can be specified.
 */
public class LimitedDrawKlondike extends AKlondike {

  private final int numRedraw;
  private CardWithState finalCard;
  private boolean onFinalCard;
  private int cycles;

  /**
   * Constructor for the LimitedDrawKlondike class.
   *
   * @throws IllegalArgumentException if the number of redraws is less than 0.
   */
  public LimitedDrawKlondike(int numTimesRedrawAllowed) {
    if (numTimesRedrawAllowed < 0) {
      throw new IllegalArgumentException("Redraw times cannot be negative.");
    } else {
      this.numRedraw = numTimesRedrawAllowed;
    }
  }

  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
          throws IllegalArgumentException, IllegalStateException {
    super.startGame(deck, shuffle, numPiles, numDraw);
    cycles = 0;
    onFinalCard = false;
    if (!draw.isEmpty()) {
      finalCard = draw.get(draw.size() - 1);
    }
  }

  /**
   * Discards the topmost draw-card.
   *
   * @throws IllegalStateException if the game hasn't been started yet.
   * @throws IllegalStateException if move is not allowable.
   */
  @Override
  public void discardDraw() throws IllegalStateException {
    testGameStarted();
    if (draw.isEmpty()) {
      throw new IllegalStateException("draw is empty");
    }
    if (draw.size() > this.numDraw) {
      draw.get(0).flipVisibility();
    }
    if (cycles < numRedraw) {
      draw.add(draw.remove(0));
    } else if (onFinalCard) {
      draw.add(draw.remove(0));
      onFinalCard = false;
      finalCard = draw.get(draw.size() - 1);
    } else {
      draw.remove(0);
    }
    if (!draw.isEmpty()) {
      adjustDraw();
    }
  }

  @Override
  protected void adjustDraw() {
    if (!draw.isEmpty()) {
      if (draw.get(0) == finalCard) {
        cycles++;
        if (cycles <= numRedraw) {
          onFinalCard = true;
        }
      }
    }
    if (draw.size() >= this.numDraw) {
      if (!draw.get(numDraw - 1).getVisibility()) {
        draw.get(numDraw - 1).flipVisibility();
      }
    }
  }

}