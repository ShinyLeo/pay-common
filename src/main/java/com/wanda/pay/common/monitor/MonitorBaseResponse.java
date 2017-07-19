package com.wanda.pay.common.monitor;

/**
 * Created by tangxuanli on 17/3/16.
 */
public class MonitorBaseResponse {

    public  MonitorBaseResponse(){
        PayMonitorContext.current().setResponse(this);
    }

    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
