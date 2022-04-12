package sk.po.spse.dzurikm.linkorganizer.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import java.net.URI;
import java.net.URISyntaxException;

import sk.po.spse.dzurikm.linkorganizer.models.Link;


public class AutoURLInfoUtil {
    private static final String GUITAR_TABS = "tabs.ultimate-guitar.com";
    private static final String SUPER_MUSIC = "supermusic.cz";
    private static final String MEDIUM = "medium.com";
    private static final String YOUTUBE = "www.youtube.com";

    public static Link getLink(String url, String response) throws URISyntaxException {
        String domain = getDomain(url);
        Link link = null;
        switch (domain){
            case GUITAR_TABS:
                link = guitarTabs(response,url);
                break;
            case SUPER_MUSIC:
                link = superMusic(response,url);
                break;
            case MEDIUM:
                link = medium(response,url);
                break;
            case YOUTUBE:
                link = youtube(response,url);
                break;
            default:
                break;
        }

        return link;
    }

    private static Link youtube(String response, String url) {
        Link link = new Link();
        String name = "",description = "";

        Document doc = Jsoup.parse(response);
        Element title = doc.getElementsByTag("title").get(0);
        //Element author = doc.getElementsByClass("iv-branding-context-name").get(0);


        link.setName(withoutLastCharacters(title.text(),9));
        //link.setDescription(author.text());

        return link;
    }


    private static String withoutLastCharacters(String s,int count){
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < s.length() - count; i++) {
            newString.append(s.charAt(i));
        }

        return newString.toString().trim();
    }

    private static Link medium(String response, String url) {
        Link link = new Link();
        String name = "",description = "";

        Document doc = Jsoup.parse(response);
        for (Element input : doc.select("meta")){
            if (input.attr("property").equals("og:title")){
                name = input.attr("content");
                if (!description.equals("")) break;
            }
            if (input.attr("name").equals("author")){
                description = input.attr("content");
                if (!name.equals("")) break;
            }

        }

        link.setName(name);
        link.setDescription(description);

        return link;

    }

    private static String getDomain(String url) throws URISyntaxException {
        return new URI(url).getHost();
    }

    private static Link guitarTabs(String response,String url){
        Link link = new Link();
        String info = "";

        // Gayle - Abcdefu Angrier (Chords)
        // [0] = Artist
        // [1] = Title + (Chords)
        // We need to filter (Chords)
        Document doc = Jsoup.parse(response);
        for (Element input : doc.select("meta")){
            if (input.attr("property").equals("og:title")){
                info = input.attr("content");
                break;
            }

        }

        String[] data = info.split("-");
        link.setHref(url);

        String[] songNameArray = data[1].split(" ");
        String songName = "";

        for (int i = 0; i < songNameArray.length - 1; i++) {
            songName += songNameArray[i] + " ";
        }

        link.setName(songName.trim());
        link.setDescription(data[0].trim());

        return link;
    }

    private static Link superMusic(String response,String url){
        Link link = new Link();
        String info = "";

        // Hana Hegerová - Lásko prokletá [akordy a text na Supermusic.sk]
        // [0] = Artist
        // [1] = Title + (Chords)
        // We need to filter [akordy a text na Supermusic.sk]
        Document doc = Jsoup.parse(response);
        for (Element input : doc.select("meta")){
            if (input.attr("name").equals("description")){
                info = input.attr("content");
                break;
            }

        }

        String[] data = info.split("-");
        link.setHref(url);

        String[] songNameArray = data[1].split("\\[");
        String songName = songNameArray[0];


        link.setName(songName.trim());
        link.setDescription(data[0].trim());

        return link;
    }


}
