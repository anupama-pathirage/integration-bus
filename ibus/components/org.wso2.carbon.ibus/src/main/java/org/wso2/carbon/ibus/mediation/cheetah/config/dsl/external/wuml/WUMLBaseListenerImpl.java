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
package org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.wuml;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.ibus.mediation.cheetah.Constants;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.WUMLConfigurationBuilder;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.inbound.InboundEndpointFactory;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.inbound.InboundEndpointType;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.flow.MediatorFactory;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.outbound.OutboundEndpointFactory;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.outbound.OutboundEndpointType;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.StringParserUtil;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.outbounddatasource.OutboundDataSourceFactory;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.outbounddatasource.OutboundDataSourceType;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.wuml.generated.WUMLBaseListener;
import org.wso2.carbon.ibus.mediation.cheetah.config.dsl.external.wuml.generated.WUMLParser;
import org.wso2.carbon.ibus.mediation.cheetah.flow.Mediator;
import org.wso2.carbon.ibus.mediation.cheetah.flow.MediatorCollection;
import org.wso2.carbon.ibus.mediation.cheetah.flow.Pipeline;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.BeginTransactionMediator;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.CallDataSourceMediator;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.filter.Condition;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.filter.FilterMediator;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.filter.Source;
import org.wso2.carbon.ibus.mediation.cheetah.flow.mediators.iterator.IteratorMediator;
import org.wso2.carbon.ibus.mediation.cheetah.inbound.InboundEndpoint;
import org.wso2.carbon.ibus.mediation.cheetah.inbound.protocols.http.HTTPInboundEP;
import org.wso2.carbon.ibus.mediation.cheetah.outbound.OutboundEndpoint;
import org.wso2.carbon.ibus.mediation.cheetah.outbounddatasource.OutboundDataSource;

import java.util.Properties;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Implementation class of the ANTLR generated listener class
 */
public class WUMLBaseListenerImpl extends WUMLBaseListener {
    private static final Logger log = LoggerFactory.getLogger(WUMLBaseListener.class);

    WUMLConfigurationBuilder.IntegrationFlow integrationFlow;
    Stack<String> pipelineStack = new Stack<String>();

    //For Filter
    Stack<FilterMediator> filterMediatorStack = new Stack<FilterMediator>();
    Stack<IteratorMediator> iteratorMediatorStack = new Stack<IteratorMediator>();
    boolean ifMultiThenBlockStarted = false;
    boolean ifLoopBlockStarted = false;
    boolean ifElseBlockStarted = false;

    //For Transaction
    Stack<BeginTransactionMediator> transactionMediatorStack = new Stack<BeginTransactionMediator>();
    boolean transactionMultiThenBlockStarted = false;
    boolean transactionElseBlockStarted = false;

    public WUMLBaseListenerImpl() {
        this.integrationFlow = new WUMLConfigurationBuilder.IntegrationFlow("default");
    }

    public WUMLBaseListenerImpl(WUMLConfigurationBuilder.IntegrationFlow integrationFlow) {
        this.integrationFlow = integrationFlow;
    }

    public WUMLConfigurationBuilder.IntegrationFlow getIntegrationFlow() {
        return integrationFlow;
    }

    @Override
    public void exitScript(WUMLParser.ScriptContext ctx) {
        super.exitScript(ctx);
    }

    @Override
    public void exitHandler(WUMLParser.HandlerContext ctx) {
        super.exitHandler(ctx);
    }

    @Override
    public void exitStatementList(WUMLParser.StatementListContext ctx) {
        super.exitStatementList(ctx);
    }

    @Override
    public void exitStatement(WUMLParser.StatementContext ctx) {
        super.exitStatement(ctx);
    }

    @Override
    public void exitParticipantStatement(WUMLParser.ParticipantStatementContext ctx) {
        super.exitParticipantStatement(ctx);
    }

    @Override
    public void exitIntegrationFlowDefStatement(WUMLParser.IntegrationFlowDefStatementContext ctx) {
        //Create the integration flow when definition is found
        integrationFlow = new WUMLConfigurationBuilder.IntegrationFlow(ctx.INTEGRATIONFLOWNAME().getText());
        super.exitIntegrationFlowDefStatement(ctx);
    }

    @Override
    public void exitTitleStatement(WUMLParser.TitleStatementContext ctx) {
        //Create the integration flow when definition is found
        integrationFlow = new WUMLConfigurationBuilder.IntegrationFlow(ctx.IDENTIFIER().getText());
        super.exitTitleStatement(ctx);
    }

    @Override
    public void exitInboundEndpointDefStatement(WUMLParser.InboundEndpointDefStatementContext ctx) {
        String protocolName = StringParserUtil.getValueWithinDoubleQuotes(ctx.inboundEndpointDef().
                PROTOCOLDEF().getText());
        int port = Integer.parseInt(StringParserUtil.getValueWithinBrackets(ctx.inboundEndpointDef().
                PORTDEF().getText()));
        String context = StringParserUtil.getValueWithinDoubleQuotes(ctx.inboundEndpointDef().CONTEXTDEF().getText());
        InboundEndpoint inboundEndpoint = InboundEndpointFactory.getInboundEndpoint(InboundEndpointType
                .valueOf(protocolName), ctx.INBOUNDENDPOINTNAME().getText(), port);
        if (inboundEndpoint instanceof HTTPInboundEP) {
            ((HTTPInboundEP) inboundEndpoint).setContext(context);
        }
        integrationFlow.getEsbConfigHolder().setInboundEndpoint(inboundEndpoint);
        super.exitInboundEndpointDefStatement(ctx);
    }

    @Override
    public void exitPipelineDefStatement(WUMLParser.PipelineDefStatementContext ctx) {
        Pipeline pipeline = new Pipeline(ctx.PIPELINENAME().getText());
        integrationFlow.getEsbConfigHolder().addPipeline(pipeline);
        super.exitPipelineDefStatement(ctx);
    }

    @Override
    public void exitOutboundEndpointDefStatement(WUMLParser.OutboundEndpointDefStatementContext ctx) {
        String protocolName = StringParserUtil.getValueWithinDoubleQuotes(ctx.outboundEndpointDef().
                PROTOCOLDEF().getText());
        String uri = StringParserUtil.getValueWithinDoubleQuotes(ctx.outboundEndpointDef().HOSTDEF().getText());
        OutboundEndpoint outboundEndpoint = OutboundEndpointFactory.getOutboundEndpoint(OutboundEndpointType
                .valueOf(protocolName), ctx.OUTBOUNDENDPOINTNAME().getText(), uri);
        integrationFlow.getEsbConfigHolder().addOutboundEndpoint(outboundEndpoint);
        super.exitOutboundEndpointDefStatement(ctx);
    }

    @Override
    public void exitOutboundDataSourceDefStatement(WUMLParser.OutboundDataSourceDefStatementContext ctx) {
        String protocolName = StringParserUtil.getValueWithinDoubleQuotes(ctx.outboundDataSourceDef().
                PROTOCOLDEF().getText());
        String uri = StringParserUtil.getValueWithinDoubleQuotes(ctx.outboundDataSourceDef().HOSTDEF().getText());
        String userName = StringParserUtil.getValueWithinDoubleQuotes(ctx.outboundDataSourceDef().USERNAMEDEF().getText());
        String password = StringParserUtil.getValueWithinDoubleQuotes(ctx.outboundDataSourceDef().PASSWORDDEF().getText());
        OutboundDataSource outboundDataSource = OutboundDataSourceFactory.getOutboundDataSource(OutboundDataSourceType.valueOf(protocolName),
                ctx.OUTBOUNDDATASOURCENAME().getText(), uri, userName, password);
        integrationFlow.getEsbConfigHolder().addOutboundDataSource(outboundDataSource);
        super.exitOutboundDataSourceDefStatement(ctx);
    }


    @Override
    public void exitMediatorDefStatement(WUMLParser.MediatorDefStatementContext ctx) {
        super.exitMediatorDefStatement(ctx);
    }

    @Override
    public void exitInboundEndpointDef(WUMLParser.InboundEndpointDefContext ctx) {
        super.exitInboundEndpointDef(ctx);
    }

    @Override
    public void exitPipelineDef(WUMLParser.PipelineDefContext ctx) {
        super.exitPipelineDef(ctx);
    }

    @Override
    public void exitMediatorDef(WUMLParser.MediatorDefContext ctx) {
        super.exitMediatorDef(ctx);
    }

    @Override
    public void exitOutboundEndpointDef(WUMLParser.OutboundEndpointDefContext ctx) {
        super.exitOutboundEndpointDef(ctx);
    }

    @Override
    public void exitIntegrationFlowDef(WUMLParser.IntegrationFlowDefContext ctx) {
        super.exitIntegrationFlowDef(ctx);
    }

    @Override
    public void exitProcessmessageDef(WUMLParser.ProcessmessageDefContext ctx) {
        String mediatorName = StringParserUtil.getValueWithinDoubleQuotes(ctx.MEDIATORNAMESTRINGX().getText());
        String configurations = StringParserUtil.getValueWithinDoubleQuotes(ctx.CONFIGSDEF().getText());
        Mediator mediator = MediatorFactory.getInstance().getMediator(mediatorName, configurations);

        if (transactionMultiThenBlockStarted) {
            transactionMediatorStack.peek().addThenMediator(mediator);

        } else if (transactionElseBlockStarted) {
            transactionMediatorStack.peek().addOtherwiseMediator(mediator);

        } else {
            integrationFlow.getEsbConfigHolder().getPipeline(pipelineStack.peek()).addMediator(mediator);
        }

        /*if (ifMultiThenBlockStarted) {
            filterMediatorStack.peek().addThenMediator(mediator);

        } else if (ifElseBlockStarted) {
            filterMediatorStack.peek().addOtherwiseMediator(mediator);

        } else if (ifLoopBlockStarted) {
            iteratorMediatorStack.peek().addLoopMediator(mediator);

        } else {
            integrationFlow.getEsbConfigHolder().getPipeline(pipelineStack.peek()).addMediator(mediator);
        }*/
        super.exitProcessmessageDef(ctx);
    }


    @Override
    public void exitMessageProcessingDef(WUMLParser.MessageProcessingDefContext ctx) {
        String mediatorName = ctx.MEDIATORNAME().getText();
        String configurations = StringParserUtil.getValueWithinDoubleQuotes(ctx.ARGUMENTLISTDEF().getText());
        Mediator mediator = MediatorFactory.getInstance().getMediator(mediatorName, configurations);

        if (transactionMultiThenBlockStarted) {
            transactionMediatorStack.peek().addThenMediator(mediator);

        } else if (transactionElseBlockStarted) {
            transactionMediatorStack.peek().addOtherwiseMediator(mediator);

        } else {
            integrationFlow.getEsbConfigHolder().getPipeline(pipelineStack.peek()).addMediator(mediator);
        }

        /*if (ifMultiThenBlockStarted) {
            filterMediatorStack.peek().addThenMediator(mediator);

        } else if (ifElseBlockStarted) {
            filterMediatorStack.peek().addOtherwiseMediator(mediator);

        } else if (ifLoopBlockStarted) {
            iteratorMediatorStack.peek().addLoopMediator(mediator);

        } else {
            integrationFlow.getEsbConfigHolder().getPipeline(pipelineStack.peek()).addMediator(mediator);
        }*/
        super.exitMessageProcessingDef(ctx);
    }

    @Override
    public void exitProcessingStatement(WUMLParser.ProcessingStatementContext ctx) {
        super.exitProcessingStatement(ctx);
    }

    @Override
    public void exitRoutingStatement(WUMLParser.RoutingStatementContext ctx) {
        super.exitRoutingStatement(ctx);
    }

    @Override
    public void exitInvokeFromSource(WUMLParser.InvokeFromSourceContext ctx) {
        //String inbountEndpointName = ctx.INBOUNDENDPOINTNAME().getText();
        String pipelineName = ctx.PIPELINENAME().getText();
        integrationFlow.getEsbConfigHolder().getInboundEndpoint().setPipeline(pipelineName);
        pipelineStack.push(pipelineName);
        //activePipeline = pipelineName;
        super.exitInvokeFromSource(ctx);
    }

    @Override
    public void exitInvokeToTarget(WUMLParser.InvokeToTargetContext ctx) {
        Mediator mediator = MediatorFactory.getInstance().getMediator("call", ctx.OUTBOUNDENDPOINTNAME().getText());
        if(ifMultiThenBlockStarted) {
            filterMediatorStack.peek().addThenMediator(mediator);

        } else if(ifElseBlockStarted) {
            filterMediatorStack.peek().addOtherwiseMediator(mediator);

        } else {
//            String mediatorName = StringParserUtil.getValueWithinDoubleQuotes(ctx.MEDIATORNAMESTRINGX().getText());
//            String configurations = StringParserUtil.getValueWithinDoubleQuotes(ctx.CONFIGSDEF().getText());
//            Mediator mediator = MediatorFactory.getMediator(MediatorType.valueOf(mediatorName), configurations);
            integrationFlow.getEsbConfigHolder().getPipeline(pipelineStack.peek()).addMediator(mediator);
        }
        pipelineStack.pop();
        super.exitInvokeToTarget(ctx);
    }

    @Override
    public void exitInvokeFromTarget(WUMLParser.InvokeFromTargetContext ctx) {
        String pipelineName = ctx.PIPELINENAME().getText();
        pipelineStack.push(pipelineName);
        super.exitInvokeFromTarget(ctx);
    }

    @Override
    public void exitInvokeToSource(WUMLParser.InvokeToSourceContext ctx) {
        Mediator mediator = MediatorFactory.getInstance().getMediator("respond", null);
        if(ifMultiThenBlockStarted) {
            filterMediatorStack.peek().addThenMediator(mediator);

        } else if(ifElseBlockStarted) {
            filterMediatorStack.peek().addOtherwiseMediator(mediator);

        } else {
//            String mediatorName = StringParserUtil.getValueWithinDoubleQuotes(ctx.MEDIATORNAMESTRINGX().getText());
//            String configurations = StringParserUtil.getValueWithinDoubleQuotes(ctx.CONFIGSDEF().getText());
//            Mediator mediator = MediatorFactory.getMediator(MediatorType.valueOf(mediatorName), configurations);
            integrationFlow.getEsbConfigHolder().getPipeline(pipelineStack.peek()).addMediator(mediator);
        }
        pipelineStack.pop();
        super.exitInvokeToSource(ctx);
    }

    @Override
    public void exitInvokeToTargetDataSource(WUMLParser.InvokeToTargetDataSourceContext ctx) {
        String queryStatement = StringParserUtil.getValueWithinDoubleQuotes(ctx.queryDataDef().QUERYSTATEMENTDEF().getText());
        String queryParameters = StringParserUtil.getValueWithinBrackets(ctx.queryDataDef().QUERYPARAMETERDEF().getText());
        Properties queryProperties = new Properties();
        if(queryStatement != null)
            queryProperties.setProperty(Constants.QUERYDATA.QUERYSTATEMENT, queryStatement);

        if(queryParameters != null)
            queryProperties.setProperty(Constants.QUERYDATA.QUERYPARAMETERS, queryParameters);

        Mediator mediator = MediatorFactory.getInstance().getMediator("calldatasource", ctx.OUTBOUNDDATASOURCENAME().getText(),queryProperties);
        if(transactionMultiThenBlockStarted){
            transactionMediatorStack.peek().addThenMediator(mediator);
        }
        else if (transactionElseBlockStarted){
            transactionMediatorStack.peek().addOtherwiseMediator(mediator);
        }
        else {
            integrationFlow.getEsbConfigHolder().getPipeline(pipelineStack.peek()).addMediator(mediator);
        }

        /*if(ifMultiThenBlockStarted) {
            filterMediatorStack.peek().addThenMediator(mediator);

        } else if(ifElseBlockStarted) {
            filterMediatorStack.peek().addOtherwiseMediator(mediator);

        } else if(ifLoopBlockStarted) {
            iteratorMediatorStack.peek().addLoopMediator(mediator);

        } else {
            integrationFlow.getEsbConfigHolder().getPipeline(pipelineStack.peek()).addMediator(mediator);
        }*/
        pipelineStack.pop();
        super.exitInvokeToTargetDataSource(ctx);
    }

    @Override
    public void exitInvokeFromTargetDataSource(WUMLParser.InvokeFromTargetDataSourceContext ctx) {
        String pipelineName = ctx.PIPELINENAME().getText();
        pipelineStack.push(pipelineName);

        //TODO:
        String sText = StringParserUtil.getValueWithinDoubleQuotes(ctx.queryResponseDef().getText());
        Pipeline pipelineCurr = integrationFlow.getEsbConfigHolder().getPipeline(pipelineName);
        MediatorCollection pipelineMediators = pipelineCurr.getMediators();
        Mediator prevMediator = pipelineMediators.getLastMediator();
        if(prevMediator != null && prevMediator instanceof CallDataSourceMediator)
            prevMediator.addProperty(Constants.QUERYDATA.RESULTSET,sText);
        //
        super.exitInvokeFromTargetDataSource(ctx);
    }


    @Override
    public void exitParallelStatement(WUMLParser.ParallelStatementContext ctx) {
        super.exitParallelStatement(ctx);
    }

    @Override
    public void exitParMultiThenBlock(WUMLParser.ParMultiThenBlockContext ctx) {
        super.exitParMultiThenBlock(ctx);
    }

    @Override
    public void exitParElseBlock(WUMLParser.ParElseBlockContext ctx) {
        super.exitParElseBlock(ctx);
    }

    @Override
    public void exitIfStatement(WUMLParser.IfStatementContext ctx) {
        //ctx.expression().EXPRESSIONX()
        ifMultiThenBlockStarted = false;
        ifElseBlockStarted = false;
        if (!filterMediatorStack.isEmpty()) {
            filterMediatorStack.pop();
        }
        super.exitIfStatement(ctx);
    }

    @Override
    public void exitConditionStatement(WUMLParser.ConditionStatementContext ctx) {
        String sourceDefinition = StringParserUtil.getValueWithinDoubleQuotes(ctx.conditionDef().SOURCEDEF().getText());
        Source source = new Source(sourceDefinition);
        Condition condition =
                new Condition(source,
                              Pattern.compile(
                                      StringParserUtil.getValueWithinDoubleQuotes(
                                              ctx.conditionDef().PATTERNDEF().getText())));
        FilterMediator filterMediator = new FilterMediator(condition);
        integrationFlow.getEsbConfigHolder().getPipeline(pipelineStack.peek()).addMediator(filterMediator);
        filterMediatorStack.push(filterMediator);
        super.exitConditionStatement(ctx);
    }

    @Override
    public void enterIfMultiThenBlock(WUMLParser.IfMultiThenBlockContext ctx) {
        ifMultiThenBlockStarted = true;
        super.enterIfMultiThenBlock(ctx);
    }

    @Override
    public void enterIfElseBlock(WUMLParser.IfElseBlockContext ctx) {
        ifMultiThenBlockStarted = false;
        ifElseBlockStarted = true;
        super.enterIfElseBlock(ctx);
    }

    @Override
    public void exitIfMultiThenBlock(WUMLParser.IfMultiThenBlockContext ctx) {
        ifMultiThenBlockStarted = false;
        super.exitIfMultiThenBlock(ctx);
    }

    @Override
    public void exitIfElseBlock(WUMLParser.IfElseBlockContext ctx) {
        ifElseBlockStarted = false;
        super.exitIfElseBlock(ctx);
    }

    //@Override
    /*public void exitGroupStatement(WUMLParser.GroupStatementContext ctx) {
        super.exitGroupStatement(ctx);
    }*/

    @Override
    public void exitLoopStatement(WUMLParser.LoopStatementContext ctx) {
        super.exitLoopStatement(ctx);
    }

    @Override
    public void enterLoopBlock(WUMLParser.LoopBlockContext ctx) {
        ifLoopBlockStarted = true;
        super.enterLoopBlock(ctx);
    }

    @Override
    public void exitLoopBlock(WUMLParser.LoopBlockContext ctx) {
        ifLoopBlockStarted = false;
        super.exitLoopBlock(ctx);
    }


    @Override
    public void exitRefStatement(WUMLParser.RefStatementContext ctx) {
        pipelineStack.push(ctx.PIPELINENAME().getText());
        super.exitRefStatement(ctx);
    }

    @Override
    public void exitExpressionStatement(WUMLParser.ExpressionStatementContext ctx) {
        IteratorMediator iteratorMediator = new IteratorMediator(ctx.expressionDef().EXPRESSIONSTRING().getText());
        integrationFlow.getEsbConfigHolder().getPipeline(pipelineStack.peek()).addMediator(iteratorMediator);
        iteratorMediatorStack.push(iteratorMediator);
        super.exitExpressionStatement(ctx);
    }



    @Override
    public void enterGroupStatement(WUMLParser.GroupStatementContext ctx) {
        log.info("enterGroupStatement:");

        BeginTransactionMediator beginTransactionMediator = new BeginTransactionMediator();
        integrationFlow.getEsbConfigHolder().getPipeline(pipelineStack.peek()).addMediator(beginTransactionMediator);
        transactionMediatorStack.push(beginTransactionMediator);

        super.enterGroupStatement(ctx);

    }
    @Override
    public void exitGroupStatement(WUMLParser.GroupStatementContext ctx) {
        log.info("exitGroupStatement:"+ctx.groupIdentifierStatement().getText());
        transactionMultiThenBlockStarted = false;
        transactionElseBlockStarted = false;
        if (!transactionMediatorStack.isEmpty()) {
            transactionMediatorStack.pop();
        }
        super.exitGroupStatement(ctx);
    }

    @Override
    public void enterGroupMultiThenBlock(WUMLParser.GroupMultiThenBlockContext ctx) {
        log.info(("enterGroupMultiThenBlock:"));
        transactionMultiThenBlockStarted = true;
        super.enterGroupMultiThenBlock(ctx);
    }

    @Override
    public void exitGroupMultiThenBlock(WUMLParser.GroupMultiThenBlockContext ctx) {
        log.info(("exitGroupMultiThenBlock:"));
        transactionMultiThenBlockStarted = false;
        Mediator mediator = MediatorFactory.getInstance().getMediator("endtransaction", Constants.TRANSACTION.COMMIT);
        transactionMediatorStack.peek().addThenMediator(mediator);
        super.exitGroupMultiThenBlock(ctx);
    }

    @Override
    public void enterGroupElseBlock(WUMLParser.GroupElseBlockContext ctx) {
        log.info(("enterGroupElseBlock:"));
        transactionMultiThenBlockStarted = false;
        transactionElseBlockStarted = true;
        super.enterGroupElseBlock(ctx);
    }

    @Override
    public void exitGroupElseBlock(WUMLParser.GroupElseBlockContext ctx) {
        log.info(("exitGroupElseBlock:"));
        transactionElseBlockStarted = false;

        Mediator mediator = MediatorFactory.getInstance().getMediator("endtransaction", Constants.TRANSACTION.ROLLBACK);
        transactionMediatorStack.peek().addOtherwiseMediator(mediator);

        super.exitGroupElseBlock(ctx);
    }


}
