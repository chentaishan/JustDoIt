package baway.com.justdoit;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import baway.com.justdoit.Utils.PermissionHelper;
import baway.com.justdoit.Utils.PermissionInterface;
import baway.com.justdoit.fragment.ContactListFragment;
import baway.com.justdoit.fragment.ConversationListFragment;
import baway.com.justdoit.fragment.SettingFragment;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

/**
 * 主页
 *  包含三个fragment
 *  ConversationListFragment  会话列表页面
 *  ContactListFragment    群聊列表页面
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener,PermissionInterface {
    private static final String TAG = "MainActivity";
    private ConversationListFragment conversationListFragment;
    private ContactListFragment contactListFragment;
    private Fragment[] fragments;
    private int currentTabIndex = 0;
    private int index;
    private PermissionHelper mPermissionHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_actvity);

        //初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();



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
                // TODO 遍历messages 里是否有抖一抖，如果有，震动，并且刷新聊天页面


                Log.e(TAG, "onMessageReceived: " + messages.size());
                conversationListFragment.refreshLayout();

                conversationListFragment.doShake(messages);

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

    @Override
    public int getPermissionsRequestCode() {
        return 10000;
    }

    @Override
    public String[] getPermissions() {

        String[] perms = {android.Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        return  perms;
    }

    @Override
    public void requestPermissionsSuccess() {

    }

    @Override
    public void requestPermissionsFail() {

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
