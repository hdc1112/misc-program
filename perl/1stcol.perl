#!/usr/bin/perl

foreach $line (<STDIN>)  {   
  chomp($line);
  @arr = split(/[\t\s]/,$line);
  print $arr[0] . "\n";
}
