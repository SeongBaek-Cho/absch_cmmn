package com.adms.news.cmm;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;


@Component
public class IISDCrawler extends RequestCrawler {

    private IISDCrawler () {}
    public IISDCrawler (String url) {
    	try {
            if (!url.isEmpty()) {
                // Setting required
//                super.document = Jsoup.parse(new URL(url).openStream(), "UTF8", url);
            	super.document = Jsoup.connect(url).userAgent("Mozilla").get();
                super.document_map = new HashMap<String, Elements>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<HashMap<String,String>> getTitleAttribute(String key) { //title attr
        ArrayList<HashMap<String,String>> array = new ArrayList<HashMap<String, String>>();
        Elements elements = super.document_map.get(key);
        if (!elements.isEmpty()) {
            for (Element element : elements) {
                Element title;
                Element href;
                Element date;
                try {
                    title = element.getElementsByTag("h3").get(0);
                    href = element.getElementsByTag("h3").get(0).getElementsByTag("a").get(0);
                    date = element.getElementsByTag("small").get(0).getElementsByTag("span").get(1);
                } catch (Exception d ) {
                    continue;
                }
                HashMap<String,String> map = new HashMap<String, String>();
                map.put(super.title, title.text()); // role super title, href
                map.put(super.href, href.attr("href")); // extends href
                map.put(super.date, date.text());
                array.add(map);
            }
            return array;
        }
        return null;
    }


}
