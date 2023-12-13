package cs3500.klondike.model.hw04;

import java.util.List;

/**
 * A class implementing KlondikeModel with differing rules from the basic game.
 */
public class WhiteheadKlondike extends AKlondike {

  /**
   * A helper method for startGame.
   */
  @Override
  protected void addCascades(List<CardWithState> deckToUse, int numPiles) {
    for (int i = 0; i < numPiles; i++) { //for every cascade
      for (int j = i; j < numPiles; j++) {
        if (deckToUse.isEmpty()) {
          throw new IllegalArgumentException("too many piles");
        } else {
          cascades.get(j).add(deckToUse.remove(0));
          cascades.get(j).get(i).flipVisibility();
        }
      }
    }
  }

  /**
   * A helper method for moving from cascade to cascade.
   */
  @Override
  protected void checkMove(int srcPile, int numCards, int destPile) {
    if (!cascades.get(destPile).isEmpty()) {
      if (!validStack(cascades.get(srcPile).get(cascades.get(srcPile).size() - numCards),
              cascades.get(destPile).get(cascades.get(destPile).size() - 1))) {
        throw new IllegalStateException("not valid stack");
      }
    }
    //checks for same suit
    if (numCards > 1) {
      CardWithState cardToMove = cascades.get(srcPile).get(cascades.get(srcPile).size() - 1);
      String suitToMove = cardToMove.toString().substring(cardToMove.toString().length() - 1);
      for (int i = 1; i < numCards; i++) {
        CardWithState currentCard = cascades.get(srcPile).get(cascades.get(srcPile).size() - 1 - i);
        String currentSuit = currentCard.toString().substring(cardToMove.toString().length() - i);
        if (!currentSuit.equals(suitToMove)) {
          throw new IllegalStateException("moved cards must be of the same suit.");
        }
      }
    }
  }

  /**
   * A helper method for validating the build after moving to a card to a cascade.
   */
  @Override
  protected boolean validStackHelper(CardWithState cSource, CardWithState cDest,
                                     int index1, int index2) {
    if (cSource.getCard().isRed() != cDest.getCard().isRed()) {
      return false;
    } else {
      return index1 == index2 - 1;
    }
  }

  /**
   * Removes the king condition.
   */
  @Override
  protected void kingHelper() {
    // overrides method to get rid of contents.
  }
}
