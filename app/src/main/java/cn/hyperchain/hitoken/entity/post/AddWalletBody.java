package cn.hyperchain.hitoken.entity.post;

import java.util.List;

public class AddWalletBody {

    List<WalletItem> wallets;

    public List<WalletItem> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletItem> wallets) {
        this.wallets = wallets;
    }
}
