package org.devgateway.ocds.persistence.mongo.spring;

/**
 * Created by mpost on 21-Jun-17.
 */
public class ImportResult {

    private Boolean success;

    private StringBuffer msgBuffer;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public StringBuffer getMsgBuffer() {
        return msgBuffer;
    }

    public void setMsgBuffer(StringBuffer msgBuffer) {
        this.msgBuffer = msgBuffer;
    }
}
