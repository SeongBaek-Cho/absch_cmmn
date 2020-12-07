package com.adms.news.cmm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import com.adms.news.cmm.HtmlToPlainText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springmodules.validation.commons.DefaultBeanValidator;

import com.adms.common.log.service.AuthLogService;
import com.adms.common.site.service.SiteCodeService;
import com.adms.news.service.NewsService;
import com.bsite.account.service.LoginService;
import com.bsite.vo.NewsVO;
import com.bsite.vo.sys_parcelVO;


@Component
public class Crawler {
   
   @Autowired
   private DefaultBeanValidator beanValidator;
   
   private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

   @Resource(name = "LoginService")
   private LoginService loginService;

   @Resource(name = "AuthLogService")
   private AuthLogService authLogService;

   @Resource(name = "SiteCodeService")
   private SiteCodeService siteCodeService;

   
   @Resource (name = "NewsService")
   protected NewsService newsService;
   
   private final String NAME_SPACE = "news.";

//  @Scheduled (cron = "* * 0/5 * * *") // Dateformat N
//  @Scheduled(fixedDelay = 10000)
//  @RequestMapping(value = "/adms/news/crawler/iisd.do")
 public void iisdCrawler() throws Exception {
   System.out.print("HI");
    NewsVO vo = new NewsVO();
    List<HashMap<String, String>> list = newsService.selectNews(NAME_SPACE+"SELECT_IISD", vo);
    
    if (list.size() > 0) {
       return;
    }
    
    newsService.insertNews(NAME_SPACE+"INSERT_IISD", vo);
    
     String base = "https://www.iisd.org";
     String param = "/press?sort_by=unified_date&page=";
     int page = 0;

     long start = System.currentTimeMillis();
     long end = 0;
      try {
         boolean stop = true;
         while (stop) {
            Thread.sleep(1000);
            RequestCrawler Crawler = new IISDCrawler(base + param + page);
            Crawler.elementsFind("div.views-row > article", "empty");
            if (Crawler.getText("div.views-row").isEmpty()) {
               stop = false;
            } else {
               Crawler.elementsFind("div.views-row > article", "atag");
               String[] arg = Crawler.getElementsTextArray("atag");
               ArrayList<HashMap<String, String>> array = Crawler.getTitleAttribute("atag");
               try {
                  for (HashMap<String, String> map : array) {
                     String title = map.get(Crawler.title).equals(null) || map.get(Crawler.title).equals("") ? "Title is Null" : map.get(Crawler.title);
                     String url = base + map.get(Crawler.href);
                     String date = Crawler.getDateTranslation(map.get(Crawler.date));
                     vo.setNews_date(date);
                     vo.setTitle(title);
                     vo.setUrl(url);
                     int cnt;
                     try {
                        List<HashMap<String, String>> urlList = newsService.selectNews(NAME_SPACE+"SELECT_NEWS_CHECK", vo);
                        cnt = Integer.parseInt(String.valueOf(urlList.get(0).get("CNT")));
                        
                     } catch (Exception d ){
                        cnt = 0;
                     }
                     if (cnt > 0) {
                        end = System.currentTimeMillis();
                        vo.setElapsed_time((end-start)+"");
                        newsService.updateNews(NAME_SPACE+"UPDATE_IISD_Y", vo);
                        return;
                     }
                     Crawler = new IISDCrawler(url);
//                     String contents = Crawler.getText("div.field-items > div.field-item.even").toString();
                     try {
                        String contents = new HtmlToPlainText().getPlainText(Crawler.setPlainText("section.o-section__content.o-content-from-editor.js-content-from-editor").get(0));
                        vo.setContents(contents);
                     } catch (Exception d) {
                        String contents = "null";
                        vo.setContents(contents);
                     }
                     newsService.insertNews(NAME_SPACE+"INSERT_NEWS_IISD", vo);
                  }
               } catch (Exception e) {
                  stop = false;
                  break;
               }
            }
            page++;
         }
         end = System.currentTimeMillis();
         vo.setElapsed_time((end-start)+"");
         newsService.updateNews(NAME_SPACE+"UPDATE_IISD_Y", vo);
      } catch (Exception E) {
         newsService.updateNews(NAME_SPACE+"UPDATE_IISD", vo);
      }
     


 }
 
// @Scheduled (cron = "* * 1/5 * * *") // Dateformat N
// @Scheduled(fixedDelay = 10000)
//   @RequestMapping(value = "/adms/news/crawler/gef.do")
 public void gefCrawler() throws Exception {
    NewsVO vo = new NewsVO();
    List<HashMap<String, String>> list = newsService.selectNews(NAME_SPACE+"SELECT_GEF", vo);       
    if (list.size() > 0) {
       return;
    }
    String base = "https://www.thegef.org";
    String param = "/news/news-stories?page=";
    int page = 0;
    
    long start = System.currentTimeMillis();
     long end = 0;
    
    RequestCrawler Crawler = null;
    newsService.insertNews(NAME_SPACE+"INSERT_GEF", vo);
    boolean stop = true;
    try {
         while (stop) { // url
            Thread.sleep(1000);
            if (page == 0) {
               Crawler = new GEFCrawler(base + param);
            } else {
               Crawler = new GEFCrawler(base + param + page);
            }
            Crawler.elementsFind("div.view-content > div > div > span > div > div.highlight-text", "atag");
            ArrayList<HashMap<String, String>> array = Crawler.getTitleAttribute("atag");
            try {
               for (HashMap<String, String> map : array) {
                  String title = map.get(Crawler.title).equals(null) || map.get(Crawler.title).equals("") ? "Title is Null" : map.get(Crawler.title);
                  String url;
                  if (map.get(Crawler.href).substring(0,5).equals("/news") || map.get(Crawler.href).substring(0,5).equals("/node")) {
                     url = base + map.get(Crawler.href);
                  } else if (map.get(Crawler.href).substring(0,4).equals("http")) {
                     url = map.get(Crawler.href);
                  } else {
                     url = "ERROR";
                  }
                  String date = map.get(Crawler.date) == null ? "" : Crawler.getDateTranslation(map.get(Crawler.date));
                  vo.setNews_date(date);
                  vo.setTitle(title);
                  vo.setUrl(url);
                  int  cnt;
                  try {
                     List<HashMap<String, String>> urlList = newsService.selectNews(NAME_SPACE+"SELECT_NEWS_CHECK", vo);
                     cnt = Integer.parseInt(String.valueOf(urlList.get(0).get("CNT")));
                     
                  } catch (Exception d ){
                     cnt = 0;
                  }
                  if (cnt > 0) {
                     end = System.currentTimeMillis();
                     vo.setElapsed_time((end-start)+"");
                     newsService.updateNews(NAME_SPACE+"UPDATE_GEF_Y", vo);
                     stop = false;
                     break;
                  }
                  String contents;
                  if (!url.equals("ERROR")) {
                     Crawler = new GEFCrawler(url);
//                     contents = Crawler.getText("div.field-items > div.field-item.even").toString();
                     try {
                        contents = new HtmlToPlainText().getPlainText(Crawler.setPlainText("div.field.field-name-body.field-type-text-with-summary.field-label-hidden > div.field-items > div.field-item.even").get(0));
                     } catch (Exception d) {
                        contents = "null";
                     }
                  } else {
                     contents = "ERROR";
                  }
                  vo.setContents(contents);
                  newsService.insertNews(NAME_SPACE+"INSERT_NEWS_GEF", vo);
               }
            } catch (Exception e) {
               e.printStackTrace();
               stop = false;
            }
            page++;

         }
         end = System.currentTimeMillis();
         vo.setElapsed_time((end-start)+"");
         newsService.updateNews(NAME_SPACE+"UPDATE_GEF_Y", vo);
      } catch (Exception e) {
         e.printStackTrace();
         newsService.updateNews(NAME_SPACE+"UPDATE_GEF", vo);
      }
 
 }
  
//@Scheduled (cron = "* * 2/5 * * *") // Dacteformat N
//@Scheduled(fixedDelay = 10000)
//   @RequestMapping(value = "/adms/news/crawler/embl.do")
public void emblCrawler() throws Exception  {
   NewsVO vo = new NewsVO();
   List<HashMap<String, String>> list = newsService.selectNews(NAME_SPACE+"SELECT_EMBL", vo);       
   if (list.size() > 0) {
      return;
   }
   String base = "https://www.embl.org/news";
   String param = "/archive/page/";
   int page = 1;
   boolean stop = true;
   long start = System.currentTimeMillis();
    long end = 0;
   newsService.insertNews(NAME_SPACE+"INSERT_EMBL", vo);
      try {
         while (stop) {
            Thread.sleep(10000);
            RequestCrawler Crawler = new EMBLCrawler(base + param + page);
            String[] arg = null;
            try {
               Crawler.elementsFind("div.vf-grid.vf-grid__col-2 > div > div.vf-card__content","atag");
               arg = Crawler.getElementsTextArray("atag");
               if (arg == null) {
                  throw new Exception();
               }
            } catch (Exception e) {
               Crawler.elementsFind("div.vf-grid.vf-grid__col-3 > div > div.vf-card__content","atag");
               arg = Crawler.getElementsTextArray("atag");
            }
            
            ArrayList<HashMap<String, String>> array = Crawler.getTitleAttribute("atag");
            try {
               for (HashMap<String, String> map : array) {
                  String title = map.get(Crawler.title).equals(null) || map.get(Crawler.title).equals("") ? "Title is Null" : map.get(Crawler.title);
                  String url = map.get(Crawler.href);
                  String date = Crawler.getDateTranslation(map.get(Crawler.date));
                  vo.setNews_date(date);
                  vo.setTitle(title);
                  vo.setUrl(url);
                  int cnt;
                  try {
                     List<HashMap<String, String>> urlList = newsService.selectNews(NAME_SPACE+"SELECT_NEWS_CHECK", vo);
                     cnt = Integer.parseInt(String.valueOf(urlList.get(0).get("CNT")));
                  } catch (Exception d ){
                     cnt = 0;
                     System.out.println(d);
                  }
                  if (cnt > 0) {
                     end = System.currentTimeMillis();
                     vo.setElapsed_time((end-start)+"");
                     newsService.updateNews(NAME_SPACE+"UPDATE_EMBL_Y", vo);
                     stop = false;
                     System.out.println("This is c");
                     break;
                  }
                  Crawler = new EMBLCrawler(url);
                  Thread.sleep(5000);
//                  String contents = Crawler.getText("div.vf-u-padding__bottom--xxl").toString();;
                  String contents = new HtmlToPlainText().getPlainText(Crawler.setPlainText("div.vf-content").get(0));
                  vo.setContents(contents);
                  System.out.println("This is d");
                  newsService.insertNews(NAME_SPACE+"INSERT_NEWS_EMBL", vo);
               }
            } catch (Exception E) {
               System.out.println("This is e");
               stop = false;
               System.out.println("error : " + E.getMessage());
               newsService.updateNews(NAME_SPACE+"UPDATE_EMBL", vo);
            }
            page++;
         }
         end = System.currentTimeMillis();
         vo.setElapsed_time((end-start)+"");
         newsService.updateNews(NAME_SPACE+"UPDATE_EMBL_Y", vo);
      } catch (Exception e) {
         newsService.updateNews(NAME_SPACE+"UPDATE_EMBL", vo);
      }
   
}
   
//@Scheduled (cron = "* * 4/5 * * *") // Dateformat N
//@Scheduled(fixedDelay = 10000)
//   @RequestMapping(value = "/adms/news/crawler/undp.do")
public void undpCrawler() throws Exception {
   NewsVO vo = new NewsVO();
   List<HashMap<String, String>> list = newsService.selectNews(NAME_SPACE+"SELECT_UNDP", vo);       
   if (list.size() > 0) {
      return;
   }
   String base = "https://www.undp.org/";
   
   System.out.println("Check My Style");
   int page = 0; // is offset
   
   long start = System.currentTimeMillis();
   long end = 0;
   newsService.insertNews(NAME_SPACE+"INSERT_UNDP", vo);
   boolean stop = true;
      try {
         while (stop) {
            Thread.sleep(1000);
            String param = "/content/undp/en/home/news-centre.html?s="+page+"&c=100&p=";
            RequestCrawler Crawler = new UNDPCrawler(base + param);
            page = page+100;
            Crawler.elementsFind("div.media-object-section.main-section", "atag");
            String[] arg = Crawler.getElementsTextArray("atag");
            ArrayList<HashMap<String, String>> array = Crawler.getTitleAttribute("atag");
            try {
               for (HashMap<String, String> map : array) {
                  String title = map.get(Crawler.title).equals(null) || map.get(Crawler.title).equals("") ? "Title is Null" : map.get(Crawler.title);
                  String url = base + map.get(Crawler.href).toString();
                  String[] da = map.get(Crawler.date).split(" ");
                  String date = Crawler.getDateTranslation((da[2]+" "+da[3]+" "+da[4]).trim());
                  vo.setTitle(title);
                  vo.setUrl(url);
                  vo.setNews_date(date);
                  int cnt;
                  try {
                     List<HashMap<String, String>> urlList = newsService.selectNews(NAME_SPACE+"SELECT_NEWS_CHECK", vo);
                     cnt = Integer.parseInt(String.valueOf(urlList.get(0).get("CNT")));
                     
                  } catch (Exception d ){
                     cnt = 0;
                  }
                  if (cnt > 0) {
                     end = System.currentTimeMillis();
                     vo.setElapsed_time((end-start)+"");
                     newsService.updateNews(NAME_SPACE+"UPDATE_UNDP_Y", vo);
                     stop = false;
                     break;
                  }
                  Crawler = new UNPRCrawler(url);
//                  String contents = Crawler.getText("div.parbase.section.text");
                  String contents;
                  try {
                     if (Crawler.setPlainText("div.section.article").size() > 0){
                        System.out.println("Special Case");
                        contents = new HtmlToPlainText().getPlainText(Crawler.setPlainText("div.section.article").get(0));
                     }
                     else {
                        System.out.println("Really Normal");
                        contents = new HtmlToPlainText().getPlainText(Crawler.setPlainText("div.parbase.section.text").get(0));
                     }
                  } catch (Exception d) {
                     contents = "null";
                  }
                  vo.setContents(contents);
                  newsService.insertNews(NAME_SPACE+"INSERT_NEWS_UNDP", vo);
               }
            } catch (Exception E) {
               stop = false;
               E.printStackTrace();
            }
         }
         end = System.currentTimeMillis();
         vo.setElapsed_time((end-start)+"");
         newsService.updateNews(NAME_SPACE+"UPDATE_UNDP_Y", vo);
      } catch (Exception e) {
         e.printStackTrace();
         newsService.updateNews(NAME_SPACE+"UPDATE_UNDP", vo);

      }
   
}
 


//  @Scheduled (cron = "* * 3/5 * * *") // Dateformat N
// @Scheduled(fixedDelay = 10000)
//   @RequestMapping(value = "/adms/news/crawler/unpr.do")
 public void unprCrawler() throws Exception {
    NewsVO vo = new NewsVO();
    List<HashMap<String, String>> list = newsService.selectNews(NAME_SPACE+"SELECT_UNPR", vo);       
    if (list.size() > 0) {
       return;
    }
    String base = "https://www.uniprot.org";
    boolean stop = true;
    
    long start = System.currentTimeMillis();
     long end = 0;
    newsService.insertNews(NAME_SPACE+"INSERT_UNPR", vo);
    int page = 0; // is offset
      try {
         while (stop) {
            String param = "/news/?query=*&columns=title&offset="+page+"&sort=created";
            Thread.sleep(1000);
            RequestCrawler Crawler = new UNPRCrawler(base + param);
            page = page + 25;
            Crawler.elementsFind("div.main > div.content.results > div > table.grid > tbody > tr > td", "atag");
            String[] arg = Crawler.getElementsTextArray("atag");
            ArrayList<HashMap<String, String>> array = Crawler.getTitleAttribute("atag");
            try {
               for (HashMap<String, String> map : array) {
                  String title = map.get(Crawler.title).equals(null) || map.get(Crawler.title).equals("") ? "Title is Null" : map.get(Crawler.title);
                  String url = base + map.get(Crawler.href);
                  String date = map.get(Crawler.date);
                  vo.setNews_date(date);
                  vo.setTitle(title);
                  vo.setUrl(url);
                  int cnt;
                  try {
                     List<HashMap<String, String>> urlList = newsService.selectNews(NAME_SPACE+"SELECT_NEWS_CHECK", vo);
                     cnt = Integer.parseInt(String.valueOf(urlList.get(0).get("CNT")));
                     
                  } catch (Exception d ){
                     cnt = 0;
                  }
                  if (cnt > 0) {
                     end = System.currentTimeMillis();
                     vo.setElapsed_time((end-start)+"");
                     newsService.updateNews(NAME_SPACE+"UPDATE_UNPR_Y", vo);
                     stop = false;
                     break;
                  }
                  Crawler = new UNPRCrawler(url);
//                  String contents = Crawler.getText("div.helpPages").equals(null) ? "Data is null" : Crawler.getText("div.helpPages").toString();
                  String contents = new HtmlToPlainText().getPlainText(Crawler.setPlainText("div.helpPages").get(0));
                  vo.setContents(contents);
                  newsService.insertNews(NAME_SPACE+"INSERT_NEWS_UNPR", vo);
               }
            } catch (Exception E) {
            }
         }
         end = System.currentTimeMillis();
         vo.setElapsed_time((end-start)+"");
         newsService.updateNews(NAME_SPACE+"UPDATE_UNPR_Y", vo);
      } catch (Exception e) {
         newsService.updateNews(NAME_SPACE+"UPDATE_UNPR", vo);

      }
    
 }
   

//@Scheduled(fixedDelay = 10000)
//   @RequestMapping(value = "/adms/news/crawler/ncbi.do")
   public void ncbiCrawler() throws Exception {
      NewsVO vo = new NewsVO();
    List<HashMap<String, String>> list = newsService.selectNews(NAME_SPACE+"SELECT_NCBI", vo);       
    if (list.size() > 0) {
       return;
    }
    String base = "https://ncbiinsights.ncbi.nlm.nih.gov/page/";
    boolean stop = true;
//     
    long start = System.currentTimeMillis();
    long end = 0;
    newsService.insertNews(NAME_SPACE+"INSERT_NCBI", vo);
    int page = 1;
    try {
       while (stop) {
          Thread.sleep(1000);
          String param = base+page;
          RequestCrawler crawler = new NCBICrawler(param+"/");
          crawler.elementsFind("div.content-area > main > article", "atag");
          String[] arg = crawler.getElementsTextArray("atag");
          ArrayList<HashMap<String,String>> array = crawler.getTitleAttribute("atag");
          try {
             for (HashMap<String,String> map : array) {
                String title = map.get("title");
                String href = map.get("href");
                String date = crawler.getDateTranslation(map.get("date"));
                vo.setNews_date(date);
                  vo.setTitle(title);
                  vo.setUrl(href);
                  int cnt;
                  try {
                     List<HashMap<String, String>> urlList = newsService.selectNews(NAME_SPACE+"SELECT_NEWS_CHECK", vo);
                     cnt = Integer.parseInt(String.valueOf(urlList.get(0).get("CNT")));
                     
                  } catch (Exception d ){
                     cnt = 0;
                  }
//                  int cnt = Integer.parseInt(String.valueOf(urlList.get(0).get("CNT")));
                  if (cnt > 0) {
                     end = System.currentTimeMillis();
                     vo.setElapsed_time((end-start)+"");
                     newsService.updateNews(NAME_SPACE+"UPDATE_NCBI_Y", vo);
                     stop = false;
                     break;
                  }
                
                crawler = new NCBICrawler(href);
                String contents = new HtmlToPlainText().getPlainText(crawler.setPlainText("div.entry-content").get(0));
                vo.setContents(contents);
                newsService.insertNews(NAME_SPACE+"INSERT_NEWS_NCBI", vo);
                
             } 
          } catch (Exception E) {
          }
          page++;
       }
       end = System.currentTimeMillis();
         vo.setElapsed_time((end-start)+"");
         newsService.updateNews(NAME_SPACE+"UPDATE_NCBI_Y", vo);
    } catch (Exception e) {
       e.printStackTrace();
         newsService.updateNews(NAME_SPACE+"UPDATE_NCBI", vo);
    }
   }

//   @Scheduled(fixedDelay = 10000)
//   @RequestMapping(value = "/adms/news/crawler/abs.do")
   public void absCrawler() throws Exception {
      NewsVO vo = new NewsVO();
    List<HashMap<String, String>> list = newsService.selectNews(NAME_SPACE+"SELECT_ABS", vo);       
    if (list.size() > 0) {
       return;
    }
    String base = "https://abs-sustainabledevelopment.net/story/?wpv_aux_current_post_id=3538&wpv_aux_parent_post_id=3538&wpv_view_count=343-TCPID3538&wpv_paged=";
    boolean stop = true;
    
    long start = System.currentTimeMillis();
    long end = 0;
    newsService.insertNews(NAME_SPACE+"INSERT_ABS", vo);
    int page = 1;
    try {
       while (stop) {
          Thread.sleep(1000);
          String param = base+page;
          RequestCrawler crawler = new ABSCrawler(param+"/");
          crawler.elementsFind("div.story-block", "atag");
          String[] arg = crawler.getElementsTextArray("atag");
          ArrayList<HashMap<String,String>> array = crawler.getTitleAttribute("atag");
          try {
             for (HashMap<String,String> map : array) {
                String title = map.get("title");
                String href = map.get("href");
                
                
                int cnt;
                  vo.setTitle(title);
                  vo.setUrl(href);
                  try {
                     List<HashMap<String, String>> urlList = newsService.selectNews(NAME_SPACE+"SELECT_NEWS_CHECK", vo);
                     cnt = Integer.parseInt(String.valueOf(urlList.get(0).get("CNT")));
                     
                  } catch (Exception d ){
                     cnt = 0;
                  }
                  if (cnt > 0) {
                     end = System.currentTimeMillis();
                     vo.setElapsed_time((end-start)+"");
                     newsService.updateNews(NAME_SPACE+"UPDATE_ABS_Y", vo);
                     stop = false;
                     break;
                  }
                
                
                crawler = new ABSCrawler(href);
                crawler.elementsFind("div.entry-content.clear > div.container > div.row.top20 > div.col-sm-9 > div.breadcrumb > h3", "ptag");
//                 String[] content = crawler.getElementsTextArray("ptag");
                String contents = new HtmlToPlainText().getPlainText(crawler.setPlainText("div.entry-content.clear > div.container > div.row > div.col-sm-8").get(0));
                String date = crawler.getDateTranslation(crawler.getText("div.entry-content.clear > div.container > div.row.top20 > div.col-sm-9 > h3"));
                vo.setNews_date(date);
//                 String contents = crawler.getText("div#main > div#primary > div#content > article > div.entry-content");
//                 String cont = null;
//                 for (int i=0; i<content.length; i++) {
//                    cont += content[i]+"\n";
//                 }
                vo.setContents(contents);
                
                newsService.insertNews(NAME_SPACE+"INSERT_NEWS_ABS", vo);
//                 log.to_log(contents.substring(0,50)); //test
                
             } 
          } catch (Exception E) {
             E.printStackTrace();
               stop = false;
          }
          page++;
       }
       end = System.currentTimeMillis();
         vo.setElapsed_time((end-start)+"");
         newsService.updateNews(NAME_SPACE+"UPDATE_ABS_Y", vo);
    } catch (Exception e) {
       e.printStackTrace();
      newsService.updateNews(NAME_SPACE+"UPDATE_ABS", vo);
    }
   }
   

   
}