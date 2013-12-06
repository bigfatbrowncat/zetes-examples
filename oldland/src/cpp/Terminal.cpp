/*
 * Terminal.cpp
 *
 *  Created on: Jun 19, 2013
 *      Author: imizus
 */

#include <GL3/gl3w.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>

#include "cubex/ObjMeshLoader.h"

#include "cubex/FrameBuffer.h"
#include "cubex/Rectangle.h"
#include "cubex/Sprite.h"

#include "WinLinMacApi.h"
#include "Terminal.h"


	Terminal::Terminal(const string& vertexShaderFileName, const string& fragmentShaderFileName,
	             const string& screenVertexShaderFileName, const string& screenFragmentShaderFileName, int viewWidth, int viewHeight, int frame,
	             int xLength, int yLength, const wstring& codepageSymbols, int codepageWidth, int codepageHeight, const string& textureFilename) :
			antialiasMulti(2), frameImage(NULL), depthImage(NULL), xLength(xLength), yLength(yLength), frame(frame)
	{
		symbols = L"";
		this->viewWidth = viewWidth;
		this->viewHeight = viewHeight;

		// Constructing the screen plane
		Mesh scrPlane;
		scrPlane.addVertex(glm::vec3(-1.0, 1.0, 0.0));
		scrPlane.addVertex(glm::vec3(1.0, 1.0, 0.0));
		scrPlane.addVertex(glm::vec3(1.0, -1.0, 0.0));
		scrPlane.addVertex(glm::vec3(-1.0, -1.0, 0.0));
		scrPlane.addTextureCoords(glm::vec2(0.0, antialiasMulti));
		scrPlane.addTextureCoords(glm::vec2(antialiasMulti, antialiasMulti));
		scrPlane.addTextureCoords(glm::vec2(antialiasMulti, 0.0));
		scrPlane.addTextureCoords(glm::vec2(0.0, 0.0));

		scrPlane.addFace(Face::fromVerticesAndTextureCoords(0, 1, 2, 0, 1, 2));
		scrPlane.addFace(Face::fromVerticesAndTextureCoords(0, 2, 3, 0, 2, 3));
		screenPlaneMeshBuffer = new MeshBuffer(scrPlane);

	    // Read the Vertex Shader code from the file
	    shaderProgram = ShaderProgram::fromFiles(vertexShaderFileName, fragmentShaderFileName);
	    //shaderProgram->use();


	    forecolorUniform = shaderProgram->getUniformLocation("uni_foregroundColor");
	    backcolorUniform = shaderProgram->getUniformLocation("uni_backgroundColor");

	    textureWidthUniform = shaderProgram->getUniformLocation("uni_textureWidth");
	    textureHeightUniform = shaderProgram->getUniformLocation("uni_textureHeight");

	    matrixUniform = shaderProgram->getUniformLocation("uni_matrix");
	    globalMatrixUniform = shaderProgram->getUniformLocation("uni_globalMatrix");

	    normalMatrixUniform = shaderProgram->getUniformLocation("uni_normalMatrix");

		//symbolPlace = new SymbolPlace(80, 30, 70, 8, matrixUniform, normalMatrixUniform);

		/*wstring cpws = L"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz                  "
		                "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя    "
		                "1234567890-=!@#$%^&*()_+[]{},.<>/?\\|`~\"':;                            ";
		*/
		//codepage = new Codepage(70, 8, cpws, 80, 30, matrixUniform, normalMatrixUniform);

		codepage = new Codepage(codepageWidth, codepageHeight, codepageSymbols, xLength, yLength, matrixUniform, normalMatrixUniform);

	    screenPlaneShaderProgram = ShaderProgram::fromFiles(screenVertexShaderFileName, screenFragmentShaderFileName);

	    texture = new Texture(textureFilename);
		texture->bindToImageUnit();
		renewFramebufferTextures();
	}

	void Terminal::renewFramebufferTextures()
	{
		if (frameImage != NULL)
		{
			delete frameImage;
			frameImage = NULL;
		}

		if (depthImage != NULL)
		{
			delete depthImage;
			depthImage = NULL;
		}

		//printf("w: %d, h: %d", (viewWidth), (viewHeight));
		frameImage = new Texture((viewWidth) * antialiasMulti, (viewHeight) * antialiasMulti, Texture::tRGBA, 1);
		depthImage = new Texture((viewWidth) * antialiasMulti, (viewHeight) * antialiasMulti, Texture::tDepth, 1);
		frameImage->bindToImageUnit();
		depthImage->bindToImageUnit();
	}

	void Terminal::resizeViewport(int width, int height)
	{
		this->viewWidth = width;
		this->viewHeight = height;

		renewFramebufferTextures();
	}

	void Terminal::draw(float angle)
	{
		glEnable(GL_BLEND);
		glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

		checkForError(__FILE__, __LINE__);

		//PROJECTION
		//float aspectRatio = (float)viewWidth / viewHeight;

		// Sending light position

		glViewport(0, 0, antialiasMulti * (viewWidth), antialiasMulti * (viewHeight));
		checkForError(__FILE__, __LINE__);

		FrameBuffer fbo;
		fbo.connectToTexture(*frameImage, *depthImage);
		fbo.bind();
		{
			// Drawing to the framebuffer
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			checkForError(__FILE__, __LINE__);

			shaderProgram->activate();

			texture->activate("uni_texture");
			glUniform1f(textureWidthUniform, texture->getWidth());
			glUniform1f(textureHeightUniform, texture->getHeight());

			glm::mat4 global = glm::scale(glm::mat4(1.0), glm::vec3(((float)viewWidth - 2 * frame) / viewWidth, ((float)viewHeight - 2 * frame) / viewHeight, 1.0));
			glUniformMatrix4fv(globalMatrixUniform, 1, GL_FALSE, glm::value_ptr(global));
			checkForError(__FILE__, __LINE__);

			for (int i = 0; i < symbols.size(); i++)
			{
				uint32_t fc, bc;
				if (i < forecolors.size())
				{
					fc = forecolors[i];
				}
				else
				{
					fc = 0xFFAAAAAA;
				}

				if (i < backcolors.size())
				{
					bc = backcolors[i];
				}
				else
				{
					bc = 0;
				}

				// Sending fore color to shader
				uint32_t a = fc >> 24;
				uint32_t r = (fc >> 16) & 0xFF;
				uint32_t g = (fc >> 8) & 0xFF;
				uint32_t b = fc & 0xFF;

				glUniform4f(forecolorUniform, (float)r / 0xFF, (float)g / 0xFF, (float)b / 0xFF, (float)a / 0xFF);
				checkForError(__FILE__, __LINE__);

				// Sending back color to shader
				a = bc >> 24;
				r = (bc >> 16) & 0xFF;
				g = (bc >> 8) & 0xFF;
				b = bc & 0xFF;

				glUniform4f(backcolorUniform, (float)r / 0xFF, (float)g / 0xFF, (float)b / 0xFF, (float)a / 0xFF);
				checkForError(__FILE__, __LINE__);

				// Drawing the symbol
				wchar_t ch = symbols[i];
				codepage->getSymbol(ch)->drawAt(i % xLength, i / xLength);
			}
		}
		fbo.unbind();

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		checkForError(__FILE__, __LINE__);

		screenPlaneShaderProgram->activate();
		frameImage->activate("uni_texture");
	    screenPlaneMeshBuffer->process("in_mesh_vertexPosition", "in_mesh_normal", "in_mesh_textureCoords");
	}

	Terminal::~Terminal()
	{
		delete shaderProgram;
		delete screenPlaneShaderProgram;
		//delete symbolPlace;
		delete codepage;
		delete screenPlaneMeshBuffer;

		delete texture;

		if (frameImage != NULL)
		{
			delete frameImage;
		}

		if (depthImage != NULL)
		{
			delete depthImage;
		}
	}
