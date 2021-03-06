package com.asm.text;

import android.os.Parcel;
import android.os.Parcelable;

import com.lhw.util.TextUtils;


/**
 * Position data when returns to {@code getPositionData}.
 * It marks position, lines, cols, x position, and y position.
 */
public class PositionData implements Parcelable
{
	private int mPosition = 0;
	private int mLine = 0, mCol = 0;
	private float mPosX = 0f, mPosY = 0f;


	public PositionData() {}

	public PositionData(int pos, int line, int col, float x, float y) {
		mPosition = pos;
		mLine = line;
		mCol = col;
		mPosX = x;
		mPosY = y;
	}
	
	public void set(int pos, int line, int col, float x, float y) {
		mPosition = pos;
		mLine = line;
		mCol = col;
		mPosX = x;
		mPosY = y;
	}
	
	public void setPosition(int position) {
		this.mPosition = position;
	}

	public int getPosition() {
		return mPosition;
	}

	public void setLine(int line) {
		this.mLine = line;
	}

	public int getLine() {
		return mLine;
	}

	public void setCol(int col) {
		this.mCol = col;
	}

	public int getCol() {
		return mCol;
	}
	
	public void setPos(float x, float y) {
		mPosX = x;
		mPosY = y;
	}

	public void setX(float x) {
		this.mPosX = x;
	}

	public float getX() {
		return mPosX;
	}

	public void setY(float y) {
		this.mPosY = y;
	}

	public float getY() {
		return mPosY;
	}
	
	public PositionData add(PositionData data) {
		PositionData d = new PositionData();
		d.mPosition = mPosition + data.mPosition;
		d.mLine = mLine + data.mLine;
		d.mPosX = mPosX + data.mPosX;
		if(data.mLine > 0) {
			d.mCol = data.mCol;
			d.mPosX = data.mPosX;
		} else {
			d.mCol = mCol + data.mCol;
			d.mPosY = mPosY + data.mPosY;
		}
		
		return d;
	}
	
	/**
	 * Subtract data from this.
	 * new position = cur - arg
	 * new lines = cur - arg
	 * new position x = 
	 * new position y = cur - arg
	 */
	public PositionData sub(PositionData data, CharSequence text, TextDraw draw) {
		PositionData d = new PositionData();
		if(data.mPosition < mPosition) return data.sub(this, text, draw);
		d.mPosition = mPosition - data.mPosition;
		d.mLine = mLine - data.mLine;
		d.mPosY = mPosY - data.mPosX;
		if(data.mLine > mLine || data.mCol > mCol) {
			// isColDef = false; resolve column
			int pos = d.mPosition;
			d.mCol = 0;
			d.mPosX = 0f;
			
			while(pos >= 0 && text.charAt(pos) != '\n') {
				pos--;
				d.mCol++;
				d.mPosX += draw.getPaint().measureText(text, pos, pos + 1);
			}
		} else {
			d.mCol = mCol - data.mCol;
			d.mPosX = mPosX - data.mPosX;
		}
		return d;
	}
	
	public void setPosition(CharSequence text, int len, TextDraw draw) {
		set(0, 0, 0, 0f, 0f);
		resolveDelta(text, 0, len, draw);
	}
	
	public void resolveDelta(CharSequence text, int offset, int length, TextDraw draw) {
		mPosition += length;
		
		int newLineCount = 0;
		
		for(int i = offset; i < length; i++) {
			if(text.charAt(i) == '\n') {
				newLineCount++;
				mCol = 0;
				mPosX = 0f;
				mPosY += draw.getLineSpacing();
			} else {
				mCol++;
				mPosX += draw.getPaint().measureText(new char[] {text.charAt(i)}, 0, 1);
			}
		}
		mLine += newLineCount;
	}
	
	public void resolveRemovedDelta(CharSequence text, int offset, int length, TextDraw draw) {
		mPosition += length;
		
		int newLineCount = 0;
		
		
		for(int i = offset; i < length; i++) {
			if(text.charAt(i) == '\n') {
				newLineCount++;
				mPosY -= draw.getLineSpacing();
			}
		}
		
		mCol = 0;
		mPosX = 0f;
		
		for(int i = offset; i >= 0; i++) {
			if(text.charAt(i) == '\n')
				break;
			
			mCol++;
			mPosX += draw.getPaint().measureText(new char[] {text.charAt(i)}, 0, 1);
		}
	}
	
	public static PositionData obtain(int pos, CharSequence text) {
		return obtain(pos, text, 0, 0);
	}
	
	public static PositionData obtain(int pos, CharSequence text, int startLine, int startPos) {
		PositionData data = new PositionData();
		data.setPosition(pos);
		
		return data;
	}
	
	public static PositionData obtain(Parcel p, CharSequence text, TextDraw draw) {
		PositionData data = new PositionData();
		data.resolveDelta(text, 0, p.readInt(), draw);
		return data;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel p, int flags) {
		p.writeInt(mPosition);
	}
}
