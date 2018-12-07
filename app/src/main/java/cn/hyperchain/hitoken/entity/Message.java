package cn.hyperchain.hitoken.entity;

public class Message {
    long time;
    String value;
    String plus_or_minus;
    String type;
    String status;
    String address_or_name;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPlus_or_minus() {
        return plus_or_minus;
    }

    public void setPlus_or_minus(String plus_or_minus) {
        this.plus_or_minus = plus_or_minus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress_or_name() {
        return address_or_name;
    }

    public void setAddress_or_name(String address_or_name) {
        this.address_or_name = address_or_name;
    }
}
