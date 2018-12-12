package baway.com.justdoit.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;
import com.hyphenate.chat.EMMessage;

import java.security.acl.Group;
import java.util.List;

import baway.com.justdoit.ConversationActivity;
import baway.com.justdoit.GroupAdapter;
import baway.com.justdoit.R;

import static baway.com.justdoit.Utils.Contants.CREATE_GROUP;
import static baway.com.justdoit.Utils.Contants.JOIN_GROUP;
import static com.hyphenate.easeui.EaseConstant.CHATTYPE_GROUP;

public class ContactListFragment extends Fragment implements View.OnClickListener {

    RelativeLayout creteGroup;
    RelativeLayout joinGroup;
    ListView listView;
    private ListView groupListView;
    private TextView createGroupText;

    private  String TAG = "ContactListFragment";
    private  String groupId="";
    @SuppressLint("HandlerLeak")
     Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case CREATE_GROUP:
                    EMGroup emGroup = (EMGroup) msg.obj;
                    if (emGroup!=null){
                        groupId =     emGroup.getGroupId();
                    }
                    createGroupText.setText("创建群id: "+groupId);

                    Toast.makeText(getActivity(), "创建群聊==成功=id: ="+groupId, Toast.LENGTH_LONG).show();

                    break;
                case JOIN_GROUP:
                    Log.e(TAG, "joinGroup: " );
                    Toast.makeText(getActivity(), "加入群聊==成功=id: ="+groupId, Toast.LENGTH_LONG).show();

                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_fragment,null);


        createGroupText = rootView.findViewById(R.id.create_group_text);
        creteGroup = rootView.findViewById(R.id.create_group);
        joinGroup = rootView.findViewById(R.id.join_group);
        listView = rootView.findViewById(R.id.list);

        creteGroup.setOnClickListener(this);
        joinGroup.setOnClickListener(this);

        List<EMGroup> grouplist = EMClient.getInstance().groupManager().getAllGroups();
        groupListView = (ListView) rootView.findViewById(R.id.list);
        //show group list
        GroupAdapter groupAdapter = new GroupAdapter(getActivity(), 1, grouplist);
        groupListView.setAdapter(groupAdapter);

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                // it is group chat
                intent.putExtra("chatType", EMMessage.ChatType.GroupChat.ordinal());
                intent.putExtra("userId", groupAdapter.getItem(position).getGroupId());
                intent.putExtra("name", groupAdapter.getItem(position).getGroupName());
                startActivityForResult(intent, 0);
            }
        });
        return rootView;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_group:  //创建群聊

                new Thread(){
                    public  void run(){
                        try {
                            EMGroupOptions option = new EMGroupOptions();
                            option.maxUsers = 200;
                            option.style = EMGroupStyle.EMGroupStylePublicOpenJoin;
                            EMGroup emGroup=    EMClient.getInstance().groupManager().createGroup("111", "这是群聊", new String[]{}, "申请加群：", option);
                            Message message = handler.obtainMessage();
                            message.obj = emGroup;
                            message.what = CREATE_GROUP;
                            handler.sendMessage(message);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "run: ="+e.getMessage());
                        }
                    }
                }.start();

                break;
            case R.id.join_group:  //加入群聊
                new Thread(){
                    public  void run(){
                        try {
                            EMClient.getInstance().groupManager().joinGroup("68364305235969");//需异步处理

                            handler.sendEmptyMessage(JOIN_GROUP);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "joinGroup: failed" );
                        }

                    }}.start();

                break;
        }
    }
}
