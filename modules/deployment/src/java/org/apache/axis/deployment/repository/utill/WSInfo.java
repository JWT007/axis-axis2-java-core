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
 
package org.apache.axis.deployment.repository.utill;


public class WSInfo {

    private String filename;
    private long lastmodifieddate;
    /**
     * To check whether the file is a module or a servise
     */
    private int type;

    public WSInfo(String filename, long lastmodifieddate) {
        this.filename = filename;
        this.lastmodifieddate = lastmodifieddate;
    }

    public WSInfo(String filename, long lastmodifieddate, int type) {
        this.filename = filename;
        this.lastmodifieddate = lastmodifieddate;
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getLastmodifieddate() {
        return lastmodifieddate;
    }

    public void setLastmodifieddate(long lastmodifieddate) {
        this.lastmodifieddate = lastmodifieddate;
    }

    public int getType() {
        return type;
    }
}
