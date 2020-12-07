package com.adms.news.cmm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * @Class Name : Charts.java
 * @Modification Information
 * @
 * @  
 * @ ---------   ---------   -------------------------------
 * @ 2020.04     조성백              Automatic for charts class 작성
 */



public class Charts {
	
	public String cn;
	public String[] cr;
	public String[] cl;
	public String[] cv;
	
	
	/* 
	 * Convert String Arrays to a format is required for Google Charts
	 */
	public static String paramC(String test[]) {

		String hel = "";

		for (int i = 0; i < test.length; i++) {
			if (i < test.length - 1) {
				hel = hel + "'" + test[i] + "',";
			} else {
				hel = hel + "'" + test[i] + "'";
			}
		}

		System.out.println(hel);

		return hel;
	}
	
	
	/* 
	 * Convert int Arrays to a format is required for Google Charts
	 */
	public static String paramR(String[] tes) {

		String hel = "";

		for (int i = 0; i < tes.length; i++) {
			hel = hel + "," + tes[i];
		}
		System.out.println(hel);

		return hel;
	}
	
	public static String paramSet(String param) {
		
		if (param.equals("") || param.equals("null")) {
			System.out.println(param);
			param = "0";
		} else if (param.trim().equals("") || param == null) {
			System.out.println(param);
			param = "0";
		}
		
		return param;
	}
	
	/**
	 * Method	:	barCharts
	 * @category	:	Create to chart is bar type
	 * @param	cn	:	chart name
	 * @param	cr	:	chart rows
	 * @param	cl	:	column
	 * @param	cv	:	column values
	 * @conditions	CAUTION	:	[cl] and [cv] should be a one-on-one match
	 * @return	Script for chart using [Google Charts API]
	 */
	public String barCharts(String cn, String[] cr, String[] cl, String[] cv) {
		
		this.cn = cn;
		this.cr = cr;
		this.cl = cl;
		this.cv = cv;
		String script = "<script type='text/javascript' src='https://www.gstatic.com/charts/loader.js'></script>";
		script += "\n <script type='text/javascript'>"
				+ "google.charts.load('current',{'packages':['bar']});"
				+ "google.charts.setOnLoadCallback(drawChart);"
				+ "function drawChart() {"
				+ "var data = google.visualization.arrayToDataTable(["
				+ "[<%out.print(param.paramC(new String[] {'Year','Sales','Expenses','Profit'}));%>],"
				+ "[<%out.print(param.paramR('2014',new int[] {1000,400,200}));%>],"
				+ "[<%out.print(param.paramR('2015',new int[] {1170,460,250}));%>],"
				+ "[<%out.print(param.paramR('2016',new int[] {660,1120,300}));%>],"
				+ "[<%out.print(param.paramR('2017',new int[] {1231,543,1231}));%>],"
				+ "[<%out.print(param.paramR('2018',new int[] {1030,548,350}));%>]"
				+ "]);"
				+ "var options = {"
				+ "	chart: {"
				+ "		title: 'Company Performance',"
				+ "		subtitle: 'Sales, Expenses, and Profit : 2014-2018',"
				+ "	},"
				+ "	bars: 'horizontal'"
				+ "};"
				+ "var chart = new google.charts.Bar(document.getElementById('barchart_material'));"
				+ "cart.draw(data,google.charts.Bar.convertOptions(options));"
				+ "}";
		
		
		return script;
	}
	
	
	
	/**
	 * Method	:	columnCharts
	 * @category	:	Create to chart is column type
	 * @param	list	:	List<HashMap<String,String>> Select List by XML
	 * @return	Script for chart using [Google Charts API]
	 */
	public String columnCharts(List<HashMap<String,String>> list, String timeType) {

		HashMap<String,Integer> timeMap = getTimeMap(timeType);
		String valueScript = new String();

		for(int i=0; i<list.size(); i++) {
			HashMap<String,String> map = (HashMap<String, String>) list.get(i);
			String cd = String.valueOf(map.get("CREATE_DATE"));
			int wipoCnt = Integer.parseInt(paramSet(String.valueOf(map.get("WIPO_CNT"))));
			int naverCnt = Integer.parseInt(paramSet(String.valueOf(map.get("NAVER_CNT"))));
			int ncbiCnt = Integer.parseInt(paramSet(String.valueOf(map.get("NCBI_CNT"))));
			valueScript += "['"+cd+"',"+wipoCnt+","+naverCnt+","+ncbiCnt+"],";
			timeMap.remove(cd);
		}

		for (String s : timeMap.keySet()) {
			valueScript = "['" + s + "',0,0,0]," + valueScript;
		}

		String script =
				 "google.charts.load('current', {'packages':['bar']});"
				+ "google.charts.setOnLoadCallback(drawChart);"
				+ "function drawChart() {"
				+ "	var data = google.visualization.arrayToDataTable(["
				+ "['"+timeType+"','WIPO','NAVER','NCBI'],"
				+ valueScript
				+ "]);"
				+ ""
				+ "var options = {"
				+ "	chart: {"
				+ "		title: 'Company Performance',"
				+ "		subtitle: 'Sales, Expenses, and Profit: 2016-2020',"
				+ "	}"
				+ "};"
				+ ""
				+ "var chart = new google.charts.Bar(document.getElementById('columnchart_material'));"
				+ ""
				+ "chart.draw(data, google.charts.Bar.convertOptions(options));"
				+ "}";
		return script;
	}
	
	
	/**
	 * Method	:	comboCharts
	 * @category	:	Create to chart is combo type
	 * @param	list	:	List<HashMap<String,String>> Select List by XML
	 * @return	Script for chart using [Google Charts API]
	 */
	public String comboCharts(List<HashMap<String,String>> list, String timeType) {
		HashMap<String,Integer> timeMap = getTimeMap(timeType);

		String valueScript = new String();
		for(int i=0; i<list.size(); i++) {
			HashMap<String,String> map = (HashMap<String, String>) list.get(i);
			String cd = String.valueOf(map.get("CREATE_DATE"));
			int wipoCnt = Integer.parseInt(paramSet(String.valueOf(map.get("WIPO_CNT"))));
			int naverCnt = Integer.parseInt(paramSet(String.valueOf(map.get("NAVER_CNT"))));
			int ncbiCnt = Integer.parseInt(paramSet(String.valueOf(map.get("NCBI_CNT"))));
			valueScript += "['"+cd+"',"+wipoCnt+","+naverCnt+","+ncbiCnt+","+wipoCnt + naverCnt + ncbiCnt+"],";
			timeMap.remove(cd);
		}
		for (String s : timeMap.keySet()) {
			valueScript = "['" + s + "',0,0,0,0]," + valueScript;
		}
		
		String script = " google.charts.load('current', {'packages':['corechart']});"
				+ "google.charts.setOnLoadCallback(drawVisualization);"
				+ " function drawVisualization() {"
				+ "var data = google.visualization.arrayToDataTable(["
				+ "['"+timeType+"', 'WIPO', 'NAVER', 'NCBI', 'SUM'],"
				+ valueScript
				+ "]);"
				+ "var options = {"
				+ "   title : 'Crawling Data',"
				+ "   vAxis: {title: 'Paper'},"
				+ "   hAxis: {title: '"+timeType+"'},"
				+ "   seriesType: 'bars',"
				+ "   series: {3: {type: 'line'}}        };"
				+ "var chart = new google.visualization.ComboChart(document.getElementById('columnchart_material'));"
				+ "chart.draw(data, options);"
				+ "}";
		
		
		return script;
	}
	
	/**
	 * Method	:	newsChart
	 * @category	:	Create to chart is bar type
	 * @param	list	:	List<HashMap<String,String>> Select List by XML
	 * @return	Script for chart using [Google Charts API]
	 */
	public String newsChart(List<HashMap<String,String>> list) {
		String y16 = "'2016'";
		String y17 = "'2017'";
		String y18 = "'2018'";
		String y19 = "'2019'";
		String y20 = "'2020'";
		
		for (HashMap<String,String> map : list) {
			String cnt = String.valueOf(map.get("CNT"));
			String date = String.valueOf(map.get("NEWS_DATE"));
			
			if (date.equals("16")) {
				y16 += ","+cnt;
			} else if (date.equals("17")) {
				y17 += ","+cnt;
			} else if (date.equals("18")) {
				y18 += ","+cnt;
			} else if (date.equals("19")) {
				y19 += ","+cnt;
			} else if (date.equals("20")) {
				y20 += ","+cnt;
			}
			
		}
		
		
		String script = "";
		script  += "google.charts.load('current',{'packages':['bar']});"
				+ "google.charts.setOnLoadCallback(drawChart);"
				+ "function drawChart() {"
				+ "var data = google.visualization.arrayToDataTable(["
				+ "['Year','IISD','GEF','EMBL','UNDP','UNPR'],"
				+ "["+y16+"],"
				+ "["+y17+"],"
				+ "["+y18+"],"
				+ "["+y19+"],"
				+ "["+y20+"]"
				+ "]);"
				+ "var options = {"
				+ "	chart: {"
				+ "		title: 'News Data',"
				+ "		subtitle: 'News Data For ABSCH : 2016-2020',"
				+ "	},"
				+ "	bars: 'vertical'"
				+ "};"
				+ "var chart = new google.charts.Bar(document.getElementById('barchart_material'));"
				+ "chart.draw(data,google.charts.Bar.convertOptions(options));"
				+ "}";
		
		
		return script;
	}
	
	/**
	 * Method	:	newsComboChart
	 * @category	:	Create to chart is combo type
	 * @param	list	:	List<HashMap<String,String>> Select List by XML
	 * @return	Script for chart using [Google Charts API]
	 */
	public String newsComboChart(List<HashMap<String,String>> list) {

		String y16 = "'2016'";
		String y17 = "'2017'";
		String y18 = "'2018'";
		String y19 = "'2019'";
		String y20 = "'2020'";
		
		int s16 = 0;
		int s17 = 0;
		int s18 = 0;
		int s19 = 0;
		int s20 = 0;
		
		for (HashMap<String, String> map : list) {
			String date = String.valueOf(map.get("NEWS_DATE"));
			String cnt = String.valueOf(map.get("CNT"));
			
			if (date.equals("16")) {
				y16 += ","+cnt;
				s16 += Integer.parseInt(cnt);
			} else if (date.equals("17")) {
				y17 += ","+cnt;
				s17 += Integer.parseInt(cnt);
			} else if (date.equals("18")) {
				y18 += ","+cnt;
				s18 += Integer.parseInt(cnt);
			} else if (date.equals("19")) {
				y19 += ","+cnt;
				s19 += Integer.parseInt(cnt);
			} else if (date.equals("20")) {
				y20 += ","+cnt;
				s20 += Integer.parseInt(cnt);
			}
		}
		
		String script = " google.charts.load('current', {'packages':['corechart']});"
				+ "google.charts.setOnLoadCallback(drawVisualization);"
				+ " function drawVisualization() {"
				+ "var data = google.visualization.arrayToDataTable(["
				+ "['Year', 'IISD', 'GEF', 'EMBL','UNDP','UNPR', 'SUM'],"
				+ "["+y16+s16+"],"
				+ "["+y17+s17+"],"
				+ "["+y18+s18+"],"
				+ "["+y19+s19+"],"
				+ "["+y20+s20+"]"
				+ "]);"
				+ "var options = {"
				+ "   title : 'Crawling Data',"
				+ "   vAxis: {title: 'Paper'},"
				+ "   hAxis: {title: 'Year'},"
				+ "   seriesType: 'bars',"
				+ "   series: {5: {type: 'line'}}        };"
				+ "var chart = new google.visualization.ComboChart(document.getElementById('columnchart_material'));"
				+ "chart.draw(data, options);"
				+ "}";
		
		return script;
	}

	public HashMap<String, Integer> getYearMap() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.KOREAN);
		HashMap<String,Integer> timeMap = new HashMap<>();
		for (int i=0; i<5; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -i);
			timeMap.put(sdf.format(cal.getTime()),i);
		}
		return timeMap;
	}
	public HashMap<String, Integer> getMonthMap() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.KOREAN);
		HashMap<String,Integer> timeMap = new HashMap<>();
		for (int i=0; i<5; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -i);
			timeMap.put(sdf.format(cal.getTime()),i);
		}
		return timeMap;
	}

	public HashMap<String, Integer> getDayMap() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
		HashMap<String,Integer> timeMap = new HashMap<>();
		for (int i=0; i<5; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -i);
			timeMap.put(sdf.format(cal.getTime()),i);
		}
		return timeMap;
	}

	public HashMap<String, Integer> getTimeMap(String timeType) {
		if (timeType.isEmpty()) {
			return null;
		}
		if (timeType.equals("year")) {
			return getYearMap();
		} else if (timeType.equals("month")) {
			return getMonthMap();
		} else if (timeType.equals("day")) {
			return getDayMap();
		}
		return null;
	}

	public String getTimeJoinString(String timeType) {
		if (timeType != null && timeType.isEmpty()) {
			return null;
		}
		HashMap<String, Integer> map = getTimeMap(timeType);
		return map != null ? StringUtils.join(getTimeMap(timeType).keySet(),",") : null;

	}
	public String gaugeCharts(List<HashMap<String,String>> list) {
		
		return "";
	}
	
	public static String lineCharts() {
		
		return "";
	}
	
	public static String multipleCharts() {
		
		return "";
	}
	
	public static String donutCharts() {
		
		return "";
	}
	

}
