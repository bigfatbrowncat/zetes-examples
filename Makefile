include zetes/common-scripts/globals.mk

ZETES_FEET_PATH = ../zetes/zetesfeet/target-$(PLATFORM_TAG)-$(CLASSPATH)
ZETES_HANDS_PATH = ../zetes/zeteshands/target-$(PLATFORM_TAG)-$(CLASSPATH)
ZETES_WINGS_PATH = ../zetes/zeteswings/target-$(PLATFORM_TAG)-$(CLASSPATH)

all: zetes-examples

zetes-examples: bellardpi gltest oldland tinyviewer

bellardpi: zetes
	$(MAKE) ZETES_FEET_PATH=$(ZETES_FEET_PATH) ZETES_HANDS_PATH=$(ZETES_HANDS_PATH) all -C bellardpi
	
gltest: zetes
	$(MAKE) ZETES_FEET_PATH=$(ZETES_FEET_PATH) ZETES_WINGS_PATH=$(ZETES_WINGS_PATH) all -C gltest

oldland: zetes
	$(MAKE) ZETES_FEET_PATH=$(ZETES_FEET_PATH) ZETES_WINGS_PATH=$(ZETES_WINGS_PATH) all -C oldland
	
tinyviewer: zetes
	$(MAKE) ZETES_FEET_PATH=$(ZETES_FEET_PATH) ZETES_WINGS_PATH=$(ZETES_WINGS_PATH) all -C tinyviewer

clean:
	$(MAKE) clean -C bellardpi
	$(MAKE) clean -C gltest
	$(MAKE) clean -C oldland
	$(MAKE) clean -C tinyviewer

.PHONY: all zetes zetes-examples bellardpi gltest oldland tinyviewer
.SILENT: