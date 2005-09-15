/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.amazon.search;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;

/**
 * class LinkFollower
 * Listen to HyperLink actions and Open Simple web browser to open URLs
 *
 * @auther Gayan Asanka  (gayan@opensource.lk)
 */
class LinkFollower implements HyperlinkListener, Runnable {

    /**
     * Flag to used by the thread, If set, thread opens a URL in the Simple Web Browser
     */
    protected static boolean showURL = false;

    /**
     * Used as the root of the Simple web Browser, only one instance is needed
     */
    private static JFrame f;

    /**
     * Used for the Simple web Browser, only one instance is needed
     */
    private static JPanel contentPane;
    private static JEditorPane jep;
    private static JScrollPane scrollPane;

    /**
     * Flag to prevent duplicating Building of the Simple web browser Window
     */
    private static boolean builded = false;

    /**
     * Keep the URL of the last Hyperlink click event
     */
    private static String currentURL;

    /**
     * Constructor
     */
    public LinkFollower() {

    }

    /**
     * method hyperlinkUpdate
     * The action is to show the page of the URL the user clicked on.
     *
     * @param evt the event. We only care when its type is ACTIVATED.
     */
    public void hyperlinkUpdate(HyperlinkEvent evt) {
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if (!builded) {

                /** Window is not built yet, so build it */

                buildURLWindow();
            }
            try {
                currentURL = evt.getURL().toString();   //set the URL
                showURL = true;                        // Flag is set, thread should open the URL
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * method setPage
     * Open the URL in the simple web browser window by replacing the previous one
     */
    protected void setPage() {
        jep.setEditable(false);
        jep.addHyperlinkListener(new LinkFollower());
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        contentPane.setLayout(new BorderLayout());
        contentPane.setPreferredSize(new Dimension(400, 100));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        f.pack();
        f.setSize(640, 360);
        f.setVisible(true);
        try {
            jep.setPage(currentURL);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * method buildURLWindow
     * Build the Simple Web Broser but not displayed
     */
    private void buildURLWindow() {
        builded = true;
        jep = new JEditorPane();
        f = new JFrame("Simple Web Browser");
        contentPane = (JPanel) f.getContentPane();
        scrollPane = new JScrollPane(jep);
    }

    /**
     * method run
     * check the showURL flag and if set, open the url in simple Web Browser
     */
    public void run() {
        while (true) {
            if (showURL) {
                this.setPage();
                showURL = false;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

