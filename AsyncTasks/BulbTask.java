package com.augment.golden.bulbcontrol.AsyncTasks;

import android.os.AsyncTask;

import com.augment.golden.bulbcontrol.Beans.BulbClient;
import com.augment.golden.bulbcontrol.Beans.LightInfo;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;

public class BulbTask extends AsyncTask<LightInfo, Void, String> {
    protected String doInBackground(LightInfo... params) {
        LightInfo info = params[0];

        BulbClient client = new BulbClient();
        byte[] message = new byte[0];
        String answer = "";
        if(info.isChangePower())
            if(info.getMacAddress().length() > 0)
                LifxBulb.findBulb(info.getMacAddress()).changePower(500);

        if(info.isChangeBrightness() && info.getCurrentBrightness().intValue() > -1)
            if(info.getMacAddress().length() > 0)
                LifxBulb.findBulb(info.getMacAddress()).changeHsbk();

        if(info.isGetBrightness())
            info.getCurrentBrightness().getAndSet(client.getBrightness());

        client.sendMessage(message);

        return answer;
    }

    protected void onPostExecute() {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
