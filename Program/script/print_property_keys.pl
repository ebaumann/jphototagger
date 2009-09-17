#!/usr/bin/perl
#
# Author: Elmar Baumann
# Date  : 2009/09/03
#
# Prints property keys in Java files. For usage in pipe.
#
# Bugs: 
#        * Only one quotation per line supported (else output is false)
#        * Key must start with A-Za-z
#
################################################################################

use strict;

my @lines;

while (<>) {
    $_ =~ s/.*"(.*\").*/"$1/g; # Greedy! (only correct if one "..." per line
	$_ =~ s/^\s+|\s+$//g; # trim
    push @lines, $_;
}

foreach my $line (@lines) {
    next if $line =~ /\//  # Paths "/de/elmar_baumann/jpt/icon.png"
        || $line =~ /\"\./ # ".bla"
        || $line =~ /\.\./ # ".."
        ;
    if ($line =~ /\".*\..*\"/) { # "..."
        $line =~ s/^\"|\"$//g;   # Removing "
        if ($line =~ /^[A-Za-z]/) {
            print "$line\n";
        }
    }
}
