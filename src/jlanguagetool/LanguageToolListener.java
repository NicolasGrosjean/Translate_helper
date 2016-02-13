package jlanguagetool;

import java.util.EventListener;

/**
 * Interface for an observer to receive notifications
 *
 * @author Panagiotis Minos
 */
interface LanguageToolListener extends EventListener {

  public void languageToolEventOccurred(LanguageToolEvent event);
}
