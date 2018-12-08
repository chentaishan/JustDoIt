package baway.com.justdoit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);

    }

    /**
     * 注册点击
     * @param view
     */
    public void register(View view){
        final String currentUsername = usernameEditText.getText().toString().trim();
        final String currentPassword = passwordEditText.getText().toString().trim();
            // 注册放到子线程里
            new Thread(){
                public void run(){
                    try {
                        // 创建账号
                        EMClient.getInstance().createAccount(currentUsername, currentPassword);
                        startActivity(new Intent(LoginActivity.this,ConversationActivity.class));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        Log.e(TAG, "register: " );
                    }
                }
            }.start();

    }

    /**
     * 登录
     * @param view
     */
    public void login(View view){
        String currentUsername = usernameEditText.getText().toString().trim();
        String currentPassword = passwordEditText.getText().toString().trim();

        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.e(TAG, "onSuccess: " );
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }

            @Override
            public void onError(int i, String s) {

                Log.e(TAG, "onError: s="+s );
            }

            @Override
            public void onProgress(int i, String s) {
                Log.e(TAG, "onProgress: " );
            }
        });



        }
}
