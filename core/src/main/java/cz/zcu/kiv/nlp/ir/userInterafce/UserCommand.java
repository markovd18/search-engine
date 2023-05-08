package cz.zcu.kiv.nlp.ir.userInterafce;

/**
 * Specifies different commands that may come from the user.
 */
public enum UserCommand {
  /**
   * Quit application.
   */
  EXIT,
  /**
   * Query the index for documents.
   */
  QUERY,
  /**
   * Index an article at given url.
   */
  URL,
  /**
   * Empty input.
   */
  PASS,
  /**
   * Clear the screen.
   */
  CLEAR;
}
