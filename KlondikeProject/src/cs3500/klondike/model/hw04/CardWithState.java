package cs3500.klondike.model.hw04;

import java.util.Objects;
import cs3500.klondike.model.hw02.Card;

/**
 * A class representing a card in play.
 */
public class CardWithState {

  private Card c;
  private boolean visible;

  /**
   * Constructor for the CardWithState class.
   *
   * @param c is the card to be passed
   * @param visible is the visibility of the card
   */
  public CardWithState(Card c, boolean visible) {
    this.c = c;
    this.visible = visible;
  }

  /**
   * Flips the visibility of the card.
   */
  public void flipVisibility() {
    this.visible = !this.visible;
  }

  /**
   * Gets the visibility of the card.
   */
  public boolean getVisibility() {
    return this.visible;
  }

  /**
   * Represents the card as a string.
   * @return the formatted string.
   */
  public String toString() {
    return c.toString();
  }

  /**
   * Returns the card.
   */
  public Card getCard() {
    return c;
  }

  /**
   * Equals method based on card and visibility.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof CardWithState) {
      return ((CardWithState) obj).c.equals(this.c)
              && ((CardWithState) obj).visible == this.visible;
    }
    return false;
  }

  /**
   * Hash method based on card and visibility.
   */
  @Override
  public int hashCode() {
    return Objects.hash(c, visible);
  }

}
