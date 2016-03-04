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
package org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.outbounddatasource;

import org.wso2.carbon.ibus.mediation.cheetah.outbounddatasource.OutboundDataSource;
import org.wso2.carbon.ibus.mediation.cheetah.outbounddatasource.protocol.RDBMSOutboundDataSource;

/**
 * Created by anupama on 3/1/16.
 */
public class OutboundDataSourceFactory {

    public static OutboundDataSource getOutboundDataSource(OutboundDataSourceType outboundDataSourceType,
                                                           String name, String uri, String username, String password) {
        OutboundDataSource outboundDataSource = null;
        switch (outboundDataSourceType) {
            case rdbms:
                outboundDataSource = new RDBMSOutboundDataSource(name, uri,username,password);
                break;
            case csv:
                break;
            default:
                break;
        }
        return outboundDataSource;
    }
}