/*
 * Copyright (C) 2019 Automation technology laboratory,
 * Helsinki University of Technology
 *
 * Visit automation.tkk.fi for information about the automation
 * technology laboratory.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA.
 */
package poolimporter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author moehman
 */
class IOPReader implements ByteReader {
    BufferedInputStream in;
    public IOPReader(BufferedInputStream in) {
	this.in = in;
    }
    @Override
    public int readId() {
	return readWord();
    }

    @Override
    public int readWord() {
	return
	    (readByte()) |
	    (readByte() << 8);
    }
 
    @Override
    public int readDWord() {
	return
	    (readByte()) |
	    (readByte() << 8) |
	    (readByte() << 16) |
	    (readByte() << 24);
    }

    @Override
    public float readFloat() {
	return Float.intBitsToFloat(readDWord());
    }

    @Override
    public int readColor() {
	return readByte();
    }

    @Override
    public int readType() {
	return readByte();
    }

    @Override
    public int readByte() {
	int val = -1;
	try {
	    val = in.read();
	}
	catch (IOException e) {}

	if (val < 0)
	    throw new RuntimeException("unexpected end of stream");
	return val;
    }

    @Override
    public byte[] readByteArray(int len) {
	byte[] b = new byte[len];
	for (int i = 0; i < len; i++) {
	    b[i] = (byte) readByte();
	}
	return b;
    }

    @Override
    public int readKeyCode() {
	return readByte();
    }

    @Override
    public List<Integer> readBytes(int nro) {
	List<Integer> list = new ArrayList<>();
	for (int i = 0; i < nro; i++) {
	    list.add(readByte());
	}
	return list;
    }

    @Override
    public int readRef() {
	return readWord();
    } 

    @Override
    public List<Integer> readRefs(int nro) {
	List<Integer> list = new ArrayList<>();
	for (int i = 0; i < nro; i++) {
	    list.add(readRef());
	}
	return list;
    } 

    @Override
    public List<RefXY> readRefXYs(int nro) {
	List<RefXY> list = new ArrayList<>();
	for (int i = 0; i < nro; i++) {
	    list.add(new RefXY(readRef(),
			       readWord(),
			       readWord()));
	}
	return list;
    } 

    @Override
    public List<PointXY> readPoints(int nro) {
	List<PointXY> list = new ArrayList<>();
	for (int i = 0; i < nro; i++) {
	    list.add(new PointXY(readWord(), readWord()));
	}
	return list;
    }

    @Override
    public List<String> readLanguages(int nro) {
	List<String> list = new ArrayList<>();
	for (int i = 0; i < nro; i++) {
	    String val = readString(2);
	    list.add(val);
	}
	return list;
    }

    @Override
    public String readString(int len) {
	byte[] b = readByteArray(len);
	return new String(b, Charset.forName("ISO-8859-1"));
    }
}
