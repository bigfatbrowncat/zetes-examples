Zetes Examples
==============

These small programs are made using [zetes](https://github.com/bigfatbrowncat/zetes) library. They should build well and work on Windows, OS X and desktop Linux operating systems. Their UI looks nice and native under all the OSes they support.

![Zetes demo applications](https://rawgit.com/bigfatbrowncat/zetes-examples/master/doc/images/demos.png)

GLDemo
======

This program shows an ability to use OpenGL 3 from a Zetes-powered application. It's a single-window app showing 3D animation. It could be zoomed (even to fullscreen). The model could be changed from a simple (cube) to a complicated (monkey) using the menu.

TinyViewer
==========

This app shows a multi-document interface. It supports loading any number of images in different windows. When the last window is closed, the app exits. It allows opening files from the menu or the command line and could be easily used as an image viewer (but with extremely few features ;) ).

OldLand
=======

Shows a terminal window emulator teletyping a long text with colored letters. No special idea... I just love terminals.

How to build
============

To build it you must clone this repository somewhere

    git clone https://github.com/bigfatbrowncat/zetes-examples.git
	
then enter it

	cd zetes-examples
	
then clone every dependent repository (including zetes itself) with:

	git submodule update --init --recursive
	
That will take some time. There you'll see "zetes" folder that contains the Zetes framework code. At first you should build it following [the official building guide](https://github.com/bigfatbrowncat/zetes#building-zetes).

After Zetes is built successfully (assuming you are back to the "zetes-examples" folder), just type

    make all
	
Every app is being built in its folder.
