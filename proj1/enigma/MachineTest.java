package enigma;
import org.junit.Test;
import org.junit.Assert;
import java.util.ArrayList;
import java.util.Arrays;
import static enigma.TestUtils.*;
import java.util.Collection;
import java.util.HashMap;

/** The suite of all JUnit tests for the Machine class.
 *  @author Allison Wang
 */
public class MachineTest {

    /**
     * Testing time limit.
     */

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /**
     * Check that DoubleStep is working properly.
     */

    @Test
    public void testDoubleStep() {
        Alphabet ac = new CharacterRange('A', 'D');
        Rotor one = new Reflector("R1", new Permutation("(AC) (BD)", ac));
        Rotor two = new MovingRotor("R2", new Permutation("(ABCD)", ac), "C");
        Rotor three = new MovingRotor("R3", new Permutation("(ABCD)", ac), "C");
        Rotor four = new MovingRotor("R4", new Permutation("(ABCD)", ac), "C");
        String setting = "AAA";
        Rotor[] machineRotors = {one, two, three, four};
        String[] rotors = {"R1", "R2", "R3", "R4"};
        Permutation newPlugboard = new Permutation("(YF) (ZH)", UPPER);
        Machine mach = new Machine(ac, 4, 3,
                new ArrayList<Rotor>(Arrays.asList(machineRotors)));
        mach.setPlugboard(newPlugboard);
        mach.insertRotors(rotors);
        mach.setRotors(setting);
        Assert.assertEquals("AAAA", getSetting(ac, machineRotors));
        mach.convert('a');
        Assert.assertEquals("AAAB", getSetting(ac, machineRotors));
        mach.convert('b');
        mach.convert('c');
        Assert.assertEquals("AABD", getSetting(ac, machineRotors));
    }

    /**
     * Helper method to get the String representation of
     * the current Rotor settings.
     */
    private String getSetting(Alphabet alph, Rotor[] machineRotors) {
        String currSetting = "";
        for (Rotor r : machineRotors) {
            currSetting += alph.toChar(r.setting());
        }
        return currSetting;
    }

    /**
     * Helper to make a Reflector rotor.
     */
    private Rotor makeReflector(String name, HashMap<String, String> rotors) {
        return new Reflector(name, new Permutation(rotors.get(name), UPPER));
    }

    /**
     * Helper to make a fixed rotor.
     */
    private Rotor makeFixedRotor(String name, HashMap<String, String> rotors) {
        return new FixedRotor(name, new Permutation(rotors.get(name), UPPER));
    }

    /**
     * Helper to make a moving rotor.
     */
    private Rotor makeMovingRotor(String name, HashMap<String,
            String> rotors, String notches) {
        return new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                notches);
    }

    /** Tests if insertion and setting works properly,
     * and if convert works for a single input.
     */
    @Test
    public void testIntegration() {
        Rotor b = makeReflector("B", NAVALA);
        Rotor beta = makeFixedRotor("Beta", NAVALA);
        Rotor three = makeMovingRotor("III", NAVALA, "V");
        Rotor four = makeMovingRotor("IV", NAVALA, "J");
        Rotor one = makeMovingRotor("I", NAVALA, "Q");
        Permutation newPlugboard = new Permutation("(YF) (ZH)", UPPER);
        Rotor[] completeRotors = {b, beta, three, four, one};
        Collection<Rotor> complete = new ArrayList<Rotor>();
        complete.add(b);
        complete.add(beta);
        complete.add(three);
        complete.add(four);
        complete.add(one);
        String[] rotorNames = {"B", "Beta", "III", "IV", "I"};
        String setting = "AXLE";
        Machine tester = new Machine(UPPER, 5, 3,
                new ArrayList<>(Arrays.asList(completeRotors)));
        tester.insertRotors(rotorNames);
        tester.setRotors(setting);
        tester.setPlugboard(newPlugboard);
        Assert.assertEquals("AAXLE", getSetting(UPPER, completeRotors));
        Assert.assertEquals("Z", tester.convert("Y"));
        for (int i = 0; i < 609; i++) {
            tester.convert("Y");
        }
        Assert.assertEquals("AAXIQ", getSetting(UPPER, completeRotors));
    }

    /** Tests if convert works for a string of length > 1.*/
    @Test
    public void testConvert() {
        Rotor b = makeReflector("B", NAVALA);
        Rotor beta = makeFixedRotor("Beta", NAVALA);
        Rotor one = makeMovingRotor("I", NAVALA, "V");
        Rotor two = makeMovingRotor("II", NAVALA, "J");
        Rotor three = makeMovingRotor("III", NAVALA, "Q");
        Permutation newPlugboard = new Permutation("(AQ) (EP)", UPPER);
        Rotor[] completeRotors = {b, beta, one, two, three};
        String[] rotorNames = {"B", "Beta", "I", "II", "III"};
        String setting = "AAAA";
        Machine tester = new Machine(UPPER, 5, 3,
                new ArrayList<>(Arrays.asList(completeRotors)));
        tester.insertRotors(rotorNames);
        tester.setRotors(setting);
        tester.setPlugboard(newPlugboard);
        Assert.assertEquals("AAAAA", getSetting(UPPER, completeRotors));
        Assert.assertEquals("IHBDQQMTQZ", tester.convert("Hello World"));
    }
}
