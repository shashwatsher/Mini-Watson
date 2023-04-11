import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

interface StanfordLemmatizerIf{
	public String lemmatize(String documentText);
	
}
public class StanfordLemmatizer implements StanfordLemmatizerIf{
	 protected StanfordCoreNLP pipeline;

	    public StanfordLemmatizer() {
	       
	        Properties props;
	        props = new Properties();
	        props.put("annotators", "tokenize, ssplit, pos, lemma");
	        this.pipeline = new StanfordCoreNLP(props);
	    }

	    public String lemmatize(String documentText)
	    {
	        //List<String> lemmas = new LinkedList<String>();
	        StringBuilder result = new StringBuilder();

	        Annotation document = new Annotation(documentText);
	        this.pipeline.annotate(document);
	        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	        for(CoreMap sentence: sentences) {
	            
	            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	            	
	                //Retrieving Lemma and adding to final String
	            	result.append(token.get(LemmaAnnotation.class)+" ");
	           
	            }
	        }

	        return result.toString();
	    }
	}