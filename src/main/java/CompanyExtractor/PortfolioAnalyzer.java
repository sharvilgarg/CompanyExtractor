package CompanyExtractor;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class PortfolioAnalyzer {
    private HashSet<String> portfolio;
    private static final String modelPath = "edu\\stanford\\nlp\\models\\pos-tagger\\english-left3words\\english-left3words-distsim.tagger";
    private MaxentTagger tagger;

    public PortfolioAnalyzer() {
        portfolio = new HashSet<String>();
        tagger = new MaxentTagger(modelPath);
    }

    public void addPortfolioCompany(String company) {
        portfolio.add(company);
    }

    public boolean analyzeArticle(String urlString) throws
            IOException,
            SAXException,
            BoilerpipeProcessingException
    {
        String articleText = PortfolioAnalyzer.extractFromUrl(urlString);
        String tagged = tagPos(articleText);
        HashSet<String> properNounsSet = PortfolioAnalyzer.extractProperNouns(tagged);
        return arePortfolioCompaniesMentioned(properNounsSet);
    }

    public boolean arePortfolioCompaniesMentioned(HashSet<String> articleProperNouns){
        return !Collections.disjoint(articleProperNouns, portfolio);
    }

    public static HashSet<String> extractProperNouns(String taggedOutput) {
        HashSet<String> propNounSet = new HashSet<String>();
        String[] split = taggedOutput.split(" ");
        List<String> propNounList = new ArrayList<String>();
        for (String token: split){
            String[] splitTokens = token.split("_");
            if(splitTokens.length < 2) {
                continue;
            }
            if(splitTokens[1].equals("NNP")){
                propNounList.add(splitTokens[0]);
            } else {
                addProperNounToSet(propNounSet, propNounList);
            }
        }
        addProperNounToSet(propNounSet, propNounList);
        return propNounSet;
    }

    private static void addProperNounToSet(HashSet<String> propNounSet, List<String> propNounList) {
        if (!propNounList.isEmpty()) {
            propNounSet.add(StringUtils.join(propNounList, " "));
            propNounList.clear();
        }
    }

    public String tagPos(String input) {
        return tagger.tagString(input);
    }

    public static String extractFromUrl(String userUrl) throws
            IOException,
            SAXException,
            BoilerpipeProcessingException  {
        final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(userUrl));
        final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
        return CommonExtractors.ARTICLE_EXTRACTOR.getText(doc);
    }
}
