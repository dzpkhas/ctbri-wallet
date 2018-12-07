package cn.hyperchain.hitoken.entity.post;

public class EditAddressBody {

    int id;

    String btc_address;
    String eth_address;
    String user_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBtc_address() {
        return btc_address;
    }

    public void setBtc_address(String btc_address) {
        this.btc_address = btc_address;
    }

    public String getEth_address() {
        return eth_address;
    }

    public void setEth_address(String eth_address) {
        this.eth_address = eth_address;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
