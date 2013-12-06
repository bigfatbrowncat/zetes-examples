/*
 * Sprite.h
 *
 *  Created on: 18 ????. 2013 ?.
 *      Author: il
 */

#ifndef SPRITE_H_
#define SPRITE_H_

#include "Rectangle.h"

namespace cubex
{

	class Sprite : public Rectangle
	{
	public:
		Sprite(glm::vec2 lt, glm::vec2 rb, float z, int frameX, int frameY, int framesXcount, int framesYcount);
		virtual ~Sprite();
	};

}
#endif
