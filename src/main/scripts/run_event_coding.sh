#!/usr/bin/env bash
#petrarch_file_path = $1
#input_xml_file = $2
#output_xml_file = $3
#/usr/bin/python2.7 /anaconda3/lib/python3.6/site-packages/petrarch2/petrarch2.py batch -i ~/Desktop/test.xml -o text.txt
/usr/bin/python2.7 $1 batch -i $2 > $3
