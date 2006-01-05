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

package test.interop.whitemesa.round4.simple;

import junit.framework.TestCase;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMXMLParserWrapper;
import org.apache.axis2.om.impl.llom.exception.XMLComparisonException;
import org.apache.axis2.soap.SOAPEnvelope;
import org.apache.axis2.soap.impl.llom.builder.StAXSOAPModelBuilder;
import test.interop.util.XMLComparatorInterop;
import test.interop.whitemesa.round4.simple.utils.EchoEmptyFaultClientUtil;
import test.interop.whitemesa.round4.simple.utils.EchoIntArrayFaultClientUtil;
import test.interop.whitemesa.round4.simple.utils.EchoMultipleFaults1ClientUtil;
import test.interop.whitemesa.round4.simple.utils.EchoMultipleFaults2ClientUtil;
import test.interop.whitemesa.round4.simple.utils.EchoMultipleFaults3Clientutil;
import test.interop.whitemesa.round4.simple.utils.EchoMultipleFaults4ClientUtil;
import test.interop.whitemesa.round4.simple.utils.EchoStringFaultClientUtil;
import test.interop.whitemesa.round4.simple.utils.WhitemesaR4ClientUtil;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
public class WhitemesaR4SimpleTest extends TestCase {


    EchoBlockingClient client = null;
    OMElement retEle = null;
    WhitemesaR4ClientUtil util=null;
    String soapAction = "";




    public void setUp(){
        client = new EchoBlockingClient();
        retEle = null;
        util=null;
    }

    private boolean compare(OMElement retEle,String filePath) throws XMLStreamException,
            XMLComparisonException {
        boolean compare = false;
        if(retEle!=null) {
            InputStream stream = Thread.currentThread().
                    getContextClassLoader().getResourceAsStream(filePath);
            javax.xml.stream.XMLStreamReader parser =
                    XMLInputFactory.newInstance().createXMLStreamReader(stream);
            OMXMLParserWrapper builder = new StAXSOAPModelBuilder(parser, null);
            SOAPEnvelope resEnv = (SOAPEnvelope) builder.getDocumentElement();
            OMElement resElementtobe = resEnv.getBody().getFirstElement();
            XMLComparatorInterop comparator = new XMLComparatorInterop();
            compare = comparator.compare(retEle,resElementtobe);

        }
        return compare;
    }


    public void testEchoEmptyFault()  throws Exception{
        util=new EchoEmptyFaultClientUtil();
        retEle =client.sendMsg(util,soapAction);
        assertTrue(compare(retEle,"interop/whitemesa/round4/res/resEmptyFault.xml"));
    }

    public void testEchoStringFault() throws Exception{
        //echoStringFault
        util=new EchoStringFaultClientUtil();
        retEle =client.sendMsg(util,soapAction);
        assertTrue(compare(retEle,"interop/whitemesa/round4/res/resStringFault.xml"));

    }

    //todo the messages are received right but the comparison changes. Have to check the comparator

    public void testEchoIntArrayFault() throws Exception{
        util=new EchoIntArrayFaultClientUtil();
        retEle =client.sendMsg(util,soapAction);
        assertTrue(compare(retEle,"interop/whitemesa/round4/res/resIntArray.xml"));


    }

    public void testEchoMultipleFaults1() throws Exception{
        util=new EchoMultipleFaults1ClientUtil();
        retEle =client.sendMsg(util,soapAction);
        assertTrue(compare(retEle,"interop/whitemesa/round4/res/resMultipleFaults1.xml"));

    }
    public void testEchoMultipleFaults2() throws Exception{
        util=new EchoMultipleFaults2ClientUtil();
        retEle =client.sendMsg(util,soapAction);
        assertTrue(compare(retEle,"interop/whitemesa/round4/res/resMultiplefaults2.xml"));

    }
    public void testEchoMultipleFaults3() throws Exception{
        util=new EchoMultipleFaults3Clientutil();
        retEle =client.sendMsg(util,soapAction);
        assertTrue(compare(retEle,"interop/whitemesa/round4/res/resMultipleFaults3.xml"));
    }
    public void testEchoMultipleFaults4() throws Exception{
        util=new EchoMultipleFaults4ClientUtil();
        retEle =client.sendMsg(util,soapAction);
        assertTrue( compare(retEle,"interop/whitemesa/round4/res/resMultipleFaults4.xml"));
    }

}

