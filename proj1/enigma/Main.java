package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Collection;

import static enigma.EnigmaException.*;

/**
 * Enigma simulator.
 *
 * @author Allison Wang
 */
public final class Main {

    /**
     * Process a sequence of encryptions and decryptions, as
     * specified by ARGS, where 1 <= ARGS.length <= 3.
     * ARGS[0] is the name of a configuration file.
     * ARGS[1] is optional; when present, it names an input file
     * containing messages.  Otherwise, input comes from the standard
     * input.  ARGS[2] is optional; when present, it names an output
     * file for processed messages.  Otherwise, output goes to the
     * standard output. Exits normally if there are no errors in the input;
     * otherwise with code 1.
     */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }

    /**
     * Check ARGS and open the necessary files (see comment on main).
     */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /**
     * Return a Scanner reading from the file named NAME.
     */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Return a PrintStream writing to the file named NAME.
     */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Configure an Enigma machine from the contents of configuration
     * file _config and apply it to the messages in _input, sending the
     * results to _output.
     */
    private void process() {
        Machine mac = readConfig();
        String curSet;
        String curLine = "";
        boolean setting;
        while (_input.hasNextLine()) {
            if (!_input.hasNext("\\s*[*].*")) {
                throw new EnigmaException("Missing * at the start of config");
            } else {
                curSet = "";
                setting = false;
                while (!setting) {
                    curLine = _input.nextLine();
                    if (curLine.matches("[*].+")) {
                        setting = true;
                    } else {
                        printMessageLine(curLine);
                    }
                }
                int count = 0;
                Scanner nextLine = new Scanner(curLine);
                while ((count < (mac.numRotors() + 2)) || nextLine.hasNext(
                        "[(].+[)]")) {
                    if (!nextLine.hasNext()) {
                        throw new EnigmaException("More rotors needed");
                    }
                    curSet += nextLine.next().replaceAll("[*]", "* ")
                            + " ";
                    count++;
                }
                setUp(mac, curSet.substring(0, curSet.length() - 1));
                while (!_input.hasNext("[*]") && _input.hasNextLine()) {
                    String a = _input.nextLine().replaceAll("\\s+", "")
                            .toUpperCase();
                    printMessageLine(mac.convert(a));
                }
            }
        }

    }

    /**
     * Return an Enigma machine configured from the contents of configuration
     * file _config.
     */
    private Machine readConfig() {
        try {
            _myRotors = new ArrayList<>();
            _alphabet = new CharacterRange('A', 'Z');
            _config.next();
            if (_config.hasNextInt()) {
                _numrotors = _config.nextInt();
                if (_config.hasNextInt()) {
                    _pawls = _config.nextInt();
                    while (_config.hasNext(".+")) {
                        _myRotors.add(readRotor());
                    }
                } else {
                    throw new EnigmaException(""
                            + "Missing number of pawls");
                }
            } else {
                throw new EnigmaException(
                        "Missing number of rotors");
            }
            return new Machine(_alphabet, _numrotors, _pawls, _myRotors);
        } catch (NoSuchElementException excep) {
            throw error("Error in configuration file");
        }
    }

    /**
     * Return a rotor, reading its description from _config.
     */
    private Rotor readRotor() {
        String name;
        String cycles = "";
        String notches = "";
        String holder;
        try {
            name = _config.next().toUpperCase();
            holder = _config.next();
            if (holder.charAt(0) == 'N') {
                while (_config.hasNext("\\s*[(]\\w+[)]\\s*")) {
                    cycles += _config.next() + " ";
                }
                return new FixedRotor(name,
                        new Permutation(cycles, _alphabet));
            } else if (holder.charAt(0) == 'M') {
                notches += holder.substring(1);
                while (_config.hasNext("\\s*[(].+[)]\\s*")) {
                    String s = _config.next().replaceAll("[)][(]", ") (");
                    cycles += s + " ";
                }
                return new MovingRotor(name,
                        new Permutation(cycles, _alphabet), notches);
            } else if (holder.charAt(0) == 'R') {
                while (_config.hasNext("\\s*[(]\\w+[)]\\s*")) {
                    cycles += _config.next() + " ";
                }
                return new Reflector(name, new Permutation(cycles, _alphabet));
            } else {
                throw new EnigmaException("Non-existing rotor type");
            }
        } catch (NoSuchElementException excp) {
            throw error("Wrong description for rotor");
        }
    }

    /**
     * Set M according to the specification given on SETTINGS,
     * which must have the format specified in the assignment.
     */
    private void setUp(Machine M, String settings) {
        Scanner curr = new Scanner(settings);
        String[] listedRotors = new String[M.numRotors()];
        String p = "";
        if (curr.hasNext("[*]")) {
            curr.next();
            for (int i = 0; i < M.numRotors(); i++) {
                listedRotors[i] = curr.next();
            }
            M.insertRotors(listedRotors);
            if (curr.hasNext("\\w{" + (M.numRotors() - 1) + "}")) {
                M.setRotors(curr.next());
            }
            while (curr.hasNext("[(]\\w+[)]")) {
                p += curr.next() + " ";
            }
            if (p.length() > 0) {
                M.setPlugboard(new Permutation(p.
                        substring(0, p.length() - 1),
                        _alphabet));
            }
        }
    }

    /**
     * Print MSG in groups of five (except that the last group may
     * have fewer letters).
     */
    private void printMessageLine(String msg) {
        String message = msg;
        if (message.length() == 0) {
            _output.println();
        } else {
            while (message.length() > 0) {
                if (message.length() <= 5) {
                    _output.println(message);
                    message = "";
                } else {
                    _output.print(message.substring(0, 5) + " ");
                    message = message.substring(5, message.length());
                }
            }
        }
    }

    /**
     * Alphabet used in this machine.
     */
    private Alphabet _alphabet;

    /**
     * Source of input messages.
     */
    private Scanner _input;

    /**
     * Source of machine configuration.
     */
    private Scanner _config;

    /**
     * File for encoded/decoded messages.
     */
    private PrintStream _output;

    /**
     * Rotors used.
     */
    private int _numrotors;

    /**
     * Pawls used.
     */
    private int _pawls;

    /**
     * List of all rotors.
     */
    private Collection<Rotor> _myRotors;

}
