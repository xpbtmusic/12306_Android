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
import android.widget.Toast;

import com.akari.tickets.R;
import com.akari.tickets.retrofit.RetrofitManager;
import com.akari.tickets.retrofit.TicketsService;
import com.akari.tickets.utils.HttpUtil;
import com.akari.tickets.utils.PassengerUtil;
import com.akari.tickets.utils.StationCodeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;
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

//        HttpUtil.get("https://kyfw.12306.cn/otn/login/init", new InitCallback());
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
//                checkRandCodeAndUser();
            }
        }
    }

    private boolean preCheckThrough() {
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(getRandCode())) {
            Toast.makeText(LoginActivity.this, "请点击验证码进行验证",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
/**************************************************************************************************************************/
//    private void checkRandCodeAndUser() {
////        String url = "https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn?randCode=" + randCode + "&rand=sjrand";
////        HttpUtil.get(url, new CheckRandCodeCallback());
//
//        final TicketsService service = RetrofitManager.getInstance().getService();
//        service.checkRandCode(randCode, "sjrand").enqueue(new retrofit2.Callback<ResponseBody>() {
//            @Override
//            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                try {
//                    String json = response.body().string();
//                    JSONObject object = new JSONObject(json);
//                    String result = object.getJSONObject("data").getString("result");
//                    if (result.equals("1")) {
//                        service.loginSuggest(username, password, randCode).enqueue(new retrofit2.Callback<ResponseBody>() {
//                            @Override
//                            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                                try {
//                                    String json = response.body().string();
//                                    JSONObject object = new JSONObject(json);
//                                    if (!object.has("data")) {
//                                        String message = object.getString("messages")
//                                                .replaceAll("\\[", "")
//                                                .replaceAll("]", "")
//                                                .replaceAll("\"", "");
//                                        ToastUtil.showShortToast(LoginActivity.this, message);
//
//                                        clearSelected();
//                                        service.getPassCode("login", "sjrand").enqueue(new retrofit2.Callback<ResponseBody>() {
//                                            @Override
//                                            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                                                final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        captcha.setImageBitmap(bitmap);
//                                                    }
//                                                });
//                                            }
//
//                                            @Override
//                                            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
//
//                                            }
//                                        });
//                                    }
//                                    else if (object.getJSONObject("data").getString("loginCheck").equals("Y")){
//                                        ToastUtil.showShortToast(LoginActivity.this, "登陆成功");
//                                    }
//                                }
//                                catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
//                                t.printStackTrace();
//                            }
//                        });
//                    }
//                    else {
//                        ToastUtil.showShortToast(LoginActivity.this, "验证码错误");
//                        clearSelected();
//                        service.getPassCode("login", "sjrand").enqueue(new retrofit2.Callback<ResponseBody>() {
//                            @Override
//                            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                                final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        captcha.setImageBitmap(bitmap);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
//
//                            }
//                        });
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }

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

    class InitCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            List<String> list = response.headers("Set-Cookie");
            if (!list.isEmpty()) {
                for (String s : list) {
                    HttpUtil.cookie = HttpUtil.cookie + s.split(" ")[0];
                }
                HttpUtil.cookie = HttpUtil.cookie + "_jc_save_showIns=true;";
            }
            HttpUtil.get("https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand&" + new Random().nextDouble(), new GetPassCodeNewCallback());
        }
    }

    class GetPassCodeNewCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    captcha.setImageBitmap(bitmap);
                }
            });
            List<String> list = response.headers("Set-Cookie");
            if (!list.isEmpty()) {
                String currentCaptchaType = list.get(0).split(" ")[0];
                if (!HttpUtil.cookie.contains("current_captcha_type")) {
                    HttpUtil.cookie = HttpUtil.cookie + currentCaptchaType;
                }
            }
        }
    }

    class CheckRandCodeCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            try {
                JSONObject object = new JSONObject(json);
                String result = object.getJSONObject("data").getString("result");
                if (result.equals("1")) {
                    String url = "https://kyfw.12306.cn/otn/login/loginAysnSuggest";
                    String randCode1 = randCode.replaceAll("%2C", ",");

                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("loginUserDTO.user_name", username);
                    builder.add("userDTO.password", password);
                    builder.add("randCode", randCode1);

                    HttpUtil.post(url, builder.build(), new LoginSuggestCallback());
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                            HttpUtil.get("https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand&" + new Random().nextDouble(), new GetPassCodeNewCallback());
                            clearSelected();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class LoginSuggestCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                String s = response.body().string();
                JSONObject object = new JSONObject(s);
                JSONObject data = object.getJSONObject("data");

                if (!data.isNull("loginCheck") && data.getString("loginCheck").equals("Y")) {
                    String url = "https://kyfw.12306.cn/otn/login/userLogin";
                    String nrf = response.header("Set-Cookie", "").split(" ")[0];
                    HttpUtil.cookie = nrf + HttpUtil.cookie;

                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("_json_att", "");

                    HttpUtil.post(url, builder.build(), new UserLoginCallback());
                }
                else {
                    final String messages = object.getString("messages");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, messages.split("\"")[1], Toast.LENGTH_LONG).show();
                            HttpUtil.get("https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand&" + new Random().nextDouble(), new GetPassCodeNewCallback());
                            clearSelected();
                        }
                    });
                }
            } catch (JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "系统繁忙，请稍后重试！", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    class UserLoginCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    StationCodeUtil.init(LoginActivity.this);
                }
            }).start();

            String url = "https://kyfw.12306.cn/otn/passengers/init";
            HttpUtil.get(url, new PassengersCallback());
        }
    }

    class PassengersCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string().split("passengers=")[1].split(";")[0];
            PassengerUtil.savePassengers(json);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }



    


    private void loginInit() {
        final TicketsService service = RetrofitManager.getInstance().getService();
        subscription = service.loginInit()
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(ResponseBody responseBody) {
                        System.out.println("11111111111111111111111");
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
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        captcha.setImageBitmap(bitmap);
                    }
                });
    }

    private void unSubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribe();
    }
}
