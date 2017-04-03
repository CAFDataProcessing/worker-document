package com.hpe.caf.worker.document.testing;

import java.util.Map;
import java.util.Objects;

/**
 * Document CustomData builder
 */
public class CustomDataBulder
{
    private final Map<String, String> map;
    private final DocumentBuilder parentBuilder;

    public CustomDataBulder(final Map<String, String> map, final DocumentBuilder parentBuilder)
    {
        this.map = Objects.requireNonNull(map);
        this.parentBuilder = parentBuilder;
    }

    /**
     * Add a new custom data
     *
     * @param name Custom data name
     * @param value Custom data value
     * @return This builder
     */
    public CustomDataBulder add(final String name, final String value)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);

        map.put(name, value);
        return this;
    }

    /**
     * Goes back to the parent DocumentBuilder.
     *
     * @return DocumentBuilder.
     */
    public DocumentBuilder documentBuilder()
    {
        return parentBuilder;
    }
}
