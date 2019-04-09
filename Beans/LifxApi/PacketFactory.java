package com.augment.golden.bulbcontrol.Beans.LifxApi;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class PacketFactory {
    private BitSet bits;
    private List<Byte> byteList;

    private PacketFactory(){
        bits = new BitSet();
        byteList = new ArrayList<>();

        //Initialize length bytes
        byteList.add(((byte) 0));
        byteList.add(((byte) 0));

        createHeader(true);
    }
    private PacketFactory(boolean tagged){
        bits = new BitSet();
        byteList = new ArrayList<>();

        //Initialize length bytes
        byteList.add(((byte) 0));
        byteList.add(((byte) 0));

        createHeader(tagged);
    }

    private void createHeader(boolean tagged){
        setProtocol();
        setAddressable(true);
        setTagged(tagged);
        setSource();

        updateByteList();
    }
    //Header functions
    private void setProtocol(){
        addBitsFromInt(1024, 16);
    }
    private void setAddressable(boolean on){
        setBits(on, 28);
    }
    private void setTagged(boolean on){
        setBits(on, 29);
    }
    private void setSource(){
        addBitsFromInt(2147483633, 32);
    }
    private void setMessage(int message){
        addBitsFromInt(message, 256);
        updateByteList();
    }
    private void setTarget(String macHex){
        List<Integer> ints = convertHexToInts(macHex);
        AtomicInteger start = new AtomicInteger(64);
        for(int i = 0; i < ints.size(); i++)
            addBitsFromInt(ints.get(i), start.getAndAdd(8));
        setTagged(false);
    }

    private void setAckRequired(){
        bits.set(182);
    }
    private void setResRequired(){
        bits.set(183);
    }
    private void setSequence(){
        addBitsFromInt(1000, 176);
    }


    //Messages
    static byte[] buildSetPowerMessage(boolean on, int durationMili){
        return buildSetPowerMessage(on, durationMili, null);
    }
    static byte[] buildSetPowerMessage(boolean on, int durationMili, String hex){
        PacketFactory factory = new PacketFactory();

        if(hex != null) factory.setTarget(hex);
        factory.setMessage(117);
        factory.setResRequired();
        factory.setSequence();
        List<Byte> bytes = createBytesFromInt(on ? 65535 : 0, 2);
        bytes.addAll(createBytesFromInt(100, 4));
        factory.addAll(bytes);
        factory.setLength();

        return factory.getByteArrayFromList();
    }

    static byte[] buildSetHSBKMessage(LifxBulb bulb){
        PacketFactory factory = new PacketFactory();
        factory.setMessage(102);
        factory.setTarget(bulb.getId());
        factory.byteList.add((byte) 0);
        factory.setHSBK(bulb.getHue(), bulb.getSaturation(), bulb.getBrightness(), bulb.getKelvin());

        factory.byteList.add((byte) 0);
        factory.byteList.add((byte) 0);
        factory.byteList.add((byte) 0);
        factory.byteList.add((byte) 0);

        factory.setLength();
        return factory.getByteArrayFromList();
    }

    static byte[] buildGetHBSK(String hex){
        PacketFactory factory = new PacketFactory();
        factory.setTarget(hex);
        factory.setMessage(101);
        factory.setLength();
        return factory.getByteArrayFromList();
    }

    static byte[] buildGetLabelMessage(String macAddress){
        PacketFactory factory = new PacketFactory();
        factory.setTarget(macAddress);
        factory.setMessage(23);
        factory.setLength();
        return factory.getByteArrayFromList();
    }

    static byte[] buildGetGroupMessage(String macAddress){
        PacketFactory factory = new PacketFactory();
        factory.setTarget(macAddress);
        factory.setMessage(51);
        factory.setLength();
        return factory.getByteArrayFromList();
    }

    static byte[] buildGetServiceMessage(){
        PacketFactory factory = new PacketFactory();
        factory.setMessage(2);
        factory.setLength();
        return factory.getByteArrayFromList();
    }

    //Utility Functions
    private void setHSBK(int h, int s, int b, int k){
        List<Byte> bytes = this.byteList;
        bytes.add((byte)(h & 0xff));
        bytes.add((byte)((h>>>8) & 0xff));

        bytes.add((byte)(s & 0xff));
        bytes.add((byte)((s>>>8) & 0xff));

        bytes.add((byte)(b & 0xff));
        bytes.add((byte)((b>>>8) & 0xff));

        bytes.add((byte)(k & 0xff));
        bytes.add((byte)((k>>>8) & 0xff));
    }
    private byte[] getByteArrayFromList(){
        List<Byte> byteList = this.byteList;
        byte[] bytes = new byte[byteList.size()];
        for(int i = 0; i < byteList.size(); i++)
            bytes[i] = byteList.get(i);

        return bytes;
    }
    private void addAll(List<Byte> bytes){
        this.byteList.addAll(bytes);
    }
    private List<Integer> convertHexToInts(String hex){
        List<Integer> ints = new ArrayList<>(hex.length()/2);
        for(int i = 0; i < hex.length(); i+=2)
            ints.add(Integer.parseInt(hex.substring(i, i + 2), 16));

        return ints;
    }
    private void setLength(){
        byteList.set(0, (byte) byteList.size());
        byteList.set(1, (byte)(byteList.size() >>> 8));
    }
    private void updateByteList(){
        List<Byte> byteList = new ArrayList<>(36);
        byte[] bytes = bits.toByteArray();
        for (byte aByte : bytes)
            byteList.add(aByte);

        while(byteList.size() < 36)
            byteList.add((byte) 0);

        setLength();
        this.byteList = byteList;
    }

    private void addBitsFromInt(int value, int start){
        int counter = start;
        while(value > 0){
            if(value % 2 != 0)
                bits.set(counter);
            counter++;
            value = value >>> 1;
        }
    }
    private void setBits(boolean on, int bit){
        if(on)
            bits.set(bit);
        else
            bits.clear(bit);
    }
    private void addEmptyBytes(int byteNum, List<Byte> bytes){
        for(int i = 0; i < byteNum; i++)
            bytes.add((byte) 0);
    }


    //Static Utilities
    private static List<Byte> createBytesFromInt(int num, int byteNum){
        List<Byte> bytes = new ArrayList<>();
        for(int i = 0; i < byteNum*8; i+=8)
            bytes.add((byte)(num>>>i));

        return bytes;
    }
}
