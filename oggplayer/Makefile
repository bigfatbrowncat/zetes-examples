ZETES_PATH = ../zetes

APPLICATION_NAME = OggPlayer
BINARY_NAME = oggplayer
ENTRY_CLASS = app/Application

include $(ZETES_PATH)/common-scripts/globals.mk
include $(ZETES_HANDS_PATH)/ZetesHands.mk

ifeq ($(UNAME), Darwin)	# OS X
  CUSTOM_INCLUDES = -I../external/libogg-1.3.2/include/ -I../external/libvorbis-1.3.4/include/ -I../external/portaudio-v19_20140130/include/
  CUSTOM_LIBS = -framework CoreAudio -framework AudioToolbox -framework AudioUnit -L../external/libogg-1.3.2/src/.libs/ -L../external/libvorbis-1.3.4/lib/.libs/ -L../external/portaudio-v19_20140130/lib/.libs/ -lvorbisfile -lvorbis -logg -lportaudio
else ifeq ($(UNAME) $(ARCH), Linux x86_64)	# linux on PC
  CUSTOM_INCLUDES = -I../external/libogg-1.3.2/include/ -I../external/libvorbis-1.3.4/include/ -I../external/portaudio-v19_20140130/include/
  CUSTOM_LIBS = -L../external/libogg-1.3.2/src/.libs/ -L../external/libvorbis-1.3.4/lib/.libs/ -L../external/portaudio-v19_20140130/lib/.libs/ -lvorbisfile -lvorbis -logg -lportaudio
else ifeq ($(OS), Windows_NT)	# Windows
  CUSTOM_INCLUDES = -I../external/libogg-1.3.2/include/ -I../external/libvorbis-1.3.4/include/ -I../external/portaudio-v19_20140130/include/
  CUSTOM_LIBS = -L../external/libogg-1.3.2/src/.libs/ -L../external/libvorbis-1.3.4/lib/.libs/ -L../external/portaudio-v19_20140130/lib/.libs/ -lvorbisfile -lvorbis -logg -lportaudio -lwinmm -lole32
endif

all: app

native-deps:: libogg libvorbis portaudio

# libogg
../external/libogg-1.3.2/Makefile:
	(cd ../external/libogg-1.3.2; CFLAGS="-fPIC" CXXFLAGS="-fPIC" ./configure --enable-static=yes --enable-shared=no)

libogg: ../external/libogg-1.3.2/Makefile
	(cd ../external/libogg-1.3.2; make)

# libvorbis
../external/libvorbis-1.3.4/Makefile:
	(cd ../external/libvorbis-1.3.4; CFLAGS="-fPIC" CXXFLAGS="-fPIC" ./configure --enable-static=yes --enable-shared=no --with-ogg-libraries=`pwd`/../libogg-1.3.2/src/.libs/ --with-ogg-includes=`pwd`/../libogg-1.3.2/include/)

libvorbis: libogg ../external/libvorbis-1.3.4/Makefile
	(cd ../external/libvorbis-1.3.4; make)

# portaudio
../external/portaudio-v19_20140130/Makefile:
ifeq ($(OS), Windows_NT)	# Windows
	(cd ../external/portaudio-v19_20140130; CFLAGS="-fPIC" CXXFLAGS="-fPIC" ./configure --with-winapi=wmme,directx --enable-static=yes --enable-shared=no)
else
	(cd ../external/portaudio-v19_20140130; CFLAGS="-fPIC" CXXFLAGS="-fPIC" ./configure -enable-static=yes --enable-shared=no)
endif

portaudio: ../external/portaudio-v19_20140130/Makefile
	(cd ../external/portaudio-v19_20140130; make)

.PHONY: libogg libvorbis portaudio