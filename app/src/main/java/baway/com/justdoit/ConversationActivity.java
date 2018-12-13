package baway.com.justdoit;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.util.PathUtil;

import java.io.File;

import static baway.com.justdoit.Utils.Contants.ITEM_CAMERA;
import static baway.com.justdoit.Utils.Contants.ITEM_FILE;
import static baway.com.justdoit.Utils.Contants.ITEM_LOCATION;
import static baway.com.justdoit.Utils.Contants.ITEM_PICTURE;
import static baway.com.justdoit.Utils.Contants.ITEM_SHAKE;
import static baway.com.justdoit.Utils.Contants.REQUEST_CODE_CAMERA;
import static baway.com.justdoit.Utils.Contants.REQUEST_CODE_LOCAL;
import static baway.com.justdoit.Utils.Contants.REQUEST_CODE_MAP;
import static com.baidu.mapapi.BMapManager.getContext;

/**
 * 聊天页面
 */

public class ConversationActivity extends Activity {
    EaseChatMessageList messageList;
    String userName;
    private EaseChatInputMenu inputMenu;
    private EaseVoiceRecorderView voiceRecorderView;

    private int chatType = 0;
    private String userId;
    private File cameraFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.conversation_activity);

        userName = getIntent().getStringExtra("name");
        chatType = getIntent().getIntExtra("chatType", 0);
        userId = getIntent().getStringExtra("userId");

        messageList = (EaseChatMessageList) findViewById(R.id.message_list);
        inputMenu = (EaseChatInputMenu) findViewById(R.id.input_menu);
        voiceRecorderView = (EaseVoiceRecorderView) findViewById(com.hyphenate.easeui.R.id.voice_recorder);

        //注册底部菜单扩展栏item
        //传入item对应的文字，图片及点击事件监听，extendMenuItemClickListener实现EaseChatExtendMenuItemClickListener
        inputMenu.registerExtendMenuItem(R.string.attach_take_pic, R.drawable.ease_chat_takepic_selector, ITEM_CAMERA, easeChatExtendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.attach_picture, R.drawable.ease_chat_image_selector, ITEM_PICTURE, easeChatExtendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, ITEM_FILE, easeChatExtendMenuItemClickListener);

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
//        if (chatType==EMMessage.ChatType.GroupChat.ordinal()){
//
//        }else if(chatType==EMMessage.ChatType.Chat.ordinal()){
//            //初始化messagelist
//        }
        if (chatType == EMMessage.ChatType.GroupChat.ordinal()) {
            userName = userId;
        }
        messageList.init(userName, chatType, null);

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
//        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
        //刷新消息列表
        messageList.refresh();
//        messageList.refreshSeekTo(position);
        messageList.refreshSelectLast();
    }

    private void sendVoiceMessage(String voiceFilePath, int voiceTimeLength) {
        //filePath为语音文件路径，length为录音时间(秒)
        EMMessage message = EMMessage.createVoiceSendMessage(voiceFilePath, voiceTimeLength, userName);
        message.setMessageStatusCallback(emCallBack);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    private void sendTextMessage(String content) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, userName);
        message.setMessageStatusCallback(emCallBack);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    private void sendPicMessage(String imagePath) {
        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
        EMMessage emMessage = EMMessage.createImageSendMessage(imagePath, false, userName);

        EMClient.getInstance().chatManager().sendMessage(emMessage);
    }

    EMCallBack emCallBack = new EMCallBack() {
        @Override
        public void onSuccess() {
            messageList.refresh();
        }

        @Override
        public void onError(int i, String s) {

        }

        @Override
        public void onProgress(int i, String s) {

        }
    };
    EaseChatExtendMenu.EaseChatExtendMenuItemClickListener easeChatExtendMenuItemClickListener = new EaseChatExtendMenu.EaseChatExtendMenuItemClickListener() {
        @Override
        public void onClick(int itemId, View view) {

            switch (itemId) {
                case ITEM_LOCATION:  //对应ITEM_LOCATION
//                    double latitude = 40.0376283850;
//                    double longitude = 116.3187221243;
//                    sendLocationMessage(latitude, longitude, "北京海淀区 上地七街");

                    startActivityForResult(new Intent(ConversationActivity.this,
                            EaseBaiduMapActivity.class), REQUEST_CODE_MAP);

                    break;
                case ITEM_SHAKE:  //对应ITEM_SHAKE

                    sendTextMessage("抖一抖");
                    // TODO 震动  声音


                    break;

                case ITEM_CAMERA: //  相机
                    selectPicFromCamera();

                    break;
                case ITEM_FILE://  图片
                    selectPicFromLocal();
                    break;
            }
        }
    };

    public void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        //latitude为纬度，longitude为经度，locationAddress为具体位置内容
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, userName);
        message.setMessageStatusCallback(emCallBack);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MAP) { // location
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            String locationAddress = data.getStringExtra("address");
            if (locationAddress != null && !locationAddress.equals("")) {
                sendLocationMessage(latitude, longitude, locationAddress);
            } else {
                Toast.makeText(this, "位置有误", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_CODE_LOCAL){

            if (data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    sendPicByUri(selectedImage);
                }
            }
        }else if(requestCode == REQUEST_CODE_CAMERA){
            if (cameraFile != null && cameraFile.exists())
                sendPicMessage(cameraFile.getAbsolutePath());
        }
//        startActivityForResult();

    }

    /**
     * capture new image
     */
    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(this, com.hyphenate.easeui.R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }

        File cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();
        Uri photoUri = EaseCompat.getUriForFile(getContext(), cameraFile);

        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT,
                        photoUri),
                REQUEST_CODE_CAMERA);
    }
    /**
     * send image
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, com.hyphenate.easeui.R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, com.hyphenate.easeui.R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendPicMessage(file.getAbsolutePath());
        }

    }
    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

}
