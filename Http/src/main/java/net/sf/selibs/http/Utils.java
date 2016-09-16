package net.sf.selibs.http;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import net.sf.selibs.http.constants.HNames;
import net.sf.selibs.http.constants.HValues;
import net.sf.selibs.http.io.ChunkedInputStream;

public class Utils {

    public static String removeWhiteSpaces(String str) {
        String result = str;
        while (result.startsWith("\t") || result.startsWith(" ")) {
            result = result.replaceFirst("\t", "").trim();
        }
        return result;
    }

    public static Reader getReader(HMessage msg) throws Exception {
        Reader reader;
        //if chunked encoding
        if (msg.getHeaderValue(HNames.TRANSFER_ENCODING) != null
                && msg.getHeaderValue(HNames.TRANSFER_ENCODING).contains(HValues.CHUNKED)) {
            reader = new InputStreamReader(new ChunkedInputStream(new ByteArrayInputStream(msg.payload)), "UTF-8");

        } else {
            reader = new InputStreamReader(new ByteArrayInputStream(msg.payload), "UTF-8");
        }
        return reader;
    }
   
}
