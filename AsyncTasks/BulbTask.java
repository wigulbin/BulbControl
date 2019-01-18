package com.augment.golden.bulbcontrol.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.augment.golden.bulbcontrol.Adapters.SmartBulbListAdapter;
import com.augment.golden.bulbcontrol.Beans.BulbClient;
import com.augment.golden.bulbcontrol.Beans.LightInfo;
import com.augment.golden.bulbcontrol.Beans.PacketBuilder;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.Common;
import com.augment.golden.bulbcontrol.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BulbTask extends AsyncTask<LightInfo, Void, String> {
    protected String doInBackground(LightInfo... params) {
        LightInfo info = params[0];

        BulbClient client = new BulbClient();
        PacketBuilder builder = PacketBuilder.buildPacket();
        byte[] message = new byte[0];
        String answer = "";
        if(info.isChangePower())
            if(info.getMacAddress().length() > 0)
                message = PacketBuilder.setPowerMessage(info.isOnOrOff(), 500, info.getMacAddress());
            else
                message = builder.setPower(info.isOnOrOff(), 500);

        if(info.isChangeBrightness() && info.getCurrentBrightness().intValue() > -1)
            if(info.getMacAddress().length() > 0)
                message = PacketBuilder.setBrightnessMessage(info.getCurrentBrightness().intValue(), info.getMacAddress());
            else
                message = builder.setBrightness(info.getCurrentBrightness().intValue());

        if(info.isGetBrightness())
            info.getCurrentBrightness().getAndSet(client.getBrightness());

        if(info.isFindService())
            message = builder.getService();


        if(info.isResponse())
            searchForBulbs(info, client);
        else
            client.sendMessage(message);
        System.out.println(answer);

        return answer;
    }

    private void searchForBulbs(LightInfo info, BulbClient client){
        final SmartBulbListAdapter adapter = info.getAdapter();
        Set<byte[]> bytesLIst = new HashSet<>(client.searchForBulbs(PacketBuilder.getServiceMessage()));
        Set<String> macSet = new HashSet<>();
        final Activity activity = info.getActivity();
        for (byte[] bytes : bytesLIst) {
            String hex = SmartBulb.parseMACFromReturn(Common.convertByteArrToHex(bytes));
            if(macSet.add(hex)){
                byte[] labelBytes = new BulbClient().sendAndReceiveMessage(PacketBuilder.getLabelMessage(hex));
                String label = "";
                if(labelBytes.length > 0) {
                    byte[] labelArr = Common.getSubArray(labelBytes, 36, labelBytes[0]);
                    label = new String(labelArr);
                }

                if(label.trim().length() > 0) {
                    final SmartBulb bulb = new SmartBulb(hex);
                    bulb.setLabel(label);
                    SmartBulb.saveBulb(bulb, activity);
                }
            }
        }

        info.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<SmartBulb> bulbs = SmartBulb.getAllBulbs(activity);
                ProgressBar spinner = activity.findViewById(R.id.progressBar);
                if(spinner != null)
                    spinner.setVisibility(View.GONE);
                for (SmartBulb bulb : bulbs) {
                    adapter.remove(bulb);
                    adapter.add(bulb);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    protected void onPostExecute() {
        // TODO: check this.exception
        // TODO: do something with the feed
        System.out.println("Here");
    }
}
