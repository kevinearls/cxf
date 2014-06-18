/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.ws.security.cache;

import java.net.URL;

import org.apache.cxf.Bus;
import org.apache.cxf.buslifecycle.BusLifeCycleListener;
import org.apache.cxf.buslifecycle.BusLifeCycleManager;
import org.apache.wss4j.common.cache.EHCacheReplayCache;

/**
 * Wrap the default WSS4J EHCacheReplayCache in a BusLifeCycleListener, to make sure that
 * the cache is shutdown correctly.
 */
public class CXFEHCacheReplayCache extends EHCacheReplayCache implements BusLifeCycleListener {
    private Bus bus;
    
    public CXFEHCacheReplayCache(String key, Bus bus, URL configFileURL) {
        super(key, EHCacheUtils.getCacheManager(bus, configFileURL));
        this.bus = bus;
        if (bus != null) {
            bus.getExtension(BusLifeCycleManager.class).registerLifeCycleListener(this);
        }
    }
    
    @Override
    public void close() {
        // TODO - this code can be removed when WSS4J is updated to do it as part WSS-503
        if (cacheManager != null && cache != null) {
            cacheManager.removeCache(cache.getName());
        }
        
        super.close();
        
        if (bus != null) {
            bus.getExtension(BusLifeCycleManager.class).unregisterLifeCycleListener(this);
        }
    }
}