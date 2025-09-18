package org.apache.shardingsphere.authority.rule.privilege;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.shardingsphere.authority.model.ShardingSpherePrivileges;

public class PersistDatabasePrivileges implements ShardingSpherePrivileges {

	private Set<String> allDatabase=new ConcurrentSkipListSet<>();
	
	private String userName;
	
	public PersistDatabasePrivileges(String userName, String dbName) {
		this.userName=userName;
		allDatabase.add(dbName);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public boolean hasPrivileges(String database) {
		return allDatabase.contains(database.toLowerCase());
	}

	public void addDatabaseName(String dbName) {
		allDatabase.add(dbName);
	}

}
