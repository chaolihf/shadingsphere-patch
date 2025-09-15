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

package org.apache.shardingsphere.authority.distsql.handler.query;

import org.apache.shardingsphere.authority.config.UserConfiguration;
import org.apache.shardingsphere.authority.distsql.statement.CreateDistUserRuleStatement;
import org.apache.shardingsphere.authority.rule.DistUserRule;
import org.apache.shardingsphere.distsql.handler.engine.update.rdl.rule.spi.global.GlobalRuleDefinitionExecutor;
import org.apache.shardingsphere.infra.config.rule.RuleConfiguration;

import lombok.Setter;

/**
 * 创建分布式用户规则执行器
 */
@Setter
public final class CreateDistUserRuleExecutor implements GlobalRuleDefinitionExecutor<CreateDistUserRuleStatement, DistUserRule>{

	@Override
	public void setRule(DistUserRule rule) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<DistUserRule> getRuleClass() {
		return DistUserRule.class;
	}

	@Override
	public RuleConfiguration buildToBeAlteredRuleConfiguration(CreateDistUserRuleStatement sqlStatement) {
		UserConfiguration userInfo=new UserConfiguration(
				sqlStatement.getUsername(),sqlStatement.getPassword(),null,null,false
				);
		return userInfo;
	}

	@Override
	public Class<CreateDistUserRuleStatement> getType() {
		return CreateDistUserRuleStatement.class;
	}
    
}
