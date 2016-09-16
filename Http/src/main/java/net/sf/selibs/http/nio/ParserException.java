package net.sf.selibs.http.nio;


public  class ParserException extends Exception {
        
        public ParserException(Exception ex){
            super(ex);
        }

        public ParserException(String message) {
            super(message);
        }
    }
