package com.augment.golden.bulbcontrol.Beans.LifxApi;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MessageSender {
    private LifxBulb m_bulb;
    private InetAddress address;

    public MessageSender(){
        m_bulb = null;
        try{
            address = InetAddress.getByName("255.255.255.255");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public MessageSender(LifxBulb bulb){
        m_bulb = bulb;
        try{
            address = InetAddress.getByName("255.255.255.255");
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    private List<byte[]> findBulbs(){
        byte[] serviceMessage = PacketFactory.buildGetServiceMessage();
        List<byte[]> bytesList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            List<byte[]> tempList = new ArrayList<>();
            DatagramPacket packet = new DatagramPacket(serviceMessage, serviceMessage.length, address, 56700);
            DatagramSocket socket = null;
            try {
                byte buffer[] = new byte[256];
                socket = new DatagramSocket();
                socket.setSoTimeout(500);
                socket.send(packet);
                while(true){
                    packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    tempList.add(packet.getData());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(socket != null)
                    socket.close();
            }
            bytesList.addAll(tempList);

        }

        return bytesList;
    }
}
