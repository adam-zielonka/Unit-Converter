package pro.adamzielonka.calculator.classes;

public class ByteConverter extends Converter {

    private double bitToByte(double number) {
        return number / 8.0;
    }

    private double byteToBit(double number) {
        return number * 8.0;
    }

    private double convertByte(double number, int divide, int multiple) {
        double result = number;
        for(int i=0;i<multiple;i++)
            result *= 1024.0;
        for(int i=0;i<divide;i++)
            result /= 1024.0;
        return result;
    }

    public double calculate(double number, String from, String to) {
        switch (from +" "+ to) {
            case "b B": return bitToByte(number);
            case "b KB": return convertByte(bitToByte(number),1,0);
            case "b MB": return convertByte(bitToByte(number),2,0);
            case "b GB": return convertByte(bitToByte(number),3,0);
            case "b TB": return convertByte(bitToByte(number),4,0);
            case "b PB": return convertByte(bitToByte(number),5,0);
            case "b EB": return convertByte(bitToByte(number),6,0);
            case "b ZB": return convertByte(bitToByte(number),7,0);
            case "b YB": return convertByte(bitToByte(number),8,0);

            case "B b": return byteToBit(number);
            case "B KB": return convertByte(number,1,0);
            case "B MB": return convertByte(number,2,0);
            case "B GB": return convertByte(number,3,0);
            case "B TB": return convertByte(number,4,0);
            case "B PB": return convertByte(number,5,0);
            case "B EB": return convertByte(number,6,0);
            case "B ZB": return convertByte(number,7,0);
            case "B YB": return convertByte(number,8,0);

            case "KB b": return byteToBit(convertByte(number,0,1));
            case "KB B": return convertByte(number,0,1);
            case "KB MB": return convertByte(number,1,0);
            case "KB GB": return convertByte(number,2,0);
            case "KB TB": return convertByte(number,3,0);
            case "KB PB": return convertByte(number,4,0);
            case "KB EB": return convertByte(number,5,0);
            case "KB ZB": return convertByte(number,6,0);
            case "KB YB": return convertByte(number,7,0);

            case "MB b": return byteToBit(convertByte(number,0,2));
            case "MB B": return convertByte(number,0,2);
            case "MB KB": return convertByte(number,0,1);
            case "MB GB": return convertByte(number,1,0);
            case "MB TB": return convertByte(number,2,0);
            case "MB PB": return convertByte(number,3,0);
            case "MB EB": return convertByte(number,4,0);
            case "MB ZB": return convertByte(number,5,0);
            case "MB YB": return convertByte(number,6,0);

            case "GB b": return byteToBit(convertByte(number,0,3));
            case "GB B": return convertByte(number,0,3);
            case "GB KB": return convertByte(number,0,2);
            case "GB MB": return convertByte(number,0,1);
            case "GB TB": return convertByte(number,1,0);
            case "GB PB": return convertByte(number,2,0);
            case "GB EB": return convertByte(number,3,0);
            case "GB ZB": return convertByte(number,4,0);
            case "GB YB": return convertByte(number,5,0);

            case "TB b": return byteToBit(convertByte(number,0,4));
            case "TB B": return convertByte(number,0,4);
            case "TB KB": return convertByte(number,0,3);
            case "TB MB": return convertByte(number,0,2);
            case "TB GB": return convertByte(number,0,1);
            case "TB PB": return convertByte(number,1,0);
            case "TB EB": return convertByte(number,2,0);
            case "TB ZB": return convertByte(number,3,0);
            case "TB YB": return convertByte(number,4,0);

            case "PB b": return byteToBit(convertByte(number,0,5));
            case "PB B": return convertByte(number,0,5);
            case "PB KB": return convertByte(number,0,4);
            case "PB MB": return convertByte(number,0,3);
            case "PB GB": return convertByte(number,0,2);
            case "PB TB": return convertByte(number,0,1);
            case "PB EB": return convertByte(number,1,0);
            case "PB ZB": return convertByte(number,2,0);
            case "PB YB": return convertByte(number,3,0);

            case "EB b": return byteToBit(convertByte(number,0,6));
            case "EB B": return convertByte(number,0,6);
            case "EB KB": return convertByte(number,0,5);
            case "EB MB": return convertByte(number,0,4);
            case "EB GB": return convertByte(number,0,3);
            case "EB TB": return convertByte(number,0,2);
            case "EB PB": return convertByte(number,0,1);
            case "EB ZB": return convertByte(number,1,0);
            case "EB YB": return convertByte(number,2,0);

            case "ZB b": return byteToBit(convertByte(number,0,7));
            case "ZB B": return convertByte(number,0,7);
            case "ZB KB": return convertByte(number,0,6);
            case "ZB MB": return convertByte(number,0,5);
            case "ZB GB": return convertByte(number,0,4);
            case "ZB TB": return convertByte(number,0,3);
            case "ZB PB": return convertByte(number,0,2);
            case "ZB EB": return convertByte(number,0,1);
            case "ZB YB": return convertByte(number,1,0);

            case "YB b": return byteToBit(convertByte(number,0,8));
            case "YB B": return convertByte(number,0,8);
            case "YB KB": return convertByte(number,0,7);
            case "YB MB": return convertByte(number,0,6);
            case "YB GB": return convertByte(number,0,5);
            case "YB TB": return convertByte(number,0,4);
            case "YB PB": return convertByte(number,0,3);
            case "YB EB": return convertByte(number,0,2);
            case "YB ZB": return convertByte(number,0,1);

            default: return number;
        }
    }
}
