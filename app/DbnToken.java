import java.io.*;
import java.util.*;


// SIZE and SAVE have not been added to java converter 

public class DbnToken {
  DbnToken parent;

  int kind;
  String name;
  int number;

  int line;  // line number for token

#ifdef VIS
  // for tree output
  static int serialIndex = 1;
  int serialNumber;
  String serialName;
#endif

  int childCount;
  DbnToken children[];

  // root has globals, functions have locals, repeat has var
  Hashtable variables;

  // only root has functions, null everywhere else
  Hashtable functions;

    
  static final int EOL            = 0;
  static final int NUMBER         = 1;
  static final int NAME           = 2;
  static final int PIXEL          = 3;
  static final int VARIABLE       = 4;
  static final int BLOCK          = 5;
  static final int VALUE          = 6;
  static final int MATH           = 7;
  static final int STACK_VARIABLE = 8;

  static final int ADD      = 10;
  static final int SUBTRACT = 11;
  static final int MULTIPLY = 12;
  static final int DIVIDE   = 13;
  static final int OPERATOR = 14;
  static final int MODULO   = 15;

  static final int INPUT_CONNECTOR  = 20;
  static final int OUTPUT_CONNECTOR = 21;

  static final int COMMAND_DEF  = 30;
  static final int FUNCTION_DEF = 31;
  static final int RETURN_VALUE = 32;
  static final int COMMAND      = 33;
  static final int FUNCTION     = 34;

  static final int REPEAT  = 40;
  static final int FOREVER = 41;

  static final int SET     = 50;
  static final int PAPER   = 51;
  static final int PEN     = 52;
  static final int LINE    = 53;
  static final int FIELD   = 54;
  static final int PAUSE   = 55;
  static final int REFRESH = 56;
  static final int SIZE    = 57;
  static final int SAVE    = 58;

  static final int ANTIALIAS   = 80;  // the deprecated list
  static final int NOREFRESH   = 81;
  static final int FAST        = 82;

  static final int SMALLER     = 60;
  static final int NOT_SMALLER = 61;
  static final int SAME        = 62;
  static final int NOT_SAME    = 63;

  static final int ROOT        = 70;
  static final int STATEMENT   = 71;
  static final int STATEMENTS  = 72;


  DbnToken() { }

  DbnToken(int kind, int line) {
    this.kind = kind;
    this.line = line;
  }

  DbnToken(int kind, String name, int line) {
    this.kind = kind;
    this.name = name;
    this.line = line;
  }
  
  DbnToken(int kind, int number, int line) {
    this.kind = kind;
    this.number = number;
    this.line = line;
  }


  public Object clone() {  // for parser (multiple/single arg stuff)
    DbnToken dolly = new DbnToken();
    dolly.kind = kind;
    dolly.name = name;
    dolly.number = number;
    dolly.line = line;

    dolly.childCount = childCount;
    dolly.children = new DbnToken[children.length];
    System.arraycopy(children, 0, dolly.children, 0, childCount);

    return dolly;
  }


  void addChild(DbnToken newbie, int line) {
    if (children == null) {
      children = new DbnToken[10];
    } else if (childCount == children.length) {
      DbnToken temp[] = new DbnToken[childCount*2];
      System.arraycopy(children, 0, temp, 0, childCount);
      children = temp;
    }
    newbie.parent = this;
    newbie.line = line;
    children[childCount++] = newbie;
  }

  DbnToken addChild(int kind, int line) {
    DbnToken newbie = new DbnToken(kind, line);
    addChild(newbie, line);
    return newbie;
  }

  DbnToken addChild(int kind, String name, int line) {
    DbnToken newbie = new DbnToken(kind, name, line);
    addChild(newbie, line);
    return newbie;
  }

  DbnToken addChild(int kind, int number, int line) {
    DbnToken newbie = new DbnToken(kind, number, line);
    addChild(newbie, line);
    return newbie;
  }


  DbnToken addVariable(String title, int line) {
    if (variables == null) variables = new Hashtable();

    // this will store the value for the execution engine
    DbnToken newbie = new DbnToken(VARIABLE, title, line);
    variables.put(title, newbie);
    return newbie;
  }


  DbnToken findVariable(String name) {
    if (variables != null) {
      DbnToken guess = (DbnToken) variables.get(name);
      if (guess != null) return guess;
    }
    return (parent != null) ? parent.findVariable(name) : null;
  }


  DbnToken findFunction(String name) {
    if (functions != null) {
      DbnToken guess = (DbnToken) functions.get(name);
      if (guess != null) return guess;
    }
    return (parent != null) ? parent.findFunction(name) : null;
  }

  boolean findToken(int tokenKind) {
    if (kind == tokenKind) return true;
    for (int i = 0; i < childCount; i++) {
      if (children[i].findToken(tokenKind)) 
	return true;
    }
    return false;
  }

#ifndef KVM
  public void print() {
    System.out.println(toString());
  }

  public String toString() {
#ifdef VIS
    System.out.println("N START");
    return toString(0, "START");
#else
    return toString(0, null);
#endif
  }
    
  public String toString(int indentCount, String vparent) {
	
    StringBuffer buffer = new StringBuffer();

#ifdef VIS
    serialNumber = serialIndex++;
    buffer.append(vparent + "/");
    buffer.append(serialNumber);
    buffer.append("_");
#else
    for (int i = 0; i < indentCount; i++) 
      buffer.append(' ');
#endif	

    switch(kind) {
    case EOL: buffer.append("EOL"); break;
    case NUMBER: buffer.append("NUMBER " + number); break;
    case NAME: buffer.append("NAME " + name); break;
    case PIXEL: buffer.append("PIXEL"); break;
    case VARIABLE: buffer.append("VARIABLE " + name); break;
    case BLOCK: buffer.append("BLOCK"); break;
    case VALUE: buffer.append("VALUE"); break;
    case MATH: buffer.append("MATH"); break;
    case STACK_VARIABLE: 
      buffer.append("STACK_VARIABLE " + number); break;

    case ADD: buffer.append("ADD"); break;
    case SUBTRACT: buffer.append("SUBTRACT"); break;
    case MULTIPLY: buffer.append("MULTIPLY"); break;
    case DIVIDE: buffer.append("DIVIDE"); break;
    case OPERATOR: buffer.append("OPERATOR"); break;
    case MODULO: buffer.append("MODULO"); break;

    case INPUT_CONNECTOR: 
      buffer.append("INPUT_CONNECTOR " + name); break;
    case OUTPUT_CONNECTOR: 
      buffer.append("OUTPUT_CONNECTOR " + name); break;

    case COMMAND_DEF: buffer.append("COMMAND_DEF " + name); break;
    case FUNCTION_DEF: buffer.append("FUNCTION_DEF " + name); break;
    case RETURN_VALUE: buffer.append("RETURN_VALUE"); break;
    case COMMAND: buffer.append("COMMAND " + name); break;
    case FUNCTION: buffer.append("FUNCTION " + name); break;

    case REPEAT: buffer.append("REPEAT"); break;
    case FOREVER: buffer.append("FOREVER"); break;

    case SET: buffer.append("SET"); break;
    case PAPER: buffer.append("PAPER"); break;
    case PEN: buffer.append("PEN"); break;
    case LINE: buffer.append("LINE"); break;
    case FIELD: buffer.append("FIELD"); break;
    case PAUSE: buffer.append("PAUSE"); break;
    case REFRESH: buffer.append("REFRESH"); break;
    case FAST: buffer.append("FAST"); break;
    case SIZE: buffer.append("SIZE"); break;
    case SAVE: buffer.append("SAVE"); break;

      // deprecated pals
    case NOREFRESH: buffer.append("NOREFRESH"); break;
    case ANTIALIAS: buffer.append("ANTIALIAS"); break;

    case SMALLER: buffer.append("SMALLER"); break;
    case NOT_SMALLER: buffer.append("NOT_SMALLER"); break;
    case SAME: buffer.append("SAME"); break;
    case NOT_SAME: buffer.append("NOT_SAME"); break;

    case STATEMENT: buffer.append("STATEMENT"); break;
    case STATEMENTS: buffer.append("STATEMENTS"); break;
    case ROOT: buffer.append("ROOT"); break;

    default: 
      System.err.println("not handled1: " + kind);
      System.exit(1);
    }

#ifndef VIS	
    if (variables != null) {
      buffer.append(" variables: ");
      Enumeration e = variables.keys();
      while (e.hasMoreElements()) {
	buffer.append((String) e.nextElement());
	buffer.append(' ');
      }
    }
    buffer.append(System.getProperty("line.separator"));

    for (int i = 0; i < childCount; i++) {
      buffer.append(children[i].toString(indentCount + 2, null));
    }
    return buffer.toString();
#else
    serialName = buffer.toString().replace(' ', '%');

    StringBuffer output = new StringBuffer();
    output.append("N " + serialName);
    output.append(System.getProperty("line.separator"));

    output.append("B " + parent + " " + serialName);
    output.append(System.getProperty("line.separator"));
	
    for (int i = 0; i < childCount; i++) {
      output.append(children[i].toString(0, serialName));
    }
    //return output.toString().replace(' ', '%');
    return output.toString();
#endif
  }


#ifdef CONVERTER
  static boolean shouldOutputFunctions;
  static boolean insideLoop;

  public String convert(String classname) {
    //System.err.println(kind);
    switch (kind) {

    case ROOT:
      cbuffer = new StringBuffer();
      outputln("public class " + classname + " extends DbnPlayer");
      outputln("{");
      moreIndent();
	    
      //outputln("public " + classname + "(DbnApplet applet)");
      //outputln("{");
      //moreIndent();
      //outputln("super(applet);");
      //lessIndent();
      //outputln("}");
      //outputln();

      shouldOutputFunctions = true;
      Enumeration e = functions.elements();
      while (e.hasMoreElements()) {
	Object object = e.nextElement();
	if (object instanceof DbnToken) {
	  DbnToken function = (DbnToken) object;
	  function.convert(null);
	  outputln();
	}
      }

      shouldOutputFunctions = false;
      convertVariables();  // so that they're global
      outputln("public void execute() throws DbnException");
      outputln("{");
      moreIndent();
      //convertVariables();  // needs to be global to the class
      convertChildren();
      lessIndent();
      outputln("}");
      outputln();

      outputln("long lastSleepTime;");
      outputln("private final void sleepIfTired()");
      outputln("{");
      moreIndent();
      outputln("long t = System.currentTimeMillis();");
      outputln("if (t - lastSleepTime < 1000) return;");
      outputln("try {");
      moreIndent();
      outputln("thread.sleep(5);");
      outputln("lastSleepTime = t + 5;");
      lessIndent();
      outputln("} catch (InterruptedException e) { }");
      lessIndent();
      outputln("}");

      lessIndent();
      outputln("}");
      return cbuffer.toString();
      //break;
	    
    case NUMBER: output(String.valueOf(number)); break;
    case NAME: output(name); break;

    case PIXEL: 
      output("graphics.getPixel(");
      convertChild(0);
      output(", ");
      convertChild(1);
      output(")");
      break;
	    
    case VARIABLE: 
      if (name != null) {
	output(name);
      } else {
	convertChild(0);
      }
      break;

    case BLOCK: 
      outputln("{");
      moreIndent();
      if (insideLoop) {
	outputln("if (state != RUNNER_STARTED) return;");
	outputln("sleepIfTired();");
	insideLoop = false;
      }
      if ((parent.kind == COMMAND_DEF) ||
	  (parent.kind == FUNCTION_DEF)) {
	parent.convertVariables();
      }
      convertChildren();
      lessIndent();
      outputln("}");
      break;
	    
    case VALUE: convertChild(0); break;
	
    case MATH:
      output("(");
      convertChildren();
      output(")");
      break;

    case ADD: output("+"); break;
    case SUBTRACT: output("-"); break;
    case MULTIPLY: output("*"); break;
    case DIVIDE: output("/"); break;
    case MODULO: output("%"); break;

    case OPERATOR: convertChild(0); break;

    case INPUT_CONNECTOR: 
      String connector = name;
      String title = Character.toUpperCase(connector.charAt(0)) +
	connector.substring(1);
      output("graphics.get" + title + "(");
      //output("graphics.getConnector(\"" + name + "\", ");
      convertChild(0);
      output(")");
      break;

    case COMMAND_DEF:
    case FUNCTION_DEF:
      if (shouldOutputFunctions) {
	if (kind == COMMAND_DEF) 
	  output("void " + name + "(");
	else 
	  output("int " + name + "(");
	int paramCount = childCount-1;
	for (int i = 0; i < paramCount; i++) {
	  output("int " + children[i].name);
	  if (i != paramCount-1) output(", ");
	}
	outputln(") throws DbnException");  // just in case
	convertChild(paramCount); // it's a block
	//System.out.println(variables);
      }
      break;

    case RETURN_VALUE: 
      output("return ");
      convertChild(0);
      outputln(";");
      break;

    case FUNCTION:
    case COMMAND:
      output(name + "(");
      for (int i = 0; i < childCount; i++) {
	convertChild(i);
	if (i != childCount-1) output(", ");
      }
      output(")");
      if (kind == COMMAND) outputln(";");
      break;
	    
    case REPEAT:
      // doesn't check to see if it's local 
      // cannot do loops going downward
      boolean goingUp = true;
      if ((children[1].children[0].kind == NUMBER) && 
	  (children[2].children[0].kind == NUMBER)) {
	int a = children[1].children[0].number;
	int b = children[2].children[0].number;
	goingUp = (a < b);
      }
      /*
	start, stop
	inc = (start <= stop) 1 : -1;
	for (int i = start;
	(((inc ==  1) && (i <= finish)) ||
	((inc == -1) && (i >= finish)));
	i += inc) {
	// blah
      */
      output("for (");
      if (findVariable(children[0].name) == null) 
	output("int ");
      convertChild(0);
      output(" = ");
      convertChild(1);
      output("; ");
      convertChild(0);
      output(goingUp ? " <= " : " >= ");
      convertChild(2);
      output("; ");
      convertChild(0);
      outputln(goingUp ? "++)" : "--)");
      insideLoop = true;
      convertChild(3);
      break;

    case FOREVER: 
      outputln("while (state == RUNNER_STARTED)");
      insideLoop = true;
      convertChild(0);
      break;

    case SET:
      DbnToken variable = children[0];
      if (variable.childCount == 0) {
	output(variable.name);
	output(" = ");
	convertChild(1);
	outputln(";");
      } else if (variable.children[0].kind == PIXEL) {
	output("graphics.setPixel(");
	variable.children[0].convertChild(0);
	output(", ");
	variable.children[0].convertChild(1);
	output(", ");
	convertChild(1);
	outputln(");");
      } else if (variable.children[0].kind == OUTPUT_CONNECTOR) {
	connector = variable.children[0].name;
	title = Character.toUpperCase(connector.charAt(0)) +
	  connector.substring(1);
	output("graphics.set" + title + "(");
	//output("graphics.setConnector(\"");
	//output(variable.children[0].name);
	//output("\", ");
	//convertChild(1);
	variable.children[0].convertChild(0);
	output(", ");
	//System.out.println(cbuffer.toString());
	//variable.children[1].
	convertChild(1); // what it should be set to
	outputln(");");
      } else {
	System.err.println("not handled2 " + children[0].kind);
	System.exit(1);
      }
      break;
	    
    case PAPER:
      output("graphics.paper(");
      convertChild(0);
      outputln(");");
      break;

    case PEN:
      output("graphics.pen(");
      convertChild(0);
      outputln(");");
      break;

    case LINE: 
      output("graphics.line(");
      convertChild(0);
      output(", ");
      convertChild(1);
      output(", ");
      convertChild(2);
      output(", ");
      convertChild(3);
      outputln(");");
      break;

    case FIELD:
      output("graphics.field(");
      convertChild(0);
      output(", ");
      convertChild(1);
      output(", ");
      convertChild(2);
      output(", ");
      convertChild(3);
      output(", ");
      convertChild(4);
      outputln(");");
      break;

    case PAUSE:
      output("graphics.pause(");
      convertChild(0);
      outputln(");");
      break;

    case ANTIALIAS:
      output("graphics.pause(");
      convertChild(0);
      outputln(");");
      break;

    case REFRESH:
      outputln("graphics.refresh();");
      break;

    case NOREFRESH:
      outputln("graphics.norefresh();");
      break;

    case SMALLER:
    case NOT_SMALLER:
    case SAME:
    case NOT_SAME:
      output("if (");
      convertChild(0);
      if (kind == SMALLER) output(" < ");
      else if (kind == NOT_SMALLER) output(" >= ");
      else if (kind == SAME) output(" == ");
      else if (kind == NOT_SAME) output(" != ");
      convertChild(1);
      outputln(")");
      convertChild(2);
      break;

    case STATEMENT: 
    case STATEMENTS: 
      convertChildren(); 
      break;

    default: 
      System.err.println("not handled3: " + kind);
      System.exit(1);
    }
    return null;
  }


  // could also comma-separate
  private void convertVariables() {
    if (variables == null) return;
    boolean hasVariables = false;

    Enumeration e = variables.keys();
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      boolean shouldOutput = true;
      // don't the var if it's a function parameter
      if ((kind == COMMAND_DEF) || (kind == FUNCTION_DEF)) {
	int paramCount = childCount - 1;
	for (int i = 0; i < paramCount; i++) {
	  if (children[i].name.equals(name)) {
	    shouldOutput = false;
	  }
	}
      }
      if (shouldOutput) {
	outputln("int " + name + ";");
	hasVariables = true;
      }
    }
    if (hasVariables) {
      outputln();
    }
  }

  private void convertChild(int which) {
    children[which].convert(null);
  }

  private void convertChildren() {
    for (int i = 0; i < childCount; i++) {
      children[i].convert(null);
    }
  }

  static int indentCount = 0;
  static String indent = "";
  static boolean shouldIndent = true;
  static char spaces[] = new char[128];
  static {
    for (int i = 0; i < spaces.length; i++) {
      spaces[i] = ' ';
    }
  }

  private void moreIndent() {
    indentCount += 2;
    updateIndent();
  }
    
  private void lessIndent() {
    indentCount -= 2;
    updateIndent();
  }
    
  private void updateIndent() {
    StringBuffer temp = new StringBuffer();
    for (int i = 0; i < indentCount; i++) {
      temp.append(' ');
    }
    indent = temp.toString();
  }    

  static StringBuffer cbuffer;
    
  private void outputln() {
    cbuffer.append(System.getProperty("line.separator"));
    shouldIndent = true;
  }

  private void output(String str) {
    if (shouldIndent) {
      cbuffer.append(indent);
      shouldIndent = false;
    }
    cbuffer.append(str);
  }

  private void outputln(String str) {
    output(str);
    outputln();
  }
#endif // ndef CONVERTER
#endif // KVM
}
