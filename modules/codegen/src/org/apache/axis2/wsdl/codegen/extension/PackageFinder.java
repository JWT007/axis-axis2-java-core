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

package org.apache.axis2.wsdl.codegen.extension;

import org.apache.axis2.util.URLProcessor;
import org.apache.wsdl.WSDLBinding;

public class PackageFinder extends AbstractCodeGenerationExtension {


    public void engage() {
        String packageName = this.configuration.getPackageName();
        if (packageName == null || URLProcessor.DEFAULT_PACKAGE.equals(packageName)) {
            WSDLBinding binding = configuration.getWom().getBinding(
                    AxisBindingBuilder.AXIS_BINDING_QNAME);
            if (binding != null &&
                    binding.getBoundInterface() != null &&
                    binding.getBoundInterface().getName() != null &&
                    binding.getBoundInterface().getName().getNamespaceURI() != null) {
                String temp = binding.getBoundInterface().getName()
                        .getNamespaceURI();
                packageName = URLProcessor.makePackageName(temp);
            }
        }

        if (null == packageName || "".equals(packageName))
            packageName = URLProcessor.DEFAULT_PACKAGE;

        this.configuration.setPackageName(packageName.toLowerCase());

    }


}