#ifndef RECTANGLE_H_
#define RECTANGLE_H_

#include <glm/glm.hpp>
#include "Mesh.h"

namespace cubex
{

	class Rectangle : public Mesh
	{
	public:
		Rectangle(glm::vec2 lt, glm::vec2 rb, float z, glm::vec2 texlt, glm::vec2 texrb);
		virtual ~Rectangle();
	};

}
#endif
