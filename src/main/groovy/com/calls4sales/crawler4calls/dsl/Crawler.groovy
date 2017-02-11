package com.calls4sales.crawler4calls.dsl

import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.url.WebURL
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.htmlcleaner.HtmlCleaner
import org.htmlcleaner.TagNode

/**
 * Created by Igor on 08.02.2017.
 */
class Crawler extends WebCrawler {
    private final Log log = LogFactory.getLog(getClass());
    private HtmlCleaner cleaner = new HtmlCleaner();
    @Override
    boolean shouldVisit(Page referringPage, WebURL url) {
        return Config.instance.shouldVisit(url.URL)
    }

    @Override
    void visit(Page page) {
        String url = page.getWebURL().getURL();
        if (page.getParseData() instanceof HtmlParseData
                && Config.instance.shouldParse(page, url)) {
            //println("We should parse $url")
            try {
                parsePage(page)
            } catch (Exception ex) {
                log.error("Error while parse page $url ${ex.message}", ex)
            }
        }
    }

    void parsePage(Page page) {
        HtmlParseData htmlParseData = page.getParseData() as HtmlParseData
        //String text = htmlParseData.getText();
        //System.out.println(text);
        String html = htmlParseData.getHtml();
        TagNode node = cleaner.clean(html);
        int i = 0;
        List<String> values = new ArrayList<>(Config.instance.columnConfigs.size())
        for (columnConfig in Config.instance.columnConfigs) {
            String value = null;
            try {
                if (columnConfig.visitor != null) {
                    Object[] founds = node.evaluateXPath(columnConfig.xpath)
                    if (founds.length > 0) {
                        Object found = founds[0]
                        if (found instanceof TagNode) {
                           value =  columnConfig.visitor(found)
                        }
                    }
                } else if (columnConfig.value != null) {
                    if (columnConfig.value.equals("url")) {
                        value = page.webURL.URL
                    }
                } else if (columnConfig.xpath != null) {
                    Object[] founds = node.evaluateXPath(columnConfig.xpath)
                    if (founds.length > 0) {
                        Object found = founds[0]
                        String foundString = null
                        if (found instanceof StringBuilder) {
                            foundString = ((StringBuilder) found).toString()
                        } else if (found instanceof String) {
                            foundString = ((String)found)
                        }
                        foundString = foundString.trim()
                        value = foundString
                    }
                }
            } catch (Exception ex) {
                log.error("Error while parse page $url, column ${columnConfig.name}: ${ex.message}", ex)
            }
            if (columnConfig.postprocessor != null) {
                value = columnConfig.postprocessor(value)
            }
            values.add(i++, value)
        }
        if (existSomething(values)) {
            Config.instance.dataCollector.add(values)
        }
    }

    boolean existSomething(ArrayList<String> strings) {
        for (s in strings) {
            if (s != null) return true
        }
        false
    }
}
