package com.fish.muturalcoo.translate;

import android.util.Base64;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by allen on 2017/12/8.
 */

public class Tx {
    private String APPID="1254222753";
    private String SecretId="AKIDiJiy3w4hbJtHSREqGya1GKCuQksnN4Tf";
    private String SecretKey="9AVkojRzLUTIKCtNfomY2ZzDE3f5II5g";

    private HashMap<String, String> hashMap = new HashMap<>();
    public String getLanguageDetect(String detectContent) {
        hashMap.clear();
        hashMap.put("Action", "LanguageDetect");
        hashMap.put("Nonce", ""+ new Random().nextInt());
        hashMap.put("Region", "gz");
        hashMap.put("SecretId", SecretId);
        hashMap.put("Timestamp", ""+System.currentTimeMillis()/1000);
        hashMap.put("text", detectContent);

        Collection<String> keyset = hashMap.keySet();
        List<String> list = new ArrayList<String>(keyset);
        Collections.sort(list);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            buffer.append(key);
            buffer.append("=");
            buffer.append(hashMap.get(key));
            if (i != list.size() - 1) {
                buffer.append("&");
            }
        }
        //获取签名原文
        String src="GETtmt.api.qcloud.com/v2/index.php?"+buffer.toString();
//        Log.d("Tx","签名原文："+ src);
        //生成签名串
        String Signature = encodeSignatureString(src, SecretKey);
//        Log.d("Tx","签名："+ Signature);
        //URL编码字符串
        Signature=URLEncoder.encode(Signature);
//        Log.d("Tx","签名URL："+ Signature);
        StringBuffer bf = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            bf.append(key);
            bf.append("=");
            bf.append(URLEncoder.encode(hashMap.get(key)));
            if (i != list.size() - 1) {
                bf.append("&");
            }
        }
        String url = "https://tmt.api.qcloud.com/v2/index.php?" + bf.append("&Signature=" +Signature);
//        Log.d("Tx", "请求url："+url);

        return url;
    }

    private String encodeSignatureString(String src, String secret) {
         String hash="";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
            sha256_HMAC.init(secret_key);
            hash = Base64.encodeToString(sha256_HMAC.doFinal(src.getBytes()), Base64.DEFAULT).trim();
        } catch (Exception e) {
            System.out.println("Error");
        }
        return hash;
    }

}
