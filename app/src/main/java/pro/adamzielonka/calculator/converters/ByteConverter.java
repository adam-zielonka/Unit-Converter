package pro.adamzielonka.calculator.converters;

import pro.adamzielonka.calculator.abstractes.Converter;

public class ByteConverter extends Converter {

    private double bitToByte(double number) {
        return number / 8.0;
    }

    private double byteToBit(double number) {
        return number * 8.0;
    }

    private double convertByte(double number, int divide, int multiple) {
        double result = number;
        for (int i = 0; i < multiple; i++)
            result *= 1024.0;
        for (int i = 0; i < divide; i++)
            result /= 1024.0;
        return result;
    }

    private int letterToNumber(String letter) {
        switch (letter) {
            case "b":
                return 0;
            case "B":
                return 1;
            case "KB":
                return 2;
            case "MB":
                return 3;
            case "GB":
                return 4;
            case "TB":
                return 5;
            case "PB":
                return 6;
            case "EB":
                return 7;
            case "ZB":
                return 8;
            case "YB":
                return 9;
            default:
                return -1;
        }
    }

    public double calculate(double number, String from, String to) {
        if (from.equals(to))
            return number;

        int fromNumber = letterToNumber(from);
        int toNumber = letterToNumber(to);

        if (fromNumber == -1 || toNumber == -1)
            return number;

        double result;
        boolean isToBit = false;

        if (fromNumber == 0) {
            result = bitToByte(number);
            fromNumber++;
        } else
            result = number;

        if (toNumber == 0) {
            isToBit = true;
            toNumber++;
        }

        if (toNumber > fromNumber)
            result = convertByte(result, toNumber - fromNumber, 0);
        else
            result = convertByte(result, 0, (-1) * (toNumber - fromNumber));

        if (isToBit)
            result = byteToBit(result);

        return result;
    }
}
