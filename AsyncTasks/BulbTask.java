package com.augment.golden.bulbcontrol.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.augment.golden.bulbcontrol.Adapters.SmartBulbListAdapter;
import com.augment.golden.bulbcontrol.Beans.BulbClient;
import com.augment.golden.bulbcontrol.Beans.LightInfo;
import com.augment.golden.bulbcontrol.Beans.PacketBuilder;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;

public class BulbTask extends AsyncTask<LightInfo, Void, String> {
    protected String doInBackground(LightInfo... params) {
        LightInfo info = params[0];

        BulbClient client = new BulbClient();
//        PacketBuilder builder = PacketBuilder.buildPacket();
        byte[] message = new byte[0];
        String answer = "";
        if(info.isChangePower())
            if(info.getMacAddress().length() > 0)
                LifxBulb.findBulb(info.getMacAddress()).changePower(info.isOnOrOff(), 500);

        if(info.isChangeBrightness() && info.getCurrentBrightness().intValue() > -1)
            if(info.getMacAddress().length() > 0)
                LifxBulb.findBulb(info.getMacAddress()).changeHSBK();

        if(info.isGetBrightness())
            info.getCurrentBrightness().getAndSet(client.getBrightness());

//        if(info.isFindService())
//            message = builder.getService();


        if(info.isResponse())
            searchForBulbs(info);
        else
            client.sendMessage(message);
        System.out.println(answer);

        return answer;
    }

    private void searchForBulbs(LightInfo info){
        final SmartBulbListAdapter adapter = info.getAdapter();
        final Activity activity = info.getActivity();
        LifxBulb.findAndSaveBulbs(adapter, activity);
    }

    protected void onPostExecute() {
        // TODO: check this.exception
        // TODO: do something with the feed
        System.out.println("Here");
    }
}
