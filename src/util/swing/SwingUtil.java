// SwingUtil.java
// See toplevel license.txt for copyright and license terms.

package util.swing;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.font.LineMetrics;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/** Miscellaneous Swing-related utililities. */
public class SwingUtil {
    /** Return true if the Control button was held when 'e' was generated. */
    public static boolean controlPressed(InputEvent e)
    {
        return (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
    }

    /** Return true if the Alt button was held when 'e' was generated. */
    public static boolean altPressed(InputEvent e)
    {
        return (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
    }

    /** Return true if the Shift button was held when 'e' was generated. */
    public static boolean shiftPressed(InputEvent e)
    {
        return (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0;
    }

    /** Return true if no keyboard modifiers or mouse buttons were held
      * when 'e' was generated. */
    public static boolean noModifiers(KeyEvent e)
    {
        return e.getModifiersEx() == 0;
    }

    /** Send a message to close a window.
      *
      * I do not really understand whether or why this is better than
      * simply calling dispose(), but I infer from code snippets that
      * it may be. */
    public static void closeWindow(Window window)
    {
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }

    /** An action to close a window or dialog. */
    public static class WindowCloseAction extends AbstractAction {
        private static final long serialVersionUID = -1615998248180527506L;

        /** The window that will be closed. */
        public Window window;

        public WindowCloseAction(Window window)
        {
            this.window = window;
        }

        @Override
        public void actionPerformed(ActionEvent event)
        {
            closeWindow(this.window);
        }
    }

    /** Draw 'str' centered at 'p'. */
    public static void drawCenteredText(Graphics g, Point p, String str)
    {
        FontMetrics fm = g.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(str, g);

        // Go to 'p', then add a/2 to get to the baseline.
        // I ignore the descent because it looks better to center without
        // regard to descenders.
        int baseY = p.y + (int)(lm.getAscent()/2);
        int baseX = p.x - fm.stringWidth(str)/2;

        g.drawString(str, baseX, baseY);
    }

    /** Draw 'str' at the given location, but process newlines by moving
      * to a new line. */
    public static void drawTextWithNewlines(Graphics g, String str, int x, int y)
    {
        String lines[] = str.split("\n");
        int lineHeight = g.getFontMetrics().getHeight();
        for (String s : lines) {
            g.drawString(s, x, y);
            y += lineHeight;
        }
    }

    /** Create a JOptionPane instance that word-wraps its message. */
    public static JOptionPane makeWordWrapJOptionPane()
    {
        // The basic problem is described in this bug report:
        //
        //   http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4104906
        //
        // The workaround there requires adding a scrollbar to the
        // message, which I do not want to do.

        // I tried these solutions, but they do not work (anymore?):
        //
        //   http://stackoverflow.com/questions/4330076/joptionpane-showmessagedialog-truncates-jtextarea-message
        //   http://www.coderanch.com/t/339970/GUI/java/wrap-large-message-JOptionPane-showConfirmDialog
        //
        // Most other solutions involve manually inserting newlines.

        // Thankfully, this one actually does work:
        //
        //   http://www.jroller.com/Fester/entry/joptionpane_with_word_wrapping

        @SuppressWarnings("serial")
        JOptionPane pane = new JOptionPane() {
            @Override
            public int getMaxCharactersPerLineCount()
            {
                return 80;
            }
        };
        return pane;
    }

    /** Show an error message dialog box with message word wrapping. */
    public static void errorMessageBox(Component parent, String message)
    {
        JOptionPane pane = makeWordWrapJOptionPane();
        pane.setMessage(message);
        pane.setMessageType(JOptionPane.ERROR_MESSAGE);

        JDialog dialog = pane.createDialog(parent, "Error");
        dialog.setVisible(true);
    }

    /** Show a confirmation message box with line wrapped message. */
    public static int confirmationBox(
        Component parent,
        String message,
        String title,
        int optionType)
    {
        JOptionPane pane = makeWordWrapJOptionPane();
        pane.setMessage(message);
        pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        pane.setOptionType(optionType);

        JDialog dialog = pane.createDialog(parent, "Error");
        dialog.setVisible(true);

        Object result = pane.getValue();
        if (result == null || !(result instanceof Integer)) {
            return JOptionPane.CLOSED_OPTION;
        }
        else {
            return ((Integer)result).intValue();
        }
    }

    /** Show a message box with a long multi-line piece of text,
      * like a log file. */
    public static void logFileMessageBox(
        Component parent,
        String logMessage,
        String title)
    {
        // A scrollable text area holds the text.
        JTextArea textArea = new JTextArea(20, 80);
        textArea.setText(logMessage);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Show it.
        JOptionPane.showMessageDialog(parent, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }
}

// EOF
