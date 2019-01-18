package com.augment.golden.bulbcontrol.Beans;

public class BuildMessage {
    public static byte[] getPower(){
        String hex = "24 00 00 34 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 74 00 00 00";
        return hexStringToByteArray(hex.replace(" ", ""));
    }
    public static byte[] getBrightness(){
        String hex = "24 00 00 34 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 74 00 00 00";
        return hexStringToByteArray(hex.replace(" ", ""));
    }

    public static byte[] changeBrightness(int amount){
        String start        = "0034";
        String source       = "00000000";

        String target       = "0000000000000000";
        String reserved1    = "000000000000";
        String ackRes       = "00";
        String sequence     = "66";

        String reserved2    = "0000000000000000";
        String messageType  = "6600";
        String reserved3    = "0000";

        String header = start + source + target + reserved1 + ackRes + sequence + reserved2 + messageType + reserved3 + "FFFF00040000";
        String length = decimal2hex((header.length() + 4) / 2);
        header = length  + "00" + header;

        System.out.println(header);
        return hexStringToByteArray(header);
    }

    public static byte[] turnOnPower(){
        String start        = "0034";
        String source       = "00000000";

        String target       = "0000000000000000";
        String reserved1    = "000000000000";
        String ackRes       = "00";
        String sequence     = "66";

        String reserved2    = "0000000000000000";
        String messageType  = "7500";
        String reserved3    = "0000";

        String header = start + source + target + reserved1 + ackRes + sequence + reserved2 + messageType + reserved3 + "FFFF00040000";
        String length = decimal2hex((header.length() + 4) / 2);
        header = length  + "00" + header;

        return hexStringToByteArray(header);
    }

    public static byte[] turnOffPower(){
        String start        = "0034";
        String source       = "00000000";

        String target       = "0000000000000000";
        String reserved1    = "000000000000";
        String ackRes       = "00";
        String sequence     = "66";

        String reserved2    = "0000000000000000";
        String messageType  = "7500";
        String reserved3    = "0000";

        String header = start + source + target + reserved1 + ackRes + sequence + reserved2 + messageType + reserved3 + "000000040000";
        String length = decimal2hex((header.length() + 4) / 2);
        header = length  + "00" + header;

        return hexStringToByteArray(header);
    }


    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static String decimal2hex(int d) {
        String digits = "0123456789ABCDEF";
        if (d <= 0) return "0";
        int base = 16;   // flexible to change in any base under 16
        String hex = "";
        while (d > 0) {
            int digit = d % base;              // rightmost digit
            hex = digits.charAt(digit) + hex;  // string concatenation
            d = d / base;
        }
        return hex;
    }
}
