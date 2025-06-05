package org.openspg.idea.schema.lexer;

import com.intellij.lexer.FlexAdapter;
import org.openspg.idea.schema.lang.lexer.SchemaLexer;

public class SchemaLexerAdapter extends FlexAdapter {

    public SchemaLexerAdapter() {
        super(new SchemaLexer(null));
    }

}
