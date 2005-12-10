package org.apache.axis2.wsdl.writer;

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

public class WOMWriterFactory {

    public static WOMWriter createWriter(int WSDLVersion){
        if (org.apache.wsdl.WSDLConstants.WSDL_1_1 ==WSDLVersion){
            return new WOMtoWSDL11Writer();
        }else if (org.apache.wsdl.WSDLConstants.WSDL_2_0 == WSDLVersion){
            return new WOMtoWSDL20Writer();
        }else{
            throw new RuntimeException(" Unknown WSDLversion");
        }
    }

}
