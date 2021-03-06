import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class webcrawler {

	public static void main(String[] args) {
		String website = "www.discovery.com";
		Map visitedLinks = new HashMap();
		ArrayList<String> uriList = new ArrayList<String>();
		Map<String, String> allowDisallowMap = readRobot(website);
		uriList = getLinks(website, allowDisallowMap, visitedLinks);
//		printList(uriList);
//		System.out.println(allowDisallowMap);
//		printMap(allowDisallowMap);
		visitUriList(visitedLinks, uriList);
		printMap(visitedLinks);
	}
	
	//this function generates a list of URLs that we are allow to visit. 
	public static ArrayList<String> getLinks (String website, Map<String, String> allowDisallowMap, Map<String, String> visitedLinks){
		ArrayList<String> uriList = new ArrayList<String>();
		
		try{
			//Opens up the connection to the link
			Document doc = Jsoup.connect("http://" + website).timeout(0).ignoreContentType(true).ignoreHttpErrors(true).get();
			visitedLinks.put("http://" + website, "visiting");
			//Selects anything that has a tag "<a> </a>", and stores it in the object links with the <a> tag.  
			Elements links = doc.body().select("a");

			//For each link stored in "links", we want to grab the href attribute value
			for (Element link : links) {
//				System.out.println(link.attr("href"));
				String normalizedURI;
				if(link.attr("href").startsWith("#") || link.attr("href").contains("mailto:") || link.attr("href").length() < 1){
					;
				}
				else {
					//converts the attribute value into a String and checks if it starts with a "/"
					if(link.attr("href").startsWith("/") && !(link.attr("href").contains("//"))) {
						normalizedURI = "http://" + website + link.attr("href");
					}
					else if (link.attr("href").contains("//") && !(link.attr("href").contains("http:")) && !(link.attr("href").contains("https:"))) {
						normalizedURI = "http:" + link.attr("href");
					}
					else {
						normalizedURI = link.attr("href");
					}
					if(checkAllowDisallow(normalizedURI, allowDisallowMap)){
						uriList.add(normalizedURI);
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			return uriList;
		}
	}
	
	//this function prints out the URI List
	public static void printList(ArrayList<String> uriList){
		for(String link:uriList){
			System.out.println(link);
		}
	}
	
	//this function prints the hash map that consist of all the allow and disallow links
	public static void printMap(Map<String, String> allowDisallowMap){
		for(String key: allowDisallowMap.keySet()){
			System.out.println(key + " " + allowDisallowMap.get(key));
		}
	}
	
	//this function reads the robots.txt file and determines whether the link is allowed or disallowed
	public static Map<String, String> readRobot(String website){
		Map allowDisallowMap = new HashMap();
		try(BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://" + website + "/robots.txt").openStream()))){
			String line = null;
			
			//Defines a new pattern
			String pattern = "(\\/)(.*)";
			
			//Creates a Pattern object
			Pattern r = Pattern.compile(pattern);
						
			//Looks through Robots.txt until end of file
			while((line = in.readLine()) != null){
				//If line contains disallow, Matcher matches each line against the pattern
				if(line.startsWith("Disallow")){
					Matcher m = r.matcher(line);
					if(m.find()){
//						System.out.println(m.group(0));
						allowDisallowMap.put("http://" + website + m.group(0), "disallowed");
					}
				}
				else if(line.startsWith("Allow")){
					Matcher m = r.matcher(line);
					if(m.find()){
						allowDisallowMap.put("http://" + website + m.group(0), "allowed");
					}
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return allowDisallowMap;
	}

	//This function checks whether the path is allowed or disallowed, and if the path is not in the map, it checks it's parent path to determine whether it's allowed or disallowed. 
	public static boolean checkAllowDisallow(String path, Map<String, String> allowDisallowMap) throws MalformedURLException{
		if(allowDisallowMap.get(path) == "allowed"){
			return true;
		}
		else if(allowDisallowMap.get(path) == "disallowed"){
			return false;
		}
		else{
			File f = new File(path);
			String parent = f.getParent();
			if(parent == null || parent == "/"){
				return true;
			}
			else{
				return checkAllowDisallow(parent, allowDisallowMap);
			}
		}
	}
	
	//This function goes through each of the links in the arraylist.
	public static Map<String, String> visitUriList (Map<String, String> visitedLinks, ArrayList<String> uriList){
		for(String link: uriList){
			openLink(visitedLinks, link);
		}
		return visitedLinks;
	}
	
	//This function checks if the link in the arraylist has already been visited.
	public static Map<String, String> openLink (Map<String, String> visitedLinks, String link){
		if(visitedLinks.containsKey(link)){
			return visitedLinks;
		}
		else{
			markAsVisited(visitedLinks, link);
		}
		return visitedLinks;
	}
	
	//This function visits all the unvisited links and marks the link as visited.
	public static Map<String, String> markAsVisited(Map<String, String> visitedLinks, String link){
		visitedLinks.put(link, "visiting");
		System.out.println("Visiting " + link);
		try{
			Jsoup.connect(link).timeout(0).ignoreContentType(true).ignoreHttpErrors(true).get(); 
			visitedLinks.put(link, "visited");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return visitedLinks;
	}
}
