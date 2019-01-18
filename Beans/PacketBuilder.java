package com.augment.golden.bulbcontrol.Beans;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/*
Header - 36 bytes
Payload - 2 bytes reserved
 */

public class PacketBuilder {
    private BitSet bits;
    private List<Byte> byteList;

    private BitSet payloadBits;

    private PacketBuilder(){
        bits = new BitSet();
        byteList = new ArrayList<>();
        byteList.add(((byte) 0));
        byteList.add(((byte) 0));
    }

    public static PacketBuilder buildPacket(boolean tagged){
        PacketBuilder builder = new PacketBuilder();
        builder.createHeader(tagged);
        return builder;
    }

    public static PacketBuilder buildPacket(){
        PacketBuilder builder = new PacketBuilder();
        builder.createHeader(true);
        return builder;
    }

    public byte[] getByteArray(){
        List<Byte> bytes = new ArrayList<>();
        byte[] byteArr = new byte[bytes.size()];

        byte[] byteArray = bits.toByteArray();
        byteArray[0] = (byte)byteArray.length;

        return byteArray;
    }
    public List<Byte> getByteList(){
        List<Byte> bytes = new ArrayList<>(getHeaderBytes());
        return bytes;
    }

    public byte[] getByteArrayFromList(){
        byte[] bytes = new byte[byteList.size()];
        for(int i = 0; i < byteList.size(); i++)
            bytes[i] = byteList.get(i);

        return bytes;
    }

    private List<Byte> getHeaderBytes(){
        List<Byte> byteList = new ArrayList<>(36);
        byte[] bytes = bits.toByteArray();
        for (byte aByte : bytes)
            byteList.add(aByte);

        while(byteList.size() < 36)
            byteList.add((byte) 0);

        setLength(byteList);
        return byteList;
    }

    private void setLength(List<Byte> byteList){
        byteList.set(0, (byte)byteList.size());
        byteList.set(1, (byte)(byteList.size() >>> 8));
    }

    private void createEmptyPayload(){
        payloadBits = new BitSet();

    }

    public void createHeader(boolean tagged){
        setProtocol();
        setAddressable(true);
        setTagged(tagged);
        setSource();

        byteList = getByteList();
    }

    private void setTagged(boolean on){
        setBits(on, 29);
    }
    private void setAddressable(boolean on){
        setBits(on, 28);
    }

    private void setProtocol(){
        addBitsFromInt(1024, 16);
    }

    private void setSource(){
        //TODO create unique value
        addBitsFromInt(2147483633, 32);
    }

    public void setTarget(String macHex){
        //TODO allow for target to be set starts at bit 64
        List<Integer> ints = convertHexToInts(macHex);
        AtomicInteger start = new AtomicInteger(64);
//        for(int i = ints.size()-1; i >= 0; i--){
        for(int i = 0; i < ints.size(); i++){
            addBitsFromInt(ints.get(i), start.getAndAdd(8));

        }
        setTagged(false);
    }

    private void setAckRequired(){
        bits.set(182);
    }
    private void setResRequired(){
        bits.set(183);
    }
    private void setSequence(){

    }

    private void setMessage(int message){
        addBitsFromInt(message, 256);
        byteList = getByteList();
    }


    public void addBitsFromInt(int value, int start){
        int counter = start;
        while(value > 0){
            if(value % 2 != 0)
                bits.set(counter);
            counter++;
            value = value >>> 1;
        }
    }

    private BitSet createBitFromInt(int value){
        BitSet bitSet = new BitSet();
        int counter = 0;
        while(value > 0){
            if(value % 2 != 0)
                bitSet.set(counter);
            counter++;
            value = value >>> 1;
        }

        return bitSet;
    }

    private List<Integer> convertHexToInts(String hex){
        List<Integer> ints = new ArrayList<>(hex.length()/2);
        for(int i = 0; i < hex.length(); i+=2)
            ints.add(Integer.parseInt(hex.substring(i, i + 2), 16));

        return ints;
    }

    public static byte[] getSetPowerMessage(boolean on, int durationMili){
        PacketBuilder builder = PacketBuilder.buildPacket();
        return builder.setPower(on, durationMili);
    }


    public static byte[] setPowerMessage(boolean on, int durationMili, String hex){
        PacketBuilder builder = PacketBuilder.buildPacket(false);
        builder.setTarget(hex);
        return builder.setPower(on, durationMili);
    }
    public byte[] setPower(boolean on, int durationMili){
        setMessage(117);

        int num = on ? 65535 : 0;
        List<Byte> bytes = createBytesFromInt(num, 2);
        bytes.addAll(createBytesFromInt(durationMili, 4));
        byteList.addAll(bytes);
        setLength(byteList);

        return getByteArrayFromList();
    }

    public static byte[] getServiceMessage(){
        PacketBuilder builder = PacketBuilder.buildPacket();
        return builder.getService();
    }
    public byte[] getService(){
        setMessage(2);
        setLength(byteList);

        return getByteArrayFromList();
    }

    public static byte[] getLabelMessage(String hex){
        PacketBuilder builder = PacketBuilder.buildPacket(false);
        builder.setTarget(hex);
        return builder.getLabel();
    }
    public byte[] getLabel(){
        setMessage(23);
        setLength(byteList);

        return getByteArrayFromList();
    }

    public static byte[] setBrightnessMessage(int brightness, String hex){
        PacketBuilder builder = PacketBuilder.buildPacket(false);
        builder.setTarget(hex);
        return builder.setBrightness(brightness);
    }

    public byte[] setBrightness(int brightness){
        setMessage(102);

        List<Byte> bytes = new ArrayList<>();
        addEmptyBytes(1, bytes);
        addEmptyBytes(2, bytes);
        addEmptyBytes(2, bytes);
        bytes.addAll(createBytesFromInt(brightness, 2));
        addEmptyBytes(2, bytes);
        bytes.addAll(createBytesFromInt(1024, 4));
        byteList.addAll(bytes);
        setLength(byteList);

        return getByteArrayFromList();
    }

    private void setHSBK(List<Byte> bytes, int h, int s, int b, int k){
        if(h == 0)
            addEmptyBytes(2, bytes);
        else
            bytes.addAll(createBytesFromInt(h, 2));
        if(s == 0)
            addEmptyBytes(2, bytes);
        else
            bytes.addAll(createBytesFromInt(s, 2));
        if(b == 0)
            addEmptyBytes(2, bytes);
        else
            bytes.addAll(createBytesFromInt(b, 2));
        if(k == 0)
            addEmptyBytes(2, bytes);
        else
            bytes.addAll(createBytesFromInt(k, 2));


    }


    private void addEmptyBytes(int byteNum, List<Byte> bytes){
        for(int i = 0; i < byteNum; i++)
            bytes.add((byte) 0);
    }




    public static List<Byte> createBytesFromInt(int num, int byteNum){
        List<Byte> bytes = new ArrayList<>(4);
        for(int i = 0; i < byteNum*8; i+=8)
            bytes.add((byte)(num>>>i));

        return bytes;
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

    private void setBits(boolean on, int bit){
        if(on)
            bits.set(bit);
        else
            bits.clear(bit);
    }
}