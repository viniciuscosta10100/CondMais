package br.si.cond.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

public class GcmMessageHandler extends IntentService {

    String mes;
    private Handler handler;
    private boolean flag = false;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {


        flag = true;
    }






}
