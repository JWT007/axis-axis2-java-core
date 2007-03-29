
package org.apache.axis2.jaxws.sample.nonwrap.sei;

import java.util.concurrent.Future;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Holder;
import javax.xml.ws.Response;

import org.test.sample.nonwrap.OneWay;
import org.test.sample.nonwrap.OneWayVoid;
import org.test.sample.nonwrap.ReturnType;
import org.test.sample.nonwrap.TwoWay;
import org.test.sample.nonwrap.TwoWayHolder;

/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.0_01-b15-fcs
 * Generated source version: 2.0
 * 
 */
@WebService(name = "DocLitNonWrapPortType", targetNamespace = "http://nonwrap.sample.test.org")
@SOAPBinding(parameterStyle = ParameterStyle.BARE)
public interface DocLitNonWrapPortType {


    /**
     * 
     * @param allByMyself
     */
    @WebMethod(action = "http://nonwrap.sample.test.org/twoWayReturn")
    @Oneway
    public void oneWayVoid(
        @WebParam(name = "oneWayVoid", targetNamespace = "http://nonwrap.sample.test.org", partName = "allByMyself")
        OneWayVoid allByMyself);

    /**
     * 
     * @param allByMyself
     */
    @WebMethod(action = "http://nonwrap.sample.test.org/twoWayReturn")
    @Oneway
    public void oneWay(
        @WebParam(name = "oneWay", targetNamespace = "http://nonwrap.sample.test.org", partName = "allByMyself")
        OneWay allByMyself);

    /**
     * 
     * @param allByMyself
     * @return
     *     returns javax.xml.ws.Response<org.test.sample.nonwrap.TwoWayHolder>
     */
    @WebMethod(operationName = "twoWayHolder", action = "http://nonwrap.sample.test.org/twoWayReturn")
    public Response<TwoWayHolder> twoWayHolderAsync(
        @WebParam(name = "twoWayHolder", targetNamespace = "http://nonwrap.sample.test.org", partName = "allByMyself")
        TwoWayHolder allByMyself);

    /**
     * 
     * @param allByMyself
     * @param asyncHandler
     * @return
     *     returns java.util.concurrent.Future<? extends java.lang.Object>
     */
    @WebMethod(operationName = "twoWayHolder", action = "http://nonwrap.sample.test.org/twoWayReturn")
    public Future<?> twoWayHolderAsync(
        @WebParam(name = "twoWayHolder", targetNamespace = "http://nonwrap.sample.test.org", partName = "allByMyself")
        TwoWayHolder allByMyself,
        @WebParam(name = "twoWayHolderResponse", targetNamespace = "", partName = "asyncHandler")
        AsyncHandler<TwoWayHolder> asyncHandler);

    /**
     * 
     * @param allByMyself
     */
    @WebMethod(action = "http://nonwrap.sample.test.org/twoWayReturn")
    public void twoWayHolder(
        @WebParam(name = "twoWayHolder", targetNamespace = "http://nonwrap.sample.test.org", mode = Mode.INOUT, partName = "allByMyself")
        Holder<TwoWayHolder> allByMyself);

    /**
     * 
     * @param allByMyself
     * @return
     *     returns javax.xml.ws.Response<org.test.sample.nonwrap.ReturnType>
     */
    @WebMethod(operationName = "twoWay", action = "http://nonwrap.sample.test.org/twoWayReturn")
    public Response<ReturnType> twoWayAsync(
        @WebParam(name = "twoWay", targetNamespace = "http://nonwrap.sample.test.org", partName = "allByMyself")
        TwoWay allByMyself);

    /**
     * 
     * @param allByMyself
     * @param asyncHandler
     * @return
     *     returns java.util.concurrent.Future<? extends java.lang.Object>
     */
    @WebMethod(operationName = "twoWay", action = "http://nonwrap.sample.test.org/twoWayReturn")
    public Future<?> twoWayAsync(
        @WebParam(name = "twoWay", targetNamespace = "http://nonwrap.sample.test.org", partName = "allByMyself")
        TwoWay allByMyself,
        @WebParam(name = "twoWayResponse", targetNamespace = "", partName = "asyncHandler")
        AsyncHandler<ReturnType> asyncHandler);

    /**
     * 
     * @param allByMyself
     * @return
     *     returns org.test.sample.nonwrap.ReturnType
     */
    @WebMethod(action = "http://nonwrap.sample.test.org/twoWayReturn")
    @WebResult(name = "ReturnType", targetNamespace = "http://nonwrap.sample.test.org", partName = "allByMyself")
    public ReturnType twoWay(
        @WebParam(name = "twoWay", targetNamespace = "http://nonwrap.sample.test.org", partName = "allByMyself")
        TwoWay allByMyself);

}
