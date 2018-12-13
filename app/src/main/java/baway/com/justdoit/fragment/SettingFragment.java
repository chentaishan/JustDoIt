package baway.com.justdoit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import baway.com.justdoit.R;

/**
 * 好友相关逻辑
 * 加好友
 * 同意好友请求
 * 好友列表
 */
public class SettingFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView  = inflater.inflate(R.layout.setting_fragment,null);
        return rootView;
    }
}
