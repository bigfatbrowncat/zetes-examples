ZETES_FEET_PATH = ../zetes/zetesfeet/target
ZETES_WINGS_PATH = ../zetes/zeteswings/target

APPLICATION_NAME = OldLand
BINARY_NAME = ol
ENTRY_CLASS = oldland/OldLandApplication

TARGET = target

UNAME := $(shell uname)

ifeq ($(UNAME), Darwin)	# OS X
  CUSTOM_INCLUDES = -I/System/Library/Frameworks/OpenGL.framework/Headers
  CUSTOM_LIBS = -framework OpenGL lib/$(PLATFORM_TAG)/libcubex-allinc.a
else ifeq ($(UNAME) $(ARCH), Linux x86_64)	# linux on PC
  CUSTOM_LIBS = -lopengl32 -lglu32 lib/$(PLATFORM_TAG)/libcubex-allinc.a
else ifeq ($(OS), Windows_NT)	# Windows
  CUSTOM_LIBS = -lopengl32 -lglu32 lib/$(PLATFORM_TAG)/libcubex-allinc.a
endif

all: app

include $(ZETES_WINGS_PATH)/ZetesWings.mk

.SILENT: