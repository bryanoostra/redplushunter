/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:27 $
 * @since version 0
 */

// Last modification by: $Author: swartjes $
// $Log: XMLTokenizer.java,v $
// Revision 1.1  2006/05/24 09:00:27  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:11  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.26  2005/10/20 09:54:57  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.3  2005/09/18 21:47:06  zwiers
// no message
//

package parlevink.xml;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import parlevink.util.*;
import parlevink.util.Console;

/**
 * A scanner of XML input streams.
 * <p>
 * An XML scanner enforces only the simple lexical well-formedness constraints of XML 1.0.
 * An XML stream is a sequence of lexical tokens. These lexical tokens have an external
 * (string) representation, and an internal representation.
 * The recognized lexical tokens, and their external representations are:
 * <ul>
 * <li> STag       :  Start tags, of the form              &lt;identifier   ... &gt;
 *                    Here, the "dots" indicate a sequence of Attributes,
                      externally represented in the form AttributeName = AttributeValue   </li>
 * <li> AttrName   :  Attribute names, of the form         identifier                </li>
 * <li> AttrValue  :  Attribute values, of the form  "string", where string must not contain " chars. </li>
 * <li> ETag       :  End tags, of the form                </identifier>                              </li>
 * <li> CharData   :  Character data, in the form of character strings, see below.                    </li>
 * <li> PI         :  Processing instruction, of the form  <?string?>                                 </li>
 * <li> Decl       :  Declarations, of the form            <!string>                                 
 *    (Special cases are:   
 *    <ul>
 *             <li>    comments of the form                <!--string-->, where string must not contain -- </li>
 *             <li>    document type declarations:         <!DOCTYPE= ..... >                              </li>
 *    </ul> </li>
 * <li> EndOfData        :  EndOfData is a pseudo token that signals that no more tokens are available.                   </li>
 * </ul>
 *
 * identifiers consist exclusively of the following characters: a-zA-Z0-9-_.: 
 * and must start with one of the characters a-zA-Z_
 * <p> 
 * A start tag immediately followed by the corresponding end tag can be represented
 * externally as an "empty tag" of the form:         <identifier/>
 * <p>
 * CharData, or "content" is considered to be "parsed character data", which means the following:
 * <ul>
 * <li> The external representation is assumed not to contain < characters </li>
 * <li> & characters are assumed to start an entity reference of the form: 
 *    &lt;   &gt;   &amp;   &quot;   &apos; </li>
 * </ul>
 * Such entity references are translated to their internal representation:
 *     <      >      &       "        '
 * 3) The XML standard assumes also that the character sequence ]]> does not occur in character data.
 * Note that the characters > " ' are not forbidden in character data. However, the easiest way to
 * translate arbitrary character strings into legal XML character data is to do the following:
 * <ul>
 * <li>   replace < by &amp;lt;                                                       </li>
 * <li>   replace > by &amp;gt;    (This automatically takes care of ]]> sequences)   </li>
 * <li>   replace & by &amp;amp;                                                      </li>
 * </ul>
 * <p>
 * The regular expression that describes the possible streams of lexical tokens is:
 *   ( (Stag (AttrName AttrValue)*) | ETag | CharData | PI | Decl )* EndOfData
 * <p>
 
 **********************************************************************************************
 * The scanner can work with two different interfaces:
 * <ul>
 * <li> the nextToken/hasMoreTokens interface, comparable to a StringTokenizer. 
 * hasMoreTokens() indicates whether more lexical tokens  are available; if true,
 * nextToken() can be called, and yields an integer code denoting the token category,
 * like XMLTokenizer.CharData, XMLTokenizer.STag, etc. 
 * Depending on the category, other token attributes are available,
 * such as tagName, attributeName, attributeValue etc.
 * These attributes must be retrieved by calling getA() methods, where "A" denotes the attribute.
 * </li>
 * <li> the interface consisting of atX(), getA(), takeX() methods.
 * Here the idea is that an atX() method checks whether the scanner is at some particular token X.
 * If this is the case, attributes can be retrieved by calling getA() methods, like above.
 * The takeX() methods consumes an X token, which means that they conceptually advance to the next token
 * if the scanner is actually at an X token, but throw an exception if this is not the case.
 * The various takeX() methods return a String value with an attribute of the token, like the values
 * returned by the getA() methods.
 * </li>
 * </ul>
 * 
 * In principle, the two styles should not be mixed. 
 * [ Note: It is possible to combine hasMoreTokens() with the atX() methods, but not with the
 * takeX() methods. In this case, the hasMoreTokens() has the effect of consuming the current token.]
 *
 * <p>
 * Whitespace is always ignored inside markup.  whitespace CharData can be handled in 
 * various ways:
 * <p>
 * The property trimWS determines whether the whitespace at the beginning and end 
 * of CharData should be removed.
 * <p>
 * The property preserveIgnorableWS determines whether "ignorable whitespace" should be ignored.
 * if false (the default) then whitespace in between markup is ignored.
 * if preserveIgnorableWS == true, then all whitespace is returned as CharData, including
 * possible whitespace at the start or end of the document.
 */
public class XMLTokenizer {

   /*
    * initial default values. 
    */
   public static final boolean SKIPDOCTYPE           = true;
   public static final boolean SKIPCOMMENT           = true;
   public static final boolean SKIPPI                = true;
   public static final boolean SKIPCLOSESTAGS        = false;
   public static final boolean COMPLETEATTRIBUTES    = true;
   public static final boolean COMPLETESTAGS         = true;
   public static final boolean PRESERVE_IGNORABLE_WS = false; 
   public static final boolean TRIM_WS               = false;
   public static final boolean CHECKWELLFORMEDNES    = false;
   public static final boolean PROPERTYLOCK          = false;
   public static final boolean LOG                   = false;
   public static final int     LOGBUFSIZE            = 2000;   // initial size of log StringBuffer
   public static final int     DISCARDED_TOKEN_LIMIT = 5;      // max number of discarded tokens shown i  one recoverX call
        

   /**
    * Create a XMLTokenizer for the specified Reader.
    * (Note that in general a BufferedReader is preferred.)
    */
   public XMLTokenizer(Reader in) {
   /* all other constructors rely on this one */
      setDefaultModes();
      setReader(in);
   }

   /**
    * Create a XMLTokenizer for the specified InputStream.
    */
   public XMLTokenizer(InputStream in) {
      this(new BufferedReader(new InputStreamReader(in)));
   }


   /**
    * Create XMLTokenizer for a null Reader.
    */
   public XMLTokenizer() {
      this((Reader)null);  
   }
   
   /**
    * Like XMLTokenizer(Reader), with a Reader constructed from
    * a StringReader for xmlString. No file name or URL is defined, 
    * so error messages cannot refer to these attributes.
    */
   public XMLTokenizer(String xmlString) {
      //this(new BufferedReader(new StringReader(xmlString)));// actually slightly worse performance
      this(new StringReader(xmlString));        
   }

   /**
    * Create a XMLTokenizer for a (buffered) Reader constructed
    * from the specified File. 
    * Also, the base URL is set to the file URL derived from the File. 
    */
   public XMLTokenizer(File inFile) throws FileNotFoundException {
      this((Reader)null);
      setFile(inFile);
   }
 

   
   /**
    * Create a XMLTokenizer for a (buffered) Reader constructed
    * from the specified URL.
    */
   public XMLTokenizer(URL url) { //throws IOException {
      this((Reader)null);
      try {
         setURL(url);
      } catch (IOException e) {
         throw new RuntimeException("Could not set XMLTokenizer URL: " + e);
      }
   }
     

   /**
    * Sets the current File, and opens a Reader for the File.
    * Also, the base URL is set to the file URL derived from the File. 
    */
   public void setFile(File inFile) throws FileNotFoundException {
      setReader(new BufferedReader(new FileReader(inFile)));
      this.file = inFile;
      try {
         setBaseURL(inFile.toURL());
      } catch (MalformedURLException me) {
          Console.println("Malformed file URL: " + me); 
      } 
   }      


   /**
    * like setURL, in that it defines the current URL, but does not
    * attempt to open a new Reader for the URL.
    * Therefore, it is allowed to use an URL that specifies a 
    * directory name, ending with a slash character, rather than
    * a file name.
    * This URL will be effectively used as the ``base URL''
    * when setURL is called with a relative URL specification.
    */
   public void setBaseURL(URL url) {
      this.url = url;
   }

   /**
    * like setURL, in that it defines the current URL, 
    * but unlike setURL, setBaseURL does not attempt 
    * to open a new Reader for the URL.
    * Therefore, it is allowed to use an URL that specifies a 
    * directory name, ending with a slash character, rather than
    * a file name.
    * This URL will be effectively used as the ``base URL''
    * when setURL is called next, with a relative URL specification.
    * The base url specification can be absolute, or relative
    * to the current URL.
    */
   public void setBaseURL(String urlSpec) {
      try {
         URL newUrl = new URL(url, urlSpec);
         if (newUrl != null) {
            url = newUrl;
         }
      } catch (java.net.MalformedURLException e) {
         Console.println("Malformed URL: " + urlSpec);
         throw getXMLScanException("Malformed URL: " + urlSpec);
      }
   }


   /**
    * sets the current URL, and opens a Reader for the new URL. 
    * The current URL is ignored, that is, it is not regarded
    * as a ``base URL''.
    */
   public void setURL(URL url) throws IOException {
      if (url != null) {
         InputStream is = url.openStream();
         Reader ir = new BufferedReader(new InputStreamReader(is));
         this.url = url;
         setReader(ir);
      } else {
         throw new XMLScanException("XMLTokenizer: null URL specified");  
      }           
   }
   
   /**
    * starts reading from a new URL. the urlSpec String
    * is regarded as relative to the current URL, unless
    * it has the form of an absolute URL. 
    */
   public void setURL(String urlSpec) throws IOException {
      //Console.println("XMLTokenizer: setURL: " + urlSpec);
      try {
         URL newUrl = new URL(url, urlSpec);
         if (newUrl != null) {
            //Console.println("XMLTokenizer: new URL set: " + newUrl);
            InputStream is = newUrl.openStream();
            Reader ir = new BufferedReader(new InputStreamReader(is));
            url = newUrl;
            setReader(ir);
         }
      } catch (java.net.MalformedURLException e) {
         Console.println("Malformed URL: " + urlSpec);
         throw getXMLScanException("Malformed URL: " + urlSpec);
      }
   }

   /**
    * returns the current URL, which could be null,
    * for instance in the case of a XMLTokenizer constructed
    * from a String. XMLTokenizers constructed from a File
    * object do have a defined (file) URL.
    */
   public URL getURL() {
       return url;  
   }

   /**
    * replaces the Reader that this tokenizer should process.
    * The previous Reader is returned.
    */
   public final Reader setReader(Reader in)  {
       Reader oldreader = this.in;
       this.in = in;
       initState();
       return oldreader;
   }

   /**
    * Gets the Reader that this tokenizer
    * is currently using. 
    */
   public final Reader getReader()  {
       return in;
   }


   /**
    * Pushes the current XMLTokenizer status on the stack, and then
    * starts reading from the newly specified Reader.
    */
   public final void pushReader(Reader in)  {
       pushState();
       setReader(in);
   }

   /**
    * Pushes the current XMLTokenizer status on the stack, and then
    * starts reading from the Raeder specified in URL form.
    * The URL specification can be relative, in which case 
    * the current base URL is taken into account, or it can be an absolute URL.
    * If boolean value returned is true if the reader for the specified url could
    * be opened, otherwise false is retuned.
    */
   public final void pushReader(String urlSpec) throws IOException {
      pushState();
      try {
         setURL(urlSpec);
      } catch (IOException ie) {
          popState();
          throw(ie);
      }
   }

   /**
    * assuming that a previous call to pushReader has been made, this call
    * will restore the previous reader, and the state of the XMLTokenizer
    * to the state when the pushReader call was made.
    * Note that popReader calls are implied (made automatically) when
    * the pop-on-ENDOFDOCUMENT mode is true.
    */
   public final void popReader() {
       popState(); 
   } 


   private static boolean includeTokenLine = true;
   private static boolean includeTokenCharPos = true;
   private static boolean includeLine = true;
   private static boolean includeCharPos = true;
   private static boolean includeFile = true;
   private static boolean includeURL = true;
   
    
    
   // mask values for including parts of error messages in XMLScanExceptions.
   public static final int ERRORTOKENLINE = 1;
   public static final int ERRORTOKENPOS = 2;
   public static final int ERRORLINE = 4;
   public static final int ERRORPOS = 8;
   public static final int ERRORFILE = 16;
   public static final int ERRORURL = 32;
   public static final int ERRORFULL = ERRORTOKENLINE | ERRORTOKENPOS | ERRORLINE | ERRORPOS | ERRORFILE | ERRORURL;
   public static final int ERRORFILELINE = ERRORLINE  | ERRORFILE | ERRORURL;
   public static final int NOERRORPOSITION = 0;
   
   private int defaultScanExceptionMode = ERRORFULL;
   
   
   /**
    * determines what to include in SMXScanExceptions, generated
    * by getXMLScanException(). ``mode'' must be an OR of a selection of
    * the following masks:
    *    
    * ERRORTOKENLINE (line of error token)
    * ERRORTOKENPOS (starting position of the error token) 
    * ERRORLINE (line where error was detected)
    * ERRORPOS (character position where error was detected)
    * ERRORFILE (file name, if available)
    * ERRORURL (URL, if available, but not if file name has been included already)
    * The default mode is ERRORFULL, which is simply the ``or''
    * of all masks. 
    * Also available is ERRORFILELINE, which equals ERRORFILE|ERRORURL|ERRORLINE,
    * and NOERRORPOSITION, which is simply 0; 
    */
   public void setXMLScanExceptionMode(int mode) {
      if (mode > ERRORFULL) {
         Console.println("XMLTokenizer: Illegal error mode"); 
      } else {
         defaultScanExceptionMode = mode;
      }
   }
   
   /**
    * returns an XMLScanException, containing the message String, but also
    * including positional information, depending on available information, and
    * settings. In principle, the file or url (if available), the token line
    * and character position, and the curent line and position are included.
    */
   public XMLScanException getXMLScanException(String message) {
      return getXMLScanException(message, defaultScanExceptionMode);
   }


   /**
    * returns an XMLScanException, containing the message String, but also
    * including positional information, depending on available information, and
    * settings. In principle, the file or url (if available), the token line
    * and character position, and the curent line and position are included.
    */
   public XMLScanException getXMLScanException(String message, int mode) {
//      StringBuffer msg = new StringBuffer(40);
//      if (message != null) msg.append(message);
//      if ((mode&ERRORFILE) != 0 && file != null) {
//         msg.append(", file: ");
//         msg.append(file.getName());
//      } else if ((mode&ERRORURL) != 0 && url != null) {
//          msg.append(", URL: ");
//          msg.append(url.toString());  
//      }
//      if ((mode&ERRORTOKENLINE) != 0) {
//         msg.append(", token line: ");
//         msg.append(getTokenLine());
//      }
//      if ((mode&ERRORTOKENPOS) != 0) {
//         msg.append(", position: ");
//         msg.append(getTokenCharPos());
//      }        
//      if ((mode&ERRORLINE) != 0) {
//         msg.append(", error line: ");
//         msg.append(getLine());
//      }
//      if ((mode&ERRORPOS) != 0) {
//         msg.append(", position: ");
//         msg.append(getCharPos());
//      }        
      return new XMLScanException(getErrorMessage(message, mode));
              
   }

   /**
    * returns an error mesage String, containing the message String, but also
    * including positional information, depending on available information, and
    * settings. In principle, the file or url (if available), the token line
    * and character position, and the curent line and position are included.
    */
   public String getErrorMessage(String message) {
      return getErrorMessage(message, defaultScanExceptionMode);
   }


   /**
    * returns an error mesage String, containing the message String, but also
    * including positional information, depending on available information, and
    * settings. In principle, the file or url (if available), the token line
    * and character position, and the curent line and position are included.
    */
   public String getErrorMessage(String message, int mode) {
      StringBuffer msg = new StringBuffer(40);
      if (message != null) msg.append(message);
      if ((mode&ERRORFILE) != 0 && file != null) {
         msg.append(", file: ");
         msg.append(file.getName());
      } else if ((mode&ERRORURL) != 0 && url != null) {
          msg.append(", URL: ");
          msg.append(url.toString());  
      }
      if ((mode&ERRORTOKENLINE) != 0) {
         msg.append(", token line: ");
         msg.append(getTokenLine());
      }
      if ((mode&ERRORTOKENPOS) != 0) {
         msg.append(", position: ");
         msg.append(getTokenCharPos());
      }        
      if ((mode&ERRORLINE) != 0) {
         msg.append(", error line: ");
         msg.append(getLine());
      }
      if ((mode&ERRORPOS) != 0) {
         msg.append(", position: ");
         msg.append(getCharPos());
      }        
      return msg.toString();      
   } 

   /**
    * returns the current line number;
    * line counts start at 1.
    */
   public final int getLine() {
     return line;
   }

   /**
    * returns the current character position within the current line.
    * character counts start at 1 for the first character within the line.
    * After a \n character, the character position is 0.
    *
    */
   public final int getCharPos() {
     return charPos;
   } 

   /**
    * returns the starting line number of the start of the the current token.
    * line counting starts at 1.
    */
   public final int getTokenLine() {
     return tokenLine;
   }
 
   /**
    * returns the character position of the start of the current token.
    */
   public final int getTokenCharPos() {
     return tokenCharPos;
   } 


   /*
    * return the current token, i.e. the token returned by the last nextToken call
    *
    */
   public final int currentToken() {
      return token;
   }

   /*
    * return sthe current token, as String i.e. the token returned by the last nextToken call
    *
    */
   public final String currentTokenString() {
      if (token == STag || token == ETag) {
         return tokenString(token) + " " + tagName;
      } else {
         return tokenString(token);
      }
   }



   /**
    * discards input until an STag with specified tag name is reached,
    * the end of document is reached, or an IOException is thrown.
    * In the first situation, true is returned, otherwise false.
    */
   public final boolean recoverAtSTag(String stag)  {
      return recoverAtSTag(stag, DISCARDED_TOKEN_LIMIT);
   }

   /**
    * discards input until an STag with specified tag name is reached,
    * the end of document is reached, or an IOException is thrown.
    * In the first situation, true is returned, otherwise false.
    * When showDiscardedTokens is true, discarded tokens are shown on the Console,
    * up to a certain limit ( DISCARDED_TOKEN_LIMIT)
    */
   public final boolean recoverAtSTag(String stag, int tokenLimit)  {
      int skipCount = 0;
      try {
         while (  ! (atEndOfDocument() || atSTag(stag) )) {
            nextToken();
            if (skipCount < tokenLimit) {
               if (token == STag || token == ETag) {
                   Console.println("Skipping " + tokenString(token) + " token: " + tagName);
               } else {
                   Console.println("Skipping " + tokenString(token) + "token");
               } 
            } else if (skipCount == tokenLimit) {
               Console.println("Skipping tokens ....");
            }
            skipCount++;
         }  
         return atSTag(stag);
      } catch (IOException e) {
         Console.println("Exception while recovering for " + stag + " STag: " + e);
         return false;
      }
   }

   /**
    * discards input until an ETag with specified tag name is reached,
    * the end of document is reached, or an IOException is thrown.
    * In the first situation, the ETag is consumed, and true is returned,
    * otherwise false is returned.
    */
   public final boolean recoverAfterETag(String etag) {
      return recoverAfterETag(etag, DISCARDED_TOKEN_LIMIT);
   }


   /**
    * discards input until an ETag with specified tag name is reached,
    * the end of document is reached, or an IOException is thrown.
    * In the first situation, the ETag is consumed, and true is returned,
    * otherwise false is returned.
    * When showDiscardedTokens is true, discarded tokens are shown on the Console,
    * up to a certain limit ( DISCARDED_TOKEN_LIMIT)
    */
   public final boolean recoverAfterETag(String etag, int tokenLimit)  {
      //Console.println("recoverAfterETag " + etag);
      int skipCount = 0;
      try {
         while (  ! (atEndOfDocument() || atETag(etag) )) {
            nextToken();
            if (skipCount < tokenLimit) {
               if (token == STag || token == ETag) {
                   Console.println("Skipping " + tokenString(token) + " token: " + tagName);
               } else {
                   Console.println("Skipping " + tokenString(token) + "token");
               } 
            } else if (skipCount == tokenLimit) {
               Console.println("Skipping tokens ....");
            }
            skipCount++;
         }  
         if (atETag(etag)) {
             takeETag();
             return true;  
         } else {
            return false;
         }
      } catch (IOException e) {
         Console.println("Exception while recovering for " + etag + " ETag: " + e);
         return false;
      }
   }

   /**
    * assuming that we are at a STag, skips the remainder of the current tag, up to and including
    * the matching ETag. The current token must be STag, OpenSTag, or CloseSTag.
    * The XML part that is skipped must be well formed, implying that STags and ETags should
    * be properly matched. An Exception is thrown if the EndOfDocument is reached while skipping.
    */
   public final void skipTag() throws IOException {
      if (! (atSTag() || atCloseSTag()) ) { // atOpenSTag implies atSTag
            throw new IllegalStateException("The XMLTokenizer was not at an STag at start of skipTag action(" + line + ", " + charPos + ")");
        }
      String skipTagName = getTagName();
      boolean saveCheckWellformedness = checkWellformednes;
      if (! checkWellformednes) {
         checkWellformednes = true; // temporary check wellformedness
         pushTag(skipTagName);
         
      } 
      int skipStackSize = tagStack.size() - 1; //tagStackSize value immediately before and after the tag to be skipped.
      do {
         do { // skip tokens until ETag is reached (or until an -illegal- EndOfDocument is reached)
            nextToken();
            if (atEndOfDocument() ) {
               Console.println("EndOfDocument reached while skipping tag: " + skipTagName);
               token = EndOfDocument;
               checkWellformednes = saveCheckWellformedness; // restore old state.
               return;
            }
         } while ( ! atETag() );    
      } while ( tagStack.size() > skipStackSize ||  ! getTagName().equals(skipTagName) ); 
      tokenConsumed = true; // consume the last ETag
      checkWellformednes = saveCheckWellformedness; // restore old state.
   }


   
   /**
    * denotes whether there are more tokens available, excluding the EndOfDocument token.
    */
   public final boolean hasMoreTokens() throws IOException  {
      // In order to determine whether there are more tokens, 
      // we are forced to read the next token internally.
      // This is recorded in the state by setting tokenBuffered to true.
      // Also, the current token must be considered "consumed",
      // since it is no longer possible to inspect tagName or attributes.
      if (! tokenBuffered) {
         nextToken(); // 
         tokenConsumed = true;
         tokenBuffered = true; 
      }
      return (token != EndOfDocument); // test the buffered token.
   }
  
   /**
    * Called to move the tokenizer to a next token. Effect depends on
    * current token and settings of several settings.
    * 
    */
   public final int nextToken() throws IOException, XMLScanException {
      //Console.println("nextToken...");
      try {
         while (true) {
            tokenConsumed = false;
            if (tokenBuffered) {
               tokenBuffered = false;
               //Console.println("buffered token");
               return token;
            }
            switch (tokenMode) {
               case CharData_MODE: 
                    parseCharData();
                    break;
               case STagTail_MODE: // expect attribute name,  '>', or "/>"
                    int tailToken = parseSTagTailElement();
                     if (skipCloseSTags && tailToken == CloseSTag ) {
                         parseCharData();
                     } else  {
                         token = tailToken;
                     }
                     break;
                case PendingETag_MODE: // empty tag, return ETag token.
                     token = ETag;
                     tokenMode = CharData_MODE;
                     if (checkWellformednes) popTag();
                     // tagName is still valid
                     if (etagFence) {
                        if (tagName.equals(fenceTagName)) {
                           throw getXMLScanException("XMLTokenizer cannot pass fence for </" + fenceTagName + "> tag");  
                         }   
                     };
                     break;
                case AttributeValue_MODE: // expect '=' followed by attribute value
                     parseAttributeValue();
                     token = AttributeValue;
                     tokenMode = STagTail_MODE;
                     break;
                case EndOfDocument_MODE: // EndOfDocument reached before
                     token = EndOfDocument;
                     break;
                default: 
                    throw getXMLScanException ("unknown XMLTokenizer processing mode");
            }
            if (token == EndOfDocument && popOnEndOfDocument && tokenizerStateStack.size() > 0) {
               popState();
            } else {
               return token;
            }
         }
      } catch (RuntimeException re) { // including XMLScanExceptions
//           if (debug) {
//              token = ErrorToken;
//              throw re;
//           } else {
              //Console.println(re.toString());
              token = ErrorToken;
              throw re;
//              return token;
//           }
        }
    }


   /*
    * parses CharData, and detects whether other lexical tokens are included
    * within this CharData.
    */
   private final int parseCharData() throws IOException  {   
       //Console.println("parseCharData");    
       clearBuffer(charDataBuffer);
       setTokenPos();
       while (ci != EOS) {  // iterate until non-ignorable token is returned, or EOS is reached
         if (ci == CONSUMED) nextParsedChar();
          while (isSpaceChar()) {
             //System.out.println("space");
             charDataBuffer.append(ch);
             nextParsedChar();
          }         
          // use ci, not ch, for classification; ch could be '<' because of &lt; pattern in input stream)
          if (ci == '<') { // STag, ETag, PI, or decl
              if (preserveIgnorableWS && charDataBuffer.length() > 0) {
                 token = CharData;
                 return token;  // tokenMode remains CharData_MODE          
              }
              int markup = parseMarkup();
              if (markup > 0) {
                  return markup;
              } else {
                  // continue with next iteration of  "while (ci != EOS)" loop;
                  setTokenPos();
              }
          } else if (ci == EOS) { // trailing blank spaces
             if (preserveIgnorableWS && charDataBuffer.length() > 0) {
                 token = CharData;
                 return token;  // tokenMode remains CharData_MODE          
              }
             token = EndOfDocument;
             tokenMode = EndOfDocument_MODE;
             setTokenPos();
             if (checkWellformednes) {
               checkEmptyTagStack();
             }
             return token;          
          } else { // proper CharData, not ignorable
              while (ci != '<' && ci != EOS) {
                 charDataBuffer.append(ch);
                 nextParsedChar(); 
              }
              token = CharData;
              // tokenMode remains CharData_MODE
              return token;                 
          }
       } // quit loop because of EOS char (immediately following previous token): end-of-data reached.
       //System.out.println("END-OF_DATA");
       token = EndOfDocument;
       tokenMode = EndOfDocument_MODE;
       setTokenPos();
       if (checkWellformednes) {
         checkEmptyTagStack();
       }
       return token;    
   }

   /*
    * parses XML markup, where "markup" if everything starting with a '<' character.
    * precondition: ci = '<', tokenMode = CharData mode.
    */
   private final int parseMarkup() throws IOException {
      setTokenPos();
      nextChar(); // expect a '!', a '?', a '/', or nameStartChar.
      //System.out.println("next char: " + ((char)ci));
      if (ci == '!') { // declaration or comment or CDSect
         //System.out.println("declaration");
         return parseDeclaration();
         //int declState = parseDeclaration(); // return value < 0 if decl is to be ignored.
         //if (declState >= 0) return declState;
         // else: ignorable declaration or comment.
         //else return -1;
      } else if (ci == '?') { // PI
         //System.out.println("PI");
         int piState = parsePI();   // returns -1 if PI's must be ignored.
         if (piState >= 0) return piState;  // return PI, unless PI's are to be ignored.
         // else: ignorable PI.
         else return -1;
      } else if (ci == '/') { // ETag of the form </tagName>
           return parseETag();
      } else if (isNameStartChar()) { // STag of form <tagName ....  >, possibly including attributes
           return parseSTag();
      }   
      return -1;
   }

   private final int parseETag() throws IOException {
      clearBuffer(tagNameBuffer);
      nextChar(); // consume '/' char.  assume next char is a legal start of tag name.
      while (isNameChar()) {
      	  tagNameBuffer.append((char)ci);
      	  nextChar();
      }
      if (ci != '>') throw getXMLScanException("\'>\' character at end of ETag expected, instead of '" + ((char)ci) + "'");
      ci = CONSUMED;                                       
      token = ETag;
      // tokenMode remains CharData_MODE
      tagName = tagNameBuffer.toString();
      if (checkWellformednes) popTag(tagName);
      if (etagFence) {
         if (tagName.equals(fenceTagName)) {
             throw getXMLScanException("XMLTokenizer cannot pass fence for </" + fenceTagName + "> tag");  
         }   
      };
      return token;
   }

   /* parse STags, either the whole STag, or just the OpenSTag part,
    * depending on the STag mode.
    * pre: current ci following the '<' satisfies isStartNameChar(), tokenMode = CharData_MODE. 
    */
   private final int parseSTag() throws IOException {
      clearBuffer(tagNameBuffer);
      while (isNameChar()) {
      	  tagNameBuffer.append((char)ci);
      	  nextChar();
      }
      tagName = tagNameBuffer.toString();
      if (checkWellformednes) pushTag(tagName);
      if (! completeSTags ) {
          token = OpenSTag;
          tokenMode = STagTail_MODE;
          return token;
      }
      // else: parse remainder of STag
      return parseSTagTail();
   }


   /*
    * parses the remainder of an STag, as a whole.
    * Attributes are collected in the "attributes" HashMap, rather than as separate
    * AttributeNames and AttributeValue. 
    * When the STag is terminated by means of the "/>" sequence, a pending ETag
    * is registered, by setting tokenMode to PendingETag_MODE.
    */
   private final int parseSTagTail() throws IOException {
      attributes.clear();
      skipSpaceChars();
      while (ci != '>' && ci != EOS) {
         if (ci == '/') { // empty content tag, set tokenMode for pending ETag.
            nextChar();
            if (ci != '>') throw getXMLScanException("\'>\' character after \'/\' expected instead of '" + (char)ci + "'");                                          
            tokenMode = PendingETag_MODE;
         } else {
           if (! isNameStartChar() ) throw getXMLScanException("Attribute name expected");         
           parseAttribute(); 
           attributes.put(attributeName, attributeValueBuffer.toString());
           skipSpaceChars();
         }   
      }
      if (ci != '>') throw getXMLScanException("\'>\' expected at end of STag");
      ci = CONSUMED;
      token = STag;
      if (tokenMode != PendingETag_MODE) tokenMode = CharData_MODE;
      //System.out.println("parseSTagTail, token = " + token);
      return token;
   }
 
   /* 
    * parses elements that occur as part of the tail of an STag, that is, 
    * the part following the openSTag part. 
    * The tail itself consists of zero or more attribute-value pairs, followed by a '>' char,
    * or followed by "/>". In the latter case, an "empty content tag" is being used,
    * an STag token is returned, and an ETag token remains pending, by setting tokenMode = PendingETag_MODE. 
    */
   private final int parseSTagTailElement() throws IOException {
       //System.out.println("STagTail_MODE");
       skipSpaceChars();
       // check for end of STag:   (">")
       if ( ci == '>' ) { // Close STag
          ci = CONSUMED;
          token = CloseSTag;
          tokenMode = CharData_MODE;
          return token;         // when skipCloseSTags == true, the caller will suppress this token.        	
       } 
       // check for possible empty contents tag: ("/>")
       if ( ci == '/') { // empty content tag, return Close STag now, set tokenMode for pending ETag
          //System.out.println("empty content tag");
          nextChar();
          //System.out.println("next char:" + (char)ci);
          if (ci != '>') throw getXMLScanException("\'>\' character after \'/\' expected instead of '" + (char)ci + "'");
          ci = CONSUMED;
          token = CloseSTag;
          tokenMode = PendingETag_MODE;
          // tagName is still valid
          return token;
       } 
       // check for new attribute:
       if (! isNameStartChar() ) throw getXMLScanException("Attribute name expected");
       if (completeAttributes) {
           parseAttribute();
           token = Attribute;
           // tokenMode remains STagTail mode
           return token;
       } else {
           parseAttributeName();
           token = AttributeName;
           tokenMode = AttributeValue_MODE;
           return token;
      }
   }


   /* parses a complete attribute-name-value pair.
    * does not return a token, nor does it set the tokenMode.
    * only the attributeName and the attributeValueBuffer are set, and the attribute characters are consumed.
    */
   private final void parseAttribute() throws IOException {
       clearBuffer(attributeNameBuffer);
       clearBuffer(attributeValueBuffer);
       while ( isNameChar() ) {
          attributeNameBuffer.append((char) ci);
          nextChar();	
       }
       attributeName = attributeNameBuffer.toString(); 
      skipSpaceChars();
      if (ci != '=') throw getXMLScanException("\'=\' character expected in attribute instead of '" + (char)ci + "'");
      nextChar();
      parseString(attributeValueBuffer);
   }

   /* parse attribute name.
    * does not return a token, nor does it set the tokenMode.
    * only the attributeName is set, and the name characters are consumed.
    */
   private final void parseAttributeName() throws IOException {
       clearBuffer(attributeNameBuffer);
       while ( isNameChar() ) {
          attributeNameBuffer.append((char) ci);
          nextChar();	
       }
       attributeName = attributeNameBuffer.toString(); 
   }

   /* parse attribute value part of form:  = "value"   or:  = 'value' 
    * does not return a token, nor does it set the tokenMode.
    * only the attributeValueBuffer is filled, and all characters up to
    * and including the closing " or ' char are consumed.
    */
   private final void parseAttributeValue() throws IOException {
      skipSpaceChars();
      if (ci != '=') throw getXMLScanException("\'=\' character expected in attribute instead of '" + (char)ci + "'");
      nextChar();
      clearBuffer(attributeValueBuffer);
      if (parseString(attributeValueBuffer) < 0) 
         throw getXMLScanException("\' or \" expected at start of attribute value instead of '" + (char)ci + "'");;      
   }


   /*
    * parses strings of the form "..(chars except ")..." or '..(chars except ')...',
    * possibly preceded by blank space.
    * Note that such strings can contain parsed chardata, so e.g. &quot is allowed
    * inside a '...' type of string.
    * The result is appended to buf.
    * An XMLScanException is thrown if EOS is reached.
    * When no string was found, -1 is returned, else the length of the string is returned.
    */
   private final int parseString(StringBuffer buf) throws IOException {

      // switch to parsed character data within the string
      // the end is indicated by ci == '"' or ci == '\''.
      // Note that ch can be '"' or '\'' if an entity reference of the &quot;
      // or &apos; is present in the input stream.
      skipSpaceChars();
      if (ci != '"' && ci != '\'') return -1;
      boolean aposmode = (ci=='\'');
      nextParsedChar();
      int len = 0;
      while ((aposmode || ci != '"') && (! aposmode || ci != '\'') && ci != EOS) {
         buf.append(ch);
         len++;
         nextParsedChar();
      }
      if (ci == EOS) throw getXMLScanException("missing \" or \': end-of-data reached");
      //else, if ci == '"' or ci == '\'',  
      nextChar();    
      return len;
   }

    /*
     * should take care of "declarations", including comment en doctypes.
     * returns token > 0 if relevant token, or -1 if declaration must be ignored.
     * IMplicit assumption: after '<!' a valid known declaration type MUST follow.
     */
    private final int parseDeclaration() throws IOException {
        nextChar(); // remove the '!' char
        if (ci == '-') { // comment
            return parseComment();       
        } else if (ci == 'D') { 
            return parseDoctype();
        } else if (ci == '[') {   //CData:  <![CDATA[ ... ]]>
            return parseCDSect();
        } else { //  some other declaration
             throw getXMLScanException("Unknown declaration type");
        }       
    }

    /*
     * parses (remainder of) a CDATA section.
     * ND: At the moment, this method contains the implicit assumption that a valid 
     * declaration MUST follow after '<!' 
     * assume that ci contains the first '[' char of the "<![CDATA[" opening sequence.
     */
    private final int parseCDSect() throws IOException {
        if (nextChar() != 'C') throw getXMLScanException("Wrong format at CDATA section" ); // start removing 'CDATA[' 
        if (nextChar() != 'D') throw getXMLScanException("Wrong format at CDATA section" );
        if (nextChar() != 'A') throw getXMLScanException("Wrong format at CDATA section" );
        if (nextChar() != 'T') throw getXMLScanException("Wrong format at CDATA section" );
        if (nextChar() != 'A') throw getXMLScanException("Wrong format at CDATA section" );
        if (nextChar() != '[') throw getXMLScanException("Wrong format at CDATA section" );
        //clear buffer
        cDataBuffer = new StringBuffer(100);
        //Start reading content until ]]>
        nextChar(); //read first CDATA char
        boolean endCData = false;
        while (!endCData) {
            if (ci != ']' && ci != EOS) {
               cDataBuffer.append((char)ci);
               nextChar();
            } else {
               if (ci != EOS)    //if ci == EOS, the last clause will return end of CData
                  nextChar();
               if (ci != ']' && ci != EOS) {
                  cDataBuffer.append(']');
                  cDataBuffer.append((char)ci);
                  nextChar();
               } else {
                  if (ci != EOS)    //if ci == EOS, the last clause will return end of CData
                     nextChar();
                  while (ci == ']' && ci != EOS) {
                     cDataBuffer.append(']');
                     nextChar();
                  }
                  if (ci != '>' && ci != EOS) {
                     cDataBuffer.append(']');
                     cDataBuffer.append(']');
                     cDataBuffer.append((char)ci);
                     nextChar();
                  } else {
                     if (ci == EOS) {
                        throw getXMLScanException("Wrong format at CDATA section: unexpected EOS" );                     
                     }
                     //end of CDATA
                     endCData = true;
                     ci = CONSUMED;
                     token = CDSect;
                     return token;
                  }
               }
            }
        }
        throw getXMLScanException("'Impossible' situation in CDATA section: ran out of loops" );                         
    }
   
   
    /*
     * parses (remainder of) a comment declaration.
     * assume that ci contains the first '-' char of the "<!--" opening sequence.
     */
    private final int parseComment() throws IOException {
        //System.out.println("comment");
        if (nextChar() != '-') return -1; // remove first '-', check for second '-'
        nextChar(); // read first comment char. 
        // read comment until "--" is encountered.
        if (!skipComment) clearBuffer(commentDataBuffer);
        //System.out.println("First comment char:" + ((char) ci));
        while (ci != '-' && ci != EOS) { // read until second '-' encountered
           while (ci != '-' && ci != EOS) { // read until first '-' encountered
              //System.out.println("comment char:" + (char)ci);
              if (!skipComment) commentDataBuffer.append((char)ci);
              nextChar();
           } // '-' encountered, potential end of comment
           nextChar(); // if it's a '-', it's the end of comment, else the '-' belongs to the commentDataBuffer
           if (!skipComment && ci != '-' && ci != EOS) commentDataBuffer.append('-');           
        } 
        if (ci == '-') nextChar();
        // end of comment reached, consume the expected '>' char, but retain a possible EOS
        if (ci == '>') 
           ci = CONSUMED;
        else {
           throw getXMLScanException("'>' expected at end of XML comment instead of '" + (char)ci + "'"); 
        }
        if (skipComment) {
           //System.out.println("parseDeclaration: skip comment, return -1");
           return -1;
        } else {
           token = Comment;
           return token;
        }      
    }

    /*
     * skips (remainder of) a DOCTYPE declaration.
     * assume that ci contains the first 'D' char of the "<!DOCTYPE" opening sequence.
     * skips upto and including closing '>' char, taking nested "<..>" pairs into account.
     * currently, cannot cope with internal comments or  entity declarations
     * that contain improperly paired "," or ">" chars.
     */ 
   private final void skipDoctype() throws IOException {
      int brackLevel = 1; // counting the '<' in the opening sequence "<!DOCTYPE"
      // read unparsed chardata until brackLevel reaches 0 or EOS is reached.
      while (brackLevel > 0 && ci != EOS) {
         nextChar(); 
         //System.out.println("brackLevel = " + brackLevel + " skip " + (char)ci);
         if (ci == '<') brackLevel++;
         if (ci == '>') brackLevel--;
      }
      if (ci == '>') ci = CONSUMED;
    
   }
      
    /*
     * parses (remainder of) a DOCTYPE declaration.
     * assume that ci contains the first 'D' char of the "<!DOCTYPE" opening sequence.
     */
    private final int parseDoctype() throws IOException {
       checkSequence("DOCTYPE");
       if (skipDoctype) {
          skipDoctype();
          return -1;
       }
       //System.out.println("doctype declaration");
       if (doctypeName == null ) {
          buf = new StringBuffer(20);
          //pubidLiteral = new StringBuffer
          //systemLiteral;   
          //doctypeDataBuffer = new StringBuffer(50);
       } else {
          throw getXMLScanException("redeclaration of DOCTYPE not allowed"); 
       }
       skipSpaceChars();
       if (  ! isNameStartChar()) {
          throw getXMLScanException("illegal character at start of DOCTYPE name: "+ (char)ci); 
       }
       while (isNameChar()) {
      	  buf.append((char)ci);
      	  nextChar();
       }
       doctypeName = buf.toString();
       skipSpaceChars();
       if (ci == 'S') { // SYSTEM ExternalId
          checkSequence("SYSTEM");
          clearBuffer(buf);
          parseString(buf);
          systemLiteral = buf.toString();;
       } else if (ci == 'P') { // PUBLIC ExternalId
          checkSequence("PUBLIC");
          clearBuffer(buf);
          parseString(buf);
          pubidLiteral = buf.toString();;        
          clearBuffer(buf);
          parseString(buf);
          systemLiteral = buf.toString();; 
       }
       
       if (ci == '>') { 
       } else if (ci == '[') {
          throw getXMLScanException("\"[....]\" inside DOCTYPE not supported");
       }
       skipSpaceChars();
       if (ci == '>') {
          ci = CONSUMED;
       } else {
          throw getXMLScanException("unexpected character at end of DOCTYPE declaration"); 
       }
       token = Doctype;
       return token;     
    }     
    
    
   /*
    * checks and consumes the sequence of characters seq
    * assumes that ci is CONSUMED or else contains the first character
    * of seq. Will leave the first character after seq in ci.
    */
   private final void checkSequence(String seq) throws IOException {
      if (ci == CONSUMED) nextChar();
      for (int i=0; i < seq.length(); i++) {
          if (ci != seq.charAt(i)) throw getXMLScanException(seq.charAt(i) + " character expected in " + seq + " instead of '" + (char)ci + "'");
          nextChar();
      }
   }
        
    /*
     * should take care of PI's (processing instructions.
     * The PI section includes chars up to and including "?>"
     * The text of the PI must not be "parsed".
     */
    private final int parsePI() throws IOException {
       //System.out.println("parsePI");
       nextChar(); // remove the '?' char.
       
       clearBuffer(piDataBuffer); 
       while (ci != '>' && ci != EOS) { // read until "?>" or EOS is encountered
          while (ci != '?' && ci != EOS) {
             piDataBuffer.append((char)ci); 
             nextChar();
          }  // reached a '?' char, potential end of PI
          nextChar(); // if it's a '>' or EOS, it's the end of this PI, else the '?' belongs to the piDataBuffer
          if (ci != '>' && ci != EOS) piDataBuffer.append('?');
       }
       if (ci == '>') ci = CONSUMED;
       if (skipPI) {
         return -1;
       } else {
          token = PI;
          return token;
       }
    }

    

    /*
     * sets a fence for ETags: an ETag with the specified tagName will cause an Exception
     * if it would be returned as next token.
     */
    public final String setFenceTagName(String etagName) {
        String oldvalue = fenceTagName;
        if (! etagName.equals(oldvalue)) checkPropertyLock();
        fenceTagName = etagName; 
        return oldvalue;      
    }
 
    /*
     * turns the ETag fence on or off;
     */
    public final void setEtagFence(boolean mode) {
       etagFence = mode;
    } 
    
    /**
     * used to lock the possiblity of setting properties.
     * Once locked, properties like skipComment, completeSTags etc cannot be (re)set any 
     * longer. This includes setting propertyLock, so once locked, unlocking is
     * not possible.
     */
    public final void setPropertyLock() {
        propertyLock = true; 
    }

    /*
     * checks whether properties can be set, and throws an IllegalArgumentException
     * when this is not allowed, due to a propertyLock
     */
    private final void checkPropertyLock() {
       if (propertyLock) {
           throw new IllegalArgumentException("XMLTokenizer cannot (re)set properties in locked mode");
       }
    }
    
    
    /**
     * Used to set how whitespaces in character content should be handled.
     */
    public final boolean setPreserveIgnorableWS(boolean preserveMode) {
        boolean oldvalue = preserveIgnorableWS;
        if (oldvalue != preserveMode) checkPropertyLock();
        preserveIgnorableWS = preserveMode;
        return oldvalue;
    }

    /**
     * Used to set how whitespaces on the left of the character content should be handled.
     */
    public final boolean setTrimWS(boolean trimmed) {
        boolean oldvalue = trimWS;
        if (oldvalue != trimmed) checkPropertyLock();
        trimWS = trimmed;
        return oldvalue;
    }


    /**
     * Used to set if PIDATA should be skipped.
     */
    public final boolean setSkipPI(boolean skipped) {
        boolean oldvalue = skipPI;
        if (oldvalue != skipped) checkPropertyLock();
        skipPI = skipped;
        return oldvalue;
    }
        
    /**
     * Used to set if COMMENTS should be skipped.
     */
    public final boolean setSkipComment(boolean skipped) {
        boolean oldvalue = skipComment;
        if (oldvalue != skipped) checkPropertyLock();
        skipComment = skipped;
        return oldvalue;
    }
        
    /**
     * Used to set if DOCTYPE should be skipped.
     */
    public final boolean setSkipDoctype(boolean skipped) {
        boolean oldvalue = skipDoctype;
        if (oldvalue != skipped) checkPropertyLock();
        skipDoctype = skipped;
        return oldvalue;
    }

    /**
     * Used to set if CloseSTag tokens should be skipped.
     */
    public boolean setSkipCloseSTags(boolean skipped) {
        boolean oldvalue = skipCloseSTags;
        if (oldvalue != skipped) checkPropertyLock();
        skipCloseSTags = skipped;
        return oldvalue;
    }

    /**
     * Used to set if attributes should be returned as a whole, using Attribute tokens,
     * or whether they should be broken into AttributeName and AttributeValue tokens.
     */
    public final boolean setCompleteAttributes(boolean complete) {
        boolean oldvalue = completeAttributes;
        if (oldvalue != complete) checkPropertyLock();
        completeAttributes = complete;
        return oldvalue;
    }

    /**
     * returns the current status of STag handling.
     */
    public final boolean  getCompleteSTags() {
        return completeSTags;
    }

    /**
     * Used to set whether STags should be returned as a whole, using STag tokens,
     * or whether they should be broken into OpenSTag, AttributeName and AttributeValue
     * and CloseSTag tokens.
     */
    public final boolean setCompleteSTags(boolean complete) {
        boolean oldvalue = completeSTags;
        if (oldvalue != complete) checkPropertyLock();
        completeSTags = complete;
        return oldvalue;
    }

    /**
     * returns the current status of wellformednes checking.
     */
    public final boolean getCheckWellformednes() {
        return checkWellformednes;
    }    

    /**
     * Used to set whether wellformednes should be checked.
     * "wellformed" means that STags and ETags are properly matched and nested.
     */
    public final boolean setCheckWellformednes(boolean checked) {
        boolean oldvalue = checkWellformednes;
        if (oldvalue != checked) checkPropertyLock();
        checkWellformednes = checked;
        return oldvalue;
    }    

    /**
     * Used to set logging mode
     */
    public final boolean setLogging(boolean logging) {
        boolean oldvalue = log;
        //if (oldvalue != log) checkPropertyLock();
        log = logging;
        if (log && logBuffer == null) {
            logBuffer = new StringBuffer(LOGBUFSIZE);
        }
        return oldvalue;
    }

   /**
    *
    */
    public final void clearLog() {
      logBuffer.delete(0, logBuffer.length());
   }

    /**
     *
     */
     public final String getLog() {
        if (logBuffer == null) return "";
        return logBuffer.toString();
     }

     /* atX methods ******************************************************/

    /**
     * Tests whether the scanner is positioned at an OpenSTag token.
     * This is just the "<tagName" part of an STag.
     * When the scanner has read a <it>complete</it> STag, "false" will be returned. 
     * @return true if the scanner's token is OpenSTag.
     */
    public final boolean atOpenSTag() throws IOException {
        if (tokenConsumed) nextToken();     
        return token == OpenSTag;
    }


    /**
     * Tests whether the scanner is positioned at an OpenSTag with the given name,
     * that is, whether the current token is "<"+tagName.
     * @return true if the scanner's token is at an start tag and the name in the tag is equal to tagName.
     */
    public final boolean atOpenSTag(String tagName) throws IOException {
        return atOpenSTag() && this.tagName.equals(tagName);
    }
    
    /**
     * Tests whether the scanner is positioned at an STag or an OpenSTag.
     * (Note that the value of completeSTags determines whether the actual
     * token is an STag or an OpenSTag; also, the atOpenSTag() can be used to 
     * ensure that the token is OpenSTag, rather than an STag, if desired.)
     * @return true if the scanner's token is STag or OpenSTag.
     */
    public final boolean atSTag() throws IOException {
        if (tokenConsumed) nextToken();     
        return token == STag || token == OpenSTag;
    }

    /**
     * Tests whether the scanner is positioned at an start tag with the given name.
     * The actual token can be STag or OpenSTag.
     * @param  tagName The element name to be tested
     * @return true if the scanner's token is at an start tag and the name in the tag is equal to tagName.
     */
    public final boolean atSTag(String tagName) throws IOException {
        return atSTag() && this.tagName.equals(tagName);
    }


    /**
     * Tests whether the scanner is positioned at the closing section of an STag.
     * @return true if the scanner's token is CloseSTag.
     */
    public final boolean atCloseSTag() throws IOException {
        if (tokenConsumed) nextToken();
        return token == CloseSTag;
    }
    
    /**
     * Tests whether the scanner is positioned at an end tag.
     * @return true if the scanner's token is ETag. 
     */
    public final boolean atETag() throws IOException {
        //System.out.println("atETag test1, consumed = " + tokenConsumed + ",token = " + token + ", ci = " + ((char)ci));
        if (tokenConsumed) nextToken();
        //System.out.println("atETag test1, token = " + token);
        return token == ETag;
    }


    /**
     * Tests whether the scanner is positioned at an end tag with the given name.
     * @param  tName The element name to be tested
     * @return true if the scanner's token is at an end tag and the name in the tag is equal to tagName.
     */
    public final boolean atETag(String tName) throws IOException {
        //System.out.println("atETag test2, consumed = " + tokenConsumed + ",token = " + token + ", ci = " + ((char)ci));
        //System.out.println("expect ETag: " + tName + ", found: " + tagName);
        return atETag() && tagName.equals(tName);
    }

    /**
     * Tests whether the scanner is positioned at an attribute
     * @return true if the scanner's token is Attribute.
     */
    public final boolean atAttribute() throws IOException {
        if (tokenConsumed) nextToken();
        return token == Attribute;
    }

    /**
     * Tests whether the scanner is positioned at an attribute name
     * @return true if the scanner's token is AttributeName.
     */
    public final boolean atAttributeName() throws IOException {
        if (tokenConsumed) nextToken();
        return token == AttributeName;
    }

    /**
     * Tests whether the scanner is positioned at a specific attribute name
     * @return true if the scanner's token is AttributeName and the attribute name is attName
     */
    public final boolean atAttributeName(String attName) throws IOException {
        return atAttributeName() && attributeName.equals(attName);
    }

    /**
     * Tests whether the scanner is positioned at an attribute name
     * @return true if the scanner's token is AttributeValue.
     */
    public final boolean atAttributeValue() throws IOException {
        if (tokenConsumed) nextToken();
        return token == AttributeValue;
    }
    
    /**
     * Tests whether the scanner is positioned at a processing instruction.
     * @return true if the scanner's token is PI.
     */
    public final boolean atPI() throws IOException {
        //System.out.println("atPI test, consumed = " + tokenConsumed);
        if (tokenConsumed) nextToken();
        //System.out.println("atPi test, token = " + token);
        return token == PI;
    }

    /**
     * tests whether the scanner is positioned at a comment.
     * @return true if the scanner's token is Comment.
     */
    public final boolean atComment() throws IOException {
        //System.out.println("atComment(), ci =" + ci + "(" + (char)ci + ") tokenConsumed:" + tokenConsumed);
        if (tokenConsumed) nextToken();
        return token == Comment;
    }

    /**
     * tests whether the scanner is positioned at a doctype comment.
     * @return true if the scanner's token is Doctype.
     */
    public final boolean atDoctype() throws IOException {
        if (tokenConsumed) nextToken();
        return token == Doctype;
    }

    /**
     * tests whether the scanner is positioned at a CDATA section.
     * @return true if the scanner's token is CDATA.
     */
    public final boolean atCDSect() throws IOException {
        if (tokenConsumed) nextToken();
        return token == CDSect;
    }

    /**
     * tests whether the scanner is positioned at a doctype comment.
     * and that the doctype name equals name.
     */
    public final boolean atDoctype(String name) throws IOException {
        return atDoctype() && doctypeName.equals(name);
    }

    /**
     * tests whether the scanner is positioned at CharData
     * @return true if the scanner's token is CharData.
     */
    public final boolean atCharData() throws IOException {
        if (tokenConsumed) nextToken();
        return token == CharData;
    }

    /**
     * Tests whether the scanner is positioned at the end of the document.
     * @return true if the scanner's token is EndOfDocument.
     */
    public final boolean atEndOfDocument() throws IOException {
        if (tokenConsumed) nextToken();
        return token == EndOfDocument;
    }
    
    /* getX methods ******************************************************/


    /**
     * returns the current token, without consuming it (unlike nextToken)
     * If the current token was consumed, nextToken is called first.
     * @return The current token
     */
    public final int getToken() throws IOException {
        if (tokenConsumed) nextToken();
        return token;      
    }


    /**
     * returns the current token in String format, without consuming it.
     * If the current token was consumed, nextToken is called first.
     * @return The current token as String
     */
    public final String getTokenString() throws IOException {
        if (tokenConsumed) nextToken();
        return tokenString(token);
    }

    /**
     * Reads the current start tag.
     * @return The name in the current start tag
     */
    public final String getTagName() throws IOException {
        if (! (atSTag() || atCloseSTag() || atETag()) ) {
            throw new IllegalStateException("The XMLTokenizer was not at an STag or an ETag (" + line + ", " + charPos + ")" );
        }
        return tagName;
    }
    
    
    /**
     * reads the current comment, without advancing the scanner to the next token.
     * @return The current comment
     */
    public final String getComment() throws IOException {
        if (!atComment()) {
            throw new IllegalStateException("The XMLTokenizer was not at a Comment token(" + line + ", " + charPos + ")" );
        }
        return commentDataBuffer.toString();
    }

    /**
     * returns the doctype name, or null if not defined.
     */
    public final String getDoctypeName()  {
       return doctypeName;
    }

    /**
     * 
     */
    public final String getPubidLiteral() { 
       return pubidLiteral;
    }
    
    /**
     * 
     */
    public final String getSystemLiteral() {
       return systemLiteral;
    }
    
    /**
     * reads the current PI, without advancing the scanner to the next token.
     * @return The current pi
     */
    public final String getPI() throws IOException {
        if (!atPI()) {
            throw new IllegalStateException("The XMLTokenizer was not at a PI token(" + line + ", " + charPos + ")");
        }
        return piDataBuffer.toString();
    }


    /**
     * Reads the current CharData.
     * @return current CharData.
     */
    public final String getCharData() throws IOException {
        if (!atCharData()) {
            throw new IllegalStateException("The XMLTokenizer was not at an CharData token(" + line + ", " + charPos + ")");
        }
        String charData = charDataBuffer.toString();
        if (trimWS) return charData.trim();
        else return charData;
    }

    /**
     * Reads the current CDATA.
     * @return current CDATA.
     */
    public final String getCDSect() throws IOException {
        if (!atCDSect()) {
            throw new IllegalStateException("The XMLTokenizer was not at an CDATA token(" + line + ", " + charPos + ")");
        }
        String cData = cDataBuffer.toString();
        return cData;
    }

    /**
     * Reads the current attribute name, which is defined iff the current token
     * is either Attribute or AttributeName.
     * @return current attribute name.
     */
    public final String getAttributeName() throws IOException {
        if (!atAttributeName() && ! atAttribute()) {
            throw new IllegalStateException("The XMLTokenizer was not at an attribute name token(" + line + ", " + charPos + ")");
        }
        return attributeName;
    }



    /**
     * Reads the current attribute value, which is defined iff the current token
     * is either Attribute or AttributeValue.
     * @return current attribute value.
     */
    public final String getAttributeValue() throws IOException {
        if (!atAttributeValue() && ! atAttribute()) {
            throw new IllegalStateException("The XMLTokenizer was not at an attribute value token(" + line + ", " + charPos + ")");
        }
        return attributeValueBuffer.toString();
    }
    
    /**
     * Reads the current long integer attribute value, 
     * which is defined iff the current token
     * is either Attribute or AttributeValue, and the actual value
     * encodes a long integer.
     * @return current attribute value.
     */
    public final long getLongAttributeValue() throws IOException {
        if (!atAttributeValue() && ! atAttribute()) {
            throw new IllegalStateException("The XMLTokenizer was not at an attribute value token(" + line + ", " + charPos + ")");
        }
        return Long.parseLong(attributeValueBuffer.toString());
    }

    /**
     * Reads the current integer attribute value, 
     * which is defined iff the current token
     * is either Attribute or AttributeValue, and the actual value
     * encodes an integer
     * @return current attribute value.
     */
    public final int getIntegerAttributeValue() throws IOException {
        return (int) getLongAttributeValue();
    }


    /**
     * Reads the current double attribute value, 
     * which is defined iff the current token
     * is either Attribute or AttributeValue, and the actual value
     * encodes a double.
     * @return current attribute value.
     */
    public final double getDoubleAttributeValue() throws IOException {
        if (!atAttributeValue() && ! atAttribute()) {
            throw new IllegalStateException("The XMLTokenizer was not at an attribute value token(" + line + ", " + charPos + ")");
        }
        return Double.parseDouble(attributeValueBuffer.toString());
    }

    /**
     * Reads the current float attribute value, 
     * which is defined iff the current token
     * is either Attribute or AttributeValue, and the actual value
     * encodes a float.
     * @return current attribute value.
     */
    public final float getFloatAttributeValue() throws IOException {
        return (float) getDoubleAttributeValue();
    }

    /**
     * Reads the current boolean attribute value, 
     * which is defined iff the current token
     * is either Attribute or AttributeValue, and the actual value
     * encodes a boolean.
     * @return current attribute value.
     */
    public final boolean getBooleanAttributeValue() throws IOException {
        if (!atAttributeValue() && ! atAttribute()) {
            throw new IllegalStateException("The XMLTokenizer was not at an attribute value token(" + line + ", " + charPos + ")");
        }
        return Boolean.getBoolean(attributeValueBuffer.toString());
    }

    /**
     * returns the attributes HashMap, which has properly defined name/value pairs
     * iff the current token is eitherOpenSTag, or STag.
     */
    public final HashMap getAttributes() throws IOException {
        if (! atSTag() ) {
          throw new IllegalStateException("The XMLTokenizer was not at an STag(" + line + ", " + charPos + ")");
        }
        if (token == OpenSTag) token = parseSTagTail();
        return attributes;    
    }

    public final String getAttribute(String attributeName) throws IOException {
        if (! atSTag() ) {
          throw new IllegalStateException("The XMLTokenizer was not at an STag(" + line + ", " + charPos + ")");
        }
        if (token == OpenSTag) token = parseSTagTail();
        return attributes.get(attributeName);         
    }

    /**
     * returns an Iterator for the attributes HashMap, which has properly defined name/value pairs
     * iff the current token is eitherOpenSTag, or STag.
     * The Iterator elements are of type  Map.Entry,
     * which accessor methods getKey() and getValue()
     */
    public final Iterator getAttributeIterator() throws IOException {
        if (! atSTag() ) {
          throw new IllegalStateException("The XMLTokenizer was not at an STag(" + line + ", " + charPos + ")");
        }
        if (token == OpenSTag) token = parseSTagTail();
        return attributes.entrySet().iterator();  //attrMap.entrySet().iterator()  
    }

   
    /* takeX methods ******************************************************/

   /**
    * checks whether the current token is an STag, and consumes it.
    * When the current token is OpenSTag, the remainder of the STag will be read
    * and consumed.
    * The tagName of the STag is returned.
    */
   public final String takeSTag() throws IOException {
      if (! atSTag()) throw new IllegalStateException("The XMLTokenizer was not at an STag token(" + line + ", " + charPos + ")");
      if (token == OpenSTag) token = parseSTagTail();
      //System.out.println("token = " + token);
      String result = getTagName();
      tokenConsumed = true;
      return result;
   }

   /**
    * checks whether the current token is an STag, and consumes it.
    * When the current token is OpenSTag, the remainder of the STag will be read
    * and consumed.
    * The tagName of the STag is returned.
    */
   public final void takeSTag(String tagName) throws IOException {
      if (! atSTag()) throw new IllegalStateException("The XMLTokenizer was not at an STag token(" + line + ", " + charPos + ")");
      if (token == OpenSTag) token = parseSTagTail();
      if ( ! getTagName().equals(tagName)) throw getXMLScanException(tagName + " tag expected, found: " + getTagName()) ;
      tokenConsumed = true;
   }

   /**
    * checks whether the current token is an OpenSTag (i.e. not an STag), and consumes it.
    */    
   public final void takeOpenSTag(String tagName) throws IOException {
      if ( ! atOpenSTag()) 
        throw new IllegalStateException("The XMLTokenizer was not at an STag token(" + line + ", " + charPos + ")");
      if ( ! getTagName().equals(tagName)) 
        throw getXMLScanException(tagName + " tag expected, found: " + getTagName() ) ;
      tokenConsumed = true;
   }

   /**
    * checks whether the current token is an OpenSTag (i.e. not an STag), and consumes it.
    * The tagName is returned.
    */  
   public final String takeOpenSTag() throws IOException {
      if ( ! atOpenSTag()) 
        throw new IllegalStateException("The XMLTokenizer was not at an OpenSTag token(" + line + ", " + charPos + ")");
      String result = getTagName();  // will throw exception if not at STag.
      tokenConsumed = true;
      return result;
   }

   public final void takeCloseSTag() throws IOException {
      if ( ! atCloseSTag()) 
        throw new IllegalStateException("The XMLTokenizer was not at an CloseSTag token(" + line + ", " + charPos + ")");
      tokenConsumed = true;
   }
   
   public final String takeETag() throws IOException {
      //System.out.println("takeETag, token = " + token + ", consumed = " + tokenConsumed);
      String result = getTagName();
      //System.out.println("takeETag, tagName = " + result);
      if (! atETag() )  throw new IllegalStateException("The XMLTokenizer was not at an ETag token(" + line + ", " + charPos + ")");
      tokenConsumed = true;
      return result;
   }

   public final void takeETag(String tagName) throws IOException {
    //System.out.println("takeETag, token = " + token + ", consumed = " + tokenConsumed);
      if (! atETag() )  
         throw new IllegalStateException("The XMLTokenizer was not at an ETag token(" + line + ", " + charPos + ")");
      if (!(getTagName().equals(tagName)))
        throw getXMLScanException("ETag " + tagName + " expected, but found : " + getTagName());
      tokenConsumed = true;
   }
      
  public final String takeAttributeName() throws IOException {
      String result = getAttributeName();  // will throw exception if not at an attribute name
      tokenConsumed = true;
      return result;
   }

   public final void takeAttributeName(String attName) throws IOException {
      if ( ! atAttributeName(attName)) 
        throw getXMLScanException("The XMLTokenizer was not at an AttributeName token, or wrong attribute name");
      tokenConsumed = true;
   }
   
  public final String takeAttributeValue() throws IOException {
      String result = getAttributeValue(); // will throw exception if not at an attribute value
      tokenConsumed = true;
      return result;
   }

  public final void takeAttribute() throws IOException {
      if ( ! atAttribute() ) throw new IllegalStateException("The XMLTokenizer was not at an Attribute token(" + line + ", " + charPos + ")");
      tokenConsumed = true;
   }
 
   public final String takeCharData() throws IOException {
      String result = getCharData(); // will throw exception if not at char data
      tokenConsumed = true;
      return result;
   }  

   public final String takeCDSect() throws IOException {
      String result = getCDSect(); // will throw exception if not at CDATA
      tokenConsumed = true;
      return result;
   }  

   public final String takePI() throws IOException {
      String result = getPI(); // will throw exception if not at PI
      tokenConsumed = true;
      return result;
   }  

   public final String takeComment() throws IOException {
      String result = getComment(); // will throw exception if not at comment
      tokenConsumed = true;
      return result;
   }  

   /**
    * reads one (unparsed) character from the input stream.
    * this is a low-level method, that should be used only for exceptional situations
    * for instance, when embedded data that does not conform to XML standards has to be processed.
    */
   public final int read() throws IOException {
       if (ci == CONSUMED) {
          nextChar();
       }
       int result = ci;
       ci = CONSUMED;
       return result;    
   }

   /**
    * reads unparsed characters from the input stream, and returns them as String.
    * Exactly len characters will be read, including "blank space".
    */
   public final String takeString(int len) throws IOException {
       //System.out.println("takeString(" + len + "), ci =" + ci);
       if (len <= 0) return "";
       // len >= 1
       StringBuffer buf = new StringBuffer(len);
       // the first character is the current ci value, if not yet consumed,
       // else, the first character must be read.
       if (ci == CONSUMED) {
          nextChar();
       }
       buf.append((char)ci);
       // read remaining characters (start at i=1)
       for (int i=1; i<len; i++) {
           nextChar();
           if (ci < 0) {
              throw getXMLScanException("takeString("+len+") : EOS while reading String character nr " + i);
           }
           buf.append((char) ci);
       }
       // last character read belong to String, so ci is consumed:
       ci = CONSUMED;
       return buf.toString();
   }

    /*
     * defines the characters that can be used inside names, like 
     * tag names and attribute names.
     * Currently, (unicode) "CombiningChars" and "Extenders" are not included.
     * Name characters are: letters, digits, and the four characters '_', '-', '.', and ':'.
     * Note ':' characters are allowed for names that use name spaces.
     * The test is carried out on the current character ci.
     */
   private final boolean isNameChar() {
      return ('a'<=ci && ci<='z') ||  ('A'<=ci && ci<='Z') || 
             ('0'<=ci && ci<='9') ||  (ci=='_') || (ci=='-') || (ci=='.') || (ci==':');
   }

    /*
     * defines the characters that can be used as <it>first</it> character of names.
     * Characters allowed are: letters, and the two characters '_' and ':'.
     * The test is carried out on the current character ci.
     */
   private final boolean isNameStartChar() {
      return ('a'<=ci && ci<='z') || ('A'<=ci && ci<='Z') || (ci=='_') || (ci==':');
   }

    /*
     * defines the characters that classify as "Space" in XML.
     * The test is carried out on the current character ci.
     */
   private final boolean isSpaceChar() {
      return (ci == ' ' || ci == '\n' || ci == '\t' || ci == '\r');
   }

   /*
    * skips "blank space" characters. i.e skips ' ', '\n', '\t', and '\r' characters.
    */
   private final void skipSpaceChars() throws IOException {
      while(ci == ' ' || ci == '\n' || ci == '\t' || ci == '\r') {
         nextChar();
      } 
   }


   /*
    * reads the ci character from the input stream, without further processing.
    */
   private final int nextChar() throws IOException {
      ci = in.read();
      charPos++;
      if (log) {
          logBuffer.append((char)ci);  
      }
      if (ci == '\n') {
          line++;
          charPos = 0;
      }
      return ci;
   }



   /*
    * reads the ci character from the input stream, translates entity references like &lt;
    * and fills the character ch. 
    * ci contains the last raw character from "in"; ch contains the last parsed character,
    * so for instance, when the input contains &gt;, ci contains ';', and ch contains '>'.
    * When the input contains '>', both ci and ch contain '>'.
    * This translation is required while reading CharData and Attribute Values.
    * It is not to be used while reading/parsing tags, attribute names, PI's, comments etc.
    */
   public final void nextParsedChar() throws IOException {      
      nextChar();
      if (ci == '&') {
          nextChar();
          switch (ci) {
             case 'l' : {  
                 if (nextChar() != 't' || (nextChar()) != ';') throw getXMLScanException("error in \"&lt;\" reference (char:" + (char)ci +") ");
                 ch = '<';
                 break; 
             }           
             case 'g' : {               
                 if (nextChar() != 't' || (ci=nextChar()) != ';') throw getXMLScanException("error in \"&gt;\" reference(char:" + (char)ci +") ");
                 ch = '>';
                 break; 
             }
             case 'q' : {               
                 if (nextChar() != 'u' ||  nextChar() != 'o' || nextChar() != 't' || (ci=nextChar()) != ';') throw getXMLScanException("error in \"&quot;\" reference(char:" +ci +") ");
                 ch = '"';
                 break; 
             }
             case 'a' : {  // could be &amp; or &apos;
                 nextChar();   
                 if (ci == 'm') {
                     if (nextChar() != 'p' || (nextChar()) != ';') 
                          throw getXMLScanException("error in \"&amp;\" reference(char:" + (char)ci +") ");
                     else ch = '&';
                 } else if (ci == 'p') {
                     if (nextChar() != 'o' || nextChar() != 's' || (ci=nextChar()) != ';') 
                          throw getXMLScanException("error in \"&apos;\" reference(char:" + (char)ci +") ");
                     else ch = '\'';
                 } else {
                     throw getXMLScanException("error in \"$amp;\" or \"&apos;\" reference(char:" + (char)ci +") ");
                 }
                 break; 
             }    
            default: { throw getXMLScanException("unexpected character after \'&\'(char:" + (char)ci +") ");
            
            }
         }
      } else {
      	ch = (char) ci;
      }
   }

   /*
    * freezes the current line and character position.
    */
   private void setTokenPos() {
      tokenLine = line;
      tokenCharPos = charPos;
      if (ci == CONSUMED) tokenCharPos++;
   }


   /* clears the charDataBuffer buffer etc*/

   private final void clearBuffer(StringBuffer b) {  b.delete(0, b.length()); }

   /**
    * push a tag symbol on the tagStack.
    */
   private final void pushTag(String tag) {
      if (tagStack == null) {
          tagStack = new ArrayStack<String>(20);
      }
      tagStack.push(tag);
   }

   /**
    * pops a tag symbol from the tagStack.
    * throws an XMLScanException if tag is not at the current top,
    * or if the stack is empty.
    */
   private final void popTag(String tag) {
      if (tagStack == null || tagStack.isEmpty()) 
         throw getXMLScanException("XML document not wellformed: ETag " 
          + tag + " without corresponding STag" );
      String top = (String) tagStack.pop();
      if ( ! top.equals(tag))
         throw getXMLScanException("XML document not wellformed: /" 
          + tag + " read, while expecting: /" + top );
   }

   /**
    * pops a tag symbol from the tagStack.
    * throws an XMLScanException if the stack is empty.
    */
   private final void popTag() {
      if (tagStack == null || tagStack.isEmpty()) 
         throw getXMLScanException("pop on empty tag stack " );
      tagStack.pop();
   }

   /**
    * checks whether the tagStack is empty. 
    * If not, an XMLScanException is thrown.
    */
   private final void checkEmptyTagStack() {
       if ( ! tagStack.isEmpty()) {
         throw getXMLScanException("XML document not wellformed: Open tag " 
          + tagStack.top() + " at end of document" );    
       }       
   }




   /**
    * sets the mode of popping the status stack when the ENDOFDOCUMENT token
    * is encountered within the input stream: auto popping enabled will silently suppress
    * the End-Of-Document tokens until the stack is empty
    * and the ENDOFDOCUMENT is reached
    */
   public final void setpopOnEndOfDocument(boolean mode) {
      popOnEndOfDocument = mode;
   }






   /**
    * returns a String representation for each integer that represents a lexical token, 
    * like the String "STag" for the integer STag constant etc.
    * Useful for printing tag names in Console messages.
    */
   public static String tokenString(int token) {
       switch (token) {
       case NULL_Token     : return "NULL_Token"; 
       case STag           : return "STag";
       case OpenSTag       : return "OpenSTag";
       case CloseSTag      : return "CloseSTag";
       case ETag           : return "ETag";
       case Attribute      : return "Attribute";
       case AttributeName  : return "AttributeName";
       case AttributeValue : return "AttributeValue";
       case CharData       : return "CharData";
       case CDSect         : return "CDSect";
       case Comment        : return "Comment";
       case PI             : return "PI";
       case Decl           : return "Decl";
       case Doctype        : return "DocType";
       case EndOfDocument  : return "EndOfDocument";
       case ErrorToken     : return "XML Error";
       }
       return "";
   }

   private static boolean debug = false;
   
   /**
    * sets the debug mode;
    * when set to true, RuntimeExceptions are not catched
    */
   public static void setDebug(boolean mode) {
      debug = mode;
   }


    /**
     * sets the modes to their current default values.
     */
    public final void setDefaultModes() {
        preserveIgnorableWS = PRESERVE_IGNORABLE_WS;
        trimWS              = TRIM_WS;
        skipDoctype         = SKIPDOCTYPE;
        skipComment         = SKIPCOMMENT;
        skipPI              = SKIPPI;
        skipCloseSTags      = SKIPCLOSESTAGS;
        completeAttributes  = COMPLETEATTRIBUTES;
        completeSTags       = COMPLETESTAGS;  
        checkWellformednes  = CHECKWELLFORMEDNES;   
        propertyLock        = PROPERTYLOCK;   
        log                 = LOG;
    }    


   /* Possible token values: */
   public static final int NULL_Token     = 0;
   public static final int STag           = 1;     // a complete STag, including attributes
   public static final int OpenSTag       = 2;     // the opening part of an STag: "<tagName"
   public static final int CloseSTag      = 3;     // the closing part of an STag: ">"
   public static final int ETag           = 4;     // a complete Etag: "</tagName>; (also generated for empty tags)
   public static final int Attribute      = 5;     // a complete attribute-value pair: attrName = attrValue
   public static final int AttributeName  = 6;     // an attribute name
   public static final int AttributeValue = 7;     // an attribute value, preceded by an '=' token. 
   public static final int CharData       = 8;     // parsed character data
   public static final int CDSect         = 9;     // unparsed character data:      <![CData[ .... ]]>  
   public static final int Comment        = 10;    // comment:                      <!-- ..... -->
   public static final int PI             = 11;    // processing instruction:       <?.....?>
   public static final int Decl           = 12;    // other declarations,:          <! ..... >
   public static final int Doctype        = 13;    // document type declaration:    <!DOCTYPE ... >
   public static final int EndOfDocument  = 14;    // token returned when end of input stream has been reached.
   public static final int ErrorToken     = 15;    // token returned when an RuntimeException occurs


    /*
     * Properties defining how the XMLTokenizer should handle whitespaces in character data 
     * "ignorable white space" is a sequence of white space in between markup.
     * if preserveIgnorableWS == true, such white space is considered CharData,
     * otherwise (the default) such white space is ignored. 
     * White space around non-white content can be trimmed away, by setting trimWS to true.
     */
    boolean preserveIgnorableWS;  
    boolean trimWS;

    /*
     * Properties defining what type of info should be skipped by scanner 
     */
    boolean skipDoctype;
    boolean skipComment;
    boolean skipPI;
    /* if true, CloseSTag tokens (the '>' at the end of STags) are skipped */
    boolean skipCloseSTags;

    /* if true, only complete Attribute  tokens are returned, else 
     * separate AttributeName and AttributeValue tokens are used.
     */
    boolean completeAttributes;

    /* if true, only complete STags tokens are returned,  
     * else OpenSTag, AttributeName, AttributeValue, and CloseSTag tokens are returned
     * as separate tokens.
     */
    boolean completeSTags;

    /*
     * checkWellformedness denotes whether proper nesting and matching of tags 
     * must be checked.
     */
    boolean checkWellformednes;  

    /*
     * if propertyLock is set to true, the following properties cannot be (re)set any longer:
     * completeSTags, completeAttributes, skipDoctype, skipComment, skipPI, skipCoseSTags,
     * trimWS, preserveIgnorableWS, checkWellformedness
     * once set, the lock cannot be unlocked.
     */
    boolean propertyLock;
     

   /* 
    * "in" is the current input stream, in Reader format. It might actually be a PushbackReader,
    * which is denoted by the value of the boolean "isPushbackReader".
    * The amount of buffering for "in" is kept to a minimum, so as to allow other processes
    * (i.e. outside this XMLTokenizer) to share access to the "in" Reader. 
    * For STag and ETag tokens, only the characters belonging properly to the token are read from "in".
    * For names and for CharData, the character immediately following the token is necesarily read also.
    * It is possible to set "in" to a PushbackReader. In this case, pushback() can be called, which will 
    * push back the last character read, or, after reading an OpenSTag token, pushbackOpenSTag()
    * will push back the complete "<tagName " String, (including the character following the OpenSTag).
    */   
   private Reader in; // the "current input stream", in Reader format.
   //private boolean isPushbackReader; // denotes whether Reader "in" is actually a PushbackReader

   
   /*
    * url and/or file are the URL and/or File (if applicable) corresponding to the current Reader.
    */
   //protected String urlSpec;
   private URL url;
   private File file;


   /*
    * ci is a one-place buffer, containing the "current" unparsed character.
    * It has int type, rather than char type, since reading the input stream yields ints,
    * which can be negative: EOS (= -1) denotes end-of-stream reached, CONSUMED (=-2) means
    * that the current ci value is considered "consumed". When "consumed", the one place buffer 
    * is considered "empty", and a new value for ci should be read from "in" before further processing. 
    * The "consumed" state allows for other processes than the process running this 
    * XMLTokenizer object to start reading from the same Reader, 
    * without losing even a single character, since no character is buffered 
    * in the XMLTokenizer when ci is CONSUMED.
    */
   private int  ci;                   // the "current character",  in int format.

   /*
    * ch is the current parsed character. It is used inside CharData sections, and is normally
    * just ci converted to char type. "Parsed" implies that entity references (like "&lt;")
    * that occur in the "raw" input, will end up as a single ch value.
    * For instance,  the ci character sequence "&lt"; will be transformed into a '<' ch value).
    */
   private char ch;                  // the "current parsed character", in char format.
   private int line;                 // number of input lines read thus far, starting at line 1.
   private int charPos;                // position of the current character.
   private int tokenLine;            // starting line number for the current token. 
   private int tokenCharPos;             // starting character position for the current token.
   private int token;                // the "current lexical token", like STag etc.
   private StringBuffer logBuffer; //
   private boolean log;
   private boolean etagFence;// if true, an ETag with tagName equal to fenceTagName cannot be passed.
   private String fenceTagName;// Tag that cannot be passed
   private boolean blocking = true; // denotes whether the tokenizer should block if necessary
                                    // when the Reader is not ready.
   
   /* the current token can be "consumed", which means that getX and takeX methods
    * will first read a new token, when they are called. 
    * However, "consumed" does not mean that the input Reader has advanced as yet.
    * This allows for other processes to read from the same Reader, without "missing"
    * input that is buffered by this XMLTokenizer.
    * The next token can be "buffered" in case a hasMoreTokens() call must determine
    * whether there ARE next tokens, yet is not allowed to advance to that next token.
    * In this state, we have that the current token is "consumed", whereas the next token is 
    * buffered. (Thank's Eelco). A call to nextToken in this state simply changes
    * "tokenConsumed" and "tokenBuffered" to false, without doing anything else.
    */
    
   private boolean tokenConsumed;    // denotes whether the current token has been "consumed".
   private boolean tokenBuffered;    // denotes whether the next token has been "buffered"
   private int tokenMode;            // the "current (tag) mode ", indicates position within STags.



   /* buffers for collecting tag names, attribute names, attribute values, char data, comments, pi's */
   public String tagName;        // tagNames and attributeNames are immediately converted to Strings.
   public String attributeName;
   public String doctypeName;         // name of DOCTYPE 
   public String pubidLiteral ;       // public Id of DOCTYPE
   public String systemLiteral;       // system id of DOCTYPE
   // attributeValues, charData, piData, commentData remains in StringBuffer form until retrieved
   // by a getX or takeX method.
   
   public StringBuffer tagNameBuffer        = new StringBuffer(10);
   public StringBuffer attributeNameBuffer  = new StringBuffer(10);
   public StringBuffer attributeValueBuffer = new StringBuffer(100);
   public StringBuffer charDataBuffer       = new StringBuffer(100);
   public StringBuffer cDataBuffer          = new StringBuffer(100);
   public StringBuffer piDataBuffer         = new StringBuffer(10);
   public StringBuffer commentDataBuffer    = new StringBuffer(50);
   public StringBuffer buf;

   private ArrayStack<String> tagStack;  // stack of tagnames, used to check wellformednes
   private ArrayStack<TokenizerState> tokenizerStateStack;  // stack of XMLParserState objects
   private boolean popOnEndOfDocument = false;          // indicates whether to (automatically) pop the status stack
                                             // when EndOfDocument is encountered.
   private ArrayStack<HashMap<Id, Object>> contextStack;  // stack of context Objects
   
   /*
    * when completeSTags is set to true, the attributes HashMap is used to
    * collect all attribute value pairs.
    */
   public java.util.HashMap<String, String> attributes = new java.util.LinkedHashMap<String, String>(); 
   
   /*
    * The context attribute is not necessary for the functioning of the XMLTokenizer itself
    * Rather, it is a generic Java HashMap that could be something like a symbol table
    * that needs to be passed on between a collection of methods for a recursive decent parser
    * it is allocated only when context methods are being used.
    */
    public HashMap<Id, Object> context;
   

   /* possible tokenMode values: */
   private static final int CharData_MODE       = 1; // "default mode", CharData unless '<' or EOS
   private static final int STagTail_MODE       = 2; // inside an STag tail, expecting attributes or '>'
   private static final int AttributeValue_MODE = 3; // inside STag, expecting "=attributeValue" part.
   private static final int PendingETag_MODE    = 4; // at end of an "Empty Tag" ("<..../>"), with "pending ETag token".
   private static final int EndOfDocument_MODE  = 5; // mode after reaching EndOfDocument.
   

   public static final int EOS       = -1; // end-of-stream char 
   public static final int CONSUMED  = -2; // ci has been consumed without actually reading next char from input stream.

    /**
     * Starts the scanner on the current input 
     */
   private final void initState() {
       // initial state is the same as if a \n has just been read, and has been consumed,
       // so we are at line 1, position 0 (because of \n), tokenLine = 1, tokenCharPos = 1
       // (because ci == CONSUMED).
       // we start in CharData mode, and pretend the "last" token has been consumed too. 
       ci = CONSUMED;
       if (in == null) {
          token = EndOfDocument;
          tokenMode = EndOfDocument_MODE;
       } else {
          token = NULL_Token;
          tokenMode = CharData_MODE;
       }
       tokenConsumed = true;  // will cause the first atX/takeX/getX call to read the first token.
       tokenBuffered = false; // we have not read anything yet.
       line = 1;
       charPos = 0;
       setTokenPos();
   }





   /**
    * sets the current context Object, and returns the previous value
    *
   public Object setContext(HashMap newContext) {
      Object oldContext = context;
      context = newContext;
      return oldContext;
   }

   /**
    * returns the current context Object
    */
   public HashMap getContext() {
      return context;
   } 
 
   /*
    * push the current context Object, turns the specified Object
    * into the current context Object.
    *
   public final void pushContext(Object newContext) {
      if (contextStack == null) {
          contextStack = new ArrayStack(10);
      }
      contextStack.push(context);
      context = newContext;
   }


   /*
    * push the current context Object, and allocates a
    * HashMap that acts as the new current context Object.
    */
   public final void pushContext() {
      if (contextStack == null) {
          contextStack = new ArrayStack<HashMap<Id, Object>>(10);
      }
      contextStack.push(context);
      context = new HashMap<Id, Object>();
   }

   /*
    * pops the context Object and assigns it to the current context, and also returns it.
    */
   public final HashMap<Id, Object> popContext() {
      context =   contextStack.pop();
      return context;
   } 

   /**
    * saves an attribute within the current context HashMap.
    * A new HashMap is allocated when no map is allocated as yet.
    */
   public final void setContextAttribute(Id attrId, Object attribute) {
      if (context == null) context = new HashMap<Id, Object>();
      context.put(attrId, attribute);
   }

   /**
    * saves an attribute within the current context HashMap.
    * A new HashMap is allocated when no map is allocated as yet.
    */
   public final void setContextAttribute(String attrIdString, Object attribute) {
      if (context == null) context = new HashMap<Id, Object>();
      context.put(Id.forName(attrIdString), attribute);
   }

    
   /**
    * returns the current value of an context attribute, as stored in the
    * context HashMap.
    * Returns null if no context has been allocated, or when no attribute value has been
    * sorted for the specified Id
    */ 
   public Object getContextAttribute(Id attrId) {
      if (context == null) return null;
      return context.get(attrId);
   }


   /**
    * returns the current value of an context attribute, as stored in the
    * context HashMap.
    * Returns null if no context has been allocated, or when no attribute value has been
    * sorted for the specified Id
    */ 
   public Object getContextAttribute(String attrIdString) {
      if (context == null) return null;
      return context.get(Id.forName(attrIdString));
   }

   /*
    * push current XMLTokenizer status on the status stack
    */
   public final void pushState() {
      if (tokenizerStateStack == null) {
          tokenizerStateStack = new ArrayStack<TokenizerState>();
      }
      TokenizerState currentState = new TokenizerState();
      currentState.copyState();
      tokenizerStateStack.push(currentState);
   }

   /*
    * pops the status stack, and restores the current XMLTokenizer state
    * from the top stack element.
    */
   public final void popState() {
      TokenizerState savedState =  tokenizerStateStack.pop();
      savedState.restoreState();
   }

   /*
    * prints the current contents of the stack on the Console
    */
   public final void showTokenizerStack() {
      showTokenizerStack("TokenizerStack:\n");
   }


   /*
    * prints the current contents of the stack on the Console
    */
   public final void showTokenizerStack(String message) {
      if (tokenizerStateStack == null) {
         Console.println("Null TokenizerStateStack");
      } else {
          Console.println(tokenizerStateStack.toString(message, "\n")); 
    
      }
   }

   /*
    * prints the current contents of the stack on the Console
    */
   public final void showTokenizerState() {
      showTokenizerState("TokenizerState:\n");
   }

   /*
    * prints the current contents of the stack on the Console
    */
   public final void showTokenizerState(String message) {
      TokenizerState currentState = new TokenizerState();
      currentState.copyState();
      Console.println(message + currentState); 
   }

   /*
    * TokenizerState objects are used to save and restore the current "state"
    * of this XMLTokenizer on the stack.
    */
   private class TokenizerState {
      Reader inState;
      URL urlState;
      int ciState;
      char chState;
      int lineState;
      int charPosState; 
      int tokenCharPosState;
      int tokenState;
      int tokenModeState;  
      boolean tokenConsumedState;  
      boolean tokenBufferedState; 
      String tagNameState;   
      boolean popOnEndOfDocumentState;
      /* save current XMLTokenizer state in this TokenizerState Object */
      void copyState() {
         inState = in; 
         urlState = url;
         ciState = ci;
         chState = ch;
         lineState = line;
         charPosState = charPos; 
         tokenCharPosState = tokenCharPos;
         tokenState = token;
         tokenModeState = tokenMode;
         tokenConsumedState = tokenConsumed;
         tokenBufferedState = tokenBuffered;
         tagNameState = tagName;
         popOnEndOfDocumentState = popOnEndOfDocument;
      }
      /* restore XMLTokenizer state from this TokenizerState Object */
      public void restoreState() {
         in = inState;
         url = urlState;
         ci = ciState;
         ch = chState;
         
         line = lineState;
         charPos = charPosState;
         tokenCharPos = tokenCharPosState;
         token = tokenState;
         tokenMode = tokenModeState;
         tagName = tagNameState;
         tokenConsumed = tokenConsumedState;  
         tokenBuffered = tokenBufferedState; 
         popOnEndOfDocument = popOnEndOfDocumentState;
      }
      public String toString() {
         StringBuffer buf = new StringBuffer("[");
         buf.append("url: "); buf.append(urlState);
         buf.append(" ch: "); buf.append(chState);
         buf.append(" ci: "); buf.append(ciState);
         buf.append(" line: "); buf.append(lineState);
         buf.append(" charPos: "); buf.append(charPosState);
         buf.append(" tokenCharPos: "); buf.append(tokenCharPosState);
         buf.append(" token: "); buf.append(tokenString(tokenState));
         buf.append(" tagName: "); buf.append(tagNameState);
         buf.append(" consumed: "); buf.append(tokenConsumedState);
         buf.append(" buffered: "); buf.append(tokenBufferedState);
         buf.append(" popEOD: "); buf.append(popOnEndOfDocumentState);
         buf.append(']');
         return buf.toString();
      }
   } // TokenizerState
 

}