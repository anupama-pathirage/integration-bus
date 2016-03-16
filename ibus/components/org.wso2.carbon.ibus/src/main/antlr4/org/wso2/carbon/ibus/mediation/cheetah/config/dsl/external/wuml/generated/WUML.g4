/*
 * Copyright (c) 2005-2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

grammar WUML;

/* --- PARSER RULES --- */

script
    : ( handler | NEWLINE )* EOF;

handler
    : (commentStatement NEWLINE+)?
        STARTUMLX NEWLINE+
          statementList
      ENDUMLX ;

statementList
    : ( statement NEWLINE+ )* ;

statement
    : titleStatement
    | participantStatement
    | processingStatement
    | routingStatement
    | parallelStatement
    | ifStatement
    | loopStatement
    | groupStatement
    | refStatement
    | commentStatement
    ;


/* PARSER: built-in command rules */

titleStatement
    : INTEGRATIONFLOWX WS+ COLON WS+ IDENTIFIER;

participantStatement
    : integrationFlowDefStatement
    | inboundEndpointDefStatement
    | pipelineDefStatement
    | outboundEndpointDefStatement
    | outboundDataSourceDefStatement
    | mediatorDefStatement;

integrationFlowDefStatement
    : PARTICIPANT WS+ INTEGRATIONFLOWNAME WS+ COLON WS+ integrationFlowDef;

inboundEndpointDefStatement
    : PARTICIPANT WS+ INBOUNDENDPOINTNAME WS+ COLON WS+ inboundEndpointDef;

pipelineDefStatement
    : PARTICIPANT WS+ PIPELINENAME WS+ COLON WS+ pipelineDef;

outboundEndpointDefStatement
    : PARTICIPANT WS+ OUTBOUNDENDPOINTNAME WS+ COLON WS+ outboundEndpointDef;

outboundDataSourceDefStatement
    : PARTICIPANT WS+ OUTBOUNDDATASOURCENAME WS+ COLON WS+ outboundDataSourceDef;

mediatorDefStatement
    : PARTICIPANT WS+ MEDIATORNAME WS+ COLON WS+ mediatorDef;

processingStatement : processmessageDef | messageProcessingDef;

integrationFlowDef: INTEGRATIONFLOWX LPAREN STRINGX RPAREN;

inboundEndpointDef: INBOUNDENDPOINTX LPAREN PROTOCOLDEF COMMA_SYMBOL PORTDEF COMMA_SYMBOL CONTEXTDEF RPAREN;

pipelineDef: PIPELINEX LPAREN STRINGX RPAREN;

mediatorDef: MEDIATORX LPAREN STRINGX RPAREN;

outboundEndpointDef: OUTBOUNDENDPOINTX LPAREN PROTOCOLDEF COMMA_SYMBOL HOSTDEF RPAREN;

outboundDataSourceDef:OUTBOUNDDATASOURCEX LPAREN PROTOCOLDEF COMMA_SYMBOL HOSTDEF COMMA_SYMBOL USERNAMEDEF COMMA_SYMBOL PASSWORDDEF RPAREN;

processmessageDef: PROCESS_MESSAGEX LPAREN MEDIATORNAMESTRINGX COMMA_SYMBOL CONFIGSDEF RPAREN;

messageProcessingDef: MEDIATORNAME ARGUMENTLISTDEF;

queryDataDef:QUERYX LPAREN QUERYSTATEMENTDEF (COMMA_SYMBOL QUERYPARAMETERDEF)? RPAREN;

queryResponseDef: RESULTSETX LPAREN STRINGX RPAREN;

routingStatement
    : invokeFromSource
    | invokeFromTarget
    | invokeToSource
    | invokeToTarget
    | invokeToTargetDataSource
    | invokeFromTargetDataSource;

invokeFromSource: INBOUNDENDPOINTNAME WS+ ARROW WS+ PIPELINENAME WS+ COLON WS+ STRINGX;
invokeToTarget: PIPELINENAME WS+ ARROW WS+ OUTBOUNDENDPOINTNAME WS+ COLON WS+ STRINGX;
invokeFromTarget: OUTBOUNDENDPOINTNAME WS+ ARROW WS+ PIPELINENAME WS+ COLON WS+ STRINGX;
invokeToSource: PIPELINENAME WS+ ARROW WS+ INBOUNDENDPOINTNAME WS+ COLON WS+ STRINGX;
invokeToTargetDataSource: PIPELINENAME WS+ ARROW WS+ OUTBOUNDDATASOURCENAME WS+ COLON WS+ queryDataDef;
invokeFromTargetDataSource: OUTBOUNDDATASOURCENAME WS+ ARROW WS+ PIPELINENAME WS+ COLON WS+ queryResponseDef;

/* -> PAR block rules */

parallelStatement
    : PAR NEWLINE?
      NEWLINE parMultiThenBlock
      END
    ;

parMultiThenBlock
    : statementList NEWLINE (parElseBlock)? ;


parElseBlock
    : (ELSE NEWLINE statementList)+ ;

/* -> ALT block rules */

ifStatement
    : ALT WS conditionStatement NEWLINE
      NEWLINE? ifMultiThenBlock
      END
    ;

ifMultiThenBlock
    : statementList NEWLINE (ifElseBlock)? ;


ifElseBlock
    : (ELSE NEWLINE statementList)+ ;

/* -> GROUP block rules */
/*groupStatement
    : GROUP WS IDENTIFIER NEWLINE
      NEWLINE? statementList
      END
    ;*/
 groupStatement
     : GROUP WS groupIdentifierStatement NEWLINE
       NEWLINE groupMultiThenBlock
       END
     ;

 groupMultiThenBlock
     : statementList NEWLINE (groupElseBlock)? ;


 groupElseBlock
     : (ELSE NEWLINE statementList)+ ;

/* -> LOOP block rules */
loopStatement
     : LOOP WS expressionStatement NEWLINE
       NEWLINE? loopBlock
       END
     ;

 loopBlock
     : statementList;

refStatement
    : REF WS PIPELINENAME NEWLINE?;

conditionStatement
    : conditionDef;

conditionDef: CONDITIONX LPAREN SOURCEDEF COMMA_SYMBOL PATTERNDEF RPAREN;

groupIdentifierStatement
    : IDENTIFIER;


commentStatement
    : COMMENT;

expressionStatement
    : expressionDef;

expressionDef: EXPRESSIONSTRING;


/* --- LEXER rules --- */

/* LEXER: keyword rules */

COMMENT
    :  '/*' .*? '*/'
    ;

SOURCEDEF: SOURCE LPAREN CONFIGPARAMS RPAREN;

PATTERNDEF: PATTERN LPAREN STRINGX RPAREN;

PROTOCOLDEF: PROTOCOL LPAREN STRINGX RPAREN;

PORTDEF: PORT LPAREN NUMBER RPAREN;

CONTEXTDEF: CONTEXT LPAREN URLSTRINGX RPAREN;

HOSTDEF: HOST LPAREN URLSTRINGX RPAREN;

USERNAMEDEF:USERNAME LPAREN STRINGX RPAREN;

PASSWORDDEF:PASSWORD LPAREN NUMERICSTRINGX RPAREN;

QUERYSTATEMENTDEF: QUERYSTATEMENT QUERYDEF;

QUERYPARAMETERDEF: QUERYPARAMETER ARGUMENTLISTDEF;

//PROCESSMESSAGEDEF: PROCESS_MESSAGE LPAREN MEDIATORNAMESTRINGX COMMA_SYMBOL CONFIGSDEF RPAREN;

CONFIGSDEF: CONFIGS LPAREN (CONFIGPARAMS COMMA_SYMBOL)* (CONFIGPARAMS)* RPAREN;

ARGUMENTLISTDEF: LPAREN (CONFIGPARAMS COMMA_SYMBOL)* (CONFIGPARAMS)* RPAREN;

QUERYDEF: LPAREN DOUBLEQUOTES (QUERYSYNTAXPARAMS COMMA_SYMBOL)* (QUERYSYNTAXPARAMS)* DOUBLEQUOTES RPAREN;

EXPRESSIONX: EXPRESSION;
CONDITIONX: CONDITION;


TIMEOUTDEF: TIMEOUT LPAREN NUMBER RPAREN;

INTEGRATIONFLOWX: INTEGRATIONFLOW;
INBOUNDENDPOINTX: INBOUNDENDPOINT;
PIPELINEX: PIPELINE;
MEDIATORX: MEDIATOR;
OUTBOUNDENDPOINTX: OUTBOUNDENDPOINT;
PROCESS_MESSAGEX: PROCESS_MESSAGE;
OUTBOUNDDATASOURCEX: OUTBOUNDDATASOURCE;
QUERYX:QUERY;
RESULTSETX: RESULTSET;
//PROTOCOLX: PROTOCOL;
//PORTX: PORT;
//CONTEXTX: CONTEXT;
//HOSTX: HOST;
//TIMEOUTX: TIMEOUT;
//CONFIGSX: CONFIGS;
//CONFIGPARAMSX: CONFIGPARAMS;

MEDIATORNAMESTRINGX: MEDIATORNAMESTRING;
STRINGX: STRING;
NUMERICSTRINGX: NUMERICSTRING;
URLSTRINGX: URLSTRING;



fragment STRING: DOUBLEQUOTES IDENTIFIER DOUBLEQUOTES;
fragment NUMERICSTRING: DOUBLEQUOTES NUMERIC DOUBLEQUOTES;
fragment URLSTRING: DOUBLEQUOTES URL DOUBLEQUOTES;
fragment MEDIATORNAMESTRING: DOUBLEQUOTES MEDIATORNAME DOUBLEQUOTES;
fragment EXPRESSION: LPAREN CONFIGPARAMS RPAREN;


//FLOWCOMMENT: DOUBLEQUOTES IDENTIFIER DOUBLEQUOTES;

INTEGRATIONFLOWNAME: INTEGRATIONFLOW POSTSCIPRT;

MEDIATORNAME: CALL | FILTER | RESPOND | ENRICH | LOG | TRANSFORM| GENERATERESPONSE;

INBOUNDENDPOINTNAME: INBOUNDENDPOINT POSTSCIPRT;

PIPELINENAME: PIPELINE POSTSCIPRT;

OUTBOUNDENDPOINTNAME: OUTBOUNDENDPOINT POSTSCIPRT;

OUTBOUNDDATASOURCENAME: OUTBOUNDDATASOURCE POSTSCIPRT;


AND : A N D ;
DIV : D I V ;
DO : D O ;
DOWN : D O W N ;
EXIT: E X I T ;
FOR: F O R ;
FOREVER : F O R E V E R ;
FUNCTION : F U N C T I O N ;
GLOBAL : G L O B A L ;
HYPERCARD : H Y P E R C A R D ;
IF : I F ;
IS : I S ;
MOD : M O D ;
NEXT : N E X T ;
NOT : N O T ;
ON : O N ;
OR : O R ;
REPEAT : R E P E A T ;
THEN : T H E N ;
TIMES : T I M E S ;
TO : T O ;
UNTIL : U N T I L ;
WITH : W I T H ;
WHILE : W H I L E ;

/* TYPES */

STARTUMLX: STARTUML;
ENDUMLX: ENDUML;

fragment STARTUML: '@startuml';
fragment ENDUML: '@enduml';

PARTICIPANT: P A R T I C I P A N T;

PAR: P A R;
ALT: A L T;
REF: R E F;
END: E N D;
ELSE: E L S E;
LOOP: L O O P;
GROUP: G R O U P;




ACTOR: A C T O R;
USECASE: U S E C A S E;
CLASS: C L A S S;
INTERFACE: I N T E R F A C E;
ABSTRACT: A B S T R A C T;
ENUM: E N U M;
COMPONENT: C O M P O N E N T;
STATE: S T A T E;
OBJECT: O B J E C T;
ARTIFACT: A R T I F A C T;
FOLDER : F O L D E R;
RECT: R E C T;
NODE: N O D E;
FRAME: F R A M E;
CLOUD: C L O U D;
DATABASE: D A T A B A S E;
STORAGE: S T O R A G E;
AGENT: A G E N T;
BOUNDARY: B O U N D A R Y;
CONTROL: C O N T R O L;
ENTITY: E N T I T Y;
CARD: C A R D;


/* LEXER: symbol rules */

AMP_SYMBOL : '&' ;
AMPAMP_SYMBOL : '&&' ;
CARET_SYMBOL : '^' ;
COMMA_SYMBOL : ',' ;
COMMENT_SYMBOL : '--' ;
CONTINUATION_SYMBOL : '\\' | '\u00AC' ;
EQ_SYMBOL : '=' ;
GE_SYMBOL : '>=' | '\u2265' ;
GT_SYMBOL : '>' ;
LE_SYMBOL : '<=' | '\u2264' ;
LT_SYMBOL : '<' ;
MINUS_SYMBOL : '-' ;
NE_SYMBOL : '<>' | '\u2260';
PLUS_SYMBOL : '+' ;
STAR_SYMBOL : '*' ;
SLASH_SYMBOL : '/' ;
UNDERSCORE : '-';

COLON: ':';
ARROW: '->';
fragment DOUBLEQUOTES: '"';
SINGLEQUOTES: '\'';

/* LEXER: miscellanea */

LPAREN : '(' ;
RPAREN : ')' ;

NEWLINE
    : ( '\r\n' | '\n' | '\r' ) ;

WS
    : ' ';

fragment POSTSCIPRT
    : ( 'a'..'z' | 'A'..'Z' | DIGIT | '_')*;

IDENTIFIER
    : ( 'a'..'z' | 'A'..'Z' ) ( 'a'..'z' | 'A'..'Z' | DIGIT | '_')+ ;

EXPRESSIONSTRING
    : '$' IDENTIFIER '.' IDENTIFIER ;

NUMERIC
    : ( 'a'..'z' | 'A'..'Z' | DIGIT | '_')* ;

fragment CONFIGPARAMS: (WS | [a-zA-Z\?] | COLON | [0-9] | '$' | '.' | '@' | SINGLEQUOTES | DOUBLEQUOTES | '{' | '}' | AMP_SYMBOL | AMPAMP_SYMBOL | CARET_SYMBOL | COMMA_SYMBOL | COMMENT_SYMBOL | CONTINUATION_SYMBOL | EQ_SYMBOL | GE_SYMBOL | GT_SYMBOL | LE_SYMBOL | LT_SYMBOL | MINUS_SYMBOL | NE_SYMBOL | PLUS_SYMBOL | STAR_SYMBOL | SLASH_SYMBOL )+;

fragment QUERYSYNTAXPARAMS: (WS | [a-zA-Z\?] | COLON | [0-9] | '$' | '.' | '@' | SINGLEQUOTES | DOUBLEQUOTES | '(' | ')' | AMP_SYMBOL | AMPAMP_SYMBOL | CARET_SYMBOL | COMMA_SYMBOL | COMMENT_SYMBOL | CONTINUATION_SYMBOL | EQ_SYMBOL | GE_SYMBOL | GT_SYMBOL | LE_SYMBOL | LT_SYMBOL | MINUS_SYMBOL | NE_SYMBOL | PLUS_SYMBOL | STAR_SYMBOL | SLASH_SYMBOL )+;

NUMBER
    : ( '0' | '1'..'9' DIGIT*) ('.' DIGIT+ )? ;

URL: ([a-zA-Z/\?&] | COLON | [0-9])+;

//QUERYSYNTAXPARAMS: INSERTQUERY | SELECTQUERY | UPDATEQUERY | DELETEQUERY;
//
//INSERTQUERY: INSERT WS INTO WS POSTSCIPRT WS VALUES LPAREN (QUESTION_MARK | POSTSCIPRT COMMA_SYMBOL)* (QUESTION_MARK | POSTSCIPRT)? RPAREN;
//
//SELECTQUERY: SELECT WS (STAR_SYMBOL| ((POSTSCIPRT COMMA_SYMBOL)* POSTSCIPRT?)) WS FROM WS POSTSCIPRT WHERECLAUSE? ;
//
//DELETEQUERY: DELETE WS FROM WS POSTSCIPRT WHERECLAUSE?;
//
//UPDATEQUERY: UPDATE WS POSTSCIPRT WS SET WS POSTSCIPRT WS EQ_SYMBOL WS (QUESTION_MARK | POSTSCIPRT) (COMMA_SYMBOL POSTSCIPRT WS EQ_SYMBOL WS (QUESTION_MARK | POSTSCIPRT))* WHERECLAUSE?;
//
//WHERECLAUSE: WS WHERE WS POSTSCIPRT WS EQ_SYMBOL WS (QUESTION_MARK | POSTSCIPRT) (WS AND WS POSTSCIPRT WS EQ_SYMBOL WS (QUESTION_MARK | POSTSCIPRT))*;
//
//fragment INSERT: I N S E R T;
//fragment SELECT: S E L E C T;
//fragment DELETE: D E L E T E;
//fragment UPDATE: U P D A T E;
//fragment FROM: F R O M;
//fragment INTO: I N T O;
//fragment VALUES: V A L U E S;
//fragment WHERE: W H E R E;
//fragment SET: S E T;
//QUESTION_MARK: '?';

CONTINUATION
    : CONTINUATION_SYMBOL ~[\r\n]* NEWLINE -> skip ;


WHITESPACE
    : [ \t]+ -> skip ;

/* LEXER: fragments */

fragment DIGIT : '0'..'9' ;

fragment INTEGRATIONFLOW: I N T E G R A T I O N F L O W;
fragment INBOUNDENDPOINT: I N B O U N D E N D P O I N T;
fragment HTTP: H T T P;
fragment PIPELINE: P I P E L I N E;
fragment PROCESSMESSAGE: P R O C E S S M E S S A G E;
fragment OUTBOUNDENDPOINT: O U T B O U N D E N D P O I N T ;
fragment MEDIATOR: M E D I A T O R;
fragment PROTOCOL: P R O T O C O L;
fragment PORT: P O R T;
fragment ENDPOINT: E N D P O I N T;
fragment CONTEXT: C O N T E X T;
fragment TIMEOUT: T I M E O U T;
fragment HOST: H O S T;
fragment CONFIGS: C O N F I G S;
fragment CONDITION: C O N D I T I O N;
fragment SOURCE: S O U R C E;
fragment PATTERN: P A T T E R N;
fragment PROCESS_MESSAGE: 'process_message';
fragment OUTBOUNDDATASOURCE: O U T B O U N D D A T A S O U R C E ;
fragment USERNAME: U S E R N A M E;
fragment PASSWORD: P A S S W O R D;
fragment QUERY: Q U E R Y;
fragment QUERYSTATEMENT: Q U E R Y S T A T E M E N T;
fragment QUERYPARAMETER:Q U E R Y P A R A M E T E R;
fragment RESULTSET: R E S U L T S E T;

fragment CALL: C A L L;
fragment FILTER: F I L T E R;
fragment RESPOND: R E S P O N D;
fragment LOG: L O G;
fragment ENRICH: E N R I C H;
fragment TRANSFORM: T R A N S F O R M;
fragment DATASOURCE: D A T A S O U R C E;
fragment GENERATERESPONSE: G E N E R A T E R E S P O N S E;
/* case insensitive lexer matching */
fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');

