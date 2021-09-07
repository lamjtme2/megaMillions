#!/usr/bin/perl
use strict;
use warnings;

use File::Find;
use File::Basename;
use File::Copy;
use File::Path qw(make_path);
use Cwd qw(getcwd);

my @hb;

# -------------------------------------
sub addHit {
	my $m1 = shift @_;
	my $m2 = shift @_;
	my $m3 = shift @_;
	my $m4 = shift @_;
	my $m5= shift @_;
	my $mb = shift @_;

	my @hblocal = ($m1, $m2, $m3, $m4, $m5,$mb);
	push @hb, \@hblocal;
}

# read last N rows, compare to guesses, score, delay, and repeat
my $finName = $ARGV[0];
my $keepLast = $ARGV[1];
my $pathSource = $ARGV[2];
my $pathBase = getcwd();
my $pathDest =  $pathBase."/rank/";
my $pathTemp = $pathBase."/temp";
#print STDERR "$finName\n$keepLast\n$pathSource -> $pathDest\n";

my @inFiles;
find ( { wanted => sub { if (-f $_ && $_=~/\.txt$/) { push @inFiles, $_ } }, no_chdir => 1 }, $pathSource );
#for my $if (@inFiles) {
#	printf STDERR "$if\n";
#}
print "\n".scalar(@inFiles)." guess files detected.\n";

my $foutBall = sprintf("%s.ball.txt",$finName);
my $foutMegaBall = sprintf("%s.megaball.txt",$finName);
open(FIN, '<', $finName) or die "Unable to open $_";

while (my $ln = <FIN>) {
	if ($ln=~/^(\d+)\/(\d+)\/(\d{4});\s+(\d+),(\d+),(\d+),(\d+),(\d+); Mega Ball: (\d+)/)  {
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
			addHit($m1,$m2,$m3,$m4,$m5,$mb);
		}
	}	
}
close FIN;

# truncate to the last N
my $drop = scalar(@hb)-$keepLast;
#print STDERR "Dropping $drop of ".scalar(@hb)."\n";
for (my $i=0; $i<$drop; $i++) {
	# popping instead of shift because the most recent is at the beginning of the file
	pop  @hb;
}

printf "\nLoaded last historical hits:\n";
my $aref = \@hb;
for (my $i=0; $i<scalar(@hb); $i++ ) {
	print "$i: ";
	foreach  my $j (@{$aref->[$i]}) {
		print  "$j, ";
	}
	print "\n";
}

# -------------------------------------
sub CheckRowHit {
	my $aRefRow = shift @_;
	
	my $rankHist = 0;
	for (my $i=0; $i<scalar(@hb); $i++ ) {
		# for every hit history row
		my $rankRow= 0;
		for (my $j=0; $j<@{$aref->[$i]}; $j++) {
			# for every hit history row column
			my $mlc = $aRefRow->[$j];
			my $rlc = $aref->[$i]->[$j];
#			print "c[ $mlc:$rlc], "; 
			if ($mlc eq $rlc) {
				$rankRow++;
			}
		}
		if ($rankRow gt $rankHist) {
			$rankHist = $rankRow;
		}
#		print "\n";
	}
	return $rankHist;
}

# -------------------------------------
sub formatScore {
	my $aref = shift @_;
	
	my $fmt = "";
	for (my $i=5; $i>=0; $i--) {
		$fmt .= sprintf("%06d_", $aref->[$i]);
	}
	return $fmt;
}

# now iterate through files and rank output
print "\nEvaluating guess files:\n";
foreach my $gf (@inFiles) {
	my @score = (0, 0, 0, 0, 0, 0 ); # 1-6 hits
	open(FIN, '<', $gf) or die "Unable to open $_";
	open(FOUT, '>', $pathTemp) or die "Unable to write $_";
	while (my $ln = <FIN>) {
		chomp($ln);
		if ($ln=~/^\d+:\s+(\d+),\s+(\d+),\s+(\d+),\s+(\d+),\s+(\d+),\s+(\d+)/) {
			my $m1 = $1;
			my $m2 = $2;
			my $m3 = $3;
			my $m4 = $4;
			my $m5 = $5;
			my $mb = $6;
			my @row = ($m1,$m2,$m3,$m4,$m5,$mb);
						
			# go through last N to the current row
			my $hits = CheckRowHit(\@row);
			#print "$hits, ";
			if ($hits gt 0) {
				$ln .= sprintf("\t: %sx%s", "-------" x $hits, $hits);
				$score[$hits-1]++;
			}
			printf FOUT "$ln\n";
		}
	}
	close(FIN);
	close(FOUT);
	my $prefix = formatScore(\@score);
	
	if ($score[3] gt 0 || $score[4] gt 0 || $score[5] gt 0) {
		my $newName = sprintf("%s%s%s", $pathDest,$prefix,basename $gf );
		printf "%s\n", $newName;
		move $pathTemp, $newName; 
	} else {
		unlink $gf;
	}
}

print "Done.";
