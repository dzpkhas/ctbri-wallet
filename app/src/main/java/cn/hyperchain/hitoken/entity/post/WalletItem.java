package cn.hyperchain.hitoken.entity.post;

public class WalletItem {

    String account_id;
    String address;
    String type;

    public WalletItem() {

    }

    public WalletItem(String account_id, String address, String type) {
        this.account_id = account_id;
        this.address = address;
        this.type = type;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
