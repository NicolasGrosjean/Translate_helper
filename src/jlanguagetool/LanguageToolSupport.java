package jlanguagetool;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

import org.apache.commons.lang.StringUtils;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.MultiThreadedJLanguageTool;
import org.languagetool.gui.Configuration;
import org.languagetool.gui.ConfigurationDialog;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.ITSIssueType;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;

/**
 * Support for associating a LanguageTool instance and a JTextComponent
 *
 * @author Panagiotis Minos
 * @since 2.3
 */
class LanguageToolSupport {

  static final String CONFIG_FILE = ".languagetool.cfg";
  //maximum entries in the activate rule menu.
  //If entries' number is bigger, create per category submenus
  //can set to 0 to always create category submenus
  private static final int MAX_RULES_NO_CATEGORY_MENU = 12;
  //maximum rule menu entries, if more create a More submenu
  private static final int MAX_RULES_PER_MENU = 12;
  //maximum category menu entries, if more create a More submenu
  private static final int MAX_CATEGORIES_PER_MENU = 12;

  private final JFrame frame;
  private final JTextComponent textComponent;
  private final EventListenerList listenerList = new EventListenerList();
  private final ResourceBundle messages;
  private final Map<Language, ConfigurationDialog> configDialogs = new HashMap<>();
  private final List<RuleMatch> ruleMatches;
  private final List<Span> documentSpans;

  private JLanguageTool languageTool;
  private HighlightPainter redPainter;  // a red color highlight painter for marking spelling errors  
  private HighlightPainter bluePainter;  // a blue color highlight painter for marking grammar errors
  private ScheduledExecutorService checkExecutor;
  private MouseListener mouseListener;
  private ActionListener actionListener;
  private int millisecondDelay = 1500;
  private AtomicInteger check;
  private boolean popupMenuEnabled = true;
  private boolean backgroundCheckEnabled = true;
  private Configuration config;
  private final UndoRedoSupport undo;

  /**
   * LanguageTool support for a JTextComponent
   */
  public LanguageToolSupport(JFrame frame, JTextComponent textComponent) {
    this(frame, textComponent, null);
  }

  /**
   * LanguageTool support for a JTextComponent
   * 
   * @since 2.7
   */
  public LanguageToolSupport(JFrame frame, JTextComponent textComponent, UndoRedoSupport support) {
    this.frame = frame;
    this.textComponent = textComponent;
    this.messages = JLanguageTool.getMessageBundle();
    ruleMatches = new ArrayList<>();
    documentSpans = new ArrayList<>();    
    this.undo = support;
    init();
  }

  JTextComponent getTextComponent() {
    return textComponent;
  }

  List<RuleMatch> getMatches() {
    return this.ruleMatches;
  }

  ConfigurationDialog getCurrentConfigDialog() {
    Language language = this.languageTool.getLanguage();
    final ConfigurationDialog configDialog;
    if (configDialogs.containsKey(language)) {
      configDialog = configDialogs.get(language);
    } else {
      configDialog = new ConfigurationDialog(frame, false, config);
      configDialogs.put(language, configDialog);
    }
    return configDialog;
  }

  void reloadConfig() {
    //FIXME
    //if mother tongue changes then create new JLanguageTool instance

    boolean update = false;
  
    Set<String> disabledRules = config.getDisabledRuleIds();
    if (disabledRules == null) {
      disabledRules = Collections.emptySet();
    }

    Set<String> common = new HashSet<>(disabledRules);
    common.retainAll(languageTool.getDisabledRules());
    Set<String> toDisable = new HashSet<>(disabledRules);
    toDisable.removeAll(common);
    Set<String> toEnable = new HashSet<>(languageTool.getDisabledRules());
    toEnable.removeAll(common);
    
    for (final String ruleId : toDisable) {
      languageTool.disableRule(ruleId);
      update = true;
    }
    for (final String ruleId : toEnable) {
      languageTool.enableRule(ruleId);
      update = true;
    }

    Set<String> disabledCategories = config.getDisabledCategoryNames();
    if (disabledCategories == null) {
      disabledCategories = Collections.emptySet();
    }
    common = new HashSet<>(disabledCategories);
    common.retainAll(languageTool.getDisabledCategories());
    toDisable = new HashSet<>(disabledCategories);
    toDisable.removeAll(common);
    toEnable = new HashSet<>(languageTool.getDisabledCategories());
    toEnable.removeAll(common);

    if(!toDisable.isEmpty()) {
      languageTool.getDisabledCategories().addAll(toDisable);
      // ugly hack to trigger reInitSpellCheckIgnoreWords()
      languageTool.disableRules(new ArrayList<String>());
      update = true;
    }
    if(!toEnable.isEmpty()) {
      languageTool.getDisabledCategories().removeAll(toEnable);
      // ugly hack to trigger reInitSpellCheckIgnoreWords()
      languageTool.disableRules(new ArrayList<String>());
      update = true;
    }

    Set<String> enabledRules = config.getEnabledRuleIds();
    if (enabledRules == null) {
      enabledRules = Collections.emptySet();
    }
    for (String ruleName : enabledRules) {
      languageTool.enableDefaultOffRule(ruleName);
      languageTool.enableRule(ruleName);
    }

    if(update) {
      //FIXME
      //we could skip a full check if the user disabled but didn't enable rules
      checkImmediately(null);
    }
  }

  private void loadConfig() {
    final Set<String> disabledRules = config.getDisabledRuleIds();
    if (disabledRules != null) {
      for (final String ruleId : disabledRules) {
        languageTool.disableRule(ruleId);
      }
    }
    final Set<String> disabledCategories = config.getDisabledCategoryNames();
    if (disabledCategories != null) {
      for (final String categoryName : disabledCategories) {
        languageTool.disableCategory(categoryName);
      }
    }
    final Set<String> enabledRules = config.getEnabledRuleIds();
    if (enabledRules != null) {
      for (String ruleName : enabledRules) {
        languageTool.enableDefaultOffRule(ruleName);
        languageTool.enableRule(ruleName);
      }
    }
  }

  private void reloadLanguageTool(Language language) {
    try {
      //FIXME
      //no need to read again the file
      config = new Configuration(new File(System.getProperty("user.home")), CONFIG_FILE, language);
      //config still contains old language, update it
      this.config.setLanguage(language);
      languageTool = new MultiThreadedJLanguageTool(language, config.getMotherTongue());
      loadConfig();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void init() {
    try {
      config = new Configuration(new File(System.getProperty("user.home")), CONFIG_FILE, null);
    } catch (IOException ex) {
      throw new RuntimeException("Could not load configuration", ex);
    }

    Language defaultLanguage = config.getLanguage();
    if(defaultLanguage == null) {
        defaultLanguage = Languages.getLanguageForLocale(Locale.getDefault());
    }

    /**
     * Warm-up: we have a lot of lazy init in LT, which causes the first check to
     * be very slow (several seconds) for languages with a lot of data and a lot of
     * rules. We just assume that the default language is the language that the user
     * often uses and init the LT object for that now, not just when it's first used.
     * This makes the first check feel much faster:
     */    
    reloadLanguageTool(defaultLanguage);

    redPainter = new HighlightPainter(Color.red);
    bluePainter = new HighlightPainter(Color.blue);

    checkExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
      @Override
      public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        t.setName(t.getName() + "-lt-background");
        return t;
      }
    });

    check = new AtomicInteger(0);

    this.textComponent.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        recalculateSpans(e.getOffset(), e.getLength(), false);
        if (backgroundCheckEnabled) {
          checkDelayed(null);
        }
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        recalculateSpans(e.getOffset(), e.getLength(), true);
        if (backgroundCheckEnabled) {
          checkDelayed(null);
        }
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        if (backgroundCheckEnabled) {
          checkDelayed(null);
        }
      }
    });

    mouseListener = new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent me) {
      }

      @Override
      public void mousePressed(MouseEvent me) {
        if (me.isPopupTrigger()) {
          showPopup(me);
        }
      }

      @Override
      public void mouseReleased(MouseEvent me) {
        if (me.isPopupTrigger()) {
          showPopup(me);
        }
      }

      @Override
      public void mouseEntered(MouseEvent me) {}
      @Override
      public void mouseExited(MouseEvent me) {}
    };
    this.textComponent.addMouseListener(mouseListener);

    actionListener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        _actionPerformed(e);
      }
    };

    if (!this.textComponent.getText().isEmpty() && backgroundCheckEnabled) {
      checkImmediately(null);
    }
  }

  public int getMillisecondDelay() {
    return millisecondDelay;
  }

  /**
   * The text checking delay in milliseconds.
   */
  public void setMillisecondDelay(int millisecondDelay) {
    this.millisecondDelay = millisecondDelay;
  }

  public boolean isPopupMenuEnabled() {
    return popupMenuEnabled;
  }

  public void setPopupMenuEnabled(boolean popupMenuEnabled) {
    if (this.popupMenuEnabled == popupMenuEnabled) {
      return;
    }
    this.popupMenuEnabled = popupMenuEnabled;
    if (popupMenuEnabled) {
      textComponent.addMouseListener(mouseListener);
    } else {
      textComponent.removeMouseListener(mouseListener);
    }
  }

  public boolean isBackgroundCheckEnabled() {
    return backgroundCheckEnabled;
  }

  public void setBackgroundCheckEnabled(boolean backgroundCheckEnabled) {
    if (this.backgroundCheckEnabled == backgroundCheckEnabled) {
      return;
    }
    this.backgroundCheckEnabled = backgroundCheckEnabled;
    if (backgroundCheckEnabled) {
      checkImmediately(null);
    }
  }

  public void setLanguage(Language language) {
    reloadLanguageTool(language);
    if (backgroundCheckEnabled) {
      checkImmediately(null);
    }
  }

  Language getLanguage() {
      return this.languageTool.getLanguage();
  }

  public Configuration getConfig() {
    return config;
  }

  // called from Main.showOptions() and Main.tagTextAndDisplayResults()
  JLanguageTool getLanguageTool() {
    return languageTool;
  }
  private Span getSpan(int offset) {
    for (final Span cur : documentSpans) {
      if (cur.end > cur.start && cur.start <= offset && offset < cur.end) {
        return cur;
      }
    }
    return null;
  }

  private void showPopup(MouseEvent event) {
    if(documentSpans.isEmpty() && languageTool.getDisabledRules().isEmpty()) {
      //No errors and no disabled Rules
      return;
    }

    int offset = this.textComponent.viewToModel(event.getPoint());
    final Span span = getSpan(offset);
    JPopupMenu popup = new JPopupMenu("Grammar Menu");
    if (span != null) {
      JLabel msgItem = new JLabel("<html>"
              + span.msg.replace("<suggestion>", "<b>").replace("</suggestion>", "</b>")
              + "</html>");
      msgItem.setToolTipText(
              span.desc.replace("<suggestion>", "").replace("</suggestion>", ""));
      msgItem.setBorder(new JMenuItem().getBorder());
      popup.add(msgItem);

      popup.add(new JSeparator());

      for (String r : span.replacement) {
        ReplaceMenuItem item = new ReplaceMenuItem(r, span);
        popup.add(item);
        item.addActionListener(actionListener);
      }

      popup.add(new JSeparator());

      JMenuItem moreItem = new JMenuItem(messages.getString("guiMore"));
      moreItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          showDialog(textComponent, span.msg, span.desc, span.rule);
        }
      });
      popup.add(moreItem);
    }

    if (span != null) {
      textComponent.setCaretPosition(span.start);
      textComponent.moveCaretPosition(span.end);
    }

    popup.addPopupMenuListener(new PopupMenuListener() {
      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
        if(span != null) {
          textComponent.setCaretPosition(span.start);
        }
      }
    });
    popup.show(textComponent, event.getPoint().x, event.getPoint().y);

  }

  private void _actionPerformed(ActionEvent e) {
    ReplaceMenuItem src = (ReplaceMenuItem) e.getSource();

    this.documentSpans.remove(src.span);
    applySuggestion(e.getActionCommand(), src.span.start, src.span.end);
  }

  private void applySuggestion(String str, int start, int end) {
    if (end < start) {
      throw new IllegalArgumentException("end before start: " + end + " < " + start);
    }
    Document doc = this.textComponent.getDocument();
    if (doc != null) {
      try {
        if(this.undo != null) {
          this.undo.startCompoundEdit();
        }
        if (doc instanceof AbstractDocument) {
          ((AbstractDocument) doc).replace(start, end - start, str, null);
        } else {
          doc.remove(start, end - start);
          doc.insertString(start, str, null);
        }
      } catch (BadLocationException e) {
        throw new IllegalArgumentException(e);
      } finally {
        if(this.undo != null) {
          this.undo.endCompoundEdit();
        }
      }
    }
  }

  public void checkDelayed() {
    checkDelayed(null);
  }

  public void checkDelayed(Object caller) {
    check.getAndIncrement();
    checkExecutor.schedule(new RunnableImpl(caller), millisecondDelay, TimeUnit.MILLISECONDS);
  }

  public void checkImmediately() {
    checkImmediately(null);
  }

  public void checkImmediately(Object caller) {
    check.getAndIncrement();
    checkExecutor.schedule(new RunnableImpl(caller), 0, TimeUnit.MILLISECONDS);
  }

  private synchronized List<RuleMatch> checkText(final Object caller) throws IOException {
    final List<RuleMatch> matches = this.languageTool.check(this.textComponent.getText());
    int v = check.get();
    if (v == 0) {
      if (!SwingUtilities.isEventDispatchThread()) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            updateHighlights(matches);
          }
        });
      } else {
        updateHighlights(matches);
      }
    }
    return matches;
  }

  private void removeHighlights() {
    for (Highlighter.Highlight hl : textComponent.getHighlighter().getHighlights()) {
      if (hl.getPainter() == redPainter || hl.getPainter() == bluePainter) {
        textComponent.getHighlighter().removeHighlight(hl);
      }
    }
  }

  private void recalculateSpans(int offset, int length, boolean remove) {
    if (length == 0) {
      return;
    }
    for (Span span : this.documentSpans) {
      if (offset >= span.end) {
        continue;
      }
      if (!remove) {
        if (offset <= span.start) {
          span.start += length;
        }
        span.end += length;
      } else {
        if (offset + length <= span.end) {
          if (offset > span.start) {
            //
          } else if (offset + length <= span.start) {
            span.start -= length;
          } else {
            span.start = offset;
          }
          span.end -= length;
        } else {
          span.end -= Math.min(length, span.end - offset);
        }
      }
    }
    updateHighlights();
  }

  private void updateHighlights(List<RuleMatch> matches) {
    List<Span> spans = new ArrayList<>();
    for (RuleMatch match : matches) {
      spans.add(new Span(match));
    }
    prepareUpdateHighlights(matches, spans);
  }

  private void prepareUpdateHighlights(List<RuleMatch> matches, List<Span> spans) {
    ruleMatches.clear();
    documentSpans.clear();
    ruleMatches.addAll(matches);
    documentSpans.addAll(spans);
    updateHighlights();
  }

  private void updateHighlights() {
    removeHighlights();

    Highlighter h = textComponent.getHighlighter();
    List<Span> spellErrors = new ArrayList<>();
    List<Span> grammarErrors = new ArrayList<>();

    for (Span span : documentSpans) {
      if (span.start == span.end) {
        continue;
      }
      if (ITSIssueType.Misspelling.equals(span.rule.getLocQualityIssueType())) {
        spellErrors.add(span);
      } else {
        grammarErrors.add(span);
      }
    }

    for (Span span : grammarErrors) {
      try {
        if (span.start < span.end) { //to avoid the BadLocationException
          h.addHighlight(span.start, span.end, bluePainter);
        }
      } catch (BadLocationException ex) {
        ex.printStackTrace();
      }
    }
    for (Span span : spellErrors) {
      try {
        if (span.start < span.end) { //to avoid the BadLocationException
          h.addHighlight(span.start, span.end, redPainter);
        }
      } catch (BadLocationException ex) {
        ex.printStackTrace();
      }
    }
  }

  private void showDialog(Component parent, String title, String message, Rule rule) {
//    Tools.showRuleInfoDialog(parent, title, message, rule, messages, languageTool.getLanguage().getShortNameWithCountryAndVariant());
  }

  private static class HighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

    private static final BasicStroke OO_STROKE1 = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]{3.0f, 5.0f}, 2);
    private static final BasicStroke OO_STROKE2 = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]{1.0f, 3.0f}, 3);
    private static final BasicStroke OO_STROKE3 = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]{3.0f, 5.0f}, 6);
    private static final BasicStroke ZIGZAG_STROKE1 = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]{1.0f, 1.0f}, 0);

    private HighlightPainter(Color color) {
      super(color);
    }

    @Override
    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
      Rectangle rect;

      if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
        if (bounds instanceof Rectangle) {
          rect = (Rectangle) bounds;
        } else {
          rect = bounds.getBounds();
        }
      } else {
        try {
          Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
          rect = shape instanceof Rectangle ? (Rectangle) shape : shape.getBounds();
        } catch (BadLocationException e) {
          rect = null;
        }
      }

      if (rect != null) {
        Color color = getColor();

        if (color == null) {
          g.setColor(c.getSelectionColor());
        } else {
          g.setColor(color);
        }

        rect.width = Math.max(rect.width, 1);

        int descent = c.getFontMetrics(c.getFont()).getDescent();

        if (descent > 3) {
          drawCurvedLine(g, rect);
        } else if (descent > 2) {
          drawCurvedLine(g, rect);
        } else {
          drawLine(g, rect);
        }
      }

      return rect;
    }

    private void drawCurvedLine(Graphics g, Rectangle rect) {
      int x1 = rect.x;
      int x2 = rect.x + rect.width;
      int y = rect.y + rect.height;
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setStroke(OO_STROKE1);
      g2.drawLine(x1, y - 1, x2, y - 1);
      g2.setStroke(OO_STROKE2);
      g2.drawLine(x1, y - 2, x2, y - 2);
      g2.setStroke(OO_STROKE3);
      g2.drawLine(x1, y - 3, x2, y - 3);
    }

    private void drawLine(Graphics g, Rectangle rect) {
      int x1 = rect.x;
      int x2 = rect.x + rect.width;
      int y = rect.y + rect.height;
      Graphics2D g2 = (Graphics2D) g;
      g2.setStroke(ZIGZAG_STROKE1);
      g2.drawLine(x1, y - 1, x2, y - 1);
    }
  }

  private static class ReplaceMenuItem extends JMenuItem {

    private final Span span;

    private ReplaceMenuItem(String name, Span span) {
      super(name);
      this.span = span;
    }
  }

  private static class Span {

    private int start;
    private int end;
    private final String msg;
    private final String desc;
    private final List<String> replacement;
    private final Rule rule;

    private Span(RuleMatch match) {
      start = match.getFromPos();
      end = match.getToPos();
      String tmp = match.getShortMessage();
      if (StringUtils.isEmpty(tmp)) {
        tmp = match.getMessage();
      }
      msg = tmp;//Tools.shortenComment(tmp);
      desc = match.getMessage();
      replacement = new ArrayList<>();
      replacement.addAll(match.getSuggestedReplacements());
      rule = match.getRule();
    }
  }

  private class RunnableImpl implements Runnable {

    private final Object caller;

    private RunnableImpl(Object caller) {
      this.caller = caller;
    }

    @Override
    public void run() {
      int v = check.decrementAndGet();
      if (v != 0) {
        return;
      }
      try {
        checkText(caller);
      } catch (Exception ex) {
//        Tools.showError(ex);
      }
    }
  }
}
