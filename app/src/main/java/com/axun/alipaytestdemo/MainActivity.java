package com.axun.alipaytestdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.axun.alipay.utils.AliPayRequest;

public class MainActivity extends AppCompatActivity {

    private String title = "测试支付";
    private String body = "测试商品";
    private String pay = "0.01";

    private AliPayRequest request;
    private Button mBtnPay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        request = new AliPayRequest(this);
        request.initRsa(PayInfoConfig.DaAi.APPID, PayInfoConfig.DaAi.RSA2, PayInfoConfig.DaAi.RSA, PayInfoConfig.DaAi.PID, PayInfoConfig.DaAi.SELLER);

        initView();
    }

    private String orderInfo="alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2017102709554419&biz_content=%7B%22body%22%3A%22alipaytest%22%2C%22out_trade_no%22%3A%221535618819272%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22test%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%220.01%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fwww.yykhc.com%2Fadm%2Finterfaces%2FalipayNotifyPay.do&sign=C1h7IdSFyR3elb2eeZ0e%2FNQv4HdSaccuOjwpwGAjixQQDGfe8ZrA9tQR6ykicyQho1aZ4bHXULGmi22e9deVTSrjI%2Fv7Y0gsM4pFiE9Q1UPz%2FOvhpFTsGuzJnB%2Bkbqqaoi9P8tzK0H72PGFx548rOXFEniWSt25gOpyEQnEuXojlu6WuFkc%2Bvo%2BUIuIIB1KgmkpgkN9VU4ho3W%2FOY%2FJsEciPjQeIkVawCAGUqPZiCUlQyEWjfAr2VWBBUYRAF9PgDvHgg1pjKc4keBcclOpRgx2oSfhIufFIPI92SJS3aTQHb3WrQLIOHG7VsdHOQ4EiAzH%2FeU9HBKvipZdyo34BVQ%3D%3D&sign_type=RSA2&timestamp=2018-08-30+16%3A46%3A59&version=1.0";

    private void initView() {
        mBtnPay = (Button) findViewById(R.id.btn_pay);
        mBtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.PayForOrder(request.getOrderInfo(title, body, pay), new AliPayRequest.OnPayResultCallback() {
                    @Override
                    public void onPaySuccess(String result) {
                        Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPayFaild(String error) {
                        Toast.makeText(MainActivity.this,error,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
