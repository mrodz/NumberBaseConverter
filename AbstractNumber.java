package converter;

import java.math.*;

@SuppressWarnings("unused")
final class AbstractNumber implements Comparable<AbstractNumber> {
    static {
        ZERO = AbstractNumber.valueOf(0L);
        ONE = AbstractNumber.valueOf(1L);
        TWO = AbstractNumber.valueOf(2L);
        TEN = AbstractNumber.valueOf(10L);
    }

    /**
     * The Instance {@code enum} links ASCII characters with their corresponding Integer value
     * in accordance to this rule:
     * <ul>
     *     <li><b>0-9</b> as Integers</li>
     *     <li><b>10-35</b> as A-Z</li>
     *     <li><b>36-61</b> as a-z</li>
     *     <li><b>62</b> as +</li>
     *     <li><b>63</b> as /</li>
     * </ul>
     * @see <a href="https://en.wikipedia.org/wiki/Base64">This Link</a> for information on Base64
     */
    private enum Instance {
        _0('0', 0),
        _1('1', 1),
        _2('2', 2),
        _3('3', 3),
        _4('4', 4),
        _5('5', 5),
        _6('6', 6),
        _7('7', 7),
        _8('8', 8),
        _9('9', 9),
        _A('A', 10),
        _B('B', 11),
        _C('C', 12),
        _D('D', 13),
        _E('E', 14),
        _F('F', 15),
        _G('G', 16),
        _H('H', 17),
        _I('I', 18),
        _J('J', 19),
        _K('K', 20),
        _L('L', 21),
        _M('M', 22),
        _N('N', 23),
        _O('O', 24),
        _P('P', 25),
        _Q('Q', 26),
        _R('R', 27),
        _S('S', 28),
        _T('T', 29),
        _U('U', 30),
        _V('V', 31),
        _W('W', 32),
        _X('X', 33),
        _Y('Y', 34),
        _Z('Z', 35),
        _a('a', 36),
        _b('b', 37),
        _c('c', 38),
        _d('d', 39),
        _e('e', 40),
        _f('f', 41),
        _g('g', 42),
        _h('h', 43),
        _i('i', 44),
        _j('j', 45),
        _k('k', 46),
        _l('l', 47),
        _m('m', 48),
        _n('n', 49),
        _o('o', 50),
        _p('p', 51),
        _q('q', 52),
        _r('r', 53),
        _s('s', 54),
        _t('t', 55),
        _u('u', 56),
        _v('v', 57),
        _w('w', 58),
        _x('x', 59),
        _y('y', 60),
        _z('z', 61),
        ___('+', 62),
        __('\\', 63);

        final int value;
        final char symbol;
        Instance(char symbol, int value) {
            this.symbol = symbol;
            this.value = value;
        }
    }
    private static final String VALID_MATCHES = "[a-zA-Z0-9+\\\\]";
    private static final char DECIMAL_SPLIT = '.'; // per U.S. standards; update as you wish
    private static final char THOUSAND_SPLIT = ','; // per U.S. standards; update as you wish
    private static final int MAX_RADIX = 64; // DO NOT MODIFY THIS LINE

    public static final AbstractNumber ZERO;
    public static final AbstractNumber ONE;
    public static final AbstractNumber TWO;
    public static final AbstractNumber TEN;

    private final String HEX;
    private final String BINARY;
    private final BigDecimal DECIMAL;
    private final int RADIX;

    private int DECIMAL_LENGTH = 5; // this is the value requested by HyperSkill, can be updated with `setPrecision(int)`

    private boolean negative = false;
    private boolean isInteger;

    /**
     * Most useful constructor to initialize an {@link AbstractNumber}.
     * @param hex valid {@link String} to serve as a number.
     * @param radix the <i>base</i> to which this number is defined (incl. Base 2 - 64).
     * @throws IllegalArgumentException a digit in the <code>hex</code> is greater than or equal to the <code>radix</code> bound;
     *                                  there is an invalid digit (not found under {@link Instance});
     *                                  <code>radix</code> > {@link #MAX_RADIX}
     */
    public AbstractNumber(String hex, int radix) throws IllegalArgumentException {
        if (hex.charAt(0) == '-') {
            hex = hex.substring(1);
            negative = true;

        }

        this.isInteger = !hex.matches(".*\\..*");
        int periods = hex.length() - hex.replaceAll("\\" + DECIMAL_SPLIT, "").length();

        if (periods > 1)
            throw new IllegalArgumentException(String.format("Too many decimal separators! Expected 0, 1; found: " +
                    "%dx '%c'", periods, DECIMAL_SPLIT));
        if (areInvalidBounds(radix) > 0)
            throw new IllegalArgumentException("This converter only supports up to Base64 encoding!");
        if (areInvalidBounds(radix) < 0)
            throw new IllegalArgumentException(String.format("Invalid radix %d: one cannot form a number out of" +
                    "anything less than Base 2", getInvalidBound(radix)));
        if (periods != 1 && !hex.matches(String.format("%s{%d}", VALID_MATCHES, hex.length())))
            throw new NumberFormatException(String.format("Invalid HEX Characters -> '%s'",
                    hex.replaceAll(VALID_MATCHES, "")));
        if (AbstractNumber.hexOutsideRadix(hex, radix))
            throw new IllegalArgumentException(String.format("Error! Digit '%c' is greater than or equal to upper" +
                    " radix bound %d", AbstractNumber.getInvalidHexInRadix(hex, radix), radix));

        this.RADIX = radix;
        this.HEX = hex.length() > 0 ? (radix < 36 ? hex.toUpperCase() : hex) : "0";
        this.DECIMAL = toNumber();
        this.BINARY = hex(2);
    }

    /**
     * Constructor to initialize an {@link AbstractNumber} using a {@link BigDecimal}. (Defaults to Base 10)
     * @param decimal any valid {@link BigDecimal}
     * @throws IllegalArgumentException See {@link #AbstractNumber(String, int)}
     */
    public AbstractNumber(BigDecimal decimal) throws IllegalArgumentException {
        this(decimal, 10);
    }

    /**Used internally for conversions*/
    private AbstractNumber(BigDecimal decimal, int radix) {
        if (areInvalidBounds(radix) > 0)
            throw new IllegalArgumentException("This converter only supports up to Base64 encoding!");
        if (areInvalidBounds(radix) < 0)
            throw new IllegalArgumentException(String.format("Invalid radix %d: one cannot form a number out of" +
                    "anything less than Base 2", getInvalidBound(radix)));

        this.isInteger = decimal.compareTo(decimal.subtract(decimal.setScale(0, RoundingMode.CEILING))) == 0;

        this.DECIMAL = decimal;
        this.RADIX = radix;
        this.BINARY = hex(2);
        this.HEX = hex();
        this.negative = decimal.compareTo(BigDecimal.ZERO) < 0;

        int periods = this.HEX.length() - this.HEX.replaceAll("\\" + DECIMAL_SPLIT, "").length();
        if (periods > 1) {
            throw new IllegalArgumentException(String.format("Too many decimal separators! Expected 0, 1; found: " +
                    "%dx '%c'", periods, DECIMAL_SPLIT));
        }
    }

    private AbstractNumber(BigDecimal decimal, int radix, boolean b, int precision) {
        this(decimal, radix);
        this.isInteger = b;
        this.DECIMAL_LENGTH = precision;
    }

    /**
     * Constructor to initialize an {@link AbstractNumber} using a {@code long} value. (Defaults to Base 10)
     * @param decimal any valid {@code long} number.
     * @throws IllegalArgumentException See {@link #AbstractNumber(String, int)}
     * @deprecated - use {@link AbstractNumber#valueOf(long)} instead.
     */
    public AbstractNumber(long decimal) {
        this(Long.toString(decimal), 10);
    }

    /**
     * Returns a {@code new} {@link AbstractNumber} obtained from a {@code long} value.
     * @param l any valid {@code long} number.
     * @return the proper {@link AbstractNumber} (in Base 10)
     */
    public static AbstractNumber valueOf(long l) {
        return new AbstractNumber(Long.toString(l), 10);
    }

    /**
     * Follows the format of an implementation of {@link Comparable}.
     * @param bounds user supplied parameters ({@code int...}) used to check for a valid radix.
     * @return No invalid bounds = 0; bound over {@link #MAX_RADIX} = 1; bound under 2 = -1.
     */
    public static int areInvalidBounds(int... bounds) {
        Integer def = getInvalidBound(bounds);
        return def == null ? 0 : def > MAX_RADIX ? 1 : -1;
    }

    /**
     * Get an invalid bound from multiple bounds.
     * @param bounds user supplied parameters ({@code int...} to be analyzed)
     * @return {@code null} if the input is sufficient; the invalid bound as an {@link Integer}
     */
    public static Integer getInvalidBound(int... bounds) {
        for (int i : bounds) {
            if (i > MAX_RADIX || i < 2) return i;
        }
        return null;
    }

    public static char getInvalidHexInRadix(String hex, int radix) {
        hex = radix <= 36 ? hex.toUpperCase() : hex;
        for (char c : hex.replaceAll("\\s++|\\.", "").toCharArray()) {
            if (getHexFromSymbol(c).value >= radix) return c;
        }
        return (char) 0;
    }

    public static boolean hexOutsideRadix(String hex, int radix) {
        return (getInvalidHexInRadix(hex, radix) != (char) 0);
    }

    private static Instance getInstanceFromValue(BigDecimal value) throws IllegalArgumentException {
        try {
            return Instance.values()[value.intValue()];
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw new IllegalArgumentException("No HEX found with value " + value);
        }
    }

    private static Instance getHexFromSymbol(char symbol) throws IllegalStateException {
        for (Instance h : Instance.values())
            if (h.symbol == symbol) return h;
        throw new IllegalStateException(String.format("'%c' is not a valid Base64 Character!", symbol));
    }

    /**
     * Build an {@link AbstractNumber} from a pre-existing instance, keeping the numeric value, but
     * setting a different radix.
     * @param radix new radix to be set
     * @return a new {@link AbstractNumber} with a different radix field.
     */
    public AbstractNumber newRadix(int radix) {
        return new AbstractNumber(this.toNumber(), radix, this.isInteger, this.DECIMAL_LENGTH);
    }

    /**Quick conversion to Base 2*/
    public String toBinaryString() {
        return hex(2);
    }

    /**Quick conversion to Base 16*/
    public String toHexadecimalString() {
        return hex(16);
    }

    /**Calculate this instance's {@link #HEX} value*/
    public String hex() {
        return hex(this.RADIX);
    }

    private String hex(int radix) {
        BigDecimal integer = this.DECIMAL.setScale(0, RoundingMode.FLOOR);
        BigDecimal decimal = this.DECIMAL.subtract(this.DECIMAL.setScale(0, RoundingMode.FLOOR)).setScale(DECIMAL_LENGTH, RoundingMode.HALF_UP);

        String res = "";

        if (this.DECIMAL.compareTo(BigDecimal.ZERO) == 0) {
            return "0." + fillWithZeroes("", this.DECIMAL_LENGTH);
        }

        res = res.concat(integer.compareTo(BigDecimal.ZERO) == 0 ? "0" : toHexRecursion0(integer, "", radix));

        if (decimal.compareTo(BigDecimal.ZERO) == 0) {
            return this.isInteger ? res : res + DECIMAL_SPLIT + fillWithZeroes("", this.DECIMAL_LENGTH);
        }

        res += DECIMAL_SPLIT;

        if (this.DECIMAL.toString().length() - this.DECIMAL.toString().replaceAll("\\" + DECIMAL_SPLIT, "").length() == 1) {
            res = res.concat(fillWithZeroes(toHexRecursion1(decimal, "", 0, radix), this.DECIMAL_LENGTH));
        }
        return res;
    }

    /**
     * @see <a href="https://www.tutorialspoint.com/how-to-convert-binary-to-decimal" target="_blank">This Link</a> for
     * more detailed information on the math behind how this calculator works.
     */
    private String toHexRecursion0(BigDecimal bd, String base, int radix) {
        if (bd.equals(BigDecimal.ZERO)) return base;
        return toHexRecursion0(bd.divide(BigDecimal.valueOf(radix), 0, RoundingMode.FLOOR), 
                               getInstanceFromValue(bd.remainder(BigDecimal.valueOf(radix))).symbol + base, radix);
    }
    
    private String toHexRecursion1(BigDecimal bd, String base, int reps, int radix) {
        //Current decimal value (not doubled), fits between 0-1.
        BigDecimal decimal = bd.subtract(bd.setScale(0, RoundingMode.FLOOR));

        //End case
        if (decimal.compareTo(BigDecimal.ZERO) == 0 || reps >= DECIMAL_LENGTH) return base;

        decimal = decimal.multiply(BigDecimal.valueOf(radix));
        return toHexRecursion1(decimal, base + getInstanceFromValue(decimal.setScale(0, RoundingMode.FLOOR)).symbol, reps + 1, radix);
    }

    /**
     * Get the {@link BigDecimal} value associated with this instance.
     * @return {@link #DECIMAL decimal}
     */
    public BigDecimal toNumber() {
        String[] number = this.HEX.split("\\.").length == 2
                ? this.HEX.split("\\.")
                : new String[] {this.HEX, "0"};

        return toNumberRecursion0(number[0], BigDecimal.ZERO, number[0].length() - 1, 0, this.RADIX)
                .add(toNumberRecursion1(number[1], BigDecimal.ZERO, -1, 0, this.RADIX));
    }

    private BigDecimal toNumberRecursion0(String integerHex, BigDecimal val, int pow, int pos, int radix) {
        if (pos == integerHex.length()) return val;
        return toNumberRecursion0(integerHex, val.add(
                BigDecimal.valueOf(getHexFromSymbol(integerHex.charAt(pos)).value).multiply(BigDecimal.valueOf(radix).pow(pow, MathContext.DECIMAL128))
        ), pow - 1, pos + 1, radix);
    }

    private BigDecimal toNumberRecursion1(String decimalHex, BigDecimal val, int pow, int pos, int radix) {
        if (pos == decimalHex.length()) return val;
        return toNumberRecursion1(decimalHex, val.add(
                BigDecimal.valueOf(getHexFromSymbol(decimalHex.charAt(pos)).value).multiply(BigDecimal.valueOf(radix).pow(pow, MathContext.DECIMAL128))
        ), pow - 1, pos + 1, radix);
    }

    /**
     * @return a fancy {@link String} formatted with commas and a Negative (-) indicator
     */
    public String formatNumber() {
        return (isNegative() ? "(- NEG) " : "") +
                withCommas(this.DECIMAL.toString().split("\\.")[0]) +
                this.DECIMAL.subtract(this.DECIMAL.setScale(0, RoundingMode.FLOOR)).toString().substring(1);
    }

    private String withCommas(BigDecimal number) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < number.toString().length(); i++) {
            if (i % 3 == 0 && i > 0) res.append(THOUSAND_SPLIT);
            res.append(number.toString().charAt(number.toString().length() - 1 - i));
        }
        return res.reverse().toString();
    }

    @SuppressWarnings("SameParameterValue")
    private String repeatCharacters(String charSequence, int reps) {
        return String.valueOf(charSequence).repeat(Math.max(0, reps));
    }

    private String fillWithZeroes(String str, int newLength) {
        String s = str;
        for (int i = str.length(); i < newLength; i++) s = s.concat("0");
        return s;
    }

    /**@return The inverse of {@link #DECIMAL decimal} (-{@link #DECIMAL decimal})*/
    public AbstractNumber negate() {
        return new AbstractNumber(toNumber().negate(), this.RADIX);
    }

    /**
     * Get the status of this number; is it negative?
     * @return {@link #negative}
     */
    public boolean isNegative() {
        return negative;
    }

    /**
     * Creates a new instance of {@link AbstractNumber}, keeping all other fields, but allowing for
     * greater precision after the decimal point.
     * @param newDecimalLength The amount of digits (precision) to be built into the new instance.
     * @return An updates {@link AbstractNumber}
     */
    public AbstractNumber setPrecision(int newDecimalLength) {
        return new AbstractNumber(this.toNumber(), this.RADIX, this.isInteger, newDecimalLength);
    }

    /**
     * Compares {@code this} with another {@link AbstractNumber}.
     * This will only check for the number's value, ie. the independent {@link #DECIMAL decimal} variable.
     * @param abstractNumber the other number to be evaluated.
     * @return {@code -1} if this instance is numerically <u>less</u> than the other; {@code 0} if
     *         they are numerically <u>equal</u> to each other; {@code 1} if this instance is numerically <u>greater</u>
     *         than the other.
     */
    @Override
    public int compareTo(AbstractNumber abstractNumber) {
        return this.toNumber().compareTo(abstractNumber.toNumber());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractNumber number = (AbstractNumber) o;
        return negative == number.negative && BINARY.equals(number.BINARY) && DECIMAL.equals(number.DECIMAL);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(new Object[] {BINARY, DECIMAL, negative});
    }

    @Override
    public String toString() {
        return "AbstractNumber{" +
                "radix='" + this.RADIX + '\'' +
                ", hex='" + this.HEX + '\'' +
                ", binary='" + this.BINARY + '\'' +
                ", decimal='" + this.DECIMAL + '\'' +
                ", negative='" + this.negative + '\'' +
                '}';
    }
}
