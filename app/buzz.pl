#!/perl/bin/perl

# needs to make a temporary directory, compile into that
# clear out contents of temporary directory at begin of compile
# create temp directory if it doesn't exist

# should take arguments for the compiler:
# jikes -d classes *.java
# instead looks like
# buzz "jikes -d classes" *.java
# maybe everything always goes in /tmp? 
# (no, don't want to leave code around)

$blank_line = "\n";

$temp_dir = "buzztemp";
if (-d $temp_dir) {
    `rm -rf $temp_dir`;
}
mkdir($temp_dir, 0777) || die $!;

if ($ENV{'WINDIR'} ne '') {
    $separator = "\\";
    $windows = 1;
} else {
    $separator = '/';
    $unix = 1;
}

#print "args = @ARGV\n";
$command = shift(@ARGV);
if ($command eq '') {
    print "buzz.pl: perl is misconfigured.. no args passed in.. can't run\n";
    print "         cygwin perl seems to have problems, use activestate\n";
    exit;
}

if ($command =~ /-classpath/) {
    die "cannot set classpath using this version of buzz";
}
$classpath = $ENV{"CLASSPATH"};
if ($classpath eq "") {
    # find java in the path
    if ($windows) {
	@elements = split(';', $ENV{"PATH"});
	foreach $element (@elements) {
	    #print "trying $element\\java.exe\n";
	    if (-f "$element\\java.exe") {
		$classpath = "$element\\..\\lib\\classes.zip";
		print "found java: $element\\java.exe\n";
		last;
	    }
	}
	if ($classpath eq "") {
	    die "java.exe is not in your path, and classpath not set";
	}
    } else {
	die "code for searching path not written for unix";
    }
}


# if target directory, -d, option is used, add it to CLASSPATH
if ($command =~ /\-d\s(\S*)/) {
    if ($windows) {
	$classpath = "$1;$classpath";
    } else {
	$classpath = "$1:$classpath";
    }
}

foreach $arg (@ARGV) {
    if ($arg =~ /^-d(.*)/) {
	$params{$1} = 1;
    #} elsif ($arg =~/^-c(.*)/) {
	#$compiler = $1;
    } elsif ($arg =~ /\.java$/) {
	if ($arg =~ /(.*)\*\.java$/) {
	    # gotta expand * to all matching
	    #print "expanding *.java from \"$1\"\n";
	    $dir = $1;
	    if ($dir eq "") {
		$dir = '.';
	    } else {
		#print "creating dir $temp_dir$separator$dir\n";
		mkdir("$temp_dir$separator$dir", 0777) || die $!;
	    }
	    opendir(DIR, $dir) || die $!;
	    @dcontents = readdir(DIR);
	    closedir(DIR);
	    foreach $file (@dcontents) {
		if ($file =~ /\.java$/) {
		    if ($dir eq '.') {
			$fullname = "$file";
		    } else {
			$fullname = "$dir$file";
		    }
		    #print "adding $fullname\n";
		    unshift @file_list, "$fullname";
		}
	    }
	    
	} else {
	    unshift @file_list, $arg;	
	}
    }
}

# support: define, ifdef, ifndef, else, endif
# no support: defined(x), elif, #define blah 12, nesting

print "processing...\n";
foreach $file (@file_list) {
    open(FILE, "$file") || die "error with $file, $!";
    @contents = <FILE>;
    close(FILE);

    @new_contents = ();
    &read_positive;

    open(OUTPUT, ">$temp_dir$separator$file") || die $!;
    print OUTPUT reverse(@new_contents);
    close(OUTPUT);
    unshift(@new_file_list, "$temp_dir$separator$file");
}

print "compiling...\n";
$files = join(' ', @new_file_list);
$compile_command = "$command -classpath $classpath $files";
#print "$compile_command\n";
print `$compile_command`;

# clean up
print "cleaning...\n";
`rm -rf $temp_dir`;

# finished
print "done.\n";


# reads until else or endif, adding what it finds
# to the new output file
sub read_positive {
    my $line;
    while ($line = shift(@contents)) {
	if ($line =~ /$\#if(\w*)def\s+(\S+)/) {
	    unshift(@new_contents, $blank_line);
	    if ((($1 eq "") && ($params{$2} == 1)) ||   #ifdef found
		(($1 eq "n") && ($params{$2} != 1))) {  #ifndef found
		# include until endif/else
		&read_positive;
		#return;

	    } else {
		# exclude until endif/else
		&read_negative(0);
                #return;
	    }
	} elsif ($line =~ /$\#else/) {  
	    unshift(@new_contents, $blank_line);
	    &read_negative(0);
	    return;

        } elsif ($line =~ /$\#endif/) {
	    unshift(@new_contents, $blank_line);
	    return;

	} elsif ($line =~ /$\#define\s+(\S+)/) {
	    $params{$1} = 1;
	    unshift(@new_contents, $blank_line); # maintain lf count

	} else {
	    unshift(@new_contents, $line);  # no change
	}
    }
}


# excludes everything until an else or an endif
sub read_negative {
    my ($inside_negative) = @_[0];
    my $line;
    while ($line = shift(@contents)) {
	if ($line =~ /$\#if(\w*)def\s+(\S+)/) {
	    unshift(@new_contents, $blank_line);
	    if ((($1 eq "") && ($params{$2} == 1)) ||   #ifdef found
		(($1 eq "n") && ($params{$2} != 1))) {  #ifndef found
		#&read_positive;
		&read_negative(1);
		
	    } else {
		# exclude until endif/else
		&read_negative(1);
	    }
	} elsif ($line =~ /$\#else/) {
	    unshift(@new_contents, $blank_line);
	    if ($inside_negative) {
		&read_negative(1);
	    } else {
		&read_positive;
	    }
	    return;
		 
        } elsif ($line =~ /$\#endif/) {
	    unshift(@new_contents, $blank_line);
	    return;

	#} elsif ($line =~ /$\#define\s+(\S+)/) {
	#   unshift(@new_contents, $blank_line); # maintain lf count

	} else {
	    # blank line, maintain lf count
	    unshift(@new_contents, $blank_line);
	}
    }
}

