package jlanguagetool;

import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.languagetool.JLanguageTool;
import org.languagetool.Languages;

public class DemoFrame extends JFrame {

    public DemoFrame() {

        JScrollPane jScrollPane = new javax.swing.JScrollPane();
        JTextArea jTextArea = new javax.swing.JTextArea("This is a a samplee.");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea.setColumns(20);
        jTextArea.setRows(5);
        jScrollPane.setViewportView(jTextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                        .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                        .addContainerGap())
        );

        pack();

//        LanguageToolSupport lt = new MyLanguageToolSupport(this, jTextArea);
        new LanguageToolSupport(jTextArea, 
        		new UndoRedoSupport(jTextArea, JLanguageTool.getMessageBundle()),
        		Languages.getLanguageForLocale(new Locale("fr", "FR")));
        // TODO : BUG if we correct with right click, we cannot undo without change the text
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DemoFrame().setVisible(true);
            }
        });
    }

}
