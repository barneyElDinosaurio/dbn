import java.awt.*;
import java.applet.Applet;
import java.io.*;
import java.net.*;
import java.util.*;


public class DbnApplet extends Applet
{
    static DbnApplet applet;
    static Properties properties;
    boolean errorState;

#ifndef PLAYER
    String encoding;
    DbnEnvironment environment;
#endif

    public void init() {
	applet = this;
	//System.getProperties().list(System.out);
	//System.out.println("home = " + System.getProperty("user.home"));
	//System.out.println("prefix = " + System.getProperty("sys.prefix"));

#ifdef PLAYER
	// because it's the player version, cut out all the 
	// other crap, so that this file is as small as possible

	//} else if (mode.equals("player")) {
	// could also do a class.forname for jdk11
	//DbnPlayerProgram dpp = new DbnPlayerProgram(this);
	try {
	    String program = get("program");
	    DbnPlayer player = 
		((DbnPlayer) Class.forName(program).newInstance());
	    add(player);
	    //environment = player;
	    player.init(this);
	    player.start();
	} catch (Exception e) {
	    e.printStackTrace();
	    errorState = true;
	}
#else
	encoding = get("encoding");
	new DbnPreprocessor(this);

	String mode = get("mode", "editor");
	if (mode.equals("editor")) {
	    System.err.println("editor not yet complete");
	    System.exit(0);
	    /*
	    boolean beautify = false; 
	    String program = get("program"); 
	    if (program == null) { 
		program = get("inline_program"); 
	    } 
	    if (program != null) { 
		// don't convert ; to \n if scheme
		if (program.charAt(0) != ';') { 
		    program = program.replace(';', '\n'); 
		    // not scheme, but don't beautify if it's python
		    if (program.charAt(0) != '#') 
			beautify = true; 
		} 
	    } 
	    add(hostess = new DbnEditor(this, program));
	    DbnEditor editor = new DbnEditor(this, program);
	    if (beautify) {
	    editor.doBeautify();
	    }
	    add(editor);
	    environment = editor;
	    */
	} else if (mode.equals("grid")) {
	    // read 1 or more programs to be laid out in grid mode
	    // first count how many programs
	    int counter = 0;
	    while (true) {
		if (get("program" + counter) == null)
		    break;
		counter++;
	    }
	    // next load the programs
	    // what to do if zero programs in griddify?
	    String filenames[] = new String[counter];
	    String programs[] = new String[counter];
	    for (int i = 0; i < counter; i++) {
		String filename = get("program" + i);
		programs[i] = readFile(filename);
	    }
	    DbnGrid grid = new DbnGrid(this, programs);
	    add(grid);
	    environment = grid;

#ifdef CONVERTER
	} else if (mode.equals("convert")) {
	    convert(readFile(get("input_filename")), 
		    get("output_class"), get("output_filename"));
	    System.exit(0);
#endif
	}
#endif PLAYER
    }


#ifndef PLAYER
    public void destroy() {
	if (environment != null) {
	    environment.terminate();
	}
    }
#endif


    public void paint(Graphics g) {
	if (errorState) {
	    g.setColor(Color.red);
	    Dimension d = size();
	    g.fillRect(0, 0, d.width, d.height);
	    //} else {
	    //super(g);
	}
    }

#ifdef CONVERTER
    public void convert(String program, String classname, String filename) {
	try {
	    DbnParser parser = 
		new DbnParser(DbnPreprocessor.process(program));
	    String converted = parser.getRoot().convert(classname);
	    FileOutputStream fos = 
		new FileOutputStream(filename + ".java");
	    PrintStream ps = new PrintStream(fos);
	    ps.print(converted);
	    ps.close();

	    fos = new FileOutputStream(filename + ".html");
	    ps = new PrintStream(fos);
	    ps.println("<HTML> <BODY BGCOLOR=\"white\">");
	    ps.println("<APPLET CODE=\"DbnApplet\" WIDTH=101 HEIGHT=101>");
	    ps.print("<PARAM NAME=\"program\" VALUE=\"");
	    ps.print(classname);
	    ps.println("\">");
	    ps.println("</APPLET>");
	    ps.println("</BODY> </HTML>");
	    ps.close();
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
#endif


#ifndef PLAYER
    /* loading order:
     * 0. if application, a file on the disk
     * 1. a file relative to the .html file containing the applet
     * 2. a url 
     * 3. a file relative to the .class files
     */
    public String readFile(String filename) {
	if (filename.length() == 0) {
	    return null;
	}
	URL url;
	InputStream stream = null;
	String openMe;
	byte temp[] = new byte[65536];  // 64k, 16k was too small

     	try {
	    // this is two cases, one is bound to throw (or work)
	    if (isApplet()) {
		// Try to open it relative to the document base
		url = new URL(getDocumentBase(), filename);
		stream = url.openStream();
	    } else {
		// if running as an application, get file from disk
		stream = new FileInputStream(filename);
	    }

	} catch (Exception e1) { try {
	    if (isApplet()) {
		// now try to open it relative to the code base
		url = new URL(getCodeBase(), filename);
		stream = url.openStream();
	    } else {
#ifdef JDK11
		url = getClass().getResource(filename);
		stream = url.openStream();
#else
		throw new DbnException(); // move to next
#endif
	    } 

	} catch (Exception e2) { try {
	    // Try to open the param string as a URL
	    url = new URL(filename);
	    stream = url.openStream();
	
	} catch (Exception e3) {
	    e1.printStackTrace(); 
	    e2.printStackTrace();
	    return null;
	} } }

	try {
	    int offset = 0;
	    while (true) {
		int byteCount = stream.read(temp, offset, 1024);
		if (byteCount <= 0) break;
		offset += byteCount;
	    }
	    byte program[] = new byte[offset];
	    System.arraycopy(temp, 0, program, 0, offset);

	    //return languageEncode(program);
#ifdef JDK11
	    // convert the bytes based on the current encoding
	    try {
		if (encoding == null)
		    return new String(program);
		return new String(program, encoding);
	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
		encoding = null;
		return new String(program);
	    }
#else
	    // use old-style jdk 1.0 constructor
	    return new String(program, 0);
#endif 

	} catch (Exception e) {
	    System.err.println("problem during download");
	    e.printStackTrace();
	    return null;
	}
    }
#endif  // !PLAYER

    // all the information from DbnProperties

    static public String get(String attribute) {
	return get(attribute, null);
    }

    static public String get(String attribute, String defaultValue) {
	String value = (properties != null) ?
	    properties.getProperty(attribute) : applet.getParameter(attribute);

	return (value == null) ? 
	    defaultValue : value;
    }

#ifndef PLAYER
    static public boolean getBoolean(String attribute, boolean defaultValue) {
	String value = get(attribute, null);
	return (value == null) ? defaultValue : 
	    (new Boolean(value)).booleanValue();
    }

    static public int getInteger(String attribute, int defaultValue) {
	String value = get(attribute, null);
	return (value == null) ? defaultValue : 
	    Integer.parseInt(value);
    }

    static public Color getColor(String name, Color otherwise) {
	Color parsed = null;
	String s = get(name, null);
	if ((s != null) && (s.indexOf("#") == 0)) {
	    try {
		int v = Integer.parseInt(s.substring(1), 16);
		parsed = new Color(v);
	    } catch (Exception e) {
	    }
	}
	if (parsed == null) return otherwise;
	return parsed;
    }

    static public boolean isMacintosh() {
	return System.getProperty("os.name").toLowerCase().indexOf("mac") != -1;
    }

    static public boolean hasFullPrivileges() {
	//if (applet == null) return true;  // application
	//return false;
	return !isApplet();
    }

    static public Font getFont(String which) {
	if (which.equals("editor")) {
	    // 'Monospaced' and 'courier' also caused problems.. ;-/
	    return new Font("monospaced", Font.PLAIN, 12);
	}
	return null;
    }
#endif  // PLAYER

    public String getNetServer() {
	String host = get("net_server", null);
	if (host != null) return host;

	if (isApplet()) {
	    return getCodeBase().getHost();
	}
	return "dbn.media.mit.edu";
    }

    static public boolean isApplet() {
	return (properties == null);
    }
}


	/*
	String separator = System.getProperty("line.separator");
	eolCount = separator.length();
	eol = new char[eolCount];
	for (int i = 0; i < eolCount; i++) {
	    eol[i] = separator.charAt(i);
	}
	*/


	/* not so useful
    public boolean isLocal() {
	if (!isApplet()) return true;
	String codebase = getCodeBase().toString();
	return (codebase.indexOf("file") == 0);
    }
	*/


    /* temporary, a little something for the kids */
    /*
    static public void debugString(String s) {
	byte output[] = s.getBytes();
	for (int i = 0; i < output.length; i++) {
	    if (output[i] >= 32) {
		System.out.print((char)output[i]);
	    } else {
		System.out.print("\\" + (int)output[i]);
		if (output[i] == '\n') System.out.println();
            }
        }
	System.out.println();
    }
    */
