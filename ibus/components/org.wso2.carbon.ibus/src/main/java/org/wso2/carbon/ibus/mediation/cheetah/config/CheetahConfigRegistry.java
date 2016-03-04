/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.ibus.mediation.cheetah.config;


import org.wso2.carbon.ibus.mediation.cheetah.flow.Pipeline;
import org.wso2.carbon.ibus.mediation.cheetah.inbound.InboundEndpoint;
import org.wso2.carbon.ibus.mediation.cheetah.inbound.manager.InboundEndpointManager;
import org.wso2.carbon.ibus.mediation.cheetah.outbound.OutboundEndpoint;
import org.wso2.carbon.ibus.mediation.cheetah.outbounddatasource.OutboundDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A class that represents the Cheetah configuration
 */
public class CheetahConfigRegistry {


    private static CheetahConfigRegistry cheetahConfigRegistry = new CheetahConfigRegistry();

    private Map<String, InboundEndpoint> inboundEndpoints = new HashMap<>();

    private Map<String, Pipeline> pipelineMap = new HashMap<>();

    private Map<String, OutboundEndpoint> outBoundEndpointMap = new HashMap<>();
    private Map<String, OutboundDataSource> outBoundDataSourceMap = new HashMap<>();

    private List<ConfigRegistryObserver> observers = new ArrayList<>();

    private Map<String, ESBConfigHolder> configurations = new HashMap<>();

    public static CheetahConfigRegistry getInstance() {
        return cheetahConfigRegistry;
    }

    private CheetahConfigRegistry() {

    }

    public void addESBConfig(ESBConfigHolder config) {
        configurations.put(config.getName(), config);
        updateArtifacts(config);
    }

    public void removeESBConfig(ESBConfigHolder configHolder) {
        configurations.remove(configHolder.getName());
        unDeployArtifacts(configHolder);
    }

    public ESBConfigHolder getESBConfig(String name) {
        return configurations.get(name);
    }

    private void updateArtifacts(ESBConfigHolder config) {

        //For Inbound Endpoint
        InboundEndpoint inboundEndpoint = config.getInboundEndpoint();
        if (inboundEndpoint != null) {
            registerInboundEndpoint(inboundEndpoint);
        }

        //For Pipelines
        for (Pipeline pipeline : config.getPipelines().values()) {
            registerPipeline(pipeline);
        }

        //For Outbound Endpoints
        for (OutboundEndpoint outboundEndpoint : config.getOutboundEndpoints().values()) {
            registerOutboundEndpoint(outboundEndpoint);
        }

        //For Outbound DataSources
        for (OutboundDataSource outboundDataSource: config.getOutboundDataSources().values()) {
            registerOutboundDataSource(outboundDataSource);
        }


    }

    private void unDeployArtifacts(ESBConfigHolder esbConfigHolder) {
        //For Inbound Endpoint
        InboundEndpoint inboundEndpoint = esbConfigHolder.getInboundEndpoint();
        if (inboundEndpoint != null) {
            unregisterInboundEndpoint(inboundEndpoint);
        }

        //For Pipelines
        for (Pipeline pipeline : esbConfigHolder.getPipelines().values()) {
            unregisterPipeline(pipeline);
        }

        //For Outbound Endpoints
        for (OutboundEndpoint outboundEndpoint : esbConfigHolder.getOutboundEndpoints().values()) {
            unregisterOutboundEndpoint(outboundEndpoint);
        }

        //For Outbound DataSources
        for (OutboundDataSource outboundDataSource : esbConfigHolder.getOutboundDataSources().values()) {
            unregisterOutboundDataSource(outboundDataSource);
        }

    }

    public void registerInboundEndpoint(InboundEndpoint inboundEndpoint) {
        inboundEndpoints.put(inboundEndpoint.getName(), inboundEndpoint);
        InboundEndpointManager.getInstance().deploy(inboundEndpoint);

        //Inform Observers
        for (ConfigRegistryObserver observer : observers) {
            observer.endpointAdded(inboundEndpoint);
        }
    }

    public void unregisterInboundEndpoint(InboundEndpoint inboundEndpoint) {

        inboundEndpoints.remove(inboundEndpoint.getName());
        InboundEndpointManager.getInstance().undeploy(inboundEndpoint);

        //Inform Observers
        for (ConfigRegistryObserver observer : observers) {
            observer.endpointRemoved(inboundEndpoint);
        }
    }

    public InboundEndpoint getInboundEndpoint(String name) {
        return inboundEndpoints.get(name);
    }

    public void registerObserver(ConfigRegistryObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(ConfigRegistryObserver observer) {
        observers.remove(observer);
    }


    public void registerPipeline(Pipeline pipeline) {
        pipelineMap.put(pipeline.getName(), pipeline);
    }

    public void unregisterPipeline(Pipeline pipeline) {
        pipelineMap.remove(pipeline.getName());
    }

    public Pipeline getPipeline(String name) {
        return pipelineMap.get(name);
    }

    public OutboundEndpoint getOutboundEndpoint(String key) {
        return outBoundEndpointMap.get(key);
    }

    public void registerOutboundEndpoint(OutboundEndpoint outboundEndpoint) {
        outBoundEndpointMap.put(outboundEndpoint.getName(), outboundEndpoint);
    }

    public void unregisterOutboundEndpoint(OutboundEndpoint outboundEndpoint) {
        outBoundEndpointMap.remove(outboundEndpoint);
    }

    public OutboundDataSource getOutboundDataSource(String key) {
        return outBoundDataSourceMap.get(key);
    }

    public void registerOutboundDataSource(OutboundDataSource outboundDataSource) {
        outBoundDataSourceMap.put(outboundDataSource.getName(), outboundDataSource);
    }

    public void unregisterOutboundDataSource(OutboundDataSource outboundDataSource) {
        outBoundDataSourceMap.remove(outboundDataSource);
    }

}
