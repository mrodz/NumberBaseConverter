package converter;

public class Main {
    private static final java.util.Scanner scanner = new java.util.Scanner(System.in).useLocale(java.util.Locale.US);

    public static void main(String[] args) {
        menu();
    }

    private static void menu() {
        String[] userInput = prompt();

        if (!Main.matches(userInput, "\\d++")) {
            System.out.println("\nPlease enter numbers!\n");
            menu();
            return;
        }

        if (userInput.length != 2) {
            System.out.println("\nPlease enter two parameters!\n");
            menu();
            return;
        }

        int sourceBase = Integer.parseInt(userInput[0]);
        int targetBase = Integer.parseInt(userInput[1]);

        if (AbstractNumber.areInvalidBounds(sourceBase, targetBase) > 0) {
            System.out.printf("This converter only supports up to Base64! (Your input: %d)\n\n",
                    AbstractNumber.getInvalidBound(sourceBase, targetBase));
            menu();
            return;
        } else if (AbstractNumber.areInvalidBounds(sourceBase, targetBase) < 0) {
            System.out.printf("Invalid radix %d: One cannot form a number out of anything less than Base 2\n\n",
                    AbstractNumber.getInvalidBound(sourceBase, targetBase));
            menu();
            return;
        }

        for (int i = 0; i < 1e6; i++) {
            System.out.printf("%nEnter number in base %d to convert to base %d (To go back type /back) ",
                    sourceBase, targetBase);
            String numInput = scanner.next();

            if (!numInput.matches("\\d++") && numInput.replaceAll("\\s++", "").equalsIgnoreCase("/back")) {
                scanner.nextLine();
                System.out.println("...\n..\n.\n");
                menu();
                return;
            }

            if (AbstractNumber.hexOutsideRadix(numInput.charAt(0) == '-' ? numInput.substring(1) : numInput, sourceBase)) {
                System.out.printf("Error! Digit '%c' is greater than or equal to upper radix bound %d%n",
                        AbstractNumber.getInvalidHexInRadix(numInput, sourceBase),
                        sourceBase
                );
            } else {
                AbstractNumber num = new AbstractNumber(numInput, sourceBase).setPrecision(5);
                System.out.printf("Conversion result: %s%n%n%n", num.newRadix(targetBase).hex());
            }
        }
        System.out.println("This is enough!");
        System.exit(1);
    }

    /**Asks the user for input.*/
    private static String[] prompt() {
        System.out.print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ");
        String[] input = scanner.nextLine().split("\\s++");
        if (input.length > 0 && input[0].replaceAll("\\s++", "").equalsIgnoreCase("/exit")) {
            System.exit(0);
        }

        if (!(input.length >= 1 && input.length <= 2)) {
            System.out.printf("Wrong input! Found: \"%s\"\n\nPlease try again!\n", buildString(input));
            return prompt();
        } else {
            return input;
        }
    }

    private static String buildString(String... strings) {
        String str = "";
        java.util.Arrays.stream(strings).forEach(n -> append(str, n, " "));
        return str;
    }

    private static void append(String base, String... strings) {
        for (String s : strings) base = base.concat(s);
    }

    /**O(n^2)*/
    @SafeVarargs
    private static <T extends String, K extends String>boolean matches(T[] array, K... patterns) {
        for (T element : array) {
            for (K pattern : patterns) {
                if (element.matches(pattern)) return true;
            }
        }
        return false;
    }
}

