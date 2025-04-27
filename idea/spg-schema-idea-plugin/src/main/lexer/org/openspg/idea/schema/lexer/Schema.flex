package org.openspg.idea.lang.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

/* Auto generated File */
%%

%class SchemaLexer
%implements com.intellij.lexer.FlexLexer, org.openspg.idea.grammar.psi.SchemaTypes
%unicode
%public
%column

%function advance
%type IElementType

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// USER CODE //////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

%{
    /**
     * The number of open but not closed braces.
     * Note: lexer does not distinguish braces from brackets while counting them.
     */
    private int myBraceCount = 0;

    private final int[] indentPos = {0, 0, 0, 0, 0, 0};
    private final int[] indentState = {ENTITY_STATE, ENTITYMETA_STATE, PROPERTY_STATE, PROPERTYMETA_STATE, PROPERTY_STATE, PROPERTYMETA_STATE};
    private final IElementType[] indentToken = {INDENT, INDENT_META, INDENT_PROP, INDENT_PROPMETA, INDENT_SUBPROP, INDENT_SUBPROPMETA};
    private final int maxIndentLevel = 6;
    private int currentIndentLevel = 0;

    //-------------------------------------------------------------------------------------------------------------------

    /** @param offset offset from currently matched token start (could be negative) */
    private char getCharAtOffset(final int offset) {
        final int loc = getTokenStart() + offset;
        return 0 <= loc && loc < zzBuffer.length() ? zzBuffer.charAt(loc) : (char) -1;
    }

    private boolean isAfterEol() {
        final char prev = getCharAtOffset(-1);
        return prev == (char)-1 || prev == '\n';
    }

    private int getIndentLevel() {
        assert isAfterEol();
        int currentIndentPos = yylength();
        for (int i = 0; i < yylength(); i+=1) {
            if (getCharAtOffset(i) == '\t') {
                currentIndentPos += 1;
            }
        }

        int lastIndentPos = this.indentPos[this.currentIndentLevel];
        if (currentIndentPos > lastIndentPos) {
            if (this.currentIndentLevel == maxIndentLevel) {
                return -1;
            }
            this.currentIndentLevel ++;
            this.indentPos[this.currentIndentLevel] = currentIndentPos;
            return this.currentIndentLevel;

        } else if (currentIndentPos < lastIndentPos) {
            for (int i = 0; i < this.currentIndentLevel; i++) {
                if (this.indentPos[i] == currentIndentPos) {
                    this.currentIndentLevel = i;
                    return this.currentIndentLevel;
                }
            }
            return -1;
        }
        return this.currentIndentLevel;
    }

    //-------------------------------------------------------------------------------------------------------------------
    private void openBrace() {
        myBraceCount++;
        if (myBraceCount != 0) {
            yybegin(PLAIN_BLOCK_STATE);
        }
    }

    private void closeBrace() {
        if (myBraceCount > 0) {
            myBraceCount--;
        }
        if (myBraceCount == 0){
            yybegin(this.indentState[this.currentIndentLevel]);
        }
    }

    private void goToState(int state) {
        yybegin(state);
        yypushback(yylength());
    }

    //-------------------------------------------------------------------------------------------------------------------
    private void trace(String tag) {
        int tokenStart = getTokenStart();
        int tokenEnd = Math.min(tokenStart + 40, zzBuffer.length());
        System.out.println("====" + tag + "\n{{ " + zzBuffer.subSequence(tokenStart, tokenEnd) + " }}");
    }
%}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////// REGEXPS DECLARATIONS //////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// NB !(!a|b) is "a - b"
// From the spec
ANY_CHAR = [^]

NS_CHAR = [^\n\t\r\ ]
NS_INDICATOR = [-?:,\(\)\[\]\{\}#&*!|>'\"%@`]

EOL =                           "\n"
WHITE_SPACE_CHAR =              [ \t]
WHITE_SPACE =                   {WHITE_SPACE_CHAR}+

NAME =                          [\w]+

LINE =                          [^\n]*

// Schema spec: when a comment follows another syntax element,
//  it must be separated from it by space characters.
// See http://www.yaml.org/spec/1.2/spec.html#comment
COMMENT =                       "#"{LINE}

ESCAPE_SEQUENCE=                \\[^\n]
DSTRING =                       \"([^\\\"]|{ESCAPE_SEQUENCE}|\\\n)*\"
STRING =                        '([^']|'')*'

TEXT =                          {DSTRING}|{STRING}|{NAME}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////// STATES DECLARATIONS //////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Main states
%xstate LINE_START_STATE, BLOCK_STATE, PLAIN_BLOCK_STATE

// Small technical one-token states
%xstate NAMESPACE_STATE, LINE_COMMENT_STATE, ERROR_STATE

%xstate ENTITY_STATE, WAITING_ENTITY_ALIAS_NAME_STATE, WAITING_ENTITY_CLASS_STATE
%xstate ENTITYMETA_STATE

%xstate PROPERTY_STATE, WAITING_PROPERTY_ALIAS_NAME_STATE, WAITING_PROPERTY_CLASS_STATE
%xstate PROPERTYMETA_STATE

%xstate WAITING_META_VALUE_STATE, WAITING_META_BUILTIN_VALUE_STATE, WAITING_META_TEXT_VALUE_STATE

%%
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////// RULES declarations ////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//-------------------------------------------------------------------------------------------------------------------
// State in the start of new line in block mode
<YYINITIAL, LINE_START_STATE> {
    // It is a text, go next state and process it there
    "namespace" {
          this.currentIndentLevel = 0;
          goToState(NAMESPACE_STATE);
      }

    {WHITE_SPACE}* {COMMENT} {
          goToState(LINE_COMMENT_STATE);
      }

    {EOL} {
          return EOL;
      }

    {WHITE_SPACE} {
          int indentLevel = this.getIndentLevel();
          if (indentLevel < 0) {
              goToState(ERROR_STATE);
          } else {
              yybegin(this.indentState[indentLevel]);
              yypushback(yylength() - 1);
              return this.indentToken[indentLevel];
          }
      }

    {ANY_CHAR} {
          this.currentIndentLevel = 0;
          goToState(ENTITY_STATE);
      }
}
//-------------------------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------------------------
// common: white-space, eol, comment
<NAMESPACE_STATE, ENTITY_STATE, ENTITYMETA_STATE, PROPERTY_STATE, PROPERTYMETA_STATE, ERROR_STATE> {
    {EOL} {
          yybegin(LINE_START_STATE);
          return EOL;
      }

    {COMMENT} {
          return COMMENT;
      }

    {WHITE_SPACE} {
          return TokenType.WHITE_SPACE;
      }
}

<LINE_COMMENT_STATE> {
    {EOL} {
          yybegin(LINE_START_STATE);
          return EOL;
      }

    {COMMENT} {
          return LINE_COMMENT;
      }

    {WHITE_SPACE} {
          return TokenType.WHITE_SPACE;
      }
}
//-------------------------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------------------------
// namespace
<NAMESPACE_STATE> {
    ("namespace") {
        return NAMESPACE_MARKER;
    }

    {TEXT} {
          return TEXT;
      }
}
//-------------------------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------------------------
// common error line
<ERROR_STATE> {
    {ANY_CHAR} {
          return TokenType.BAD_CHARACTER;
      }
}
//-------------------------------------------------------------------------------------------------------------------


// common waiting state
<WAITING_ENTITY_ALIAS_NAME_STATE, WAITING_ENTITY_CLASS_STATE, WAITING_PROPERTY_ALIAS_NAME_STATE, WAITING_PROPERTY_CLASS_STATE, WAITING_META_VALUE_STATE> {
    {WHITE_SPACE}*{EOL} {
          yybegin(LINE_START_STATE);
          return EOL;
      }

    {COMMENT} {
          return COMMENT;
      }

    {WHITE_SPACE} {
          return TokenType.WHITE_SPACE;
      }
}
//-------------------------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------------------------
// level-1 entity
<ENTITY_STATE> {
    [a-zA-Z0-9\.]+ {
          return ENTITY_NAME;
      }

    "(" {TEXT} ")" {
          goToState(WAITING_ENTITY_ALIAS_NAME_STATE);
      }

    [^\n] {
          return TokenType.BAD_CHARACTER;
      }
}

<WAITING_ENTITY_ALIAS_NAME_STATE> {
    "(" {
          return OPEN_BRACKET;
      }

    ")" {
          yybegin(WAITING_ENTITY_CLASS_STATE);
          return CLOSE_BRACKET;
      }

    {TEXT} {
          return ENTITY_ALIAS_NAME;
      }
}

<WAITING_ENTITY_CLASS_STATE> {
    "EntityType" | "ConceptType" | "EventType" | "StandardType" | "BasicType" {
          return ENTITY_BUILTIN_CLASS;
      }

    "," {
          return COMMA;
      }

    ":" {
          return COLON;
      }

    "->" {
          return INHERITED;
      }

    [a-zA-Z0-9]+ {
          return ENTITY_CLASS;
      }

    [^\n] {
          return TokenType.BAD_CHARACTER;
      }
}
//-------------------------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------------------------
// level-2 entity meta
<ENTITYMETA_STATE> {
    "desc" | "properties" | "relations" | "hypernymPredicate" | "regular" | "spreadable" | "autoRelate" {
          return META_TYPE;
      }

    ":" {
          yybegin(WAITING_META_VALUE_STATE);
          return COLON;
      }

    [^\n] {
          return TokenType.BAD_CHARACTER;
      }
}
//-------------------------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------------------------
// level-3/5 property/subproperty
<PROPERTY_STATE> {
    [a-zA-Z0-9#]+ {
          return PROPERTY_NAME;
      }

    "(" {TEXT} ")" {
          goToState(WAITING_PROPERTY_ALIAS_NAME_STATE);
      }

    [^\n] {
          return TokenType.BAD_CHARACTER;
      }
}

<WAITING_PROPERTY_ALIAS_NAME_STATE> {
    "(" {
          return OPEN_BRACKET;
      }

    ")" {
          yybegin(WAITING_PROPERTY_CLASS_STATE);
          return CLOSE_BRACKET;
      }

    {TEXT} {
          return PROPERTY_ALIAS_NAME;
      }
}

<WAITING_PROPERTY_CLASS_STATE> {
    "EntityType" | "ConceptType" | "EventType" | "StandardType" | "Integer" | "Text" | "Float" {
          return BUILTIN_TYPE;
      }

    "," {
          return COMMA;
      }

    ":" {
          return COLON;
      }

    [a-zA-Z0-9\.]+ {
          return PROPERTY_CLASS;
      }

    [^\n] {
          return TokenType.BAD_CHARACTER;
      }
}
//-------------------------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------------------------
// level-4/6 property meta/subproperty meta
<PROPERTYMETA_STATE> {
    "desc" | "properties" | "constraint" | "rule" | "index" {
          return META_TYPE;
      }

    ":" {
          yybegin(WAITING_META_VALUE_STATE);
          return COLON;
      }

    [^\n] {
          return TokenType.BAD_CHARACTER;
      }
}
//-------------------------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------------------------
// common meta value
<WAITING_META_VALUE_STATE, WAITING_META_BUILTIN_VALUE_STATE, WAITING_META_TEXT_VALUE_STATE> {
    {EOL} {
          yybegin(LINE_START_STATE);
          return EOL;
      }

    {WHITE_SPACE} {
          return TokenType.WHITE_SPACE;
      }

    {COMMENT} {
          return COMMENT;
      }
}
<WAITING_META_VALUE_STATE> {
    "[[" {
          yybegin(PLAIN_BLOCK_STATE);
          return OPEN_PLAIN_BLOCK;
      }

    ("isA"|"locateAt"|"mannerOf"|"Text"|"Vector"|"TextAndVector"|"NotNull"|"MultiValue") {WHITE_SPACE}* {EOL} {
          goToState(WAITING_META_BUILTIN_VALUE_STATE);
      }

    [^#\n] {
          goToState(WAITING_META_TEXT_VALUE_STATE);
      }
}

<WAITING_META_BUILTIN_VALUE_STATE> {
    {TEXT} {
          return BUILTIN_TYPE;
      }
}

<WAITING_META_TEXT_VALUE_STATE> {
    [^#\n]+ {
          //return TokenType.BAD_CHARACTER;
          return TEXT;
      }
}

//-------------------------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------------------------
// plain block. [[plain text]]
<PLAIN_BLOCK_STATE> {
    {WHITE_SPACE}*{EOL} {
          return EOL;
      }

    {WHITE_SPACE} {
          return TokenType.WHITE_SPACE;
      }

    "]]" {
          closeBrace();
          return CLOSE_PLAIN_BLOCK;
      }

    "]" {
          return TEXT;
      }

    [^\n\]]+ {
          return TEXT;
      }
}
