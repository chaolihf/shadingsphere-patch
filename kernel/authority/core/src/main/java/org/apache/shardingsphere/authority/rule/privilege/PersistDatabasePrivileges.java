package org.apache.shardingsphere.authority.rule.privilege;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.shardingsphere.authority.model.ShardingSpherePrivileges;

public class PersistDatabasePrivileges implements ShardingSpherePrivileges {

	private Set<String> allDatabase=new ConcurrentSkipListSet<>();
	
	private String userName;
	
	private boolean isAllowAll=false;
	
	public PersistDatabasePrivileges(String userName, String dbName) {
		this.userName=userName;
		addDatabaseName(dbName);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public boolean hasPrivileges(String database) {
		if (isAllowAll) {
			return true;
		}
		return allDatabase.contains(database.toLowerCase());
	}

	public void addDatabaseName(String dbName) {
		if("%".equals(dbName)) {
			isAllowAll=true;
		} else {
			allDatabase.add(dbName);
		}
	}

}
