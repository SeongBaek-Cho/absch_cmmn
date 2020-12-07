package com.adms.patent.cmmn;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springmodules.validation.commons.DefaultBeanValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adms.common.log.service.AuthLogService;
import com.adms.common.site.service.SiteCodeService;
import com.adms.patent.service.PatentService;
import com.bsite.account.service.LoginService;
import com.bsite.vo.PatentVO;
import com.bsite.vo.Patent_designatedVO;
import com.bsite.vo.Patent_familyVO;
import com.bsite.vo.Patent_imageVO;
import com.bsite.vo.Patent_ipcVO;
import com.bsite.vo.Patent_legalVO;
import com.bsite.vo.Patent_personVO;
import com.bsite.vo.Patent_priordocumentVO;
import com.bsite.vo.Patent_priorityVO;


@Component
public class KiprisParser {
	
	@Autowired
	private DefaultBeanValidator beanValidator;
	private static final Logger logger = LoggerFactory.getLogger(KiprisParser.class);
	
	@Resource(name = "LoginService")
	private LoginService loginService;

	@Resource(name = "AuthLogService")
	private AuthLogService authLogService;

	@Resource(name = "SiteCodeService")
	private SiteCodeService siteCodeService;

	@Resource (name = "PatentService")
	private PatentService patentService;
	
	
	
//	private final static String Servicekey = "A/u7iG0SdXDncO68VJNUBO3UUGN2MJ6S3NuEoAPQahc=";
	private final static String Servicekey = "Uwsq9QuL9unbs/kiXVJb3RvPs6cB49=LKMhIIt4e8uU=";
	private final String NAME_SPACE = "patent.";
	
	public void parser(String keyword, @ModelAttribute("searchVO") PatentVO searchVO) throws Exception {
		
		// 해외특허
//		String base = "http://plus.kipris.or.kr/openapi/rest/ForeignPatentAdvencedSearchService/freeSearch?";
//		String parameter = "free=sequence&collectionValues=US&accessKey="+Servicekey;
//		String[] nationCode = {"US","EP","WO","JP","","","",""};
		
		// 국내특허 application number 추출
		String base = "http://plus.kipris.or.kr/openapi/rest/patUtiModInfoSearchSevice/freeSearchInfo?";
		String parameter = "word="+keyword+"&patent=true&utility=false&docsStart=2&docsCount=1&lastvalue=&accessKey=" + Servicekey;
		
		String strurl = base + parameter;
		System.out.println(strurl);
		
		ArrayList<String> appList = new ArrayList<String>();
		Document doc = parseXML(strurl);
		NodeList nList = doc.getElementsByTagName("PatentUtilityInfo");
		for (int i=0; i<nList.getLength(); i++) {
			Element element = (Element) nList.item(i);
			String app = element.getElementsByTagName("ApplicationNumber").item(0).getTextContent();
			appList.add(app);
		}
		System.out.println(appList);
		searchVO.setKeyword(keyword);
		try {
			System.out.println("KiprisParser"+searchVO.getKeyword());
			System.out.println(patentService);
			patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_KEYWORD", searchVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int keyword_id = searchVO.getKeyword_id();
		// 국내특허 서지정보 추출
		String master = "http://plus.kipris.or.kr/kipo-api/kipi/patUtiModInfoSearchSevice/getBibliographyDetailInfoSearch?";
		for (int i=0; i<appList.size(); i++) {
			parameter = "applicationNumber="+appList.get(i)+"&ServiceKey="+Servicekey;
			strurl = master + parameter;
			System.out.println(strurl);
			doc = parseXML(strurl);
			
			String applicationDate = "";
			String applicationFlag = "";
			String applicationNumber = "";
			String finalDisposal = "";
			String inventionTitle = "";
			String inventionTitleEng = "";
			String openDate = "";
			String openNumber = "";
			String originalApplicationDate = "";
			String originalApplicationKind = "";
			String originalApplicationNumber = "";
			String originalExaminationRequestDate = "";
			String originalExaminationRequestFlag = "";
			String publicationDate = "";
			String publicationNumber = "";
			String registerDate = "";
			String registerNumber = "";
			String registerStatus = "";
			String translationSubmitDate = "";
			
			// getBiblioList
			applicationDate = getString(doc,"applicationDate"); searchVO.setApplication_date(applicationDate);
			applicationFlag = getString(doc,"applicationFlag"); searchVO.setApplication_flag(applicationFlag);
			applicationNumber = getString(doc,"applicationNumber"); searchVO.setApplication_number(applicationNumber);
			finalDisposal = getString(doc,"finalDisposal"); searchVO.setFinal_disposal(finalDisposal);
			inventionTitle = getString(doc,"inventionTitle"); searchVO.setInvention_title(inventionTitle);
			inventionTitleEng = getString(doc,"inventionTitleEng"); searchVO.setInvention_title_eng(inventionTitleEng);
			openDate = getString(doc,"openDate"); searchVO.setOpen_date(openDate);
			openNumber = getString(doc,"openNumber"); searchVO.setOpen_number(openNumber);
			originalApplicationDate = getString(doc,"originalApplicationDate"); searchVO.setOriginal_application_date(originalApplicationDate);
			originalApplicationKind = getString(doc,"originalApplicationKind"); searchVO.setOriginal_application_kind(originalApplicationKind);
			originalApplicationNumber = getString(doc,"originalApplicationNumber"); searchVO.setOriginal_application_number(originalApplicationNumber);;
			originalExaminationRequestDate = getString(doc,"originalExaminationRequestDate"); searchVO.setExamination_request_date(originalExaminationRequestDate);
			originalExaminationRequestFlag = getString(doc,"originalExaminationRequestFlag"); searchVO.setExamination_request_flag(originalExaminationRequestFlag);
			publicationDate = getString(doc,"publicationDate"); searchVO.setPublication_date(publicationDate);
			publicationNumber = getString(doc,"publicationNumber"); searchVO.setPublication_number(publicationNumber);
			registerDate = getString(doc,"registerDate"); searchVO.setRegister_date(registerDate);
			registerNumber = getString(doc,"registerNumber"); searchVO.setRegister_number(registerNumber);
			registerStatus = getString(doc,"registerStatus"); searchVO.setRegister_status(registerStatus);
			translationSubmitDate = getString(doc,"translationSubmitDate"); searchVO.setTranslation_submit_date(translationSubmitDate);
			
			
			// getAbstract
			String ABSTRACT = "";
			NodeList absList = doc.getElementsByTagName("abstractInfo");
			ABSTRACT = getString (absList, "astrtCont"); searchVO.setABSTRACT(ABSTRACT);
			
			String internationOpenDate = "";
			String internationOpenNumber = "";
			String internationalApplicationDate = "";
			String internationalApplicationNumber = "";
			// getInternational
			Element interList = (Element) doc.getElementsByTagName("internationalInfo").item(0);
			internationOpenDate = getString(interList, "internationOpenDate"); searchVO.setInt_open_date(internationOpenDate);
			internationOpenNumber = getString(interList, "internationOpenNumber"); searchVO.setInt_open_number(internationOpenNumber);
			internationalApplicationDate = getString(interList ,"internationalApplicationDate"); searchVO.setInt_application_date(internationalApplicationDate);
			internationalApplicationNumber = getString(interList, "internationalApplicationNumber"); searchVO.setInt_application_number(internationalApplicationNumber);
			
			// getClaim
			String claim = "";
			NodeList claimList = doc.getElementsByTagName("claimInfo");
			claim = getString(claimList, "claim"); searchVO.setClaim(claim);
			
			
			int biblio_id = 0;
			try {
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPIRS_BIBLIO", searchVO);
			} catch(Exception e) {
				e.printStackTrace();
			}
			biblio_id = searchVO.getBiblio_id();
			searchVO.setBiblio_id(biblio_id);
			searchVO.setKeyword_id(keyword_id);
			patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_RE_KEYWORD", searchVO);
			
			// getIpc
			NodeList ipcList = doc.getElementsByTagName("ipcInfo");
			String ipcDate = "";
			String ipcNumber = "";
			Patent_ipcVO ipcVO = new Patent_ipcVO();
			for (int j=0; j<ipcList.getLength(); j++) {
				Element ipcElement = (Element) ipcList.item(j);
				ipcVO.setBiblio_id(biblio_id);
				ipcDate = getString(ipcElement, "ipcDate");  ipcVO.setIpc_date(ipcDate);
				ipcNumber = getString(ipcElement, "ipcNumber"); ipcVO.setIpc_number(ipcNumber);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_IPC", ipcVO);
				int ipc_id = ipcVO.getIpc_id();
				searchVO.setIpc_id(ipc_id);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_RE_IPC", searchVO);
			}
			
			// getFamiily
			String familyApplicationNumber = "";
			Patent_familyVO familyVO = new Patent_familyVO();
			NodeList familyList = doc.getElementsByTagName("familyInfo");
			for (int j=0; j<familyList.getLength(); j++) {
				Element familyElement = (Element) familyList.item(j);
				familyVO.setBiblio_id(biblio_id);
				familyApplicationNumber = getString(familyElement, "familyApplicationNumber"); familyVO.setApplication_number(familyApplicationNumber);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_FAMILY", familyVO);
				int family_id = familyVO.getFamily_id();
				searchVO.setFamily_id(family_id);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_RE_FAMILY", searchVO);
			}
			
			
			
			
			
			// getPerson
			String address = "";
			String code = "";
			String country = "";
			String name = "";
			String eng_name = "";
			int person_id = 0;
			NodeList applicantList = doc.getElementsByTagName("applicantInfo");
			Patent_personVO personVO = new Patent_personVO();
			personVO.setBiblio_id(biblio_id);
			for (int j=0; j<applicantList.getLength(); j++) {
				personVO.setType("applicant");
				Element appElement = (Element) applicantList.item(j);
				address = getString (appElement, "address"); personVO.setAddress(address);
				code = getString(appElement, "code"); personVO.setCode(code);
				country = getString(appElement, "country"); personVO.setCountry(country);
				eng_name = getString(appElement, "eng_name"); personVO.setEng_name(eng_name);
				name = getString(appElement, "name"); personVO.setName(name);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_PERSON", personVO);
				person_id = personVO.getPerson_id();
				searchVO.setPerson_id(person_id);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_RE_PERSON", searchVO);
			}
			
			address = "";
			code = "";
			country = "";
			name = "";
			eng_name = "";
			NodeList agentList = doc.getElementsByTagName("agentInfo");
			for (int j=0; j<agentList.getLength(); j++) {
				personVO.setType("agent");
				Element agentElement = (Element) agentList.item(j);
				address = getString (agentElement, "address"); personVO.setAddress(address);
				code = getString(agentElement, "code"); personVO.setCode(code);
				country = getString(agentElement, "country"); personVO.setCountry(country);
				eng_name = getString(agentElement, "eng_name"); personVO.setEng_name(eng_name);
				name = getString(agentElement, "name"); personVO.setName(name);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_PERSON", personVO);
				person_id = personVO.getPerson_id();
				searchVO.setPerson_id(person_id);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_RE_PERSON", searchVO);
			}
			
			address = "";
			code = "";
			country = "";
			name = "";
			eng_name = "";
			NodeList inventorList = doc.getElementsByTagName("inventorInfo");
			for (int j=0; j<inventorList.getLength(); j++) {
				personVO.setType("inventor");
				Element inventorElement = (Element) inventorList.item(j);
				address = getString (inventorElement, "address"); personVO.setAddress(address);
				code = getString(inventorElement, "code"); personVO.setCode(code);
				country = getString(inventorElement, "country"); personVO.setCountry(country);
				eng_name = getString(inventorElement, "eng_name"); personVO.setEng_name(eng_name);
				name = getString(inventorElement, "name"); personVO.setName(name);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_PERSON", personVO);
				person_id = personVO.getPerson_id();
				searchVO.setPerson_id(person_id);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_RE_PERSON", searchVO);
			}
			
			// getPriority
			String priorityApplicationCountry = "";
			String priorityApplicationDate = "";
			String priorityApplicationNumber = "";
			NodeList priList = doc.getElementsByTagName("priorityInfo");
			Patent_priorityVO priorityVO = new Patent_priorityVO();
			int priority_id = 0;
			priorityVO.setBiblio_id(biblio_id);
			for (int j=0; j<priList.getLength(); j++) {
				Element priElement = (Element) priList.item(j);
				priorityApplicationCountry = getString(priElement,"priorityApplicationCountry"); priorityVO.setApplication_country(priorityApplicationCountry);
				priorityApplicationDate = getString (priElement,"priorityApplicationNumber"); priorityVO.setApplication_date(priorityApplicationDate);
				priorityApplicationNumber = getString(priElement,"priorityApplicationNumber"); priorityVO.setApplication_number(priorityApplicationNumber);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_PRIORITY", priorityVO);
				priority_id = priorityVO.getPriority_id();
				searchVO.setPriority_id(priority_id);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_RE_PRIORITY", searchVO);
			}
			
			//getLagal
			String commonCodeName = "";
			String documentEngName = "";
			String documentName = "";
			String receiptDate = "";
			String receiptNumber = "";
			NodeList legalList = doc.getElementsByTagName("legalStatusInfo");
			Patent_legalVO legalVO = new Patent_legalVO();
			int legal_id = 0;
			legalVO.setBiblio_id(biblio_id);
			for (int j=0; j<legalList.getLength(); j++) {
				Element legalElement = (Element) legalList.item(j);
				commonCodeName = getString(legalElement,"commonCodeName"); legalVO.setCommon_code_name(commonCodeName);
				documentEngName = getString(legalElement,"documentEngName"); legalVO.setDocument_eng_name(documentEngName);
				documentName = getString(legalElement,"documentName"); legalVO.setDocument_name(documentName);
				receiptDate = getString(legalElement,"receiptDate"); legalVO.setReceipt_date(receiptDate);
				receiptNumber = getString(legalElement,"receiptNumber"); legalVO.setReceipt_number(receiptNumber);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_LEGAL", legalVO);
				legal_id = legalVO.getLegal_id();
				searchVO.setLegal_id(legal_id);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_RE_LEGAL", searchVO);
			}
			
			//getImage
			String docName = "";
			String largePath = "";
			String path = "";
			NodeList imgList = doc.getElementsByTagName("imagePathInfo");
			Patent_imageVO imageVO = new Patent_imageVO();
			int image_id = 0;
			imageVO.setBiblio_id(biblio_id);
			for (int j=0; j<imgList.getLength(); j++) {
				Element imgElement = (Element) imgList.item(j);
				docName = getString(imgElement, "docName"); imageVO.setName(docName);
				path = getString(imgElement, "path"); imageVO.setPath(path);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_IMAGE", imageVO);
				image_id = imageVO.getImage_id();
				searchVO.setImage_id(image_id);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_RE_IMAGE", searchVO);
			}
			
			//getPriorDocument
			String documentNumber = "";
			Patent_priordocumentVO documentVO = new Patent_priordocumentVO();
			documentVO.setBiblio_id(biblio_id);
			int document_id = 0;
			NodeList docuemntList = doc.getElementsByTagName("priorArtDocumentsInfo");
			for (int j=0; j<docuemntList.getLength(); j++) {
				Element documentElement = (Element) docuemntList.item(j);
				documentNumber = getString (documentElement, "documentsNumber"); documentVO.setDocument_number(documentNumber);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_PRIORDOCUMENT", documentVO);
				document_id = documentVO.getDocument_id();
				searchVO.setDocument_id(document_id);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_RE_PRIORDOCUMENT", searchVO);
			}
			
			//getDesignated
			String kind = "";
			String designated_country = "";
			NodeList designList = doc.getElementsByTagName("designatedStateInfo");
			Patent_designatedVO designatedVO = new Patent_designatedVO();
			int designated_id = 0;
			designatedVO.setBiblio_id(biblio_id);
			for (int j=0; j<designList.getLength(); j++) {
				Element designElement = (Element) designList.item(j);
				kind = getString(designElement, "kind"); designatedVO.setKind(kind);
				designated_country = getString(designElement, "country"); designatedVO.setCountry(designated_country);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_DESIGNATED", designatedVO);
				designated_id = designatedVO.getDesignated_id();
				searchVO.setDesignated_id(designated_id);
				patentService.insertPatent(NAME_SPACE+"INSERT_KIPRIS_RE_DESIGNATED", searchVO);
			}
		}
	}
	
	public static String getString (NodeList nList, String attr) {
		try {
			String result = "";
			for (int i=0; i<nList.getLength(); i++) {
				String value = ( (Element) nList.item(i) ).getElementsByTagName(attr).item(0).getTextContent();
				result += value;
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getString (Element element, String attr) {
		try {
			return element.getElementsByTagName(attr).item(0).getTextContent();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getString (Document doc, String attr) {
		try {
			return doc.getElementsByTagName(attr).item(0).getTextContent();
		} catch (Exception e) {
			return null;
		}
	}
	
	private static Document parseXML(String stream) throws Exception {
		DocumentBuilderFactory objDocumentBuilderFactory = null;
		DocumentBuilder objDocumentBuilder = null;
		Document doc = null;
		
		try {
			objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
			
			doc = objDocumentBuilder.parse(stream);
			
		} catch (Exception ex) {
			throw ex;
		}
		
		return doc;
	}

}

