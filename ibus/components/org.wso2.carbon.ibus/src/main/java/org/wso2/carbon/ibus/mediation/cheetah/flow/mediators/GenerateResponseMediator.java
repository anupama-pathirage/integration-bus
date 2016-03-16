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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.ibus.mediation.cheetah.Constants;
import org.wso2.carbon.ibus.mediation.cheetah.flow.AbstractMediator;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.DefaultCarbonMessage;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * GenerateResponseMediator responsible for generating the response from outbound dataSource
 */
public class GenerateResponseMediator extends AbstractMediator {
    private static final Logger log = LoggerFactory.getLogger(GenerateResponseMediator.class);

    private enum ResponseType {
        json,xml
    }

    public static final String RESPONSE_TYPE_JSON = "JSON";
    public static final String RESPONSE_TYPE_XML = "XML";

    private ResponseType responseType = ResponseType.json;
    private String resultSetName = "RS";

    @Override
    public String getName() {
        return "generateresponse";
    }

    public void setConfigs(String configs) {
        if(configs != null && !configs.isEmpty()) {
            String[] configArray = configs.split(",");
            if(configArray.length == 2){
                String sResponseType = configArray[0];
                if(sResponseType.equalsIgnoreCase(Constants.HTTPREQUEST.RESPONSE_TYPE_JSON))
                    responseType = ResponseType.json;
                else if(sResponseType.equalsIgnoreCase(Constants.HTTPREQUEST.RESPONSE_TYPE_XML))
                    responseType = ResponseType.xml;
                else
                    log.error("Invalid Response Type Requested:"+configs+".Set to Default Type:JSON");

                resultSetName = configArray[1].toUpperCase();
            }
        }

    }

    @Override
    public boolean receive(CarbonMessage carbonMessage, CarbonCallback carbonCallback) throws Exception {
        log.info("Message received at Generate Response Mediator");
        DefaultCarbonMessage cMsg = getDefaultCarbonResponse(carbonMessage);
        return next(cMsg, carbonCallback);
    }

    public DefaultCarbonMessage getDefaultCarbonResponse(CarbonMessage carbonMessage){
        DefaultCarbonMessage defCmsg = new DefaultCarbonMessage();
        defCmsg.setHeader("Transfer-Encoding", "chunked");//TODO::Remove hard coded values
        defCmsg.setHeader("Connection","keep-alive");
        defCmsg.setProperty("DIRECTION","DIRECTION-RESPONSE");
        defCmsg.setProperty("HOST","localhost");
        defCmsg.setProperty("HTTP_STATUS_CODE",200);
        //defCmsg.setHeader("Content-Encoding","gzip");
        //defCmsg.setProperty("PORT","8080");


        Charset charset = Charset.forName("UTF-8");
        if(responseType == ResponseType.json){
            defCmsg.setHeader("Content-Type","application/json");
            String msg = getJSONResponse((ResultSet)carbonMessage.getProperty(resultSetName));
            ByteBuffer bf = ByteBuffer.wrap(msg.getBytes(charset));
            defCmsg.addMessageBody(bf);
        }
        else{
            String msg = getXMLResponse((ResultSet)carbonMessage.getProperty(resultSetName));
            ByteBuffer bf = ByteBuffer.wrap(msg.getBytes(charset));
            defCmsg.addMessageBody(bf);
        }

        defCmsg.setEndOfMsgAdded(true);



        return defCmsg;

    }

    public String getJSONResponse(ResultSet rs){
        String jsonRes = null;

        JSONArray ja = new JSONArray();

        try {
            ResultSetMetaData rsmd = rs.getMetaData();

            while (rs.next()) {

                int iColumnCount = rsmd.getColumnCount();
                JSONObject obj = new JSONObject();
                for(int i=1; i<=iColumnCount; i++)
                {
                    String sColumnName = rsmd.getColumnName(i);
                    String sValue = rs.getString(i);
                    obj.put(sColumnName,sValue);

                }
                ja.put(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put(Constants.HTTPRESPONSE.RESPONSEROOT, ja);
            jsonRes = mainObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonRes;
    }

    public String getXMLResponse(ResultSet rs){ //TODO:Implement this
        String xmlRes = null;
        return xmlRes;
    }
}