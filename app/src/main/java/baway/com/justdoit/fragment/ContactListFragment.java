package baway.com.justdoit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.security.acl.Group;

import baway.com.justdoit.R;

public class ContactListFragment extends Fragment implements View.OnClickListener {

    RelativeLayout creteGroup;
    RelativeLayout joinGroup;
    ListView listView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_fragment,null);


        creteGroup = rootView.findViewById(R.id.create_group);
        joinGroup = rootView.findViewById(R.id.join_group);
        listView = rootView.findViewById(R.id.list);


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
//                EMGroupOptions option = new EMGroupOptions();
//                option.maxUsers = 200;
//                option.style = EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
//
//                EMClient.getInstance().groupManager().createGroup("HHH", "这是群聊", "", "", option);
                break;
            case R.id.join_group:

                break;
        }
    }
}
