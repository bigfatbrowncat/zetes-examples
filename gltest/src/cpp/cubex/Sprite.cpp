/*
 * Sprite.cpp
 *
 *  Created on: 18 ????. 2013 ?.
 *      Author: il
 */

#include "Sprite.h"

namespace cubex
{

	Sprite::Sprite(glm::vec2 lt, glm::vec2 rb, float z, int frameX, int frameY, int framesXcount, int framesYcount) :
	        Rectangle(lt, rb, z, glm::vec2((float)frameX / framesXcount, (float)(framesYcount - frameY - 1) / framesYcount),
	                             glm::vec2((float)(frameX + 1) / framesXcount, (float)(framesYcount - frameY) / framesYcount))
	{


	}

	Sprite::~Sprite() {
		// TODO Auto-generated destructor stub
	}

}
