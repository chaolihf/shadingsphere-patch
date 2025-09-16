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

import java.util.ArrayList;
import java.util.List;

import org.apache.shardingsphere.authority.config.AuthorityRuleConfiguration;
import org.apache.shardingsphere.authority.config.UserConfiguration;
import org.apache.shardingsphere.authority.distsql.statement.CreateDistUserRuleStatement;
import org.apache.shardingsphere.authority.rule.AuthorityRule;
import org.apache.shardingsphere.distsql.handler.engine.update.rdl.rule.spi.global.GlobalRuleDefinitionExecutor;
import org.apache.shardingsphere.infra.config.rule.RuleConfiguration;

import lombok.Setter;

/**
 * 创建分布式用户规则执行器
 */
@Setter
public final class CreateDistUserRuleExecutor implements GlobalRuleDefinitionExecutor<CreateDistUserRuleStatement, AuthorityRule>{

	private AuthorityRule rule;

	@Override
	public void setRule(AuthorityRule rule) {
		this.rule=rule;
	}

	@Override
	public Class<AuthorityRule> getRuleClass() {
		return AuthorityRule.class;
	}

	@Override
	public RuleConfiguration buildToBeAlteredRuleConfiguration(CreateDistUserRuleStatement sqlStatement) {
		AuthorityRuleConfiguration currentConfig = rule.getConfiguration();
		List<UserConfiguration> currentUsers = new ArrayList<>(currentConfig.getUsers());
		currentUsers.add(new UserConfiguration(sqlStatement.getUsername(),sqlStatement.getPassword(),"","",false));
		AuthorityRuleConfiguration config=new AuthorityRuleConfiguration(
				currentUsers,currentConfig.getPrivilegeProvider(),
					currentConfig.getAuthenticators(),currentConfig.getDefaultAuthenticator()
				);
		return config;
	}

	@Override
	public Class<CreateDistUserRuleStatement> getType() {
		return CreateDistUserRuleStatement.class;
	}
    
}
