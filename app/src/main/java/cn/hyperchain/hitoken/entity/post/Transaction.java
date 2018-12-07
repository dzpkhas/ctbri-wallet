package cn.hyperchain.hitoken.entity.post;

public class Transaction {


    String hex;
    TxBody tx_body;
    String type;
    String pay_word;

    public String getPay_word() {
        return pay_word;
    }

    public void setPay_word(String pay_word) {
        this.pay_word = pay_word;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public TxBody getTx_body() {
        return tx_body;
    }

    public void setTx_body(TxBody tx_body) {
        this.tx_body = tx_body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
