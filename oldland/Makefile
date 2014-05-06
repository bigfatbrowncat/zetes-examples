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
  CUSTOM_LIBS = -framework OpenGL ../external/cubex/target/lib/$(PLATFORM_TAG)/libcubex.a ../external/libpng-1.6.10/.libs/libpng16.a
else ifeq ($(UNAME) $(ARCH), Linux x86_64)	# linux on PC
  CUSTOM_LIBS = -lstdc++ -lGL -lGLU ../external/cubex/target/lib/$(PLATFORM_TAG)/libcubex.a ../external/libpng-1.6.10/.libs/libpng16.a
else ifeq ($(OS), Windows_NT)	# Windows
  CUSTOM_LIBS = -lstdc++ -lopengl32 -lglu32 ../external/cubex/target/lib/$(PLATFORM_TAG)/libcubex.a ../external/libpng-1.6.10/.libs/libpng16.a
endif

all:
	make deps
	make app

deps: ../external/cubex/target/lib/$(PLATFORM_TAG)/libcubex.a ../external/libpng-1.6.10/.libs/libpng16.a 

../external/cubex/target/lib/$(PLATFORM_TAG)/libcubex.a:
	(cd ../external/cubex; make all)
	
../external/libpng-1.6.10/.libs/libpng16.a:
	(cd ../external/libpng-1.6.10; ./configure && make all)

include $(ZETES_WINGS_PATH)/ZetesWings.mk

#.SILENT:
