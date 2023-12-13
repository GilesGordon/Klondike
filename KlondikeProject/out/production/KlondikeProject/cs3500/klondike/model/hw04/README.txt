Changes to old code:
- Removed visibility specifications in CardImpl class and created the CardWithState class that
  contains a CardImpl and adds the visibility field and methods previously in CardImpl.
- made CardImpl fields immutable.
- abstracted all BasicKlondike methods to a class called AKlondike.
- various bug fixed in shared model functionality (i.e. moving two cards at once).
- made various helper methods in AKlondike both to break down large methods and also be able
  to override smaller code portions in the extended models.
- fixed the view to display 10's properly.
- created helper methods for the controller class to append without repeat code.
- added more documentation to models.