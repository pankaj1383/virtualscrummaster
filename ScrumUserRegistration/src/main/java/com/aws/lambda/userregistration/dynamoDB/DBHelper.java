package com.aws.lambda.userregistration.dynamoDB;

import java.util.Map;

import com.aws.lambda.userregistration.dynamoDB.pojo.DBUserInfo;

/**
 * 
 * @author pankaj.lole
 *
 */
public interface DBHelper {

	public Map<String, DBUserInfo> getAllUsers() throws Exception;

	public Map<String, DBUserInfo> getActiveUsers() throws Exception;

	public DBUserInfo fetchUserByActivationKey(String activationKey) throws Exception;

	public DBUserInfo fetchUserByEmailId(String emailId) throws Exception;

	public DBUserInfo fetchUserBySocialId(String socialId) throws Exception;

	public void saveUser(DBUserInfo user) throws Exception;

	public void deleteUser(DBUserInfo user) throws Exception;

	public void deleteUserByEmail(String email) throws Exception;
}
