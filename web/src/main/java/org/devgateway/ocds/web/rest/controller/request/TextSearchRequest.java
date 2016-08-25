/**
 *
 */
package org.devgateway.ocds.web.rest.controller.request;

import javax.validation.constraints.Size;

/**
 * @author mihai
 *
 */
public class TextSearchRequest extends GenericPagingRequest {

    @Size(min = 3, max = 30)
    private String text;

    public TextSearchRequest() {
        super();
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

}
