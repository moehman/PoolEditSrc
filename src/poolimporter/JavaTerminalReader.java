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

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author moehman
 */
class JavaTerminalReader implements ByteReader {
    Scanner sc;
    public JavaTerminalReader(Scanner sc) {
	this.sc = sc;
    }
    
    @Override
    public int readId() {
	final String id = sc.next();
	if (!id.equals("ID"))
	    throw new RuntimeException("id expected: " + id);

	final int val = Integer.parseInt(sc.next());
	if (val > 0xFFFF) 
	    throw new RuntimeException("id too large: " + val);
	
	return val;
    }

    @Override
    public int readWord() {
	final String id = sc.next();
	if (!id.equals("WORD"))
	    throw new RuntimeException("word expected: " + id);

	final int val = Integer.parseInt(sc.next());
	if (val > 0xFFFF) 
	    throw new RuntimeException("word too large: " + val);
	
	return val;
    }
 
    @Override
    public int readDWord() {
	final String id = sc.next();
	if (!id.equals("DWORD"))
	    throw new RuntimeException("double word expected: " + id);

	final int val = Integer.parseInt(sc.next());
	return val;
    }

    @Override
    public float readFloat() {
	final String id = sc.next();
	if (!id.equals("FLOAT"))
	    throw new RuntimeException("float expected: " + id);

	final float val = Float.parseFloat(sc.next());
	return val;	
    }

    @Override
    public int readColor() {
	final String id = sc.next();
	if (!id.equals("COLOR"))
	    throw new RuntimeException("byte expected: " + id);

	final int val = Integer.parseInt(sc.next());
	if (val > 0xFF) 
	    throw new RuntimeException("byte too large: " + val);
	
	return val;
    }

    @Override
    public int readType() {
	final String id = sc.next();
	if (!id.equals("TYPE"))
	    throw new RuntimeException("type expected: " + id);

	final int val = Integer.parseInt(sc.next());
	if (val > 0xFF) 
	    throw new RuntimeException("type too large: " + val);
	
	return val;
    }

    @Override
    public int readByte() {
	final String id = sc.next();
	if (!id.equals("BYTE"))
	    throw new RuntimeException("byte expected: " + id);

	final int val = Integer.parseInt(sc.next());
	if (val > 0xFF) 
	    throw new RuntimeException("byte too large: " + val);
	
	return val;
    }

    @Override
    public byte[] readByteArray(int len) {
	final String id = sc.next();
	if (!id.equals("BYTEARRAY"))
	    throw new RuntimeException("byte array expected: " + id);

	byte[] b = new byte[len];
	for (int i = 0; i < len; i++) {
	    int val = Integer.parseInt(sc.next());
	    if (val > 0xFF) 
		throw new RuntimeException("byte too large: " + val);

	    b[i] = (byte) val;
	}
	return b;
    }

    @Override
    public int readKeyCode() {
	final String id = sc.next();
	if (!id.equals("KEYCODE"))
	    throw new RuntimeException("key code expected: " + id);

	final int val = Integer.parseInt(sc.next());
	if (val > 0xFF) 
	    throw new RuntimeException("key code too large: " + val);
	
	return val;
    }

    @Override
    public List<Integer> readBytes(int nro) {
	List<Integer> list = new ArrayList<>();
	for (int i = 0; i < nro; i++) {
	    list.add(this.readByte());
	}
	return list;
    }

    @Override
    public int readRef() {
	final String id = sc.next();
	if (!id.equals("REF"))
	    throw new RuntimeException("ref expected: " + id);

	final int val = Integer.parseInt(sc.next());
	if (val > 0xFFFF) 
	    throw new RuntimeException("ref too large: " + val);
	
	return val;
    } 

    @Override
    public List<Integer> readRefs(int nro) {
	List<Integer> list = new ArrayList<>();
	for (int i = 0; i < nro; i++) {
	    list.add(this.readRef());
	}
	return list;
    } 

    @Override
    public List<RefXY> readRefXYs(int nro) {
	List<RefXY> list = new ArrayList<>();
	for (int i = 0; i < nro; i++) {
	    list.add(new RefXY(this.readRef(),
			       this.readWord(),
			       this.readWord()));
	}
	return list;
    } 

    @Override
    public List<PointXY> readPoints(int nro) {
	List<PointXY> list = new ArrayList<>();
	for (int i = 0; i < nro; i++) {
	    list.add(new PointXY(this.readWord(), this.readWord()));
	}
	return list;
    }

    @Override
    public List<String> readLanguages(int nro) {
	List<String> list = new ArrayList<>();
	for (int i = 0; i < nro; i++) {
	    final String id = sc.next();
	    if (!id.equals("STRING"))
		throw new RuntimeException("string expected: " + id);
	    
	    String val = sc.findInLine("\"[^\"]*\"");
	    if (val == null) 
		throw new RuntimeException("string is missing: " + id);

	    val = val.substring(1, val.length() - 1);
	    list.add(val);
	}
	return list;
    }

    @Override
    public String readString(int len) {
	final String id = sc.next();
	if (!id.equals("STRING"))
	    throw new RuntimeException("string expected: " + id);
	    
	String val = sc.findInLine("\"[^\"]*\"");
	if (val == null) 
	    throw new RuntimeException("string is missing: " + id);
	
	val = val.substring(1, val.length() - 1);

	if (val.length() > len)
	    throw new RuntimeException("string is too long for the allocated " +
				       "storage: \"" + val + "\", len: " + len);
	return val;
    }
}
