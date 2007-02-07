/**
 * 
 */
package org.apache.axis2.jaxws.sample;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.axis2.jaxws.sample.faults.FaultyWebServiceFault_Exception;
import org.apache.axis2.jaxws.sample.faults.FaultyWebServicePortType;
import org.apache.axis2.jaxws.sample.faults.FaultyWebServiceService;
import org.apache.axis2.jaxws.sample.wrap.sei.DocLitWrap;
import org.apache.axis2.jaxws.sample.wrap.sei.DocLitWrapService;
import org.apache.axis2.jaxws.framework.AbstractTestCase;

import org.test.faults.FaultyWebServiceResponse;

public class FaultyWebServiceTests extends AbstractTestCase {
	String axisEndpoint = "http://localhost:8080/axis2/services/FaultyWebServiceService";

    public static Test suite() {
        return getTestSetup(new TestSuite(FaultyWebServiceTests.class));
    }

	public void testFaultyWebService(){
		FaultyWebServiceFault_Exception exception = null;
		try{
			System.out.println("----------------------------------");
		    System.out.println("test: " + getName());
		    FaultyWebServiceService service = new FaultyWebServiceService();
		    FaultyWebServicePortType proxy = service.getFaultyWebServicePort();
			BindingProvider p =	(BindingProvider)proxy;
			p.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,axisEndpoint);

			// the invoke will throw an exception, if the test is performed right
			int total = proxy.faultyWebService(10);
			
		}catch(FaultyWebServiceFault_Exception e){
			exception = e;
		}catch(Exception e) {
           e.printStackTrace();
           fail(e.toString());
        }
		
		System.out.println("----------------------------------");
		
		assertNotNull(exception);
		assertEquals("custom exception", exception.getMessage());
		assertNotNull(exception.getFaultInfo());
		assertEquals("bean custom fault info", exception.getFaultInfo().getFaultInfo());
		assertEquals("bean custom message", exception.getFaultInfo().getMessage());
		
	}
    
    public void testFaultyWebService_badEndpoint(){
        
        String host = "this.is.a.bad.endpoint.terrible.in.fact";
        String badEndpoint = "http://" + host;
        
        WebServiceException exception = null;

        try{
            System.out.println("----------------------------------");
            System.out.println("test: " + getName());
            FaultyWebServiceService service = new FaultyWebServiceService();
            FaultyWebServicePortType proxy = service.getFaultyWebServicePort();
            BindingProvider p = (BindingProvider)proxy;
            p.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,badEndpoint);

            // the invoke will throw an exception, if the test is performed right
            int total = proxy.faultyWebService(10);

        }catch(FaultyWebServiceFault_Exception e) {
            // shouldn't get this exception
            fail(e.toString());
        }catch(WebServiceException e) {
            exception = e;
        }catch(Exception e) {
            fail("This testcase should only produce a WebServiceException.  We got: " + e.toString());
        }
        
        System.out.println("----------------------------------");
        
        assertNotNull(exception);
        assertTrue(exception.getCause() instanceof UnknownHostException);
        assertEquals(exception.getCause().getMessage(), host);

    }

    // TODO should also have an invoke oneway bad endpoint test to make sure
    // we get an exception as indicated in JAXWS 6.4.2.

    
    public void testFaultyWebService_badEndpoint_oneWay() {
        
        String host = "this.is.a.bad.endpoint.terrible.in.fact";
        String badEndpoint = "http://" + host;
        
        WebServiceException exception = null;
        
        System.out.println("------------------------------");
        System.out.println("Test : "+getName());
        try{
            
            DocLitWrapService service = new DocLitWrapService();
            DocLitWrap proxy = service.getDocLitWrapPort();
            BindingProvider p = (BindingProvider)proxy;
            p.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,badEndpoint);
            proxy.oneWayVoid();
            
        }catch(WebServiceException e) {
            exception = e;
        }catch(Exception e) {
            fail("This testcase should only produce a WebServiceException.  We got: " + e.toString());
        }
        
        System.out.println("----------------------------------");
        
        assertNotNull(exception);
        assertTrue(exception.getCause() instanceof UnknownHostException);
        assertEquals(exception.getCause().getMessage(), host);
        
    }
    
    /*
     * Tests fault processing for user defined fault types
     */      
    public void testCustomFault_AsyncCallback() throws Exception {
        System.out.println("------------------------------");
        System.out.println("test: " + getName());
        
        FaultyWebServiceService service = new FaultyWebServiceService();
        FaultyWebServicePortType proxy = service.getFaultyWebServicePort();
        BindingProvider p = (BindingProvider) proxy;
        p.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, axisEndpoint);
        
        FaultyAsyncHandler callback = new FaultyAsyncHandler();
        Future<?> future = proxy.faultyWebServiceAsync(1, callback);
        
        while (!future.isDone()) {
            Thread.sleep(1000);
            System.out.println("Async invocation incomplete");
        }
        
        Exception e = callback.getException();
        e.printStackTrace();
        
        // Section 4.3.3 states that the top level Exception should be
        // an ExecutionException, with a WebServiceException underneath.
        assertNotNull("The exception was null.", e);
        assertTrue("The thrown exception should be an ExecutionException.", 
                e.getClass().equals(ExecutionException.class));
        assertTrue("The expected fault type under the ExecutionException should be a " +
                "SOAPFaultException.  Found type: " + e.getCause().getClass(), 
                e.getCause().getClass().isAssignableFrom(SOAPFaultException.class));
    }
    
//    /*
//     * Tests fault processing for generic faults that may
//     * occur on the server.
//     */
//    public void testGenericFault_AsyncCallback() {
//        System.out.println("------------------------------");
//        System.out.println("test: " + getName());
//        
//        FaultyWebServiceService service = new FaultyWebServiceService();
//        FaultyWebServicePortType proxy = service.getFaultyWebServicePort();
//        BindingProvider p = (BindingProvider) proxy;
//        p.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,axisEndpoint);
//    }
//
//    /*
//     * Tests fault processing for user defined fault types
//     */      
//    public void testCustomFault_AsyncPolling() {
//        System.out.println("------------------------------");
//        System.out.println("test: " + getName());
//        
//        FaultyWebServiceService service = new FaultyWebServiceService();
//        FaultyWebServicePortType proxy = service.getFaultyWebServicePort();
//        BindingProvider p = (BindingProvider) proxy;
//        p.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,axisEndpoint);
//    }
//    
//    /*
//     * Tests fault processing for generic faults that may
//     * occur on the server.
//     */
//    public void testGenericFault_AsyncPolling() {
//        System.out.println("------------------------------");
//        System.out.println("test: " + getName());
//        
//        FaultyWebServiceService service = new FaultyWebServiceService();
//        FaultyWebServicePortType proxy = service.getFaultyWebServicePort();
//        BindingProvider p = (BindingProvider) proxy;
//        p.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,axisEndpoint);
//    }

    /*
     * A callback implementation that can be used to collect the exceptions
     */
    class FaultyAsyncHandler implements AsyncHandler<FaultyWebServiceResponse> {
     
        Exception exception;
        
        public void handleResponse(Response<FaultyWebServiceResponse> response) {
            try {
                System.out.println("FaultyAsyncHandler.handleResponse() was called");
                FaultyWebServiceResponse r = response.get();
                System.out.println("No exception was thrown from Response.get()");
            }
            catch (Exception e) {
                System.out.println("An exception was thrown: " + e.getClass());
                exception = e;
            }
        }
        
        public Exception getException() {
            return exception;
        }
    }
    
}
