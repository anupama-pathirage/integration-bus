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
package org.wso2.carbon.ibus.mediation.cheetah.flow.mediators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.ibus.mediation.cheetah.Constants;
import org.wso2.carbon.ibus.mediation.cheetah.flow.*;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

/**
 * Indicate the Transaction begin at the transaction boundary
 */
public class BeginTransactionMediator extends AbstractMediator implements FlowController {

    private static final Logger log = LoggerFactory.getLogger(BeginTransactionMediator.class);

    private MediatorCollection childThenMediatorList = new MediatorCollection();
    private MediatorCollection childOtherwiseMediatorList = new MediatorCollection();

    public void addThenMediator(Mediator mediator) {
        childThenMediatorList.addMediator(mediator);
    }

    public void addOtherwiseMediator(Mediator mediator) {
        childOtherwiseMediatorList.addMediator(mediator);
    }

    @Override
    public String getName() {
        return "begintransaction";
    }

    @Override
    public boolean receive(CarbonMessage carbonMessage, CarbonCallback carbonCallback)
            throws Exception {

        carbonMessage.setProperty(Constants.TRANSACTION.ERRORSEQ,childOtherwiseMediatorList.getFirstMediator());
        carbonMessage.setProperty(Constants.TRANSACTION.BEGINTRANS,true);
        childThenMediatorList.getFirstMediator().receive(carbonMessage,new FlowControllerCallback(carbonCallback, this));
        return true;
    }

}
