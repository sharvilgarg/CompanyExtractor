package CompanyExtractor;


import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.xml.sax.SAXException;

import java.io.IOException;

public class App
{
    public static void main( String[] args ) throws
            IOException,
            SAXException,
            BoilerpipeProcessingException
    {
        PortfolioAnalyzer analyzer = new PortfolioAnalyzer();
        analyzer.addPortfolioCompany("Luxottica");

        boolean mentioned = analyzer.analyzeArticle("https://www.reuters.com/article/us-essilor-m-a-luxottica-group-idUSKBN14Z110");
        if (mentioned) {
            System.out.println("Article mentions portfolio companies");
        } else {
            System.out.println("Article does not mention portfolio companies");
        }
    }
}
