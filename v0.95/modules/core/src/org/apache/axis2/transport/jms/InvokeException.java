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

/**
 * The <code>InvokeException</code> is thrown when a method encounters a
 * general exception in the course of processing.
 */
public class InvokeException extends RuntimeException {
	
    private static final long serialVersionUID = -9143832230352429639L;

	public InvokeException(String message) {
        super(message);
    }
}
