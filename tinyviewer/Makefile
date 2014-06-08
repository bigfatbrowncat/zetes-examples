include ../zetes/common-scripts/globals.mk

ZETES_FEET_PATH = ../zetes/zetesfeet/target-$(PLATFORM_TAG)-$(CLASSPATH)
ZETES_WINGS_PATH = ../zetes/zeteswings/target-$(PLATFORM_TAG)-$(CLASSPATH)

APPLICATION_NAME = Tiny\ Viewer
BINARY_NAME = tinyview
ENTRY_CLASS = tinyviewer/TinyViewerApplication

TARGET = target-$(PLATFORM_TAG)-$(CLASSPATH)

all: package

include $(ZETES_WINGS_PATH)/ZetesWings.mk

#.SILENT: