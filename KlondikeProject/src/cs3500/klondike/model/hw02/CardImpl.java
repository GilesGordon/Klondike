package cs3500.klondike.model.hw02;

import java.util.Objects;

/**
 * Implementation of the card interface.
 */
public class CardImpl implements cs3500.klondike.model.hw02.Card {

  protected final Value value;
  protected final Suit suit;

  /**
   * An enumeration representing values of cards.
   */
  public enum Value {
    Ace("A"), Two("2"), Three("3"), Four("4"), Five("5"), Six("6"), Seven("7"), Eight("8"),
    Nine("9"), Ten("10"), Jack("J"), Queen("Q"), King("K");

    private final String value;

    private Value(String value) {
      this.value = value;
    }

    public String value() {
      return value;
    }
  }

  /**
   * An enumeration representing suits of cards.
   */
  public enum Suit {
    C("♣"), S("♠"), H("♡"), D("♢");

    private final String value;

    private Suit(String value) {
      this.value = value;
    }

    public String value() {
      return value;
    }
  }

  /**
   * Constructor for the CardImpl class.
   */
  public CardImpl(Value v, Suit s) {
    if (v == null || s == null) {
      throw new IllegalArgumentException("Cannot pass null args.");
    }
    this.value = v;
    this.suit = s;
  }

  /**
   * Represents the card as a string.
   * @return the formatted string.
   */
  public String toString() {
    return value.value() + suit.value();
  }

  /**
   * Checks if teh card is red.
   * @return true if the card is red.
   */
  public boolean isRed() {
    return (suit.equals(Suit.H) || suit.equals(Suit.D));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof CardImpl) {
      return ((CardImpl) obj).toString().equals(this.toString());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, suit);
  }
}
