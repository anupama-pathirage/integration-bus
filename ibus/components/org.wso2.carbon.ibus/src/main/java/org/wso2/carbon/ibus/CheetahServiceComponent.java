/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.ibus;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.WUMLConfigurationBuilder;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.WUMLLoader;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.internal.JavaConfigurationBuilder;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.internal.DSLLoader;
import org.wso2.carbon.messaging.TransportSender;


/**
 * Service component for gate way.
 */
@Component(
        name = "CheetahServiceComponent",
        immediate = true
)
public class CheetahServiceComponent {
    @Reference(
            name = "transport-sender",
            service = TransportSender.class,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "removeTransportSender"
    )
    protected void addTransportSender(TransportSender transportSender) {
        ServiceContextHolder.getInstance().addTransportSender(transportSender);
    }

    protected void removeTransportSender(TransportSender transportSender) {
        ServiceContextHolder.getInstance().removeTransportSender(transportSender);
    }

    @Reference(
            name = "java-dsl",
            service = JavaConfigurationBuilder.class,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "removeJavaDSL"
    )
    protected void addJavaDSL(JavaConfigurationBuilder dsl) {
        DSLLoader.loadDSL(dsl);
    }

    protected void removeJavaDSL(JavaConfigurationBuilder dsl) {
    }

    @Reference(
            name = "wuml-dsl",
            service = WUMLConfigurationBuilder.class,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "removeWumlDSL"
    )
    protected void addWumlDSL(WUMLConfigurationBuilder dsl) {
        WUMLLoader.loadDSL(dsl);
    }

    protected void removeWumlDSL(WUMLConfigurationBuilder dsl) {
    }
}