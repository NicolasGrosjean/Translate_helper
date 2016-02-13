package jlanguagetool;


/**
 *
 * @author Panagiotis Minos
 */
class LanguageToolEvent {

  public enum Type {
    CHECKING_STARTED,
    CHECKING_FINISHED,
    LANGUAGE_CHANGED,
    RULE_DISABLED,
    RULE_ENABLED
  }
  
  private final LanguageToolSupport source;
  private final Type type;
  private final Object caller;

  LanguageToolEvent(LanguageToolSupport source, Type type, Object caller) {
    this.source = source;
    this.type = type;
    this.caller = caller;
  }

  LanguageToolSupport getSource() {
    return source;
  }

  Object getCaller() {
    return caller;
  }

  Type getType() {
    return type;
  }
}

