package com.aws.lambda.rally;

import com.aws.lambda.rally.exception.ScrumException;
import com.aws.lambda.rally.pojo.BaseResponse;

public abstract interface IScrumTool
    {
        public abstract BaseResponse getAllRallyUsers()
        throws ScrumException;
    }
