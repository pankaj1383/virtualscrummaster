package com.aws.lambda.schedular.dynamodb;

import com.aws.lambda.schedular.dynamodb.pojo.DBUserInfo;
import java.util.Map;

public abstract interface DBHelper
    {
        public abstract Map<String, DBUserInfo> getAllUsers()
        throws Exception;
        
        public abstract Map<String, DBUserInfo> getActiveUsers()
        throws Exception;
        
        public abstract DBUserInfo fetchUserByActivationKey(String paramString)
        throws Exception;
        
        public abstract DBUserInfo fetchUserByEmailId(String paramString)
        throws Exception;
        
        public abstract DBUserInfo fetchUserBySocialId(String paramString)
        throws Exception;
        
        public abstract void saveUser(DBUserInfo paramDBUserInfo)
        throws Exception;
        
        public abstract void deleteUser(DBUserInfo paramDBUserInfo)
        throws Exception;
        
        public abstract void deleteUserByEmail(String paramString)
        throws Exception;
    }
