package com.aws.lambda.rally.exception;







public class ScrumException
extends Exception
{
    private static final long serialVersionUID = 1L;
    
    
    
    
    
    
    
    public ScrumException(String message)
    {
        super(message);
    }
    
    
    
    
    
    
    
    
    
    public ScrumException(String message, Exception innerException)
    {
        super(message, innerException);
    }
}
