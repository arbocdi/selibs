
package com.megacom.ashevelev.utils;

public interface ConfigUtilI {
    String LOGS="log";
    String CONFIG="config";
    String DOC="doc";
    
    public void init() throws Exception;
    public void generateConfigFiles() throws Exception;
    
}
