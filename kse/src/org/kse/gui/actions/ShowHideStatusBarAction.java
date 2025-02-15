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
package org.kse.gui.actions;

import org.kse.gui.KseFrame;

/**
 * Action to show/hide the status bar.
 */
public class ShowHideStatusBarAction extends KeyStoreExplorerAction {
    private static final long serialVersionUID = 1L;

    /**
     * Construct action.
     *
     * @param kseFrame KeyStore Explorer frame
     */
    public ShowHideStatusBarAction(KseFrame kseFrame) {
        super(kseFrame);

        putValue(LONG_DESCRIPTION, res.getString("ShowHideStatusBarAction.statusbar"));
        putValue(NAME, res.getString("ShowHideStatusBarAction.text"));
        putValue(SHORT_DESCRIPTION, res.getString("ShowHideStatusBarAction.tooltip"));
    }

    /**
     * Do action.
     */
    @Override
    protected void doAction() {
        kseFrame.showHideStatusBar();
    }
}
