/*
* Copyright 2005 The Apache Software Foundation.
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


package org.apache.axis2.fault;

/**
 * Representation of a FaultReason; a language specific reason for a fault.
 */
public class FaultReason {

    /**
     * Language of the reason.
     * xml:lang="en" "en-GB" or just ""
     */
    private String language = "";

    /**
     * env:reasontext
     */
    private String text;

    public FaultReason() {
    }

    public FaultReason(String text, String language) {
        this.text = text;
        this.language = language;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return the text value
     */
    public String toString() {
        return text;
    }

    public String getLanguage() {
        return language;
    }

    public String getText() {
        return text;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setText(String text) {
        this.text = text;
    }
}
