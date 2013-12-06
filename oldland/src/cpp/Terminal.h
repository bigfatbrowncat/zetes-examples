/*
 * Terminal.h
 *
 *  Created on: Jun 19, 2013
 *      Author: imizus
 */

#ifndef TERMINAL_H_
#define TERMINAL_H_

#include "cubex/ShaderProgram.h"
#include "cubex/MeshBuffer.h"
#include "cubex/Texture.h"
#include "cubex/GLObject.h"

#include "SymbolPlace.h"
#include "Codepage.h"

using namespace cubex;

	class Terminal : public GLObject
	{
	private:
		// This class isn't copyable
		Terminal operator = (const Terminal& other);
		Terminal(const Texture& other);

	private:
		Codepage* codepage;
		ShaderProgram* shaderProgram;
		ShaderProgram* screenPlaneShaderProgram;
		MeshBuffer* screenPlaneMeshBuffer;
		Texture* texture;
		Texture* frameImage;
		Texture* depthImage;

		int antialiasMulti;

		//int lightPositionUniform;
		GLint forecolorUniform;
		GLint backcolorUniform;

		GLint globalMatrixUniform;
		GLint matrixUniform, normalMatrixUniform;
		GLint textureWidthUniform, textureHeightUniform;

		int viewWidth, viewHeight;
		int frame;

		int xLength, yLength;

		wstring symbols;
		vector<uint32_t> forecolors;
		vector<uint32_t> backcolors;

		void renewFramebufferTextures();

	public:
		Terminal(const string& vertexShaderFileName, const string& fragmentShaderFileName,
			             const string& screenVertexShaderFileName, const string& screenFragmentShaderFileName, int viewWidth, int viewHeight, int frame,
			             int xLength, int yLength, const wstring& codepageSymbols, int codepageWidth, int codepageHeight, const string& textureFilename);
		void resizeViewport(int width, int height);
		void draw(float time);

		void setSymbols(const wstring& symbols)
		{
			this->symbols = symbols;
		}
		void setForegroundColors(const vector<uint32_t>& forecolors)
		{
			this->forecolors = forecolors;
		}
		void setBackgroundColors(const vector<uint32_t>& backcolors)
		{
			this->backcolors = backcolors;
		}

		virtual ~Terminal();
	};

#endif
