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

package test.interop.whitemesa.round2;

import org.apache.axis2.AxisFault;
import org.apache.ws.commons.soap.SOAPEnvelope;
import test.interop.whitemesa.SunClient;
import test.interop.whitemesa.SunClientUtil;
import test.interop.whitemesa.WhiteMesaIneterop;
import test.interop.whitemesa.round2.util.Round2EchoBase64ClientUtil;
import test.interop.whitemesa.round2.util.Round2EchoBooleanClientUtil;
import test.interop.whitemesa.round2.util.Round2EchoDateClientUtil;
import test.interop.whitemesa.round2.util.Round2EchoDecimalClientUtil;
import test.interop.whitemesa.round2.util.Round2EchoFloatArrayClientUtil;
import test.interop.whitemesa.round2.util.Round2EchoFloatClientUtil;
import test.interop.whitemesa.round2.util.Round2EchoHexBinaryClientUtil;
import test.interop.whitemesa.round2.util.Round2EchoIntegerArrayclientUtil;
import test.interop.whitemesa.round2.util.Round2EchoIntegerClientUtil;
import test.interop.whitemesa.round2.util.Round2EchoStringArrayClientUtil;
import test.interop.whitemesa.round2.util.Round2EchoStringclientUtil;
import test.interop.whitemesa.round2.util.Round2EchoStructArrayClientUtil;

import java.io.File;

/**
 * class GSRound2InteropTest
 * To test Interoperability Axis2 clients vs gSOAP Server, Round2
 * WSDLs:-
 * "base" 	 http://www.cs.fsu.edu/~engelen/interop2.wsdl
 * "Group B" http://www.cs.fsu.edu/~engelen/interop2B.wsdl
 * "Group C" http://www.cs.fsu.edu/~engelen/interop2C.wsdl
 * Todo - The commented Tests are failing.
 * IMO: the request and response SOAP envelopes are correct.
 */

public class GSRound2InteropTest extends WhiteMesaIneterop {

    SOAPEnvelope retEnv = null;
    File file = null;
    String url = "";
    String soapAction = "";
    String resFilePath = "interop/whitemesa/round2/";
    String tempPath = "";
    SunClientUtil util;
    SunClient client = new SunClient();

    /**
     * Round2
     * Group Base
     * operation echoString
     */
    public void testR2BaseEchoString() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoStringclientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseStringRes.xml";
        compareXML(retEnv, tempPath);
    }

    /**
     * Round2
     * Group Base
     * operation echoStringArray
     */
    public void testR2BaseEchoStringArray() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoStringArrayClientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseStringArrayRes.xml";
        compareXML(retEnv, tempPath);
    }

    /**
     * Round2
     * Group Base
     * operation echoInteger
     */
    public void testR2BaseEchoInteger() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoIntegerClientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseIntegerRes.xml";
        compareXML(retEnv, tempPath);
    }

    /**
     * Round2
     * Group Base
     * operation echoIntegerArray
     */
    public void testR2BaseEchoIntegerArray() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoIntegerArrayclientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseIntegerArrayRes.xml";
        compareXML(retEnv, tempPath);
    }

    /**
     * Round2
     * Group Base
     * operation echoFloat
     */
    public void testR2BaseEchoFloat() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoFloatClientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseFloatRes.xml";
        compareXML(retEnv, tempPath);
    }

    /**
     * Round2
     * Group Base
     * operation echoFloatArray
     */
    public void testR2BaseEchoFloatArray() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoFloatArrayClientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseFloatArrayRes.xml";
        compareXML(retEnv, tempPath);
    }

    /**
     * Round2
     * Group Base
     * operation echoStruct
     */
    /*public void testRBaseEchoStruct() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoStructClientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        System.out.println(retEnv.toString());
        tempPath = resFilePath + "GSBaseStructRes.xml";
        compareXML(retEnv, tempPath);
    }*/

    /**
     * Round2
     * Group Base
     * operation echoStructArray
     */
    public void testR2BaseEchoStructArray() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoStructArrayClientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseStructArrayRes.xml";
        compareXML(retEnv, tempPath);
    }

    /**
     * Round2
     * Group Base
     * operation echoVoid
     */
    /*public void testR2BaseEchoVoid() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoVoidClientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseVoidRes.xml";
        compareXML(retEnv, tempPath);
    }
*/
    /**
     * Round2
     * Group Base
     * operation echoBase64
     */
    public void testR2BaseEchoBase64() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoBase64ClientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseBase64Res.xml";
        compareXML(retEnv, tempPath);
    }

    /**
     * Round2
     * Group Base
     * operation echoDate
     */
    public void testR2BaseEchoDate() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoDateClientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseDateRes.xml";
        compareXML(retEnv, tempPath);
    }

    /**
     * Round2
     * Group Base
     * operation echoHexBinary
     */
    public void testR2BaseEchoHexBinary() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoHexBinaryClientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseHexBinaryRes.xml";
        compareXML(retEnv, tempPath);
    }

    /**
     * Round2
     * Group Base
     * operation echoDecimal
     */
    public void testR2BaseEchoDecimal() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoDecimalClientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseDecimalRes.xml";
        compareXML(retEnv, tempPath);
    }

    /**
     * Round2
     * Group Base
     * operation echoBoolean
     */
    public void testR2BaseEchoBoolean() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2.cgi";
        soapAction = "http://soapinterop.org/";

        util = new Round2EchoBooleanClientUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSBaseBooleanRes.xml";
        compareXML(retEnv, tempPath);
    }

    /**
     * Round2
     * Group B
     * operation echoStructAsSimpleTypes
     */
    /*  public void testR2GBEchoStructAsSimpleTypes() throws AxisFault {
            url = "http://websrv.cs.fsu.edu/~engelen/interop2B.cgi";
            soapAction = "http://soapinterop.org/";

            util = new GroupbEchoStructAsSimpleTypesUtil();
            retEnv = client.sendMsg(util, url, soapAction);
            tempPath = resFilePath + "GSGroupbStructAsSimpleTypesRes.xml";
            compareXML(retEnv, tempPath);
        }
    */

    /**
     * Round2
     * Group B
     * operation echoSimpleTypesAsStruct
     */
    /*public void testR2GBEchoSimpleTypesAsStruct() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2B.cgi";
        soapAction = "http://soapinterop.org/";

        util = new GroupbEchoSimpleTypesAsStructUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSGroupbSimpletypesAsStructRes.xml";
        compareXML(retEnv, tempPath);
    }*/

    /**
     * Round2
     * Group B
     * operation echo2DStringArray
     */
    /*public void testR2GBEcho2DStringArray() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2B.cgi";
        soapAction = "http://soapinterop.org/";

        util = new GroupbEcho2DStringArrayUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSGroupb2DStringArrayRes.xml";
        compareXML(retEnv, tempPath);
    }*/

    /**
     * Round2
     * Group B
     * operation echoNestedStruct
     */
    /* public void testR2GBEchoNestedStruct() throws AxisFault {
            url = "http://websrv.cs.fsu.edu/~engelen/interop2B.cgi";
            soapAction = "http://soapinterop.org/";

            util = new GroupbEchoNestedStructUtil();
            retEnv = client.sendMsg(util, url, soapAction);
            tempPath = resFilePath + "GSGroupbNestedStructRes.xml";
            compareXML(retEnv, tempPath);
        }
    */
    /**
     * Round2
     * Group B
     * operation echoNestedArray
     */
    /* public void testR2GBEchoNestedArray() throws AxisFault {
        url = "http://websrv.cs.fsu.edu/~engelen/interop2B.cgi";
        soapAction = "http://soapinterop.org/";

        util = new GroupbEchoNestedArrayUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSGroupbNestedArrayRes.xml";
        compareXML(retEnv, tempPath);
    }*/

    /**
     * Round2
     * Group C
     * operation echoVoid
     */
    /*public void testR2GCEchoVoid() throws AxisFault {
        url = "http://www.cs.fsu.edu/~engelen/interop2C.cgi";
        soapAction = "http://soapinterop.org/";

        util = new GroupcVoidUtil();
        retEnv = client.sendMsg(util, url, soapAction);
        tempPath = resFilePath + "GSGroupcVoidRes.xml";
        compareXML(retEnv, tempPath);
    }
*/
}

