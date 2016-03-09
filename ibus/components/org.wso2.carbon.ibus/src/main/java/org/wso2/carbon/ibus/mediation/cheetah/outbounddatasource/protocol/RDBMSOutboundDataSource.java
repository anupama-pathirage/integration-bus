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
package org.wso2.carbon.ibus.mediation.cheetah.outbounddatasource.protocol;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.ibus.mediation.cheetah.Constants;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.filter.evaluator.Evaluator;
import org.wso2.carbon.ibus.mediation.cheetah.outbounddatasource.OutboundDataSource;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

import java.sql.*;
import java.util.Properties;

/**
 * RDBMS Type DataSource
 */
public class RDBMSOutboundDataSource extends OutboundDataSource {

    private static final Logger log = LoggerFactory.getLogger(RDBMSOutboundDataSource.class);


    private String uri;
    private String username;
    private String password;

    Connection rdbmsConnection=null;

    public RDBMSOutboundDataSource(String name,String uri,String username, String password) {
        super(name);
        this.uri = uri;
        this.username = username;
        this.password = password;

        createConnection(this.uri,this.username,this.password);
    }

    private void createConnection(String uri,String username, String password){
        try{
            Class.forName("com.mysql.jdbc.Driver"); //TODO:Add support for any driver
            rdbmsConnection = DriverManager.getConnection(uri, username, password);
        }catch (Exception e){
            String msg = "Error while creating RDBMS Connection";
            log.error(msg, e);
        }
    }

    @Override
    public boolean receive(CarbonMessage carbonMessage, CarbonCallback carbonCallback)
            throws Exception {

        log.info("Received to RDBMSOutboundDataSource:");

        Properties queryProperties = (Properties)carbonMessage.getProperty(Constants.QUERYDATA.QUERYPROPERTIES);
        boolean bBeginTrans = (boolean)carbonMessage.getProperty(Constants.TRANSACTION.BEGINTRANS);
        if(bBeginTrans) {
            log.info("Begin Trans Received");
            rdbmsConnection.setAutoCommit(false);
            carbonMessage.setProperty(Constants.TRANSACTION.CONNECTIONS,rdbmsConnection);
        }
        else
            log.info("No Begin Trans");

        if(queryProperties != null){
            boolean bExecuteNonQuery = false;
            String query = queryProperties.getProperty(Constants.QUERYDATA.QUERYSTATEMENT);
            String queryParameters = queryProperties.getProperty(Constants.QUERYDATA.QUERYPARAMETERS);
            String resultSetName = queryProperties.getProperty(Constants.QUERYDATA.RESULTSET);

            if(queryParameters != null) {
                query = generateQuery(carbonMessage,query, queryParameters);//TODO::Handle in a proper way - need to differentiate select from insert/update/delete queries.
                bExecuteNonQuery = true;
            }

            log.info("Query:"+query);
            log.info("resultSetName:"+resultSetName);

            if(rdbmsConnection !=null){
                if(bExecuteNonQuery){
                    Statement stmt = ExecuteNonQuery(query);
                    if(stmt != null)
                        carbonMessage.setProperty(resultSetName,stmt);
                }
                else{
                    ResultSet rs = ExecuteQuery(query);
                    if(rs!=null)
                        carbonMessage.setProperty(resultSetName,rs);
                }
            }
            else{
                log.info("Performing SQL Select Failed - No Connection");
            }
        }
        return false;
    }

    private String generateQuery(CarbonMessage carbonMessage, String query,String queryParameters)
            throws JSONException, SQLException {
        query.replace("'?'","?");
        String[] parameterArray = queryParameters.split(",");
        for( int i = 0; i < parameterArray.length; i++)
        {
            String parameter = parameterArray[i];
            Object parsedParameter = Evaluator.getRequestContent(carbonMessage, parameter);
            query= query.replaceFirst("\\?", "\"" + parsedParameter + "\"");
        }
        return query;
    }


    public ResultSet ExecuteQuery(String sql) throws SQLException{
        Statement stmt = rdbmsConnection.createStatement();
        ResultSet  rs =stmt.executeQuery(sql);
        return rs;
    }

    public Statement ExecuteNonQuery(String sql) throws SQLException{
        Statement stmt = rdbmsConnection.createStatement();
        boolean    bSuccess =stmt.execute(sql, Statement.RETURN_GENERATED_KEYS);
        return stmt;
    }

    public void setConnectionAutoCommit (boolean bCommitStatus)  throws SQLException{
        rdbmsConnection.setAutoCommit(bCommitStatus);
    }
}
