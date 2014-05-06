#include <stddef.h>

#include <algorithm>
#include <string>
#include <vector>

using namespace std;

#include <jni.h>

#include <GL3/gl3w.h>
#include "WinLinMacApi.h"

#include "cubex/cubex.h"
#include "Terminal.h"

using namespace cubex;

static Terminal* terminal;


extern "C"
{
	JNIEXPORT jboolean JNICALL Java_oldland_OldLandTerminalWindow_globalInit(JNIEnv * env, jclass appClass)
	{
		return globalInit();
	}

	JNIEXPORT void JNICALL Java_oldland_OldLandTerminalWindow_setScreenBuffer(JNIEnv * env, jclass appClass, jcharArray text)
	{
		int len = env->GetArrayLength(text);
		jchar* jchars = env->GetCharArrayElements(text, 0);
		wchar_t* wchars = new wchar_t[len + 1];
		for (int i = 0; i < len; i++)
		{
			wchars[i] = jchars[i];
		}
		wchars[len] = 0;

		terminal->setSymbols(wchars);

		delete [] wchars;
		env->ReleaseCharArrayElements(text, jchars, 0);
	}

	JNIEXPORT void JNICALL Java_oldland_OldLandTerminalWindow_setForegroundColors(JNIEnv * env, jclass appClass, jintArray forecolors)
	{
		int len = env->GetArrayLength(forecolors);
		jint* jcolors = env->GetIntArrayElements(forecolors, NULL);
		vector<uint32_t> vec;
		vec.assign(jcolors, jcolors + len);

		terminal->setForegroundColors(vec);

		env->ReleaseIntArrayElements(forecolors, jcolors, 0);
	}

	JNIEXPORT void JNICALL Java_oldland_OldLandTerminalWindow_setBackgroundColors(JNIEnv * env, jclass appClass, jintArray backcolors)
	{
		int len = env->GetArrayLength(backcolors);
		jint* jcolors = env->GetIntArrayElements(backcolors, NULL);
		vector<uint32_t> vec;
		vec.assign(jcolors, jcolors + len);

		terminal->setBackgroundColors(vec);

		env->ReleaseIntArrayElements(backcolors, jcolors, 0);
	}


	JNIEXPORT jboolean JNICALL Java_oldland_OldLandTerminalWindow_createTerminal(JNIEnv * env, jclass appClass, jint width, jint height, jint frame, jint xLength, jint yLength, jstring codepageSymbols, jint codepageWidth, jint codepageHeight)
	{
		string objFileLocation;
		string textureFileLocation;
		string vertFileLocation = WinLinMacApi::locateResource("data", "default.vert");
		string fragFileLocation = WinLinMacApi::locateResource("data", "default.frag");
		string screenVertFileLocation = WinLinMacApi::locateResource("data", "screen.vert");
		string screenFragFileLocation = WinLinMacApi::locateResource("data", "screen.frag");
		string textureFilename = WinLinMacApi::locateResource("data", "font.png");

		const jchar* codepageChars = env->GetStringChars(codepageSymbols, NULL);
		wchar_t* cpw = new wchar_t[env->GetStringLength(codepageSymbols) + 1];
		for (int i = 0; i < env->GetStringLength(codepageSymbols); i++)
		{
			cpw[i] = codepageChars[i];
		}
		cpw[env->GetStringLength(codepageSymbols)] = 0;

		bool res;
		try
		{
			terminal = new Terminal(vertFileLocation, fragFileLocation,
					screenVertFileLocation, screenFragFileLocation,
					width, height, frame, xLength, yLength,
					wstring(cpw), codepageWidth, codepageHeight,
					textureFilename);
			res = true;
		}
		catch (CubexException& ex)
		{
			printf("[ERROR] %s\n", ex.getReport().c_str());
			fflush(stdout);
			res = false;
		}

		delete [] cpw;
		env->ReleaseStringChars(codepageSymbols, codepageChars);
		return res;
	}

	JNIEXPORT jboolean JNICALL Java_oldland_OldLandTerminalWindow_destroyTerminal(JNIEnv * env, jclass appClass)
	{
		if (terminal != NULL)
		{
			delete terminal;
			terminal = NULL;
			return true;
		}
		else
		{
			return false;
		}
	}

	JNIEXPORT jboolean JNICALL Java_oldland_OldLandTerminalWindow_resizeView(JNIEnv * env, jclass appClass, int width, int height)
	{
		if (terminal != NULL)
		{
			terminal->resizeViewport(width, height);
			return true;
		}
		else
		{
			return false;
		}
	}

	JNIEXPORT jboolean JNICALL Java_oldland_OldLandTerminalWindow_drawScene(JNIEnv * env, jclass appClass, jdouble time)
	{
		try
		{
			if (terminal != NULL)
			{
				terminal->draw(time);
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (CubexException& ex)
		{
			printf("[ERROR] %s\n", ex.getReport().c_str());
			fflush(stdout);
			return false;
		}
	}

}
