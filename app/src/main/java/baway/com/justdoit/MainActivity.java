package baway.com.justdoit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.EMConnectionListener;

import java.util.List;

import baway.com.justdoit.fragment.ContactListFragment;
import baway.com.justdoit.fragment.ConversationListFragment;
import baway.com.justdoit.fragment.SettingFragment;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ConversationListFragment conversationListFragment;
    private ContactListFragment contactListFragment;
    private Fragment[] fragments;
    private int currentTabIndex = 0;
    private int index;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_actvity);

        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);

        //加载所有会话
        EMClient.getInstance().chatManager().loadAllConversations();

        conversationListFragment = new ConversationListFragment();
        contactListFragment = new ContactListFragment();
        SettingFragment settingFragment = new SettingFragment();
        fragments = new Fragment[]{conversationListFragment, contactListFragment, settingFragment};

        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, conversationListFragment)
                .add(R.id.fragment_container, contactListFragment)
                .hide(contactListFragment)
                .show(conversationListFragment)
                .commit();


        EMMessageListener msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息

                Log.e(TAG, "onMessageReceived: " + messages.size());
                conversationListFragment.refreshLayout();

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                //消息被撤回
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);

        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());

    }
    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            Log.e(TAG, "onConnected: ");
        }
        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    Log.e(TAG, "onDisconnected: "+error);

                }
            });
        }
    }


    public void changeTab() {
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        currentTabIndex = index;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                index = 0;
                break;
            case R.id.btn_2:
                index = 1;
                break;
            case R.id.btn_3:
                index = 2;
                break;
        }

        changeTab();
    }
}
