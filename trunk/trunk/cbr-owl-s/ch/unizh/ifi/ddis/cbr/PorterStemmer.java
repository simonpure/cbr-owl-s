package ch.unizh.ifi.ddis.cbr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
  * Stemmer, implementing the Porter Stemming Algorithm
  * 
  * Taken from the OWL-MX project and slightly adapted Singelton patttern
  *
  * The Stemmer class transforms a word into its root form.  The input
  * word can be provided a character at time (by calling add()), or at once
  * by calling one of the various stem(something) methods.
  * 
  * @author Mahboob Khalid
  */

public class PorterStemmer
{
	/** The file where a list of stopwords, 1 per line, are stored */
	 protected static final String stopWordsFile = "./stopwords.txt";
	/** The number of stopwords in this file */
	 protected static final int numStopWords = 514;
    
   private HashSet stopWords = null;

   static PorterStemmer stemmer;
   
   /**
    * 
    * @return stemmer instance
    */
   public static PorterStemmer instanceOf()
   {
	   if(stemmer == null) {
		   stemmer = new PorterStemmer();
	   }
	   return stemmer;
   }

   /**
		* constructor
		*/
	  public PorterStemmer()
	  {
			loadStopWords();
	  }
   
   /** Load the stopwords from file to the hashtable where they are indexed. */
	  private void loadStopWords() 
	  {
		  // Initialize hashtable to proper size given known number of
		  // stopwords in the file and a default 75% load factor with
		  // 10 extra slots for spare room.
		  int HashMapSize = (int)(numStopWords/0.75 + 10);
		  stopWords = new HashSet(HashMapSize);
		  String line;
		  try {
			  // Open stopword file for reading
			  BufferedReader in = new BufferedReader(new FileReader(stopWordsFile));
			  // Read in stopwords, one per line, until file is empty
			  while ((line = in.readLine()) != null) {
			  // Index word into the hashtable with 
			  // the default empty string as a "dummy" value.
			  stopWords.add(line);
			  }
			  in.close();
		  }
		  catch (IOException e) {
			//System.out.println("\n[Stemmer] Could not load stopwords file: " + stopWordsFile);
			stopWords.add("a");
			stopWords.add("associates");
			stopWords.add("able");
			stopWords.add("about");
			stopWords.add("above");
			stopWords.add("according");
			stopWords.add("accordingly");
			stopWords.add("across");
			stopWords.add("actually");
			stopWords.add("after");
			stopWords.add("afterwards");
			stopWords.add("again");
			stopWords.add("against");
			stopWords.add("all");
			stopWords.add("allow");
			stopWords.add("allows");
			stopWords.add("almost");
			stopWords.add("alone");
			stopWords.add("along");
			stopWords.add("already");
			stopWords.add("also");
			stopWords.add("although");
			stopWords.add("always");
			stopWords.add("am");
			stopWords.add("among");
			stopWords.add("amongst");
			stopWords.add("an");
			stopWords.add("and");
			stopWords.add("another");
			stopWords.add("any");
			stopWords.add("anybody");
			stopWords.add("anyhow");
			stopWords.add("anyone");
			stopWords.add("anything");
			stopWords.add("anyway");
			stopWords.add("anyways");
			stopWords.add("anywhere");
			stopWords.add("apart");
			stopWords.add("appear");
			stopWords.add("appreciate");
			stopWords.add("appropriate");
			stopWords.add("are");
			stopWords.add("around");
			stopWords.add("as");
			stopWords.add("aside");
			stopWords.add("ask");
			stopWords.add("asking");
			stopWords.add("associated");
			stopWords.add("at");
			stopWords.add("available");
			stopWords.add("away");
			stopWords.add("awfully");
			stopWords.add("be");
			stopWords.add("became");
			stopWords.add("because");
			stopWords.add("become");
			stopWords.add("becomes");
			stopWords.add("becoming");
			stopWords.add("been");
			stopWords.add("before");
			stopWords.add("beforehand");
			stopWords.add("behind");
			stopWords.add("being");
			stopWords.add("believe");
			stopWords.add("below");
			stopWords.add("beside");
			stopWords.add("besides");
			stopWords.add("best");
			stopWords.add("better");
			stopWords.add("between");
			stopWords.add("beyond");
			stopWords.add("both");
			stopWords.add("brief");
			stopWords.add("but");
			stopWords.add("by");
			stopWords.add("came");
			stopWords.add("can");
			stopWords.add("cannot");
			stopWords.add("cant");
			stopWords.add("cause");
			stopWords.add("causes");
			stopWords.add("certain");
			stopWords.add("certainly");
			stopWords.add("changes");
			stopWords.add("clearly");
			stopWords.add("com");
			stopWords.add("come");
			stopWords.add("comes");
			stopWords.add("concerning");
			stopWords.add("consequently");
			stopWords.add("consider");
			stopWords.add("considering");
			stopWords.add("contain");
			stopWords.add("containing");
			stopWords.add("contains");
			stopWords.add("corresponding");
			stopWords.add("could");
			stopWords.add("course");
			stopWords.add("currently");
			stopWords.add("definitely");
			stopWords.add("described");
			stopWords.add("despite");
			stopWords.add("did");
			stopWords.add("different");
			stopWords.add("do");
			stopWords.add("does");
			stopWords.add("doing");
			stopWords.add("done");
			stopWords.add("down");
			stopWords.add("downwards");
			stopWords.add("during");
			stopWords.add("each");
			stopWords.add("edu");
			stopWords.add("eg");
			stopWords.add("eight");
			stopWords.add("either");
			stopWords.add("else");
			stopWords.add("elsewhere");
			stopWords.add("enough");
			stopWords.add("entirely");
			stopWords.add("especially");
			stopWords.add("et");
			stopWords.add("etc");
			stopWords.add("even");
			stopWords.add("ever");
			stopWords.add("every");
			stopWords.add("everybody");
			stopWords.add("everyone");
			stopWords.add("everything");
			stopWords.add("everywhere");
			stopWords.add("ex");
			stopWords.add("exactly");
			stopWords.add("example");
			stopWords.add("except");
			stopWords.add("far");
			stopWords.add("few");
			stopWords.add("fifth");
			stopWords.add("first");
			stopWords.add("five");
			stopWords.add("followed");
			stopWords.add("following");
			stopWords.add("follows");
			stopWords.add("for");
			stopWords.add("former");
			stopWords.add("formerly");
			stopWords.add("forth");
			stopWords.add("four");
			stopWords.add("from");
			stopWords.add("further");
			stopWords.add("furthermore");
			stopWords.add("get");
			stopWords.add("gets");
			stopWords.add("getting");
			stopWords.add("given");
			stopWords.add("gives");
			stopWords.add("go");
			stopWords.add("goes");
			stopWords.add("going");
			stopWords.add("gone");
			stopWords.add("got");
			stopWords.add("gotten");
			stopWords.add("greetings");
			stopWords.add("had");
			stopWords.add("happens");
			stopWords.add("hardly");
			stopWords.add("has");
			stopWords.add("have");
			stopWords.add("having");
			stopWords.add("he");
			stopWords.add("hello");
			stopWords.add("help");
			stopWords.add("hence");
			stopWords.add("her");
			stopWords.add("here");
			stopWords.add("hereafter");
			stopWords.add("hereby");
			stopWords.add("herein");
			stopWords.add("hereupon");
			stopWords.add("hers");
			stopWords.add("herself");
			stopWords.add("hi");
			stopWords.add("him");
			stopWords.add("himself");
			stopWords.add("his");
			stopWords.add("hither");
			stopWords.add("hopefully");
			stopWords.add("how");
			stopWords.add("howbeit");
			stopWords.add("however");
			stopWords.add("i");
			stopWords.add("ie");
			stopWords.add("if");
			stopWords.add("ignored");
			stopWords.add("immediate");
			stopWords.add("in");
			stopWords.add("inasmuch");
			stopWords.add("inc");
			stopWords.add("indeed");
			stopWords.add("indicate");
			stopWords.add("indicated");
			stopWords.add("indicates");
			stopWords.add("inner");
			stopWords.add("insofar");
			stopWords.add("instead");
			stopWords.add("into");
			stopWords.add("inward");
			stopWords.add("is");
			stopWords.add("it");
			stopWords.add("its");
			stopWords.add("itself");
			stopWords.add("j");
			stopWords.add("just");
			stopWords.add("k");
			stopWords.add("keep");
			stopWords.add("keeps");
			stopWords.add("kept");
			stopWords.add("know");
			stopWords.add("knows");
			stopWords.add("known");
			stopWords.add("last");
			stopWords.add("lately");
			stopWords.add("later");
			stopWords.add("latter");
			stopWords.add("latterly");
			stopWords.add("least");
			stopWords.add("less");
			stopWords.add("lest");
			stopWords.add("let");
			stopWords.add("like");
			stopWords.add("liked");
			stopWords.add("likely");
			stopWords.add("little");
			stopWords.add("look");
			stopWords.add("looking");
			stopWords.add("looks");
			stopWords.add("ltd");
			stopWords.add("mainly");
			stopWords.add("many");
			stopWords.add("may");
			stopWords.add("maybe");
			stopWords.add("me");
			stopWords.add("mean");
			stopWords.add("meanwhile");
			stopWords.add("merely");
			stopWords.add("might");
			stopWords.add("more");
			stopWords.add("moreover");
			stopWords.add("most");
			stopWords.add("mostly");
			stopWords.add("much");
			stopWords.add("must");
			stopWords.add("my");
			stopWords.add("myself");
			stopWords.add("name");
			stopWords.add("namely");
			stopWords.add("nd");
			stopWords.add("near");
			stopWords.add("nearly");
			stopWords.add("necessary");
			stopWords.add("need");
			stopWords.add("needs");
			stopWords.add("neither");
			stopWords.add("never");
			stopWords.add("nevertheless");
			stopWords.add("new");
			stopWords.add("next");
			stopWords.add("nine");
			stopWords.add("no");
			stopWords.add("nobody");
			stopWords.add("non");
			stopWords.add("none");
			stopWords.add("noone");
			stopWords.add("nor");
			stopWords.add("normally");
			stopWords.add("not");
			stopWords.add("nothing");
			stopWords.add("novel");
			stopWords.add("now");
			stopWords.add("nowhere");
			stopWords.add("obviously");
			stopWords.add("of");
			stopWords.add("off");
			stopWords.add("often");
			stopWords.add("oh");
			stopWords.add("ok");
			stopWords.add("okay");
			stopWords.add("old");
			stopWords.add("on");
			stopWords.add("once");
			stopWords.add("one");
			stopWords.add("ones");
			stopWords.add("only");
			stopWords.add("onto");
			stopWords.add("or");
			stopWords.add("other");
			stopWords.add("others");
			stopWords.add("otherwise");
			stopWords.add("ought");
			stopWords.add("our");
			stopWords.add("ours");
			stopWords.add("ourselves");
			stopWords.add("out");
			stopWords.add("outside");
			stopWords.add("over");
			stopWords.add("overall");
			stopWords.add("own");
			stopWords.add("particular");
			stopWords.add("particularly");
			stopWords.add("per");
			stopWords.add("perhaps");
			stopWords.add("placed");
			stopWords.add("please");
			stopWords.add("plus");
			stopWords.add("possible");
			stopWords.add("presumably");
			stopWords.add("probably");
			stopWords.add("provides");
			stopWords.add("que");
			stopWords.add("quite");
			stopWords.add("qv");
			stopWords.add("rather");
			stopWords.add("rd");
			stopWords.add("re");
			stopWords.add("really");
			stopWords.add("reasonably");
			stopWords.add("regarding");
			stopWords.add("regardless");
			stopWords.add("regards");
			stopWords.add("relatively");
			stopWords.add("respectively");
			stopWords.add("right");
			stopWords.add("said");
			stopWords.add("same");
			stopWords.add("saw");
			stopWords.add("say");
			stopWords.add("saying");
			stopWords.add("says");
			stopWords.add("second");
			stopWords.add("secondly");
			stopWords.add("see");
			stopWords.add("seeing");
			stopWords.add("seem");
			stopWords.add("seemed");
			stopWords.add("seeming");
			stopWords.add("seems");
			stopWords.add("seen");
			stopWords.add("self");
			stopWords.add("selves");
			stopWords.add("sensible");
			stopWords.add("sent");
			stopWords.add("serious");
			stopWords.add("seriously");
			stopWords.add("seven");
			stopWords.add("several");
			stopWords.add("shall");
			stopWords.add("she");
			stopWords.add("should");
			stopWords.add("since");
			stopWords.add("six");
			stopWords.add("so");
			stopWords.add("some");
			stopWords.add("somebody");
			stopWords.add("somehow");
			stopWords.add("someone");
			stopWords.add("something");
			stopWords.add("sometime");
			stopWords.add("sometimes");
			stopWords.add("somewhat");
			stopWords.add("somewhere");
			stopWords.add("soon");
			stopWords.add("sorry");
			stopWords.add("specified");
			stopWords.add("specify");
			stopWords.add("specifying");
			stopWords.add("still");
			stopWords.add("sub");
			stopWords.add("such");
			stopWords.add("sup");
			stopWords.add("sure");
			stopWords.add("take");
			stopWords.add("taken");
			stopWords.add("tell");
			stopWords.add("tends");
			stopWords.add("th");
			stopWords.add("than");
			stopWords.add("thank");
			stopWords.add("thanks");
			stopWords.add("thanx");
			stopWords.add("that");
			stopWords.add("thats");
			stopWords.add("the");
			stopWords.add("their");
			stopWords.add("theirs");
			stopWords.add("them");
			stopWords.add("themselves");
			stopWords.add("then");
			stopWords.add("thence");
			stopWords.add("there");
			stopWords.add("thereafter");
			stopWords.add("thereby");
			stopWords.add("therefore");
			stopWords.add("therein");
			stopWords.add("theres");
			stopWords.add("thereupon");
			stopWords.add("these");
			stopWords.add("they");
			stopWords.add("think");
			stopWords.add("third");
			stopWords.add("this");
			stopWords.add("thorough");
			stopWords.add("thoroughly");
			stopWords.add("those");
			stopWords.add("though");
			stopWords.add("three");
			stopWords.add("through");
			stopWords.add("throughout");
			stopWords.add("thru");
			stopWords.add("thus");
			stopWords.add("to");
			stopWords.add("together");
			stopWords.add("too");
			stopWords.add("took");
			stopWords.add("toward");
			stopWords.add("towards");
			stopWords.add("tried");
			stopWords.add("tries");
			stopWords.add("truly");
			stopWords.add("try");
			stopWords.add("trying");
			stopWords.add("twice");
			stopWords.add("two");
			stopWords.add("un");
			stopWords.add("under");
			stopWords.add("unfortunately");
			stopWords.add("unless");
			stopWords.add("unlikely");
			stopWords.add("until");
			stopWords.add("unto");
			stopWords.add("up");
			stopWords.add("upon");
			stopWords.add("us");
			stopWords.add("use");
			stopWords.add("used");
			stopWords.add("useful");
			stopWords.add("uses");
			stopWords.add("using");
			stopWords.add("usually");
			stopWords.add("uucp");
			stopWords.add("value");
			stopWords.add("various");
			stopWords.add("very");
			stopWords.add("via");
			stopWords.add("viz");
			stopWords.add("vs");
			stopWords.add("want");
			stopWords.add("wants");
			stopWords.add("was");
			stopWords.add("way");
			stopWords.add("we");
			stopWords.add("welcome");
			stopWords.add("well");
			stopWords.add("went");
			stopWords.add("were");
			stopWords.add("what");
			stopWords.add("whatever");
			stopWords.add("when");
			stopWords.add("whence");
			stopWords.add("whenever");
			stopWords.add("where");
			stopWords.add("whereafter");
			stopWords.add("whereas");
			stopWords.add("whereby");
			stopWords.add("wherein");
			stopWords.add("whereupon");
			stopWords.add("wherever");
			stopWords.add("whether");
			stopWords.add("which");
			stopWords.add("while");
			stopWords.add("whither");
			stopWords.add("who");
			stopWords.add("whoever");
			stopWords.add("whole");
			stopWords.add("whom");
			stopWords.add("whose");
			stopWords.add("why");
			stopWords.add("will");
			stopWords.add("willing");
			stopWords.add("wish");
			stopWords.add("with");
			stopWords.add("within");
			stopWords.add("without");
			stopWords.add("wonder");
			stopWords.add("would");
			stopWords.add("would");
			stopWords.add("yes");
			stopWords.add("yet");
			stopWords.add("you");
			stopWords.add("your");
			stopWords.add("yours");
			stopWords.add("yourself");
			stopWords.add("yourselves");
			stopWords.add("zero");
			stopWords.add("q");
			stopWords.add("nbsp");
			stopWords.add("http");
			stopWords.add("www");
			stopWords.add("writeln");
			stopWords.add("pdf");
			stopWords.add("html");
//  System.exit(1);
		  }
	  }//end loasStopWords
	  
	  /**
	   * this function also check stopword and stemming if the given flag is true
	   * @param str
	   * @return stemmed string
	   */
	  public String stem(String str){
	      if (str==null)
	          return "";
				str = str.toLowerCase();
	  		String token = null;
				if(stopWords.contains(str))
				{
					token = null;
				}
				else
				{
					token = stripAffixes(str);
					if (stopWords.contains(token))
							token = null;
				}
	  		return token;
	  }//end stem
	  
	private String Clean( String str ) {
	    if ( (str==null) || (str.length()<=0))
	        return "";
	    
			 int last = str.length();
			// Character ch = new Character( str.charAt(0) );
     
		 String temp = "";

		 for ( int i=0; i < last; i++ ) {
			 if ( Character.isLetterOrDigit( str.charAt(i) ) )
				temp += str.charAt(i);
		 }
		 return temp;
	  } //clean
 
	  private boolean hasSuffix( String word, String suffix, String stem ) {

		 String tmp = "";

		 if ( word.length() <= suffix.length() )
			return false;
		 if (suffix.length() > 1) 
			if ( word.charAt( word.length()-2 ) != suffix.charAt( suffix.length()-2 ) )
			   return false;
  
		 stem = "";

		 for ( int i=0; i<word.length()-suffix.length(); i++ )
			 stem += word.charAt( i );
		 tmp = stem;

		 for ( int i=0; i<suffix.length(); i++ )
			 tmp += suffix.charAt( i );

		 if ( tmp.compareTo( word ) == 0 )
			return true;
		 else
			return false;
	  }

	  private boolean vowel( char ch, char prev ) {
		 switch ( ch ) {
			case 'a': case 'e': case 'i': case 'o': case 'u': 
			  return true;
			case 'y': {

			  switch ( prev ) {
				case 'a': case 'e': case 'i': case 'o': case 'u': 
				  return false;

				default: 
				  return true;
			  }
			}
        
			default : 
			  return false;
		 }
	  }

	  private int measure( String stem ) {
    
		int i=0, count = 0;
		int length = stem.length();

		while ( i < length ) {
		   for ( ; i < length ; i++ ) {
			   if ( i > 0 ) {
				  if ( vowel(stem.charAt(i),stem.charAt(i-1)) )
					 break;
			   }
			   else {  
				  if ( vowel(stem.charAt(i),'a') )
					break; 
			   }
		   }

		   for ( i++ ; i < length ; i++ ) {
			   if ( i > 0 ) {
				  if ( !vowel(stem.charAt(i),stem.charAt(i-1)) )
					  break;
				  }
			   else {  
				  if ( !vowel(stem.charAt(i),'?') )
					 break;
			   }
		   } 
		  if ( i < length ) {
			 count++;
			 i++;
		  }
		} //while
    
		return(count);
	  }

	  private boolean containsVowel( String word ) {

		 for (int i=0 ; i < word.length(); i++ )
			 if ( i > 0 ) {
				if ( vowel(word.charAt(i),word.charAt(i-1)) )
				   return true;
			 }
			 else {  
				if ( vowel(word.charAt(0),'a') )
				   return true;
			 }
        
		 return false;
	  }

	  private boolean cvc( String str ) {
		 int length=str.length();

		 if ( length < 3 )
			return false;
    
		 if ( (!vowel(str.charAt(length-1),str.charAt(length-2)) )
			&& (str.charAt(length-1) != 'w') && (str.charAt(length-1) != 'x') && (str.charAt(length-1) != 'y')
			&& (vowel(str.charAt(length-2),str.charAt(length-3))) ) {

			if (length == 3) {
			   if (!vowel(str.charAt(0),'?')) 
				  return true;
			   else
				  return false;
			}
			else {
			   if (!vowel(str.charAt(length-3),str.charAt(length-4)) ) 
				  return true; 
			   else
				  return false;
			} 
		 }   
  
		 return false;
	  }

	  private String step1( String str ) {
 
		 String stem = "";

		 if ( str.charAt( str.length()-1 ) == 's' ) {
			if ( (hasSuffix( str, "sses", stem )) || (hasSuffix( str, "ies", stem)) ){
			   String tmp = "";
			   for (int i=0; i<str.length()-2; i++)
				   tmp += str.charAt(i);
			   str = tmp;
			}
			else {
			   if ( ( str.length() == 1 ) && ( str.charAt(str.length()-1) == 's' ) ) {
				  str = "";
				  return str;
			   }
			   if ( str.charAt( str.length()-2 ) != 's' ) {
				  String tmp = "";
				  for (int i=0; i<str.length()-1; i++)
					  tmp += str.charAt(i);
				  str = tmp;
			   }
			}  
		 }

		 if ( hasSuffix( str,"eed",stem ) ) {
			   if ( measure( stem ) > 0 ) {
				  String tmp = "";
				  for (int i=0; i<str.length()-1; i++)
					  tmp += str.charAt( i );
				  str = tmp;
			   }
		 }
		 else {  
			if (  (hasSuffix( str,"ed",stem )) || (hasSuffix( str,"ing",stem )) ) { 
			   if (containsVowel( stem ))  {

				  String tmp = "";
				  for ( int i = 0; i < stem.length(); i++)
					  tmp += str.charAt( i );
				  str = tmp;
				  if ( str.length() == 1 )
					 return str;

				  if ( ( hasSuffix( str,"at",stem) ) || ( hasSuffix( str,"bl",stem ) ) || ( hasSuffix( str,"iz",stem) ) ) {
					 str += "e";
           
				  }
				  else {   
					 int length = str.length(); 
					 if ( (str.charAt(length-1) == str.charAt(length-2)) 
						&& (str.charAt(length-1) != 'l') && (str.charAt(length-1) != 's') && (str.charAt(length-1) != 'z') ) {
                     
						tmp = "";
						for (int i=0; i<str.length()-1; i++)
							tmp += str.charAt(i);
						str = tmp;
					 }
					 else
						if ( measure( str ) == 1 ) {
						   if ( cvc(str) ) 
							  str += "e";
						}
				  }
			   }
			}
		 }

		 if ( hasSuffix(str,"y",stem) ) 
			if ( containsVowel( stem ) ) {
			   String tmp = "";
			   for (int i=0; i<str.length()-1; i++ )
				   tmp += str.charAt(i);
			   str = tmp + "i";
			}
		 return str;  
	  }

	  private String step2( String str ) {

		 String[][] suffixes = { { "ational", "ate" },
										{ "tional",  "tion" },
										{ "enci",    "ence" },
										{ "anci",    "ance" },
										{ "izer",    "ize" },
										{ "iser",    "ize" },
										{ "abli",    "able" },
										{ "alli",    "al" },
										{ "entli",   "ent" },
										{ "eli",     "e" },
										{ "ousli",   "ous" },
										{ "ization", "ize" },
										{ "isation", "ize" },
										{ "ation",   "ate" },
										{ "ator",    "ate" },
										{ "alism",   "al" },
										{ "iveness", "ive" },
										{ "fulness", "ful" },
										{ "ousness", "ous" },
										{ "aliti",   "al" },
										{ "iviti",   "ive" },
										{ "biliti",  "ble" }};
		 String stem = "";

     
		 for ( int index = 0 ; index < suffixes.length; index++ ) {
			 if ( hasSuffix ( str, suffixes[index][0], stem ) ) {
				if ( measure ( stem ) > 0 ) {
				   str = stem + suffixes[index][1];
				   return str;
				}
			 }
		 }

		 return str;
	  }

	  private String step3( String str ) {

			String[][] suffixes = { { "icate", "ic" },
										   { "ative", "" },
										   { "alize", "al" },
										   { "alise", "al" },
										   { "iciti", "ic" },
										   { "ical",  "ic" },
										   { "ful",   "" },
										   { "ness",  "" }};
			String stem = "";

			for ( int index = 0 ; index<suffixes.length; index++ ) {
				if ( hasSuffix ( str, suffixes[index][0], stem ))
				   if ( measure ( stem ) > 0 ) {
					  str = stem + suffixes[index][1];
					  return str;
				   }
			}
			return str;
	  }

	  private String step4( String str ) {
        
		 String[] suffixes = { "al", "ance", "ence", "er", "ic", "able", "ible", "ant", "ement", "ment", "ent", "sion", "tion",
							   "ou", "ism", "ate", "iti", "ous", "ive", "ize", "ise"};
     
		 String stem = "";
        
		 for ( int index = 0 ; index<suffixes.length; index++ ) {
			 if ( hasSuffix ( str, suffixes[index], stem ) ) {
           
				if ( measure ( stem ) > 1 ) {
				   str = stem;
				   return str;
				}
			 }
		 }
		 return str;
	  }

	  private String step5( String str ) {

		 if ( str.charAt(str.length()-1) == 'e' ) { 
			if ( measure(str) > 1 ) {/* measure(str)==measure(stem) if ends in vowel */
			   String tmp = "";
			   for ( int i=0; i<str.length()-1; i++ ) 
				   tmp += str.charAt( i );
			   str = tmp;
			}
			else
			   if ( measure(str) == 1 ) {
				  String stem = "";
				  for ( int i=0; i<str.length()-1; i++ ) 
					  stem += str.charAt( i );

				  if ( !cvc(stem) )
					 str = stem;
			   }
		 }
     
		 if ( str.length() == 1 )
			return str;
		 if ( (str.charAt(str.length()-1) == 'l') && (str.charAt(str.length()-2) == 'l') && (measure(str) > 1) )
			if ( measure(str) > 1 ) {/* measure(str)==measure(stem) if ends in vowel */
			   String tmp = "";
			   for ( int i=0; i<str.length()-1; i++ ) 
				   tmp += str.charAt( i );
			   str = tmp;
			} 
		 return str;
	  }

	  private String stripPrefixes ( String str) {

		 String[] prefixes = { "kilo", "micro", "milli", "intra", "ultra", "mega", "nano", "pico", "pseudo"};

		 int last = prefixes.length;
		 for ( int i=0 ; i<last; i++ ) {
			 if ( str.startsWith( prefixes[i] ) ) {
				String temp = "";
				for ( int j=0 ; j< str.length()-prefixes[i].length(); j++ )
					temp += str.charAt( j+prefixes[i].length() );
				return temp;
			 }
		 }
     
		 return str;
	  }

	  private String stripSuffixes( String str ) {

		 str = step1( str );
		 if ( str.length() >= 1 )
			str = step2( str );
		 if ( str.length() >= 1 )
			str = step3( str );
		 if ( str.length() >= 1 )
			str = step4( str );
		 if ( str.length() >= 1 )
			str = step5( str );
 
		 return str; 
	  }

		/**  Takes a String as input and returns its stem as a String.*/
	  protected String stripAffixes( String str ) {

			str = Clean(str);
  
		if (( str != "" ) && (str.length() > 2)) {
		   str = stripPrefixes(str);

		   if (str != "" ) 
			  str = stripSuffixes(str);
		}   
		return str;
	  } //stripAffixes	  
}//end Stemmer
