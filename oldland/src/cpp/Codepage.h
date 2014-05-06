/*
 * Codepage.h
 *
 *  Created on: 30.11.2013
 *      Author: il
 */

#ifndef CODEPAGE_H_
#define CODEPAGE_H_

#include <string>
#include <map>

#include "SymbolPlace.h"

using namespace std;

class Codepage
{
private:
	int width;
	wstring data;
	map<wchar_t, SymbolPlace*> coords;
public:
	Codepage(int width, int height, const wstring& data, int xLength, int yLength, GLint matrixUniform, GLint normalMatrixUniform) : width(width), data(data)
	{
		const wchar_t* chars = data.c_str();
		for (int i = 0; i < data.size(); i++)
		{
			if (coords.find(chars[i]) == coords.end())
			{
				SymbolPlace* sp = new SymbolPlace(xLength, yLength, width, height, matrixUniform, normalMatrixUniform);
				sp->setSymbolPositionInTexture(i % width, i / width);
				coords.insert(std::pair<wchar_t, SymbolPlace*>(chars[i], sp));
			}
		}
	}

	const SymbolPlace* getSymbol(wchar_t ch) const
	{
		map<wchar_t, SymbolPlace*>::const_iterator iter = coords.find(ch);
		if (iter == coords.end()) {
			return NULL;
		} else {
			return iter->second;
		}
	}

	virtual ~Codepage()
	{
		for (map<wchar_t, SymbolPlace*>::iterator iter = coords.begin(); iter != coords.end(); iter++)
		{
			delete (*iter).second;
		}
	}
};

#endif /* CODEPAGE_H_ */
