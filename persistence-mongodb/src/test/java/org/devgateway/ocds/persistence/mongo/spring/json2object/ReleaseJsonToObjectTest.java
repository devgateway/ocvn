package org.devgateway.ocds.persistence.mongo.spring.json2object;

import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.spring.json2object.JsonToObject;
import org.devgateway.ocds.persistence.mongo.spring.json2object.ReleaseJsonToObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author idobre
 * @since 5/31/16
 */
public class ReleaseJsonToObjectTest {
    @Test
    public void toObject() throws Exception {
        String jsonRelease = "{\n" +
                "    id: \"123\",\n" +
                "    tag: [\"tender\"],\n" +
                "    planning: {\n" +
                "        budget: {\n" +
                "            description: \"budget desc...\",\n" +
                "            amount: {\n" +
                "                amount: 10000,\n" +
                "                currency: \"USD\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        JsonToObject releaseJsonToObject = new ReleaseJsonToObject(jsonRelease);

        Assert.assertTrue("Object is a Release", releaseJsonToObject.toObject() instanceof Release);

        Release release = (Release) releaseJsonToObject.toObject();

        Assert.assertEquals("IDs are the same", "123", release.getId());
        Assert.assertEquals("Check budget amount", new BigDecimal(10000), release.getPlanning().getBudget().getAmount().getAmount());
        Assert.assertEquals("Check budget currency", "USD", release.getPlanning().getBudget().getAmount().getCurrency());
    }

    @Test(expected = IOException.class)
    public void toObjectInvalidJson() throws Exception {
        String invalidJsonRelease = "{\n" +
                "    id: \"123\",\n" +
                "    tag: [\"tenderrrrrr\"],\n" +
                "    tag: [\"award\"],\n" +
                "}";

        JsonToObject invalidJsonToObject = new ReleaseJsonToObject(invalidJsonRelease);

        invalidJsonToObject.toObject();
    }

    @Test
    public void toObjectFromFile() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("json/release-json-to-object-test.json").getFile());

        JsonToObject releaseJsonToObject = new ReleaseJsonToObject(file);

        Assert.assertTrue("Object is a Release", releaseJsonToObject.toObject() instanceof Release);

        Release release = (Release) releaseJsonToObject.toObject();

        Assert.assertEquals("IDs are the same", "12345", release.getId());
        Assert.assertEquals("Check budget amount", new BigDecimal(10000), release.getPlanning().getBudget().getAmount().getAmount());
        Assert.assertEquals("Check budget currency", "RON", release.getPlanning().getBudget().getAmount().getCurrency());
    }
}
