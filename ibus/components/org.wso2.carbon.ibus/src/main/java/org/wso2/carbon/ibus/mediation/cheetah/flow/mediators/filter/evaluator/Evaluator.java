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

import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.wso2.carbon.ibus.mediation.cheetah.Constants;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.filter.Condition;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.filter.Source;
import org.wso2.carbon.messaging.CarbonMessage;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A Util class responsible for evaluate carbon message according to condition
 */
public class Evaluator {


    public static boolean isHeaderMatched(CarbonMessage carbonMessage, Source source, Pattern pattern) {

        Map<String, String> map = carbonMessage.getHeaders();

        if (map.containsKey(source.getKey())) {
            return pattern.matcher(map.get(source.getKey())).matches();
        }
        return false;
    }

    public static String getRequestContent(JSONObject jsonObject,String sContentPath) throws JSONException {
        String sValue = null;
        if(jsonObject != null) {
            sValue = jsonObject.getString(sContentPath);
        }
        return sValue;
    }

    public static String getResultSetContent(CarbonMessage cMsg,String sResultSetName,String sResultSetValue) throws SQLException {
        String sValue = "";
        Object objResult = cMsg.getProperty(sResultSetName);
        if(objResult instanceof Statement){
            Statement stmt = (Statement)objResult;
            if(sResultSetValue.equals("id")){
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()){
                    sValue=rs.getString(1);
                }
            }else if(sResultSetValue.equals("count")){
                sValue = Integer.toString(stmt.getUpdateCount());
            }
        }
        else if(objResult instanceof ResultSet){
            ResultSet rs = (ResultSet)objResult;
            if (rs.next()) {
                sValue = rs.getString(sResultSetValue);
            }
        }
        return sValue;
    }

    public static void setRequestJSONContent(CarbonMessage carbonMessage) throws JSONException {
        if(!carbonMessage.isEmpty()) {
            ByteBuffer buff = carbonMessage.getMessageBody();
            //if(buff.hasArray()){ //TODO::Check is the received one is the last buffer
            CharBuffer charBuffer = StandardCharsets.US_ASCII.decode(buff);
            String bodyContent = charBuffer.toString();
            if (!"".equals(bodyContent)) {
                JSONObject jsonobj = new JSONObject(bodyContent);
                carbonMessage.setProperty(Constants.HTTPREQUEST.REQUESTBODY,jsonobj);
            }
        }
    }

}
