package org.devgateway.ocds.persistence.mongo.flags;

public class Flag {

    private FlagType[] types;

    private Boolean value;

    private String rationale;

    public Flag(Boolean value, String rationale, FlagType[] types) {
        this.value = value;
        this.rationale = rationale;
        this.types = types;
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

    public FlagType[] getTypes() {
        return types;
    }

    public void setTypes(FlagType[] types) {
        this.types = types;
    }
}
