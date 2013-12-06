/*
 * SymbolPosition.h
 *
 *  Created on: 30.11.2013
 *      Author: il
 */

#ifndef SYMBOLPLACE_H_
#define SYMBOLPLACE_H_

#include <GL3/gl3w.h>
#include <glm/glm.hpp>

#include "cubex/Sprite.h"
#include "cubex/MeshBuffer.h"

using namespace cubex;

class SymbolPlace : GLObject
{
private:
	MeshBuffer* meshBuffer;

	int xLength, yLength;
	int xSymbolsCountInTexture, ySymbolsCountInTexture;

	GLint matrixUniform, normalMatrixUniform;

public:
		SymbolPlace(int screenWidth, int screenHeight, int xSymbolsCountInTexture, int ySymbolsCountInTexture, GLint matrixUniform, GLint normalMatrixUniform);
		void setSymbolPositionInTexture(int xPos, int yPos);
		void drawAt(int x, int y) const;
		virtual ~SymbolPlace();
};

#endif /* SYMBOLPLACE_H_ */
