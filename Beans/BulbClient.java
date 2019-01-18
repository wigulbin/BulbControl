package com.augment.golden.bulbcontrol.Beans;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class BulbClient{
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public BulbClient(){
        try{
            socket = new DatagramSocket();
            address = InetAddress.getByName("255.255.255.255");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(){
        try {
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 56700);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public void sendMessage(byte[] bytes){
        try {
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 56700);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public byte[] sendAndReceiveMessage(byte[] bytes){
        try {
            byte buffer[] = new byte[256];
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 56700);
            socket.send(packet);
            packet = new DatagramPacket(buffer, buffer.length);
            socket.setSoTimeout(500);
            socket.receive(packet);
            return packet.getData();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }

        return new byte[0];
    }

    public List<byte[]> searchForBulbs(byte[] bytes){
        List<byte[]> bytesList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            List<byte[]> tempList = new ArrayList<>();
            try {
                byte buffer[] = new byte[256];
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 56700);
                DatagramSocket socket = new DatagramSocket();
                socket.setSoTimeout(500);
                socket.send(packet);
                while(true){
                    packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    tempList.add(packet.getData());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            bytesList.addAll(tempList);

        }

        return bytesList;
    }

    public byte[] sendAndReceiveUntilRes(byte[] bytes){
        byte[] retBytes = new byte[0];
        while(retBytes.length == 0)
            retBytes = sendAndReceiveMessage(bytes);

        return retBytes;
    }

    public int getBrightness(){
        buf = BuildMessage.turnOnPower();
//        String hex = sendAndReceiveMessage();

        return 55;
    }

    private byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }
}
