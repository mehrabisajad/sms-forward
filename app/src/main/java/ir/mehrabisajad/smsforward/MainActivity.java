package ir.mehrabisajad.smsforward;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_SMS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences preferences = getSharedPreferences("setting", MODE_PRIVATE);
        final EditText number = findViewById(R.id.edtNumber);
        final EditText baseAddress = findViewById(R.id.edtBaseAddress);

        number.setText(preferences.getString("number", ""));
        baseAddress.setText(preferences.getString("baseAddress", "http://"));


        Button save = findViewById(R.id.btnSave);
        save.setOnClickListener(v -> {
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
            editor.putString("number", number.getText().toString());
            editor.putString("baseAddress", baseAddress.getText().toString());
            editor.apply();
            Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        });
        save.setOnLongClickListener(v -> {
            new Thread(() -> {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseAddress.getText().toString())
                        .build();

                ForwardService service = retrofit.create(ForwardService.class);

                Call<Void> sssssssssss = service.send("sssssssssss");
                try {
                    sssssssssss.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();
            return false;
        });
        requestSmsPermission();


    }

    private void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS},
                    PERMISSION_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
//                    sendSms(phone, message);
            } else {
                requestSmsPermission();
                // permission denied
            }
        }
    }

//    private void sendSms(String phoneNumber, String message){
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(phoneNumber, null, message, null, null);
//    }

}