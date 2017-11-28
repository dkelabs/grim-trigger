#!/bin/bash

# Build
make html \
&& make latexpdf \
&& cd build/latex

# Get pdf doc name
pdf_file="$(ls *.pdf)"
cd ../../

if [ -f "${pdf_file}" ];
then
    rm "${pdf_file}"
fi

# Copy file to main dir    
cp "build/latex/${pdf_file}" ./