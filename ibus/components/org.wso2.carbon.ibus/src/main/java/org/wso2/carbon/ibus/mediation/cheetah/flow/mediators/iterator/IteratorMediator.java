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
package org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.iterator;


import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.carbon.ibus.mediation.cheetah.Constants;
import org.wso2.carbon.ibus.mediation.cheetah.flow.AbstractMediator;
import org.wso2.carbon.ibus.mediation.cheetah.flow.Mediator;
import org.wso2.carbon.ibus.mediation.cheetah.flow.MediatorCollection;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.filter.evaluator.Evaluator;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

import java.util.List;

public class IteratorMediator extends AbstractMediator {

    private MediatorCollection childLoopMediatorList = new MediatorCollection();

    private String expression;

    public IteratorMediator() {

    }

    public IteratorMediator(String expression) {
        this.expression = expression;
    }

    public void addLoopMediator(Mediator mediator) {
        childLoopMediatorList.addMediator(mediator);
    }

    @Override
    public String getName() {
        return "iterator";
    }

    @Override
    public boolean receive(CarbonMessage carbonMessage, CarbonCallback carbonCallback) throws Exception {
        JSONArray jsonArrayOfRequest = (JSONArray) Evaluator.getRequestContent(carbonMessage,
                this.expression);
        for (int i =0; i < jsonArrayOfRequest.length(); i++) {
            for (Mediator mediator: childLoopMediatorList.getMediators()) {
                carbonMessage.setProperty(expression.split("\\.")[1].toUpperCase(),
                        jsonArrayOfRequest.getJSONObject(i));
                mediator.receive(carbonMessage, carbonCallback);
            }
        }
        return next(carbonMessage, carbonCallback);
    }

}
