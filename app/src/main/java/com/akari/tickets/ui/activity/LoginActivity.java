package com.akari.tickets.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.akari.tickets.R;
import com.akari.tickets.beans.CheckRandCodeResponse;
import com.akari.tickets.beans.LoginSuggestResponse;
import com.akari.tickets.network.RetrofitManager;
import com.akari.tickets.network.HttpService;
import com.akari.tickets.utils.PassengerUtil;
import com.akari.tickets.utils.RandCodeUtil;
import com.akari.tickets.utils.StationCodeUtil;
import com.akari.tickets.utils.SubscriptionUtil;
import com.akari.tickets.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    private EditText etUsername;
    private EditText etPassword;
    private ImageView captcha;
    private Button btnLogin;
    private ImageView selected1;
    private ImageView selected2;
    private ImageView selected3;
    private ImageView selected4;
    private ImageView selected5;
    private ImageView selected6;
    private ImageView selected7;
    private ImageView selected8;
    private List<ImageView> list;
    private String username;
    private String password;
    private String randCode;

    public static float density;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText) findViewById(R.id.username);
        etPassword = (EditText) findViewById(R.id.password);
        captcha = (ImageView) findViewById(R.id.captcha);
        btnLogin = (Button) findViewById(R.id.btn_login);
        selected1 = (ImageView) findViewById(R.id.selected1);
        selected2 = (ImageView) findViewById(R.id.selected2);
        selected3 = (ImageView) findViewById(R.id.selected3);
        selected4 = (ImageView) findViewById(R.id.selected4);
        selected5 = (ImageView) findViewById(R.id.selected5);
        selected6 = (ImageView) findViewById(R.id.selected6);
        selected7 = (ImageView) findViewById(R.id.selected7);
        selected8 = (ImageView) findViewById(R.id.selected8);
        addToList();

        captcha.setOnTouchListener(this);
        btnLogin.setOnClickListener(this);

        loginInit();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.captcha:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX() / density;
                    float y = event.getY() / density - 30;
                    RandCodeUtil.changeSelectedStatus(x, y, list, true);
                }
                break;
            default:
                break;
        }
        return false;
    }

    private void addToList() {
        list = new ArrayList<>();
        list.add(selected1);
        list.add(selected2);
        list.add(selected3);
        list.add(selected4);
        list.add(selected5);
        list.add(selected6);
        list.add(selected7);
        list.add(selected8);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            if (preCheckThrough()) {
                login();
            }
        }
    }

    private boolean preCheckThrough() {
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showShortToast(LoginActivity.this, "用户名不能为空");
            return false;
        }
        else if (TextUtils.isEmpty(password)) {
            ToastUtil.showShortToast(LoginActivity.this, "密码不能为空");
            return false;
        }
        else if (TextUtils.isEmpty(RandCodeUtil.getRandCode(list))) {
            ToastUtil.showShortToast(LoginActivity.this, "请点击验证码进行验证");
            return false;
        }
        randCode = RandCodeUtil.getRandCode(list);
        return true;
    }

    private void loginInit() {
        final HttpService service = RetrofitManager.getInstance().getService();
        SubscriptionUtil.unSubscribe(subscription);
        subscription = service.loginInit()
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(ResponseBody responseBody) {
                        return service.getPassCode("login", "sjrand");
                    }
                })
                .map(new Func1<ResponseBody, Bitmap>() {
                    @Override
                    public Bitmap call(ResponseBody responseBody) {
                        return BitmapFactory.decodeStream(responseBody.byteStream());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        captcha.setImageBitmap(bitmap);
                    }
                });
    }

    private void login() {
        final HttpService service = RetrofitManager.getInstance().getService();
        SubscriptionUtil.unSubscribe(subscription);
        subscription = service.checkRandCode(randCode, "sjrand")
                .flatMap(new Func1<CheckRandCodeResponse, Observable<LoginSuggestResponse>>() {
                    @Override
                    public Observable<LoginSuggestResponse> call(CheckRandCodeResponse checkRandCodeResponse) {
                        String result = checkRandCodeResponse.getData().getResult();
                        if (result.equals("1")) {
                            return service.loginSuggest(username, password, randCode);
                        }
                        else {
                            showToastAndClearSelected("验证码错误");
                            getPassCode();
                        }
                        return null;
                    }
                })
                .flatMap(new Func1<LoginSuggestResponse, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(LoginSuggestResponse loginSuggestResponse) {
                        String loginCheck = loginSuggestResponse.getData().getLoginCheck();
                        if (loginCheck == null) {
                            showToastAndClearSelected(loginSuggestResponse.getMessages()[0]);
                            getPassCode();
                        }
                        else if (loginCheck.equals("Y")){
                            return service.userLogin("");
                        }
                        return null;
                    }
                })
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(ResponseBody responseBody) {
                        showToastAndClearSelected("登录成功");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                StationCodeUtil.init(LoginActivity.this);
                            }
                        }).start();
                        return service.initPassengers("");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody>() {
                    @Override
                    public void call(ResponseBody responseBody) {
                        try {
                            String json = responseBody.string().split("passengers=")[1].split(";")[0];
                            PassengerUtil.savePassengers(json);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getPassCode() {
        HttpService service = RetrofitManager.getInstance().getService();
        SubscriptionUtil.unSubscribe(subscription);
        subscription = service.getPassCode("login", "sjrand")
                .map(new Func1<ResponseBody, Bitmap>() {
                    @Override
                    public Bitmap call(ResponseBody responseBody) {
                        return BitmapFactory.decodeStream(responseBody.byteStream());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        captcha.setImageBitmap(bitmap);
                    }
                });
    }

    private void showToastAndClearSelected(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShortToast(LoginActivity.this, s);
                RandCodeUtil.clearSelected(list);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SubscriptionUtil.unSubscribe(subscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SubscriptionUtil.unSubscribe(subscription);
    }
}
