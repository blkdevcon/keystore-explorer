/*
 * Copyright 2004 - 2013 Wayne Grant
 *           2013 - 2022 Kai Kramer
 *
 * This file is part of KeyStore Explorer.
 *
 * KeyStore Explorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KeyStore Explorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KeyStore Explorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kse.gui.error;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;

import org.kse.gui.CursorUtil;
import org.kse.gui.JEscDialog;
import org.kse.gui.LnfUtil;
import org.kse.gui.PlatformUtil;
import org.kse.utilities.DialogViewer;

import net.miginfocom.swing.MigLayout;

/**
 * Dialog to display a collection of errors
 */
public class DErrorCollection extends JEscDialog {
    private static final long serialVersionUID = 1L;
    private static ResourceBundle res = ResourceBundle.getBundle("org/kse/gui/error/resources");
    private Map<?, ?> errorMap;
    private JList<String> jltKeys;
    private JLabel jlblKeys;
    private JTextArea jtaKeyValue;
    private JLabel jlblKeyValue;
    private JButton jbOK;
    private JButton jbCopy;
    private JPanel jpButtons;

    /**
     * Creates new DErrorCollection dialog where the parent is a frame.
     *
     * @param parent Parent frame
     * @param map    Hashmap
     */
    public DErrorCollection(JFrame parent, Map<?, ?> map) {
        super(parent, Dialog.ModalityType.DOCUMENT_MODAL);
        this.errorMap = map;
        setTitle(res.getString("DErrorCollection.Title"));
        initFields();

    }

    /**
     * Create the elements of the dialog
     */
    public void initFields() {
        jlblKeys = new JLabel(res.getString("DErrorCollection.jlblKeys.text"));

        jltKeys = new JList();
        jltKeys.setToolTipText(res.getString("DErrorCollection.jltKeys.tooltip"));
        jltKeys.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jltKeys.setBorder(new EtchedBorder());

        jlblKeyValue = new JLabel(res.getString("DErrorCollection.jlblKeyValue.text"));
        jtaKeyValue = new JTextArea(10, 30);
        jtaKeyValue.setFont(new Font(Font.MONOSPACED, Font.PLAIN, LnfUtil.getDefaultFontSize()));
        jtaKeyValue.setEditable(false);
        jtaKeyValue.setToolTipText(res.getString("DErrorCollection.jtaKeyValue.tooltip"));
        jtaKeyValue.setLineWrap(true);
        JScrollPane scrollPane = PlatformUtil.createScrollPane(jtaKeyValue,
                                                               ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                               ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // keep uneditable color same as editable
        jlblKeyValue.putClientProperty("JTextArea.infoBackground", Boolean.TRUE);

        jbOK = new JButton(res.getString("DErrorCollection.jbOK.text"));

        jbCopy = new JButton(res.getString("DErrorCollection.jbCopy.text"));
        jbCopy.setToolTipText(res.getString("DErrorCollection.jbCopy.tooltip"));
        PlatformUtil.setMnemonic(jbCopy, res.getString("DErrorCollection.jbCopy.mnemonic").charAt(0));

        jpButtons = PlatformUtil.createDialogButtonPanel(new JButton[] { jbOK }, null, new JButton[] { jbCopy },
                                                         "insets 0");

        // layout
        Container pane = getContentPane();
        pane.setLayout(new MigLayout("insets dialog, fill", "[][]", "[][]"));
        pane.add(jlblKeys, "");
        pane.add(jlblKeyValue, "wrap unrel");
        pane.add(jltKeys, "");
        pane.add(scrollPane, "wrap");
        pane.add(new JSeparator(), "spanx, growx, wrap unrel");
        pane.add(jpButtons, "right, spanx");

        // actions
        jltKeys.addListSelectionListener(evt -> {
            try {
                CursorUtil.setCursorBusy(DErrorCollection.this);
                updateKeyValue();
            } finally {
                CursorUtil.setCursorFree(DErrorCollection.this);
            }
        });
        jbOK.addActionListener(evt -> okPressed());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                closeDialog();
            }
        });
        jbCopy.addActionListener(evt -> {
            try {
                CursorUtil.setCursorBusy(DErrorCollection.this);
                copyPressed();
            } finally {
                CursorUtil.setCursorFree(DErrorCollection.this);
            }
        });

        populateKeys(errorMap);
        setResizable(false);

        getRootPane().setDefaultButton(jbOK);

        pack();
        setLocationRelativeTo(null);

    }

    /**
     * Sets the JList keys from keys of the hashmap
     *
     * @param map Hashmap
     */
    private void populateKeys(Map<?, ?> map) {
        // convert hash map keys to a string array
        String[] listData = (String[]) map.keySet().toArray(new String[map.size()]);

        if (listData != null) {
            jltKeys.setListData(listData);
            jltKeys.setSelectedIndex(0);
        }
    }

    /**
     * Update the key value text area
     */
    private void updateKeyValue() {
        int selectedRow = jltKeys.getSelectedIndex();

        if (selectedRow == -1) {
            jtaKeyValue.setText("");
        } else {
            String strValue = errorMap.get(jltKeys.getSelectedValue()).toString();
            jtaKeyValue.setText(strValue);
            jtaKeyValue.setCaretPosition(0);
        }
    }

    /**
     * Copies the contents of the text area to the clip board.
     */
    private void copyPressed() {
        String policy = jtaKeyValue.getText();

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection copy = new StringSelection(policy);
        clipboard.setContents(copy, copy);
    }

    /**
     * Calls the close dialogue window
     */
    private void okPressed() {
        closeDialog();
    }

    /**
     * Closes the dialogue window
     */
    private void closeDialog() {
        setVisible(false);
        dispose();
    }

    // quick ui test
    public static void main(String[] args) throws Exception {
        DialogViewer.prepare();
        Map<String, String> testmap = new HashMap<String, String>();
        testmap.put("file 1", "value of file 1");
        testmap.put("file 2", "value of file 2");

        DErrorCollection dialog = new DErrorCollection(new JFrame(), testmap);
        DialogViewer.run(dialog);
    }
}
