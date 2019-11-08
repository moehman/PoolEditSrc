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
package pooledit;

/**
 *
 * @author mohman
 */
public class Definitions {

    // root
    static public final String OBJECTPOOL = "objectpool";

    // categories
    static public final String TOPLEVELOBJECTS_ = "TopLevelObjects";
    static public final String KEYOBJECTS_ = "KeyObjects";
    static public final String INPUTFIELDOBJECTS_ = "InputFieldObjects";
    static public final String OUTPUTFIELDOBJECTS_ = "OutputFieldObjects";
    static public final String SHAPEOBJECTS_ = "ShapeObjects";
    static public final String GRAPHICSOBJECTS_ = "GraphicsObjects";
    static public final String PICTUREOBJECTS_ = "PictureObjects";
    static public final String VARIABLEOBJECTS_ = "VariableObjects";
    static public final String ATTRIBUTESOBJECTS_ =  "AttributeObjects"; 
    static public final String POINTEROBJECTS_ = "PointerObjects";
    static public final String MACROOBJECTS_ = "MacroObjects";
    static public final String AUXILIARYOBJECTS_ = "AuxiliaryObjects";

    static public final String[] CATEGORIES = {
	TOPLEVELOBJECTS_, KEYOBJECTS_, INPUTFIELDOBJECTS_, OUTPUTFIELDOBJECTS_,
	SHAPEOBJECTS_, GRAPHICSOBJECTS_, PICTUREOBJECTS_, VARIABLEOBJECTS_,
	ATTRIBUTESOBJECTS_, POINTEROBJECTS_, MACROOBJECTS_, AUXILIARYOBJECTS_
    };

    // sub-categories
    static public final String WORKINGSETS_ = "WorkingSets";
    static public final String DATAMASKS_ = "DataMasks";
    static public final String ALARMMASKS_ = "AlarmMasks";
    static public final String CONTAINERS_ = "Containers";
    static public final String SOFTKEYMASKS_ = "SoftkeyMasks";
    static public final String KEYS_ = "Keys";
    static public final String BUTTONS_ = "Buttons";
    static public final String INPUTBOOLEANFIELDS_ = "InputBooleanFields";
    static public final String INPUTSTRINGFIELDS_ = "InputStringFields";
    static public final String INPUTNUMBERFIELDS_ = "InputNumberFields";
    static public final String INPUTLISTFIELDS_ = "InputListFields";
    static public final String OUTPUTSTRINGFIELDS_ = "OutputStringFields";
    static public final String OUTPUTNUMBERFIELDS_ = "OutputNumberFields";
    static public final String LINES_ = "Lines";
    static public final String RECTANGLES_ = "Rectangles";
    static public final String ELLIPSES_ = "Ellipses";
    static public final String POLYGONS_ = "Polygons";
    static public final String METERS_ = "Meters";
    static public final String LINEARBARGRAPHS_ = "LinearBarGraphs";
    static public final String ARCHEDBARGRAPHS_ = "ArchedBarGraphs";
    static public final String PICTURES_ = "Pictures";
    static public final String NUMBERVARIABLES_ = "NumberVariables";
    static public final String STRINGVARIABLES_ = "StringVariables";
    static public final String FONTATTRIBUTES_ = "FontAttributes";
    static public final String LINEATTRIBUTES_ = "LineAttributes";
    static public final String FILLATTRIBUTES_ = "FillAttributes";
    static public final String INPUTATTRIBUTES_ = "InputAttributes";
    static public final String OBJECTPOINTERS_ = "ObjectPointers";
    static public final String MACROS_ = "Macros";
    static public final String AUXILIARYFUNCTIONS_ = "AuxiliaryFunctions";
    static public final String AUXILIARYINPUTS_ = "AuxiliaryInputs";

    static public final String[][] SUBCATEGORYGROUPS = {
	{WORKINGSETS_, DATAMASKS_, ALARMMASKS_, CONTAINERS_}, 
	{SOFTKEYMASKS_, KEYS_, BUTTONS_}, 
	{INPUTBOOLEANFIELDS_, INPUTSTRINGFIELDS_, INPUTNUMBERFIELDS_, INPUTLISTFIELDS_}, 
	{OUTPUTSTRINGFIELDS_, OUTPUTNUMBERFIELDS_}, 
	{LINES_, RECTANGLES_, ELLIPSES_, POLYGONS_},
	{METERS_, LINEARBARGRAPHS_, ARCHEDBARGRAPHS_}, 
	{PICTURES_},
	{NUMBERVARIABLES_, STRINGVARIABLES_},
	{FONTATTRIBUTES_, LINEATTRIBUTES_, FILLATTRIBUTES_, INPUTATTRIBUTES_},
	{OBJECTPOINTERS_}, 
	{MACROS_}, 
	{AUXILIARYFUNCTIONS_, AUXILIARYINPUTS_}
    };

    static public final String[] SUBCATEGORIES = {
	WORKINGSETS_, DATAMASKS_, ALARMMASKS_, CONTAINERS_, 
	SOFTKEYMASKS_, KEYS_, BUTTONS_, 
	INPUTBOOLEANFIELDS_, INPUTSTRINGFIELDS_, INPUTNUMBERFIELDS_, INPUTLISTFIELDS_, 
	OUTPUTSTRINGFIELDS_, OUTPUTNUMBERFIELDS_, 
	LINES_, RECTANGLES_, ELLIPSES_, POLYGONS_,
	METERS_, LINEARBARGRAPHS_, ARCHEDBARGRAPHS_, 
	PICTURES_,
	NUMBERVARIABLES_, STRINGVARIABLES_,
	FONTATTRIBUTES_, LINEATTRIBUTES_, FILLATTRIBUTES_, INPUTATTRIBUTES_,
	OBJECTPOINTERS_, 
	MACROS_, 
	AUXILIARYFUNCTIONS_, AUXILIARYINPUTS_
    };

    // objects
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
    static public final String RECTANGLE = "rectangle";
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
    
    static public final String[] OBJECTS = {
	WORKINGSET, DATAMASK, ALARMMASK, CONTAINER, 
	SOFTKEYMASK, KEY, BUTTON, 
	INPUTBOOLEAN, INPUTSTRING, 
	INPUTNUMBER, INPUTLIST, 
	OUTPUTSTRING, OUTPUTNUMBER, 
	LINE, RECTANGLE, ELLIPSE, POLYGON,
	METER, LINEARBARGRAPH, ARCHEDBARGRAPH, PICTUREGRAPHIC,
	NUMBERVARIABLE, STRINGVARIABLE,
	FONTATTRIBUTES, LINEATTRIBUTES, FILLATTRIBUTES, INPUTATTRIBUTES,
	OBJECTPOINTER, MACRO, AUXILIARYFUNCTION, AUXILIARYINPUT
    };
  
    // pseudo objects
    static public final String POINT = "point";
    static public final String LANGUAGE = "language";
    static public final String COMMAND = "command";

    static public final String FIXEDBITMAP = "fixedbitmap";
    
    // links
    static public final String INCLUDE_OBJECT = "include_object";
    
    // tree elements
    static public final String[] TREE_ELEMENTS = {
	OBJECTPOOL,
	TOPLEVELOBJECTS_, KEYOBJECTS_, INPUTFIELDOBJECTS_, OUTPUTFIELDOBJECTS_,
	SHAPEOBJECTS_, GRAPHICSOBJECTS_, PICTUREOBJECTS_, VARIABLEOBJECTS_,
	ATTRIBUTESOBJECTS_, POINTEROBJECTS_, MACROOBJECTS_, AUXILIARYOBJECTS_,
	WORKINGSETS_, DATAMASKS_, ALARMMASKS_, CONTAINERS_, 
	SOFTKEYMASKS_, KEYS_, BUTTONS_, 
	INPUTBOOLEANFIELDS_, INPUTSTRINGFIELDS_, INPUTNUMBERFIELDS_, INPUTLISTFIELDS_, 
	OUTPUTSTRINGFIELDS_, OUTPUTNUMBERFIELDS_, 
	LINES_, RECTANGLES_, ELLIPSES_, POLYGONS_,
	METERS_, LINEARBARGRAPHS_, ARCHEDBARGRAPHS_, 
	PICTURES_,
	NUMBERVARIABLES_, STRINGVARIABLES_,
	FONTATTRIBUTES_, LINEATTRIBUTES_, FILLATTRIBUTES_, INPUTATTRIBUTES_,
	OBJECTPOINTERS_, 
	MACROS_, 
	AUXILIARYFUNCTIONS_, AUXILIARYINPUTS_,
	WORKINGSET, DATAMASK, ALARMMASK, CONTAINER, 
	SOFTKEYMASK, KEY, BUTTON, 
	INPUTBOOLEAN, INPUTSTRING, 
	INPUTNUMBER, INPUTLIST, 
	OUTPUTSTRING, OUTPUTNUMBER, 
	LINE, RECTANGLE, ELLIPSE, POLYGON,
	METER, LINEARBARGRAPH, ARCHEDBARGRAPH, PICTUREGRAPHIC,
	NUMBERVARIABLE, STRINGVARIABLE,
	FONTATTRIBUTES, LINEATTRIBUTES, FILLATTRIBUTES, INPUTATTRIBUTES,
	OBJECTPOINTER, MACRO, AUXILIARYFUNCTION, AUXILIARYINPUT,
	POINT, LANGUAGE, INCLUDE_OBJECT, FIXEDBITMAP, COMMAND
    };

    // static public final String TREE_ELEMENT_PATH = "images\\";

    static public final String TREE_ELEMENT_PATH = "/images/";
    
    static public final String[] TREE_ELEMENT_FILENAMES = {
	// --- root ---
	"objectpoolobjects.png",
	// --- categories ---
	"toplevelobjects.png", "keyobjects.png", "inputfieldobjects.png", "outputfieldobjects.png",
	"outputshapeobjects.png", "outputgraphicobjects.png", "picturegraphicobjects.png", "variableobjects.png",
	"attributeobjects.png", "pointerobjects.png", "macroobjects.png", "auxiliarycontrolobjects.png",
	// --- subcategories ---
	"workingset.png", "datamask.png", "alarmmask.png", "container.png", 
	"softkeymask.png", "softkey.png", "button.png",
	"inputbooleanfield.png", "inputstringfield.png", "inputnumberfield.png", "inputlistfield.png",
	"outputstringfield.png", "outputnumberfield.png", 
	"line.png", "rectangle.png", "ellipse.png", "polygon.png",
	"meter.png", "linearbargraph.png", "archedbargraph.png", 
	"picturegraphic.png", 
	"numbervariable.png", "stringvariable.png",
	"fontattribute.png", "lineattribute.png", "fillattribute.png", "inputattribute.png",
	"objectpointer.png",
	"macro.png",
	// --- objects ---
	"auxiliaryfunction.png", "auxiliaryinput.png",
	"workingset.png", "datamask.png", "alarmmask.png", "container.png", 
	"softkeymask.png", "softkey.png", "button.png",
	"inputbooleanfield.png", "inputstringfield.png", "inputnumberfield.png", "inputlistfield.png",
	"outputstringfield.png", "outputnumberfield.png", 
	"line.png", "rectangle.png", "ellipse.png", "polygon.png",
	"meter.png", "linearbargraph.png", "archedbargraph.png", 
	"picturegraphic.png", 
	"numbervariable.png", "stringvariable.png",
	"fontattribute.png", "lineattribute.png", "fillattribute.png", "inputattribute.png",
	"objectpointer.png",
	"macro.png",
	"auxiliaryfunction.png", "auxiliaryinput.png",
	// --- pseudo objects ---
	"point.png", "language.png", "includeobject.png", "fixedbitmap.png", "command.png"
    };

    // attributes
    static public final String ACTIVE_MASK = "active_mask";
    static public final String SOFT_KEY_MASK = "soft_key_mask";
    static public final String FONT_ATTRIBUTES = "font_attributes";
    static public final String FOREGROUND_COLOUR = "foreground_colour"; // this is the font attribute in input boolean
    static public final String INPUT_ATTRIBUTES = "input_attributes";
    static public final String VARIABLE_REFERENCE = "variable_reference";
    static public final String TARGET_VALUE_VARIABLE_REFERENCE = "target_value_variable_reference";
    static public final String LINE_ATTRIBUTES = "line_attributes";
    static public final String FILL_ATTRIBUTES = "fill_attributes";
    static public final String FILL_PATTERN = "fill_pattern";
    static public final String VALUE = "value";
    static public final String TARGET_VALUE = "target_value";
    
    static public final String NAME = "name";
    static public final String ID = "id";
    static public final String POS_X = "pos_x";
    static public final String POS_Y = "pos_y";
    static public final String FONT_SIZE = "font_size";
    static public final String NUMBER_OF_DECIMALS = "number_of_decimals";
    static public final String WIDTH = "width";
    static public final String HEIGHT = "height";
    static public final String LENGTH = "length";
    static public final String START_ANGLE = "start_angle";
    static public final String END_ANGLE = "end_angle";
    static public final String LINE_DIRECTION = "line_direction";
    static public final String DIMENSION = "dimension";
    static public final String SK_WIDTH = "sk_width";
    static public final String SK_HEIGHT = "sk_height";
    static public final String FIX_BITMAP_PATH = "fix_bitmap_path";
    static public final String STD_BITMAP_PATH = "std_bitmap_path";
    static public final String HORIZONTAL_JUSTIFICATION = "horizontal_justification";
    static public final String LINE_SUPPRESSION = "line_suppression";
    static public final String FILL_COLOUR = "fill_colour";
    static public final String COLOUR = "colour";
    static public final String FONT_COLOUR = "font_colour";
    static public final String BACKGROUND_COLOUR = "background_colour";
    static public final String LINE_COLOUR = "line_colour";
    static public final String TRANSPARENCY_COLOUR = "transparency_colour";
    static public final String LINE_WIDTH = "line_width";
    static public final String LINE_ART = "line_art";
    static public final String FILL_TYPE = "fill_type";
    static public final String FILE = "file";
    static public final String FILE1 = "file1";
    static public final String FILE4 = "file4";
    static public final String FILE8 = "file8";
    static public final String ELLIPSE_TYPE = "ellipse_type";
    static public final String HIDDEN = "hidden";
    static public final String POLYGON_TYPE = "polygon_type";
    static public final String OPTIONS = "options";
    static public final String NEEDLE_COLOUR = "needle_colour";
    static public final String BORDER_COLOUR = "border_colour";
    static public final String TARGET_LINE_COLOUR = "target_line_colour";
    static public final String ARC_AND_TICK_COLOUR = "arc_and_tick_colour";
    static public final String MIN_VALUE = "min_value";
    static public final String MAX_VALUE = "max_value";
    static public final String NUMBER_OF_TICKS = "number_of_ticks";
    static public final String BAR_GRAPH_WIDTH = "bar_graph_width";
    static public final String SCALE = "scale";
    static public final String OFFSET = "offset";
    static public final String CODE = "code";
    static public final String SELECTABLE = "selectable";
    static public final String LATCHABLE = "latchable";
    static public final String ENABLED = "enabled";
    static public final String HIDE_SHOW = "hide_show";
    static public final String ENABLE_DISABLE = "enable_disable";
    static public final String FORMAT = "format";
    static public final String RLE = "rle";
    static public final String FONT_TYPE = "font_type";
    static public final String FONT_STYLE = "font_style";
    static public final String PRIORITY = "priority";
    static public final String ACOUSTIC_SIGNAL = "acoustic_signal";
    static public final String FUNCTION_TYPE = "function_type";
    static public final String VALIDATION_TYPE = "validation_type";    
    static public final String MASK_TYPE = "mask_type";
    
    // pseudo attributes
    static public final String BLOCK_FONT = "block_font";
    static public final String BLOCK_ROW = "block_row";
    static public final String BLOCK_COL = "block_col";

    // macro roles
    static public final String ON_ACTIVATE = "on_activate";
    static public final String ON_DEACTIVATE = "on_deactivate";
    static public final String ON_SHOW = "on_show";
    static public final String ON_HIDE = "on_hide";
    // static public final String ON_REFRESH = "on_refresh"; // cannot be used in macros!
    static public final String ON_ENABLE = "on_enable";
    static public final String ON_DISABLE = "on_disable";
    static public final String ON_CHANGE_ACTIVE_MASK = "on_change_active_mask";
    static public final String ON_CHANGE_SOFT_KEY_MASK = "on_change_soft_key_mask";
    static public final String ON_CHANGE_ATTRIBUTE = "on_change_attribute"; 
    static public final String ON_CHANGE_BACKGROUND_COLOUR = "on_change_background_colour";
    static public final String ON_CHANGE_FONT_ATTRIBUTES = "on_change_font_attributes"; 
    static public final String ON_CHANGE_LINE_ATTRIBUTES = "on_change_line_attributes";
    static public final String ON_CHANGE_FILL_ATTRIBUTES = "on_change_fill_attributes";
    static public final String ON_CHANGE_CHILD_LOCATION = "on_change_child_location";
    static public final String ON_CHANGE_SIZE = "on_change_size";
    static public final String ON_CHANGE_VALUE = "on_change_value";
    static public final String ON_CHANGE_PRIORITY = "on_change_priority";
    static public final String ON_CHANGE_END_POINT = "on_change_end_point";
    static public final String ON_INPUT_FIELD_SELECTION = "on_input_field_selection";
    static public final String ON_INPUT_FIELD_DESELECTION = "on_input_field_deselection";
    static public final String ON_ESC = "on_esc";
    static public final String ON_ENTRY_OF_VALUE = "on_entry_of_value";
    static public final String ON_ENTRY_OF_NEW_VALUE = "on_entry_of_new_value";
    static public final String ON_KEY_PRESS = "on_key_press";
    static public final String ON_KEY_RELEASE = "on_key_release";
    static public final String ON_CHANGE_CHILD_POSITION = "on_change_child_position";

    //static public final String ATTRIBUTE = "attribute";
    static public final String ROLE = "role";
    static public final String BLOCK_FONT_SIZE = "block_font_size";
    static public final String IMAGE_WIDTH = "image_width";
    static public final String IMAGE_HEIGHT = "image_height";
    static public final String USE = "use";
            
    // special elements
    static public final String IMAGE_DATA = "image_data";
    
    
    static public final String COMMAND_HIDE_SHOW_OBJECT = "command_hide_show_object";
    static public final String COMMAND_ENABLE_DISABLE_OBJECT = "command_enable_disable_object";
    static public final String COMMAND_SELECT_INPUT_OBJECT = "command_select_input_object";
    static public final String COMMAND_CONTROL_AUDIO_DEVICE = "command_control_audio_device";
    static public final String COMMAND_SET_AUDIO_VOLUME = "command_set_audio_volume";
    static public final String COMMAND_CHANGE_CHILD_LOCATION = "command_change_child_location";
    static public final String COMMAND_CHANGE_CHILD_POSITION = "command_change_child_position";
    static public final String COMMAND_CHANGE_SIZE = "command_change_size";
    static public final String COMMAND_CHANGE_BACKGROUND_COLOUR = "command_change_background_colour";
    static public final String COMMAND_CHANGE_NUMERIC_VALUE = "command_change_numeric_value";
    static public final String COMMAND_CHANGE_STRING_VALUE = "command_change_string_value";
    static public final String COMMAND_CHANGE_END_POINT = "command_change_end_point";
    static public final String COMMAND_CHANGE_FONT_ATTRIBUTES = "command_change_font_attributes";
    static public final String COMMAND_CHANGE_LINE_ATTRIBUTES = "command_change_line_attributes";
    static public final String COMMAND_CHANGE_FILL_ATTRIBUTES = "command_change_fill_attributes";
    static public final String COMMAND_CHANGE_ACTIVE_MASK = "command_change_active_mask";
    static public final String COMMAND_CHANGE_SOFT_KEY_MASK = "command_change_soft_key_mask";
    static public final String COMMAND_CHANGE_ATTRIBUTE = "command_change_attribute";
    static public final String COMMAND_CHANGE_PRIORITY = "command_change_priority";
    static public final String COMMAND_CHANGE_LIST_ITEM = "command_change_list_item";
    
    static public final String[] COMMANDS = {
        COMMAND_HIDE_SHOW_OBJECT, COMMAND_ENABLE_DISABLE_OBJECT, 
        COMMAND_SELECT_INPUT_OBJECT, COMMAND_CONTROL_AUDIO_DEVICE,
        COMMAND_SET_AUDIO_VOLUME, COMMAND_CHANGE_CHILD_LOCATION,
        COMMAND_CHANGE_CHILD_POSITION, COMMAND_CHANGE_SIZE, 
        COMMAND_CHANGE_BACKGROUND_COLOUR, COMMAND_CHANGE_NUMERIC_VALUE,
        COMMAND_CHANGE_STRING_VALUE, COMMAND_CHANGE_END_POINT,
        COMMAND_CHANGE_FONT_ATTRIBUTES, COMMAND_CHANGE_LINE_ATTRIBUTES,
        COMMAND_CHANGE_FILL_ATTRIBUTES, COMMAND_CHANGE_ACTIVE_MASK,
        COMMAND_CHANGE_SOFT_KEY_MASK, COMMAND_CHANGE_ATTRIBUTE,
        COMMAND_CHANGE_PRIORITY, COMMAND_CHANGE_LIST_ITEM
    };
}
