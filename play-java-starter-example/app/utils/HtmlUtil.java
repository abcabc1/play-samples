package utils;

import models.word.ArticleLink;
import models.word.vo.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import utils.exception.InternalException;

import java.util.*;
import java.util.stream.Collectors;

import static utils.Constant.ABBR;
import static utils.exception.ExceptionEnum.FINAL_ARTICLE_FAILURE;
import static utils.exception.ExceptionEnum.MATCH_ARTICLE_TITLE_4_CONTENT_FAILURE;

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

    private static final Logger logger = LoggerFactory.getLogger(HtmlUtil.class);

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

    public static LinkedHashSet<String> extractXMLYTitle(String html) {
        Document document = Jsoup.parse(html);
        LinkedHashSet<String> pageArticleTitleSet = new LinkedHashSet<>();
        String indexClass = ".num.lF_";
        String titleClass = ".text.lF_";
        Elements indexElements = document.select(indexClass);
        List<String> indexList = indexElements.eachText();
        Elements titleElements = document.select(titleClass);
        for (int i = 0; i < titleElements.size(); i++) {
            Element element = titleElements.get(i);
            String text = element.text();
            String index = indexList.get(i);
            String href = element.select("a").attr("href");
            pageArticleTitleSet.add(index + "#" + text + "#" + href);
        }
        return pageArticleTitleSet;
    }

    public static String extractXSArticle(String html) {
        Document document = Jsoup.parse(html);
        List<String> pTextList = document.select("p[data-flag]").eachText();
        boolean contentFlag = false;
        String content = "";
        for (int i = 0; i < pTextList.size(); i++) {
            String pText = pTextList.get(i);
            if (pText.contains("英语学新闻")) {
                break;
            }
            if (pText.contains("▍原文")) {
                if (pText.equals("▍原文")) {
                    contentFlag = true;
                    continue;
                } else {
                    if (pText.indexOf("语言点") == -1) {
                        content = StringUtils.trimLeadingWhitespace(pText.substring(pText.indexOf("原文") + 2));
                    } else {
                        content = StringUtils.trimLeadingWhitespace(pText.substring(pText.indexOf("原文") + 2, pText.indexOf("语言点")));
                    }
                    return content;
                }
            } else if (pText.contains("语言点")) {
                break;
            }
            if (contentFlag && (!StringUtils.trimLeadingWhitespace(pText).isEmpty() && !StringUtil.isChineseByScript(StringUtils.trimLeadingWhitespace(pText).charAt(0)))) {
                content += pText;
            }
        }
        contentFlag = false;
        if (content.isEmpty()) {
            pTextList = document.select("p").eachText();
            for (int i = 0; i < pTextList.size(); i++) {
                String pText = pTextList.get(i);
                if (pText.contains("小雅音箱")) {
                    contentFlag = true;
                    continue;
                }
                if (contentFlag) {
                    content += pText;
                    break;
                }
            }
        }
        if (content.equals("教书匠小夏") || StringUtil.isChineseByScript(content.charAt(0)) || content.length() < 50) {
            Element element = document.selectFirst("article");
            if (element == null) {
                return "";
            }
            content = element.text();
            if (content == null || content.isEmpty()) {
                element = element.selectFirst("span");
                if (element == null) {
                    return "";
                }
                content = element.text();
            }
        }
        return content;
    }

    public static String extractChinaDailyArticleSingle(String html) {
        Document document = Jsoup.parse(html);
        String content = "";
        List<String> pTextList = document.select("p").eachText();
        for (int i = 0; i < pTextList.size(); i++) {
            String text = pTextList.get(i);
            if (text.isEmpty()) continue;
            if (text.contains("上传") || text.contains("创作中心") || text.contains("有声出版") || text.contains("小雅音箱") || text.contains("欢迎订阅") || text.contains("音频") || text.contains("Xinhua") || text.contains("Photo") || text.contains("CHINA DAILY"))
                continue;
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

    public static List<Article> extractChinaDailyArticleMulti1(String html, ArticleLink articleLink) {
        Document document = Jsoup.parse(html);
        List<Article> articleList = extractChinaDailyArticleTitle(document, articleLink);
        articleList = extractChinaDailyArticleContent(document, articleList);
        return articleList;
    }

    private static boolean filterChinaDailyArticleMulti(String s) {
        if (s.isEmpty() || s.contains("重要词汇") || s.contains("重点词汇") || s.contains("重点词") || s.contains("中国日报网") || s.contains("重点单词：") || s.contains("一键领取入口")) {
            return true;
        }
        List<String> ss = Arrays.asList("laboratory-grown", "实验室里培养的", "reinfection", "hilltop", "irreversible", "drastic", "法郎体系"
                , "yearlong", "signatory", "francs", "flocking", "impetus", "fintech", "金融科技（financial technology）", "bourse", "infuriatingly"
                , "probiotics", "cubilose", "spearheading", "grotto", "plight", "equestrian", "vineyard", "national census", "人口普查", "全国人口普查"
                , "portrayal", "expansionary", "the Ministry of Public Security", "公安部", "dweller", "megacity", "the Civil Aviation Administration of China"
                , "中国民用航空局", "crappy", "hoard", "ply", "reluctant", "bumper", "peer-reviewed", "consecutive", "controversy", "disturbance", "subdistrict"
                , "concede", "Chrysanthemum Throne", "天皇王位", "abdicate", "bulk-buy", "批量购买", "centralized", "epidemiologic", "ruminate", "pharmaceutical"
                , "sign up", "签约雇用，签约参加", "organ donation", "器官捐献", "optimize", "thorough", "put into operation", "使生效；使运转，使开动"
                , "grassroots", "bracelet", "abuser", "marital", "contraband", "canine", "swab", "daring", "enamel", "motif", "demolish", "hail", "int. 万岁；欢迎"
                , "women's singles final", "女子单打决赛", "Grand Slam", "大满贯；满垒时的全垒打", "beat", "in collaboration with", "与…勾结；与…合作", "Captive"
                , "breeding", "phase out", "使逐步淘汰；逐渐停止", "bamboo rat", "竹鼠", "果子狸", "masked palm civet", "porcupine", "porcupine", "green bamboo snake", "竹叶青"
                , "certify", "hedgehog", "guinea pig", "豚鼠；天竺鼠", "nutria", "cobra", "复数 cobras", "Robotaxi", "自动驾驶出租车", "surpassing", "e-commerce livestreaming", "电商直播"
                , "The State Administration for Market Regulation", "市场监管总局", "penalty", "fabricate", "forging", "deceive", "implement", "standardize", "curfew"
                , "unbearable", "baseline", "prohibit", "radio frequency", "无线电频率", "[免疫] 群体免疫", "herd immunity", "[免疫] 群体免疫力", "problematic", "susceptible"
                , "frustration", "tech-savvy", "精通技术的", "consultancy", "organ", "extortion", "deliberately", "fabricated", "scenario", "property", "stipulate"
                , "fraud", "Perpetrator", "negligently", "intake", "11、criminal liability /ˈkrɪmɪnl ˌlaɪəˈbɪləti/ [法] 刑事责任", "foliage", "mimic", "life expectancy"
                , "预期寿命；平均寿命", "gauge", "the National Health Commission", "国家卫健委", "orca", "mink", "circus", "dolphinarium"
                , "过去式 prohibited过去分词 prohibited现在分词 prohibiting第三人称单数 prohibits", "resurgence", "consolidate", "aesthetic"
                , "enact", "loophole", "superjumbo", "/'sjuːpə,dʒʌmbəʊ/", "巨型喷气式飞机", "dietary habit", "饮食习惯", "sodium", "potassium", "low-potassium", "低钾"
                , "hypertension", "preface", "postscript", "equitable", "contraction", "normalize", "symbolize", "vaccinate", "livestreamer", "直播流", "endeavor"
                , "sequoia", "liquidity", "submerge", "partisan", "cash-strapped", "fundraiser", "in accordance with", "依照；与…一致", "discard", "复数 censuses"
                , "designated", "enthusiasm", "compromise", "tumultuous", "subdue", "抑制；", "减轻", "snap up", "抢购，匆匆吃下，抢先弄到手；", "锁键调节式", "trilogy"
                , "sequel", "Cyberbullying", "网上欺凌", "plugged-in", "profanity", "blur", "derogatory", "extroverted", "fell", "砍伐", "confer"
                , "过去式 conferred过去分词 conferred现在分词 conferring第三人称单数 confers", "standardized", "first - tier city", "renovate", "resuscitate", "elevation", "crescent"
                , "safe - haven", "安全港；容许幅度标准", "geopolitical", "slump", "probe", "orbital", "undergo", "influential", "pour out", "fury", "supremacy", "discourse", "ideology"
                , "splashdown", "prestigious", "honorary", "archeology", "shipwreck", "uninhabited", "mafia", "outright", "countermeasure", "casualty", "interoperability", "rebound"
                , "figurine", "uncanny", "resemblance", "unicorn", "interconnected", "territory", "albeit", "strenuously", "tailor - made", "pedestrian", "escort", "fugitive", "anti - graft"
                , "反腐败", "anti - corruption", "aspiration", "carpool", "involuntarily", "amicus", "proclamation", "completion", "backbone", "downturn", "competitiveness", "commute"
                , "logistics", "物流", "hit a record high of", "创历史新高", "orientation", "adenovirus", "in the pipeline", "在运输中；在进行中；在准备中", "nozzle", "complimentary"
                , "contagion", "sovereign", "Geopark", "Neolithic", "reservoir", "cut off", "切断；中断；使死亡；剥夺继承权", "withdrawal", "obligation", "miniature", "depict", "enthrall"
                , "mandate", "hydrological", "deterioration", "underscore", "inundation", "rain - swollen", "洪水泛滥的", "precautionary", "reiterated", "反复的", "重复的", "prelaunch"
                , "disinfection", "resilience", "coalition", "friend - of - the - court", "法庭之友", "anticipated", "output", "respectively", "falsify", "simultaneously", "cryptocurrency"
                , "solicit", "blast off", "点火起飞；发射升空（等于blast - off）", "municipal", "assessment", "in response to", "响应；回答；对…有反应", "prestigious", "layoff", "redundancy", "scrutinize"
                , "immunogenicity", "frugal", "planetary", "maneuver", "exquisite", "sundial", "degradable", "outrageous", "sabotages", "unilaterally", "heroine", "tumultuous", "fruition"
                , "amphibious", "turboprop", "doe - eyed", " (像雌鹿般) 眼睛天真无邪的 ", "sainted", "unwed", "backtrack", "factory - gate price", "出厂价", "assassination"
                , "postponement", "multi - pronged", "多方面的;多管齐下的", "incentive", "panacea", "Chancellery", "decentralized", "tremendous", "grim", "cluster outbreak"
                , "集群爆发", "contamination", "fixed - rate", "固定比率", "proactive", "inter - city", "市际的", "recognition", "drastically", "slaughterhouse", "catch-up"
                , "off - limit", "禁止的", "high - accuracy", "高精度", "cluster", "spur", "Sino - US", "contract", "stealth", "assassin", "Immaculate", "baroque", "botched", "plasma"
                , "convalescent", "inclusivity", "prioritize", "dexamethasone", "tarpaulin", "glacier", "resilient", "volatile", "premium", "dominate", " hiatus", " layoff", "procurement"
                , "spiral", "politicized", "/, haɪpɚ 'θɝmɪə/", "a cascade of 大量", "recoup", "disinfectant", "routinely", "overdrive", "shrink", "rigorous", "make-or-break", "emerging", "livestreaming"
                , "suspension", "abridged", "deem", "deportation", "disregard", "algae", "Infectious", "deliberation", "pathogen", "decouple", "patriarch", "Sanatorium", "pinnacle", "airborne", "uphold", "malicious"
                , "Calabash", "impacted", "bilateral", "extradition", "arbitrarily", "propelling", "looting", "roadside booth", "路边摊", "pedestrian", "vending", "inappropriateness", "mobilize", "rioting"
                , "reconnaissance", "undermine", "flimsy", "Blackout", "competitiveness", "asymptomatic", "anti-malaria", "抗疟疾", "protocol", "solidarity", "hinder", "prospect", "mechanism", "favorable"
                , "elongate", "cobbler", "occupancy", "spike", "coaster", "racism", "brutality", "shatter", "shockwave", "implementation", "caregiver", "aggregate"
                , "liaison", "follow-up", "unflattering", "bypass", "rigorous", "realm", "downpour", "precipitation", "brutality", "reassess", "projection", "stark", "sewage", "desulfuration"
                , "脱硫（一种化学作用）", "sweltering", "refrigerant", "non-porous", "intimacy", "craftspeople", "hue", "multiracial", "scale up", "按比例放大；按比例增加", "hospitalize", "vicinity"
                , "narrate", "extraterrestrial", "bioluminescence", "plummet", "antibody", "credential", "grassroot", "star-studded", "stepwise", "epidemiologist", "prompt", "publicize", "ratchet", "ratchet up"
                , "逐渐升高；略微调高", "launchpad", "retrieval", "demographic", "Hispanic", "highest-profile", "最受人瞩目的", "最具知名度的", "最高调的", "convene", "carry out", "baccalaureate", "eliminate", "resume"
                , "normalcy", "slew", "pent-up", "outcry", "condemn", "first-of-its-kind", "史无前例", "moderator", "indispensable", "在场的；亲身的；亲自；外貌上", "堆在…之上；使堆积在…", "尽一切办法做某事", "制裁；镇压；取缔；劈啪击下"
                , "varying", "aftermath", "rope off", "用绳索隔开；围起来", "stricken", "would-be", "silent tribute 默哀", "bionic", "outperform", "entangle", "preliminary", "incalculable", "pinky toes 小脚趾", "stabilize", "crude"
                , "sweep", "resumption", "integrated", "sex assault 性侵犯", "vector", "spearhead", "cherished", "engulf", "conceivable", "intermittently", "salute", "rampage", "cruiser", "horrific", "benchmark", "evaporation"
                , "tenure", "beleaguer", "sacrifice", "wide-field", "panoramic", "astronomical", "celestial", "practitioner", "residency", "unprecedented", "cutting-edge", "per-capita", "[统计] 人均；（拉丁）每人；按人口计算"
                , "cosmologist", "compassionate", "aviation", "refit", "toll", "death toll", "死亡人数", "scooter", "narrative", "infrared", "thermometer", "telecommuting"
                , "boastful", "furlough", "nix", "int. 没有；不行；（非正式）当心（上司来临）", "pervasive", "Psychiatry", "symptom", "bring under control", "把…控制起来", "receptor", "plenary", "customarily", "streamline"
                , "island-looping", "环岛", "unwavering", "preliminary", "plausible", "venue");
        if (ss.contains(s)) {
            return true;
        }
        if (s.contains("英 [") || s.contains("英 /") || s.contains("美 [") || s.contains("美 /") || s.contains("vt.") || s.contains("adj.") || s.contains("v.") || s.contains("vi.") || s.contains("n.") || s.contains("conj.") || s.contains("dj.")) {
            return true;
        }
        if (StringUtil.isNumber(s.charAt(0)) && s.charAt(1) == '、') {
            return true;
        }
        return false;
    }

    public static List<String> extractChinaDailyArticleMulti(String html, ArticleLink articleLink) {
        Document document = Jsoup.parse(html);
        List<String> articleList = new ArrayList<>();
        List<String> pTextList = document.select("p[data-flag]").eachText();
        List<String> textList = pTextList.stream().filter(v -> !filterChinaDailyArticleMulti(v)).collect(Collectors.toList());
        /*String text = "";
        String content = "";
        String contentNote = "";
        String title = "";
        String titleNote = "";
        boolean textFlag = false;
        int titleTicket = 0;
        for (String s : pTextList) {
            if (s.toLowerCase().contains("find more audio news") || s.toLowerCase().equals("重点单词怎么读？") || s.equals("Hi everyone, here are words you should know from today's news.)) {
                break;
            }
            if (s.contains("重要词汇") || s.contains("重点词汇") || s.contains("重点词") || s.contains("中国日报网")
                    || s.contains("英 [") || s.contains("美 [") || s.contains("vt.") || s.contains("adj.") || s.contains("v.") || s.contains("vi.") || s.contains("n.")
                    || s.equals("laboratory-grown") || s.equals("ruminate") || s.equals("实验室里培养的")
                    || s.isEmpty() || (StringUtil.isNumber(s.charAt(0)) && s.charAt(1) == '、')) {
                continue;
            }
            if (articleLink.articleIndex == 920) {
                if (s.matches("^\\d\\s/.*")) {
                    articleList.add(title + "#" + titleNote + "#" + content + "#" + contentNote);
                    content = "";
                    contentNote = "";
                    title = StringUtils.trimLeadingWhitespace(s.replaceAll("^\\d\\s/", ""));
                    titleTicket = 1;
                }
                if (titleTicket == 1) {
                    titleNote = s;
                    titleTicket--;
                }
                if (titleTicket == 0) {
                    if (StringUtil.isAlpha(s.charAt(0))) {
                        content += s;
                    }
                    if (StringUtil.isChineseByScript(s.charAt(0))) {
                        contentNote += s;
                    }
                }
            } else {
                int chineseIndex = StringUtil.indexOfChinese(s);
                if (chineseIndex != -1) {
                    if (chineseIndex < 50) {
                        if (!text.isEmpty() && !content.isEmpty() && !contentNote.isEmpty()) {
                            text = text + "#" + content + "#" + contentNote;
                            articleList.add(text);
                            text = "";
                            content = "";
                            contentNote = "";
                        }
                        title = StringUtils.trimTrailingWhitespace(StringUtils.trimLeadingWhitespace(s.substring(0, chineseIndex)));
                        titleNote = StringUtils.trimTrailingWhitespace(StringUtils.trimLeadingWhitespace(s.substring(chineseIndex)));
                        text = title + "#" + titleNote;
                    } else {
                        content += s.substring(0, chineseIndex);
                        contentNote += s.substring(chineseIndex);
                    }
                }
            }
            if (!text.isEmpty()) {
                text = text + "#" + content + "#" + contentNote;
                articleList.add(text);
                text = "";
                content = "";
                contentNote = "";
            }
        }*/
        return articleList;
    }

    private static List<Article> extractChinaDailyArticleContent(Document document, List<Article> articleList) {
        if (articleList.stream().map(v -> v.title).filter(v1 -> !(v1 == null || v1.isEmpty())).collect(Collectors.toList()).size() < 4
                || articleList.stream().map(v -> v.titleNote).filter(v1 -> !(v1 == null || v1.isEmpty())).collect(Collectors.toList()).size() < 4
                || articleList.stream().map(v -> v.titleAndNote).filter(v1 -> !(v1 == null || v1.isEmpty())).collect(Collectors.toList()).size() < 4) {
            return new ArrayList<>();
        }
        int contentTicket = 0;
        boolean isTitle = false;
        List<String> pTextList = document.select("p").eachText();
        for (int i = 0; i < pTextList.size(); i++) {
            Article currentArticle = null;
            String text = pTextList.get(i);
            String textTemp = replace(text);
            if (checkText4Content(textTemp)) {
                continue;
            }
            if (StringUtils.trimAllWhitespace(textTemp).toLowerCase().contains("findmoreaudionews")) {
                break;
            }
            currentArticle = articleList.stream().filter(v -> v.titleAndNote.contains(StringUtils.trimAllWhitespace(textTemp))).findFirst().orElse(null);
            if (currentArticle == null) {
                logger.error(InternalException.build(MATCH_ARTICLE_TITLE_4_CONTENT_FAILURE).getMessage());
            } else {
                isTitle = true;
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
                if (indexOfChinese > 0) {
                    currentArticle.content = Optional.ofNullable(currentArticle.content).orElse("") + text.substring(0, indexOfChinese);
                    currentArticle.contentNote = Optional.ofNullable(currentArticle.contentNote).orElse("") + text.substring(indexOfChinese, text.length());
                } else {
                    currentArticle.content = text;
                }
                contentTicket--;
            } else if (contentTicket == 1) {
                currentArticle.contentNote = Optional.ofNullable(currentArticle.contentNote).orElse("") + text;
                contentTicket--;
            }
        }
        if (articleList.stream().map(v -> v.content).filter(v1 -> !(v1 == null || v1.isEmpty())).collect(Collectors.toList()).size() < 4
                || articleList.stream().map(v -> v.contentNote).filter(v1 -> !(v1 == null || v1.isEmpty())).collect(Collectors.toList()).size() < 4) {
            logger.error(InternalException.build(FINAL_ARTICLE_FAILURE).getMessage());
            return new ArrayList<>();
        }
        return articleList;
    }

    private static List<Article> extractChinaDailyArticleTitle(Document document, ArticleLink articleLink) {
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
            if (checkText4Title(textTemp)) {
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
                || text.contains("China Daily")
                || text.contains("不错过世界上发生的趣事")
                || text.contains("No.")
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