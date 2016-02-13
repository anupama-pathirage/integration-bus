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

package org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.filter.evaluator;

import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.filter.Condition;
import org.wso2.carbon.messaging.CarbonMessage;

import java.util.Map;

/**
 * A Util class responsible for evaluate carbon message according to condition
 */
public class Evaluator {


    public static boolean isHeaderMatched(CarbonMessage carbonMessage, Condition condition) {

        Map<String, String> map = carbonMessage.getHeaders();

        if (map.containsKey(condition.getSource().getKey())) {
            return condition.getPattern().matcher(map.get(condition.getSource().getKey())).matches();
        }
        return false;
    }


}
