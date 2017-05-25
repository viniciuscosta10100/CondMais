package br.si.cond.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import br.si.cond.R;
import br.si.cond.activities.MainActivity;
import br.si.cond.activities.MensagensActivity;
import br.si.cond.activities.MuralActivity;
import br.si.cond.activities.ReivindicacaoActivity;


public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmMessageHandler.class.getName());

        Bundle extras = intent.getExtras();
        Log.i("GCM", "Received PUSH");

        String tipo = extras.getString("tipo");
        String msg = extras.getString("mensagem");


        notify(context, tipo, msg,extras);

//        startWakefulService(context, (intent.setComponent(comp)));
//        setResultCode(Activity.RESULT_OK);
    }


    private void notify(Context ctx,String tipo,String msg,Bundle extrasPush){
        Intent it = new Intent("refresh_activity");
        Intent intent = null;
        String titulo="";
        Log.i("teste",tipo);
        if(tipo.equalsIgnoreCase("aviso")) {
            titulo = "Novo Aviso";
            intent=new Intent(ctx, MuralActivity.class);
        }else if(tipo.equalsIgnoreCase("resposta_reinvidicacao")){
            titulo = "(Reivindicação)Nova Resposta";
            intent=new Intent(ctx, ReivindicacaoActivity.class);
            Bundle extras= new Bundle();
            extras.putLong("id_reivindicacao",extrasPush.getLong("reinvidicacao_id"));
            msg = extrasPush.getString("mensage");
            intent.putExtras(extras);

            it.putExtra("message", extrasPush.getString("reinvidicacao_id"));
        }else if(tipo.equalsIgnoreCase("nova_mensagem")){
            titulo = "Nova Mensagem";
            intent=new Intent(ctx, MensagensActivity.class);
            Bundle extras= new Bundle();
            extras.putLong("user_id",extrasPush.getLong("user_id"));
            extras.putLong("remetente_id",extrasPush.getLong("remetente_id"));
            msg = extrasPush.getString("mensagem");
            intent.putExtras(extras);

            it.putExtra("user_id", extrasPush.getString("user_id"));
            it.putExtra("remetente_id", extrasPush.getString("remetente_id"));
        }


        PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

        Notification noti = new NotificationCompat.Builder(ctx)
                .setContentTitle(titulo)
                .setContentText(msg)
                .setTicker("Cond+")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_marca_white)
                .build();
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(ctx.NOTIFICATION_SERVICE);

        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, noti);



        //put whatever data you want to send, if any


        //send broadcast
        ctx.sendBroadcast(it);
    }

}