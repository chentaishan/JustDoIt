package baway.com.justdoit.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
//import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.widget.EaseConversationList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import baway.com.justdoit.ConversationActivity;
import baway.com.justdoit.R;

public class ConversationListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private EaseConversationList easeConversationList;
    private List<EMConversation> conversationList = new ArrayList<>();
//    private EaseChatInputMenu inputMenu;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.conversation_list_fragment, null);
       final EditText inputEdit =  root.findViewById(R.id.input);
       final EditText sendWhoEdit =  root.findViewById(R.id.send_who);
        root.findViewById(R.id.send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMMessage message = EMMessage.createTxtSendMessage(inputEdit.getText().toString(), sendWhoEdit.getText().toString());

                EMClient.getInstance().chatManager().sendMessage(message);
            }
        });

        //获取所有会话列表
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();

        for (Map.Entry<String, EMConversation> entry : conversations.entrySet()) {

            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

            conversationList.add(entry.getValue());
        }
        //会话列表控件
        easeConversationList = (EaseConversationList) root.findViewById(R.id.list);
        easeConversationList.setOnItemClickListener(this);
        //初始化，参数为会话列表集合
        easeConversationList.init(conversationList);

        return root;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        EMConversation emConversation = (EMConversation) parent.getAdapter().getItem(position);

        intent.putExtra("name", emConversation.getLastMessage().getUserName());
        startActivity(intent);
    }

    /**
     * 刷新数据
     */
    public void refreshLayout() {

        conversationList.clear();
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<EMConversation> conversationList = new ArrayList<>();
        for (Map.Entry<String, EMConversation> entry : conversations.entrySet()) {

            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

            conversationList.add(entry.getValue());
        }
        easeConversationList.init(conversationList);

    }

    public void doShake(List<EMMessage> messages){
        for (EMMessage emMessage:messages){
            if (((EMTextMessageBody)emMessage.getBody()).getMessage().equals("抖一抖")){
                // TODO 抖一抖


            }
        }
    }


}
