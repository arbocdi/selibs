/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.utils.store;

/**
 *
 * @author root
 */
public class StringRecordParser implements RecordParser<String, String> {

    @Override
    public String toString(Record<String, String> r) throws Exception {
        return String.format("%s#%s", r.getKey(), r.getValue());
    }

    @Override
    public Record<String, String> fromString(String str) throws Exception {
        String[] data = str.split("#", -1);
        return new StringRecord(data[0], data[1]);
    }

}
