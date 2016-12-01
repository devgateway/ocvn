/**
 * 
 */
package org.devgateway.ocds.persistence.mongo.flags;

/**
 * @author mpostelnicu Represents the list of red flags at the Release level
 */
public class ReleaseFlags implements FlagsWrappable {

    // i038: Competitive tender w/ short bidding period
    private Flag i038;

    public Flag getI038() {
        return i038;
    }

    public void setI038(Flag i038) {
        this.i038 = i038;
    }

}
