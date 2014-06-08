include ../zetes/common-scripts/globals.mk

ZETES_FEET_PATH = ../zetes/zetesfeet/target-$(PLATFORM_TAG)-$(CLASSPATH)
ZETES_HANDS_PATH = ../zetes/zeteshands/target-$(PLATFORM_TAG)-$(CLASSPATH)

APPLICATION_NAME = BellardPI
BINARY_NAME = bellardpi
ENTRY_CLASS = bellardpi/Main

TARGET = target-$(PLATFORM_TAG)-$(CLASSPATH)

all: app

include $(ZETES_HANDS_PATH)/ZetesHands.mk
include ../zetes/common-scripts/ideconf/ideconf.mk

#.SILENT: