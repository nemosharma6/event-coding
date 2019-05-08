#!/bin/bash
echo "Upgrading pip"
pip install --upgrade pip
echo "Install petrarch2"
pip install git+https://github.com/openeventdata/petrarch2.git
