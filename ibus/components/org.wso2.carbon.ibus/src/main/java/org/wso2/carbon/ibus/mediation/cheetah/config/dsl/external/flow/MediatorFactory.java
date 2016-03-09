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
package org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.flow;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.ibus.mediation.cheetah.flow.Mediator;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.*;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.filter.FilterMediator;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.iterator.IteratorMediator;
import sun.misc.Service;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Factory class to generate mediators according to the provided type
 */
public class MediatorFactory {


    private static Map<String, Class> mediatorClassMap = new HashMap<String, Class>();

    private static final Logger log = LoggerFactory.getLogger(MediatorFactory.class);

    private final static MediatorFactory instance  = new MediatorFactory();

    private MediatorFactory() {
        loadMediators();
    }

    public static MediatorFactory getInstance() {
        return instance;
    }

    private final Class[] mediators = {
            CallMediator.class,
            FilterMediator.class,
            IteratorMediator.class,
            RespondMediator.class,
            LogMediator.class,
            EnrichMediator.class,
            TransformMediator.class,
            CallDataSourceMediator.class,
            GenerateResponseMediator.class
    };


    /**
     * Factory method to get a mediator object from name
     * @param mediatorType type of the mediator
     * @return mediator
     */
    public Mediator getMediator(String mediatorType, String configs) {
        Mediator mediator = null;

        Class c = mediatorClassMap.get(mediatorType);

        if (c != null) {
            try {
                mediator = (Mediator) c.newInstance();
                mediator.setConfigs(configs);
            } catch (Exception e) {
                log.error("Error while instantiation of " + mediatorType);
            }

        } else {
            log.error("Mediator implementation not found for " + mediatorType);
        }

        return mediator;
    }

    public Mediator getMediator(String mediatorType, String configs,Properties parametes) {
        Mediator mediator = null;

        Class c = mediatorClassMap.get(mediatorType);

        if (c != null) {
            try {
                mediator = (Mediator) c.newInstance();
                mediator.setConfigs(configs);
                mediator.setProperties(parametes);
            } catch (Exception e) {
                log.error("Error while instantiation of " + mediatorType);
            }

        } else {
            log.error("Mediator implementation not found for " + mediatorType);
        }

        return mediator;
    }

    private void loadMediators() {
        for (Class c : mediators) {
            try {
                Mediator med = (Mediator) c.newInstance();
                mediatorClassMap.put(med.getName(), c);
            } catch (Exception e) {
                log.error("Error while loading mediators to MediatorFactory");
            }
        }
        // now iterate through the available pluggable mediators
        registerExtensions();
    }

    /**
     * Register Mediators from external bundles
     */
    private void registerExtensions() {
        Iterator it = Service.providers(Mediator.class);
        while (it.hasNext()) {
            Mediator mediator = (Mediator) it.next();
            String name = mediator.getName();
            mediatorClassMap.put(name, mediator.getClass());
            if (log.isDebugEnabled()) {
                log.debug("Added Mediator Extension" + mediator.getClass() + " to handle " + name);
            }
        }
    }


}
