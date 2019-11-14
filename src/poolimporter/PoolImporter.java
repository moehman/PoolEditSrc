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

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult; 
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import static jdk.internal.org.jline.utils.Colors.s;
import pooledit.FileTools;

/**
 * Constructor.
 * @author moehman
 */
public class PoolImporter {
    static public final String OBJECTPOOL = "objectpool";
    static public final String INCLUDE_OBJECT = "include_object";
    static public final String NAME = "name";
    static public final String POS_X = "pos_x";
    static public final String POS_Y = "pos_y";
    static public final String WORKINGSET = "workingset";
    static public final String DATAMASK = "datamask";
    static public final String ALARMMASK = "alarmmask";
    static public final String CONTAINER = "container";
    static public final String SOFTKEYMASK = "softkeymask";
    static public final String KEY = "key";
    static public final String BUTTON = "button";
    static public final String INPUTBOOLEAN = "inputboolean";
    static public final String INPUTSTRING = "inputstring";
    static public final String INPUTNUMBER = "inputnumber";
    static public final String INPUTLIST = "inputlist";
    static public final String OUTPUTSTRING = "outputstring";
    static public final String OUTPUTNUMBER = "outputnumber";
    static public final String LINE = "line";
    static public final String RECTANGLE  = "rectangle";
    static public final String ELLIPSE = "ellipse";
    static public final String POLYGON = "polygon";
    static public final String METER = "meter";
    static public final String LINEARBARGRAPH = "linearbargraph";
    static public final String ARCHEDBARGRAPH = "archedbargraph";
    static public final String PICTUREGRAPHIC = "picturegraphic";
    static public final String NUMBERVARIABLE = "numbervariable";
    static public final String STRINGVARIABLE = "stringvariable";
    static public final String FONTATTRIBUTES = "fontattributes";
    static public final String LINEATTRIBUTES = "lineattributes";
    static public final String FILLATTRIBUTES = "fillattributes";
    static public final String INPUTATTRIBUTES = "inputattributes";
    static public final String OBJECTPOINTER = "objectpointer";
    static public final String MACRO = "macro";
    static public final String AUXILIARYFUNCTION = "auxiliaryfunction";
    static public final String AUXILIARYINPUT = "auxiliaryinput";
    static public final String POINT_OBJECT = "point";

    static public final String DIMENSION = "dimension";
    static public final String FIX_BITMAP_PATH = "fix_bitmap_path";
    static public final String STD_BITMAT_PATH = "std_bitmap_path";
    static public final String SK_WIDTH = "sk_width";
    static public final String SK_HEIGHT = "sk_height";

    static public final String BACKGROUND_COLOUR = "background_colour";
    static public final String SELECTABLE = "selectable";
    static public final String ROLE = "role";
    static public final String PRIORITY = "priority";
    static public final String ACOUSTIC_SIGNAL = "acoustic_signal";
    static public final String WIDTH = "width";
    static public final String HEIGHT = "height";
    static public final String HIDDEN = "hidden";
    static public final String KEY_CODE = "key_code";
    static public final String BORDER_COLOUR = "border_colour";
    static public final String FOREGROUND_COLOUR = "foreground_colour";
    static public final String ENABLED = "enabled";
    static public final String VALUE = "value";
    static public final String HORIZONTAL_JUSTIFICATION = "horizontal_justification";
    static public final String OPTIONS = "options";
    static public final String LENGTH = "length";
    static public final String MIN_VALUE = "min_value";
    static public final String MAX_VALUE = "max_value";
    static public final String OFFSET = "offset";
    static public final String SCALE = "scale";
    static public final String NUMBER_OF_DECIMALS = "number_of_decimals";
    static public final String FORMAT = "format";
    static public final String LINE_DIRECTION = "line_direction";
    static public final String LINE_SUPPRESSION = "line_suppression";
    static public final String ELLIPSE_TYPE = "ellipse_type";
    static public final String START_ANGLE = "start_angle";
    static public final String END_ANGLE = "end_angle";
    static public final String POLYGON_TYPE = "polygon_type";
    static public final String NEEDLE_COLOUR = "needle_colour";
    static public final String ARC_AND_TICK_COLOUR = "arc_and_tick_colour";
    static public final String NUMBER_OF_TICKS = "number_of_ticks";
    static public final String COLOUR = "colour";
    static public final String TARGET_LINE_COLOUR = "target_line_colour";
    static public final String TARGET_VALUE = "target_value";
    static public final String TARGET_VALUE_VARIABLE_REFERENCE = "target_value_variable_reference";
    static public final String BAR_GRAPH_WIDTH = "bar_graph_width";
    static public final String TRANSPARENCY_COLOUR = "transparency_colour";
    static public final String ACTUAL_WIDTH = "actual_width";
    static public final String ACTUAL_HEIGHT = "actual_height";
    static public final String FILE = "file";
    static public final String FONT_COLOUR = "font_colour";
    static public final String FONT_SIZE = "font_size";
    static public final String FONT_STYLE = "font_style";
    static public final String FONT_TYPE = "font_type";
    static public final String LINE_ART = "line_art";
    static public final String LINE_COLOUR = "line_colour";
    static public final String LINE_WIDTH = "line_width";
    static public final String FILL_COLOUR = "fill_colour";
    static public final String FILL_TYPE = "fill_type";
    static public final String VALIDATION_TYPE = "validation_type";
    static public final String VALIDATION_STRING = "validation_string";
    static public final String FUNCTION_TYPE = "function_type";
    static public final String INPUT_ID = "input_id";

    static public final String ACTIVE_MASK = "active_mask";
    static public final String SOFT_KEY_MASK = "soft_key_mask";
    static public final String VARIABLE_REFERENCE = "variable_reference";
    static public final String FONT_ATTRIBUTES = "font_attributes";
    static public final String INPUT_ATTRIBUTES = "input_attributes";
    static public final String LINE_ATTRIBUTES = "line_attributes";
    static public final String FILL_ATTRIBUTES = "fill_attributes";
    static public final String FILL_PATTERN = "fill_pattern";

    static public final String YES = "yes";
    static public final String NO = "no";

    static public final String COMMAND_HIDE_SHOW_OBJECT = "command_hide_show_object";
    static public final String COMMAND_ENABLE_DISABLE_OBJECT = "command_enable_disable_object";
    static public final String COMMAND_SELECT_INPUT_OBJECT = "command_select_input_object";
    static public final String COMMAND_CONTROL_AUDIO_DEVICE = "command_control_audio_device";
    static public final String COMMAND_SET_AUDIO_VOLUME = "command_set_audio_volume";
    static public final String COMMAND_CHANGE_CHILD_LOCATION = "command_change_child_location";
    static public final String COMMAND_CHANGE_SIZE = "command_change_size";
    static public final String COMMAND_CHANGE_BACKGROUND_COLOUR = "command_change_background_colour";
    static public final String COMMAND_CHANGE_NUMERIC_VALUE = "command_change_numeric_value";
    static public final String COMMAND_CHANGE_STRING_VALUE = "command_change_string_value";
    static public final String COMMAND_CHANGE_END_POINT = "command_change_end_point";
    static public final String COMMAND_CHANGE_FONT_ATTRIBUTES = "command_change_font_attributes";
    static public final String COMMAND_CHANGE_LINE_ATTRIBUTES = "command_change_line_attributes";
    static public final String COMMAND_CHANGE_FILL_ATTRIBUTES = "command_change_fill_attributes";
    static public final String COMMAND_CHANGE_ACTIVE_MASK = "command_change_active_mask";
    static public final String COMMAND_CHANGE_SOFT_KEY_MASK = "command_change_softkey_mask";
    static public final String COMMAND_CHANGE_ATTRIBUTE = "command_change_attribute";
    static public final String COMMAND_CHANGE_PRIORITY = "command_change_priority";
    static public final String COMMAND_CHANGE_LIST_ITEM = "command_change_list_item";
    static public final String COMMAND_CHANGE_CHILD_POSITION = "command_change_child_position";

    static public final String OBJECT_ID = "object_id";
    static public final String HIDE_SHOW = "hide_show";
    static public final String NUMBER_OF_REPETITIONS = "number_of_repetitions";
    static public final String FREQUENCY = "frequency";
    static public final String ON_TIME = "on_time";
    static public final String OFF_TIME = "off_time";
    static public final String VOLUME = "volume";
    static public final String PARENT_ID = "parent_id";
    static public final String CHILD_ID = "child_id";
    static public final String D_POS_X = "d_pos_x";
    static public final String D_POS_Y = "d_pos_y";
    static public final String MASK_TYPE = "mask_type";
    static public final String ATTRIBUTE_ID = "attribute_id";
    static public final String LIST_INDEX = "list_index";
    static public final String C_POS_X = "c_pos_x";
    static public final String C_POS_Y = "c_pos_y";

    static private final int[] VALUES_8BIT = {
	0x000000, 0xFFFFFF, 0x009900, 0x009999, //0
	0x990000, 0x990099, 0x999900, 0xCCCCCC, //4
	0x999999, 0x0000FF, 0x00FF00, 0x00FFFF, //8
	0xFF0000, 0xFF00FF, 0xFFFF00, 0x000099, //12
	0x000000, 0x000033, 0x000066, 0x000099, //16
	0x0000CC, 0x0000FF, 0x003300, 0x003333, //20
	0x003366, 0x003399, 0x0033CC, 0x0033FF, //24
	0x006600, 0x006633, 0x006666, 0x006699, //28
	0x0066CC, 0x0066FF, 0x009900, 0x009933, //32
	0x009966, 0x009999, 0x0099CC, 0x0099FF, //36
	0x00CC00, 0x00CC33, 0x00CC66, 0x00CC99, //40
	0x00CCCC, 0x00CCFF, 0x00FF00, 0x00FF33, //44
	0x00FF66, 0x00FF99, 0x00FFCC, 0x00FFFF, //48
	0x330000, 0x330033, 0x330066, 0x330099, //52
	0x3300CC, 0x3300FF, 0x333300, 0x333333, //56
	0x333366, 0x333399, 0x3333CC, 0x3333FF, //60
	0x336600, 0x336633, 0x336666, 0x336699, //64
	0x3366CC, 0x3366FF, 0x339900, 0x339933, //68
	0x339966, 0x339999, 0x3399CC, 0x3399FF, //72
	0x33CC00, 0x33CC33, 0x33CC66, 0x33CC99, //76
	0x33CCCC, 0x33CCFF, 0x33FF00, 0x33FF33, //80
	0x33FF66, 0x33FF99, 0x33FFCC, 0x33FFFF, //84
	0x660000, 0x660033, 0x660066, 0x660099, //88
	0x6600CC, 0x6600FF, 0x663300, 0x663333, //92
	0x663366, 0x663399, 0x6633CC, 0x6633FF, //96
	0x666600, 0x666633, 0x666666, 0x666699, //100
	0x6666CC, 0x6666FF, 0x669900, 0x669933, //104
	0x669966, 0x669999, 0x6699CC, 0x6699FF, //108
	0x66CC00, 0x66CC33, 0x66CC66, 0x66CC99, //112
	0x66CCCC, 0x66CCFF, 0x66FF00, 0x66FF33, //116
	0x66FF66, 0x66FF99, 0x66FFCC, 0x66FFFF, //120
	0x990000, 0x990033, 0x990066, 0x990099, //124
	0x9900CC, 0x9900FF, 0x993300, 0x993333, //128
	0x993366, 0x993399, 0x9933CC, 0x9933FF, //132
	0x996600, 0x996633, 0x996666, 0x996699, //136
	0x9966CC, 0x9966FF, 0x999900, 0x999933, //140
	0x999966, 0x999999, 0x9999CC, 0x9999FF, //144
	0x99CC00, 0x99CC33, 0x99CC66, 0x99CC99, //148
	0x99CCCC, 0x99CCFF, 0x99FF00, 0x99FF33,
	0x99FF66, 0x99FF99, 0x99FFCC, 0x99FFFF,
	0xCC0000, 0xCC0033, 0xCC0066, 0xCC0099,
	0xCC00CC, 0xCC00FF, 0xCC3300, 0xCC3333,
	0xCC3366, 0xCC3399, 0xCC33CC, 0xCC33FF,
	0xCC6600, 0xCC6633, 0xCC6666, 0xCC6699,
	0xCC66CC, 0xCC66FF, 0xCC9900, 0xCC9933,
	0xCC9966, 0xCC9999, 0xCC99CC, 0xCC99FF,
	0xCCCC00, 0xCCCC33, 0xCCCC66, 0xCCCC99,
	0xCCCCCC, 0xCCCCFF, 0xCCFF00, 0xCCFF33,
	0xCCFF66, 0xCCFF99, 0xCCFFCC, 0xCCFFFF,
	0xFF0000, 0xFF0033, 0xFF0066, 0xFF0099,
	0xFF00CC, 0xFF00FF, 0xFF3300, 0xFF3333,
	0xFF3366, 0xFF3399, 0xFF33CC, 0xFF33FF,
	0xFF6600, 0xFF6633, 0xFF6666, 0xFF6699,
	0xFF66CC, 0xFF66FF, 0xFF9900, 0xFF9933,
	0xFF9966, 0xFF9999, 0xFF99CC, 0xFF99FF,
	0xFFCC00, 0xFFCC33, 0xFFCC66, 0xFFCC99,
	0xFFCCCC, 0xFFCCFF, 0xFFFF00, 0xFFFF33,
	0xFFFF66, 0xFFFF99, 0xFFFFCC, 0xFFFFFF
    };
    private final File inputFile;
    private final int dimension;
    private final int sk_width;
    private final int sk_height;
    private final String fixBitmapPath;
    private final String stdBitmapPath;
    private final File outputFile;

    /**
     * Constructor.
     * @param inputFile
     * @param dimension
     * @param sk_width
     * @param sk_height
     * @param fixBitmapPath
     * @param stdBitmapPath
     * @param outputFile
     */
    public PoolImporter(
            File inputFile,
            int dimension,
            int sk_width,
            int sk_height,
            String fixBitmapPath,
            String stdBitmapPath,
            File outputFile) {
        this.inputFile = inputFile;
        this.dimension = dimension;
        this.sk_width = sk_width;
        this.sk_height = sk_height;
        this.fixBitmapPath = fixBitmapPath;
        this.stdBitmapPath = stdBitmapPath;
        this.outputFile = outputFile;
    }
    /**
     *
     * @param br
     * @param len
     * @return
     */
    public List<VTObject> readCommands(ByteReader br, int len) {
	List<VTObject> list = new ArrayList<>();	
	int count = 0;
	while (count < len) {
	    final int type = br.readType();
	    switch (type) {
	    case 160: // command_hide_show_object
		list.add(new VTAbstObj() { 
			int objectid;
			boolean show;
			public void read(ByteReader br) {
			    objectid = br.readWord();
			    show = br.readByte() != 0;
			    br.readDWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_hide_show_object/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element hideshow = doc.createElement(COMMAND_HIDE_SHOW_OBJECT);
			    hideshow.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    hideshow.setAttribute(HIDE_SHOW, show ? "show" : "hide");
			    root.appendChild(hideshow);
			}
			public String getType() { return "hide_show_object"; }
		    });
		count += 8;
		break;
	    case 161: // command_enable_disable_object enable_disable="enable" object_id="3000"
		list.add(new VTAbstObj() { 
			int objectid;
			boolean enable;
			public void read(ByteReader br) {
			    objectid = br.readWord();
			    enable = br.readByte() != 0;
			    br.readDWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_enable_disable_object/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element enabledisable = doc.createElement(COMMAND_ENABLE_DISABLE_OBJECT);
			    enabledisable.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    enabledisable.setAttribute(HIDE_SHOW, enable ? "enable" : "disable");
			    root.appendChild(enabledisable);
			}
			public String getType() { return "enable_disable_object"; }
		    });
		count += 8;
		break;
	    case 162: // command_select_input_object object_id=""
		list.add(new VTAbstObj() { 
			int objectid;
			public void read(ByteReader br) {
			    objectid = br.readWord();
			    br.readByte();
			    br.readDWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_select_input_object/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element selectinput = doc.createElement(COMMAND_SELECT_INPUT_OBJECT);
			    selectinput.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    root.appendChild(selectinput);
			}
			public String getType() { return "select_input_object"; }
		    });
		count += 8;
		break;
	    case 163: // command_control_audio_device frequency="0" number_of_repetitions="0" off_time="0" on_time="0"
		list.add(new VTAbstObj() { 
			int repetitions;
			int frequency;
			int ontime;
			int offtime;
			public void read(ByteReader br) {
			    repetitions = br.readByte();
			    frequency = br.readWord();
			    ontime = br.readWord();
			    offtime = br.readWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_control_audio_device/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element controlaudio = doc.createElement(COMMAND_CONTROL_AUDIO_DEVICE);
			    controlaudio.setAttribute(NUMBER_OF_REPETITIONS, Integer.toString(repetitions));
			    controlaudio.setAttribute(FREQUENCY, Integer.toString(frequency));
			    controlaudio.setAttribute(ON_TIME, Integer.toString(ontime));
			    controlaudio.setAttribute(OFF_TIME, Integer.toString(offtime));
			    root.appendChild(controlaudio);
			}
			public String getType() { return "control_audio_device"; }
		    });
		count += 8;
		break;
	    case 164: // command_set_audio_volume volume="0"
		list.add(new VTAbstObj() { 
			int volume;
			public void read(ByteReader br) {
			    volume = br.readByte();
			    br.readWord();
			    br.readDWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_set_audio_volume/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element setvolume = doc.createElement(COMMAND_SET_AUDIO_VOLUME);
			    setvolume.setAttribute(VOLUME, Integer.toString(volume));
			    root.appendChild(setvolume);
			}
			public String getType() { return "set_audio_volume"; };
		    });
		count += 8;
		break;
	    case 165: // command_change_child_location child_id="0" d_pos_x="0" d_pos_y="0" parent_id="0"
		list.add(new VTAbstObj() { 
			int parentid;
			int childid;
			int dx;
			int dy;
			public void read(ByteReader br) {
			    parentid = br.readId();
			    childid = br.readId();
			    dx = br.readByte() - 127; // apply offset
			    dy = br.readByte() - 127;
			    br.readByte();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_child_location/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changechild = doc.createElement(COMMAND_CHANGE_CHILD_LOCATION);
			    changechild.setAttribute(PARENT_ID, Integer.toString(parentid));
			    changechild.setAttribute(CHILD_ID, Integer.toString(childid));
			    changechild.setAttribute(D_POS_X, Integer.toString(dx));
			    changechild.setAttribute(D_POS_Y, Integer.toString(dy));
			    root.appendChild(changechild);
			}
			public String getType() { return "change_child_location"; };
		    });
		count += 8;
		break;
	    case 166: // command_change_size height="0" object_id="0" width="0"
		list.add(new VTAbstObj() { 
			int objectid;
			int width;
			int height;
			public void read(ByteReader br) {
			    objectid = br.readId();
			    width = br.readWord();
			    height = br.readWord();
			    br.readByte();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_set_audio_volume/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changesize = doc.createElement(COMMAND_CHANGE_SIZE);
			    changesize.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    changesize.setAttribute(WIDTH, Integer.toString(width));
			    changesize.setAttribute(HEIGHT, Integer.toString(height));
			    root.appendChild(changesize);
			}
			public String getType() { return "change_size"; };
		    });
		count += 8;
		break;
	    case 167: // command_change_background_colour background_colour="black" object_id="0"
		list.add(new VTAbstObj() { 
			int objectid;
			int bgcol;
			public void read(ByteReader br) {
			    objectid = br.readId();
			    bgcol = br.readColor();
			    br.readDWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_background_colour/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changebgcol = doc.createElement(COMMAND_CHANGE_BACKGROUND_COLOUR);
			    changebgcol.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    changebgcol.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
			    root.appendChild(changebgcol);
			}
			public String getType() { return "change_size"; };
		    });
		count += 8;
		break;
	    case 168: // command_change_numeric_value object_id="0" value="0"
		list.add(new VTAbstObj() { 
			int objectid;
			int value;
			public void read(ByteReader br) {
			    objectid = br.readId();
			    br.readByte();
			    value = br.readDWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_numeric_value/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changevalue = doc.createElement(COMMAND_CHANGE_NUMERIC_VALUE);
			    changevalue.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    changevalue.setAttribute(VALUE, Integer.toString(value));
			    root.appendChild(changevalue);
			}
			public String getType() { return "change_numeric_value"; };
		    });		
		count += 8;
		break;
	    case 179: // command_change_string_value length="0" object_id="0" value=""
		final int objectid_ = br.readId();
		final int length_ = br.readWord();
		final String value_ = br.readString(length_);
		list.add(new VTAbstObj() { 
			int objectid;
			int length;
			String value;
			public void read(ByteReader br) {
			    objectid = objectid_;
			    length = length_;
			    value = value_;
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_string_value/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changevalue = doc.createElement(COMMAND_CHANGE_STRING_VALUE);
			    changevalue.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    changevalue.setAttribute(LENGTH, Integer.toString(length));
			    changevalue.setAttribute(VALUE, value);
			    root.appendChild(changevalue);
			}
			public String getType() { return "change_string_value"; };
		    });
		count += 5 + length_;
		break;
	    case 169: // command_change_end_point height="0" line_direction="toplefttobottomright" object_id="0" width="0"
		list.add(new VTAbstObj() { 
			int objectid;
			int width;
			int height;
			int linedir;
			public void read(ByteReader br) {
			    objectid = br.readId();
			    width = br.readWord();
			    height = br.readWord();
			    linedir = br.readByte();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_end_point/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changepoint = doc.createElement(COMMAND_CHANGE_END_POINT);
			    changepoint.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    changepoint.setAttribute(WIDTH, Integer.toString(width));
			    changepoint.setAttribute(HEIGHT, Integer.toString(height));
			    changepoint.setAttribute(LINE_DIRECTION, Integer.toString(linedir));
			    root.appendChild(changepoint);
			}
			public String getType() { return "change_change_end_point"; };
		    });
		count += 8;
		break;
	    case 170: // command_change_font_attributes font_colour="black" font_size="6x8" font_style="" font_type="latin1" object_id="0"
		list.add(new VTAbstObj() {
			int objectid;
			int fontcol;
			int fontsize;
			int fonttype;
			int fontstyle;
			public void read(ByteReader br) {
			    objectid = br.readId();
			    fontcol = br.readColor();
			    fontsize = br.readByte();
			    fonttype = br.readByte();
			    fontstyle = br.readByte();
			    br.readByte();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_font_attributes/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changefont = doc.createElement(COMMAND_CHANGE_FONT_ATTRIBUTES);
			    changefont.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    changefont.setAttribute(FONT_COLOUR, getColor(fontcol));
			    changefont.setAttribute(FONT_SIZE, getFontSize(fontsize));
			    changefont.setAttribute(FONT_TYPE, getFontType(fonttype));
			    changefont.setAttribute(FONT_STYLE, getFontStyle(fontstyle));
			    root.appendChild(changefont);
			}
			public String getType() { return "change_change_font_attributes"; };
		    });
		count += 8;
		break;
	    case 171: // command_change_line_attributes line_art="65535" line_colour="black" line_width="1" object_id="0"
		list.add(new VTAbstObj() {
			int objectid;
			int linecol;
			int linewidth;
			int lineart;
			public void read(ByteReader br) {
			    objectid = br.readId();
			    linecol = br.readColor();
			    linewidth = br.readByte();
			    lineart = br.readWord();
			    br.readByte();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_line_attributes/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changeline = doc.createElement(COMMAND_CHANGE_LINE_ATTRIBUTES);
			    changeline.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    changeline.setAttribute(LINE_COLOUR, getColor(linecol));
			    changeline.setAttribute(LINE_WIDTH, Integer.toString(linewidth));
			    changeline.setAttribute(LINE_ART, getLineArt(lineart));
			    root.appendChild(changeline);
			}
			public String getType() { return "change_change_line_attributes"; };
		    });
		count += 8;
		break;
	    case 172: // command_change_fill_attributes fill_colour="black" fill_type="fillcolour" object_id="0"
		list.add(new VTAbstObj() {
			int objectid;
			int filltype;
			int fillcol;
			int fillpatternref;
			public void read(ByteReader br) {
			    objectid = br.readId();
			    filltype = br.readByte();
			    fillcol = br.readColor();
			    fillpatternref = br.readRef();
			    br.readByte();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_fill_attributes/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changefill = doc.createElement(COMMAND_CHANGE_FILL_ATTRIBUTES);
			    changefill.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    changefill.setAttribute(FILL_TYPE, getFillType(filltype));
			    changefill.setAttribute(FILL_COLOUR, getColor(fillcol));
			    // FIXME: this should be an attribute, or
			    // all references should be like this in
			    // all commands
			    if (map.containsKey(fillpatternref)) { 
				Element fillpattr = doc.createElement(INCLUDE_OBJECT);
				fillpattr.setAttribute(NAME, map.get(fillpatternref));
				fillpattr.setAttribute(ROLE, FILL_PATTERN);
				changefill.appendChild(fillpattr);
			    }
			    root.appendChild(changefill);
			}
			public String getType() { return "change_fill_attributes"; };
		    });
		count += 8;
		break;
	    case 173: // command_change_active_mask child_id="0" parent_id="0"
		list.add(new VTAbstObj() {
			int parentid;
			int childid;
			public void read(ByteReader br) {
			    parentid = br.readId();
			    childid = br.readId();
			    br.readByte();
			    br.readWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_active_mask/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changemask = doc.createElement(COMMAND_CHANGE_ACTIVE_MASK);
			    changemask.setAttribute(PARENT_ID, Integer.toString(parentid));
			    changemask.setAttribute(CHILD_ID, Integer.toString(childid));
			    root.appendChild(changemask);
			}
			public String getType() { return "change_active_mask"; };
		    });
		count += 8;
		break;
	    case 174: // command_change_soft_key_mask child_id="0" mask_type="1" parent_id="0"
		list.add(new VTAbstObj() {
			int masktype;
			int parentid;
			int childid;
			public void read(ByteReader br) {
			    masktype = br.readByte();
			    parentid = br.readId();
			    childid = br.readId();
			    br.readWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_soft_key_mask/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changemask = doc.createElement(COMMAND_CHANGE_SOFT_KEY_MASK);
			    changemask.setAttribute(MASK_TYPE, getMaskType(masktype));
			    changemask.setAttribute(PARENT_ID, Integer.toString(parentid));
			    changemask.setAttribute(CHILD_ID, Integer.toString(childid));
			    root.appendChild(changemask);
			}
			public String getType() { return "change_soft_key_mask"; };
		    });
		count += 8;
		break;
	    case 175: // command_change_attribute attribute_id="1" object_id="0" value="0"
		list.add(new VTAbstObj() {
			int objectid;
			int aid;
			int value;
			public void read(ByteReader br) {
			    objectid = br.readId();
			    aid = br.readByte();
			    value = br.readDWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_attribute/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changeattribute = doc.createElement(COMMAND_CHANGE_ATTRIBUTE);
			    changeattribute.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    changeattribute.setAttribute(ATTRIBUTE_ID, Integer.toString(aid));
			    changeattribute.setAttribute(VALUE, Integer.toString(value));
			    root.appendChild(changeattribute);
			}
			public String getType() { return "change_attribute"; };
		    });
		count += 8;
		break;
	    case 176: // command_change_priority object_id="0" priority="high"
		list.add(new VTAbstObj() {
			int objectid;
			int priority;
			public void read(ByteReader br) {
			    objectid = br.readId();
			    priority = br.readByte();
			    br.readByte();
			    br.readDWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_priority/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changepriority = doc.createElement(COMMAND_CHANGE_PRIORITY);
			    changepriority.setAttribute(OBJECT_ID, Integer.toString(objectid));
			    changepriority.setAttribute(PRIORITY, getPriority(priority));
			    root.appendChild(changepriority);
			}
			public String getType() { return "change_priority"; };
		    });
		count += 8;
		break;
	    case 177: //   <command_change_list_item child_id="0" list_index="0" parent_id="0"/>
		list.add(new VTAbstObj() {
			int parentid;
			int listindex;
			int childid;
			public void read(ByteReader br) {
			    parentid = br.readId();
			    listindex = br.readByte();
			    childid = br.readId();
			    br.readWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_list_item/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changeindex = doc.createElement(COMMAND_CHANGE_LIST_ITEM);
			    changeindex.setAttribute(PARENT_ID, Integer.toString(parentid));
			    changeindex.setAttribute(LIST_INDEX, Integer.toString(listindex));
			    changeindex.setAttribute(CHILD_ID, Integer.toString(childid));
			    root.appendChild(changeindex);
			}
			public String getType() { return "change_list_item"; };
		    });
		count += 8;
		break;
	    case 180: // command_change_child_position c_pos_x="0" c_pos_y="0" child_id="0" parent_id="0"
		list.add(new VTAbstObj() {
			int parentid;
			int childid;
			int x;
			int y;
			public void read(ByteReader br) {
			    parentid = br.readId();
			    childid = br.readId();
			    x = br.readWord();
			    y = br.readWord();
			}
			public void emitXML(Map<Integer, String> map, PrintStream out) {
			    out.format("<command_change_child_position/>\n");
			}
			public void appendDoc(Map<Integer, String> map, Document doc) {
			    Element root = doc.getDocumentElement();
			    Element changeindex = doc.createElement(COMMAND_CHANGE_CHILD_POSITION);
			    changeindex.setAttribute(PARENT_ID, Integer.toString(parentid));
			    changeindex.setAttribute(CHILD_ID, Integer.toString(childid));
			    changeindex.setAttribute(C_POS_X, Integer.toString(x));
			    changeindex.setAttribute(C_POS_Y, Integer.toString(y));
			    root.appendChild(changeindex);
			}
			public String getType() { return "change_child_position"; };
		    });
		count += 9;
		break;
	    }	    
	}

	return list;
    }

    //************************************************************
    
    public String getColor(int col) {
	final String[] COLS = {"black", "white", "green", "teal",
			       "maroon", "purple", "olive", "silver",
			       "grey", "blue", "lime", "cyan", 
			       "red", "magenta", "yellow", "navy"};

	if (col < 16)
	    return COLS[col];
	
	return Integer.toString(col);
    }

    public String getPriority(int pri) {
	final String[] PRIS = {"high", "medium", "low"};
	return PRIS[pri];
    }

    public String getAcousticSignal(int sig) {
	final String[] SIGS = {"high", "medium", "low", "none"};
	return SIGS[sig];
    }

    public String getHorizontalJustification(int just) {
	final String[] JUSTS = {"left", "middle", "right"};
	return JUSTS[just];
    }

    public String getNumberFormat(int format) {
	final String[] FORMATS = {"fixed", "exponential"};
	return FORMATS[format];
    }

    public String getPictureFormat(int format) {
	//1bit|4bit|8bit
	final String[] FORMATS = {"1bit", "4bit" , "8bit"}; // FIXME: check the standard!
	return FORMATS[format];
    }

    public String getScale(float scale) {
	return String.format("%f", scale);
    }

    public String getLineArt(int art) {
	String s = Integer.toBinaryString(art);           // creates a bin string
	s = "0000000000000000".substring(s.length()) + s; // pads it with leading zeros
	return s;
    }

    public String getFontSize(int size) {
	final String[] SIZES = {"6x8", "8x8", "8x12",
				"12x16", "16x16", "16x24",
				"24x32", "32x32", "32x48",
				"48x64", "64x64", "64x96",
				"96x128", "128x128", "128x192"};
	return SIZES[size];
    }

    private String getOptions(int opt, String[] OPTS) {
	return getOptions(opt, "none", OPTS);
    }

    private String getOptions(int opt, String def, String[] OPTS) {
	if (opt == 0)
	    return def;

	String s = "";	
	for (int i = 0, b = 1; i < OPTS.length; i++, b <<= 1) {
	    if ((opt & b) != 0) 
		s += OPTS[i] + "+";	    
	}
	return s.substring(0, s.length() - 1);
    }

    /**
     * This method is used by both input and output strings.
     * @param opt
     * @return
     */
    public String getStringOptions(int opt) {
	final String[] OPTS = {"transparent", "autowrap"};
	return getOptions(opt, OPTS);
	/*
	if (opt == 0)
	    return "none";

	String s = "";
	if ((opt & 0x01) != 0)
	    s += "transparent+";
	if ((opt & 0x02) != 0)
	    s += "autowrap+";
	return s.substring(0, s.length() - 1);
	*/
    }

    /**
     * This method is used by both input and output numbers.
     * @param opt
     * @return
     */
    public String getNumberOptions(int opt) {
	final String[] OPTS = {"transparent", "leadingzeros", "blankzero"};
	return getOptions(opt, OPTS);
	/*
	if (opt == 0)
	    return "none";

	String s = "";
	if ((opt & 0x01) != 0) 
	    s += "transparent+";
	if ((opt & 0x02) != 0) 
	    s += "leadingzeros+";
	if ((opt & 0x04) != 0)
	    s += "blankzero+";
	return s.substring(0, s.length() - 1);
	*/
    }
    
    public String getMeterOptions(int opt) {
	final String[] OPTS = {"arc", "border", "ticks", "clockwise"};
	return getOptions(opt, OPTS);
	/*
	if (opt == 0)
	    return "none";

	String s = "";
	if ((opt & 0x01) != 0) 
	    s += "arc+";
	if ((opt & 0x02) != 0) 
	    s += "border+";
	if ((opt & 0x04) != 0)
	    s += "ticks+";
	if ((opt & 0x04) != 0)
	    s += "clockwise+";
	return s.substring(0, s.length() - 1);
	*/
    }
    
    public String getLineabrarGraphOptions(int opt) {
	final String[] OPTS = {"border", "targetline", "ticks", "nofill", "horizontal" , "growpositive"};
	return getOptions(opt, OPTS);
    }

    public String getArchedBarGraphOptions(int opt) {
	final String[] OPTS = {"border", "targetline", null, "nofill", "clockwise"};
	return getOptions(opt, OPTS);
    }

    public String getPictureGraphicOptions(int opt) {
	//none|(rle|rle1|rle4|rle8|opaque|normal|transparent|flashing
	final String[] OPTS = {"transparent", "flashing"}; // FIXME: STUFF MISSING! CHECK THE STANDARD!
	return getOptions(opt, OPTS);
    }

    public String getFontType(int opt) {
	//0|1|2|255|latin1|latin9|latin5|proprietary
	switch (opt) {
	case 0:
	    return "latin1";
	case 1:
	    return "latin9";
	case 2:
	    return "latin5";
	case 255:
	    return "proprietary";
	default:
	    throw new RuntimeException("undefined font type: " + opt);
	}
    }

    public String getFontStyle(int opt) {
	//normal|(bold|crossed|underlined|italic|inverted|flashinginverted|flashinghidden
	final String[] OPTS = {"bold", "crossed", "underlined", "italic", "inverted", "flashinginverted", "flashinghidden"};
	return getOptions(opt, "normal", OPTS);
    }

    public String getLineDirection(int dir) {
	// alternatively this function could just return 0 or 1
	final String[] DIRS = {"toplefttobottomright", "bottomlefttotopright"}; 
	return DIRS[dir];
    }

    public String getEllipseType(int type) {
	final String[] TYPES = {"closed", "open", "closedsegment", "closedsection"};
	return TYPES[type];
    }
    
    public String getPolygonType(int type) {
	final String[] TYPES = {"convex", "nonconvex", "complex", "open"};
	return TYPES[type];
    }

    public String getFillType(int type) {
	final String[] TYPES = {"nofill", "linecolour", "fillcolour", "pattern"};
	return TYPES[type];
    }

    public String getValidationType(int type) {
	final String[] TYPES = {"validcharacters", "invalidcharacters"};
	return TYPES[type];
    }

    public String getFunctionType(int type) {
	 final String[] TYPES = {"boolean", "analog"};
	 return TYPES[type];
    }
    
    public String getMaskType(int type) {
	// alternatively this function could just return 1 or 2
	final String[] TYPES = {null, "datamask", "alarmmask"};
	return TYPES[type];
    }

    //************************************************************

    public VTObject createVTO(ByteReader br) {
	final int type = br.readType();
	switch (type) {
	case 0: // working set
	    return new VTAbstObj() { 
		int bgcol;
		boolean selectable;
		int activemaskid;
		List<RefXY> objects;
		List<Integer> macros;
		List<String> languages;
		public void read(ByteReader br) {
		    bgcol = br.readColor();
		    selectable = br.readByte() != 0;
		    activemaskid = br.readRef();
		    int nroObjects = br.readByte();
		    int nroMacros = br.readByte();
		    int nroLanguages = br.readByte();
		    objects = br.readRefXYs(nroObjects);
		    macros = br.readBytes(nroMacros);
		    languages = br.readLanguages(nroLanguages);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<workingset name=\"%s\">\n", name);
		    for (RefXY ref : objects) {
			out.format("  <include_object name=\"%s\" pos_x=\"%d\" pos_y=\"%d\"/>\n",
				   map.get(ref.id), ref.x, ref.y);
		    }
		    out.format("</workingset>\n");
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element workingset = doc.createElement(WORKINGSET);
		    workingset.setAttribute(NAME, map.get(id));
		    workingset.setAttribute(BACKGROUND_COLOUR, getColor(bgcol)); 
		    workingset.setAttribute(SELECTABLE, selectable ? YES : NO);
		    if (map.containsKey(activemaskid)) {
			Element activemask = doc.createElement(INCLUDE_OBJECT);
			activemask.setAttribute(NAME, map.get(activemaskid));
			activemask.setAttribute(ROLE, ACTIVE_MASK);
			workingset.appendChild(activemask);
		    }
		    for (RefXY ref : objects) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(ref.id));
			child.setAttribute(POS_X, Integer.toString(ref.x));
			child.setAttribute(POS_Y, Integer.toString(ref.y));
			workingset.appendChild(child);
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			workingset.appendChild(child);
		    }
		    root.appendChild(workingset);
		}
		public String getType() { return "workingset"; }
	    };
	case 1: // data mask
	    return new VTAbstObj() {
		int bgcol;
		int softkeymaskid;
		List<RefXY> objects;
		List<Integer> macros;
		public void read(ByteReader br) {
		    bgcol = br.readColor();
		    softkeymaskid = br.readRef();
		    int nroObjects = br.readByte();
		    int nroMacros = br.readByte();
		    objects = br.readRefXYs(nroObjects);
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<datamask name=\"%s\">\n", name);
		    for (RefXY ref : objects) {
			out.format("  <include_object name=\"%s\" pos_x=\"%d\" pos_y=\"%d\"/>\n",
				   map.get(ref.id), ref.x, ref.y);
		    }
		    out.format("</datamask>\n");
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element datamask = doc.createElement(DATAMASK);
		    datamask.setAttribute(NAME, map.get(id));
		    datamask.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
		    if (map.containsKey(softkeymaskid)) {
			Element softkeymask = doc.createElement(INCLUDE_OBJECT);
			softkeymask.setAttribute(NAME, map.get(softkeymaskid));
			softkeymask.setAttribute(ROLE, SOFT_KEY_MASK);
			datamask.appendChild(softkeymask);
		    }
		    for (RefXY ref : objects) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(ref.id));
			child.setAttribute(POS_X, Integer.toString(ref.x));
			child.setAttribute(POS_Y, Integer.toString(ref.y));
			datamask.appendChild(child);
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			datamask.appendChild(child);
		    }
		    root.appendChild(datamask);
		}
		public String getType() { return "datamask"; }
	    };
	case 2: // alarm mask
	    return new VTAbstObj() {
		int bgcol;
		int softkeymaskid;
		int priority;
		int acousticsig;
		List<RefXY> objects;
		List<Integer> macros;
		public void read(ByteReader br) {
		    bgcol = br.readColor();
		    softkeymaskid = br.readRef();
		    priority = br.readByte();
		    acousticsig = br.readByte();
		    int nroObjects = br.readByte();
		    int nroMacros = br.readByte();
		    objects = br.readRefXYs(nroObjects);
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<alarmmask name=\"%s\">\n", name);
		    for (RefXY ref : objects) {
			out.format("  <include_object name=\"%s\" pos_x=\"%d\" pos_y=\"%d\"/>\n",
				   map.get(ref.id), ref.x, ref.y);
		    }
		    out.format("</alarmmask>\n");
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element alarmmask = doc.createElement(ALARMMASK);
		    alarmmask.setAttribute(NAME, map.get(id));
		    alarmmask.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
		    alarmmask.setAttribute(PRIORITY, getPriority(priority));
		    alarmmask.setAttribute(ACOUSTIC_SIGNAL, getAcousticSignal(acousticsig));
		    if (map.containsKey(softkeymaskid)) {
			Element softkeymask = doc.createElement(INCLUDE_OBJECT);
			softkeymask.setAttribute(NAME, map.get(softkeymaskid));
			softkeymask.setAttribute(ROLE, SOFT_KEY_MASK);
			alarmmask.appendChild(softkeymask);
		    }
		    for (RefXY ref : objects) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(ref.id));
			child.setAttribute(POS_X, Integer.toString(ref.x));
			child.setAttribute(POS_Y, Integer.toString(ref.y));
			alarmmask.appendChild(child);
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			alarmmask.appendChild(child);
		    }
		    root.appendChild(alarmmask);
		}
		public String getType() { return "alarmmask"; }
	    };
	case 3: // container
	    return new VTAbstObj() {
		int width;
		int height;
		boolean hidden;
		List<RefXY> objects;
		List<Integer> macros;
		public void read(ByteReader br) {
		    width = br.readWord();
		    height = br.readWord();
		    hidden = br.readByte() != 0;
		    int nroObjects = br.readByte();
		    int nroMacros = br.readByte();
		    objects = br.readRefXYs(nroObjects);
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<container name=\"%s\">\n", name);
		    for (RefXY ref : objects) {
			out.format("  <include_object name=\"%s\" pos_x=\"%d\" pos_y=\"%d\"/>\n",
				   map.get(ref.id), ref.x, ref.y);
		    }
		    out.format("</container>\n");
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element container = doc.createElement(CONTAINER);
		    container.setAttribute(NAME, map.get(id));
		    container.setAttribute(WIDTH, Integer.toString(width));
		    container.setAttribute(HEIGHT, Integer.toString(height));
		    container.setAttribute(HIDDEN, Boolean.toString(hidden));
		    for (RefXY ref : objects) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(ref.id));
			child.setAttribute(POS_X, Integer.toString(ref.x));
			child.setAttribute(POS_Y, Integer.toString(ref.y));
			container.appendChild(child);
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			container.appendChild(child);
		    }
		    root.appendChild(container);
		}
		public String getType() { return "container"; }
	    };
	case 4: // softkey mask
	    return new VTAbstObj() {
		int bgcol;
		List<Integer> objects;
		List<Integer> macros;
		public void read(ByteReader br) {
		    bgcol = br.readColor();
		    int nroObjects = br.readByte();
		    int nroMacros = br.readByte();
		    objects = br.readRefs(nroObjects);
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<softkeymask name=\"%s\">\n", name);
		    for (Integer oid : objects) {
			out.format("  <include_object name=\"%s\"/>\n",
				   map.get(oid));
		    }
		    out.format("</softkeymask>\n");
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element softkeymask = doc.createElement(SOFTKEYMASK);
		    softkeymask.setAttribute(NAME, map.get(id));
		    softkeymask.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
		    for (Integer oid : objects) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(oid));
			softkeymask.appendChild(child);
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			softkeymask.appendChild(child);
		    }
		    root.appendChild(softkeymask);
		}
		public String getType() { return "softkeymask"; }
	    };
	case 5: // key
	    return new VTAbstObj() {
		int bgcol;
		int keycode;
		List<RefXY> objects;
		List<Integer> macros;
		public void read(ByteReader br) {
		    bgcol = br.readColor();
		    keycode = br.readKeyCode();
		    int nroObjects = br.readByte();
		    int nroMacros = br.readByte();
		    objects = br.readRefXYs(nroObjects);
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<key name=\"%s\">\n", name);
		    for (RefXY ref : objects) {
			out.format("  <include_object name=\"%s\" pos_x=\"%d\" pos_y=\"%d\"/>\n",
				   map.get(ref.id), ref.x, ref.y);
		    }
		    out.format("</key>\n");
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element key = doc.createElement(KEY);
		    key.setAttribute(NAME, map.get(id));
		    key.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
		    key.setAttribute(KEY_CODE, Integer.toString(keycode));
		    for (RefXY ref : objects) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(ref.id));
			child.setAttribute(POS_X, Integer.toString(ref.x));
			child.setAttribute(POS_Y, Integer.toString(ref.y));
			key.appendChild(child);
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			key.appendChild(child);
		    }
		    root.appendChild(key);
		}
		public String getType() { return "key"; }
	    };
	case 6: // button
	    return new VTAbstObj() {
		int width;
		int height;
		int bgcol;
		int bordercol;
		int keycode;
		boolean latchable;
		List<RefXY> objects;
		List<Integer> macros;
		public void read(ByteReader br) {
		    width = br.readWord();
		    height = br.readWord();
		    bgcol = br.readColor();
		    bordercol = br.readColor();
		    keycode = br.readKeyCode();
		    latchable = br.readByte() != 0;
		    int nroObjects = br.readByte();
		    int nroMacros = br.readByte();
		    objects = br.readRefXYs(nroObjects);
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<button name=\"%s\">\n", name);
		    for (RefXY ref : objects) {
			out.format("  <include_object name=\"%s\" pos_x=\"%d\" pos_y=\"%d\"/>\n",
				   map.get(ref.id), ref.x, ref.y);
		    }
		    out.format("</button>\n");
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element button = doc.createElement(BUTTON);
		    button.setAttribute(NAME, map.get(id));
		    button.setAttribute(WIDTH, Integer.toString(width));
		    button.setAttribute(HEIGHT, Integer.toString(height));
		    button.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
		    button.setAttribute(BORDER_COLOUR, getColor(bordercol));
		    button.setAttribute(KEY_CODE, Integer.toString(keycode));
		    for (RefXY ref : objects) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(ref.id));
			child.setAttribute(POS_X, Integer.toString(ref.x));
			child.setAttribute(POS_Y, Integer.toString(ref.y));
			button.appendChild(child);
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			button.appendChild(child);
		    }
		    root.appendChild(button);
		}
		public String getType() { return "button"; }
	    };
	case 7: // input boolean
	    return new VTAbstObj() {
		int bgcol;
		int width;
		int fgcolref;
		int varref;
		boolean value;
		boolean enabled;
		List<Integer> macros;
		public void read(ByteReader br) {
		    bgcol = br.readColor();
		    width = br.readWord();
		    fgcolref = br.readRef();
		    varref = br.readRef();
		    value = br.readByte() != 0;
		    enabled = br.readByte() != 0;
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<inputboolean name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element inputboolean = doc.createElement(INPUTBOOLEAN);
		    inputboolean.setAttribute(NAME, map.get(id));
		    inputboolean.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
		    inputboolean.setAttribute(WIDTH, Integer.toString(width));
		    if (map.containsKey(fgcolref)) {
			Element fgcolour = doc.createElement(INCLUDE_OBJECT);
			fgcolour.setAttribute(NAME, map.get(fgcolref));
			fgcolour.setAttribute(ROLE, FOREGROUND_COLOUR);
			inputboolean.appendChild(fgcolour);
		    }
		    if (map.containsKey(varref)) {
			Element variableref = doc.createElement(INCLUDE_OBJECT);
			variableref.setAttribute(NAME, map.get(varref));
			variableref.setAttribute(ROLE, VARIABLE_REFERENCE);
			inputboolean.appendChild(variableref);
		    }
		    inputboolean.setAttribute(VALUE, value ? "1" : "0");
		    inputboolean.setAttribute(ENABLED, enabled ? YES : NO);
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			inputboolean.appendChild(child);
		    }
		    root.appendChild(inputboolean);
		}
		public String getType() { return "inputboolean"; }
	    };
	case 8: // input string
	    return new VTAbstObj() {
		int width;
		int height;
		int bgcol;
		int fontattrib;
		int inputattrib;
		int options;
		int varref;
		int horizontaljust;
		int length;
		String value;
		boolean enabled;
		List<Integer> macros;
		public void read(ByteReader br) {
		    width = br.readWord();
		    height = br.readWord();
		    bgcol = br.readColor();
		    fontattrib = br.readRef();
		    inputattrib = br.readRef();
		    options = br.readByte();
		    varref = br.readRef();
		    horizontaljust = br.readByte();
		    if (varref == 0xFFFF) {
			length = br.readByte();
			value = br.readString(length);
		    }
		    else {
			length = br.readByte();
		    }
		    enabled = br.readByte() != 0;
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<inputstring name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element inputstring = doc.createElement(INPUTSTRING);
		    inputstring.setAttribute(NAME, map.get(id));
		    inputstring.setAttribute(WIDTH, Integer.toString(width));
		    inputstring.setAttribute(HEIGHT, Integer.toString(height));
		    inputstring.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
		    if (map.containsKey(fontattrib)) {
			Element fontattr = doc.createElement(INCLUDE_OBJECT);
			fontattr.setAttribute(NAME, map.get(fontattrib));
			fontattr.setAttribute(ROLE, FONT_ATTRIBUTES);
			inputstring.appendChild(fontattr);
		    }
		    if (map.containsKey(inputattrib)) {
			Element inputattr = doc.createElement(INCLUDE_OBJECT);
			inputattr.setAttribute(NAME, map.get(inputattrib));
			inputattr.setAttribute(ROLE, INPUT_ATTRIBUTES);
			inputstring.appendChild(inputattr);
		    }
		    inputstring.setAttribute(OPTIONS, getStringOptions(options));
		    if (map.containsKey(varref)) {
			Element variableref = doc.createElement(INCLUDE_OBJECT);
			variableref.setAttribute(NAME, map.get(varref));
			variableref.setAttribute(ROLE, VARIABLE_REFERENCE);
			inputstring.appendChild(variableref);
		    }
		    else {
			inputstring.setAttribute(VALUE, value);
		    }
		    inputstring.setAttribute(HORIZONTAL_JUSTIFICATION, getHorizontalJustification(horizontaljust));
		    inputstring.setAttribute(LENGTH, Integer.toString(length));
		    inputstring.setAttribute(ENABLED, Boolean.toString(enabled)); //enabled ? YES : NO);
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			inputstring.appendChild(child);
		    }
		    root.appendChild(inputstring);
		}
		public String getType() { return "inputstring"; }
	    };
	case 9: // input number
	    return new VTAbstObj() {
		int width;
		int height;
		int bgcol;
		int fontattrib;
		int options;
		int varref;
		int value;
		int minvalue;
		int maxvalue;
		int offset;
		float scale;
		int nroDecimals;
		int format;
		int horizontaljust;
		boolean enabled;
		List<Integer> macros;
		public void read(ByteReader br) {
		    width = br.readWord();
		    height = br.readWord();
		    bgcol = br.readColor();
		    fontattrib = br.readRef();
		    options = br.readByte();
		    varref = br.readRef();
		    value = br.readDWord();
		    minvalue = br.readDWord();
		    maxvalue = br.readDWord();
		    offset = br.readDWord();
		    scale = br.readFloat();
		    nroDecimals = br.readByte();
		    format = br.readByte();
		    horizontaljust = br.readByte();
		    enabled = br.readByte() != 0;
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<inputnumber name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element inputnumber = doc.createElement(INPUTNUMBER);
		    inputnumber.setAttribute(NAME, map.get(id));
		    inputnumber.setAttribute(WIDTH, Integer.toString(width));
		    inputnumber.setAttribute(HEIGHT, Integer.toString(height));
		    inputnumber.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
		    if (map.containsKey(fontattrib)) {
			Element fontattr = doc.createElement(INCLUDE_OBJECT);
			fontattr.setAttribute(NAME, map.get(fontattrib));
			fontattr.setAttribute(ROLE, FONT_ATTRIBUTES);
			inputnumber.appendChild(fontattr);
		    }
		    inputnumber.setAttribute(OPTIONS, getNumberOptions(options));
		    if (map.containsKey(varref)) {
			Element variableref = doc.createElement(INCLUDE_OBJECT);
			variableref.setAttribute(NAME, map.get(varref));
			variableref.setAttribute(ROLE, VARIABLE_REFERENCE);
			inputnumber.appendChild(variableref);
		    }
		    else {
			inputnumber.setAttribute(VALUE, Integer.toString(value));
		    }
		    inputnumber.setAttribute(MIN_VALUE, Integer.toString(minvalue));
		    inputnumber.setAttribute(MAX_VALUE, Integer.toString(maxvalue));
		    inputnumber.setAttribute(OFFSET, Integer.toString(offset));
		    inputnumber.setAttribute(SCALE, getScale(scale));
		    inputnumber.setAttribute(NUMBER_OF_DECIMALS, Integer.toString(nroDecimals));
		    inputnumber.setAttribute(FORMAT, getNumberFormat(format));
		    inputnumber.setAttribute(HORIZONTAL_JUSTIFICATION, getHorizontalJustification(horizontaljust));
		    inputnumber.setAttribute(ENABLED, enabled ? YES : NO);
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			inputnumber.appendChild(child);
		    }
		    root.appendChild(inputnumber);
		}
		public String getType() { return "inputnumber"; }
	    };
	case 10: // input list
	    return new VTAbstObj() {
		int width;
		int height;
		int varref;
		int value;
		boolean enabled;
		List<Integer> objects;
		List<Integer> macros;
		public void read(ByteReader br) {
		    width = br.readWord();
		    height = br.readWord();
		    varref = br.readRef();
		    value = br.readByte();
		    int nroObjects = br.readByte();
		    enabled = br.readByte() != 0;
		    int nroMacros = br.readByte();
		    objects = br.readRefs(nroObjects);
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<inputlist name=\"%s\">\n", name);
		    for (Integer oid : objects) {
			out.format("  <include_object name=\"%s\"/>\n",
				   map.get(oid));
		    }
		    out.format("</inputlist>\n");
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element inputlist = doc.createElement(INPUTLIST);
		    inputlist.setAttribute(NAME, map.get(id));
		    inputlist.setAttribute(WIDTH, Integer.toString(width));
		    inputlist.setAttribute(HEIGHT, Integer.toString(height));
		    if (map.containsKey(varref)) {
			Element variableref = doc.createElement(INCLUDE_OBJECT);
			variableref.setAttribute(NAME, map.get(varref));
			variableref.setAttribute(ROLE, VARIABLE_REFERENCE);
			inputlist.appendChild(variableref);
		    }
		    else {
			inputlist.setAttribute(VALUE, Integer.toString(value));
		    }
		    inputlist.setAttribute(ENABLED, enabled ? YES : NO);

		    for (Integer oid : objects) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(oid));
			inputlist.appendChild(child);
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			inputlist.appendChild(child);
		    }
		    root.appendChild(inputlist);
		}
		public String getType() { return "inputlist"; }
	    };
	case 11: // output string 
	    return new VTAbstObj() {
		int width;
		int height;
		int bgcol;
		int fontattrib;
		int options;
		int varref;
		int horizontaljust;
		int length;
		String value;
		List<Integer> macros;
		public void read(ByteReader br) {
		    width = br.readWord();
		    height = br.readWord();
		    bgcol = br.readColor();
		    fontattrib = br.readRef();
		    options = br.readByte();
		    varref = br.readRef();
		    horizontaljust = br.readByte();
		    if (varref == 0xFFFF) {
			length = br.readWord();
			value = br.readString(length);
		    }
		    else {
			length = br.readWord();
		    }
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);		    		    
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<outputstring name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element outputstring = doc.createElement(OUTPUTSTRING);
		    outputstring.setAttribute(NAME, map.get(id));
		    outputstring.setAttribute(WIDTH, Integer.toString(width));
		    outputstring.setAttribute(HEIGHT, Integer.toString(height));
		    outputstring.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
		    if (map.containsKey(fontattrib)) {
			Element fontattr = doc.createElement(INCLUDE_OBJECT);
			fontattr.setAttribute(NAME, map.get(fontattrib));
			fontattr.setAttribute(ROLE, FONT_ATTRIBUTES);
			outputstring.appendChild(fontattr);
		    }
		    outputstring.setAttribute(OPTIONS, getStringOptions(options));
		    if (map.containsKey(varref)) {
			Element variableref = doc.createElement(INCLUDE_OBJECT);
			variableref.setAttribute(NAME, map.get(varref));
			variableref.setAttribute(ROLE, VARIABLE_REFERENCE);
			outputstring.appendChild(variableref);
		    }
		    else {
			outputstring.setAttribute(VALUE, value);
		    }
		    outputstring.setAttribute(HORIZONTAL_JUSTIFICATION, getHorizontalJustification(horizontaljust));
		    outputstring.setAttribute(LENGTH, Integer.toString(length));
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			outputstring.appendChild(child);
		    }
		    root.appendChild(outputstring);
		}
		public String getType() { return "outputstring"; }
	    };
	case 12: // output number
	    return new VTAbstObj() {
		int width;
		int height;
		int bgcol;
		int fontattrib;
		int options;
		int varref;
		int value;
		int offset;
		float scale;
		int nroDecimals;
		int format;
		int horizontaljust;
		List<Integer> macros;
		public void read(ByteReader br) {
		    width = br.readWord();
		    height = br.readWord();
		    bgcol = br.readColor();
		    fontattrib = br.readRef();
		    options = br.readByte();
		    varref = br.readRef();
		    value = br.readDWord();
		    offset = br.readDWord();
		    scale = br.readFloat();
		    nroDecimals = br.readByte();
		    format = br.readByte();
		    horizontaljust = br.readByte();
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<outputnumber name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element outputnumber = doc.createElement(OUTPUTNUMBER);
		    outputnumber.setAttribute(NAME, map.get(id));
		    outputnumber.setAttribute(WIDTH, Integer.toString(width));
		    outputnumber.setAttribute(HEIGHT, Integer.toString(height));
		    outputnumber.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
		    if (map.containsKey(fontattrib)) {
			Element fontattr = doc.createElement(INCLUDE_OBJECT);
			fontattr.setAttribute(NAME, map.get(fontattrib));
			fontattr.setAttribute(ROLE, FONT_ATTRIBUTES);
			outputnumber.appendChild(fontattr);
		    }
		    outputnumber.setAttribute(OPTIONS, getNumberOptions(options));
		    if (map.containsKey(varref)) {
			Element variableref = doc.createElement(INCLUDE_OBJECT);
			variableref.setAttribute(NAME, map.get(varref));
			variableref.setAttribute(ROLE, VARIABLE_REFERENCE);
			outputnumber.appendChild(variableref);
		    }
		    else {
			outputnumber.setAttribute(VALUE, Integer.toString(value));
		    }
		    outputnumber.setAttribute(OFFSET, Integer.toString(offset));
		    outputnumber.setAttribute(SCALE, getScale(scale));
		    outputnumber.setAttribute(NUMBER_OF_DECIMALS, Integer.toString(nroDecimals));
		    outputnumber.setAttribute(FORMAT, getNumberFormat(format));
		    outputnumber.setAttribute(HORIZONTAL_JUSTIFICATION, getHorizontalJustification(horizontaljust));
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			outputnumber.appendChild(child);
		    }
		    root.appendChild(outputnumber);
		}
		public String getType() { return "outputnumber"; }
	    };
	case 13: // line
	    return new VTAbstObj() {
		int lineattrib;
		int width;
		int height;
		int linedir;
		List<Integer> macros;
		public void read(ByteReader br) {
		    lineattrib = br.readRef();
		    width = br.readWord();
		    height = br.readWord();
		    linedir = br.readByte();
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<line name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element line = doc.createElement(LINE);
		    line.setAttribute(NAME, map.get(id));
		    if (map.containsKey(lineattrib)) {
			Element lineattr = doc.createElement(INCLUDE_OBJECT);
			lineattr.setAttribute(NAME, map.get(lineattrib));
			lineattr.setAttribute(ROLE, LINE_ATTRIBUTES);
			line.appendChild(lineattr);
		    }
		    line.setAttribute(WIDTH, Integer.toString(width));
		    line.setAttribute(HEIGHT, Integer.toString(height));
		    line.setAttribute(LINE_DIRECTION, getLineDirection(linedir));
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			line.appendChild(child);
		    }
		    root.appendChild(line);
		}
		public String getType() { return "line"; }
	    };
	case 14: // rectangle
	    return new VTAbstObj() {
		int lineattrib;
		int width;
		int height;
		int linesupp;
		int fillattrib;
		List<Integer> macros;
		public void read(ByteReader br) {
		    lineattrib = br.readRef();
		    width = br.readWord();
		    height = br.readWord();
		    linesupp = br.readByte();
		    fillattrib = br.readRef();
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<rectangle name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element rectangle = doc.createElement(RECTANGLE);
		    rectangle.setAttribute(NAME, map.get(id));
		    if (map.containsKey(lineattrib)) {
			Element lineattr = doc.createElement(INCLUDE_OBJECT);
			lineattr.setAttribute(NAME, map.get(lineattrib));
			lineattr.setAttribute(ROLE, LINE_ATTRIBUTES);
			rectangle.appendChild(lineattr);
		    }
		    rectangle.setAttribute(WIDTH, Integer.toString(width));
		    rectangle.setAttribute(HEIGHT, Integer.toString(height));
		    rectangle.setAttribute(LINE_SUPPRESSION, Integer.toString(linesupp));
		    if (map.containsKey(fillattrib)) {
			Element fillattr = doc.createElement(INCLUDE_OBJECT);
			fillattr.setAttribute(NAME, map.get(fillattrib));
			fillattr.setAttribute(ROLE, FILL_ATTRIBUTES);
			rectangle.appendChild(fillattr);
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			rectangle.appendChild(child);
		    }
		    root.appendChild(rectangle);
		}
		public String getType() { return "rectagle"; }
	    };
	case 15: // ellipse
	    return new VTAbstObj() {
		int lineattrib;
		int width;
		int height;
		int ellipsetype;
		int startangle;
		int endangle;
		int fillattrib;		
		List<Integer> macros;
		public void read(ByteReader br) {
		    lineattrib = br.readRef();
		    width = br.readWord();
		    height = br.readWord();
		    ellipsetype = br.readByte();
		    startangle = 2 * br.readByte(); // unit conversion from
		    endangle = 2 * br.readByte();   // 0-180 to 0-360
		    fillattrib = br.readRef();
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<ellipse name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element ellipse = doc.createElement(ELLIPSE);
		    ellipse.setAttribute(NAME, map.get(id));
		    if (map.containsKey(lineattrib)) {
			Element lineattr = doc.createElement(INCLUDE_OBJECT);
			lineattr.setAttribute(NAME, map.get(lineattrib));
			lineattr.setAttribute(ROLE, LINE_ATTRIBUTES);
			ellipse.appendChild(lineattr);
		    }
		    ellipse.setAttribute(WIDTH, Integer.toString(width));
		    ellipse.setAttribute(HEIGHT, Integer.toString(height));
		    ellipse.setAttribute(ELLIPSE_TYPE, getEllipseType(ellipsetype));
		    ellipse.setAttribute(START_ANGLE, Integer.toString(startangle));
		    ellipse.setAttribute(END_ANGLE, Integer.toString(endangle));
		    if (map.containsKey(fillattrib)) {
			Element fillattr = doc.createElement(INCLUDE_OBJECT);
			fillattr.setAttribute(NAME, map.get(fillattrib));
			fillattr.setAttribute(ROLE, FILL_ATTRIBUTES);
			ellipse.appendChild(fillattr);
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			ellipse.appendChild(child);
		    }
		    root.appendChild(ellipse);
		}
		public String getType() { return "ellipse"; }
	    };
	case 16: // polygon
	    return new VTAbstObj() {
		int width;
		int height;
		int lineattrib;
		int fillattrib;
		int polygontype;
		List<PointXY> points;
		List<Integer> macros;
		public void read(ByteReader br) {
		    width = br.readWord();
		    height = br.readWord();
		    lineattrib = br.readRef();
		    fillattrib = br.readRef();
		    polygontype = br.readByte();
		    int nroPoints = br.readByte();
		    int nroMacros = br.readByte();
		    points = br.readPoints(nroPoints);
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<polygon name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element polygon = doc.createElement(POLYGON);
		    polygon.setAttribute(NAME, map.get(id));
		    polygon.setAttribute(WIDTH, Integer.toString(width));
		    polygon.setAttribute(HEIGHT, Integer.toString(height));
		    if (map.containsKey(lineattrib)) {
			Element lineattr = doc.createElement(INCLUDE_OBJECT);
			lineattr.setAttribute(NAME, map.get(lineattrib));
			lineattr.setAttribute(ROLE, LINE_ATTRIBUTES);
			polygon.appendChild(lineattr);
		    }
		    if (map.containsKey(fillattrib)) {
			Element fillattr = doc.createElement(INCLUDE_OBJECT);
			fillattr.setAttribute(NAME, map.get(fillattrib));
			fillattr.setAttribute(ROLE, FILL_ATTRIBUTES);
			polygon.appendChild(fillattr);
		    }
		    polygon.setAttribute(POLYGON_TYPE, getPolygonType(polygontype));
                    for (PointXY pnt : points) {
                        Element child = doc.createElement(POINT_OBJECT);
			child.setAttribute(POS_X, Integer.toString(pnt.x));
                        child.setAttribute(POS_Y, Integer.toString(pnt.y));
			polygon.appendChild(child);
                    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			polygon.appendChild(child);
		    }
		    root.appendChild(polygon);
		}
		public String getType() { return "polygon"; }
	    };
	case 17: // meter
	    return new VTAbstObj() {
		int width;
		int needlecol;
		int bordercol;
		int tickcol;
		int options;
		int nroTicks;
		int startangle;
		int endangle;
		int minvalue;
		int maxvalue;
		int varref;
		int value;
		List<Integer> macros;
		public void read(ByteReader br) {
		    width = br.readWord();
		    needlecol = br.readColor();
		    bordercol = br.readColor();
		    tickcol = br.readColor();
		    options = br.readByte();
		    nroTicks = br.readByte();
		    startangle = 2 * br.readByte(); // unit conversion from
		    endangle = 2 * br.readByte();   // 0-180 to 0-360
		    minvalue = br.readWord();
		    maxvalue = br.readWord();
		    varref = br.readRef();
		    value = br.readWord();
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<meter name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element meter = doc.createElement(METER);
		    meter.setAttribute(NAME, map.get(id));
		    meter.setAttribute(WIDTH, Integer.toString(width));
		    meter.setAttribute(NEEDLE_COLOUR, getColor(needlecol));
		    meter.setAttribute(BORDER_COLOUR, getColor(bordercol));
		    meter.setAttribute(ARC_AND_TICK_COLOUR, getColor(tickcol));
		    meter.setAttribute(OPTIONS, getMeterOptions(options));
		    meter.setAttribute(NUMBER_OF_TICKS, Integer.toString(nroTicks));
		    meter.setAttribute(START_ANGLE, Integer.toString(startangle));
		    meter.setAttribute(END_ANGLE, Integer.toString(endangle));
		    meter.setAttribute(MIN_VALUE, Integer.toString(minvalue));
		    meter.setAttribute(MAX_VALUE, Integer.toString(maxvalue));
		    if (map.containsKey(varref)) {
			Element variableref = doc.createElement(INCLUDE_OBJECT);
			variableref.setAttribute(NAME, map.get(varref));
			variableref.setAttribute(ROLE, VARIABLE_REFERENCE);
			meter.appendChild(variableref);
		    }
		    else {
			meter.setAttribute(VALUE, Integer.toString(value));
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			meter.appendChild(child);
		    }
		    root.appendChild(meter);
		}
		public String getType() { return "meter"; }
	    };
	case 18: // linear bar graph
	    return new VTAbstObj() {
		int width;
		int height;
		int col;
		int targetcol;
		int options;
		int nroTicks;
		int minvalue;
		int maxvalue;
		int varref;
		int value;
		int targetvarref;
		int targetvalue;
		List<Integer> macros;
		public void read(ByteReader br) {
		    width = br.readWord();
		    height = br.readWord();
		    col = br.readColor();
		    targetcol = br.readColor();
		    options = br.readByte();
		    nroTicks = br.readByte();
		    minvalue = br.readWord();
		    maxvalue = br.readWord();
		    varref = br.readRef();
		    value = br.readWord();
		    targetvarref = br.readRef();
		    targetvalue = br.readWord();
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<lineabrargraph name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element lineabrargraph = doc.createElement(LINEARBARGRAPH);
		    lineabrargraph.setAttribute(NAME, map.get(id));
		    lineabrargraph.setAttribute(WIDTH, Integer.toString(width));
		    lineabrargraph.setAttribute(HEIGHT, Integer.toString(height));
		    lineabrargraph.setAttribute(COLOUR, getColor(col));		    
		    lineabrargraph.setAttribute(TARGET_LINE_COLOUR, getColor(targetcol));
		    lineabrargraph.setAttribute(OPTIONS, getLineabrarGraphOptions(options));
		    lineabrargraph.setAttribute(NUMBER_OF_TICKS, Integer.toString(nroTicks));
		    lineabrargraph.setAttribute(MIN_VALUE, Integer.toString(minvalue));
		    lineabrargraph.setAttribute(MAX_VALUE, Integer.toString(maxvalue));
		    if (map.containsKey(varref)) {
			Element variableref = doc.createElement(INCLUDE_OBJECT);
			variableref.setAttribute(NAME, map.get(varref));
			variableref.setAttribute(ROLE, VARIABLE_REFERENCE);
			lineabrargraph.appendChild(variableref);
		    }
		    else {
			lineabrargraph.setAttribute(VALUE, Integer.toString(value));
		    }
		    if (map.containsKey(targetvarref)) {
			Element variableref = doc.createElement(INCLUDE_OBJECT);
			variableref.setAttribute(NAME, map.get(targetvarref));
			variableref.setAttribute(ROLE, TARGET_VALUE_VARIABLE_REFERENCE);
			lineabrargraph.appendChild(variableref);
		    }
		    else {
			lineabrargraph.setAttribute(TARGET_VALUE, Integer.toString(targetvalue));
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			lineabrargraph.appendChild(child);
		    }
		    root.appendChild(lineabrargraph);
		}
		public String getType() { return "lineabrargraph"; }
	    };
	case 19: // arched bar graph
	    return new VTAbstObj() {
		int width;
		int height;
		int col;
		int targetcol;
		int options;
		int startangle;
		int endangle;
		int barwidth;
		int minvalue;
		int maxvalue;
		int varref;
		int value;
		int targetvarref;
		int targetvalue;
		List<Integer> macros;
		public void read(ByteReader br) {
		    width = br.readWord();
		    height = br.readWord();
		    col = br.readColor();
		    targetcol = br.readColor();
		    options = br.readByte();
		    startangle = 2 * br.readByte(); // unit conversion from
		    endangle = 2 * br.readByte();   // 0-180 to 0-360
		    barwidth = br.readWord();
		    minvalue = br.readWord();
		    maxvalue = br.readWord();
		    varref = br.readRef();
		    value = br.readWord();
		    targetvarref = br.readRef();
		    targetvalue = br.readWord();
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<archedbargraph name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element archedbargraph = doc.createElement(ARCHEDBARGRAPH);
		    archedbargraph.setAttribute(NAME, map.get(id));
		    archedbargraph.setAttribute(WIDTH, Integer.toString(width));
		    archedbargraph.setAttribute(HEIGHT, Integer.toString(height));
		    archedbargraph.setAttribute(COLOUR, getColor(col));
		    archedbargraph.setAttribute(TARGET_LINE_COLOUR, getColor(targetcol));
		    archedbargraph.setAttribute(OPTIONS, getArchedBarGraphOptions(options));
		    archedbargraph.setAttribute(START_ANGLE, Integer.toString(startangle));
		    archedbargraph.setAttribute(END_ANGLE, Integer.toString(endangle));
		    archedbargraph.setAttribute(BAR_GRAPH_WIDTH, Integer.toString(barwidth));
		    archedbargraph.setAttribute(MIN_VALUE, Integer.toString(minvalue));
		    archedbargraph.setAttribute(MAX_VALUE, Integer.toString(maxvalue));
		    if (map.containsKey(varref)) {
			Element variableref = doc.createElement(INCLUDE_OBJECT);
			variableref.setAttribute(NAME, map.get(varref));
			variableref.setAttribute(ROLE, VARIABLE_REFERENCE);
			archedbargraph.appendChild(variableref);
		    }
		    else {
			archedbargraph.setAttribute(VALUE, Integer.toString(value));
		    }
		    if (map.containsKey(targetvarref)) {
			Element variableref = doc.createElement(INCLUDE_OBJECT);
			variableref.setAttribute(NAME, map.get(targetvarref));
			variableref.setAttribute(ROLE, TARGET_VALUE_VARIABLE_REFERENCE);
			archedbargraph.appendChild(variableref);
		    }
		    else {
			archedbargraph.setAttribute(TARGET_VALUE, Integer.toString(targetvalue));
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			archedbargraph.appendChild(child);
		    }
		    root.appendChild(archedbargraph);
		}
		public String getType() { return "archedbargraph"; }
	    };
	case 20: // picture graphic
	    return new VTAbstObj() {
		int width;
		int actualwidth;
		int actualheight;
		int format;
		int options;
		int transparentcol;
		byte[] data;
		List<Integer> macros;
		public void read(ByteReader br) {
		    width = br.readWord();
		    actualwidth = br.readWord();
		    actualheight = br.readWord();
		    format = br.readByte();
		    options = br.readByte();
		    transparentcol = br.readByte();
		    int nroDataBytes = br.readDWord();
		    int nroMacros = br.readByte();
		    data = br.readByteArray(nroDataBytes);
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<picturegraphic name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element picturegraphic = doc.createElement(PICTUREGRAPHIC);
		    picturegraphic.setAttribute(NAME, map.get(id));
		    picturegraphic.setAttribute(WIDTH, Integer.toString(width));
		    //picturegraphic.setAttribute(ACTUAL_WIDTH, Integer.toString(actualwidth));
		    //picturegraphic.setAttribute(ACTUAL_HEIGHT, Integer.toString(actualheight));
		    picturegraphic.setAttribute(FORMAT, getPictureFormat(format));
		    picturegraphic.setAttribute(OPTIONS, getPictureGraphicOptions(options));
		    picturegraphic.setAttribute(TRANSPARENCY_COLOUR, getColor(transparentcol));

		    createImageFile(name, format, options, transparentcol, actualwidth, actualheight, data);
		    picturegraphic.setAttribute(FILE, name + ".png");
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			picturegraphic.appendChild(child);
		    }
		    root.appendChild(picturegraphic);
		}
		public String getType() { return "picturegraphic"; }
	    };
	case 21: // number variable
	    return new VTAbstObj() {
		int value;
		public void read(ByteReader br) {
		    value = br.readDWord();
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<numbervariable name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element numbervariable = doc.createElement(NUMBERVARIABLE);
		    numbervariable.setAttribute(NAME, map.get(id));
		    numbervariable.setAttribute(VALUE, Integer.toString(value));
		    root.appendChild(numbervariable);
		}
		public String getType() { return "numbervariable"; }
	    };
	case 22: // string variable
	    return new VTAbstObj() {
		int length;
		String value;
		public void read(ByteReader br) {
		    length = br.readWord();
		    value = br.readString(length);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<stringvariable name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element stringvariable = doc.createElement(STRINGVARIABLE);
		    stringvariable.setAttribute(NAME, map.get(id));
		    stringvariable.setAttribute(LENGTH, Integer.toString(length));
		    stringvariable.setAttribute(VALUE, value);
		    root.appendChild(stringvariable);
		}
		public String getType() { return "stringvariable";}
	    };
	case 23: // font attributes
	    return new VTAbstObj() {
		int fontcol;
		int fontsize;
		int fonttype;
		int fontstyle;
		List<Integer> macros;
		public void read(ByteReader br) {
		    fontcol = br.readColor();
		    fontsize = br.readByte();
		    fonttype = br.readByte();
		    fontstyle = br.readByte();
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<fontattributes name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element fontattributes = doc.createElement(FONTATTRIBUTES);
		    fontattributes.setAttribute(NAME, map.get(id));
		    fontattributes.setAttribute(FONT_COLOUR, getColor(fontcol));
		    fontattributes.setAttribute(FONT_SIZE, getFontSize(fontsize));
		    fontattributes.setAttribute(FONT_TYPE, getFontType(fonttype));
		    fontattributes.setAttribute(FONT_STYLE, getFontStyle(fontstyle));
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			fontattributes.appendChild(child);
		    }
		    root.appendChild(fontattributes);
		}
		public String getType() { return "fontattributes"; }
	    };
	case 24: // line attributes
	    return new VTAbstObj() {
		int linecol;
		int linewidth;
		int lineart;
		List<Integer> macros;
		public void read(ByteReader br) {
		     linecol = br.readColor();
		     linewidth = br.readByte();
		     lineart = br.readWord();
		     int nroMacros = br.readByte();
		     macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<lineattributes name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element lineattributes = doc.createElement(LINEATTRIBUTES);
		    lineattributes.setAttribute(NAME, map.get(id));
		    lineattributes.setAttribute(LINE_COLOUR, getColor(linecol));
		    lineattributes.setAttribute(LINE_WIDTH, Integer.toString(linewidth));
		    lineattributes.setAttribute(LINE_ART, getLineArt(lineart));
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			lineattributes.appendChild(child);
		    }
		    root.appendChild(lineattributes);
		}
		public String getType() { return "lineattributes"; }
	    };
	case 25: // fill attributes
	    return new VTAbstObj() {
		int filltype;
		int fillcol;
		int fillpatternref;
		List<Integer> macros;
		public void read(ByteReader br) {
		    filltype = br.readByte();
		    fillcol = br.readColor();
		    fillpatternref = br.readRef();
		    int nroMacros = br.readByte();
		    macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<fillattributes name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element fillattributes = doc.createElement(FILLATTRIBUTES);
		    fillattributes.setAttribute(NAME, map.get(id));
		    fillattributes.setAttribute(FILL_TYPE, getFillType(filltype));
		    fillattributes.setAttribute(FILL_COLOUR, getColor(fillcol));
		    if (map.containsKey(fillpatternref)) {
			Element fillpattr = doc.createElement(INCLUDE_OBJECT);
			fillpattr.setAttribute(NAME, map.get(fillpatternref));
			fillpattr.setAttribute(ROLE, FILL_PATTERN);
			fillattributes.appendChild(fillpattr);
		    }
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			fillattributes.appendChild(child);
		    }
		    root.appendChild(fillattributes);
		}
		public String getType() { return "fillattributes"; }
	    };
	case 26: // input attributes
	    return new VTAbstObj() {
		int validationtype;
		int length;
		String validationstring;
		List<Integer> macros;
		public void read(ByteReader br) {
		     validationtype = br.readByte();
		     length = br.readByte();
		     validationstring = br.readString(length);
		     int nroMacros = br.readByte();
		     macros = br.readBytes(nroMacros);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<inputattributes name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element inputattributes = doc.createElement(INPUTATTRIBUTES);
		    inputattributes.setAttribute(NAME, map.get(id));
		    inputattributes.setAttribute(VALIDATION_TYPE, getValidationType(validationtype));
		    inputattributes.setAttribute(LENGTH, Integer.toString(length));
		    inputattributes.setAttribute(VALIDATION_STRING, validationstring);
		    for (Integer mac : macros) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(mac));
			inputattributes.appendChild(child);
		    }
		    root.appendChild(inputattributes);
		}
		public String getType() { return "inputattributes"; }
	    };
	case 27: // object pointer
	    return new VTAbstObj() {
		int varref;
		public void read(ByteReader br) {
		    varref = br.readRef();
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<objectpointer name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element objectpointer = doc.createElement(OBJECTPOINTER);
		    objectpointer.setAttribute(NAME, map.get(id));
		    if (map.containsKey(varref)) {
			Element val = doc.createElement(INCLUDE_OBJECT);
			val.setAttribute(NAME, map.get(varref));
			val.setAttribute(ROLE, VALUE);
			objectpointer.appendChild(val);
		    }
		    root.appendChild(objectpointer);
		}
		public String getType() { return "objectpointer"; }
	    };
	case 28: // macro
	    return new VTAbstObj() {
		List<VTObject> commands;
		public void read(ByteReader br) {
		    int nroBytes = br.readWord();
		    commands = readCommands(br, nroBytes);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<macro name=\"%s\">\n", name);
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element macro = doc.createElement(MACRO);
		    macro.setAttribute(NAME, map.get(id));
		    for (VTObject vto : commands) {
			vto.appendDoc(map, doc);
		    }
		    root.appendChild(macro);
		}
		public String getType() { return "macro"; }
	    };
	case 29: // aux function
	    return new VTAbstObj() {
		int bgcol;
		int funtype;
		List<RefXY> objects;
		public void read(ByteReader br) {
		    bgcol = br.readColor();
		    funtype = br.readByte();
		    int nroObjects = br.readByte();
		    objects = br.readRefXYs(nroObjects);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<auxiliaryfunction name=\"%s\">\n", name);
		    for (RefXY ref : objects) {
			out.format("  <include_object name=\"%s\" pos_x=\"%d\" pos_y=\"%d\"/>\n",
				   map.get(ref.id), ref.x, ref.y);
		    }
		    out.format("</auxiliaryfunction>\n");
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element auxiliaryfunction = doc.createElement(AUXILIARYFUNCTION);
		    auxiliaryfunction.setAttribute(NAME, map.get(id));
		    auxiliaryfunction.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
		    auxiliaryfunction.setAttribute(FUNCTION_TYPE, getFunctionType(funtype));
		    for (RefXY ref : objects) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(ref.id));
			child.setAttribute(POS_X, Integer.toString(ref.x));
			child.setAttribute(POS_Y, Integer.toString(ref.y));
			auxiliaryfunction.appendChild(child);
		    }
		    root.appendChild(auxiliaryfunction);
		}
		public String getType() { return "auxiliaryfunction"; }
	    };
	case 30: // aux input
	    return new VTAbstObj() {
		int bgcol;
		int funtype;
		int inputid;
		List<RefXY> objects;
		public void read(ByteReader br) {
		    bgcol = br.readColor();
		    funtype = br.readByte();
		    inputid = br.readByte();
		    int nroObjects = br.readByte();
		    objects = br.readRefXYs(nroObjects);
		}
		public void emitXML(Map<Integer, String> map, PrintStream out) {
		    out.format("<auxiliaryinput name=\"%s\">\n", name);
		    for (RefXY ref : objects) {
			out.format("  <include_object name=\"%s\" pos_x=\"%d\" pos_y=\"%d\"/>\n",
				   map.get(ref.id), ref.x, ref.y);
		    }
		    out.format("</auxiliaryinput>\n");
		}
		public void appendDoc(Map<Integer, String> map, Document doc) {
		    Element root = doc.getDocumentElement();
		    Element auxiliaryinput = doc.createElement(AUXILIARYINPUT);
		    auxiliaryinput.setAttribute(NAME, map.get(id));
		    auxiliaryinput.setAttribute(BACKGROUND_COLOUR, getColor(bgcol));
		    auxiliaryinput.setAttribute(FUNCTION_TYPE, getFunctionType(funtype));
		    auxiliaryinput.setAttribute(INPUT_ID, Integer.toString(inputid));
		    for (RefXY ref : objects) {
			Element child = doc.createElement(INCLUDE_OBJECT);
			child.setAttribute(NAME, map.get(ref.id));
			child.setAttribute(POS_X, Integer.toString(ref.x));
			child.setAttribute(POS_Y, Integer.toString(ref.y));
			auxiliaryinput.appendChild(child);
		    }
		    root.appendChild(auxiliaryinput);
		}
		public String getType() { return "auxiliaryinput"; }
	    };
	default:
	    throw new RuntimeException("unknown type encountered: " + type);
	}
    }

    /**
     *
     * @param name
     * @param type
     * @param set
     * @return
     */
    public String createUniqueName(String name, String type, Set<String> set) {
        if (!name.isEmpty() && !set.contains(name))
	    return name;

        int i = name.length() - 1;
	while (i >= 0 && Character.isDigit((name.charAt(i)))) i--;
        String basename = name.substring(0, i + 1);
	String digits = name.substring(i + 1);
	if (basename.isEmpty()) {
	    basename = type;
	}
	int n = digits.isEmpty() ? 0 : Integer.parseInt(digits);

	//go through names until free name is found
	String result;
        while (set.contains(result = basename + n)) n++;
        return result;
    }

    /**
     * FIXME: 
     * - transparency is not implemented
     * - RLE compression is not implemented
     * @param name
     * @param format
     * @param options
     * @param transparentcol
     * @param width
     * @param height
     * @param data
     */
    public void createImageFile(
            String name, int format, int options, int transparentcol,
            int width, int height, byte[] data)
    {
	System.out.format("name: %s, form: %d, opt: %d, transp: %d, w: %d, h: %d, dlen: %d\n", 
			  name, format, options, transparentcol, width, height, data.length);
        switch (format) {
            case 0: {
                // 1-bit color
                final int BYTE_WIDTH = (width + 7) / 8;
                BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int v = (data[x / 8 + y * BYTE_WIDTH] << (x % 8)) & 0x80;
                        im.setRGB(x, y, v == 0x00 ? 0x00000000 : 0x00FFFFFF);
                    }
                }
                String fullname = FileTools.joinPaths(stdBitmapPath, name + ".png");
                File outfile = new File(fullname);
                try {
                    ImageIO.write(im, "png", outfile);
                }
                catch (IOException e) {
                    e.printStackTrace(System.err);
                }
                break;
            }
            case 1: {
                // 4-bit color
                final int BYTE_WIDTH = (width + 1) / 2;
                BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int idx = data[x / 2 + y * BYTE_WIDTH];
                        if ((x % 2) == 0)
                            idx = (idx >> 4) & 0x0F;
                        else
                            idx &= 0x0F;

                        im.setRGB(x, y, VALUES_8BIT[idx]); // the first values are the same for both 4 and 8 bits
                    }
                }
                String fullname = FileTools.joinPaths(stdBitmapPath, name + ".png");
                File outfile = new File(fullname);
                try {
                    ImageIO.write(im, "png", outfile);
                }
                catch (IOException e) {
                    e.printStackTrace(System.err);
                }
                break;
            }
            case 2: {
                // 8-bit color
                BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int idx = data[x + y * width] & 0xFF;
                        if (idx >= VALUES_8BIT.length)
                            idx = VALUES_8BIT.length - 1;
                        im.setRGB(x, y, VALUES_8BIT[idx]);
                    }
                }
                String fullname = FileTools.joinPaths(stdBitmapPath, name + ".png");
                File outfile = new File(fullname);
                try {
                    ImageIO.write(im, "png", outfile);
                }
                catch (IOException e) {
                    e.printStackTrace(System.err);
                }
                break;
            }
            default:
                break;
        }
    }

    /**
     * Process file with InputStreamReader and JavaTerminalReader.
     * @param file
     * @param doc
     */
    public void processSavFile(File file, Document doc) {

	try {
	    BufferedReader input = // new BufferedReader(new FileReader(file));
		new BufferedReader(new InputStreamReader(new FileInputStream(file), "8859_1")); 

	    List<VTObject> list = new ArrayList<>();
	    Map<Integer, String> map = new HashMap<>();
	    Set<String> set = new HashSet<>();

	    String name, desc;
	    while ((name = input.readLine()) != null) {
		desc = input.readLine();
		Scanner sc = new Scanner(desc);
		sc.useDelimiter("[\\s(),]+");
		ByteReader br = new JavaTerminalReader(sc);
		while (sc.hasNext()) {
		    int id = br.readId();
		    VTObject vto = createVTO(br);
		    name = createUniqueName(name, vto.getType(), set);
		    set.add(name);
		    map.put(id, name);
		    vto.setName(name);
		    vto.setId(id);
		    vto.read(br);
		    list.add(vto);
		}
	    }
	    input.close();

	    for (VTObject vto : list) {
		vto.appendDoc(map, doc);
	    }
	}
	catch (IOException ex){
	    ex.printStackTrace();
	}
    }

    /**
     * Process file using BufferedInputStream and IOPReader.
     * @param file
     * @param doc
     */
    public void processIOPFile(File file, Document doc) {

	try {
	    BufferedInputStream input =
		new BufferedInputStream(new FileInputStream(file)); 

	    List<VTObject> list = new ArrayList<>();
	    Map<Integer, String> map = new HashMap<>();
	    Set<String> set = new HashSet<>();

	    ByteReader br = new IOPReader(input);
	    int b;
	    while ((b = input.read()) >= 0) {
		int id = b | (br.readByte() << 8);
		VTObject vto = createVTO(br);
		String name = createUniqueName("", vto.getType(), set);
		set.add(name);
		map.put(id, name);
		vto.setName(name);
		vto.setId(id);
		vto.read(br);
		list.add(vto);
	    }
	    input.close();

	    for (VTObject vto : list) {
		vto.appendDoc(map, doc);
	    }
	}
	catch (IOException ex){
	    ex.printStackTrace();
	}
    }

    public void processHeaderFile(File file, Document doc) {
        try {
	    BufferedReader input = // new BufferedReader(new FileReader(file));
		new BufferedReader(new InputStreamReader(new FileInputStream(file), "8859_1")); 

            List<String> objs = new ArrayList<>();
            Map<Integer, String> map1 = new HashMap<>();
            
	    List<VTObject> list = new ArrayList<>();
	    Map<Integer, String> map2 = new HashMap<>();
	    Set<String> set = new HashSet<>();
            
            String line;
            int mode = 0;
	    while ((line = input.readLine()) != null) {
		switch (mode) {
		    /* find start of the array */
                    case 0: {
                        if (line.equals("unsigned char *pool = {"))
                            mode = 1;
                        break;
                    }
		    /* collect objects until the end of the array */
                    case 1: {
                        if (line.equals("};")) {
                            mode = 2;
                            break;
                        }
                        objs.add(line);
                        break;
                    }
		    /* collect object names from the definitions */
                    case 2: {
                        if (line.startsWith("#define")) {
                            // e.g.: #define RearAdjAuxFun 642
                            Scanner sc = new Scanner(line);
                            sc.useDelimiter("[\\s]+");
                            String define = sc.next();
                            String name = sc.next();
                            int id = Integer.parseInt(sc.next());
                            map1.put(id, name);
                        }
                        break;
                    }
                } /* switch */
	    }
	    input.close();
            
            for (String obj : objs) {
                System.out.println(obj);
                Scanner sc = new Scanner(obj);
                sc.useDelimiter("[\\s,]+");
                CArrayReader br = new CArrayReader(sc);
                while (sc.hasNext()) {
                    int id = br.readId();
                    VTObject vto = createVTO(br);
                    String name = map1.get(id);
                    if (name == null)
                        name = ""; // not suppose to happen!
                    name = createUniqueName(name, vto.getType(), set);
                    set.add(name);
                    map2.put(id, name);
                    
                    vto.setName(name);
                    vto.setId(id);
                    vto.read(br);
                    list.add(vto);
                }
            }
	    for (VTObject vto : list) {
		vto.appendDoc(map2, doc);
	    }
	}
	catch (IOException ex){
	    ex.printStackTrace();
	}
    }
    
    /**
     * This used to be the main method of a separate program.
     */
    public void poolImport() {
    
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            /*
             * e.g.:
             * <objectpool dimension="200" fix_bitmap_path="images\"
             *   sk_height="32" sk_width="60" std_bitmap_path="images\">
             */
            Element root = doc.createElement(OBJECTPOOL);
            root.setAttribute(DIMENSION, Integer.toString(dimension));
            root.setAttribute(FIX_BITMAP_PATH, fixBitmapPath);
            root.setAttribute(STD_BITMAT_PATH, stdBitmapPath);
            root.setAttribute(SK_WIDTH, Integer.toString(sk_width));
            root.setAttribute(SK_HEIGHT, Integer.toString(sk_height));
            doc.appendChild(root);

            //Element child = doc.createElement("hello");
            //root.appendChild(child);
            if (inputFile.getName().endsWith(".sav")) {
                processSavFile(inputFile, doc);
            }
            else if (inputFile.getName().endsWith(".iop")) {
                processIOPFile(inputFile, doc);
            }
            else if (inputFile.getName().endsWith(".h")) {
                processHeaderFile(inputFile, doc);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", Integer.valueOf(2));
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //"ISO-8859-1");

            //initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);		
            String xmlString = result.getWriter().toString();
            //System.out.println(xmlString);
            try {
                // java.nio API java.lang API
                // ISO-8859-1 	ISO8859_1
                // UTF-8 	UTF8
                BufferedWriter out = new BufferedWriter
                    (new OutputStreamWriter
                     (new FileOutputStream(outputFile), "UTF-8")); //ISO8859_1"));
                out.write(xmlString);
                out.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace(System.err);
            }
            catch (IOException e2) {
                e2.printStackTrace(System.err);
            }
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace(System.err);
        }
        catch (TransformerConfigurationException e2) {
            e2.printStackTrace(System.err);
        } 
        catch (TransformerException e3) {
            e3.printStackTrace(System.err);
        }
    }
}
