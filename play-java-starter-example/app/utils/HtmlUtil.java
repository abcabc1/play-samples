package utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.*;

public class HtmlUtil {

//    public static Map extractFile(String path) {
//
//        File file = new File(path);
//        try {
//            Document doc = Jsoup.parse(file, "utf-8");
//            return extractHansHtml(doc);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static Map<String, List<String>> extractDictEn(String html) {
        Document document = Jsoup.parse(html);
        Map<String, List<String>> map = new HashMap<>();
        List<String> typeList = document.select("ul.dict-basic-ul").select("li").eachText();
        List<String> soundList = document.select(".phonetic").select("bdo").eachText();
        map.put("sound", soundList);
        Elements translationElements = document.select("div.layout").select(".sort").select("ol");
        for (int i = 0; i < translationElements.size(); i++) {
            List<String> textList = eachText(translationElements.get(i).select("li"));
            map.put(typeList.get(i), textList);
        }
        return map;
    }

    public static List<String> eachText(Elements elements) {
        ArrayList<String> texts = new ArrayList<>(elements.size());
        for (Element el : elements) {
            if (el.hasText())
                texts.add(el.html());
        }
        return texts;
    }

    public static List<String> extractHansPhrase(String html) {
        Document document = Jsoup.parse(html);
        List<String> list = new ArrayList<>();
        list.add(document.selectFirst(".dicpy").html());
        list.add(document.select(".gc_sy").html());
        return list;
    }

    public static Map<String, List<String>> extractHans(String html) {
        Document document = Jsoup.parse(html);
        Map<String, List<String>> map = new HashMap<>();
        List<String> pinyinList = document.select(".z_py").select(".z_d.song").eachText();
        Elements elements = document.select(".content.definitions.jnr").select("ol");
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).select("li").size() == 0) {
                map.put(pinyinList.get(i), elements.get(i).select("p").eachText());
            } else {
                map.put(pinyinList.get(i), elements.get(i).select("li").eachText());
            }
        }
        return map;
    }

    public static Map<String, List<String>> extractHansHtml1(String html) {
        Document document = Jsoup.parse(html);
        Map<String, List<String>> map = new HashMap<>();
        List<String> pinyinList = document.select(".z_d.song").eachText();
        Element element = document.selectFirst(".content.definitions.jnr");
        element.after("");
        element.nextElementSibling();
        element.elementSiblingIndex();
        element.nextSibling();
        int pinyinSize = element.select(".dicpy").size();
        for (int i = 0; i < pinyinSize - 1; i++) {
            Element olElement = element.select("ol").get(i);
            if (olElement.select("li").size() == 0) {
                map.put(pinyinList.get(i), olElement.select("p").eachText());
            } else {
                map.put(pinyinList.get(i), olElement.select("li").eachText());
            }
        }
        return map;
    }

    public static Map<String, List<String>> extractDictPhraseEn(String html) {
        Document document = Jsoup.parse(html);
        Map<String, List<String>> map = new HashMap<>();
        String note = document.select(".basic.clearfix").select("ul").select("li").select("strong").text();
        List<String> list = eachText(document.select("div.layout.sort").select("ol").select("li"));
        map.put(note, list);
        return map;
    }

    public static LinkedList<String> extractXMLYXiaShuoTitle(String html) {
        Document document = Jsoup.parse(html);
        LinkedHashSet<String> titleSet = new LinkedHashSet<>();
        LinkedList<String> titleList = new LinkedList<>();
        Elements indexElements = document.select(".num._Vc");
        List<String> indexList = indexElements.eachText();
        Elements elements = document.select(".text._Vc");
        int i = 0;
        for (Element element : elements) {
            String title = "";
            String text = element.text();
            if (text.contains("（")) {
                String index = indexList.get(i);
                title = text.substring(0, text.indexOf("（"));
                String href = element.select("a").attr("href");
                if (titleSet.add(title)) {
                    titleList.add(index + "#" + title + "#" + href);
                }
            }
            i++;
        }
        return titleList;
    }

    public static LinkedHashSet<String> extractXMLYChinaDailyTitle(String html) {
        Document document = Jsoup.parse(html);
        LinkedHashSet<String> pageArticleTitleSet = new LinkedHashSet<>();
        Elements indexElements = document.select(".num._Vc");
        List<String> indexList = indexElements.eachText();
        Elements elements = document.select(".text._Vc");
        int i = 0;
        for (Element element : elements) {
            String title = "";
            String text = element.text();
            if (text.contains("热门：") || (text.contains("月") && text.contains("日"))) {
                String index = indexList.get(i);
                title = text.substring(text.indexOf("：") + 1);
                String href = element.select("a").attr("href");
                pageArticleTitleSet.add(index + "#" + title + "#" + href);
            }
            i++;
        }
        return pageArticleTitleSet;
    }

    public static List<String> extractXMLYXiaShuoArticle(String html) {
        Document document = Jsoup.parse(html);
        List<String> articleList = new ArrayList<>();
        List<String> pTextList = document.select("p[data-flag]").eachText();
        int startIndex = 0, endIndex = 0;
        for (int i = 0; i < pTextList.size(); i++) {
            String pText = pTextList.get(i);
            if (pText.contains("原文")) {
                startIndex = i;
            }
            if (pText.contains("语言点")) {
                endIndex = i;
                break;
            }
        }
        if (startIndex == 0 || endIndex == 0) {
            return null;
        }
        for (int j = startIndex + 1; j < endIndex; j++) {
            articleList.add(pTextList.get(j));
        }
        return articleList;
    }

    public static String extractXMLYChinaDailyArticleSingle(String html) {
        Document document = Jsoup.parse(html);
        String content = "";
        List<String> pTextList = document.select("p").eachText();
        for (int i = 0; i < pTextList.size(); i++) {
            String text = pTextList.get(i);
            if (text.isEmpty()) continue;
            if (text.contains("Xinhua") || text.contains("Photo") || text.contains("CHINA DAILY")) continue;
            if (text.contains("Find more audio news")) {
                if (!content.isEmpty()) {
                    content += text;
                }
                break;
            }
            boolean isContent = (StringUtil.isAlpha(text.charAt(0)) || (text.charAt(0) == '"' && StringUtil.isAlpha(text.charAt(1))));
            if (isContent) {
                if (!text.substring(text.length() - 1).contains(".")) {
                    text = text + ".";
                }
                content += text;
            }
        }
        return content;
    }

    public static List<String> extractXMLYChinaDailyArticleMulti(String html) {
        Document document = Jsoup.parse(html);
        Map<String, List<String>> titleMap = extractXMLYChinaDailyArticleTitle(document);
        Map<String, List<String>> contentMap = extractXMLYChinaDailyArticleContent(document, titleMap);
        List<String> titleList = titleMap.get("title");
        List<String> titleNoteList = titleMap.get("titleNote");
        List<String> contentList = contentMap.get("content");
        List<String> contentNoteList = contentMap.get("contentNote");
        List<String> articleList = new ArrayList<>();
        if (titleList.size() != 4 || titleNoteList.size() != 4 || contentList.size() != 4 || contentNoteList.size() != 4) {
            return articleList;
        }
        for (int i = 0; i < 4; i++) {
            articleList.add(titleList.get(i) + "#" + titleNoteList.get(i) + "#" + contentList.get(i) + "#" + contentNoteList.get(i));
        }
        return articleList;
    }

    private static Map<String, List<String>> extractXMLYChinaDailyArticleContent(Document document, Map<String, List<String>> titleMap) {
        List<String> titleList = titleMap.get("title");
        List<String> titleNoteList = titleMap.get("titleNote");
        List<String> contentList = new ArrayList<>();
        List<String> contentNoteList = new ArrayList<>();
        String content = "";
        String contentNote = "";
        boolean contentFlag = false;
        int contentTicket = 0;
        List<String> pTextList = document.select("p").eachText();
        for (int i = 0; i < pTextList.size(); i++) {
            String text = pTextList.get(i);
            String textTemp =
                    StringUtils.trimAllWhitespace(text).toLowerCase();
            textTemp = textTemp
                    .replaceAll("\\?", "")
                    .replaceAll(":", "")
                    .replaceAll("'", "")
                    .replaceAll(">", "");
            if (textTemp.isEmpty()
                    || textTemp.contains("上传")
                    || textTemp.contains("创作中心")
                    || textTemp.contains("有声出版")
                    || textTemp.contains("小雅音箱")
                    || textTemp.contains("chinadaily")
                    || textTemp.contains("重点词汇")) {
                continue;
            }
            if (textTemp.contains("findmoreaudionews")) {
                break;
            }
            boolean isAlphaFirst = StringUtil.isAlpha(textTemp.charAt(0));
            boolean isChineseFirst = StringUtil.isChineseByScript(textTemp.charAt(0));
            int indexOfChinese = StringUtil.indexOfChinese(textTemp);
            if (isAlphaFirst && indexOfChinese > 0) {
                if (titleList.contains(textTemp.substring(0, indexOfChinese - 1))) {
                    if (!content.isEmpty()) {
                        contentList.add(content);
                        content = "";
                    }
                    continue;
                }
            } else if (isAlphaFirst) {
                if (titleList.contains(text)) {
                    if (!content.isEmpty()) {
                        contentList.add(content);
                        content = "";
                    }
                    continue;
                }
            } else if (isChineseFirst) {
                if (titleNoteList.contains(text)) {
                    if (!contentNote.isEmpty()) {
                        contentNoteList.add(contentNote);
                        contentNote = "";
                    }
                    continue;
                }
            }
            boolean isContent = StringUtil.isAlpha(textTemp.charAt(0));
            if (isContent) {
                contentTicket = 2;
            }
            if (contentTicket == 2) {
                content += text;
                contentTicket--;
            } else if (contentTicket == 1) {
                contentNote += text;
                contentTicket--;
            }

            /*boolean isContent = (StringUtil.isAlpha(text.charAt(0)) || text.charAt(0) == '\'' || text.charAt(0) == 34) && !StringUtil.hasChinese(text) && contentFlag;
            if (isContent) {
                contentTicket = 2;
            } else if (!content.isEmpty() && contentTicket == 0) {
                contentList.add(content);
                contentNoteList.add(contentNote);
                content = "";
                contentNote = "";
                contentFlag = false;
            }
            if (contentTicket == 2) {
                content += text;
                contentTicket--;
            } else if (contentTicket == 1) {
                contentNote += text;
                contentTicket--;
            }
            if (isContent) {
                indexOfChinese = StringUtil.indexOfChinese(text);
                if (indexOfChinese > 0) {
                    content += text.substring(0, indexOfChinese - 1);
                    contentNote += text.substring(indexOfChinese - 1);
                    contentFlag = false;

                } else if (contentFlag) {
                    contentTicket = 2;
                }
            }
            if (contentTicket == 2) {
                content += text;
                contentTicket--;
            } else if (contentTicket == 1) {
                contentNote += text;
                contentTicket--;
            }
*/
        }
        return null;
    }

    private static Map<String, List<String>> extractXMLYChinaDailyArticleTitle(Document document) {
        Map<String, List<String>> map = new HashMap<>();
        List<String> titleList = new ArrayList<>();
        List<String> titleNoteList = new ArrayList<>();
        map.put("title", titleList);
        map.put("titleNote", titleNoteList);
        List<String> bTextList = document.select("b").eachText();
        int titleTicket = 0;
        boolean isTitle = false;
        for (int i = 0; i < bTextList.size(); i++) {
            if (titleList.size() == 4 && titleNoteList.size() == 4) {
                break;
            }
            String text = bTextList.get(i);
            String textTemp = text
                    .replaceAll("\\?", "")
                    .replaceAll(":", "")
                    .replaceAll("'", "")
                    .replaceAll(">", "");
            if (textTemp.trim().isEmpty() || !textTemp.contains("No.") || textTemp.contains("、")) {
                continue;
            }
            isTitle = StringUtil.isAlpha(textTemp.charAt(0));
            if (isTitle) {
                titleTicket = 2;
            }
            if (titleTicket == 2) {
                titleList.add(textTemp);
                titleTicket--;
            } else if (titleTicket == 1) {
                titleNoteList.add(textTemp);
                titleTicket--;
            }
        }
        if (titleList.size() == 4 && titleNoteList.size() == 4) {
            return map;
        }
        List<String> pTextList = document.select("p").eachText();
        for (int i = 0; i < pTextList.size(); i++) {
            String text = pTextList.get(i);
            String textTemp =
                    StringUtils.trimAllWhitespace(text).toLowerCase();
            textTemp = textTemp
                    .replaceAll("\\?", "")
                    .replaceAll(":", "")
                    .replaceAll("'", "")
                    .replaceAll(">", "");
            if (textTemp.isEmpty()
                    || textTemp.contains("上传")
                    || textTemp.contains("创作中心")
                    || textTemp.contains("有声出版")
                    || textTemp.contains("小雅音箱")
                    || textTemp.contains("chinadaily")
                    || textTemp.contains("重点词汇")) {
                continue;
            }
            if (textTemp.contains("findmoreaudionews")) {
                break;
            }
            boolean isAlphaFirst = StringUtil.isAlpha(textTemp.charAt(0));
            boolean isChineseFirst = StringUtil.isChineseByScript(textTemp.charAt(0));
            int indexOfChinese = StringUtil.indexOfChinese(textTemp);
            if (isAlphaFirst && indexOfChinese > 0) {
                if (titleList.contains(textTemp.substring(0, indexOfChinese - 1))) {
                    continue;
                } else {
                    titleList.add(textTemp);
                }
            } else if (isAlphaFirst) {
                if (titleList.contains(text)) {
                    continue;
                } else {
                    titleList.add(textTemp);
                }
            } else if (isChineseFirst) {
                if (titleNoteList.contains(text)) {
                    continue;
                } else {
                    titleNoteList.add(textTemp);
                }
            }
        }
        return map;
    }
            /*} else {
                isTitle = isAlphaFirst || StringUtil.isNumber(textTemp.charAt(0));
                int indexOfAlpha = StringUtil.indexOfAlpha(text);
            }*/
            /*if (titleList.size() == 0 && isTitle) {
                titleTicket = 2;
            }
            if (titleTicket == 2) {
                titleList.add(textTemp);
                *//*if (indexOfAlpha > 0) {
                    titleList.add(text.substring(indexOfAlpha - 1));
                }*//*
                titleTicket--;
            } else if (titleTicket == 1) {
                titleNoteList.add(textTemp);
                titleTicket--;
                contentFlag = true;
                continue;
                *//*if (!contentNote.isEmpty() && contentTicket == 0) {
                    contentList.add(content);
                    contentNoteList.add(contentNote);
                    content = "";
                    contentNote = "";
                }
                continue;*//*
            }*/
}