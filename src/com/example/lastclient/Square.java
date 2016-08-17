package com.example.lastclient;

public class Square
{

	int x, y; // X 좌표, Y 좌표
	byte color; // 색깔

	Square(int x, int y, byte color)
	{

		this.x = x;
		this.y = y;
		this.color = color;
	}

	int getX()
	{
		return x;
	}

	int getY()
	{
		return y;
	}

	byte getColor()
	{
		return color;
	}

	// 현재의 사각형이 판 안에 존재하는 것인지 확인
	boolean isInBounds()
	{
		return (x >= 0 && x < Tetris4Android.COLS && y >= 0 && y < Tetris4Android.ROWS);
	}

	boolean isEqual(Square s)
	{

		return x == s.x && y == s.y && color == s.color;
	}
}
