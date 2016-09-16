package net.sf.selibs.http;

//Name: value

import java.io.Serializable;

public class HHeader implements Serializable {
    public HName name;
    public String value;
    
    public String toStgring(){
        StringBuilder sb = new StringBuilder();
        sb.append(name.name);
        sb.append(": ");
        sb.append(value);
        return sb.toString();
    }
    public static HHeader fromString(String str){
        HHeader header = new HHeader();
        String data[] = str.split(":");
        header.name = new HName(data[0]);
        header.value = str.substring(data[0].length()+1).trim();
        return header;
    }
}
