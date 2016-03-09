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

package org.wso2.carbon.ibus.mediation.cheetah;


public class Constants {

    public static final String CHEETAH_ERROR_HANDLER = "CHEETAH_ERROR_HANDLER";

    public static final class QUERYDATA{
        public static final String QUERYPROPERTIES = "QUERYPROPERTIES";
        public static final String QUERYSTATEMENT = "QUERYSTATEMENT";
        public static final String RESULTSET = "RESULTSET";
        public static final String QUERYPARAMETERS = "QUERYPARAMETERS";
    }

    public static final class HTTPREQUEST{
        public static final String REQUESTBODY = "REQUESTBODY";
        public static final String RESPONSE_TYPE_JSON = "JSON";
        public static final String RESPONSE_TYPE_XML = "XML";
    }

    public static final class HTTPRESPONSE{
        public static final String RESPONSEROOT = "RESULTSET";
    }


    public static final class TRANSACTION{
        public static final String ERRORSEQ = "ERRORSEQ";
        public static final String BEGINTRANS= "BEGINTRANS";
        public static final String CONNECTIONS= "CONNECTIONS";
        public static final String COMMIT= "COMMIT";
        public static final String ROLLBACK= "ROLLBACK";
        public static final String GENERATEDID = "GENERATEDID";
        public static final String ROWCOUNT = "ROWCOUNT";
    }

}
