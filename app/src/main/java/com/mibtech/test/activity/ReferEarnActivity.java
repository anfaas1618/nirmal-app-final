package com.mibtech.test.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import com.mibtech.test.R;
import com.mibtech.test.helper.Constant;
import com.mibtech.test.helper.Session;

public class ReferEarnActivity extends AppCompatActivity {

    TextView txtrefercoin, txtcode, txtcopy, txtinvite;
    Toolbar toolbar;
    Session session;
    String preText = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_frnd);

        session = new Session(ReferEarnActivity.this);
        toolbar = findViewById(R.id.toolbar);
        txtrefercoin = findViewById(R.id.txtrefercoin);
        if (Constant.REFER_EARN_METHOD.equals("rupees")) {
            preText = Constant.SETTING_CURRENCY_SYMBOL + Constant.REFER_EARN_BONUS;
        } else {
            preText = Constant.REFER_EARN_BONUS + "% ";
        }
        txtrefercoin.setText("Refer a friend and earn upto " + preText + " when your friend's first order is successfully delivered. Minimum Order amount should be " + Constant.SETTING_CURRENCY_SYMBOL + Constant.REFER_EARN_ORDER_AMOUNT + ". which allows you to earn upto " + Constant.SETTING_CURRENCY_SYMBOL + Constant.MAX_EARN_AMOUNT + ".");
        txtcode = findViewById(R.id.txtcode);
        txtcopy = findViewById(R.id.txtcopy);
        txtinvite = findViewById(R.id.txtinvite);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.invite_frnd));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtinvite.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(ReferEarnActivity.this, R.drawable.ic_share), null, null, null);
        txtcode.setText(session.getData(Session.KEY_REFER_CODE));
        txtcopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", txtcode.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ReferEarnActivity.this, R.string.refer_code_copied, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void OnInviteFrdClick(View view) {
        if (!txtcode.getText().toString().equals("code")) {
            try {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.refer_share_msg_1)
                        + getResources().getString(R.string.app_name) + getString(R.string.refer_share_msg_2)
                        + "\n " + Constant.share_url + "refer/" + txtcode.getText().toString());
                startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_frnd_title)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.refer_code_alert_msg), Toast.LENGTH_SHORT).show();
        }
    }
}
