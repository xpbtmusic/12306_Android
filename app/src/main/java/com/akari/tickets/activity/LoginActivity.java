package com.akari.tickets.activity;

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
import com.akari.tickets.http.RetrofitManager;
import com.akari.tickets.http.APIService;
import com.akari.tickets.utils.PassengerUtil;
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

    private float density;
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
                    changeSelectedStatus(x, y);
                }
                break;
            default:
                break;
        }
        return false;
    }

    private void changeSelectedStatus(float x, float y) {
        if (y > 10 && y < 77) {
            if (x > 5 && x < 72) {
                toggle(selected1);
            }
            else if (x > 77 && x < 144) {
                toggle(selected2);
            }
            else if (x > 149 && x < 216) {
                toggle(selected3);
            }
            else if (x > 221 && x < 288) {
                toggle(selected4);
            }
        }
        else if (y > 82 && y < 149) {
            if (x > 5 && x < 72) {
                toggle(selected5);
            }
            else if (x > 77 && x < 144) {
                toggle(selected6);
            }
            else if (x > 149 && x < 216) {
                toggle(selected7);
            }
            else if (x > 221 && x < 288) {
                toggle(selected8);
            }
        }
    }

    private void toggle(View view) {
        if (view.getVisibility() == View.INVISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    private void clearSelected() {
        selected1.setVisibility(View.INVISIBLE);
        selected2.setVisibility(View.INVISIBLE);
        selected3.setVisibility(View.INVISIBLE);
        selected4.setVisibility(View.INVISIBLE);
        selected5.setVisibility(View.INVISIBLE);
        selected6.setVisibility(View.INVISIBLE);
        selected7.setVisibility(View.INVISIBLE);
        selected8.setVisibility(View.INVISIBLE);
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
        else if (TextUtils.isEmpty(getRandCode())) {
            ToastUtil.showShortToast(LoginActivity.this, "请点击验证码进行验证");
            return false;
        }
        return true;
    }

    private String getRandCode() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getVisibility() == View.VISIBLE) {
                if (first) {
                    first = false;
                }
                else {
                    builder.append("%2C");
                }
                if (i < 4) {
                    builder.append(35 + i * 70);
                    builder.append("%2C");
                    builder.append("35");
                }
                else {
                    builder.append(35 + (i - 4) * 70);
                    builder.append("%2C");
                    builder.append("105");
                }
            }
        }
        randCode = builder.toString();
        return randCode;
    }

    private void loginInit() {
        final APIService service = RetrofitManager.getInstance().getService();
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
        final APIService service = RetrofitManager.getInstance().getService();
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
        APIService service = RetrofitManager.getInstance().getService();
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
                clearSelected();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SubscriptionUtil.unSubscribe(subscription);
    }
}
