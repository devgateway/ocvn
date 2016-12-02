/**
 * 
 */
package org.devgateway.ocds.persistence.mongo;

import org.devgateway.ocds.persistence.mongo.flags.Flaggable;
import org.devgateway.ocds.persistence.mongo.flags.ReleaseFlags;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mpostelnicu
 *
 */
@Document(collection = "release")
public class FlaggedRelease extends Release implements Flaggable {

    @JsonIgnore
    private ReleaseFlags flags;

    @Override
    public ReleaseFlags getFlags() {
        return flags;
    }

    public void setFlags(ReleaseFlags flags) {
        this.flags = flags;
    }
    
    

}
