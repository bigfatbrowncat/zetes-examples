ZETES_FEET_PATH = ../zetes/zetesfeet/target-$(CLASSPATH)
ZETES_WINGS_PATH = ../zetes/zeteswings/target-$(CLASSPATH)

APPLICATION_NAME = OldLand
BINARY_NAME = ol
ENTRY_CLASS = oldland/OldLandApplication

TARGET = target-$(CLASSPATH)

UNAME := $(shell uname)
ifndef ARCH
  ARCH := $(shell uname -m)
endif

ifeq ($(UNAME), Darwin)	# OS X
  CUSTOM_INCLUDES = -I/System/Library/Frameworks/OpenGL.framework/Headers
  CUSTOM_LIBS = -framework OpenGL lib/$(PLATFORM_TAG)/libcubex-allinc.a
else ifeq ($(UNAME) $(ARCH), Linux x86_64)	# linux on PC
  CUSTOM_LIBS = -lstdc++ -lGL -lGLU lib/$(PLATFORM_TAG)/libcubex-allinc.a
else ifeq ($(OS), Windows_NT)	# Windows
  CUSTOM_LIBS = -lstdc++ -lopengl32 -lglu32 lib/$(PLATFORM_TAG)/libcubex-allinc.a
endif

all: app

include $(ZETES_WINGS_PATH)/ZetesWings.mk

#.SILENT:
