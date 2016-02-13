package jlanguagetool;

import javax.swing.JFrame;
import javax.swing.text.JTextComponent;

/**
 *
 * @author pminos
 */
public class MyLanguageToolSupport extends LanguageToolSupport {

    public MyLanguageToolSupport(JFrame frame, JTextComponent textComponent) {
        super(frame, textComponent);
    }
    
    public MyLanguageToolSupport(JFrame frame, JTextComponent textComponent, UndoRedoSupport support) {
        super(frame, textComponent, support);
    }
}