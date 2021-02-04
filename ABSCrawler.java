

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
public class ABSCrawler extends RequestCrawler {

	SimpleDateFormat en = new SimpleDateFormat("yyyy-MMM-dd",Locale.ENGLISH);
	SimpleDateFormat kor = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
	
	private ABSCrawler() {}
	
	public ABSCrawler(String url) {
		try{
			if (!url.isEmpty()) {
				super.document = Jsoup.parse(new URL(url).openStream(), "UTF8",url);
				super.document_map = new HashMap<String, Elements>();
			}
		} catch(Exception e) {
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
				Element contents;
				try {
					title = element.getElementsByTag("h3").get(0);
					href = element.getElementsByTag("h3").get(0).getElementsByTag("a").get(0);
					date = element;
				} catch (Exception d) {
					continue;
				}
				HashMap<String,String> map = new HashMap<String, String>();
				map.put(super.title, title.text());
				map.put(super.href, href.attr("href"));
				map.put(super.date, date.text());
				System.out.println(date.text().trim().substring(0,11));
				array.add(map);
			}
			return array;
		}
		return null;
	}
	
	
}
