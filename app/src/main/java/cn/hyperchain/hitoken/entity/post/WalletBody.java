package cn.hyperchain.hitoken.entity.post;

import java.util.List;

public class WalletBody {

    String pay_password;
    String sed;
    String wallet_name;
    List<WalletItem> wallets;

    public String getPay_password() {
        return pay_password;
    }

    public void setPay_password(String pay_password) {
        this.pay_password = pay_password;
    }

    public String getSed() {
        return sed;
    }

    public void setSed(String sed) {
        this.sed = sed;
    }

    public String getWallet_name() {
        return wallet_name;
    }

    public void setWallet_name(String wallet_name) {
        this.wallet_name = wallet_name;
    }

    public List<WalletItem> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletItem> wallets) {
        this.wallets = wallets;
    }
}
