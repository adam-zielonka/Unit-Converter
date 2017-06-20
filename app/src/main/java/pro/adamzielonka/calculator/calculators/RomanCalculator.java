package pro.adamzielonka.calculator.calculators;

public class RomanCalculator {

    public static final int POSITIVE_INFINITY_ROMAN = 4001;
    public static final int NEGATIVE_INFINITY_ROMAN = 4002;
    public static final int NAN_ROMAN = 4003;


    private String result;
    private String lastOperator;

    public RomanCalculator() {
        clear();
    }

    public void clear() {
        result = "";
        lastOperator = "=";
    }

    public String getLastOperator() {
        return lastOperator;
    }

    public String getMemory() {
        return result;
    }

    public String calculate(String number, String operator) {
        switch (lastOperator) {
            case "+":
                result = sum(result, number);
                break;
            case "-":
                result = difference(result, number);
                break;
            case "=":
            default:
                result = number;
                break;
        }
        lastOperator = operator;
        return result;
    }

    public String singleCalculate(String number, String operator) {
        switch (operator) {
            case "+-":
                return convertIntToRoman(convertRomanToInt(number) * (-1));
            default:
                return number;
        }
    }

    public String convertIntToRoman(int intNumber) {
        String sign = "";
        if (intNumber < 0) {
            intNumber *= (-1);
            sign = "-";
        }
        if (intNumber > 0 && intNumber < 4000) {
            StringBuilder romanNumber = new StringBuilder();
            String[] romans = new String[]{"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M"};
            int[] ints = new int[]{1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};
            for (int i = ints.length - 1; i >= 0; i--) {
                for (int j = intNumber / ints[i]; j > 0; j--) {
                    romanNumber.append(romans[i]);
                }
                intNumber %= ints[i];
            }
            return sign + romanNumber.toString();
        }
        if (intNumber == 0) return "";
        if (intNumber > 4000 && !sign.equals("-")) return "∞";
        return "-∞";
    }

    public int convertRomanToInt(String romanNumberIn) {
        switch (romanNumberIn) {
            case "∞":
                return POSITIVE_INFINITY_ROMAN;
            case "-∞":
                return NEGATIVE_INFINITY_ROMAN;
            case "NaN":
                return NAN_ROMAN;
        }
        if (romanNumberIn.length() == 0) return 0;
        String romanNumber = romanNumberIn.toUpperCase();
        int sign = 1;

        if (romanNumber.contains("-")) {
            sign = -1;
            romanNumber = romanNumber.replace("-", "");
        }

        if (!romanNumber.matches("^M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$"))
            return NAN_ROMAN;

        int intNumber = 0;
        int lastNumber = 0;
        int number;

        for (int i = romanNumber.length() - 1; i >= 0; i--) {
            switch (romanNumber.charAt(i)) {
                case 'M':
                    number = 1000;
                    break;
                case 'D':
                    number = 500;
                    break;
                case 'C':
                    number = 100;
                    break;
                case 'L':
                    number = 50;
                    break;
                case 'X':
                    number = 10;
                    break;
                case 'V':
                    number = 5;
                    break;
                case 'I':
                    number = 1;
                    break;
                default:
                    return NAN_ROMAN;
            }
            if (lastNumber <= number) {
                intNumber += number;
            } else {
                intNumber -= number;
            }
            lastNumber = number;
        }

        return sign * intNumber;
    }

    private String sum(String romanNumber1, String romanNumber2) {
        if (convertRomanToInt(romanNumber1) == NAN_ROMAN || convertRomanToInt(romanNumber2) == NAN_ROMAN)
            return "NaN";
        if (convertRomanToInt(romanNumber1) == NEGATIVE_INFINITY_ROMAN || convertRomanToInt(romanNumber2) == NEGATIVE_INFINITY_ROMAN)
            return "-∞";
        if (convertRomanToInt(romanNumber1) == POSITIVE_INFINITY_ROMAN || convertRomanToInt(romanNumber2) == POSITIVE_INFINITY_ROMAN)
            return "∞";
        return convertIntToRoman(convertRomanToInt(romanNumber1) + convertRomanToInt(romanNumber2));
    }

    private String difference(String romanNumber1, String romanNumber2) {
        if (convertRomanToInt(romanNumber1) == NAN_ROMAN || convertRomanToInt(romanNumber2) == NAN_ROMAN)
            return "NaN";
        if (convertRomanToInt(romanNumber1) == NEGATIVE_INFINITY_ROMAN || convertRomanToInt(romanNumber2) == NEGATIVE_INFINITY_ROMAN)
            return "-∞";
        if (convertRomanToInt(romanNumber1) == POSITIVE_INFINITY_ROMAN || convertRomanToInt(romanNumber2) == POSITIVE_INFINITY_ROMAN)
            return "∞";
        return convertIntToRoman(convertRomanToInt(romanNumber1) - convertRomanToInt(romanNumber2));
    }

}
