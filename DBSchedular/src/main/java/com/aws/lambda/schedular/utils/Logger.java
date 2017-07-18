package com.aws.lambda.schedular.utils;






public final class Logger
{
    private static StringBuilder eventLogs = null;
    
    
    public Logger() {}
    
    public static void log(String log)
    {
        if (eventLogs == null) {
            eventLogs = new StringBuilder();
        }
        
        if (log != null) {
            eventLogs.append("\n");
            eventLogs.append(log);
        }
    }
    
    
    
    
    public static String getLogs()
    {
        if (eventLogs != null) {
            return eventLogs.toString();
        }
        return null;
    }
    
    
    
    public static void clearLogs()
    {
        if (eventLogs != null) {
            eventLogs.delete(0, eventLogs.length());
        }
        eventLogs = null;
    }
}
