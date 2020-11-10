package utils;

import models.word.vo.Article;
import models.word.vo.ArticleLink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static utils.Constant.ABBR;

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
            if (text.contains("上传") || text.contains("创作中心") || text.contains("有声出版") || text.contains("小雅音箱") || text.contains("欢迎订阅") || text.contains("音频") || text.contains("Xinhua") || text.contains("Photo") || text.contains("CHINA DAILY")) continue;
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

    public static List<Article> extractXMLYChinaDailyArticleMulti(String html, ArticleLink articleLink) {
        Document document = Jsoup.parse(html);
        List<Article> articleList = extractXMLYChinaDailyArticleTitle(document, articleLink);
        articleList = extractXMLYChinaDailyArticleContent(document, articleList);
        return articleList;
    }

    private static List<Article> extractXMLYChinaDailyArticleContent(Document document, List<Article> articleList) {
        if (articleList.stream().map(v -> v.title).filter(v1 -> !(v1 == null || v1.isEmpty())).collect(Collectors.toList()).size() < 4
                || articleList.stream().map(v -> v.titleNote).filter(v1 -> !(v1 == null || v1.isEmpty())).collect(Collectors.toList()).size() < 4
                || articleList.stream().map(v -> v.titleAndNote).filter(v1 -> !(v1 == null || v1.isEmpty())).collect(Collectors.toList()).size() < 4) {
            return new ArrayList<>();
        }
        Article currentArticle = null;
        int contentTicket = 0;
        boolean isTitle = false;
        List<String> pTextList = document.select("p").eachText();
        for (int i = 0; i < pTextList.size(); i++) {
            String text = pTextList.get(i);
            String textTemp = replace(text);
            if (checkText4Content(textTemp)) {
                continue;
            }
            if (StringUtils.trimAllWhitespace(textTemp).toLowerCase().contains("findmoreaudionews")) {
                break;
            }
            for (Article article : articleList) {
                if (article.titleAndNote == null || article.title == null || article.titleNote == null
                        || article.titleAndNote.isEmpty() || article.title.isEmpty() || article.titleNote.isEmpty()) {
                    return articleList;
                }
                if (article.titleAndNote.contains(StringUtils.trimAllWhitespace(textTemp))) {
                    currentArticle = article;
                    isTitle = true;
                    break;
                }
                /*if (article.title.contains(StringUtils.trimAllWhitespace(textTemp))) {
                    currentArticle = article;
                    isTitle = true;
                    break;
                }
                if (article.titleNote.contains(StringUtils.trimAllWhitespace(textTemp))) {
                    currentArticle = article;
                    isTitle = true;
                    break;
                }*/
            }
            if (isTitle) {
                isTitle = false;
                continue;
            }
            boolean isContent = StringUtil.isAlpha(textTemp.charAt(0));
            int indexOfChinese = StringUtil.indexOfChinese(textTemp);
            if (isContent) {
                contentTicket = 2;
            }
            if (contentTicket == 2) {
                if (currentArticle != null) {
                    if (currentArticle.content == null) {
                        if (indexOfChinese > 0) {
                            currentArticle.content = text.substring(0, indexOfChinese);
                            currentArticle.contentNote = text.substring(indexOfChinese, text.length());
                        } else {
                            currentArticle.content = text;
                        }
                    } else {
                        if (indexOfChinese > 0) {
                            currentArticle.content += text.substring(0, indexOfChinese);
                            currentArticle.contentNote += text.substring(indexOfChinese, text.length());
                        } else {
                            currentArticle.content += text;
                        }
                    }
                }
                contentTicket--;
            } else if (contentTicket == 1) {
                if (currentArticle != null) {
                    if (currentArticle.contentNote == null) {
                        currentArticle.contentNote = text;
                    } else {
                        currentArticle.contentNote += text;
                    }
                }
                contentTicket--;
            }
        }
        if (articleList.stream().map(v -> v.content).filter(v1 -> !(v1 == null || v1.isEmpty())).collect(Collectors.toList()).size() < 4
                || articleList.stream().map(v -> v.contentNote).filter(v1 -> !(v1 == null || v1.isEmpty())).collect(Collectors.toList()).size() < 4) {
            return new ArrayList<>();
        }
        return articleList;
    }

    private static List<Article> extractXMLYChinaDailyArticleTitle(Document document, ArticleLink articleLink) {
        List<Article> articleList = new ArrayList<>();
        Article article = null;
        List<String> bTextList = document.select("b").eachText();
        int titleTicket = 0;
        boolean isTitle;
        boolean isTitleContent;
        String titleAndNote = "";
        for (int i = 0; i < bTextList.size(); i++) {
            String text = bTextList.get(i);
            String textTemp = text
                    .replaceAll("\\?", "")
                    .replaceAll(":", "")
                    .replaceAll("'", "")
                    .replaceAll(">", "")
                    .replaceAll("\"", "");
            if (StringUtils.trimAllWhitespace(textTemp).toLowerCase().contains("findmoreaudionews")) {
                break;
            }
            if (textTemp.trim().isEmpty() || textTemp.contains("No.") || textTemp.contains("、")
                    || textTemp.contains("各位听众") || textTemp.contains("中国日报") || textTemp.contains("China Daily") || textTemp.contains("不错过世界上发生的趣事")) {
                continue;
            }
            String checkString = StringUtils.trimAllWhitespace(
                    textTemp.replaceAll("\\d", "")
                            .replaceAll("%", "")
                            .replaceAll("￡", "")
                            .replaceAll("\\$", ""));
            if (checkString.isEmpty()) {
                continue;
            }
            isTitle = StringUtil.isAlpha(checkString.charAt(0)) && titleTicket == 0;
            isTitleContent = StringUtil.isChineseByScript(checkString.charAt(0)) && titleTicket == 0;
            if (isTitle) {
                titleTicket = 2;
            } else if (isTitleContent) {
                titleTicket = 1;
            }
            if (titleTicket == 2) {
                if (articleList.size() == 4) {
                    break;
                }
                article = new Article(articleLink);
                articleList.add(article);
                titleAndNote += StringUtils.trimAllWhitespace(textTemp);
                article.title = textTemp;
                titleTicket--;
            } else if (titleTicket == 1) {
                if (article == null) {
                    article = new Article(articleLink);
                    articleList.add(article);
                }
                titleAndNote += StringUtils.trimAllWhitespace(textTemp);
                article.titleNote = Optional.ofNullable(article.titleNote).orElse("") + textTemp;
                article.titleAndNote = Optional.ofNullable(article.titleAndNote).orElse("") + titleAndNote;
                titleTicket--;
                titleAndNote = "";
            }
        }
        if (articleList.size() == 4) {
            return articleList;
        }
        List<String> titleList = articleList.stream().map(v -> v.title).collect(Collectors.toList());
        List<String> titleNoteList = articleList.stream().map(v -> v.titleNote).collect(Collectors.toList());
        List<String> titleAndNoteList = articleList.stream().map(v -> v.titleAndNote).collect(Collectors.toList());
        List<String> pTextList = document.select("p").eachText();
        for (int i = 0; i < pTextList.size(); i++) {
            String text = pTextList.get(i);
            String textTemp = replace(text);
            if (checkText4Title(textTemp)) {
                continue;
            }
            if (StringUtils.trimAllWhitespace(textTemp).toLowerCase().contains("findmoreaudionews") || StringUtils.trimAllWhitespace(textTemp).toLowerCase().contains("重点单词怎么读")) {
                break;
            }
            if (titleAndNoteList.contains(StringUtils.trimAllWhitespace(textTemp))) {
                continue;
            }
            if (titleList.contains(textTemp)) {
                continue;
            }
            if (titleNoteList.contains(textTemp)) {
                continue;
            }
            String checkString = StringUtils.trimAllWhitespace(
                    textTemp.replaceAll("\\d", "")
                            .replaceAll("%", "")
                            .replaceAll("￡", ""));
            if (checkString.isEmpty()) {
                continue;
            }
            isTitle = StringUtil.isAlpha(checkString.charAt(0)) && titleTicket == 0;
            isTitleContent = StringUtil.isChineseByScript(checkString.charAt(0)) && titleTicket == 0;
            if (isTitle) {
                titleTicket = 2;
            } else if (isTitleContent) {
                titleTicket = 1;
            }
            if (titleTicket == 2) {
                article = new Article(articleLink);
                articleList.add(article);
                titleAndNote += StringUtils.trimAllWhitespace(textTemp);
                article.title = textTemp;
                titleTicket--;
            } else if (titleTicket == 1) {
                if (article == null) {
                    article = new Article(articleLink);
                    articleList.add(article);
                }
                titleAndNote += StringUtils.trimAllWhitespace(textTemp);
                article.titleNote = textTemp;
                article.titleAndNote = titleAndNote;
                titleTicket--;
                titleAndNote = "";
            }
            if (articleList.size() == 4 && titleTicket == 0) {
                break;
            }
        }
        return articleList;
    }

    private static String replace(String text) {
        return text
                .replaceAll("\\?", "")
                .replaceAll(":", "")
                .replaceAll("'", "")
                .replaceAll(">", "")
                .replaceAll("\"", "")
                .replaceAll("/", "")
                .replaceAll("《", "")
                .replaceAll("》", "")
                .replaceAll("-", "");
    }

    private static boolean checkText4Title(String text) {
        return text.isEmpty()
                || text.contains("上传")
                || text.contains("创作中心")
                || text.contains("有声出版")
                || text.contains("小雅音箱")
                || text.contains("中国日报")
                || StringUtils.trimAllWhitespace(text).toLowerCase().contains("thisischinadaily")
                || text.contains("重点词汇")
                || text.contains("各位听众")
                || (text.contains("英") && text.contains("美"))
                || Arrays.stream(ABBR).anyMatch(text::contains)
                || StringUtil.isAlpha(text)
                || (StringUtil.isAlpha(text.charAt(0)) && text.length() > 50)
                || (StringUtil.isChineseByScript(text.charAt(0)) && text.length() > 20);
    }

    private static boolean checkText4Content(String text) {
        return text.isEmpty()
                || text.contains("上传")
                || text.contains("创作中心")
                || text.contains("有声出版")
                || text.contains("小雅音箱")
                || text.contains("中国日报")
                || StringUtils.trimAllWhitespace(text).toLowerCase().contains("thisischinadaily")
                || text.contains("重点词汇")
                || text.contains("各位听众")
                || (text.contains("英") && text.contains("美"))
                || Arrays.stream(ABBR).anyMatch(text::contains)
                || StringUtil.isAlpha(text);
    }
}