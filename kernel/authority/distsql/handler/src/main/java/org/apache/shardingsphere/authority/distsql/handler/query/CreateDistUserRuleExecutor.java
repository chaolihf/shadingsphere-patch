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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static final Logger LOGGER = Logger.getLogger(CreateDistUserRuleExecutor.class.getName());
	
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
		AuthorityRuleConfiguration config=null;
		AuthorityRuleConfiguration currentConfig = rule.getConfiguration();
		if (currentConfig.getPrivilegeProvider().getType().equals(AuthorityRule.PERSIST_DATABASE_PERMITTED)) {
			try(Connection dbConnection=AuthorityRule.getConnection()) {
				saveUserInfo(dbConnection,sqlStatement);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE,e.getMessage(),e);
				throw new RuntimeException(e);
			}
			
			config=new AuthorityRuleConfiguration(
					currentConfig.getUsers(),currentConfig.getPrivilegeProvider(),
						currentConfig.getAuthenticators(),currentConfig.getDefaultAuthenticator());
		} else {
			List<UserConfiguration> currentUsers = new ArrayList<>(currentConfig.getUsers());
			currentUsers.add(new UserConfiguration(sqlStatement.getUsername(),sqlStatement.getPassword(),"","",false));
			config=new AuthorityRuleConfiguration(
					currentUsers,currentConfig.getPrivilegeProvider(),
						currentConfig.getAuthenticators(),currentConfig.getDefaultAuthenticator()
					);
			
		}
		return config;
	}

	private void saveUserInfo(Connection dbConnection, CreateDistUserRuleStatement sqlStatement) throws SQLException {
		PreparedStatement dbCommand=null;
		try {
			dbCommand = dbConnection.prepareStatement("insert into users(username,password,status,admin,updatetime) values(?,?,1,0,now())");
			dbCommand.setString(1, sqlStatement.getUsername());
			dbCommand.setString(2, sqlStatement.getPassword());
			dbCommand.execute();
			dbCommand = dbConnection.prepareStatement("insert into user_hosts(username,hostname,status,updatetime) values(?,'%',1,now())");
			dbCommand.setString(1, sqlStatement.getUsername());
			dbCommand.execute();
			dbCommand = dbConnection.prepareStatement("insert into user_permits(username,dbname,status,updatetime) values(?,'%',1,now())");
			dbCommand.setString(1, sqlStatement.getUsername());
			dbCommand.execute();
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			if (dbCommand!=null) {
				dbCommand.close();
			}
		}
	}

	@Override
	public Class<CreateDistUserRuleStatement> getType() {
		return CreateDistUserRuleStatement.class;
	}
    
}
