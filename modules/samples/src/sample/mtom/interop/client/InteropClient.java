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

package sample.mtom.interop.client;

import javax.swing.*;
import java.awt.*;

public class InteropClient extends JFrame {
    public InteropClient(String title) throws HeadlessException {
        super(title);

        this.getContentPane().add(new UserInterface(this));
        this.show();
    }

    public static void main(String[] args) {
        InteropClient form = new InteropClient("Interop Client");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        form.setLocation(screenSize.width / 4 - 20,
                screenSize.height / 4);
        form.setSize(screenSize.width / 2 - 80, screenSize.height / 2);
        form.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        form.show();
    }
}
