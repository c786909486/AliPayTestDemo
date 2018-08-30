package com.axun.alipay.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

/**
 * Created by hz-java on 2018/8/30.
 */

public class AliPayRequest {

    private String APPID;
    private String RSA2_PRIVATE;
    private String RSA_PRIVATE;
    private String PID;
    private String SELLER;

    private Activity context;

    public AliPayRequest(Activity context) {
        this.context = context;
    }

    public void initRsa(String APPID, String RSA2_PRIVATE, String RSA_PRIVATE, String PID, String SELLER){
        this.APPID = APPID;
        this.RSA2_PRIVATE = RSA2_PRIVATE;
        this.RSA_PRIVATE =RSA_PRIVATE;
        this.PID = PID;
        this.SELLER = SELLER;
    }

    public String getOrderInfo(String title,String body,String price){
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {

            return null;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID,PID,SELLER,title,body,price, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        String orderInfo = orderParam + "&" + sign;
        Log.d("OrderInfo",orderInfo);
        return orderInfo;
    }


    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    Log.d("ORDER_RESULT",resultStatus);
                    if (TextUtils.equals(resultStatus, "9000")) {
                        callback.onPaySuccess(resultInfo);
                    } else {
                        callback.onPayFaild(resultInfo);
                    }
                    break;

                default:
                    break;
            }
        };
    };

    public void PayForOrder(final String orderInfo,OnPayResultCallback callback){
        this.callback = callback;
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) context);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    private OnPayResultCallback callback;

    public interface OnPayResultCallback{
        void onPaySuccess(String result);
        void onPayFaild(String error);
    }

}
