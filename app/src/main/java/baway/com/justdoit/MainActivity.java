package baway.com.justdoit;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import baway.com.justdoit.fragment.ContactListFragment;
import baway.com.justdoit.fragment.ConversationListFragment;
import baway.com.justdoit.fragment.SettingFragment;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private ConversationListFragment conversationListFragment;
    private ContactListFragment contactListFragment;
    private Fragment[] fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_actvity);
        //加载所有会话
        EMClient.getInstance().chatManager().loadAllConversations();

        conversationListFragment = new ConversationListFragment();
        contactListFragment = new ContactListFragment();
        SettingFragment settingFragment = new SettingFragment();
        fragments = new Fragment[] { conversationListFragment, contactListFragment, settingFragment};

        this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment)
                .add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(conversationListFragment)
                .commit();



        EMMessageListener msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息

                Log.e(TAG, "onMessageReceived: "+ messages.size());
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
    }


}
