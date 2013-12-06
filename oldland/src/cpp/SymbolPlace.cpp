#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>

#include "SymbolPlace.h"

SymbolPlace::SymbolPlace(int xLength, int yLength, int xSymbolsCountInTexture, int ySymbolsCountInTexture, GLint matrixUniform, GLint normalMatrixUniform) :
	xLength(xLength), yLength(yLength),
	xSymbolsCountInTexture(xSymbolsCountInTexture), ySymbolsCountInTexture(ySymbolsCountInTexture),
	matrixUniform(matrixUniform), normalMatrixUniform(normalMatrixUniform)
{
	meshBuffer = NULL;
}

void SymbolPlace::setSymbolPositionInTexture(int xPos, int yPos)
{
	if (meshBuffer != NULL)
	{
		delete meshBuffer;
	}
	meshBuffer = new MeshBuffer(Sprite(glm::vec2(0.0f, 0.0f), glm::vec2(1.0f, 1.0f), 0.0f, xPos, yPos, xSymbolsCountInTexture, ySymbolsCountInTexture));

}

void SymbolPlace::drawAt(int x, int y) const
{
	float dx = (-(float)xLength + 2 * x) / xLength;
	float dy = ((float)yLength - 2 * (1 + y)) / yLength;

	glm::mat4 MP = glm::scale(glm::translate(glm::mat4(1.0), glm::vec3(dx, dy, 0)), glm::vec3(2.0 / xLength, 2.0 / yLength, 1.0));
	glm::mat3 NM = glm::inverse(glm::transpose(glm::mat3(MP)));


	// Sending matrix
	glUniformMatrix4fv(matrixUniform, 1, GL_FALSE, glm::value_ptr(MP));
	checkForError(__FILE__, __LINE__);
	glUniformMatrix3fv(normalMatrixUniform, 1, GL_FALSE, glm::value_ptr(NM));
	checkForError(__FILE__, __LINE__);

	if (meshBuffer != NULL)
	{
		meshBuffer->process("in_mesh_vertexPosition", "in_mesh_normal", "in_mesh_textureCoords");
	}
	else
	{
		printf("Nothing to process cause symbol position isn't set");
	}
}

SymbolPlace::~SymbolPlace()
{
	if (meshBuffer != NULL)
	{
		delete meshBuffer;
	}
}
