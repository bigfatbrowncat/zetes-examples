ZETES_FEET_PATH = ../zetes/zetesfeet/target
ZETES_HANDS_PATH = ../zetes/zeteshands/target
ZETES_WINGS_PATH = ../zetes/zeteswings/target

all: zetes-examples

zetes-examples: bellardpi gltest tinyviewer

bellardpi: zetes
	$(MAKE) ZETES_FEET_PATH=$(ZETES_FEET_PATH) ZETES_HANDS_PATH=$(ZETES_HANDS_PATH) all -C bellardpi
	
gltest: zetes
	$(MAKE) ZETES_FEET_PATH=$(ZETES_FEET_PATH) ZETES_WINGS_PATH=$(ZETES_WINGS_PATH) all -C gltest

tinyviewer: zetes
	$(MAKE) ZETES_FEET_PATH=$(ZETES_FEET_PATH) ZETES_WINGS_PATH=$(ZETES_WINGS_PATH) all -C tinyviewer

clean:
	$(MAKE) clean -C bellardpi
	$(MAKE) clean -C gltest
	$(MAKE) clean -C tinyviewer

.PHONY: all zetes zetes-examples bellardpi gltest tinyviewer
.SILENT: