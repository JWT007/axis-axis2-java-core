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
package org.apache.wsdl;

/**
 * @author chathura@opensource.lk
 */
public interface WSDLConstants {
    /**
     * Field WSDL2_0_NAMESPACE
     */
    public static final String WSDL2_0_NAMESPACE =
            "http://www.w3.org/2004/03/wsdl";

    /**
     * Field WSDL1_1_NAMESPACE
     */
    public static final String WSDL1_1_NAMESPACE =
            "http://schemas.xmlsoap.org/wsdl/";

    /**
     * Field WSDL_NAMESPACES
     */
    public static final String[] WSDL_NAMESPACES = {WSDL2_0_NAMESPACE,
            WSDL1_1_NAMESPACE};

    // TODO Verify weather the value is right with the spec.

    /**
     * Field WSDL_MESSAGE_DIRECTION_IN
     */
    public static final String WSDL_MESSAGE_DIRECTION_IN = "in";

    // TODO Verify weather the value is right with the spec.

    /**
     * Field WSDL_MESSAGE_DIRECTION_OUT
     */
    public static final String WSDL_MESSAGE_DIRECTION_OUT = "out";

    // ////////////////////////////////////////////////////////////////
    // /////////////Message Exchange Pattern Constants/////////////////
    // ////////////////////////////////////////////////////////////////

    /**
     * Field MEP_URI_IN_ONLY
     */
    public static final String MEP_URI_IN_ONLY =
            "http://www.w3.org/2004/08/wsdl/in-only";

    /**
     * Field MEP_URI_ROBUST_IN_ONLY
     */
    public static final String MEP_URI_ROBUST_IN_ONLY =
            "http://www.w3.org/2004/08/wsdl/robust-in-only";

    /**
     * Field MEP_URI_IN_OUT
     */
    public static final String MEP_URI_IN_OUT =
            "http://www.w3.org/2004/08/wsdl/in-out";

    /**
     * Field MEP_URI_IN_OPTIONAL_OUT
     */
    public static final String MEP_URI_IN_OPTIONAL_OUT =
            "http://www.w3.org/2004/08/wsdl/in-opt-out";

    /**
     * Field MEP_URI_OUT_ONLY
     */
    public static final String MEP_URI_OUT_ONLY =
            "http://www.w3.org/2004/08/wsdl/out-only";

    /**
     * Field MEP_URI_ROBUST_OUT_ONLY
     */
    public static final String MEP_URI_ROBUST_OUT_ONLY =
            "http://www.w3.org/2004/08/wsdl/robust-out-only";

    /**
     * Field MEP_URI_OUT_IN
     */
    public static final String MEP_URI_OUT_IN =
            "http://www.w3.org/2004/08/wsdl/out-in";

    /**
     * Field MEP_URI_OUT_OPTIONL_IN
     */
    public static final String MEP_URI_OUT_OPTIONL_IN =
            "http://www.w3.org/2004/08/wsdl/out-opt-in";
}
