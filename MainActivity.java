package com.example.leiyang.smstest;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText messageToEdit;
    private EditText nameToEdit;
    private Button send_message;
    private TextView messageToShow;
    private TextView nameToShow;
    private IntentFilter sendIntentFilter;
    private IntentFilter receiverIntentFilter;
    private MessageReceiver messageReceiver;
    private SendStatusReceiver sendStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageToEdit = (EditText) findViewById(R.id.edit_message);
        nameToEdit = (EditText) findViewById(R.id.edit_name);
        send_message = (Button) findViewById(R.id.send_message);
        messageToShow = (TextView) findViewById(R.id.show_message);
        nameToShow = (TextView) findViewById(R.id.show_name);

        /**
         * 动态注册广播接收器
         */
        receiverIntentFilter = new IntentFilter();
        receiverIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        messageReceiver = new MessageReceiver();
        registerReceiver(messageReceiver,receiverIntentFilter);

        sendIntentFilter = new IntentFilter();
        sendIntentFilter.addAction("SENT_SMS_ACTION");
        sendStatusReceiver = new SendStatusReceiver();
        registerReceiver(sendStatusReceiver,sendIntentFilter);

        /*点击发送按钮，发送短信*/
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Action为SENT_SMS_ACTION的广播，SENT_SMS_ACTION为自定义的标示*/
                Intent intent = new Intent("SENT_SMS_ACTION");

                /*通过PendingIntent的getBroadcast()方法得到PendingIntent对象*/
                PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);

                /*实例化一个SmsManager对象*/
                SmsManager smsManager = SmsManager.getDefault();

                /*第一个参数是收件人的电话号码，
                 *第三个参数是短信内容,
                 *第四个参数为PendingIntent对象，配合SendStatusReceiver类中getResultCode()用于判断短信是否发送成功
                 * getResultCode() == Activity表示短信发送成功
                 */
                smsManager.sendTextMessage(nameToEdit.getText().toString(),
                        null,messageToEdit.getText().toString(),pi,null);
            }
        });
    }

    /**
     * 退出程序时注销广播接收器
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sendStatusReceiver);
        unregisterReceiver(messageReceiver);
    }

    /**
     * Created by LeiYang on 2016/8/14 0014.
     */

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            /*pdus.length即为短信分割的个数，每条短信不得超过160个字符*/
            SmsMessage[] messagers = new SmsMessage[pdus.length];
            for (int i = 0; i < messagers.length; i++) {
                messagers[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String address = messagers[0].getOriginatingAddress();
                String fullMessage = "";
                for (SmsMessage messager:messagers
                     ) {
                    fullMessage +=messager.getMessageBody();
                }
                nameToShow.setText(address);
                messageToShow.setText(fullMessage);
            }
        }
    }
}
