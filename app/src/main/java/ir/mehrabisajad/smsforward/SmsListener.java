package ir.mehrabisajad.smsforward;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;

public class SmsListener extends BroadcastReceiver {

    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();

                        preferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
                        String number = preferences.getString("number", null);
                        String baseAddress = preferences.getString("baseAddress", null);
                        if (baseAddress != null && msg_from.equals(number)) {

                            new Thread(() -> {
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(baseAddress)
                                        .build();

                                ForwardService service = retrofit.create(ForwardService.class);

                                try {
                                    Response<Void> execute = service.send(msgBody).execute();
                                    Log.e("onReceive: ", execute.isSuccessful() + "");
                                    Log.e("onReceive: ", execute.errorBody() != null ? execute.errorBody().string() : "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
