#ifndef FONT_H
#define FONT_H

#include <stdlib.h>
#include <vector>
#include <string>

using namespace std;

class Font
{
private:
	vector<bool*> letters;
	int letterWidth, letterHeight, overSize;
protected:
	void initFromOther(const Font& other)
	{
		letterWidth = other.letterWidth;
		letterHeight = other.letterHeight;
		for (vector<bool*>::const_iterator iter = other.getLettersBegin(); iter != other.getLettersEnd(); iter++)
		{
			bool* newChar = *insert(letters.end());
			memcpy(newChar, *iter, letterWidth * letterHeight * sizeof(bool));
		}
	}
public:
	// Getters

	int getOverSize() const { return overSize; }
	int getLetterWidth() const { return letterWidth; }
	int getLetterHeight() const { return letterHeight; }
	int getLettersNumber() const { return letters.size(); }
	const vector<bool*>::const_iterator getLettersBegin() const { return letters.begin(); }
	const vector<bool*>::const_iterator getLettersEnd() const { return letters.end(); }
	const vector<bool*>::iterator getLettersBegin() { return letters.begin(); }
	const vector<bool*>::iterator getLettersEnd() { return letters.end(); }

	// Constructors / destructor

	Font(const Font& other)
	{
		initFromOther(other);
	}

	Font(int letterWidth, int letterHeight) : letterWidth(letterWidth),
	                                      letterHeight(letterHeight)
	{

	}

	Font(const string& fileName)
	{
		if (!loadFromFile(fileName))
		{
			throw exception();
		}
	}

	virtual ~Font()
	{
		clear();
	}

	// Operators

	Font& operator = (const Font& other)
	{
		clear();
		initFromOther(other);
		return *this;
	}

	// Methods

	bool loadFromFile(const string& fileName)
	{
		clear();
		FILE* f = fopen(fileName.c_str(), "r");
		if (f != NULL)
		{
			fscanf(f, "%d %d %d", &letterWidth, &letterHeight, &overSize);
			fgetc(f);	// skip '\n'
		}

		if (f != NULL)
		{
			while (!feof(f))
			{
				bool* newChar = *insert(letters.end());
				for (int j = 0; j < letterHeight * overSize; j++)
				{
					for (int i = 0; i < letterWidth * overSize; i++)
					{
						char c = fgetc(f);
						newChar[j * letterWidth * overSize + i] = (c == '#');
					}
					fgetc(f);	// skip '\n'
				}
				fgetc(f);	// skip '\n'
			}
			fclose(f);
			return true;
		}
		else
			return false;
	}

	bool saveToFile(const string& filename)
	{
		FILE* f = fopen(filename.c_str(), "w");
		fprintf(f, "%d %d %d\n", letterWidth, letterHeight, overSize);

		if (f != NULL)
		{
			for (vector<bool*>::iterator iter = letters.begin(); iter != letters.end(); iter++)
			{
				for (int j = 0; j < letterHeight * overSize; j++)
				{
					for (int i = 0; i < letterWidth * overSize; i++)
					{
						if ((*iter)[j * letterWidth * overSize + i])
							fprintf(f, "#");
						else
							fprintf(f, " ");
					}
					fprintf(f, "\n");
				}
				if (iter != letters.end() - 1)
					fprintf(f, "\n");
			}
			fclose(f);
			return true;
		}
		else
			return false;
	}

	vector<bool*>::iterator insert(vector<bool*>::iterator iter)
	{
		bool* newChar = new bool[letterWidth * letterHeight * overSize * overSize];
		for (int i = 0; i < letterWidth * letterHeight * overSize * overSize; i++) newChar[i] = false;
		return letters.insert(iter, newChar);
	}

	void remove(vector<bool*>::iterator iter)
	{
		delete [] (*iter);
		letters.erase(iter);
	}

	void clear()
	{
		while (letters.size() > 0) remove(letters.begin());
	}
};

struct Selection
{
	int xLeft, xRight, yTop, yBottom;
	Selection(int x1, int y1, int x2, int y2)
	{
		xLeft = x1 < x2 ? x1 : x2;
		xRight = x2 > x1 ? x2 : x1;
		yTop = y1 < y2 ? y1 : y2;
		yBottom = y2 > y1 ? y2 : y1;
	}
	bool operator == (const Selection& other)
	{
		return xLeft == other.xLeft && xRight == other.xRight && yTop == other.yTop && yBottom == other.yBottom;
	}
};

static const Selection SELECTION_NONE(-1, -1, -1, -1);

class EditableFont : public Font
{
private:
	bool* clipboard;
public:
	EditableFont(const string& fileName) : Font(fileName)
	{
		clipboard = new bool[getLetterWidth() * getLetterHeight() * getOverSize() * getOverSize()];
	}

	void copyToClipboard(vector<bool*>::iterator iter)
	{
		for (int i = 0; i < getLetterWidth() * getLetterHeight() * getOverSize() * getOverSize(); i++)
		{
			clipboard[i] = (*iter)[i];
		}
	}
	void pasteFromClipboard(vector<bool*>::iterator iter)
	{
		for (int i = 0; i < getLetterWidth() * getLetterHeight() * getOverSize() * getOverSize(); i++)
		{
			(*iter)[i] = clipboard[i];
		}
	}
	void mirrorHorizontal(vector<bool*>::iterator iter, Selection selection)
	{
		int letterWidth = getLetterWidth();
		int imin, imax, jmin, jmax;

		if (selection == SELECTION_NONE)
		{
			imin = 0;
			imax = letterWidth * getOverSize();
			jmin = 0;
			jmax = getLetterHeight() * getOverSize();
		}
		else
		{
			imin = selection.xLeft;
			imax = selection.xRight + 1;
			jmin = selection.yTop;
			jmax = selection.yBottom + 1;
		}
		int sel_w = imax - imin;

		if (sel_w >= 2)
		{
			int overSize = getOverSize();
			for (int j = jmin; j < jmax; j++)
			{
				for (int di = 0; di < sel_w / 2; di++)
				{
					int w = letterWidth * overSize;

					int rpos = j * w + imin + (sel_w - 1 - di);
					int lpos = j * w + imin + di;

					bool tmp = (*iter)[rpos];
					(*iter)[rpos] = (*iter)[lpos];
					(*iter)[lpos] = tmp;
				}
			}
		}
	}

	void mirrorVertical(vector<bool*>::iterator iter, Selection selection)
	{
		int letterWidth = getLetterWidth();
		int letterheight = getLetterHeight();
		int overSize = getOverSize();
		int w = letterWidth * overSize;

		int imin, imax, jmin, jmax;

		if (selection == SELECTION_NONE)
		{
			imin = 0;
			imax = letterWidth * overSize;
			jmin = 0;
			jmax = letterheight * overSize;
		}
		else
		{
			imin = selection.xLeft;
			imax = selection.xRight + 1;
			jmin = selection.yTop;
			jmax = selection.yBottom + 1;
		}
		int sel_h = jmax - jmin;

		if (sel_h >= 2)
		{
			for (int i = imin; i < imax; i++)
			{
				for (int dj = 0; dj < sel_h / 2; dj++)
				{
					int bpos = (jmin + sel_h - 1 - dj) * w + i;
					int tpos = (jmin + dj) * w + i;

					bool tmp = (*iter)[bpos];
					(*iter)[bpos] = (*iter)[tpos];
					(*iter)[tpos] = tmp;
				}
			}
		}

	}

	void kernLeft(vector<bool*>::iterator iter, Selection selection)
	{
		int letterWidth = getLetterWidth();
		int letterHeight = getLetterHeight();
		int overSize = getOverSize();
		int w = letterWidth * overSize;
		int h = letterHeight * overSize;

		int imin, imax, jmin, jmax;

		if (selection == SELECTION_NONE)
		{
			imin = 0;
			imax = letterWidth * overSize;
			jmin = 0;
			jmax = letterHeight * overSize;
		}
		else
		{
			imin = selection.xLeft;
			imax = selection.xRight + 1;
			jmin = selection.yTop;
			jmax = selection.yBottom + 1;
		}
		int sel_w = imax - imin;

		for (int j = jmin; j < jmax; j++)
		{
			bool tmp = (*iter)[j * w + imin];
			for (int di = 1; di < sel_w; di++)
			{
				(*iter)[j * w + imin + (di + sel_w - 1) % sel_w] = (*iter)[j * w + imin + di];
			}
			(*iter)[j * w + (imax - 1)] = tmp;
		}
	}

	void kernRight(vector<bool*>::iterator iter, Selection selection)
	{
		int letterWidth = getLetterWidth();
		int letterHeight = getLetterHeight();
		int overSize = getOverSize();
		int w = letterWidth * overSize;
		int h = letterHeight * overSize;

		int imin, imax, jmin, jmax;

		if (selection == SELECTION_NONE)
		{
			imin = 0;
			imax = letterWidth * overSize;
			jmin = 0;
			jmax = letterHeight * overSize;
		}
		else
		{
			imin = selection.xLeft;
			imax = selection.xRight + 1;
			jmin = selection.yTop;
			jmax = selection.yBottom + 1;
		}
		int sel_w = imax - imin;

		for (int j = jmin; j < jmax; j++)
		{
			bool tmp = (*iter)[j * w + (imax - 1)];
			for (int di = sel_w - 2; di >= 0; di--)
			{
				(*iter)[j * w + imin + (di + 1) % sel_w] = (*iter)[j * w + imin + di];
			}
			(*iter)[j * w + imin] = tmp;
		}
	}

	void kernDown(vector<bool*>::iterator iter, Selection selection)
	{
		int letterWidth = getLetterWidth();
		int letterHeight = getLetterHeight();
		int overSize = getOverSize();
		int w = letterWidth * overSize;
		int h = letterHeight * overSize;

		int imin, imax, jmin, jmax;

		if (selection == SELECTION_NONE)
		{
			imin = 0;
			imax = letterWidth * overSize;
			jmin = 0;
			jmax = letterHeight * overSize;
		}
		else
		{
			imin = selection.xLeft;
			imax = selection.xRight + 1;
			jmin = selection.yTop;
			jmax = selection.yBottom + 1;
		}
		int sel_h = jmax - jmin;

		for (int i = imin; i < imax; i++)
		{
			bool tmp = (*iter)[(jmax - 1) * w + i];
			for (int dj = sel_h - 2; dj >= 0; dj--)
			{
				(*iter)[(jmin + (dj + 1) % sel_h) * w + i] = (*iter)[(jmin + dj) * w + i];
			}
			(*iter)[jmin * w + i] = tmp;
		}
	}

	void kernUp(vector<bool*>::iterator iter, Selection selection)
	{
		int letterWidth = getLetterWidth();
		int letterHeight = getLetterHeight();
		int overSize = getOverSize();
		int w = letterWidth * overSize;
		int h = letterHeight * overSize;

		int imin, imax, jmin, jmax;

		if (selection == SELECTION_NONE)
		{
			imin = 0;
			imax = letterWidth * overSize;
			jmin = 0;
			jmax = letterHeight * overSize;
		}
		else
		{
			imin = selection.xLeft;
			imax = selection.xRight + 1;
			jmin = selection.yTop;
			jmax = selection.yBottom + 1;
		}
		int sel_h = jmax - jmin;

		for (int i = imin; i < imax; i++)
		{
			bool tmp = (*iter)[jmin * w + i];
			for (int dj = 1; dj < sel_h; dj++)
			{
				(*iter)[(jmin + (dj - 1 + sel_h) % sel_h) * w + i] = (*iter)[(jmin + dj) * w + i];
			}
			(*iter)[(jmax - 1) * w + i] = tmp;
		}
	}


	~EditableFont()
	{
		delete [] clipboard;
	}
};

class CachedFont : public Font
{
private:
	vector<unsigned char*> cache;

public:
	const vector<unsigned char*>::const_iterator getCachedLettersBegin() const { return cache.begin(); }
	const vector<unsigned char*>::const_iterator getCachedLettersEnd() const { return cache.end(); }
	const vector<unsigned char*>::iterator getCachedLettersBegin() { return cache.begin(); }
	const vector<unsigned char*>::iterator getCachedLettersEnd() { return cache.end(); }


	CachedFont(const string& fileName) : Font(fileName)
	{
		for (vector<bool*>::const_iterator iter = this->getLettersBegin(); iter != getLettersEnd(); iter++)
		{
			unsigned char* charCache = new unsigned char[getLetterWidth() * getLetterHeight()];

			for (int j = 0; j < getLetterHeight(); j++)
			for (int i = 0; i < getLetterWidth(); i++)
			{
				float v = 0;
				for (int p = 0; p < getOverSize(); p++)
				for (int q = 0; q < getOverSize(); q++)
				{
					int ii = i * getOverSize() + p;
					int jj = j * getOverSize() + q;

					if ((*iter)[jj * getLetterWidth() * getOverSize() + ii])
					{
						v += 1;
					}
				}
				charCache[j * getLetterWidth() + i] = (unsigned char)((v * 255) / getOverSize() / getOverSize());
			}

			cache.push_back(charCache);
		}
	}

	~CachedFont()
	{
		for (vector<unsigned char*>::iterator iter = cache.begin(); iter != cache.end(); iter++)
		{
			delete[] (*iter);
		}
		cache.clear();
	}
};

#endif
