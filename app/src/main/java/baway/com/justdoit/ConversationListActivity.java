package baway.com.justdoit;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.EaseConversationList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hyphenate.easeui.EaseConstant.CHATTYPE_GROUP;

public class ConversationListActivity extends Activity {
    EaseConversationList easeConversationList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.conversation_activity);

        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<EMConversation> conversationList = new ArrayList<>();
        for (Map.Entry<String, EMConversation> entry : conversations.entrySet()) {

            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

            conversationList.add(entry.getValue());
        }
//会话列表控件
        easeConversationList = (EaseConversationList) findViewById(R.id.list);
//初始化，参数为会话列表集合
        easeConversationList.init(conversationList);
//刷新列表
        easeConversationList.refresh();



        EMMessageListener msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息


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

    public void sendMsg(View view){
//创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage("hhh", "222");

//发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
    }

}
