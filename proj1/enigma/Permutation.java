package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Allison Wang
 */
class Permutation {

    /**
     * Set this Permutation to that specified by CYCLES, a string in the
     * form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     * is interpreted as a permutation in cycle notation.  Characters in the
     * alphabet that are not included in any cycle map to themselves.
     * Whitespace is ignored.
     */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
    }

    /**
     * Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     * c0c1...cm.
     */
    private void addCycle(String cycle) {
        _cycles = _cycles + cycle;
    }

    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /**
     * Returns the size of the alphabet I permute.
     */
    int size() {
        return _alphabet.size();
    }

    /**
     * Return the result of applying this permutation to P modulo the
     * alphabet size.
     */
    int permute(int p) {
        int input = wrap(p);
        char temp = _alphabet.toChar(input);
        temp = permute(temp);
        return _alphabet.toInt(temp);
    }

    /**
     * Return the result of applying the inverse of this permutation
     * to  C modulo the alphabet size.
     */
    int invert(int c) {
        int input = wrap(c);
        char temp = _alphabet.toChar(input);
        temp = invert(temp);
        return _alphabet.toInt(temp);
    }

    /**
     * Return the result of applying this permutation to the index of P
     * in ALPHABET, and converting the result to a character of ALPHABET.
     */
    char permute(char p) {
        char letter = p;
        String element = Character.toString(p);
        String[] iterate = listCycles(_cycles);
        for (String cycle: iterate) {
            if (cycle.contains(element)) {
                int index = cycle.indexOf(element);
                if (index == cycle.length() - 1) {
                    letter = cycle.charAt(0);
                } else {
                    letter = cycle.charAt(index + 1);
                }
            }
        }
        return letter;
    }
    /**
     * Return the result of applying the inverse of this permutation to C.
     */
    char invert(char c) {
        char letter = c;
        String[] iterate = listCycles(_cycles);
        String element = Character.toString(c);
        for (String cycle: iterate) {
            if (cycle.contains(element)) {
                int current = cycle.indexOf(element);
                if (current == 0) {
                    letter = cycle.charAt(cycle.length() - 1);
                } else {
                    letter = cycle.charAt(current - 1);
                }
            }
        }
        return letter;
    }

    /**
     * Return the alphabet used to initialize this Permutation.
     */
    Alphabet alphabet() {
        return _alphabet;
    }

    /**
     * Return true iff this permutation is a derangement (i.e., a
     * permutation for which no value maps to itself).
     */
    boolean derangement() {
        String[] itercycles = listCycles(_cycles);
        for (String cycles: itercycles) {
            if (cycles.length() == 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Alphabet of this permutation.
     */
    private Alphabet _alphabet;
    /**
     * String of form c0->c1->...->cm->c0 representing one cycle.
     */
    private String _cycles;
    /**
     * Returns the original OLDCYCLE as a list of strings of all the cycles,
     * so we can iterate through them.
     */
    String[] listCycles(String oldCycle) {
        if (!oldCycle.contains(" ")) {
            oldCycle = oldCycle.replaceAll("\\)", "");
            oldCycle = oldCycle.replaceAll("\\(", "");
            String[] separated = {oldCycle};
            return separated;
        } else {
            oldCycle = oldCycle.replaceAll(" ", "");
            String[] second = oldCycle.split("\\)\\(");
            second[0] = second[0].substring(1);
            int end = second.length - 1;
            second[end] = second[end].substring(0, second[end].length() - 1);
            return second;
        }
    }
}

