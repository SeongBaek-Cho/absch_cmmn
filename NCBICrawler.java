package com.adms.news.cmm;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;



@Component
public class NCBICrawler extends RequestCrawler {
	
	SimpleDateFormat en = new SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH);
    SimpleDateFormat kor = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
	
	private NCBICrawler() {}
	
	public NCBICrawler(String url) {
		try {
			if (!url.isEmpty()) {
				super.document = Jsoup.parse(new URL(url).openStream(), "UTF8",url);
//				super.document.outputSettings(new Document.OutputSettings().prettyPrint(false));
				super.document_map = new HashMap<String, Elements>();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ArrayList<HashMap<String, String>> getTitleAttribute(String key) {
		ArrayList<HashMap<String,String>> array = new ArrayList<HashMap<String, String>>();
		Elements elements = super.document_map.get(key);
		if (!elements.isEmpty()) {
			for (Element element : elements) {
				System.out.println(element);
				Element title;
				Element href;
				Element date;
				try {
					title = element.getElementsByTag("header").get(0).getElementsByTag("h1").get(0);
					href = element.getElementsByTag("header").get(0).getElementsByTag("h1").get(0).getElementsByTag("a").get(0);
					date = element.getElementsByTag("footer").get(0).getElementsByTag("time").get(0);
				} catch (Exception d) {
					continue;
				}
				HashMap<String,String> map = new HashMap<String, String>();
				map.put(super.title, title.text());
				map.put(super.href, href.attr("href"));
				map.put(super.date, date.text());
				array.add(map);
			}
			return array;
		}
		return null;
	}

	

}
