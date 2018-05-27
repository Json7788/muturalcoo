package com.fish.muturalcoo.translate;

/**
 * Created by allen on 2017/12/9.
 */

public class LanguageDetectBean {


    /**
     * code : 0
     * message :
     * codeDesc : Success
     * lang : zh
     */

    private int code;
    private String message;
    private String codeDesc;
    private String lang;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCodeDesc() {
        return codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
