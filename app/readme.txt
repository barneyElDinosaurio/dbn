DESIGN BY NUMBERS RELEASE NOTES
Version 3.0.1, Released November 9, 2001
http://dbn.media.mit.edu * dbn-feedback@media.mit.edu


* FINAL VERSION OF DBN
We intend for version 3.0.1 to be the final release of 
the Design By Numbers system. While there may be upgrades 
for important bug fixes, this is the last version that
will include new features. (In fact, version 3.0 was 
intended as the final release, but a major bug was found,
and 3.0.1 was released)


* QUESTIONS?
See the DBN web site, dbn.media.mit.edu, especially the
'frequently asked questions' page: dbn.media.mit.edu/faq.html
If your question is not covered there, send mail to the
DBN maintainers at dbn-feedback@media.mit.edu.


* DBNGRAPHICS.DBN and DBNLETTERS.DBN
These modules provide additional graphics functions
and primitive letter drawing, as documented in the book.
The files are included in the download, so to use them, 
add 'load dbnletters.dbn' or 'load dbngraphics.dbn' to 
the beginning of your program to include their functions.


* MACINTOSH PEOPLE READ THIS!
The Macintosh version requires Macintosh Runtime for 
Java (MRJ), 2.2.x (or newer). This can be downloaded 
from Apple at http://www.apple.com/java. Previous 
versions of MRJ were incompatible, slow and buggy, so 
you should definitely be using the most recent version. 
MRJ in fact continues to be somewhat incompatible, slow 
and buggy, though Apple's small team of developers is 
slowly chipping away at the problems.

Also, make sure that the date on your Macintosh is not
set earlier than 1970. There is a problem with MRJ that
will cause your machine to behave highly erratically 
under this condition.


* WINDOWS PEOPLE READ THIS!
The Windows version of the DBN download includes JRE,
the Java Runtime Environment. This means that you won't
have to download anything to get DBN up and running. To
start DBN, just double-click the file titled 'dbn.bat'.

If you are using Windows 95, 98 (or perhaps even ME), 
you may have trouble with using the run file. If the
window opens and then closes quickly, instead move
dbn to your C drive, and try the dbn95.bat file. More 
information can be found in the DBN FAQ:
http://dbn.media.mit.edu/faq.html


* HIDDEN FEATURES INSIDE DBN
There are several hidden features inside DBN that may be
of interest to advanced users. Visit the 'hidden features'
section of the web site for more information:
http://dbn.media.mit.edu/info/hidden.html


* KNOWN ISSUES
There is a problem with functions that are too heavily
recursive. You will know you have run up against this 
problem if you get a "java.lang.StackOverFlowError" 
after your program has been running for a while. A fix
for this would require major work on dbn's internal engine,
and it's not clear if the problem could truly be resolved.


* IF YOU HAVE PROBLEMS
We're working to iron out any inconsistencies and 
outstanding issues. If you run across what appears to be
a bug, send email with the following:
1. What you were doing at the time (i.e. a copy of the 
program that you were working on)
2. Description of your system setup: Windows 95/98/NT 
or what version of MacOS, version of MRJ if Mac, etc.
3. Steps to reproduce the problem
Send this mail to dbn-feedback@media.mit.edu


* PEOPLE
DBN is the product of many people. Benjamin Fry is the 
chief architect of the most current release. DBN 1.0.1 
was created by Tom White. The original version DBN 1.0 
was created by John Maeda. The courseware system was 
developed by Casey Reas, and translated to Japanese by
Kazuo Ohno. Other people that have contributed to DBN 
development are Peter Cho, Elise Co, Lauren Dubick, 
Golan Levin, Jocelyn Lin, and Josh Nimoy.


LICENSE
Massachusetts Institute of Technology ("MIT") hereby grants 
permission for you to copy and use Design by Numbers (DBN) 
software. MIT shall retain all right, title and interest, 
including copyright, in and to the Design by Numbers (DBN) 
software. 

THE Design by Numbers (DBN) SOFTWARE IS PROVIDED TO YOU "AS IS," 
AND MIT MAKES NO EXPRESS OR IMPLIED WARRANTIES WHATSOEVER WITH 
RESPECT TO ITS FUNCTIONALITY, OPERABILITY, OR USE, INCLUDING, 
WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE, OR INFRINGEMENT. MIT EXPRESSLY 
DISCLAIMS ANY LIABILITY WHATSOEVER FOR ANY DIRECT, INDIRECT, 
CONSEQUENTIAL, INCIDENTAL OR SPECIAL DAMAGES, INCLUDING, WITHOUT 
LIMITATION, LOST REVENUES, LOST PROFITS, LOSSES RESULTING FROM 
BUSINESS INTERRUPTION OR LOSS OF DATA, REGARDLESS OF THE FORM 
OF ACTION OR LEGAL THEORY UNDER WHICH THE LIABILITY MAY BE 
ASSERTED, EVEN IF ADVISED OF THE POSSIBILITY OR LIKELIHOOD OF 
SUCH DAMAGES. 


CHANGES (technical section that you're welcome to ignore)


Version 3.0.1: Changes since 3.0

- fixed a bug where getting and setting pixels was returning
  numbers that were backwards (100 - the correct number).
  thanks to all the people who pointed this out.


Version 3.0: Changes since 2.0.1

- changed .bat file to be called 'dbn'
- added dbn95.bat for common win95 error
- removed the need for 'norefresh'
- disabled antialias feature (it was poorly implemented)
- added automatic-slowdown feature
- added ability to disable slowdown feature
- ability to save tiff files
- support for color commands: pen, paper, get/set [ ]
- changing of dbn drawing area using the 'size' command
- ability to magnify drawing area
- dbnletters was broken.. it used functions called typeA,
  typeB, typeC; but the book used letterA, letterB, letterC
  the new dbnletters is based on the book examples
- fixed a bug for the example on page 57 of the book, 
  where code inside curly braces would fail to run
- made fixes so that the new DBN works with the courseware
- changed threading so that DBN stops hesitating every
  once in a while while running.


Version 2.0.1: Changes since 2.0

- oops! Andrew Otwell pointed out that <key> was having
  difficulties (thanks Andrew), because of an oversight
  by the programmer who was in too much of a hurry to get
  DBN2K out the door (boo Ben). this has been fixed.


Version 2.0: Changes since 1.3

- whizzy new user interface
- rewrite of everything else that wasn't redone for 1.2
- semi-automatic indenting of lines
- tabs magically turn into spaces
- no more unsightly boxes at the end of selected lines (win95/nt)
- correct line number used from python stack traces (courseware)


Version 1.3: Changes since 1.2.1

- added printing feature for downloadable dbn
- drawing is now faster on the mac and elsewhere
- added norefresh to DbnGraphics
- re-oriented the screen layout
- disabled paren balancing code for mac (paste causes crash)
- added parenthesis balancing code
- default directory and filename for file saving are
  now being respected or set.
- fixed a bug that would cause dbn to die if the user
  tried to divide by zero
- fixed a bug where field was not properly setting the 
  values of the screen's [x y] image array
- refixed that bug again because it hadn't been completely fixed
- fixed a bug where recursion was not working properly
- turns off anti-alias between program executions,
  before it wasn't paying attention and leaving
  anti-alias turned on for the rest of the dbn session


Version 1.21: Minor changes since 1.2

- this version will only be updated on the website,
  since the bug fix doesn't affect the downloadable version
- moved the application code out of DbnApplet so that it
  works with Netscape Navigator


Version 1.2: Changes and fixes since version 1.01

- (major) new parser and execution engine
  increased stability and error messages for syntax problems
- Multi-file viewing 'griddify' function
  view many dbnlets within a single applet
- Won't show snapshot menu option if "user" parameter not specified
- pgm file i/o methods replacing old snapshot code,
  code that talks to the dbn courseware better


Version 1.01: Changes and fixes since version 1.0

- Fixed end of line bug. If a program had a comment at
  the end of its final line, the line would not be read by
  the parser (end-of-line fix for multiple platforms)

