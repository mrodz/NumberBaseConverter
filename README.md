# NumberBaseConverter
HyperSkill's Number Base Converter Project

Easily convert from any base within the range of 2 (Binary) and 64 (Base64).
The ` Main.java ` file contains an example implementation of this program, but the ` AbstractNumber ` class contains a lot of helpful base/numerical conversions.  
  
# AbstractNumber
### Constructors:
<ol>
  <li><tt>new AbstractNumber(String, int)</tt><br/>&nbsp; &nbsp; &nbsp; - where <tt>String</tt> is a valid 'number' that matches the regex <tt>[a-zA-Z0-9+\]</tt>, and <tt>int</tt> is the radix (base) of the number</li>
  <li><tt>new AbstractNumber(BigDecimal)</tt><br/>&nbsp; &nbsp; &nbsp; - where <tt>BigDecimal</tt> takes a <u>BigDecimal</u> as its only parameter. The radix defaults to <tt>10</tt></li>
  <li><tt><strike>new AbstractNumber(long)</strike></tt><br/>&nbsp; &nbsp; &nbsp; @Deprecated - use <tt>AbstractNumber.valueOf(long)</tt> instead</li>
</ol>

### Methods:
##### Static -
<!-- 5 total -->
<ul>
  <li><tt>valueOf(long)</tt> - returns a new instance of <tt>AbstractNumber</tt> with a radix of <tt>10</tt></li>
  <li><tt>areInvalidBounds(int...)</tt> - returns <tt>0</tt> if the supplied <tt>ints</tt> fit within the maximum radix allowed in this program, <tt>1</tt> if at least one of these values exceeds the upper radix bound, and <tt>-1</tt> if at least one of these bounds is <= <tt>2</tt> </li>
  <li><tt>getInvalidBound(int...)</tt> - returns the first <tt>int</tt> <i>(boxed)</i> that is found outside of the bounds, if present; otherwise <tt>null</tt></li>
  <li><tt>hexOutsideRadix(String, int)</tt> - returns <tt>true</tt> if the hex (<tt>String</tt>) does not fit within the radix bound defined by <tt>int</tt></li>
  <li><tt>getInvalidHexInRadix(String, int)</tt> - returns the first <tt>char</tt> within <tt>String</tt> that does not fit within the <tt>int</tt> radix if present; otherwise, <tt>(char) 0</tt></li>
</ul>

##### Instance -
<!-- 13 total -->
<ul>
  <li><tt>toNumber()</tt> - returns this instance's numerical value in Base 10 as a <tt>BigDecimal</tt></li>
  <li><tt>negate()</tt> - returns the inverse (-) of this instance</li>
  <li><tt>newRadix(int)</tt> - returns a new instance of this object, preserving its numerical value, but altering the <tt>radix</tt> field</li>
  <li><tt>isNegative()</tt> - returns <tt>true</tt> if this number is negative; <tt>false</tt> if not</li>
  <li><tt>formatNumber()</tt> - returns a fancy <tt>String</tt> detailing this abstract number, which contains whether it is a negative followed by this number formatted with commas.</li>
  <li><tt>setPrecision(int)</tt> - returns a new instance of this object, copying all of its fields but setting a new precision (the amount of spaces after the decimal point to be accounted for) </li>
  <li><tt>toBinaryString()</tt> - returns this number's binary value (Base 2) as a <tt>String</tt></li>
  <li><tt>toHexadecimalString()</tt> - returns this number's hexadecimal value (Base 16) as a <tt>String</tt></li>
  <li><tt>hex()</tt> - returns this number's 'hex' value. This is a <tt>String</tt> containing the visual representation of this number in accordance with the specified <tt>radix</tt> parameter</li>
  
  <li><tt>toString()</tt> - returns a <tt>String</tt> with the format: AbstractNumber{radix='%d', hex='%s', decimal='%s', negative='%b'}</li>
  <li><tt>equals(Object)</tt> - returns <tt>true</tt> if <tt>Object</tt> is an instance of <tt>AbstractNumber</tt> and the two objects share a numerical value</li>
  <li><tt>compareTo(AbstractNumber)</tt> - returns [-1, 0, 1] following <tt>BigDecimal.compareTo(BigDecimal)</tt></li>
  <li><tt>hashCode()</tt> - returns a hash of this instance</li>
</ul>

### Fields:
<!-- As of now, these don't really serve a purpose. However, they are still fully functional and can be used for testing or for other operations -->
<ul>
  <li><tt>AbstractNumber.ZERO</tt> - constant instance with a numerical value of <tt>0</tt> and a radix of <tt>10</tt></li>
  <li><tt>AbstractNumber.ONE</tt> - constant instance with a numerical value of <tt>1</tt> and a radix of <tt>10</tt></li>
  <li><tt>AbstractNumber.TWO</tt> - constant instance with a numerical value of <tt>2</tt> and a radix of <tt>10</tt></li>
  <li><tt>AbstractNumber.TEN</tt> - constant instance with both a numerical value of <tt>10</tt> and a radix of <tt>10</tt></li>
</ul>


# Character Lookup Table
| Symbol | Decimal <br/>Numeric Value |
|:------:|:--------------------------:|
|    0   |              0             |
|    1   |              1             |
|    2   |              2             |
|    3   |              3             |
|    4   |              4             |
|    5   |              5             |
|    6   |              6             |
|    7   |              7             |
|    8   |              8             |
|    9   |              9             |
|    A   |              10            |
|    B   |              11            |
|    C   |              12            |
|    D   |              13            |
|    E   |              14            |
|    F   |              15            |
|    G   |              16            |
|    H   |              17            |
|    I   |              18            |
|    J   |              19            |
|    K   |              20            |
|    L   |              21            |
|    M   |              22            |
|    N   |              23            |
|    O   |              24            |
|    P   |              25            |
|    Q   |              26            |
|    R   |              27            |
|    S   |              28            |
|    T   |              29            |
|    U   |              30            |
|    V   |              31            |
|    W   |              32            |
|    X   |              33            |
|    Y   |              34            |
|    Z   |              35            |
|    a   |              36            |
|    b   |              37            |
|    c   |              38            |
|    d   |              39            |
|    e   |              40            |
|    f   |              41            |
|    g   |              42            |
|    h   |              43            |
|    i   |              44            |
|    j   |              45            |
|    k   |              46            |
|    l   |              47            |
|    m   |              48            |
|    n   |              49            |
|    o   |              50            |
|    p   |              51            |
|    q   |              52            |
|    r   |              53            |
|    s   |              54            |
|    t   |              55            |
|    u   |              56            |
|    v   |              57            |
|    w   |              58            |
|    x   |              59            |
|    y   |              60            |
|    z   |              61            |
|    +   |              62            |
|    \   |              63            |

### Thanks for taking a look at this program!
