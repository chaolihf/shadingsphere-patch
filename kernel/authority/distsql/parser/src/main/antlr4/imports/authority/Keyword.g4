/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

lexer grammar Keyword;

import Alphabet,Symbol;

WS
    : [ \t\r\n] + ->skip
    ;

SHOW
    : S H O W
    ;

RULE
    : R U L E
    ;

AUTHORITY
    : A U T H O R I T Y
    ;

CREATE
    : C R E A T E
    ;

USER
    : U S E R
    ;

IDENTIFIED
    : I D E N T I F I E D
    ;
    
BY
    : B Y
    ;
    

DIST
	: D I S T
	;
	
IDENTIFIER_
    : [A-Za-z_$0-9]*?[A-Za-z_$]+?[A-Za-z_$0-9]*
    | BQ_ ~'`'+ BQ_
    ;
    