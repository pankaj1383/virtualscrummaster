package com.aws.lambda.schedular.dynamodb.pojo;

public abstract interface DBUser
    {
        public abstract String getEmail();
        
        public abstract void setEmail(String paramString);
        
        public abstract String getName();
        
        public abstract void setName(String paramString);
        
        public abstract String getActivationKey();
        
        public abstract void setActivationKey(String paramString);
        
        public abstract String getRallyAPIKey();
        
        public abstract void setRallyAPIKey(String paramString);
        
        public abstract String getSocialId();
        
        public abstract void setSocialId(String paramString);
        
        public abstract String getUserStatus();
        
        public abstract void setUserStatus(String paramString);
        
        public abstract boolean isActiveUser();
    }
