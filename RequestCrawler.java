package com.adms.news.cmm;


import com.adms.news.cmm.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

@Component
public abstract class RequestCrawler {

	
	SimpleDateFormat en = new SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH);
    SimpleDateFormat kor = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
    
    Document document = null;
    HashMap<String,Elements> document_map = new HashMap<String, Elements>();

    public final String title = "title";
    public final String href = "href";
    public final String date = "date";

    public Elements setPlainText(String xpath) {
    	return this.document.select(xpath);
    }
    
    public String getPlainText(Element element) {
    	return new HtmlToPlainText().getPlainText(element);
    }
    
    public String getText(String xpath) {
        return this.document.select(xpath).isEmpty() ? "" : this.document.select(xpath).text();
    }
    public void elementsFind(String xpath, String key) {
        Elements elements = document.select(xpath);
        addElements(elements, key);
    }

    public void addElements(Elements elements, String key) {
        if (!key.isEmpty()) {
            this.document_map.put(key, elements);
        } else {
            // Exception handling required
        }
    }

    public String[] getElementsTextArray(String key) {
        if (key.isEmpty() || this.document_map.get(key).isEmpty()) {
            // Exception handling required
        } else {
            Elements elements = this.document_map.get(key);
            String[] textArray = new String[elements.size()];
            for (int i=0; i<elements.size(); i++) {
                if (!elements.get(i).text().isEmpty()) {
                    textArray[i] = elements.get(i).text();
                }
            }
            return textArray;
        }
        return null;
    }

    public String[] getElementsHrefArray(String key) {
        if (key.isEmpty() || this.document_map.get(key).isEmpty()) {
            // Exception handling required
        } else {
            Elements elements = this.document_map.get(key);
            String[] textArray = new String[elements.size()];
            for (int i=0; i<elements.size(); i++) {
                if (!elements.get(i).attr("href").isEmpty()) {
                    textArray[i] = elements.get(i).attr("href");
                }
            }
            return textArray;
        }
        return null;
    }

    public abstract ArrayList<HashMap<String,String>> getTitleAttribute(String key);
    public String getDateTranslation(String date) throws Exception {
        // EN -> yyyy-MMM-dd
        // KOR -> yyyy-MM-dd
        String[] dates = date != null && !date.equals("") ? date.replaceAll(",","").split(" ") : null;
        if (dates == null) {
            return "";
        }
        HashMap<String,String> map = new HashMap<String, String>();
        for (String s : dates) {
            if (!Character.isDigit(s.charAt(0))) {
                map.put("MONTH", s.substring(0, 1).toUpperCase() + s.substring(1, 3).toLowerCase());
                continue;
            }
            if (s.length() < 4) {
                if (s.length() == 2) {
                    map.put("DAY", s);
                    continue;
                } else if (s.length() == 1) {
                    map.put("DAY", "0"+s);
                    continue;
                }
            }
            if (s.length() == 4) {
                map.put("YEAR", s);
            }
        }
        date = map.get("YEAR")+"-"+map.get("MONTH")+"-"+map.get("DAY");
        return kor.format(en.parse(date));
    }
}
