package com.augment.golden.bulbcontrol.Beans.LifxApi;

import com.augment.golden.bulbcontrol.Common;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

class LifxWrapper {
    private static InetAddress getAddress(){
        InetAddress address = null;
        try{
            address = InetAddress.getByName("255.255.255.255");
        }catch (Exception e){
            e.printStackTrace();
        }

        return address;
    }

    private static byte[] sendMessage(byte[] message, boolean timeout){
        byte[] data = new byte[0];
        InetAddress address = getAddress();
        DatagramPacket packet = new DatagramPacket(message, message.length, address, 56700);
        try(DatagramSocket socket = new DatagramSocket()){
            byte buffer[] = new byte[256];
            socket.setSoTimeout(500);
            socket.send(packet);
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            data = packet.getData();
        }catch (Exception e){
            System.out.println("Message not Sent");
        }

        return data;
    }

    private static void sendMessage(byte[] message){
        InetAddress address = getAddress();
        DatagramPacket packet = new DatagramPacket(message, message.length, address, 56700);
        try(DatagramSocket socket = new DatagramSocket()){
            socket.send(packet);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Message not Sent");
        }
    }

    static void setPower(String hex, boolean on, int durationMili){
        byte[] message = PacketFactory.buildSetPowerMessage(on, durationMili, hex);
        sendMessage(message);
        sendMessage(message);
    }

    static void setHSBK(LifxBulb bulb){
        byte[] message = PacketFactory.buildSetHSBKMessage(bulb);
        sendMessage(message);
        sendMessage(message);
    }

    static HSBK getHSBK(String macAddress){
        HSBK hsbk = new HSBK();
        byte[] response;
        int i = 0;
        do {
            response = sendMessage(PacketFactory.buildGetHBSK(macAddress), true);
            i++;
        }while(response.length == 0 && i < 8);

        byte[] hsbkArr = Common.getSubArray(response, 37, response[0]);
        try{
            hsbk.setHue(((hsbkArr[0] & 0xff) << 8) | (hsbkArr[1] & 0xff));
            hsbk.setSaturation(((hsbkArr[2] & 0xff) << 8) | (hsbkArr[3] & 0xff));
            hsbk.setBrightness(((hsbkArr[4] & 0xff) << 8) | (hsbkArr[5] & 0xff));
            hsbk.setKelvin(((hsbkArr[6] & 0xff) << 8) | (hsbkArr[7] & 0xff));
        }catch (Exception e){
            e.printStackTrace();
        }

        return hsbk;
    }

    static String getLabel(String macAddress){
        byte[] response;
        do response = sendMessage(PacketFactory.buildGetLabelMessage(macAddress), true);
        while(response.length == 0);
        byte[] labelArr = Common.getSubArray(response, 36, response[0]);
        return new String(labelArr);
    }

    static String getGroup(String macAddress){
        byte[] response;
        do response = sendMessage(PacketFactory.buildGetGroupMessage(macAddress), true);
        while(response.length == 0);
        byte[] labelArr = Common.getSubArray(response, 52, 68);
        return new String(labelArr);
    }

    static List<String> getAllMacAddresses(){
        InetAddress address = getAddress();

        byte[] serviceMessage = PacketFactory.buildGetServiceMessage();
        List<byte[]> bytesList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            List<byte[]> tempList = new ArrayList<>();
            DatagramPacket packet = new DatagramPacket(serviceMessage, serviceMessage.length, address, 56700);
            try(DatagramSocket socket = new DatagramSocket()) {
                byte buffer[] = new byte[256];
                socket.setSoTimeout(500);
                socket.send(packet);
                while(true){
                    packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    tempList.add(packet.getData());
                }
            } catch (Exception e) {
                System.out.println("Service - Done");
            }
            bytesList.addAll(tempList);

        }
        List<String> macAddresses = new ArrayList<>(bytesList.size());
        for (byte[] bytes : bytesList)
            macAddresses.add(parseMACFromReturn(Common.convertByteArrToHex(bytes)));

        return macAddresses;
    }



    private static String parseMACFromReturn(String hex){
        int start = hex.indexOf("d073d5");
        int end = start + 12;
        if(start != -1 && end < hex.length())
            return hex.substring(start, end);

        return "";
    }
}
