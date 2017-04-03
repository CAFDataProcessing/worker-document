package com.hpe.caf.worker.document.testing;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CustomDataBuilderTest
{
    @Test
    public void testAdd() throws Exception
    {
        Map<String, String> map = new HashMap<>();
        CustomDataBulder builder = new CustomDataBulder(map, null);

        builder.add("data-1", "value-1").add("data-2", "value-2");

        assertThat(map.size(), is(2));
        assertThat(map, hasEntry("data-1", "value-1"));
        assertThat(map, hasEntry("data-2", "value-2"));
    }
}
