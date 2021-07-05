package enigma;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Class that represents a complete enigma machine.
 *
 * @author Allison Wang
 */
class Machine {

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        assert numRotors > 1;
        _numRotors = numRotors;
        assert pawls >= 0 && pawls < numRotors;
        Rotor[] all;
        all = allRotors.toArray(new Rotor[allRotors.size()]);
        _allRotors = all;
    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return _numRotors;
    }

    /**
     * Return the number of pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _numPawls;
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        _rotorList = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for (String rotor : rotors) {
            for (Rotor curr : _allRotors) {
                String name = curr.name();
                if (rotor.equals(name)) {
                    curr.set(0);
                    _rotorList.add(curr);
                    if (names.contains(rotor)) {
                        throw new EnigmaException("Multiple"
                                + "rotors of the same name");
                    } else {
                        names.add(rotor);
                    }
                }
            }
        }
        if (names.size() != _rotorList.size()) {
            throw new EnigmaException("A wrong rotor name was passed");
        }
    }

    /**
     * Set my rotors according to SETTING, which must be a string of
     * numRotors()-1 upper-case letters. The first letter refers to the
     * leftmost rotor setting (not counting the reflector).
     */
    void setRotors(String setting) {
        char[] temp = setting.toCharArray();
        assert temp.length == (_rotorList.size() - 1);
        int i = 0;
        for (Rotor r : _rotorList) {
            if (!r.reflecting()) {
                r.set(_alphabet.toInt(temp[i]));
                i++;
            } else {
                if (r.reflecting()) {
                    r.set(0);
                }
            }
        }
    }

    /**
     * Set the plugboard to PLUGBOARD.
     */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /**
     * Returns the result of converting the input character C (as an
     * index in the range 0..alphabet size - 1), after first advancing
     * the machine.
     */
    int convert(int c) {
        ArrayList<Boolean> notched = new ArrayList<>(numRotors());
        Rotor secLast = (_rotorList.get(numRotors() - 1));
        if (!secLast.atNotch()) {
            secLast.advance();
        } else {
            int index = 0;
            for (Rotor isNotch : _rotorList) {
                if (isNotch.atNotch()) {
                    notched.add(index - 1, true);
                    notched.add(index, true);
                } else {
                    notched.add(index, false);
                }
                index++;
            }
            for (int i = 0; i < numRotors(); i++) {
                if (notched.get(i)) {
                    _rotorList.get(i).advance();
                }
            }
        }
        int in = c % _alphabet.size();
        if (_plugboard != null) {
            in = _plugboard.permute(in);
        }
        for (int size = _rotorList.size() - 1; size > 0; size--) {
            Rotor current = _rotorList.get(size);
            in = current.convertForward(in);
        }
        int out = _rotorList.get(0).convertForward(in);
        for (int first = 1; first < _rotorList.size(); first++) {
            Rotor cur = _rotorList.get(first);
            out = cur.convertBackward(out);
        }
        if (_plugboard != null) {
            out = _plugboard.invert(out);
        }
        return out;
    }

    /**
     * Returns the encoding/decoding of MSG, updating the state of
     * the rotors accordingly.
     */
    String convert(String msg) {
        String converted = "";
        for (int i = 0; i < msg.length(); i++) {
            char character = Character.toUpperCase(msg.charAt(i));
            if (character != ' ') {
                int temp = _alphabet.toInt(character);
                char convert = _alphabet.toChar(convert(temp));
                converted += Character.toString(convert);
            }
        }
        return converted;
    }

    /**
     * Common alphabet of my rotors.
     */
    private final Alphabet _alphabet;

    /**
     * Number of rotor slots I have.
     */
    private int _numRotors;
    /**
     * Number of pawls, and therefore moving rotors I have.
     */
    private int _numPawls;
    /**
     * All my available rotors.
     */
    private Rotor[] _allRotors;
    /**
     * My current array of rotors.
     */
    private ArrayList<Rotor> _rotorList;
    /**
     * My plugboard.
     */
    private Permutation _plugboard;
}
