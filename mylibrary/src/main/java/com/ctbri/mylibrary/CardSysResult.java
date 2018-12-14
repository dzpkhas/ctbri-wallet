package com.ctbri.mylibrary;

public class CardSysResult {
    public String resCode = "test";          //返回结果
    public String careerKey = "test";        //运营商密码
    public String encryptedSeed = "test";    //加密后种子

    public String getResCode() {
        return resCode;
    }

    public void  setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getCareerKey() {
        return careerKey;
    }

    public void setCareerKey(String careerKey) {
        this.careerKey = careerKey;
    }

    public String getEncryptedSeed() {
        return encryptedSeed;
    }

    public void setEncryptedSeed(String encryptedSeed) {
        this.encryptedSeed = encryptedSeed;
    }





}
