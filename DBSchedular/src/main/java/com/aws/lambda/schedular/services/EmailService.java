package com.aws.lambda.schedular.services;

import com.aws.lambda.schedular.dynamodb.pojo.DBUserInfo;

public abstract interface EmailService
    {
        public abstract boolean sendMailUsingSendGrid(DBUserInfo paramDBUserInfo);
    }
