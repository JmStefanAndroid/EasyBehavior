package me.stefan.easybehavior;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;

import me.stefan.easybehavior.demo2.behavior.CoAliRefreshBehavior;
import me.stefan.easybehavior.demo2.widget.SmileView;

/**
 * Created by stefan on 2017/7/11.
 * Func:仿支付宝首页下拉刷新
 */

public class Demo2Activity extends AppCompatActivity {

    private AppBarLayout mAppBarLayout;
    private SmileView mSmileView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);
        findId();
        initListener();
    }

    /**
     * 初始化id
     */
    private void findId() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mSmileView = (SmileView) findViewById(R.id.smile_view);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSmileView != null) {
            mSmileView.cancelAnim();
        }
    }

    /**
     * 绑定事件
     */
    private void initListener() {
        final CoAliRefreshBehavior myAppBarLayoutBehavoir = (CoAliRefreshBehavior)
                ((CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams()).getBehavior();
        myAppBarLayoutBehavoir.setOnRefrehViewActionListener(new CoAliRefreshBehavior.onRefrehViewActionListener() {
            @Override
            public void onRefresh() {
                mSmileView.setDuration(2000);
                mSmileView.performAnim();
                mSmileView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Demo2Activity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAppBarLayoutBehavoir.stopPin();
                                mSmileView.cancelAnim();
                            }
                        });
                    }
                }, 4000);
            }
        });
    }


}
