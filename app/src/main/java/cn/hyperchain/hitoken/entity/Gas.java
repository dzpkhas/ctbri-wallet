package cn.hyperchain.hitoken.entity;

public class Gas {

    long gas;
    long gas_limit;
    long gas_max;
    long gas_min;


    public long getGas() {
        return gas;
    }

    public void setGas(long gas) {
        this.gas = gas;
    }

    public long getGas_limit() {
        return gas_limit;
    }

    public void setGas_limit(long gas_limit) {
        this.gas_limit = gas_limit;
    }

    public long getGas_max() {
        return gas_max;
    }

    public void setGas_max(long gas_max) {
        this.gas_max = gas_max;
    }

    public long getGas_min() {
        return gas_min;
    }

    public void setGas_min(long gas_min) {
        this.gas_min = gas_min;
    }
}
