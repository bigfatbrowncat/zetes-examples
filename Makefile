include zetes/common-scripts/globals.mk

PACKAGE_NAME = zetes-examples-$(PLATFORM_TAG)-$(CLASSPATH)-`git describe`

ZETES_FEET_PATH = ../zetes/zetesfeet/target-$(PLATFORM_TAG)-$(CLASSPATH)
ZETES_HANDS_PATH = ../zetes/zeteshands/target-$(PLATFORM_TAG)-$(CLASSPATH)
ZETES_WINGS_PATH = ../zetes/zeteswings/target-$(PLATFORM_TAG)-$(CLASSPATH)

all: app

app: zetes-examples-app

package: zetes-examples-package
	mkdir -p $(PACKAGE_NAME)
	
ifeq ($(UNAME), Darwin)	# OS X
	cp -rf bellardpi/target-$(PLATFORM_TAG)-$(CLASSPATH)/package/BellardPI $(PACKAGE_NAME)
	cp -rf gltest/target-$(PLATFORM_TAG)-$(CLASSPATH)/package/GLDemo/bundle/* $(PACKAGE_NAME)
	cp -rf oldland/target-$(PLATFORM_TAG)-$(CLASSPATH)/package/OldLand/bundle/* $(PACKAGE_NAME)
	cp -rf tinyviewer/target-$(PLATFORM_TAG)-$(CLASSPATH)/package/Tiny\ Viewer/bundle/* $(PACKAGE_NAME)
	cp -rf HTMLViewer/target-$(PLATFORM_TAG)-$(CLASSPATH)/package/HTMLViewer/bundle/* $(PACKAGE_NAME)
ifeq ($(CLASSPATH), android)
	cp -rf parrot-server/target-$(PLATFORM_TAG)-$(CLASSPATH)/package/parrot-server $(PACKAGE_NAME)
endif	
else
	cp -rf bellardpi/target-$(PLATFORM_TAG)-$(CLASSPATH)/package/BellardPI $(PACKAGE_NAME)
	cp -rf gltest/target-$(PLATFORM_TAG)-$(CLASSPATH)/package/GLDemo $(PACKAGE_NAME)
	cp -rf oldland/target-$(PLATFORM_TAG)-$(CLASSPATH)/package/OldLand $(PACKAGE_NAME)
	cp -rf tinyviewer/target-$(PLATFORM_TAG)-$(CLASSPATH)/package/Tiny\ Viewer $(PACKAGE_NAME)
	cp -rf HTMLViewer/target-$(PLATFORM_TAG)-$(CLASSPATH)/package/HTMLViewer $(PACKAGE_NAME)
ifeq ($(CLASSPATH), android)
	cp -rf parrot-server/target-$(PLATFORM_TAG)-$(CLASSPATH)/package/parrot-server $(PACKAGE_NAME)
endif	
endif

ifeq ($(UNAME), Darwin)	# OS X
	hdiutil create $(PACKAGE_NAME).dmg -srcfolder $(PACKAGE_NAME) -ov

else ifeq ($(OS), Windows_NT)	# Windows 
	( \
	    cd $(PACKAGE_NAME); \
		zip -r ../$(PACKAGE_NAME).zip *; \
	)
else
	( \
	    cd $(PACKAGE_NAME); \
		tar -cjf ../$(PACKAGE_NAME).tar.bz2 *; \
	)
endif	

ifeq ($(CLASSPATH), android)
zetes-examples-app: bellardpi-app gltest-app oldland-app tinyviewer-app html-viewer parrot-server-app
zetes-examples-package: bellardpi-package gltest-package oldland-package tinyviewer-package html-viewer parrot-server-package
else
zetes-examples-app: bellardpi-app gltest-app oldland-app tinyviewer-app
zetes-examples-package: bellardpi-package gltest-package oldland-package tinyviewer-package
endif

# Packaging targets

bellardpi-package: zetes
	$(MAKE) package -C bellardpi
	
gltest-package: zetes
	$(MAKE) package -C gltest

oldland-package: zetes
	$(MAKE) package -C oldland
	
tinyviewer-package: zetes
	$(MAKE) package -C tinyviewer

htmlviewer-package: zetes
	$(MAKE) package -C HTMLViewer

ifeq ($(CLASSPATH), android)

parrot-server-package: zetes
	$(MAKE) package -C parrot-server

endif

# App targets
	
bellardpi-app: zetes
	$(MAKE) app -C bellardpi
	
gltest-app: zetes
	$(MAKE) app -C gltest

oldland-app: zetes
	$(MAKE) app -C oldland
	
tinyviewer-app: zetes
	$(MAKE) app -C tinyviewer

htmlviewer-app: zetes
	$(MAKE) app -C HTMLViewer

ifeq ($(CLASSPATH), android)

parrot-server-app: zetes
	$(MAKE) app -C parrot-server

endif

clean:
	$(MAKE) clean -C bellardpi
	$(MAKE) clean -C gltest
	$(MAKE) clean -C oldland
	$(MAKE) clean -C tinyviewer
	$(MAKE) clean -C HTMLViewer
ifeq ($(CLASSPATH), android)
	$(MAKE) clean -C parrot-server
endif
	rm -rf $(PACKAGE_NAME)

.PHONY: all app package zetes-examples-app zetes-examples-package bellardpi-app gltest-app oldland-app tinyviewer-app htmlviewer-app parrot-server-app bellardpi-package gltest-package oldland-package tinyviewer-package htmlviewer-package parrot-server-package
.SILENT: