package net.sf.selibs.http.html;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import lombok.Data;

@Data
public class HTMLPrinter {

    public static final String TABLE_BORDER = "table class=\"%s\"";

    protected String title;
    protected Field[] fields;
    protected String tableClass = "myTable";
    protected List<String> rowClasses;
    protected String style = ".myTable { \n"
            + "                width: 100%;\n"
            + "                text-align: left;\n"
            + "                background-color: lemonchiffon;\n"
            + "                border-collapse: collapse; \n"
            + "            }\n"
            + "            .myTable th { \n"
            + "                background-color: goldenrod;\n"
            + "                color: white; \n"
            + "            }\n"
            + "            .myTable td, \n"
            + "            .myTable th { \n"
            + "                padding: 10px;\n"
            + "                border: 1px solid goldenrod; \n"
            + "            }";

    public HTMLPrinter() {

    }

    public void setFields(Field... fields) {
        this.fields = fields;
    }
//    <tr>
//            <th>заголовок1</th>
//            <th>заголовок2</th>
//    </tr>        

    public StringBuilder generateHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(HTMLTags.getOpen(HTMLTags.TR));
        sb.append("\n");
        for (Field f : this.fields) {
            sb.append("\t");
            sb.append(HTMLTags.getWithBody(HTMLTags.TH, f.getName()));
        }
        sb.append(HTMLTags.getClose(HTMLTags.TR));
        return sb;
    }

    //<tr>
    //<td>data1</td>
    //<td>data2</td>
    //</tr>
    public StringBuilder generateRow(Object entity) throws IllegalArgumentException, IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        sb.append(HTMLTags.getOpen(HTMLTags.TR));
        sb.append("\n");
        for (Field f : this.fields) {
            sb.append("\t");
            Object value = f.get(entity);
            sb.append(HTMLTags.getWithBody(HTMLTags.TD, value == null ? null : value.toString()));
        }
        sb.append(HTMLTags.getClose(HTMLTags.TR));
        return sb;
    }

    public StringBuilder generateTable(List entities) throws IllegalArgumentException, IllegalAccessException {
        StringBuilder table = new StringBuilder();
        table.append(HTMLTags.getOpen(String.format(TABLE_BORDER, this.tableClass)));
        table.append("\n");
        table.append(this.generateHeader());
        for (Object entity : entities) {
            table.append(this.generateRow(entity));
        }
        table.append(HTMLTags.getClose(HTMLTags.TABLE));
        return table;
    }

    public String generateDocument(List entities) throws IllegalArgumentException, IllegalAccessException {
        return String.format(HTMLTags.DOC, this.title, this.generateTable(entities)).replaceAll("#style#", this.style);
    }

}
