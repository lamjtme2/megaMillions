#!/usr/bin/perl
use strict;
use warnings;

my %hb = ();
my %hmb = ();

sub addBall {
	my $b = shift @_;
	
	if (exists($hb{$b})) {
		$hb{$b} = $hb{$b} + 1;
	} else {
		$hb{$b} = 1;
	}
}

sub addMegaBall {
	my $mb = shift @_;
	
	if (exists($hmb{$mb})) {
		$hmb{$mb} = $hmb{$mb} + 1;
	} else {
		$hmb{$mb} = 1;
	}
}

print STDERR "Reading <$ARGV[0]>\n";
my $drawsAdded = 0;
my $drawsRead = 0;

# ----------------------------------------------------
# On/Up to 10/18/2013 --------- 5/56 --- 1/46
# 10/18/2013 to 10/31/2017------5/75 --- 1/15
# On/After 10/31/2017 ----------5/70 ----1/25 --- 20171031
# ----------------------------------------------------

my $finName = $ARGV[0];
my $foutBall = sprintf("%s.ball.txt",$finName);
my $foutMegaBall = sprintf("%s.megaball.txt",$finName);
open(FIN, '<', $finName) or die "Unable to open $_";
while (my $ln = <FIN>) {
	if ($ln=~/^(\d+)\/(\d+)\/(\d{4});\s+(\d+),(\d+),(\d+),(\d+),(\d+); Mega Ball: (\d+)/) {
		my $dtm = $1;
		my $dtd = $2;
		my $dty = $3;
		my $m1 = $4;
		my $m2 = $5;
		my $m3 = $6;
		my $m4 = $7;
		my $m5 = $8;
		my $mb = $9;
		
		my $dt = sprintf("%04d%02d%02d", $dty,$dtm,$dtd);
		if ($dt ge "20171031") {
#			print STDERR "$dt\n";
			addBall($m1);
			addBall($m2);
			addBall($m3);
			addBall($m4);
			addBall($m5);
			addMegaBall($mb);
			$drawsAdded++;
		}
		$drawsRead++;
	}	
}
close FIN;
my $maxB = 70; #max(keys %hb);
open(FOUTB, '>', $foutBall) or die "Unable to write ball file $_";
for my $b (1..$maxB) {
	my $v = 0;
	if (exists($hb{$b})) {
		$v = $hb{$b};
	}
	printf FOUTB "%2d,%4d, %s\n", $b,$v, "*" x $v;
}
close(FOUTB);
my $maxMB = 25; #max(keys %hmb);
open(FOUTMB, '>', $foutMegaBall) or die "Unable to write megaball file $_";
for my $mb (1..$maxMB) {
	my $v = 0;
	if (exists($hmb{$mb})) {
		$v = $hmb{$mb};
	}
	printf FOUTMB "%2d,%4d, %s\n", $mb,$v, "*" x $v;
}
close(FOUTMB);

print STDERR "$drawsAdded/$drawsRead draws added\n";
