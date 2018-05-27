package com.fish.muturalcoo.translate;

/**
 * Created by Administrator on 2017/12/20.
 */

public interface TranslateListener {
    void onTranslated(String originalText, String originalLanguageCode, String objectiveText, String objectiveLanguageCode);
    void onError();
}
