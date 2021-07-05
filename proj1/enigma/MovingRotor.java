package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Allison Wang
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
        _permutation = perm;
    }

    @Override
    boolean rotates() {
        return true;
    }
    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i += 1) {
            char notch = _notches.charAt(i);
            if (notch == alphabet().toChar(setting())) {
                return true;
            }
        }
        return false;
    }

    /** Advances setting by 1. */
    @Override
    void advance() {
        super.set(_permutation.wrap(super.setting() + 1));
    }

    /** List of the positions of the notches of the rotor. */
    private String _notches;
    /** Gives a permutation variable. */
    private Permutation _permutation;

}
