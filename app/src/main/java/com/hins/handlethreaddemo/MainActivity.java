package com.hins.handlethreaddemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int MSG_REFRESH = 1;

    private HandlerThread mHandlerThread;
    private Handler mWorkHandler;

    //更新UI handler
    private Handler mUIHandler;

    private Button mRefresh;
    private TextView mTextView;

    private boolean isRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRefresh = (Button) findViewById(R.id.refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRefresh) {
                    mRefresh.setText("停止刷新");
                    isRefresh = true;
                    mWorkHandler.sendEmptyMessage(MSG_REFRESH);
                } else {
                    mRefresh.setText("开始刷新");
                    isRefresh = false;
                    mWorkHandler.removeMessages(MSG_REFRESH);
                }
            }
        });
        mTextView = (TextView) findViewById(R.id.text_view_info);

        mUIHandler = new Handler();

        initHandle();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止刷新
        isRefresh = false;
        mWorkHandler.removeMessages(MSG_REFRESH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        mHandlerThread.quit();
    }

    //通过以下方法 workhandler 存在于handlerthread 开启的子线程中，在这里处理复杂耗时的任务
    private void initHandle() {
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        mWorkHandler = new Handler(mHandlerThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {

                refreshUI();

                if (isRefresh) {
                    mWorkHandler.sendEmptyMessage(MSG_REFRESH);
                }
            }
        };
    }

    private void refreshUI() {
        try {
            //模拟耗时操作
            Thread.sleep(2000);

            //里面的run()方法运行于ui线程
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    String result = "每隔2秒更新一下数据: ";
                    result += Math.random();
                    mTextView.setText(result);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
