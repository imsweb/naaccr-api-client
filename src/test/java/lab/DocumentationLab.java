/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package lab;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.imsweb.naaccr.api.client.NaaccrApiClient;
import com.imsweb.naaccr.api.client.entity.NaaccrAllowedCode;
import com.imsweb.naaccr.api.client.entity.NaaccrDataItem;
import com.imsweb.seerutils.SeerUtils;

public class DocumentationLab {

    public static void main(String[] args) throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();

        NaaccrDataItem item = client.getDataItem("23", "race1");

        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        String content = SeerUtils.readFile(new File(System.getProperty("user.dir") + "/src/test/resources/naaccr-doc-template.txt"));
        String style = SeerUtils.readFile(new File(System.getProperty("user.dir") + "/src/test/resources/naaccr-doc-style.css"));

        // it would be better to use a template engine, but that will do for now.
        StringBuilder alternateNames = new StringBuilder();
        if (item.getAlternateNames() != null && !item.getAlternateNames().isEmpty()) {
            alternateNames.append("<strong>Alternate Names</strong>\n");
            for (String name : item.getAlternateNames())
                alternateNames.append("<br/>&nbsp;&nbsp;&nbsp;").append(name).append("\n");
        }

        StringBuilder allowedCodes = new StringBuilder();
        if (item.getAllowedCodes() != null && !item.getAllowedCodes().isEmpty()) {
            //if (item.getCodeHeading() != null && !item.getCodeHeading().trim().isEmpty())  // TODO FD code heading
            allowedCodes.append("<br/><br/>\n");
            allowedCodes.append("<div class='content chap10-para'><strong>Codes</strong></div>\n");
            allowedCodes.append("<table>\n");
            // TODO FD clean descriptions?
            for (NaaccrAllowedCode code : item.getAllowedCodes())
                allowedCodes.append("<tr class='code-row'><td class='code-nbr'>").append(code.getCode()).append("</td><td class='code-desc'>").append(code.getDescription()).append("</td></tr>\n");
            allowedCodes.append("</table>\n");
            //if (item.getCodeHeading() != null && !item.getCodeHeading().trim().isEmpty())  // TODO FD code notes
        }

        content = content.replace("{ITEM_NUMBER}", item.getItemNumber().toString());
        content = content.replace("{ITEM_LENGTH}", item.getItemLength().toString());
        content = content.replace("{SOURCE_OF_STANDARD}", Objects.toString(item.getSourceOfStandard(), ""));
        content = content.replace("{YEAR_IMPLEMENTED}", Objects.toString(item.getYearImplemented(), ""));
        content = content.replace("{VERSION_IMPLEMENTED}", Objects.toString(item.getVersionImplemented(), ""));
        content = content.replace("{YEAR_RETIRED}", Objects.toString(item.getYearRetired(), ""));
        content = content.replace("{VERSION_RETIRED}", Objects.toString(item.getVersionRetired(), ""));
        content = content.replace("{DATA_LEVEL}", item.getXmlParentId());
        content = content.replace("{XML_ID}", item.getXmlNaaccrId());

        content = content.replace("{ALTERNATE_NAMES}", alternateNames);
        content = content.replace("{DESCRIPTION}", cleanHtml(renderer.render(parser.parse(item.getDescription()))));
        content = content.replace("{RATIONALE}", cleanHtml(renderer.render(parser.parse(item.getRationale()))));
        content = content.replace("{ALLOWED_CODES}", allowedCodes);

        // TODO FD deal with replacing special characters in HTML... (see race1 for an example of a quote)

        StringBuilder buf = new StringBuilder();
        buf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        buf.append("\n");
        buf.append("<html>\n");
        buf.append("\n");
        buf.append("<head>\n");
        buf.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n");
        buf.append("<title>").append(item.getItemName().replace("&", "&amp;")).append("</title>\n");
        buf.append("<style>\n");
        buf.append("body { padding:5px; font-family:Tahoma; font-size: 14px; }\n");
        buf.append("h1 { font-size:14px; margin-top:0px; }\n");
        buf.append(style);
        buf.append("</style>\n");
        buf.append("</head>\n");
        buf.append("\n");
        buf.append("<body>\n");
        buf.append("\n");
        buf.append("<h1>").append(item.getItemName().replace("&", "&amp;")).append("</h1>\n");
        buf.append("\n");
        buf.append(content);
        buf.append("</body>\n");
        buf.append("</html>\n");

        SeerUtils.writeFile(buf.toString(), new File(System.getProperty("user.dir") + "/build/test.html"));
    }

    private static String cleanHtml(String html) {
        html = Pattern.compile("^<p>").matcher(html).replaceAll("");
        html = Pattern.compile("</p>$").matcher(html).replaceAll("");

        html = Pattern.compile("\\^(\\d+)\\^").matcher(html).replaceAll("<sup>$1</sup>");

        return html;
    }

}
