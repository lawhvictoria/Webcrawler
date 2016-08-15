import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class webcrawler {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String website = "www.kp.org";
		ArrayList<String> uriList = new ArrayList<String>();
		uriList = getLinks(website);
		printList(uriList);
//		System.out.println(uriList);
		
	}
	
	public static ArrayList<String> getLinks (String website){
		ArrayList<String> uriList = new ArrayList<String>();
		
		try{
			//Opens up the connection to the link
			Document doc = Jsoup.connect("http://" + website).get(); 
			
			//Selects anything that has a tag "<a> </a>", and stores it in the object links with the <a> tag.  
			Elements links = doc.body().select("a");
			
//			System.out.println(links.toString());

			//For each link stored in "links", we want to grab the href attribute value
			for (Element link : links) {
//				System.out.println(link.attr("href"));
				
				//converts the attribute value into a String and checks if it starts with a "/"
				if(link.attr("href").startsWith("/") && !(link.attr("href").contains("//"))) {
					uriList.add("http://" + website + link.attr("href"));
				}
				else if (link.attr("href").contains("//") && !(link.attr("href").contains("http:")) && !(link.attr("href").contains("https:"))) {
					uriList.add("http:" + link.attr("href"));
				}
				else {
					uriList.add(link.attr("href"));
				}
			}
//			System.out.println();
//			System.out.println();
//			System.out.println();
//			System.out.println();
//			System.out.println();
			
//			String htmlString = doc.toString();
//			System.out.println(htmlString);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			return uriList;
		}
	}
	
	public static void printList(ArrayList<String> uriList){
		for(String link:uriList){
			System.out.println(link);
		}
	}

}
