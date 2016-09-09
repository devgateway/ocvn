package org.devgateway.toolkit.forms.wicket.providers;

import org.apache.commons.lang3.StringUtils;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple choice provider that uses toString() for names. The list must not change since its indexes are used as
 * values.
 *
 * Created by octavian on 8/11/16.
 */
public class ListChoiceProvider<T> extends ChoiceProvider<T> {

    private final List<T> values;

    public ListChoiceProvider(final List<T> values) {
        this.values = values;
    }

    @Override
    public String getDisplayValue(final T object) {
        return object.toString();
    }

    @Override
    public String getIdValue(final T object) {
        return Integer.toString(values.indexOf(object));
    }

    @Override
    public void query(final String term, final int page, final Response<T> response) {
        String termLower = StringUtils.lowerCase(term);
        for (T value : values) {
            if (StringUtils.isBlank(termLower) || value.toString().toLowerCase().contains(termLower)) {
                response.add(value);
            }
        }
    }

    @Override
    public Collection<T> toChoices(final Collection<String> ids) {
        List<T> selectedChoices = new ArrayList<>(ids.size());
        for (String id : ids) {
            selectedChoices.add(values.get(Integer.parseInt(id)));
        }
        return selectedChoices;
    }
}
