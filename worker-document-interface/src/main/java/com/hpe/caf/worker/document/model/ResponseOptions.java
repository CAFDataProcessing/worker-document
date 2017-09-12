package com.hpe.caf.worker.document.model;

import java.util.Map;

public class ResponseOptions
{
    private final String queueName;
    private final Map<String, String> customData;

    public ResponseOptions(String queueName, Map<String, String> customData)
    {
        this.queueName = queueName;
        this.customData = customData;
    }

    /**
     * Getter for property 'queueName'.
     *
     * @return Value for property 'queueName'.
     */
    public String getQueueName()
    {
        return queueName;
    }

    /**
     * Getter for property 'customData'.
     *
     * @return Value for property 'customData'.
     */
    public Map<String, String> getCustomData()
    {
        return customData;
    }
}
