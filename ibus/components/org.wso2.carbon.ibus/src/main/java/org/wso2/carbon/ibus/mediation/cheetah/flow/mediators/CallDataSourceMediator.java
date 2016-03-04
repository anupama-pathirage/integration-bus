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
package org.wso2.carbon.ibus.mediation.cheetah.flow.mediators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.ibus.mediation.cheetah.Constants;
import org.wso2.carbon.ibus.mediation.cheetah.config.CheetahConfigRegistry;
import org.wso2.carbon.ibus.mediation.cheetah.flow.AbstractMediator;
import org.wso2.carbon.ibus.mediation.cheetah.outbounddatasource.OutboundDataSource;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.DefaultCarbonMessage;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Send a Message out from Pipeline to an Outbound Datasource
 */
public class CallDataSourceMediator extends AbstractMediator {

    private String outboundDataSourceKey;

    private OutboundDataSource outboundDataSource;

    private Properties queryProperties;

    private static final Logger log = LoggerFactory.getLogger(CallDataSourceMediator.class);

    public CallDataSourceMediator() {
    }

    public CallDataSourceMediator(String outboundEPKey) {
        this.outboundDataSourceKey = outboundEPKey;
    }

    public CallDataSourceMediator(OutboundDataSource outboundDataSource) {
        this.outboundDataSource = outboundDataSource;
    }

    public void setConfigs(String configs) {
        outboundDataSourceKey = configs;
    }

    public void setProperties(Properties properties){
        queryProperties = properties;
    }

    public void addProperty(String key, String value)
    {
        if(queryProperties != null)
            queryProperties.setProperty(key,value);
    }
    @Override
    public String getName() {
        return "calldatasource";
    }

    @Override
    public boolean receive(CarbonMessage carbonMessage, CarbonCallback carbonCallback)
            throws Exception {

        OutboundDataSource dataSource = outboundDataSource;
        if (dataSource == null) {
            dataSource = CheetahConfigRegistry.getInstance().getOutboundDataSource(outboundDataSourceKey);

            if (dataSource == null) {
                log.error("Outbound DataSource : " +  outboundDataSourceKey + "not found ");
                return false;
            }
        }

        //CarbonCallback callback = new FlowControllerCallback(carbonCallback, this);

        //dataSource.receive(carbonMessage, callback);
        carbonMessage.setProperty(Constants.QUERYDATA.QUERYPROPERTIES,queryProperties);
        dataSource.receive(carbonMessage,carbonCallback);


        return next(carbonMessage, carbonCallback);
    }
}
