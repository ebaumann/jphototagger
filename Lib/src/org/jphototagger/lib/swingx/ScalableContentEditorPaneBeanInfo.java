package org.jphototagger.lib.swingx;

import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


/**
 *
 * @author Elmar Baumann
 */
public class ScalableContentEditorPaneBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

        // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_accessibleContext = 0;
    private static final int PROPERTY_actionMap = 1;
    private static final int PROPERTY_actions = 2;
    private static final int PROPERTY_alignmentX = 3;
    private static final int PROPERTY_alignmentY = 4;
    private static final int PROPERTY_ancestorListeners = 5;
    private static final int PROPERTY_autoscrolls = 6;
    private static final int PROPERTY_background = 7;
    private static final int PROPERTY_backgroundSet = 8;
    private static final int PROPERTY_baselineResizeBehavior = 9;
    private static final int PROPERTY_border = 10;
    private static final int PROPERTY_bounds = 11;
    private static final int PROPERTY_caret = 12;
    private static final int PROPERTY_caretColor = 13;
    private static final int PROPERTY_caretListeners = 14;
    private static final int PROPERTY_caretPosition = 15;
    private static final int PROPERTY_colorModel = 16;
    private static final int PROPERTY_commands = 17;
    private static final int PROPERTY_component = 18;
    private static final int PROPERTY_componentCount = 19;
    private static final int PROPERTY_componentListeners = 20;
    private static final int PROPERTY_componentOrientation = 21;
    private static final int PROPERTY_componentPopupMenu = 22;
    private static final int PROPERTY_components = 23;
    private static final int PROPERTY_containerListeners = 24;
    private static final int PROPERTY_contentType = 25;
    private static final int PROPERTY_cursor = 26;
    private static final int PROPERTY_cursorSet = 27;
    private static final int PROPERTY_debugGraphicsOptions = 28;
    private static final int PROPERTY_disabledTextColor = 29;
    private static final int PROPERTY_displayable = 30;
    private static final int PROPERTY_document = 31;
    private static final int PROPERTY_doubleBuffered = 32;
    private static final int PROPERTY_dragEnabled = 33;
    private static final int PROPERTY_dropLocation = 34;
    private static final int PROPERTY_dropMode = 35;
    private static final int PROPERTY_dropTarget = 36;
    private static final int PROPERTY_editable = 37;
    private static final int PROPERTY_editorKit = 38;
    private static final int PROPERTY_enabled = 39;
    private static final int PROPERTY_focusable = 40;
    private static final int PROPERTY_focusAccelerator = 41;
    private static final int PROPERTY_focusCycleRoot = 42;
    private static final int PROPERTY_focusCycleRootAncestor = 43;
    private static final int PROPERTY_focusListeners = 44;
    private static final int PROPERTY_focusOwner = 45;
    private static final int PROPERTY_focusTraversable = 46;
    private static final int PROPERTY_focusTraversalKeys = 47;
    private static final int PROPERTY_focusTraversalKeysEnabled = 48;
    private static final int PROPERTY_focusTraversalPolicy = 49;
    private static final int PROPERTY_focusTraversalPolicyProvider = 50;
    private static final int PROPERTY_focusTraversalPolicySet = 51;
    private static final int PROPERTY_font = 52;
    private static final int PROPERTY_fontSet = 53;
    private static final int PROPERTY_foreground = 54;
    private static final int PROPERTY_foregroundSet = 55;
    private static final int PROPERTY_graphics = 56;
    private static final int PROPERTY_graphicsConfiguration = 57;
    private static final int PROPERTY_height = 58;
    private static final int PROPERTY_hierarchyBoundsListeners = 59;
    private static final int PROPERTY_hierarchyListeners = 60;
    private static final int PROPERTY_highlighter = 61;
    private static final int PROPERTY_hyperlinkListeners = 62;
    private static final int PROPERTY_ignoreRepaint = 63;
    private static final int PROPERTY_inheritsPopupMenu = 64;
    private static final int PROPERTY_inputContext = 65;
    private static final int PROPERTY_inputMap = 66;
    private static final int PROPERTY_inputMethodListeners = 67;
    private static final int PROPERTY_inputMethodRequests = 68;
    private static final int PROPERTY_inputVerifier = 69;
    private static final int PROPERTY_insets = 70;
    private static final int PROPERTY_keyListeners = 71;
    private static final int PROPERTY_keymap = 72;
    private static final int PROPERTY_layout = 73;
    private static final int PROPERTY_lightweight = 74;
    private static final int PROPERTY_locale = 75;
    private static final int PROPERTY_location = 76;
    private static final int PROPERTY_locationOnScreen = 77;
    private static final int PROPERTY_managingFocus = 78;
    private static final int PROPERTY_margin = 79;
    private static final int PROPERTY_maximumSize = 80;
    private static final int PROPERTY_maximumSizeSet = 81;
    private static final int PROPERTY_minimumSize = 82;
    private static final int PROPERTY_minimumSizeSet = 83;
    private static final int PROPERTY_mouseListeners = 84;
    private static final int PROPERTY_mouseMotionListeners = 85;
    private static final int PROPERTY_mousePosition = 86;
    private static final int PROPERTY_mouseWheelListeners = 87;
    private static final int PROPERTY_name = 88;
    private static final int PROPERTY_navigationFilter = 89;
    private static final int PROPERTY_nextFocusableComponent = 90;
    private static final int PROPERTY_opaque = 91;
    private static final int PROPERTY_optimizedDrawingEnabled = 92;
    private static final int PROPERTY_page = 93;
    private static final int PROPERTY_paintingForPrint = 94;
    private static final int PROPERTY_paintingTile = 95;
    private static final int PROPERTY_paragraphSelector = 96;
    private static final int PROPERTY_parent = 97;
    private static final int PROPERTY_peer = 98;
    private static final int PROPERTY_preferredScrollableViewportSize = 99;
    private static final int PROPERTY_preferredSize = 100;
    private static final int PROPERTY_preferredSizeSet = 101;
    private static final int PROPERTY_propertyChangeListeners = 102;
    private static final int PROPERTY_registeredKeyStrokes = 103;
    private static final int PROPERTY_requestFocusEnabled = 104;
    private static final int PROPERTY_rootPane = 105;
    private static final int PROPERTY_scaleFactor = 106;
    private static final int PROPERTY_scaleFactorPercent = 107;
    private static final int PROPERTY_scrollableTracksViewportHeight = 108;
    private static final int PROPERTY_scrollableTracksViewportWidth = 109;
    private static final int PROPERTY_searchable = 110;
    private static final int PROPERTY_selectedText = 111;
    private static final int PROPERTY_selectedTextColor = 112;
    private static final int PROPERTY_selectionColor = 113;
    private static final int PROPERTY_selectionEnd = 114;
    private static final int PROPERTY_selectionStart = 115;
    private static final int PROPERTY_showing = 116;
    private static final int PROPERTY_size = 117;
    private static final int PROPERTY_text = 118;
    private static final int PROPERTY_toolkit = 119;
    private static final int PROPERTY_toolTipText = 120;
    private static final int PROPERTY_topLevelAncestor = 121;
    private static final int PROPERTY_transferHandler = 122;
    private static final int PROPERTY_treeLock = 123;
    private static final int PROPERTY_UI = 124;
    private static final int PROPERTY_UIClassID = 125;
    private static final int PROPERTY_valid = 126;
    private static final int PROPERTY_validateRoot = 127;
    private static final int PROPERTY_verifyInputWhenFocusTarget = 128;
    private static final int PROPERTY_vetoableChangeListeners = 129;
    private static final int PROPERTY_visible = 130;
    private static final int PROPERTY_visibleRect = 131;
    private static final int PROPERTY_width = 132;
    private static final int PROPERTY_x = 133;
    private static final int PROPERTY_y = 134;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[135];
    
        try {
            properties[PROPERTY_accessibleContext] = new PropertyDescriptor ( "accessibleContext", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getAccessibleContext", null ); // NOI18N
            properties[PROPERTY_actionMap] = new PropertyDescriptor ( "actionMap", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getActionMap", "setActionMap" ); // NOI18N
            properties[PROPERTY_actions] = new PropertyDescriptor ( "actions", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getActions", null ); // NOI18N
            properties[PROPERTY_alignmentX] = new PropertyDescriptor ( "alignmentX", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getAlignmentX", "setAlignmentX" ); // NOI18N
            properties[PROPERTY_alignmentY] = new PropertyDescriptor ( "alignmentY", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getAlignmentY", "setAlignmentY" ); // NOI18N
            properties[PROPERTY_ancestorListeners] = new PropertyDescriptor ( "ancestorListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getAncestorListeners", null ); // NOI18N
            properties[PROPERTY_autoscrolls] = new PropertyDescriptor ( "autoscrolls", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getAutoscrolls", "setAutoscrolls" ); // NOI18N
            properties[PROPERTY_background] = new PropertyDescriptor ( "background", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getBackground", "setBackground" ); // NOI18N
            properties[PROPERTY_backgroundSet] = new PropertyDescriptor ( "backgroundSet", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isBackgroundSet", null ); // NOI18N
            properties[PROPERTY_baselineResizeBehavior] = new PropertyDescriptor ( "baselineResizeBehavior", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getBaselineResizeBehavior", null ); // NOI18N
            properties[PROPERTY_border] = new PropertyDescriptor ( "border", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getBorder", "setBorder" ); // NOI18N
            properties[PROPERTY_bounds] = new PropertyDescriptor ( "bounds", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getBounds", "setBounds" ); // NOI18N
            properties[PROPERTY_caret] = new PropertyDescriptor ( "caret", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getCaret", "setCaret" ); // NOI18N
            properties[PROPERTY_caretColor] = new PropertyDescriptor ( "caretColor", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getCaretColor", "setCaretColor" ); // NOI18N
            properties[PROPERTY_caretListeners] = new PropertyDescriptor ( "caretListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getCaretListeners", null ); // NOI18N
            properties[PROPERTY_caretPosition] = new PropertyDescriptor ( "caretPosition", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getCaretPosition", "setCaretPosition" ); // NOI18N
            properties[PROPERTY_colorModel] = new PropertyDescriptor ( "colorModel", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getColorModel", null ); // NOI18N
            properties[PROPERTY_commands] = new PropertyDescriptor ( "commands", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getCommands", null ); // NOI18N
            properties[PROPERTY_component] = new IndexedPropertyDescriptor ( "component", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, null, null, "getComponent", null ); // NOI18N
            properties[PROPERTY_componentCount] = new PropertyDescriptor ( "componentCount", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getComponentCount", null ); // NOI18N
            properties[PROPERTY_componentListeners] = new PropertyDescriptor ( "componentListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getComponentListeners", null ); // NOI18N
            properties[PROPERTY_componentOrientation] = new PropertyDescriptor ( "componentOrientation", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getComponentOrientation", "setComponentOrientation" ); // NOI18N
            properties[PROPERTY_componentPopupMenu] = new PropertyDescriptor ( "componentPopupMenu", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getComponentPopupMenu", "setComponentPopupMenu" ); // NOI18N
            properties[PROPERTY_components] = new PropertyDescriptor ( "components", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getComponents", null ); // NOI18N
            properties[PROPERTY_containerListeners] = new PropertyDescriptor ( "containerListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getContainerListeners", null ); // NOI18N
            properties[PROPERTY_contentType] = new PropertyDescriptor ( "contentType", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getContentType", "setContentType" ); // NOI18N
            properties[PROPERTY_cursor] = new PropertyDescriptor ( "cursor", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getCursor", "setCursor" ); // NOI18N
            properties[PROPERTY_cursorSet] = new PropertyDescriptor ( "cursorSet", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isCursorSet", null ); // NOI18N
            properties[PROPERTY_debugGraphicsOptions] = new PropertyDescriptor ( "debugGraphicsOptions", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getDebugGraphicsOptions", "setDebugGraphicsOptions" ); // NOI18N
            properties[PROPERTY_disabledTextColor] = new PropertyDescriptor ( "disabledTextColor", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getDisabledTextColor", "setDisabledTextColor" ); // NOI18N
            properties[PROPERTY_displayable] = new PropertyDescriptor ( "displayable", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isDisplayable", null ); // NOI18N
            properties[PROPERTY_document] = new PropertyDescriptor ( "document", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getDocument", "setDocument" ); // NOI18N
            properties[PROPERTY_doubleBuffered] = new PropertyDescriptor ( "doubleBuffered", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isDoubleBuffered", "setDoubleBuffered" ); // NOI18N
            properties[PROPERTY_dragEnabled] = new PropertyDescriptor ( "dragEnabled", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getDragEnabled", "setDragEnabled" ); // NOI18N
            properties[PROPERTY_dropLocation] = new PropertyDescriptor ( "dropLocation", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getDropLocation", null ); // NOI18N
            properties[PROPERTY_dropMode] = new PropertyDescriptor ( "dropMode", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getDropMode", "setDropMode" ); // NOI18N
            properties[PROPERTY_dropTarget] = new PropertyDescriptor ( "dropTarget", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getDropTarget", "setDropTarget" ); // NOI18N
            properties[PROPERTY_editable] = new PropertyDescriptor ( "editable", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isEditable", "setEditable" ); // NOI18N
            properties[PROPERTY_editorKit] = new PropertyDescriptor ( "editorKit", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getEditorKit", "setEditorKit" ); // NOI18N
            properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isEnabled", "setEnabled" ); // NOI18N
            properties[PROPERTY_focusable] = new PropertyDescriptor ( "focusable", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isFocusable", "setFocusable" ); // NOI18N
            properties[PROPERTY_focusAccelerator] = new PropertyDescriptor ( "focusAccelerator", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getFocusAccelerator", "setFocusAccelerator" ); // NOI18N
            properties[PROPERTY_focusCycleRoot] = new PropertyDescriptor ( "focusCycleRoot", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isFocusCycleRoot", "setFocusCycleRoot" ); // NOI18N
            properties[PROPERTY_focusCycleRootAncestor] = new PropertyDescriptor ( "focusCycleRootAncestor", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getFocusCycleRootAncestor", null ); // NOI18N
            properties[PROPERTY_focusListeners] = new PropertyDescriptor ( "focusListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getFocusListeners", null ); // NOI18N
            properties[PROPERTY_focusOwner] = new PropertyDescriptor ( "focusOwner", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isFocusOwner", null ); // NOI18N
            properties[PROPERTY_focusTraversable] = new PropertyDescriptor ( "focusTraversable", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isFocusTraversable", null ); // NOI18N
            properties[PROPERTY_focusTraversalKeys] = new IndexedPropertyDescriptor ( "focusTraversalKeys", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, null, null, null, "setFocusTraversalKeys" ); // NOI18N
            properties[PROPERTY_focusTraversalKeysEnabled] = new PropertyDescriptor ( "focusTraversalKeysEnabled", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getFocusTraversalKeysEnabled", "setFocusTraversalKeysEnabled" ); // NOI18N
            properties[PROPERTY_focusTraversalPolicy] = new PropertyDescriptor ( "focusTraversalPolicy", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getFocusTraversalPolicy", "setFocusTraversalPolicy" ); // NOI18N
            properties[PROPERTY_focusTraversalPolicyProvider] = new PropertyDescriptor ( "focusTraversalPolicyProvider", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isFocusTraversalPolicyProvider", "setFocusTraversalPolicyProvider" ); // NOI18N
            properties[PROPERTY_focusTraversalPolicySet] = new PropertyDescriptor ( "focusTraversalPolicySet", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isFocusTraversalPolicySet", null ); // NOI18N
            properties[PROPERTY_font] = new PropertyDescriptor ( "font", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getFont", "setFont" ); // NOI18N
            properties[PROPERTY_fontSet] = new PropertyDescriptor ( "fontSet", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isFontSet", null ); // NOI18N
            properties[PROPERTY_foreground] = new PropertyDescriptor ( "foreground", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getForeground", "setForeground" ); // NOI18N
            properties[PROPERTY_foregroundSet] = new PropertyDescriptor ( "foregroundSet", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isForegroundSet", null ); // NOI18N
            properties[PROPERTY_graphics] = new PropertyDescriptor ( "graphics", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getGraphics", null ); // NOI18N
            properties[PROPERTY_graphicsConfiguration] = new PropertyDescriptor ( "graphicsConfiguration", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getGraphicsConfiguration", null ); // NOI18N
            properties[PROPERTY_height] = new PropertyDescriptor ( "height", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getHeight", null ); // NOI18N
            properties[PROPERTY_hierarchyBoundsListeners] = new PropertyDescriptor ( "hierarchyBoundsListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getHierarchyBoundsListeners", null ); // NOI18N
            properties[PROPERTY_hierarchyListeners] = new PropertyDescriptor ( "hierarchyListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getHierarchyListeners", null ); // NOI18N
            properties[PROPERTY_highlighter] = new PropertyDescriptor ( "highlighter", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getHighlighter", "setHighlighter" ); // NOI18N
            properties[PROPERTY_hyperlinkListeners] = new PropertyDescriptor ( "hyperlinkListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getHyperlinkListeners", null ); // NOI18N
            properties[PROPERTY_ignoreRepaint] = new PropertyDescriptor ( "ignoreRepaint", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getIgnoreRepaint", "setIgnoreRepaint" ); // NOI18N
            properties[PROPERTY_inheritsPopupMenu] = new PropertyDescriptor ( "inheritsPopupMenu", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getInheritsPopupMenu", "setInheritsPopupMenu" ); // NOI18N
            properties[PROPERTY_inputContext] = new PropertyDescriptor ( "inputContext", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getInputContext", null ); // NOI18N
            properties[PROPERTY_inputMap] = new PropertyDescriptor ( "inputMap", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getInputMap", null ); // NOI18N
            properties[PROPERTY_inputMethodListeners] = new PropertyDescriptor ( "inputMethodListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getInputMethodListeners", null ); // NOI18N
            properties[PROPERTY_inputMethodRequests] = new PropertyDescriptor ( "inputMethodRequests", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getInputMethodRequests", null ); // NOI18N
            properties[PROPERTY_inputVerifier] = new PropertyDescriptor ( "inputVerifier", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getInputVerifier", "setInputVerifier" ); // NOI18N
            properties[PROPERTY_insets] = new PropertyDescriptor ( "insets", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getInsets", null ); // NOI18N
            properties[PROPERTY_keyListeners] = new PropertyDescriptor ( "keyListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getKeyListeners", null ); // NOI18N
            properties[PROPERTY_keymap] = new PropertyDescriptor ( "keymap", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getKeymap", "setKeymap" ); // NOI18N
            properties[PROPERTY_layout] = new PropertyDescriptor ( "layout", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getLayout", "setLayout" ); // NOI18N
            properties[PROPERTY_lightweight] = new PropertyDescriptor ( "lightweight", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isLightweight", null ); // NOI18N
            properties[PROPERTY_locale] = new PropertyDescriptor ( "locale", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getLocale", "setLocale" ); // NOI18N
            properties[PROPERTY_location] = new PropertyDescriptor ( "location", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getLocation", "setLocation" ); // NOI18N
            properties[PROPERTY_locationOnScreen] = new PropertyDescriptor ( "locationOnScreen", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getLocationOnScreen", null ); // NOI18N
            properties[PROPERTY_managingFocus] = new PropertyDescriptor ( "managingFocus", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isManagingFocus", null ); // NOI18N
            properties[PROPERTY_margin] = new PropertyDescriptor ( "margin", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getMargin", "setMargin" ); // NOI18N
            properties[PROPERTY_maximumSize] = new PropertyDescriptor ( "maximumSize", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getMaximumSize", "setMaximumSize" ); // NOI18N
            properties[PROPERTY_maximumSizeSet] = new PropertyDescriptor ( "maximumSizeSet", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isMaximumSizeSet", null ); // NOI18N
            properties[PROPERTY_minimumSize] = new PropertyDescriptor ( "minimumSize", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getMinimumSize", "setMinimumSize" ); // NOI18N
            properties[PROPERTY_minimumSizeSet] = new PropertyDescriptor ( "minimumSizeSet", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isMinimumSizeSet", null ); // NOI18N
            properties[PROPERTY_mouseListeners] = new PropertyDescriptor ( "mouseListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getMouseListeners", null ); // NOI18N
            properties[PROPERTY_mouseMotionListeners] = new PropertyDescriptor ( "mouseMotionListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getMouseMotionListeners", null ); // NOI18N
            properties[PROPERTY_mousePosition] = new PropertyDescriptor ( "mousePosition", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getMousePosition", null ); // NOI18N
            properties[PROPERTY_mouseWheelListeners] = new PropertyDescriptor ( "mouseWheelListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getMouseWheelListeners", null ); // NOI18N
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getName", "setName" ); // NOI18N
            properties[PROPERTY_navigationFilter] = new PropertyDescriptor ( "navigationFilter", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getNavigationFilter", "setNavigationFilter" ); // NOI18N
            properties[PROPERTY_nextFocusableComponent] = new PropertyDescriptor ( "nextFocusableComponent", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getNextFocusableComponent", "setNextFocusableComponent" ); // NOI18N
            properties[PROPERTY_opaque] = new PropertyDescriptor ( "opaque", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isOpaque", "setOpaque" ); // NOI18N
            properties[PROPERTY_optimizedDrawingEnabled] = new PropertyDescriptor ( "optimizedDrawingEnabled", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isOptimizedDrawingEnabled", null ); // NOI18N
            properties[PROPERTY_page] = new PropertyDescriptor ( "page", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, null, "setPage" ); // NOI18N
            properties[PROPERTY_paintingForPrint] = new PropertyDescriptor ( "paintingForPrint", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isPaintingForPrint", null ); // NOI18N
            properties[PROPERTY_paintingTile] = new PropertyDescriptor ( "paintingTile", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isPaintingTile", null ); // NOI18N
            properties[PROPERTY_paragraphSelector] = new PropertyDescriptor ( "paragraphSelector", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getParagraphSelector", null ); // NOI18N
            properties[PROPERTY_parent] = new PropertyDescriptor ( "parent", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getParent", null ); // NOI18N
            properties[PROPERTY_peer] = new PropertyDescriptor ( "peer", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getPeer", null ); // NOI18N
            properties[PROPERTY_preferredScrollableViewportSize] = new PropertyDescriptor ( "preferredScrollableViewportSize", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getPreferredScrollableViewportSize", null ); // NOI18N
            properties[PROPERTY_preferredSize] = new PropertyDescriptor ( "preferredSize", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getPreferredSize", "setPreferredSize" ); // NOI18N
            properties[PROPERTY_preferredSizeSet] = new PropertyDescriptor ( "preferredSizeSet", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isPreferredSizeSet", null ); // NOI18N
            properties[PROPERTY_propertyChangeListeners] = new PropertyDescriptor ( "propertyChangeListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getPropertyChangeListeners", null ); // NOI18N
            properties[PROPERTY_registeredKeyStrokes] = new PropertyDescriptor ( "registeredKeyStrokes", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getRegisteredKeyStrokes", null ); // NOI18N
            properties[PROPERTY_requestFocusEnabled] = new PropertyDescriptor ( "requestFocusEnabled", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isRequestFocusEnabled", "setRequestFocusEnabled" ); // NOI18N
            properties[PROPERTY_rootPane] = new PropertyDescriptor ( "rootPane", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getRootPane", null ); // NOI18N
            properties[PROPERTY_scaleFactor] = new PropertyDescriptor ( "scaleFactor", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getScaleFactor", "setScaleFactor" ); // NOI18N
            properties[PROPERTY_scaleFactor].setPreferred ( true );
            properties[PROPERTY_scaleFactor].setDisplayName ( "Scale Factor" );
            properties[PROPERTY_scaleFactor].setBound ( true );
            properties[PROPERTY_scaleFactorPercent] = new PropertyDescriptor ( "scaleFactorPercent", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getScaleFactorPercent", "setScaleFactorPercent" ); // NOI18N
            properties[PROPERTY_scaleFactorPercent].setPreferred ( true );
            properties[PROPERTY_scaleFactorPercent].setDisplayName ( "Scale Factor Percent" );
            properties[PROPERTY_scaleFactorPercent].setShortDescription ( "1/100 of Scale Factor" );
            properties[PROPERTY_scaleFactorPercent].setBound ( true );
            properties[PROPERTY_scrollableTracksViewportHeight] = new PropertyDescriptor ( "scrollableTracksViewportHeight", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getScrollableTracksViewportHeight", null ); // NOI18N
            properties[PROPERTY_scrollableTracksViewportWidth] = new PropertyDescriptor ( "scrollableTracksViewportWidth", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getScrollableTracksViewportWidth", null ); // NOI18N
            properties[PROPERTY_searchable] = new PropertyDescriptor ( "searchable", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getSearchable", "setSearchable" ); // NOI18N
            properties[PROPERTY_selectedText] = new PropertyDescriptor ( "selectedText", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getSelectedText", null ); // NOI18N
            properties[PROPERTY_selectedTextColor] = new PropertyDescriptor ( "selectedTextColor", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getSelectedTextColor", "setSelectedTextColor" ); // NOI18N
            properties[PROPERTY_selectionColor] = new PropertyDescriptor ( "selectionColor", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getSelectionColor", "setSelectionColor" ); // NOI18N
            properties[PROPERTY_selectionEnd] = new PropertyDescriptor ( "selectionEnd", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getSelectionEnd", "setSelectionEnd" ); // NOI18N
            properties[PROPERTY_selectionStart] = new PropertyDescriptor ( "selectionStart", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getSelectionStart", "setSelectionStart" ); // NOI18N
            properties[PROPERTY_showing] = new PropertyDescriptor ( "showing", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isShowing", null ); // NOI18N
            properties[PROPERTY_size] = new PropertyDescriptor ( "size", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getSize", "setSize" ); // NOI18N
            properties[PROPERTY_text] = new PropertyDescriptor ( "text", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getText", "setText" ); // NOI18N
            properties[PROPERTY_toolkit] = new PropertyDescriptor ( "toolkit", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getToolkit", null ); // NOI18N
            properties[PROPERTY_toolTipText] = new PropertyDescriptor ( "toolTipText", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getToolTipText", "setToolTipText" ); // NOI18N
            properties[PROPERTY_topLevelAncestor] = new PropertyDescriptor ( "topLevelAncestor", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getTopLevelAncestor", null ); // NOI18N
            properties[PROPERTY_transferHandler] = new PropertyDescriptor ( "transferHandler", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getTransferHandler", "setTransferHandler" ); // NOI18N
            properties[PROPERTY_treeLock] = new PropertyDescriptor ( "treeLock", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getTreeLock", null ); // NOI18N
            properties[PROPERTY_UI] = new PropertyDescriptor ( "UI", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getUI", "setUI" ); // NOI18N
            properties[PROPERTY_UIClassID] = new PropertyDescriptor ( "UIClassID", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getUIClassID", null ); // NOI18N
            properties[PROPERTY_valid] = new PropertyDescriptor ( "valid", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isValid", null ); // NOI18N
            properties[PROPERTY_validateRoot] = new PropertyDescriptor ( "validateRoot", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isValidateRoot", null ); // NOI18N
            properties[PROPERTY_verifyInputWhenFocusTarget] = new PropertyDescriptor ( "verifyInputWhenFocusTarget", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getVerifyInputWhenFocusTarget", "setVerifyInputWhenFocusTarget" ); // NOI18N
            properties[PROPERTY_vetoableChangeListeners] = new PropertyDescriptor ( "vetoableChangeListeners", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getVetoableChangeListeners", null ); // NOI18N
            properties[PROPERTY_visible] = new PropertyDescriptor ( "visible", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "isVisible", "setVisible" ); // NOI18N
            properties[PROPERTY_visibleRect] = new PropertyDescriptor ( "visibleRect", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getVisibleRect", null ); // NOI18N
            properties[PROPERTY_width] = new PropertyDescriptor ( "width", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getWidth", null ); // NOI18N
            properties[PROPERTY_x] = new PropertyDescriptor ( "x", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getX", null ); // NOI18N
            properties[PROPERTY_y] = new PropertyDescriptor ( "y", org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "getY", null ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties

        // Here you can add code for customizing the properties array.

        return properties;     }//GEN-LAST:Properties
    // EventSet identifiers//GEN-FIRST:Events
    private static final int EVENT_ancestorListener = 0;
    private static final int EVENT_caretListener = 1;
    private static final int EVENT_componentListener = 2;
    private static final int EVENT_containerListener = 3;
    private static final int EVENT_focusListener = 4;
    private static final int EVENT_hierarchyBoundsListener = 5;
    private static final int EVENT_hierarchyListener = 6;
    private static final int EVENT_hyperlinkListener = 7;
    private static final int EVENT_inputMethodListener = 8;
    private static final int EVENT_keyListener = 9;
    private static final int EVENT_mouseListener = 10;
    private static final int EVENT_mouseMotionListener = 11;
    private static final int EVENT_mouseWheelListener = 12;
    private static final int EVENT_propertyChangeListener = 13;
    private static final int EVENT_vetoableChangeListener = 14;

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[15];
    
        try {
            eventSets[EVENT_ancestorListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "ancestorListener", javax.swing.event.AncestorListener.class, new String[] {"ancestorAdded", "ancestorRemoved", "ancestorMoved"}, "addAncestorListener", "removeAncestorListener" ); // NOI18N
            eventSets[EVENT_caretListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "caretListener", javax.swing.event.CaretListener.class, new String[] {"caretUpdate"}, "addCaretListener", "removeCaretListener" ); // NOI18N
            eventSets[EVENT_componentListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "componentListener", java.awt.event.ComponentListener.class, new String[] {"componentResized", "componentMoved", "componentShown", "componentHidden"}, "addComponentListener", "removeComponentListener" ); // NOI18N
            eventSets[EVENT_containerListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "containerListener", java.awt.event.ContainerListener.class, new String[] {"componentAdded", "componentRemoved"}, "addContainerListener", "removeContainerListener" ); // NOI18N
            eventSets[EVENT_focusListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "focusListener", java.awt.event.FocusListener.class, new String[] {"focusGained", "focusLost"}, "addFocusListener", "removeFocusListener" ); // NOI18N
            eventSets[EVENT_hierarchyBoundsListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "hierarchyBoundsListener", java.awt.event.HierarchyBoundsListener.class, new String[] {"ancestorMoved", "ancestorResized"}, "addHierarchyBoundsListener", "removeHierarchyBoundsListener" ); // NOI18N
            eventSets[EVENT_hierarchyListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "hierarchyListener", java.awt.event.HierarchyListener.class, new String[] {"hierarchyChanged"}, "addHierarchyListener", "removeHierarchyListener" ); // NOI18N
            eventSets[EVENT_hyperlinkListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "hyperlinkListener", javax.swing.event.HyperlinkListener.class, new String[] {"hyperlinkUpdate"}, "addHyperlinkListener", "removeHyperlinkListener" ); // NOI18N
            eventSets[EVENT_inputMethodListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "inputMethodListener", java.awt.event.InputMethodListener.class, new String[] {"inputMethodTextChanged", "caretPositionChanged"}, "addInputMethodListener", "removeInputMethodListener" ); // NOI18N
            eventSets[EVENT_keyListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "keyListener", java.awt.event.KeyListener.class, new String[] {"keyTyped", "keyPressed", "keyReleased"}, "addKeyListener", "removeKeyListener" ); // NOI18N
            eventSets[EVENT_mouseListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "mouseListener", java.awt.event.MouseListener.class, new String[] {"mouseClicked", "mousePressed", "mouseReleased", "mouseEntered", "mouseExited"}, "addMouseListener", "removeMouseListener" ); // NOI18N
            eventSets[EVENT_mouseMotionListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "mouseMotionListener", java.awt.event.MouseMotionListener.class, new String[] {"mouseDragged", "mouseMoved"}, "addMouseMotionListener", "removeMouseMotionListener" ); // NOI18N
            eventSets[EVENT_mouseWheelListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "mouseWheelListener", java.awt.event.MouseWheelListener.class, new String[] {"mouseWheelMoved"}, "addMouseWheelListener", "removeMouseWheelListener" ); // NOI18N
            eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" ); // NOI18N
            eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor ( org.jphototagger.lib.swingx.ScalableContentEditorPane.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[] {"vetoableChange"}, "addVetoableChangeListener", "removeVetoableChangeListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events

        // Here you can add code for customizing the event sets array.

        return eventSets;     }//GEN-LAST:Events
    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_action0 = 0;
    private static final int METHOD_add1 = 1;
    private static final int METHOD_add2 = 2;
    private static final int METHOD_add3 = 3;
    private static final int METHOD_add4 = 4;
    private static final int METHOD_add5 = 5;
    private static final int METHOD_add6 = 6;
    private static final int METHOD_addKeymap7 = 7;
    private static final int METHOD_addNotify8 = 8;
    private static final int METHOD_addPropertyChangeListener9 = 9;
    private static final int METHOD_applyComponentOrientation10 = 10;
    private static final int METHOD_areFocusTraversalKeysSet11 = 11;
    private static final int METHOD_bounds12 = 12;
    private static final int METHOD_checkImage13 = 13;
    private static final int METHOD_checkImage14 = 14;
    private static final int METHOD_computeVisibleRect15 = 15;
    private static final int METHOD_contains16 = 16;
    private static final int METHOD_contains17 = 17;
    private static final int METHOD_copy18 = 18;
    private static final int METHOD_countComponents19 = 19;
    private static final int METHOD_createEditorKitForContentType20 = 20;
    private static final int METHOD_createImage21 = 21;
    private static final int METHOD_createImage22 = 22;
    private static final int METHOD_createToolTip23 = 23;
    private static final int METHOD_createVolatileImage24 = 24;
    private static final int METHOD_createVolatileImage25 = 25;
    private static final int METHOD_cut26 = 26;
    private static final int METHOD_deliverEvent27 = 27;
    private static final int METHOD_disable28 = 28;
    private static final int METHOD_dispatchEvent29 = 29;
    private static final int METHOD_doCommand30 = 30;
    private static final int METHOD_doLayout31 = 31;
    private static final int METHOD_enable32 = 32;
    private static final int METHOD_enable33 = 33;
    private static final int METHOD_enableInputMethods34 = 34;
    private static final int METHOD_findComponentAt35 = 35;
    private static final int METHOD_findComponentAt36 = 36;
    private static final int METHOD_fireHyperlinkUpdate37 = 37;
    private static final int METHOD_firePropertyChange38 = 38;
    private static final int METHOD_firePropertyChange39 = 39;
    private static final int METHOD_firePropertyChange40 = 40;
    private static final int METHOD_firePropertyChange41 = 41;
    private static final int METHOD_firePropertyChange42 = 42;
    private static final int METHOD_firePropertyChange43 = 43;
    private static final int METHOD_firePropertyChange44 = 44;
    private static final int METHOD_firePropertyChange45 = 45;
    private static final int METHOD_getActionForKeyStroke46 = 46;
    private static final int METHOD_getBaseline47 = 47;
    private static final int METHOD_getBounds48 = 48;
    private static final int METHOD_getClientProperty49 = 49;
    private static final int METHOD_getComponentAt50 = 50;
    private static final int METHOD_getComponentAt51 = 51;
    private static final int METHOD_getComponentZOrder52 = 52;
    private static final int METHOD_getConditionForKeyStroke53 = 53;
    private static final int METHOD_getDefaultLocale54 = 54;
    private static final int METHOD_getEditorKitClassNameForContentType55 = 55;
    private static final int METHOD_getEditorKitForContentType56 = 56;
    private static final int METHOD_getFocusTraversalKeys57 = 57;
    private static final int METHOD_getFontMetrics58 = 58;
    private static final int METHOD_getInsets59 = 59;
    private static final int METHOD_getKeymap60 = 60;
    private static final int METHOD_getListeners61 = 61;
    private static final int METHOD_getLocation62 = 62;
    private static final int METHOD_getMousePosition63 = 63;
    private static final int METHOD_getPage64 = 64;
    private static final int METHOD_getPopupLocation65 = 65;
    private static final int METHOD_getPrintable66 = 66;
    private static final int METHOD_getPropertyChangeListeners67 = 67;
    private static final int METHOD_getScrollableBlockIncrement68 = 68;
    private static final int METHOD_getScrollableUnitIncrement69 = 69;
    private static final int METHOD_getSize70 = 70;
    private static final int METHOD_getText71 = 71;
    private static final int METHOD_getToolTipLocation72 = 72;
    private static final int METHOD_getToolTipText73 = 73;
    private static final int METHOD_gotFocus74 = 74;
    private static final int METHOD_grabFocus75 = 75;
    private static final int METHOD_handleEvent76 = 76;
    private static final int METHOD_hasCommand77 = 77;
    private static final int METHOD_hasFocus78 = 78;
    private static final int METHOD_hide79 = 79;
    private static final int METHOD_imageUpdate80 = 80;
    private static final int METHOD_insets81 = 81;
    private static final int METHOD_inside82 = 82;
    private static final int METHOD_invalidate83 = 83;
    private static final int METHOD_isAncestorOf84 = 84;
    private static final int METHOD_isFocusCycleRoot85 = 85;
    private static final int METHOD_isLightweightComponent86 = 86;
    private static final int METHOD_keyDown87 = 87;
    private static final int METHOD_keyUp88 = 88;
    private static final int METHOD_layout89 = 89;
    private static final int METHOD_list90 = 90;
    private static final int METHOD_list91 = 91;
    private static final int METHOD_list92 = 92;
    private static final int METHOD_list93 = 93;
    private static final int METHOD_list94 = 94;
    private static final int METHOD_loadKeymap95 = 95;
    private static final int METHOD_locate96 = 96;
    private static final int METHOD_location97 = 97;
    private static final int METHOD_lostFocus98 = 98;
    private static final int METHOD_minimumSize99 = 99;
    private static final int METHOD_modelToView100 = 100;
    private static final int METHOD_mouseDown101 = 101;
    private static final int METHOD_mouseDrag102 = 102;
    private static final int METHOD_mouseEnter103 = 103;
    private static final int METHOD_mouseExit104 = 104;
    private static final int METHOD_mouseMove105 = 105;
    private static final int METHOD_mouseUp106 = 106;
    private static final int METHOD_move107 = 107;
    private static final int METHOD_moveCaretPosition108 = 108;
    private static final int METHOD_nextFocus109 = 109;
    private static final int METHOD_paint110 = 110;
    private static final int METHOD_paintAll111 = 111;
    private static final int METHOD_paintComponents112 = 112;
    private static final int METHOD_paintImmediately113 = 113;
    private static final int METHOD_paintImmediately114 = 114;
    private static final int METHOD_paste115 = 115;
    private static final int METHOD_postEvent116 = 116;
    private static final int METHOD_preferredSize117 = 117;
    private static final int METHOD_prepareImage118 = 118;
    private static final int METHOD_prepareImage119 = 119;
    private static final int METHOD_print120 = 120;
    private static final int METHOD_print121 = 121;
    private static final int METHOD_print122 = 122;
    private static final int METHOD_print123 = 123;
    private static final int METHOD_printAll124 = 124;
    private static final int METHOD_printComponents125 = 125;
    private static final int METHOD_putClientProperty126 = 126;
    private static final int METHOD_read127 = 127;
    private static final int METHOD_read128 = 128;
    private static final int METHOD_registerEditorKitForContentType129 = 129;
    private static final int METHOD_registerEditorKitForContentType130 = 130;
    private static final int METHOD_registerKeyboardAction131 = 131;
    private static final int METHOD_registerKeyboardAction132 = 132;
    private static final int METHOD_remove133 = 133;
    private static final int METHOD_remove134 = 134;
    private static final int METHOD_remove135 = 135;
    private static final int METHOD_removeAll136 = 136;
    private static final int METHOD_removeKeymap137 = 137;
    private static final int METHOD_removeNotify138 = 138;
    private static final int METHOD_removePropertyChangeListener139 = 139;
    private static final int METHOD_repaint140 = 140;
    private static final int METHOD_repaint141 = 141;
    private static final int METHOD_repaint142 = 142;
    private static final int METHOD_repaint143 = 143;
    private static final int METHOD_repaint144 = 144;
    private static final int METHOD_replaceSelection145 = 145;
    private static final int METHOD_requestDefaultFocus146 = 146;
    private static final int METHOD_requestFocus147 = 147;
    private static final int METHOD_requestFocus148 = 148;
    private static final int METHOD_requestFocusInWindow149 = 149;
    private static final int METHOD_resetKeyboardActions150 = 150;
    private static final int METHOD_reshape151 = 151;
    private static final int METHOD_resize152 = 152;
    private static final int METHOD_resize153 = 153;
    private static final int METHOD_revalidate154 = 154;
    private static final int METHOD_scrollRectToVisible155 = 155;
    private static final int METHOD_scrollToReference156 = 156;
    private static final int METHOD_select157 = 157;
    private static final int METHOD_selectAll158 = 158;
    private static final int METHOD_setBounds159 = 159;
    private static final int METHOD_setComponentZOrder160 = 160;
    private static final int METHOD_setDefaultLocale161 = 161;
    private static final int METHOD_setEditorKitForContentType162 = 162;
    private static final int METHOD_setPage163 = 163;
    private static final int METHOD_show164 = 164;
    private static final int METHOD_show165 = 165;
    private static final int METHOD_size166 = 166;
    private static final int METHOD_toString167 = 167;
    private static final int METHOD_transferFocus168 = 168;
    private static final int METHOD_transferFocusBackward169 = 169;
    private static final int METHOD_transferFocusDownCycle170 = 170;
    private static final int METHOD_transferFocusUpCycle171 = 171;
    private static final int METHOD_unregisterKeyboardAction172 = 172;
    private static final int METHOD_update173 = 173;
    private static final int METHOD_updateUI174 = 174;
    private static final int METHOD_validate175 = 175;
    private static final int METHOD_viewToModel176 = 176;
    private static final int METHOD_write177 = 177;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[178];
    
        try {
            methods[METHOD_action0] = new MethodDescriptor(java.awt.Component.class.getMethod("action", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_action0].setDisplayName ( "" );
            methods[METHOD_add1] = new MethodDescriptor(java.awt.Component.class.getMethod("add", new Class[] {java.awt.PopupMenu.class})); // NOI18N
            methods[METHOD_add1].setDisplayName ( "" );
            methods[METHOD_add2] = new MethodDescriptor(java.awt.Container.class.getMethod("add", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_add2].setDisplayName ( "" );
            methods[METHOD_add3] = new MethodDescriptor(java.awt.Container.class.getMethod("add", new Class[] {java.lang.String.class, java.awt.Component.class})); // NOI18N
            methods[METHOD_add3].setDisplayName ( "" );
            methods[METHOD_add4] = new MethodDescriptor(java.awt.Container.class.getMethod("add", new Class[] {java.awt.Component.class, int.class})); // NOI18N
            methods[METHOD_add4].setDisplayName ( "" );
            methods[METHOD_add5] = new MethodDescriptor(java.awt.Container.class.getMethod("add", new Class[] {java.awt.Component.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_add5].setDisplayName ( "" );
            methods[METHOD_add6] = new MethodDescriptor(java.awt.Container.class.getMethod("add", new Class[] {java.awt.Component.class, java.lang.Object.class, int.class})); // NOI18N
            methods[METHOD_add6].setDisplayName ( "" );
            methods[METHOD_addKeymap7] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("addKeymap", new Class[] {java.lang.String.class, javax.swing.text.Keymap.class})); // NOI18N
            methods[METHOD_addKeymap7].setDisplayName ( "" );
            methods[METHOD_addNotify8] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("addNotify", new Class[] {})); // NOI18N
            methods[METHOD_addNotify8].setDisplayName ( "" );
            methods[METHOD_addPropertyChangeListener9] = new MethodDescriptor(java.awt.Container.class.getMethod("addPropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_addPropertyChangeListener9].setDisplayName ( "" );
            methods[METHOD_applyComponentOrientation10] = new MethodDescriptor(java.awt.Container.class.getMethod("applyComponentOrientation", new Class[] {java.awt.ComponentOrientation.class})); // NOI18N
            methods[METHOD_applyComponentOrientation10].setDisplayName ( "" );
            methods[METHOD_areFocusTraversalKeysSet11] = new MethodDescriptor(java.awt.Container.class.getMethod("areFocusTraversalKeysSet", new Class[] {int.class})); // NOI18N
            methods[METHOD_areFocusTraversalKeysSet11].setDisplayName ( "" );
            methods[METHOD_bounds12] = new MethodDescriptor(java.awt.Component.class.getMethod("bounds", new Class[] {})); // NOI18N
            methods[METHOD_bounds12].setDisplayName ( "" );
            methods[METHOD_checkImage13] = new MethodDescriptor(java.awt.Component.class.getMethod("checkImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_checkImage13].setDisplayName ( "" );
            methods[METHOD_checkImage14] = new MethodDescriptor(java.awt.Component.class.getMethod("checkImage", new Class[] {java.awt.Image.class, int.class, int.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_checkImage14].setDisplayName ( "" );
            methods[METHOD_computeVisibleRect15] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("computeVisibleRect", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_computeVisibleRect15].setDisplayName ( "" );
            methods[METHOD_contains16] = new MethodDescriptor(java.awt.Component.class.getMethod("contains", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_contains16].setDisplayName ( "" );
            methods[METHOD_contains17] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("contains", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_contains17].setDisplayName ( "" );
            methods[METHOD_copy18] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("copy", new Class[] {})); // NOI18N
            methods[METHOD_copy18].setDisplayName ( "" );
            methods[METHOD_countComponents19] = new MethodDescriptor(java.awt.Container.class.getMethod("countComponents", new Class[] {})); // NOI18N
            methods[METHOD_countComponents19].setDisplayName ( "" );
            methods[METHOD_createEditorKitForContentType20] = new MethodDescriptor(javax.swing.JEditorPane.class.getMethod("createEditorKitForContentType", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_createEditorKitForContentType20].setDisplayName ( "" );
            methods[METHOD_createImage21] = new MethodDescriptor(java.awt.Component.class.getMethod("createImage", new Class[] {java.awt.image.ImageProducer.class})); // NOI18N
            methods[METHOD_createImage21].setDisplayName ( "" );
            methods[METHOD_createImage22] = new MethodDescriptor(java.awt.Component.class.getMethod("createImage", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_createImage22].setDisplayName ( "" );
            methods[METHOD_createToolTip23] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("createToolTip", new Class[] {})); // NOI18N
            methods[METHOD_createToolTip23].setDisplayName ( "" );
            methods[METHOD_createVolatileImage24] = new MethodDescriptor(java.awt.Component.class.getMethod("createVolatileImage", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_createVolatileImage24].setDisplayName ( "" );
            methods[METHOD_createVolatileImage25] = new MethodDescriptor(java.awt.Component.class.getMethod("createVolatileImage", new Class[] {int.class, int.class, java.awt.ImageCapabilities.class})); // NOI18N
            methods[METHOD_createVolatileImage25].setDisplayName ( "" );
            methods[METHOD_cut26] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("cut", new Class[] {})); // NOI18N
            methods[METHOD_cut26].setDisplayName ( "" );
            methods[METHOD_deliverEvent27] = new MethodDescriptor(java.awt.Container.class.getMethod("deliverEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_deliverEvent27].setDisplayName ( "" );
            methods[METHOD_disable28] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("disable", new Class[] {})); // NOI18N
            methods[METHOD_disable28].setDisplayName ( "" );
            methods[METHOD_dispatchEvent29] = new MethodDescriptor(java.awt.Component.class.getMethod("dispatchEvent", new Class[] {java.awt.AWTEvent.class})); // NOI18N
            methods[METHOD_dispatchEvent29].setDisplayName ( "" );
            methods[METHOD_doCommand30] = new MethodDescriptor(org.jdesktop.swingx.JXEditorPane.class.getMethod("doCommand", new Class[] {java.lang.Object.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_doCommand30].setDisplayName ( "" );
            methods[METHOD_doLayout31] = new MethodDescriptor(java.awt.Container.class.getMethod("doLayout", new Class[] {})); // NOI18N
            methods[METHOD_doLayout31].setDisplayName ( "" );
            methods[METHOD_enable32] = new MethodDescriptor(java.awt.Component.class.getMethod("enable", new Class[] {boolean.class})); // NOI18N
            methods[METHOD_enable32].setDisplayName ( "" );
            methods[METHOD_enable33] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("enable", new Class[] {})); // NOI18N
            methods[METHOD_enable33].setDisplayName ( "" );
            methods[METHOD_enableInputMethods34] = new MethodDescriptor(java.awt.Component.class.getMethod("enableInputMethods", new Class[] {boolean.class})); // NOI18N
            methods[METHOD_enableInputMethods34].setDisplayName ( "" );
            methods[METHOD_findComponentAt35] = new MethodDescriptor(java.awt.Container.class.getMethod("findComponentAt", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_findComponentAt35].setDisplayName ( "" );
            methods[METHOD_findComponentAt36] = new MethodDescriptor(java.awt.Container.class.getMethod("findComponentAt", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_findComponentAt36].setDisplayName ( "" );
            methods[METHOD_fireHyperlinkUpdate37] = new MethodDescriptor(javax.swing.JEditorPane.class.getMethod("fireHyperlinkUpdate", new Class[] {javax.swing.event.HyperlinkEvent.class})); // NOI18N
            methods[METHOD_fireHyperlinkUpdate37].setDisplayName ( "" );
            methods[METHOD_firePropertyChange38] = new MethodDescriptor(java.awt.Component.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, byte.class, byte.class})); // NOI18N
            methods[METHOD_firePropertyChange38].setDisplayName ( "" );
            methods[METHOD_firePropertyChange39] = new MethodDescriptor(java.awt.Component.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, short.class, short.class})); // NOI18N
            methods[METHOD_firePropertyChange39].setDisplayName ( "" );
            methods[METHOD_firePropertyChange40] = new MethodDescriptor(java.awt.Component.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, long.class, long.class})); // NOI18N
            methods[METHOD_firePropertyChange40].setDisplayName ( "" );
            methods[METHOD_firePropertyChange41] = new MethodDescriptor(java.awt.Component.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, float.class, float.class})); // NOI18N
            methods[METHOD_firePropertyChange41].setDisplayName ( "" );
            methods[METHOD_firePropertyChange42] = new MethodDescriptor(java.awt.Component.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, double.class, double.class})); // NOI18N
            methods[METHOD_firePropertyChange42].setDisplayName ( "" );
            methods[METHOD_firePropertyChange43] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, boolean.class, boolean.class})); // NOI18N
            methods[METHOD_firePropertyChange43].setDisplayName ( "" );
            methods[METHOD_firePropertyChange44] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, int.class, int.class})); // NOI18N
            methods[METHOD_firePropertyChange44].setDisplayName ( "" );
            methods[METHOD_firePropertyChange45] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, char.class, char.class})); // NOI18N
            methods[METHOD_firePropertyChange45].setDisplayName ( "" );
            methods[METHOD_getActionForKeyStroke46] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getActionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getActionForKeyStroke46].setDisplayName ( "" );
            methods[METHOD_getBaseline47] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getBaseline", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_getBaseline47].setDisplayName ( "" );
            methods[METHOD_getBounds48] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getBounds", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_getBounds48].setDisplayName ( "" );
            methods[METHOD_getClientProperty49] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getClientProperty", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_getClientProperty49].setDisplayName ( "" );
            methods[METHOD_getComponentAt50] = new MethodDescriptor(java.awt.Container.class.getMethod("getComponentAt", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_getComponentAt50].setDisplayName ( "" );
            methods[METHOD_getComponentAt51] = new MethodDescriptor(java.awt.Container.class.getMethod("getComponentAt", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_getComponentAt51].setDisplayName ( "" );
            methods[METHOD_getComponentZOrder52] = new MethodDescriptor(java.awt.Container.class.getMethod("getComponentZOrder", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_getComponentZOrder52].setDisplayName ( "" );
            methods[METHOD_getConditionForKeyStroke53] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getConditionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getConditionForKeyStroke53].setDisplayName ( "" );
            methods[METHOD_getDefaultLocale54] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getDefaultLocale", new Class[] {})); // NOI18N
            methods[METHOD_getDefaultLocale54].setDisplayName ( "" );
            methods[METHOD_getEditorKitClassNameForContentType55] = new MethodDescriptor(javax.swing.JEditorPane.class.getMethod("getEditorKitClassNameForContentType", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getEditorKitClassNameForContentType55].setDisplayName ( "" );
            methods[METHOD_getEditorKitForContentType56] = new MethodDescriptor(javax.swing.JEditorPane.class.getMethod("getEditorKitForContentType", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getEditorKitForContentType56].setDisplayName ( "" );
            methods[METHOD_getFocusTraversalKeys57] = new MethodDescriptor(java.awt.Container.class.getMethod("getFocusTraversalKeys", new Class[] {int.class})); // NOI18N
            methods[METHOD_getFocusTraversalKeys57].setDisplayName ( "" );
            methods[METHOD_getFontMetrics58] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getFontMetrics", new Class[] {java.awt.Font.class})); // NOI18N
            methods[METHOD_getFontMetrics58].setDisplayName ( "" );
            methods[METHOD_getInsets59] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getInsets", new Class[] {java.awt.Insets.class})); // NOI18N
            methods[METHOD_getInsets59].setDisplayName ( "" );
            methods[METHOD_getKeymap60] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("getKeymap", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getKeymap60].setDisplayName ( "" );
            methods[METHOD_getListeners61] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getListeners", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_getListeners61].setDisplayName ( "" );
            methods[METHOD_getLocation62] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getLocation", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_getLocation62].setDisplayName ( "" );
            methods[METHOD_getMousePosition63] = new MethodDescriptor(java.awt.Container.class.getMethod("getMousePosition", new Class[] {boolean.class})); // NOI18N
            methods[METHOD_getMousePosition63].setDisplayName ( "" );
            methods[METHOD_getPage64] = new MethodDescriptor(javax.swing.JEditorPane.class.getMethod("getPage", new Class[] {})); // NOI18N
            methods[METHOD_getPage64].setDisplayName ( "" );
            methods[METHOD_getPopupLocation65] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getPopupLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getPopupLocation65].setDisplayName ( "" );
            methods[METHOD_getPrintable66] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("getPrintable", new Class[] {java.text.MessageFormat.class, java.text.MessageFormat.class})); // NOI18N
            methods[METHOD_getPrintable66].setDisplayName ( "" );
            methods[METHOD_getPropertyChangeListeners67] = new MethodDescriptor(java.awt.Component.class.getMethod("getPropertyChangeListeners", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getPropertyChangeListeners67].setDisplayName ( "" );
            methods[METHOD_getScrollableBlockIncrement68] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("getScrollableBlockIncrement", new Class[] {java.awt.Rectangle.class, int.class, int.class})); // NOI18N
            methods[METHOD_getScrollableBlockIncrement68].setDisplayName ( "" );
            methods[METHOD_getScrollableUnitIncrement69] = new MethodDescriptor(org.jdesktop.swingx.JXEditorPane.class.getMethod("getScrollableUnitIncrement", new Class[] {java.awt.Rectangle.class, int.class, int.class})); // NOI18N
            methods[METHOD_getScrollableUnitIncrement69].setDisplayName ( "" );
            methods[METHOD_getSize70] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getSize", new Class[] {java.awt.Dimension.class})); // NOI18N
            methods[METHOD_getSize70].setDisplayName ( "" );
            methods[METHOD_getText71] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("getText", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_getText71].setDisplayName ( "" );
            methods[METHOD_getToolTipLocation72] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("getToolTipLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipLocation72].setDisplayName ( "" );
            methods[METHOD_getToolTipText73] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("getToolTipText", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipText73].setDisplayName ( "" );
            methods[METHOD_gotFocus74] = new MethodDescriptor(java.awt.Component.class.getMethod("gotFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_gotFocus74].setDisplayName ( "" );
            methods[METHOD_grabFocus75] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("grabFocus", new Class[] {})); // NOI18N
            methods[METHOD_grabFocus75].setDisplayName ( "" );
            methods[METHOD_handleEvent76] = new MethodDescriptor(java.awt.Component.class.getMethod("handleEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_handleEvent76].setDisplayName ( "" );
            methods[METHOD_hasCommand77] = new MethodDescriptor(org.jdesktop.swingx.JXEditorPane.class.getMethod("hasCommand", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_hasCommand77].setDisplayName ( "" );
            methods[METHOD_hasFocus78] = new MethodDescriptor(java.awt.Component.class.getMethod("hasFocus", new Class[] {})); // NOI18N
            methods[METHOD_hasFocus78].setDisplayName ( "" );
            methods[METHOD_hide79] = new MethodDescriptor(java.awt.Component.class.getMethod("hide", new Class[] {})); // NOI18N
            methods[METHOD_hide79].setDisplayName ( "" );
            methods[METHOD_imageUpdate80] = new MethodDescriptor(java.awt.Component.class.getMethod("imageUpdate", new Class[] {java.awt.Image.class, int.class, int.class, int.class, int.class, int.class})); // NOI18N
            methods[METHOD_imageUpdate80].setDisplayName ( "" );
            methods[METHOD_insets81] = new MethodDescriptor(java.awt.Container.class.getMethod("insets", new Class[] {})); // NOI18N
            methods[METHOD_insets81].setDisplayName ( "" );
            methods[METHOD_inside82] = new MethodDescriptor(java.awt.Component.class.getMethod("inside", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_inside82].setDisplayName ( "" );
            methods[METHOD_invalidate83] = new MethodDescriptor(java.awt.Container.class.getMethod("invalidate", new Class[] {})); // NOI18N
            methods[METHOD_invalidate83].setDisplayName ( "" );
            methods[METHOD_isAncestorOf84] = new MethodDescriptor(java.awt.Container.class.getMethod("isAncestorOf", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isAncestorOf84].setDisplayName ( "" );
            methods[METHOD_isFocusCycleRoot85] = new MethodDescriptor(java.awt.Container.class.getMethod("isFocusCycleRoot", new Class[] {java.awt.Container.class})); // NOI18N
            methods[METHOD_isFocusCycleRoot85].setDisplayName ( "" );
            methods[METHOD_isLightweightComponent86] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("isLightweightComponent", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isLightweightComponent86].setDisplayName ( "" );
            methods[METHOD_keyDown87] = new MethodDescriptor(java.awt.Component.class.getMethod("keyDown", new Class[] {java.awt.Event.class, int.class})); // NOI18N
            methods[METHOD_keyDown87].setDisplayName ( "" );
            methods[METHOD_keyUp88] = new MethodDescriptor(java.awt.Component.class.getMethod("keyUp", new Class[] {java.awt.Event.class, int.class})); // NOI18N
            methods[METHOD_keyUp88].setDisplayName ( "" );
            methods[METHOD_layout89] = new MethodDescriptor(java.awt.Container.class.getMethod("layout", new Class[] {})); // NOI18N
            methods[METHOD_layout89].setDisplayName ( "" );
            methods[METHOD_list90] = new MethodDescriptor(java.awt.Component.class.getMethod("list", new Class[] {})); // NOI18N
            methods[METHOD_list90].setDisplayName ( "" );
            methods[METHOD_list91] = new MethodDescriptor(java.awt.Component.class.getMethod("list", new Class[] {java.io.PrintStream.class})); // NOI18N
            methods[METHOD_list91].setDisplayName ( "" );
            methods[METHOD_list92] = new MethodDescriptor(java.awt.Component.class.getMethod("list", new Class[] {java.io.PrintWriter.class})); // NOI18N
            methods[METHOD_list92].setDisplayName ( "" );
            methods[METHOD_list93] = new MethodDescriptor(java.awt.Container.class.getMethod("list", new Class[] {java.io.PrintStream.class, int.class})); // NOI18N
            methods[METHOD_list93].setDisplayName ( "" );
            methods[METHOD_list94] = new MethodDescriptor(java.awt.Container.class.getMethod("list", new Class[] {java.io.PrintWriter.class, int.class})); // NOI18N
            methods[METHOD_list94].setDisplayName ( "" );
            methods[METHOD_loadKeymap95] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("loadKeymap", new Class[] {javax.swing.text.Keymap.class, javax.swing.text.JTextComponent.KeyBinding[].class, javax.swing.Action[].class})); // NOI18N
            methods[METHOD_loadKeymap95].setDisplayName ( "" );
            methods[METHOD_locate96] = new MethodDescriptor(java.awt.Container.class.getMethod("locate", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_locate96].setDisplayName ( "" );
            methods[METHOD_location97] = new MethodDescriptor(java.awt.Component.class.getMethod("location", new Class[] {})); // NOI18N
            methods[METHOD_location97].setDisplayName ( "" );
            methods[METHOD_lostFocus98] = new MethodDescriptor(java.awt.Component.class.getMethod("lostFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_lostFocus98].setDisplayName ( "" );
            methods[METHOD_minimumSize99] = new MethodDescriptor(java.awt.Container.class.getMethod("minimumSize", new Class[] {})); // NOI18N
            methods[METHOD_minimumSize99].setDisplayName ( "" );
            methods[METHOD_modelToView100] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("modelToView", new Class[] {int.class})); // NOI18N
            methods[METHOD_modelToView100].setDisplayName ( "" );
            methods[METHOD_mouseDown101] = new MethodDescriptor(java.awt.Component.class.getMethod("mouseDown", new Class[] {java.awt.Event.class, int.class, int.class})); // NOI18N
            methods[METHOD_mouseDown101].setDisplayName ( "" );
            methods[METHOD_mouseDrag102] = new MethodDescriptor(java.awt.Component.class.getMethod("mouseDrag", new Class[] {java.awt.Event.class, int.class, int.class})); // NOI18N
            methods[METHOD_mouseDrag102].setDisplayName ( "" );
            methods[METHOD_mouseEnter103] = new MethodDescriptor(java.awt.Component.class.getMethod("mouseEnter", new Class[] {java.awt.Event.class, int.class, int.class})); // NOI18N
            methods[METHOD_mouseEnter103].setDisplayName ( "" );
            methods[METHOD_mouseExit104] = new MethodDescriptor(java.awt.Component.class.getMethod("mouseExit", new Class[] {java.awt.Event.class, int.class, int.class})); // NOI18N
            methods[METHOD_mouseExit104].setDisplayName ( "" );
            methods[METHOD_mouseMove105] = new MethodDescriptor(java.awt.Component.class.getMethod("mouseMove", new Class[] {java.awt.Event.class, int.class, int.class})); // NOI18N
            methods[METHOD_mouseMove105].setDisplayName ( "" );
            methods[METHOD_mouseUp106] = new MethodDescriptor(java.awt.Component.class.getMethod("mouseUp", new Class[] {java.awt.Event.class, int.class, int.class})); // NOI18N
            methods[METHOD_mouseUp106].setDisplayName ( "" );
            methods[METHOD_move107] = new MethodDescriptor(java.awt.Component.class.getMethod("move", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_move107].setDisplayName ( "" );
            methods[METHOD_moveCaretPosition108] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("moveCaretPosition", new Class[] {int.class})); // NOI18N
            methods[METHOD_moveCaretPosition108].setDisplayName ( "" );
            methods[METHOD_nextFocus109] = new MethodDescriptor(java.awt.Component.class.getMethod("nextFocus", new Class[] {})); // NOI18N
            methods[METHOD_nextFocus109].setDisplayName ( "" );
            methods[METHOD_paint110] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("paint", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paint110].setDisplayName ( "" );
            methods[METHOD_paintAll111] = new MethodDescriptor(java.awt.Component.class.getMethod("paintAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintAll111].setDisplayName ( "" );
            methods[METHOD_paintComponents112] = new MethodDescriptor(java.awt.Container.class.getMethod("paintComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintComponents112].setDisplayName ( "" );
            methods[METHOD_paintImmediately113] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("paintImmediately", new Class[] {int.class, int.class, int.class, int.class})); // NOI18N
            methods[METHOD_paintImmediately113].setDisplayName ( "" );
            methods[METHOD_paintImmediately114] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("paintImmediately", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_paintImmediately114].setDisplayName ( "" );
            methods[METHOD_paste115] = new MethodDescriptor(org.jdesktop.swingx.JXEditorPane.class.getMethod("paste", new Class[] {})); // NOI18N
            methods[METHOD_paste115].setDisplayName ( "" );
            methods[METHOD_postEvent116] = new MethodDescriptor(java.awt.Component.class.getMethod("postEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_postEvent116].setDisplayName ( "" );
            methods[METHOD_preferredSize117] = new MethodDescriptor(java.awt.Container.class.getMethod("preferredSize", new Class[] {})); // NOI18N
            methods[METHOD_preferredSize117].setDisplayName ( "" );
            methods[METHOD_prepareImage118] = new MethodDescriptor(java.awt.Component.class.getMethod("prepareImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_prepareImage118].setDisplayName ( "" );
            methods[METHOD_prepareImage119] = new MethodDescriptor(java.awt.Component.class.getMethod("prepareImage", new Class[] {java.awt.Image.class, int.class, int.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_prepareImage119].setDisplayName ( "" );
            methods[METHOD_print120] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("print", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_print120].setDisplayName ( "" );
            methods[METHOD_print121] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("print", new Class[] {})); // NOI18N
            methods[METHOD_print121].setDisplayName ( "" );
            methods[METHOD_print122] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("print", new Class[] {java.text.MessageFormat.class, java.text.MessageFormat.class})); // NOI18N
            methods[METHOD_print122].setDisplayName ( "" );
            methods[METHOD_print123] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("print", new Class[] {java.text.MessageFormat.class, java.text.MessageFormat.class, boolean.class, javax.print.PrintService.class, javax.print.attribute.PrintRequestAttributeSet.class, boolean.class})); // NOI18N
            methods[METHOD_print123].setDisplayName ( "" );
            methods[METHOD_printAll124] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("printAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printAll124].setDisplayName ( "" );
            methods[METHOD_printComponents125] = new MethodDescriptor(java.awt.Container.class.getMethod("printComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printComponents125].setDisplayName ( "" );
            methods[METHOD_putClientProperty126] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("putClientProperty", new Class[] {java.lang.Object.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_putClientProperty126].setDisplayName ( "" );
            methods[METHOD_read127] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("read", new Class[] {java.io.Reader.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_read127].setDisplayName ( "" );
            methods[METHOD_read128] = new MethodDescriptor(javax.swing.JEditorPane.class.getMethod("read", new Class[] {java.io.InputStream.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_read128].setDisplayName ( "" );
            methods[METHOD_registerEditorKitForContentType129] = new MethodDescriptor(javax.swing.JEditorPane.class.getMethod("registerEditorKitForContentType", new Class[] {java.lang.String.class, java.lang.String.class})); // NOI18N
            methods[METHOD_registerEditorKitForContentType129].setDisplayName ( "" );
            methods[METHOD_registerEditorKitForContentType130] = new MethodDescriptor(javax.swing.JEditorPane.class.getMethod("registerEditorKitForContentType", new Class[] {java.lang.String.class, java.lang.String.class, java.lang.ClassLoader.class})); // NOI18N
            methods[METHOD_registerEditorKitForContentType130].setDisplayName ( "" );
            methods[METHOD_registerKeyboardAction131] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("registerKeyboardAction", new Class[] {java.awt.event.ActionListener.class, java.lang.String.class, javax.swing.KeyStroke.class, int.class})); // NOI18N
            methods[METHOD_registerKeyboardAction131].setDisplayName ( "" );
            methods[METHOD_registerKeyboardAction132] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("registerKeyboardAction", new Class[] {java.awt.event.ActionListener.class, javax.swing.KeyStroke.class, int.class})); // NOI18N
            methods[METHOD_registerKeyboardAction132].setDisplayName ( "" );
            methods[METHOD_remove133] = new MethodDescriptor(java.awt.Component.class.getMethod("remove", new Class[] {java.awt.MenuComponent.class})); // NOI18N
            methods[METHOD_remove133].setDisplayName ( "" );
            methods[METHOD_remove134] = new MethodDescriptor(java.awt.Container.class.getMethod("remove", new Class[] {int.class})); // NOI18N
            methods[METHOD_remove134].setDisplayName ( "" );
            methods[METHOD_remove135] = new MethodDescriptor(java.awt.Container.class.getMethod("remove", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_remove135].setDisplayName ( "" );
            methods[METHOD_removeAll136] = new MethodDescriptor(java.awt.Container.class.getMethod("removeAll", new Class[] {})); // NOI18N
            methods[METHOD_removeAll136].setDisplayName ( "" );
            methods[METHOD_removeKeymap137] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("removeKeymap", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_removeKeymap137].setDisplayName ( "" );
            methods[METHOD_removeNotify138] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("removeNotify", new Class[] {})); // NOI18N
            methods[METHOD_removeNotify138].setDisplayName ( "" );
            methods[METHOD_removePropertyChangeListener139] = new MethodDescriptor(java.awt.Component.class.getMethod("removePropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_removePropertyChangeListener139].setDisplayName ( "" );
            methods[METHOD_repaint140] = new MethodDescriptor(java.awt.Component.class.getMethod("repaint", new Class[] {})); // NOI18N
            methods[METHOD_repaint140].setDisplayName ( "" );
            methods[METHOD_repaint141] = new MethodDescriptor(java.awt.Component.class.getMethod("repaint", new Class[] {long.class})); // NOI18N
            methods[METHOD_repaint141].setDisplayName ( "" );
            methods[METHOD_repaint142] = new MethodDescriptor(java.awt.Component.class.getMethod("repaint", new Class[] {int.class, int.class, int.class, int.class})); // NOI18N
            methods[METHOD_repaint142].setDisplayName ( "" );
            methods[METHOD_repaint143] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("repaint", new Class[] {long.class, int.class, int.class, int.class, int.class})); // NOI18N
            methods[METHOD_repaint143].setDisplayName ( "" );
            methods[METHOD_repaint144] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("repaint", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_repaint144].setDisplayName ( "" );
            methods[METHOD_replaceSelection145] = new MethodDescriptor(javax.swing.JEditorPane.class.getMethod("replaceSelection", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_replaceSelection145].setDisplayName ( "" );
            methods[METHOD_requestDefaultFocus146] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("requestDefaultFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestDefaultFocus146].setDisplayName ( "" );
            methods[METHOD_requestFocus147] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("requestFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestFocus147].setDisplayName ( "" );
            methods[METHOD_requestFocus148] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("requestFocus", new Class[] {boolean.class})); // NOI18N
            methods[METHOD_requestFocus148].setDisplayName ( "" );
            methods[METHOD_requestFocusInWindow149] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("requestFocusInWindow", new Class[] {})); // NOI18N
            methods[METHOD_requestFocusInWindow149].setDisplayName ( "" );
            methods[METHOD_resetKeyboardActions150] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("resetKeyboardActions", new Class[] {})); // NOI18N
            methods[METHOD_resetKeyboardActions150].setDisplayName ( "" );
            methods[METHOD_reshape151] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("reshape", new Class[] {int.class, int.class, int.class, int.class})); // NOI18N
            methods[METHOD_reshape151].setDisplayName ( "" );
            methods[METHOD_resize152] = new MethodDescriptor(java.awt.Component.class.getMethod("resize", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_resize152].setDisplayName ( "" );
            methods[METHOD_resize153] = new MethodDescriptor(java.awt.Component.class.getMethod("resize", new Class[] {java.awt.Dimension.class})); // NOI18N
            methods[METHOD_resize153].setDisplayName ( "" );
            methods[METHOD_revalidate154] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("revalidate", new Class[] {})); // NOI18N
            methods[METHOD_revalidate154].setDisplayName ( "" );
            methods[METHOD_scrollRectToVisible155] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("scrollRectToVisible", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_scrollRectToVisible155].setDisplayName ( "" );
            methods[METHOD_scrollToReference156] = new MethodDescriptor(javax.swing.JEditorPane.class.getMethod("scrollToReference", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_scrollToReference156].setDisplayName ( "" );
            methods[METHOD_select157] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("select", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_select157].setDisplayName ( "" );
            methods[METHOD_selectAll158] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("selectAll", new Class[] {})); // NOI18N
            methods[METHOD_selectAll158].setDisplayName ( "" );
            methods[METHOD_setBounds159] = new MethodDescriptor(java.awt.Component.class.getMethod("setBounds", new Class[] {int.class, int.class, int.class, int.class})); // NOI18N
            methods[METHOD_setBounds159].setDisplayName ( "" );
            methods[METHOD_setComponentZOrder160] = new MethodDescriptor(java.awt.Container.class.getMethod("setComponentZOrder", new Class[] {java.awt.Component.class, int.class})); // NOI18N
            methods[METHOD_setComponentZOrder160].setDisplayName ( "" );
            methods[METHOD_setDefaultLocale161] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("setDefaultLocale", new Class[] {java.util.Locale.class})); // NOI18N
            methods[METHOD_setDefaultLocale161].setDisplayName ( "" );
            methods[METHOD_setEditorKitForContentType162] = new MethodDescriptor(javax.swing.JEditorPane.class.getMethod("setEditorKitForContentType", new Class[] {java.lang.String.class, javax.swing.text.EditorKit.class})); // NOI18N
            methods[METHOD_setEditorKitForContentType162].setDisplayName ( "" );
            methods[METHOD_setPage163] = new MethodDescriptor(javax.swing.JEditorPane.class.getMethod("setPage", new Class[] {java.net.URL.class})); // NOI18N
            methods[METHOD_setPage163].setDisplayName ( "" );
            methods[METHOD_show164] = new MethodDescriptor(java.awt.Component.class.getMethod("show", new Class[] {})); // NOI18N
            methods[METHOD_show164].setDisplayName ( "" );
            methods[METHOD_show165] = new MethodDescriptor(java.awt.Component.class.getMethod("show", new Class[] {boolean.class})); // NOI18N
            methods[METHOD_show165].setDisplayName ( "" );
            methods[METHOD_size166] = new MethodDescriptor(java.awt.Component.class.getMethod("size", new Class[] {})); // NOI18N
            methods[METHOD_size166].setDisplayName ( "" );
            methods[METHOD_toString167] = new MethodDescriptor(java.awt.Component.class.getMethod("toString", new Class[] {})); // NOI18N
            methods[METHOD_toString167].setDisplayName ( "" );
            methods[METHOD_transferFocus168] = new MethodDescriptor(java.awt.Component.class.getMethod("transferFocus", new Class[] {})); // NOI18N
            methods[METHOD_transferFocus168].setDisplayName ( "" );
            methods[METHOD_transferFocusBackward169] = new MethodDescriptor(java.awt.Container.class.getMethod("transferFocusBackward", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusBackward169].setDisplayName ( "" );
            methods[METHOD_transferFocusDownCycle170] = new MethodDescriptor(java.awt.Container.class.getMethod("transferFocusDownCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusDownCycle170].setDisplayName ( "" );
            methods[METHOD_transferFocusUpCycle171] = new MethodDescriptor(java.awt.Component.class.getMethod("transferFocusUpCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusUpCycle171].setDisplayName ( "" );
            methods[METHOD_unregisterKeyboardAction172] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("unregisterKeyboardAction", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_unregisterKeyboardAction172].setDisplayName ( "" );
            methods[METHOD_update173] = new MethodDescriptor(javax.swing.JComponent.class.getMethod("update", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_update173].setDisplayName ( "" );
            methods[METHOD_updateUI174] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("updateUI", new Class[] {})); // NOI18N
            methods[METHOD_updateUI174].setDisplayName ( "" );
            methods[METHOD_validate175] = new MethodDescriptor(java.awt.Container.class.getMethod("validate", new Class[] {})); // NOI18N
            methods[METHOD_validate175].setDisplayName ( "" );
            methods[METHOD_viewToModel176] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("viewToModel", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_viewToModel176].setDisplayName ( "" );
            methods[METHOD_write177] = new MethodDescriptor(javax.swing.text.JTextComponent.class.getMethod("write", new Class[] {java.io.Writer.class})); // NOI18N
            methods[METHOD_write177].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods

        // Here you can add code for customizing the methods array.

        return methods;     }//GEN-LAST:Methods
    private static java.awt.Image iconColor16 = null;//GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null;//GEN-END:IconsDef
    private static String iconNameC16 = null;//GEN-BEGIN:Icons
    private static String iconNameC32 = null;
    private static String iconNameM16 = null;
    private static String iconNameM32 = null;//GEN-END:Icons
    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx

//GEN-FIRST:Superclass
    // Here you can add code for customizing the Superclass BeanInfo.
//GEN-LAST:Superclass
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        return getBdescriptor();
    }

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getPdescriptor();
    }

    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return  An array of EventSetDescriptors describing the kinds of
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return getEdescriptor();
    }

    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return  An array of MethodDescriptors describing the methods
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return getMdescriptor();
    }

    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }

    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean.
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }

    /**
     * This method returns an image object that can be used to
     * represent the bean in toolboxes, toolbars, etc.   Icon images
     * will typically be GIFs, but may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from
     * this method.
     * <p>
     * There are four possible flavors of icons (16x16 color,
     * 32x32 color, 16x16 mono, 32x32 mono).  If a bean choses to only
     * support a single icon we recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background
     * so they can be rendered onto an existing background.
     *
     * @param  iconKind  The kind of icon requested.  This should be
     *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32,
     *    ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return  An image object representing the requested icon.  May
     *    return null if no suitable icon is available.
     */
    public java.awt.Image getIcon(int iconKind) {
        switch (iconKind) {
            case ICON_COLOR_16x16:
                if (iconNameC16 == null)
                    return null;
                else {
                    if (iconColor16 == null)
                        iconColor16 = loadImage(iconNameC16);
                    return iconColor16;
                }
            case ICON_COLOR_32x32:
                if (iconNameC32 == null)
                    return null;
                else {
                    if (iconColor32 == null)
                        iconColor32 = loadImage(iconNameC32);
                    return iconColor32;
                }
            case ICON_MONO_16x16:
                if (iconNameM16 == null)
                    return null;
                else {
                    if (iconMono16 == null)
                        iconMono16 = loadImage(iconNameM16);
                    return iconMono16;
                }
            case ICON_MONO_32x32:
                if (iconNameM32 == null)
                    return null;
                else {
                    if (iconMono32 == null)
                        iconMono32 = loadImage(iconNameM32);
                    return iconMono32;
                }
            default:
                return null;
        }
    }
}
