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
 
package encoding.sample1;
import org.apache.axis.addressing.AddressingConstants;
import org.apache.axis.addressing.EndpointReference;
import org.apache.axis.clientapi.Call;
import org.apache.axis.om.OMConstants;
import org.apache.axis.om.OMElement;
import org.apache.axis.om.OMFactory;
import org.apache.axis.om.OMNamespace;
import org.apache.axis.om.SOAPBody;
import org.apache.axis.om.SOAPEnvelope;
import org.apache.axis.testUtils.ArrayTypeEncoder;
import org.apache.axis.testUtils.ObjectToOMBuilder;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class Sampler {
    private int count;
    private Collecter collector;
    public Sampler(int count,Collecter collector){
        this.count = count;
        this.collector = collector;
    }
    
    
    public void invokeService() throws Exception {
        EchoStruct[] objs = new EchoStruct[count];

        for (int i = 0; i < objs.length; i++) {
            objs[i] = new EchoStruct();
            objs[i].setValue1("Ruy Lopez"+i);
            objs[i].setValue2("Kings Gambit"+i);
            objs[i].setValue3(345);
            objs[i].setValue4("Kings Indian Defence"+i);
            objs[i].setValue5("Musio Gambit"+i);
            objs[i].setValue6("Benko Gambit"+i);
            objs[i].setValue7("Secillian Defance"+i);
            objs[i].setValue8("Queens Gambit"+i);
            objs[i].setValue9("Queens Indian Defense"+i);
            objs[i].setValue10("Alekine's Defense"+i);
            objs[i].setValue11("Perc Defense"+i);
            objs[i].setValue12("Scotch Gambit");
            objs[i].setValue13("English Opening"+i);
        }

        long start = System.currentTimeMillis();
        OMFactory fac = OMFactory.newInstance();
        OMNamespace ns =
            fac.createOMNamespace(
                "http://apache.ws.apache.org/samples",
                "samples");
        OMNamespace arrayNs =
            fac.createOMNamespace(
                OMConstants.ARRAY_ITEM_NSURI,
                OMConstants.ARRAY_ITEM_NS_PREFIX);
        OMNamespace targetNs =
            fac.createOMNamespace("http://axis.apache.org", "s");


        OMElement returnelement = fac.createOMElement("param1", ns);
        EchoStructEncoder encoder = new EchoStructEncoder();
        ArrayTypeEncoder arrayEncoder = new ArrayTypeEncoder(objs, encoder);

        ObjectToOMBuilder builder =
            new ObjectToOMBuilder(returnelement, arrayEncoder);

        returnelement.setBuilder(builder);
        returnelement.declareNamespace(arrayNs);
        returnelement.declareNamespace(targetNs);

        SOAPEnvelope envelope = fac.getDefaultEnvelope();
        OMElement responseMethodName =
            fac.createOMElement("echoEchoStructArray", ns);
        envelope.getBody().addChild(responseMethodName);
        responseMethodName.addChild(returnelement);

        EndpointReference targetEPR =
            new EndpointReference(
                AddressingConstants.WSA_TO,
                "http://127.0.0.1:8080/axis2/services/echo");
        Call call = new Call();
        call.setTo(targetEPR);
        SOAPEnvelope responseEnv = call.sendReceive(envelope);

        SOAPBody body = responseEnv.getBody();
        if (body.hasFault()) {
            throw body.getFault().getException();
        }
        XMLStreamReader xpp = body.getPullParser(true);

        int event = xpp.next();
        while (event != XMLStreamConstants.START_ELEMENT) {
            event = xpp.next();
        }
        event = xpp.next();
        while (event != XMLStreamConstants.START_ELEMENT) {
            event = xpp.next();
        }
        event = xpp.next();
        while (event != XMLStreamConstants.START_ELEMENT) {
            event = xpp.next();
        }

        Object obj = arrayEncoder.deSerialize(xpp);
        long end = System.currentTimeMillis();
        Object[] structs = (Object[]) obj;

        for (int i = 0; i < structs.length; i++) {
            if(!structs[i].equals(objs[i])){
                throw new Exception("Assertion Failed");
            }

        }
        long val = end -start;
        System.out.println(val);
        collector.add(val);
    }
}
