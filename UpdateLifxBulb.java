package com.augment.golden.bulbcontrol;

import android.os.AsyncTask;

import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.LightInfo;

public class UpdateLifxBulb extends AsyncTask<LightInfo, Void, Void> {
    @Override
    protected Void doInBackground(LightInfo... params) {
        LightInfo info = params[0];

        if(info.getMacAddress().length() > 0)
        {
            if(info.isChangePower())
                LifxBulb.findBulb(info.getMacAddress()).changePower(info.isOnOrOff(), 500);

            if(info.isChangeBrightness() && info.getCurrentBrightness().intValue() > -1)
                LifxBulb.findBulb(info.getMacAddress()).changeHSBK();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}
