package baway.com.justdoit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;
import java.security.acl.Group;

import baway.com.justdoit.GroupAdapter;
import baway.com.justdoit.R;

public class ContactListFragment extends Fragment implements View.OnClickListener {

    RelativeLayout creteGroup;
    RelativeLayout joinGroup;
    ListView listView;
    private String groupId;
    private String TAG="ContactListFragment";
    protected List<EMGroup> grouplist;
    private ListView groupListView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_fragment,null);


        creteGroup = rootView.findViewById(R.id.create_group);
        joinGroup = rootView.findViewById(R.id.join_group);
        listView = rootView.findViewById(R.id.list);

        creteGroup.setOnClickListener(this);
        joinGroup.setOnClickListener(this);

        grouplist = EMClient.getInstance().groupManager().getAllGroups();
        groupListView = (ListView) findViewById(R.id.list);
        //show group list
        GroupAdapter groupAdapter = new GroupAdapter(this, 1, grouplist);
        groupListView.setAdapter(groupAdapter);

        return rootView;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_group:  //创建群聊

                /**
                 * 创建群组
                 * @param groupName 群组名称
                 * @param desc 群组简介
                 * @param allMembers 群组初始成员，如果只有自己传空数组即可
                 * @param reason 邀请成员加入的reason
                 * @param option 群组类型选项，可以设置群组最大用户数(默认200)及群组类型@see {@link EMGroupStyle}
                 *               option.inviteNeedConfirm表示邀请对方进群是否需要对方同意，默认是需要用户同意才能加群的。
                 *               option.extField创建群时可以为群组设定扩展字段，方便个性化订制。
                 * @return 创建好的group
                 * @throws HyphenateException
                 */
//                if ( EMClient.getInstance().isLoggedInBefore()){
                    Log.e(TAG, "create_group: "+EMClient.getInstance().isLoggedInBefore() );
//                }


                new Thread(){
                    public  void run(){
                        try {
                            EMGroupOptions option = new EMGroupOptions();
                            option.maxUsers = 200;
                            option.style = EMGroupStyle.EMGroupStylePublicOpenJoin;
                            EMGroup emGroup=    EMClient.getInstance().groupManager().createGroup("111", "这是群聊", new String[]{}, "申请加群：", option);
                            if (emGroup!=null){
                                groupId =     emGroup.getGroupId();

                            }
                            Log.e(TAG, "创建群聊==id: ="+groupId);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "run: ="+e.getMessage());
                        }
                    }
                }.start();

                break;
            case R.id.join_group:
//如果群开群是自由加入的，即group.isMembersOnly()为false，直接join
                new Thread(){
                    public  void run(){
                        try {
                            EMClient.getInstance().groupManager().joinGroup("68364305235969");//需异步处理
                            Log.e(TAG, "joinGroup: " );
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "joinGroup: failed" );
                        }

                    }}.start();

////需要申请和验证才能加入的，即group.isMembersOnly()为true，调用下面方法
//                EMClient.getInstance().groupManager().applyJoinToGroup(groupid, "求加入");//需异步处理
                break;
        }
    }
}
