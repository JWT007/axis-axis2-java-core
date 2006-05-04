/*
* Copyright 2001, 2002,2004 The Apache Software Foundation.
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


package org.apache.axis2.transport.jms;

import org.apache.axis2.transport.OutTransportInfo;

import javax.jms.Destination;
import java.util.HashMap;

public class JMSOutTransportInfo implements OutTransportInfo {
    Destination dest = null;
    String contentType = null;
    HashMap properties = new HashMap();

    JMSOutTransportInfo(Destination dest, HashMap properties) {
        this.dest = dest;
        this.properties.putAll(properties);
    }

    public Destination getDestination() {
        return dest;
    }

    public HashMap getProperties() {
        return properties;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
