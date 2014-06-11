ZETES_PATH = ../zetes

APPLICATION_NAME = OldLand
BINARY_NAME = ol
ENTRY_CLASS = oldland/OldLandApplication

include $(ZETES_PATH)/common-scripts/globals.mk
include $(ZETES_WINGS_PATH)/ZetesWings.mk

ifeq ($(UNAME), Darwin)	# OS X
  CUSTOM_INCLUDES = -I/System/Library/Frameworks/OpenGL.framework/Headers
  CUSTOM_LIBS = -framework OpenGL ../external/cubex/$(TARGET)/lib/$(PLATFORM_TAG)/libcubex.a ../external/libpng-1.6.10/.libs/libpng16.a
else ifeq ($(UNAME) $(ARCH), Linux x86_64)	# linux on PC
  CUSTOM_LIBS = -lstdc++ -lGL -lGLU ../external/cubex/$(TARGET)/lib/$(PLATFORM_TAG)/libcubex.a ../external/libpng-1.6.10/.libs/libpng16.a
else ifeq ($(OS), Windows_NT)	# Windows
  CUSTOM_LIBS = -lstdc++ -lopengl32 -lglu32 ../external/cubex/$(TARGET)/lib/$(PLATFORM_TAG)/libcubex.a ../external/libpng-1.6.10/.libs/libpng16.a
endif

all: app

native-deps:: cubex libpng16

cubex:
	(cd ../external/cubex; make all)
	
libpng16:
	(cd ../external/libpng-1.6.10; CFLAGS="-fPIC" CXXFLAGS="-fPIC" ./configure && make all)

.PHONY: cubex libpng16