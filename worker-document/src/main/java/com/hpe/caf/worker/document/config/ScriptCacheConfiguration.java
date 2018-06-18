/*
 * Copyright 2016-2018 Micro Focus or one of its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hpe.caf.worker.document.config;

public final class ScriptCacheConfiguration
{
    /**
     * The maximum number of entries the cache may contain.
     */
    private Long maximumSize;

    /**
     * The number of seconds since its last access before the entry expires from the cache.
     */
    private Long expireAfterAccess;

    /**
     * The number of seconds since it was created before the entry expires from the cache.
     */
    private Long expireAfterWrite;

    public Long getMaximumSize()
    {
        return maximumSize;
    }

    public void setMaximumSize(final Long maximumSize)
    {
        this.maximumSize = maximumSize;
    }

    public Long getExpireAfterAccess()
    {
        return expireAfterAccess;
    }

    public void setExpireAfterAccess(final Long expireAfterAccess)
    {
        this.expireAfterAccess = expireAfterAccess;
    }

    public Long getExpireAfterWrite()
    {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(final Long expireAfterWrite)
    {
        this.expireAfterWrite = expireAfterWrite;
    }
}
