package cs3500.klondike.view;

import java.io.IOException;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;

/**
 * A simple text-based rendering of the Klondike game.
 */
public class KlondikeTextualView implements TextView {
  private final KlondikeModel model;
  private Appendable out;

  public KlondikeTextualView(KlondikeModel model) {
    this.model = model;
  }

  public KlondikeTextualView(KlondikeModel model, Appendable out) {
    this.model = model;
    this.out = out;
  }

  public String toString() {
    return "Draw: " + this.drawString() + "\nFoundation: " + this.drawFoundation() + drawCascades();
  }

  private String drawString() {
    String str = "";
    if (!model.getDrawCards().isEmpty()) {
      for (int i = 0; i < model.getDrawCards().size() - 1; i++) {
        str = str + model.getDrawCards().get(i).toString() + ", ";
      }
      str = str + model.getDrawCards().get(model.getDrawCards().size() - 1).toString();
    }
    return str;
  }

  private String drawFoundation() {
    String str = "";
    for (int i = 0; i < model.getNumFoundations() - 1; i++) {
      if (model.getCardAt(i) == null) {
        str = str + "<none>, ";
      } else {
        str = str + model.getCardAt(i) + ", ";
      }
    }
    if (model.getCardAt(model.getNumFoundations() - 1) == null) {
      str = str + "<none>";
    } else {
      str = str + model.getCardAt(model.getNumFoundations() - 1);
    }
    return str;
  }

  private String drawCascades() {
    String str = "";
    Card current;
    for (int i = 0; i < model.getNumRows(); i++) {
      str += "\n";
      for (int j = 0; j < model.getNumPiles(); j++) {
        if (model.getPileHeight(j) >= i + 1) {
          try {
            if (model.getCardAt(j, i).toString().substring(0, 1).equals("1")) {
              str += model.getCardAt(j, i).toString();
            } else {
              str += " " + model.getCardAt(j, i).toString();
            }
          } catch (IllegalArgumentException e) {
            str += "  ?";
          }
        } else {
          if (i == 0) {
            str += "  X";
          } else {
            str += "   ";
          }
        }
      }
    }
    return str;
  }

  /**
   * Renders the Klondike as text.
   */
  @Override
  public void render() throws IOException {
    out.append(this.toString());
  }
}
