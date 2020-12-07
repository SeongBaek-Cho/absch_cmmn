package com.adms.news.cmm;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;


@Component
public class GEFCrawler extends RequestCrawler {

	
	private GEFCrawler() {}
	
	public GEFCrawler(String url) {
		try {
			if (!url.isEmpty()) {
//				super.document = Jsoup.parse(new URL(url).openStream(), "UTF8",url);
				super.document = Jsoup.connect(url).userAgent("Mozilla").get();
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
//				System.out.println(element);
				Element title;
				Element href;
				Element date;
				try {
					title = element.getElementsByTag("h4").get(0);
					href = element.getElementsByTag("h4").get(0);
					date = element.getElementsByTag("p").get(0);
				} catch (Exception d) {
					System.out.println("Continue");
					continue;
				}
				HashMap<String,String> map = new HashMap<String, String>();
				map.put(super.title, title.getElementsByTag("a").text());
				map.put(super.href, href.getElementsByTag("a").attr("href"));
				map.put(super.date, date.text());
				array.add(map);
			}
			return array;
		}
		return null;
	}

	

}
