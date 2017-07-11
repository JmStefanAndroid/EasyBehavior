package me.stefan.easybehavior;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan 邮箱：648701906@qq.com
 */
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ViewGroup mContainer = (ViewGroup) findViewById(R.id.container);
        List<String> mActivitys = createList();

        for (final String string : mActivitys) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
            textView.setGravity(Gravity.CENTER);
            textView.setText(string);
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startActivity(new Intent(StartActivity.this, Class.forName(string)));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            mContainer.addView(textView);
        }
    }

    private List<String> createList() {
        List<String> activitys = new ArrayList<>();
        activitys.add("me.stefan.easybehavior.Demo1Activity");
        activitys.add("me.stefan.easybehavior.Demo2Activity");
        return activitys;
    }


}
