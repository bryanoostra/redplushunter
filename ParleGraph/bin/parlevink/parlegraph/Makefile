# Copyright 2001 Sun Microsystems, Inc.
# All Rights Reserved.  Use is subject to license terms.
# 
# See the file "license.terms" for information on usage and
# redistribution of this file, and for a DISCLAIMER OF ALL 
# WARRANTIES.
#

# Relative path to the top of the source tree
TOP = ../../..
PACKAGE_PATH = parlevink/parlegraph
DOC_DEST = /lab/speech/web/freetts
EXTRA_JARS = .$(CSEP)$(EXTRA_LIB)/kunststoff.jar$(CSEP)$(EXTRA_LIB)/ij.jar
RUN = Test

# By default, build all of the .java files. 
FILES = $(shell echo *.java)

# List any sub directories that need to be built
SUBDIRS = control layout model utils view

##########################################################################

include ${TOP}/build/Makefile.config

CLASSES = ${FILES:%.java=${CLASS_DEST_DIR}/${PACKAGE_PATH}/%.class}

all:: ${CLASSES} ${STUBS}


