package cn.hyperchain.hitoken.entity;

public class AddressBook {

    int id;
    String user_name;
    String first_char;
    String btc_address;
    String eth_address;

    String address;
    long time;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getFirst_char() {
        return first_char;
    }

    public void setFirst_char(String first_char) {
        this.first_char = first_char;
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
}
