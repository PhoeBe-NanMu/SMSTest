package com.example.leiyang.smstest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by LeiYang on 2016/8/14 0014.
 */
public class SendStatusReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (getResultCode() == Activity.RESULT_OK) {
            Toast.makeText(context, "已经发送成功短信！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "未知原因导致短信发送失败！！", Toast.LENGTH_SHORT).show();
        }
    }

}
