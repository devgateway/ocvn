package org.devgateway.ocds.persistence.mongo.flags;

public class Flag {

    private Boolean value;

    private String rationale;

    public Flag(Boolean value, String rationale) {
        this.value = value;
        this.rationale = rationale;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

}
