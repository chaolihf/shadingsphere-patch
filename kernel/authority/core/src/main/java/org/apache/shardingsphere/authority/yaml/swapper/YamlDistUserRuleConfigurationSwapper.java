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

package org.apache.shardingsphere.authority.yaml.swapper;

import org.apache.shardingsphere.authority.config.UserConfiguration;
import org.apache.shardingsphere.authority.yaml.config.YamlDistUserRuleConfiguration;
import org.apache.shardingsphere.infra.yaml.config.swapper.rule.YamlRuleConfigurationSwapper;

/**
 * YAML用户规则配置交换
 */
public final class YamlDistUserRuleConfigurationSwapper implements YamlRuleConfigurationSwapper<YamlDistUserRuleConfiguration, UserConfiguration> {
    
    @Override
    public YamlDistUserRuleConfiguration swapToYamlConfiguration(final UserConfiguration data) {
        return new YamlDistUserRuleConfiguration();
    }
    
    @Override
    public UserConfiguration swapToObject(final YamlDistUserRuleConfiguration yamlConfig) {
        return new UserConfiguration("", "", "%", "", false);
    }
    
    @Override
    public Class<UserConfiguration> getTypeClass() {
        return UserConfiguration.class;
    }
    
    @Override
    public String getRuleTagName() {
        return "users";
    }
    
    @Override
    public int getOrder() {
        return 6000;
    }
}
