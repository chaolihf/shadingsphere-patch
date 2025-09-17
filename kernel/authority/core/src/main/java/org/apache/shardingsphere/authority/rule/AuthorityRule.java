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

package org.apache.shardingsphere.authority.rule;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.shardingsphere.authority.config.AuthorityRuleConfiguration;
import org.apache.shardingsphere.authority.constant.AuthorityOrder;
import org.apache.shardingsphere.authority.model.ShardingSpherePrivileges;
import org.apache.shardingsphere.authority.spi.PrivilegeProvider;
import org.apache.shardingsphere.infra.annotation.HighFrequencyInvocation;
import org.apache.shardingsphere.infra.metadata.user.Grantee;
import org.apache.shardingsphere.infra.metadata.user.ShardingSphereUser;
import org.apache.shardingsphere.infra.rule.scope.GlobalRule;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.infra.util.yaml.YamlConfiguration;
import org.apache.shardingsphere.infra.util.yaml.YamlEngine;
import org.apache.shardingsphere.infra.yaml.config.pojo.mode.YamlModeConfiguration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;
import lombok.SneakyThrows;

/**
 * Authority rule.
 */
public final class AuthorityRule implements GlobalRule {
    
	private static final Logger LOGGER = Logger.getLogger(AuthorityRule.class.getName());
	
    private static final String GLOBAL_CONFIG_FILE = "global.yaml";
    
    @Getter
    private final AuthorityRuleConfiguration configuration;
    
    private final Map<ShardingSphereUser, ShardingSpherePrivileges> privileges;
    
    public static HikariDataSource dataSource ;
    
    @SneakyThrows(URISyntaxException.class)
    private static File getResourceFile(final String path) throws ClassNotFoundException {
        URL url = Class.forName("org.apache.shardingsphere.proxy.backend.config.ProxyConfigurationLoader").getResource(path);
        return null == url ? new File(path) : new File(url.toURI().getPath());
    }
    
	public AuthorityRule(final AuthorityRuleConfiguration ruleConfig) {
		configuration = ruleConfig;
		if ("PERSIST_DATABASE_PERMITTED".equals(ruleConfig.getPrivilegeProvider().getType())){
			if (dataSource==null) {
	    		initDataSource();
	    	}
			privileges=initPrivileges();
		} else {
	    	Collection<ShardingSphereUser> users = ruleConfig.getUsers().stream()
	                .map(each -> new ShardingSphereUser(each.getUsername(), each.getPassword(), each.getHostname(), each.getAuthenticationMethodName(), each.isAdmin())).collect(Collectors.toList());
	        privileges = users.stream().collect(Collectors.toMap(each -> each,
	                each -> TypedSPILoader.getService(PrivilegeProvider.class, ruleConfig.getPrivilegeProvider().getType(), ruleConfig.getPrivilegeProvider().getProps())
	                        .build(ruleConfig, each.getGrantee()),
	                (oldValue, currentValue) -> oldValue, LinkedHashMap::new));
		}
    }

    private Map<ShardingSphereUser, ShardingSpherePrivileges> initPrivileges() {
    	Map<ShardingSphereUser, ShardingSpherePrivileges> result=new HashMap<>();
    	try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
    		statement.executeQuery("select * from users where state=1");
    		//ShardingSphereUser user=new ShardingSphereUser();
    		//ShardingSpherePrivileges privileges=new PersistDatabasePrivileges();
    		//result.put(user, privileges);
        } catch (SQLException e) {
        	LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
    	return result;
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initDataSource()  {
    	try {
    		Class configClass=Class.forName("org.apache.shardingsphere.proxy.backend.config.yaml.YamlProxyServerConfiguration");
    		YamlConfiguration config = YamlEngine.unmarshal(getResourceFile(String.join("/", "/conf/", GLOBAL_CONFIG_FILE)),configClass);
			Field modeField = configClass.getDeclaredField("mode");
			modeField.setAccessible(true);
			YamlModeConfiguration modeConfiguration = (YamlModeConfiguration) modeField.get(config);
			Properties props = modeConfiguration.getRepository().getProps();
			HikariConfig dataSourceConfig=new HikariConfig();
			dataSourceConfig.setDriverClassName(props.getProperty("driverClassName"));
			dataSourceConfig.setJdbcUrl(props.getProperty("jdbc_url"));
	        dataSourceConfig.setUsername(props.getProperty("username"));
	        dataSourceConfig.setPassword(props.getProperty("password"));
			dataSource = new HikariDataSource(dataSourceConfig);
			initPersistUserTable();
    	} catch (ClassNotFoundException | IOException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
    		LOGGER.log(Level.SEVERE,e.getMessage(), e);
		}
	}



	private void initPersistUserTable() {
		try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
    		statement.execute("");
        } catch (SQLException e) {
        	LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		
	}

	/**
     * Get authenticator type.
     *
     * @param user user
     * @return authenticator type
     */
    public String getAuthenticatorType(final ShardingSphereUser user) {
        if (configuration.getAuthenticators().containsKey(user.getAuthenticationMethodName())) {
            return configuration.getAuthenticators().get(user.getAuthenticationMethodName()).getType();
        }
        if (configuration.getAuthenticators().containsKey(configuration.getDefaultAuthenticator())) {
            return configuration.getAuthenticators().get(configuration.getDefaultAuthenticator()).getType();
        }
        return "";
    }
    
    /**
     * Get grantees.
     *
     * @return grantees
     */
    public Collection<Grantee> getGrantees() {
        return privileges.keySet().stream().map(ShardingSphereUser::getGrantee).collect(Collectors.toList());
    }
    
    /**
     * Find user.
     *
     * @param grantee grantee user
     * @return found user
     */
    @HighFrequencyInvocation
    public Optional<ShardingSphereUser> findUser(final Grantee grantee) {
        for (ShardingSphereUser each : privileges.keySet()) {
            if (each.getGrantee().accept(grantee)) {
                return Optional.of(each);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Find privileges.
     *
     * @param grantee grantee
     * @return found privileges
     */
    @HighFrequencyInvocation
    public Optional<ShardingSpherePrivileges> findPrivileges(final Grantee grantee) {
        for (ShardingSphereUser each : privileges.keySet()) {
            if (each.getGrantee().accept(grantee)) {
                return Optional.of(each).map(privileges::get);
            }
        }
        return Optional.empty();
    }
    
    @Override
    public int getOrder() {
        return AuthorityOrder.ORDER;
    }
}
