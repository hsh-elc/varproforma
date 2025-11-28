package org.proforma.variability.util;

import java.util.Formatter;
import java.util.HashMap;

import org.proforma.variability.transfer.CVList;
import org.proforma.variability.transfer.CVListVp;
import org.proforma.variability.transfer.CVVp;
import org.proforma.variability.transfer.V;
import org.proforma.variability.transfer.Vp;

public class RenderHtml {
    
    public static RenderHtml get() {
        return new RenderHtml();
    }
    private HashMap<String,String> classes;
    private HashMap<String,String> selectors;
    
    public static final String CLASS_TD_KEY= "proforma_variability_td_key";
    public static final String CLASS_TD_VALUE= "proforma_variability_td_value";
    public static final String CLASS_TH= "proforma_variability_th";
    public static final String CLASS_BORDER= "proforma_variability_border";
    public static final String CLASS_TABLE= "proforma_variability_table";
    public static final String CLASS_CELL= "proforma_variability_cell";
    public static final String CLASS_OL= "proforma_variability_ol";
    public static final String CLASS_LI_DIV= "proforma_variability_LI_DIV";

    public RenderHtml() {
        classes= new HashMap<>();
        classes.put(CLASS_TD_KEY, "vertical-align:middle; text-align:left;");
        classes.put(CLASS_TD_VALUE, "vertical-align:top; text-align:left;");
        classes.put(CLASS_TH, "vertical-align:middle; text-align:center;");
        classes.put(CLASS_BORDER, "border-style: solid; border-width: 1px; border-collapse: collapse;");
        classes.put(CLASS_TABLE, "margin: 2px;");
        classes.put(CLASS_CELL, "padding: 0px 2px 0px 2px;");
        classes.put(CLASS_OL, "margin:0px;padding-top:1px;padding-bottom:1px;padding-right:0px;");
        classes.put(CLASS_LI_DIV, "position: relative; left: -4px; display: inline-block; vertical-align:top;");
        selectors= new HashMap<>();
    }
    
    public void addToClass(String clazz, String style) {
        if (!classes.containsKey(clazz)) classes.put(clazz, style);
        else classes.put(clazz, classes.get(clazz)+style);
    }
    public void setClass(String clazz, String style) {
        classes.put(clazz, style);
    }

    public void addToSelector(String selector, String style) {
        if (!selectors.containsKey(selector)) selectors.put(selector, style);
        else selectors.put(selector, selectors.get(selector)+style);
    }

    public String getStyleElement(String ancestorId) {
        StringBuilder appender= new StringBuilder();
        appender.append("<style>\n");
        for (String clazz : classes.keySet()) {
            appender.append("#").append(ancestorId).append(" .").append(clazz).append(" {\n")
                .append(classes.get(clazz)).append("\n")
            .append("}\n");
        }
        for (String selector : selectors.keySet()) {
            appender.append("#").append(ancestorId).append(" ").append(selector).append(" {\n")
                .append(selectors.get(selector)).append("\n")
            .append("}\n");
        }
        appender.append("</style>\n");
        return appender.toString();
    }
    
    public static int calcLiItemPadLeft(int size) {
        return 10 + String.valueOf(size).length() * 6;
    }
    
    
    
    
    
    
    public static String renderHtml(V v, Vp vp) {
        Object o= v.getValue();
        if (o == null) return "(null)";
        Class<?> clazz= v.getValueType();
        if (clazz == String.class) return o.toString().replaceAll("\\R", "<br>");
        if (clazz == Boolean.class) return o.toString();
        if (clazz == Integer.class) return o.toString();
        if (clazz == Double.class) return o.toString();
        if (clazz == Character.class) return o.toString();
        if (clazz == CVList.class) {
            CVListVp cvs= new CVListVp(vp.getCVp(), ((CVList)v.getValue()).getElements());
            return renderHtml(cvs);
        }
        return null;
    }

    
    
    public static String renderHtml(CVVp cvvp) {
        try (Formatter out= new Formatter(new StringBuilder())) {
            out.format("<table class='%s %s'>%n", RenderHtml.CLASS_BORDER, RenderHtml.CLASS_TABLE);
            out.format("<thead>%n");
            out.format("<tr><th class='%1$s %2$s %3$s'>Variable</th><th class='%1$s %2$s %3$s'>Value</th></tr>%n", RenderHtml.CLASS_TH, RenderHtml.CLASS_BORDER, RenderHtml.CLASS_CELL);
            out.format("</thead>%n");
            out.format("<tbody>%n");
            for (int i=0; i<cvvp.getCVp().size(); i++) {
                out.format("<tr>%n");
                out.format("<td class='%s %s %s'>%s</td>%n", RenderHtml.CLASS_BORDER, RenderHtml.CLASS_TD_KEY, RenderHtml.CLASS_CELL, cvvp.getCVp().get(i).getKey());
                out.format("<td class='%s %s %s'>%s</td>%n", RenderHtml.CLASS_BORDER, RenderHtml.CLASS_TD_VALUE, RenderHtml.CLASS_CELL, renderHtml(cvvp.getCV().getVariants().get(i), cvvp.getCVp().get(i)));
                out.format("</tr>%n");
            }
            out.format("</tbody>%n");
            out.format("</table>%n");
            return out.toString();
        }
    }
    
    public static String renderHtml(CVListVp cvlvp) {
        StringBuilder sb= new StringBuilder();
        boolean doList= cvlvp.getList().size()>1;
        if (doList) {
            int padLeft= RenderHtml.calcLiItemPadLeft(cvlvp.getList().size());
            sb.append("<ol class='").append(RenderHtml.CLASS_OL).append("' style='padding-left:").append(padLeft).append("px;'>\n");
        }
        for (int i=0; i<cvlvp.getList().size(); i++) {
            if (doList) sb.append("<li><div class='").append(RenderHtml.CLASS_LI_DIV).append("'>\n");
            sb.append(renderHtml(cvlvp.getInst(i)));
            if (doList) sb.append("</div></li>\n");
        }
        if (doList) sb.append("</ol>\n");
        return sb.toString();
    }

}
