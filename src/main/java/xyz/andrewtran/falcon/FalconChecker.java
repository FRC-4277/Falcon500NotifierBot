package xyz.andrewtran.falcon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Set;

public class FalconChecker {
    private static final int TIMEOUT_MS = 10_000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";
    private static final String CTRE_URL = "http://www.ctr-electronics.com/talon-fx.html";
    private static final String VEX_URL = "https://www.vexrobotics.com/217-6515.html";

    public static boolean checkCTRE() throws Exception {
        Document document = Jsoup.connect(CTRE_URL)
            .userAgent(USER_AGENT)
            .timeout(TIMEOUT_MS)
            .get();
        Element availabilityParagraph = document.select("p.availability").first();
        if (availabilityParagraph == null) {
            throw new Exception("Could not find availability paragraph for CTRE");
        }
        Set<String> classes = availabilityParagraph.classNames();
        return !classes.contains("out-of-stock") || classes.contains("in-stock");
    }

    public static boolean checkVEX() throws Exception {
        Document document = Jsoup.connect(VEX_URL)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .get();
        Element availabilityDiv = document.select("div.stock").first();
        if (availabilityDiv == null) {
            throw new Exception("Could not find stock div for VEX");
        }
        Set<String> classes = availabilityDiv.classNames();
        return !classes.contains("unavailable") || classes.contains("available");
    }
}
