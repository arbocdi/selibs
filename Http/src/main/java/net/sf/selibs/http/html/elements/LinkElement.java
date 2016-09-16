package net.sf.selibs.http.html.elements;

import lombok.Setter;


public class LinkElement extends StringElement{
    @Setter
    protected String url;
    
    public LinkElement (String str,String url){
        super(str);
        this.url=url;
    }
    public String getURL(){
        return url;
    }
   
}
