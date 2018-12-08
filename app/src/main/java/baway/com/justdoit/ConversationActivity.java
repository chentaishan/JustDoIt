package baway.com.justdoit;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.widget.presenter.EaseChatLocationPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ConversationActivity extends Activity {
    EaseChatMessageList messageList;
    String userName;
    SwipeRefreshLayout swipeRefreshLayout;
    private EaseChatInputMenu inputMenu;
    private EaseVoiceRecorderView voiceRecorderView;
    private int ITEM_LOCATION=0;
    private int REQUEST_CODE_MAP=99;
    private int ITEM_SHAKE=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.conversation_activity);

        userName = getIntent().getStringExtra("name");

        messageList = (EaseChatMessageList) findViewById(R.id.message_list);
        inputMenu = (EaseChatInputMenu) findViewById(R.id.input_menu);
        voiceRecorderView = (EaseVoiceRecorderView) findViewById(com.hyphenate.easeui.R.id.voice_recorder);

//注册底部菜单扩展栏item
//传入item对应的文字，图片及点击事件监听，extendMenuItemClickListener实现EaseChatExtendMenuItemClickListener
        inputMenu.registerExtendMenuItem(R.string.attach_location, R.drawable.ease_location_msg, ITEM_LOCATION, easeChatExtendMenuItemClickListener);
        inputMenu.registerExtendMenuItem("抖一抖", R.drawable.shake_icon, ITEM_SHAKE, easeChatExtendMenuItemClickListener);
        //初始化，此操作需放在registerExtendMenuItem后
        inputMenu.init();
//设置相关事件监听
        inputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onSendMessage(String content) {
                // 发送文本消息
                sendTextMessage(content);
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {

            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                ////把touch事件传入到EaseVoiceRecorderView 里进行录音
                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderView.EaseVoiceRecorderCallback() {
                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        // 发送语音消息
                        sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    }
                });
            }
        });
//初始化messagelist
        messageList.init(userName, EMMessage.ChatType.Chat.ordinal(), null);
//设置item里的控件的点击事件
        messageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {
            @Override
            public void onUserAvatarClick(String username) {
                //头像点击事件
            }

            @Override
            public void onUserAvatarLongClick(String username) {
            }

            @Override
            public void onMessageInProgress(EMMessage message) {
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                //气泡框长按事件
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                //气泡框点击事件，EaseUI有默认实现这个事件，如果需要覆盖，return值要返回true
                return false;
            }

            @Override
            public boolean onResendClick(EMMessage message) {
                return false;
            }
        });
//获取下拉刷新控件
        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
//刷新消息列表
        messageList.refresh();
//        messageList.refreshSeekTo(position);
        messageList.refreshSelectLast();
    }

    private void sendVoiceMessage(String voiceFilePath, int voiceTimeLength) {
        //filePath为语音文件路径，length为录音时间(秒)
        EMMessage message = EMMessage.createVoiceSendMessage(voiceFilePath, voiceTimeLength, userName);
//如果是群聊，设置chattype，默认是单聊
//        if (chatType == CHATTYPE_GROUP)
//            message.setChatType(EMMessage.ChatType.GroupChat);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    private void sendTextMessage(String content) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, userName);
//如果是群聊，设置chattype，默认是单聊
//        if (chatType == CHATTYPE_GROUP)
//            message.setChatType(EMMessage.ChatType.GroupChat);
//发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    EaseChatExtendMenu.EaseChatExtendMenuItemClickListener easeChatExtendMenuItemClickListener = new EaseChatExtendMenu.EaseChatExtendMenuItemClickListener() {
        @Override
        public void onClick(int itemId, View view) {

            switch (itemId) {
                case 0:  //对应ITEM_LOCATION
//                    startActivityForResult(new Intent(ConversationActivity.this, EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
                    double latitude = 40.0376283850;
                    double longitude = 116.3187221243;
                        sendLocationMessage(latitude, longitude, "北京海淀区 上地七街");
                    break;
                case 1:  //对应ITEM_SHAKE
                    //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
//                    EMMessage emMessage = EMMessage.createImageSendMessage(imagePath, false, userName);
//                    EMClient.getInstance().chatManager().sendMessage(emMessage);

                    sendTextMessage("抖一抖");
                    break;
            }
        }
    };
    public void sendLocationMessage(double latitude,double longitude,String locationAddress){
        //latitude为纬度，longitude为经度，locationAddress为具体位置内容
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, userName);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAP) { // location

//            40.0376283850,116.3187221243
            double latitude = 40.0376283850;
            double longitude = 116.3187221243;
            String locationAddress = data.getStringExtra("address");
            if (locationAddress != null && !locationAddress.equals("")) {
                sendLocationMessage(latitude, longitude, locationAddress);
            } else {
                Toast.makeText(ConversationActivity.this, com.hyphenate.easeui.R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
