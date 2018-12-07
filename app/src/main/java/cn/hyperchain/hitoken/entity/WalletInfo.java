package cn.hyperchain.hitoken.entity;

public class WalletInfo {

    String nameShort;
    String name;
    int iconRes;
    String token;

    //0 关闭 1 打开 -1 隐藏
    int open;

    public WalletInfo() {

    }

    public WalletInfo(String nameShort, String name, int iconRes, String token ,int open) {
        this.nameShort = nameShort;
        this.name = name;
        this.iconRes = iconRes;
        this.token = token;
        this.open = open;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
