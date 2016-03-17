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
import org.wso2.carbon.ibus.mediation.cheetah.flow.AbstractMediator;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

import java.sql.Connection;

/**
 * Commit or Rollback Transcation at the end of transaction boundary
 */
public class EndTransationMediator extends AbstractMediator {
    private static final Logger log = LoggerFactory.getLogger(EndTransationMediator.class);

    private String endAction = "Message received at LogMediator";

    @Override
    public String getName() {
        return "endtransaction";
    }

    @Override
    public boolean receive(CarbonMessage carbonMessage, CarbonCallback carbonCallback) throws Exception {
        log.info(endAction);
        Connection rdbmsConnection= (Connection)carbonMessage.getProperty(Constants.TRANSACTION.CONNECTIONS);
        if(rdbmsConnection != null) {
            if (endAction.equals(Constants.TRANSACTION.COMMIT)) {
                rdbmsConnection.commit();
            }
            else if (endAction.equals(Constants.TRANSACTION.ROLLBACK)) {
                rdbmsConnection.rollback();
            }

            rdbmsConnection.setAutoCommit(true);
            carbonMessage.setProperty(Constants.TRANSACTION.BEGINTRANS,false);
        }
        return next(carbonMessage, carbonCallback);
    }

    public void setConfigs(String configs) {
        endAction = configs;

    }
}
