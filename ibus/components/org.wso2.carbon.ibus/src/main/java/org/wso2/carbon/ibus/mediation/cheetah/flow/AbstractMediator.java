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

package org.wso2.carbon.ibus.mediation.cheetah.flow;

import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

import java.util.Properties;

/**
 * Base class for all the mediators. All the mediators must be extended from this base class
 */
public abstract class AbstractMediator implements Mediator {

    /* Pointer for the next sibling in the pipeline*/
    Mediator nextMediator = null;

    /**
     * Check whether a sibling is present after this in the pipeline
     *
     * @return whether a sibling is present after this
     */
    public boolean hasNext() {
        return nextMediator != null;
    }

    /**
     * Invoke the next sibling in the pipeline
     *
     * @param carbonMessage  Carbon message
     * @param carbonCallback Incoming Callback
     * @return whether mediation is proceeded
     * @throws Exception
     */
    public boolean next(CarbonMessage carbonMessage, CarbonCallback carbonCallback)
            throws Exception {
        if (hasNext()) {
            return nextMediator.receive(carbonMessage, carbonCallback);
        }
        return false;
    }

    /**
     * Set the pointer to the next sibling in the pipeline
     *
     * @param nextMediator Next sibling mediator in the pipeline
     */
    public void setNext(Mediator nextMediator) {
        this.nextMediator = nextMediator;
    }

    /**
     * TODO: We need a more better way to do this
     * Set Mediator Configurations
     *
     * @param configs configuration parameter
     */
    public void setConfigs(String configs) {
        //Do nothing
    }

    /**
     * Set Additional properties for the mediator
     *
     * @param properties Additional properties passed to the mediator
     */
    public void setProperties(Properties properties){

    }

    /**
     * Set Additional properties for the mediator
     *
     * @param key key value of a specific property
     * @param value value of a specific property
     */
    public void addProperty(String key, String value){

    }

}
