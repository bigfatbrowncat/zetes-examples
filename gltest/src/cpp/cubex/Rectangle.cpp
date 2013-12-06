#include "Rectangle.h"

namespace cubex
{

	Rectangle::Rectangle(glm::vec2 lt, glm::vec2 rb, float z, glm::vec2 texlt, glm::vec2 texrb)
	{
		addVertex(glm::vec3(lt.x, lt.y, z));
		addVertex(glm::vec3(rb.x, lt.y, z));
		addVertex(glm::vec3(rb.x, rb.y, z));
		addVertex(glm::vec3(lt.x, rb.y, z));

		addTextureCoords(glm::vec2(texlt.x, texlt.y));
		addTextureCoords(glm::vec2(texrb.x, texlt.y));
		addTextureCoords(glm::vec2(texrb.x, texrb.y));
		addTextureCoords(glm::vec2(texlt.x, texrb.y));

		addFace(Face::fromVerticesAndTextureCoords(0, 1, 2, 0, 1, 2));
		addFace(Face::fromVerticesAndTextureCoords(0, 2, 3, 0, 2, 3));
	}

	Rectangle::~Rectangle()
	{
	}

}
