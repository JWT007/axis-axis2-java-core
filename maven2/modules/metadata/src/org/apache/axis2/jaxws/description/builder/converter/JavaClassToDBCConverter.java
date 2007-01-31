package org.apache.axis2.jaxws.description.builder.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebFault;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.WebServiceRefs;

import org.apache.axis2.jaxws.description.builder.*;

public class JavaClassToDBCConverter {

	private Class serviceClass;
	
	private String seiClassName;
	
	private List<Class> classes;
	
	public JavaClassToDBCConverter(Class serviceClass){
		this.serviceClass = serviceClass;
		classes = new ArrayList<Class>();
		establishClassHierarchy(serviceClass);
		establishInterfaceHierarchy(serviceClass.getInterfaces());
	}
	
	/**
	 * The only method we will expose to users of this class. It will 
	 * trigger the creation of the <code>DescriptionBuilderComposite</code>
	 * based on our service class. It will also handle the case of an impl
	 * class that references an SEI.
	 * @return - <code>DescriptionBuilderComposite</code>
	 */
	public HashMap<String, DescriptionBuilderComposite> produceDBC() {
		HashMap<String, DescriptionBuilderComposite> dbcMap = new HashMap<String, 
			DescriptionBuilderComposite>();
		for(int i=0; i < classes.size(); i++ ) {
			serviceClass = classes.get(i);
			DescriptionBuilderComposite composite = new DescriptionBuilderComposite();
			introspectClass(composite);
			dbcMap.put(composite.getClassName(), composite);
			if(seiClassName != null && !seiClassName.equals("")) {
				DescriptionBuilderComposite seiComposite = new DescriptionBuilderComposite();
				try {
					serviceClass = Thread.currentThread().getContextClassLoader().loadClass(seiClassName);
					if(serviceClass != null) {
						introspectClass(seiComposite);
						dbcMap.put(seiComposite.getClassName(), seiComposite);
					}
				}
				catch(ClassNotFoundException e) {
					System.out.println("DUSTIN CLASS NOT FOUND");
				}
			}
		}
		return dbcMap;
	}
	
	/**
	 * This method will drive the introspection of the class-level information.
	 * It will store the gathered information in the pertinent data members
	 * of the <code>DescriptionBuilderComposite</code>
	 * @param composite - <code>DescriptionBuilderComposite</code>
	 */
	private void introspectClass(DescriptionBuilderComposite composite) {
		// Need to investigate this, probably want to specify 
		composite.setClassLoader(serviceClass.getClassLoader());
		composite.setIsInterface(serviceClass.isInterface());
		composite.setSuperClassName(serviceClass.getSuperclass() != null ? serviceClass.
				getSuperclass().getName() : null);
		composite.setClassName(serviceClass.getName());
		setInterfaces(composite);
		setTypeTargettedAnnotations(composite);
		if(serviceClass.getFields().length > 0) {
			JavaFieldsToFDCConverter fieldConverter = new JavaFieldsToFDCConverter(
					serviceClass.getFields());
			List<FieldDescriptionComposite> fdcList = fieldConverter.convertFields();
			ConverterUtils.attachFieldDescriptionComposites(composite, fdcList);
		}
		if(serviceClass.getMethods().length> 0) {
			JavaMethodsToMDCConverter methodConverter = new JavaMethodsToMDCConverter(
					serviceClass.getMethods(), serviceClass.getName());
			List<MethodDescriptionComposite> mdcList = methodConverter.convertMethods();
			ConverterUtils.attachMethodDescriptionComposites(composite, mdcList);	
		}
	}
	
	/**
	 * This method is responsible for finding any interfaces implemented by the 
	 * service class. We will then set these as a list of fully qualified class
	 * names on the <code>DescriptionBuilderComposite</code>
	 * @param composite <code>DescriptionBuilderComposite</code>
	 */
	private void setInterfaces(DescriptionBuilderComposite composite) {
		Class[] interfaces = serviceClass.getInterfaces();
		List<String> interfaceList = interfaces.length > 0 ? new ArrayList<String>()
				: null;
		for(int i=0; i < interfaces.length; i++) {
			interfaceList.add(interfaces[i].getName());
		}
		// We only want to set this list if we found interfaces b/c the
		// DBC news up an interface list as part of its static initialization.
		// Thus, if we set this list to null we may cause an NPE
		if(interfaceList != null) {
			composite.setInterfacesList(interfaceList);
		}
	}
	
	/**
	 * This method will drive the attachment of Type targetted annotations to the
	 * <code>DescriptionBuilderComposite</code>
	 * @param composite - <code>DescriptionBuilderComposite</code>
	 */
	private void setTypeTargettedAnnotations(DescriptionBuilderComposite composite) {
		attachBindingTypeAnnotation(composite);
		attachHandlerChainAnnotation(composite);
		attachServiceModeAnnotation(composite);
		attachSoapBindingAnnotation(composite);
		attachWebFaultAnnotation(composite);
		attachWebServiceAnnotation(composite);
		attachWebServiceClientAnnotation(composite);
		attachWebServiceProviderAnnotation(composite);
		attachWebServiceRefsAnnotation(composite);
		attachWebServiceRefAnnotation(composite);
	}
	
	/**
	 * This method will be used to attach @WebService annotation data to the 
	 * <code>DescriptionBuildercomposite</code>
	 * @param composite - <code>DescriptionBuilderComposite</code>
	 */
	private void attachWebServiceAnnotation(DescriptionBuilderComposite composite) {
		WebService webService = (WebService) ConverterUtils.getAnnotation(
				WebService.class, serviceClass);
		if(webService != null) {
			// Attach @WebService annotated data
			WebServiceAnnot wsAnnot = WebServiceAnnot.createWebServiceAnnotImpl();
			wsAnnot.setEndpointInterface(webService.endpointInterface());
			// check for SEI and save name if necessary
			seiClassName = webService.endpointInterface();
			wsAnnot.setName(webService.name());
			wsAnnot.setPortName(webService.portName());
			wsAnnot.setServiceName(webService.serviceName());
			wsAnnot.setTargetNamespace(webService.targetNamespace());
			wsAnnot.setWsdlLocation(wsAnnot.wsdlLocation());
			composite.setWebServiceAnnot(wsAnnot);
		}
	}
	
	/**
	 * This method will be used to attach @WebServiceClient annotation data to 
	 * the  <code>DescriptionBuildercomposite</code>
	 * @param composite - <code>DescriptionBuilderComposite</code>
	 */
	private void attachWebServiceClientAnnotation(DescriptionBuilderComposite composite) {
		WebServiceClient webServiceClient = (WebServiceClient) ConverterUtils.
			getAnnotation(WebServiceClient.class, serviceClass);
		if(webServiceClient != null) {
			
		}
		
	}
	
	/**
	 * This method will be used to attach @WebServiceProvider annotation data to 
	 * the <code>DescriptionBuilderComposite</code>
	 * @param composite - <code>DescriptionBuildercomposite</code>
	 */
	private void attachWebServiceProviderAnnotation(DescriptionBuilderComposite composite) {
		WebServiceProvider webServiceProvider = (WebServiceProvider) ConverterUtils.
			getAnnotation(WebServiceProvider.class, serviceClass);
		if(webServiceProvider != null) {
			// Attach @WebServiceProvider annotation data
			WebServiceProviderAnnot wspAnnot = WebServiceProviderAnnot.
				createWebServiceAnnotImpl();
			wspAnnot.setPortName(webServiceProvider.portName());
			wspAnnot.setServiceName(webServiceProvider.serviceName());
			wspAnnot.setTargetNamespace(webServiceProvider.targetNamespace());
			wspAnnot.setWsdlLocation(webServiceProvider.wsdlLocation());
			composite.setWebServiceProviderAnnot(wspAnnot);
		}
	}
	
	/**
	 * This method will be used to attach @BindingType annotation data to 
	 * the <code>DescriptionBuilderComposite</code>
	 * @param composite - <code>DescriptionBuildercomposite</code>
	 */
	private void attachBindingTypeAnnotation(DescriptionBuilderComposite composite) {
		BindingType bindingType = (BindingType) ConverterUtils.getAnnotation(
				BindingType.class, serviceClass);
		if(bindingType != null) {
			// Attach @BindingType annotation data
			BindingTypeAnnot btAnnot = BindingTypeAnnot.createBindingTypeAnnotImpl();
			btAnnot.setValue(bindingType.value());
			composite.setBindingTypeAnnot(btAnnot);
		}
	}
	
	/**
	 * This method will be used to attach @HandlerChain annotation data to 
	 * the <code>DescriptionBuilderComposite</code>
	 * @param composite - <code>DescriptionBuildercomposite</code>
	 */
	private void attachHandlerChainAnnotation(DescriptionBuilderComposite composite){
		ConverterUtils.attachHandlerChainAnnotation(composite, serviceClass);
	}
	
	/**
	 * This method will be used to attach @ServiceMode annotation data to 
	 * the <code>DescriptionBuilderComposite</code>
	 * @param composite - <code>DescriptionBuildercomposite</code>
	 */
	private void attachServiceModeAnnotation(DescriptionBuilderComposite composite) {
		ServiceMode serviceMode = (ServiceMode) ConverterUtils.getAnnotation(
				ServiceMode.class, serviceClass);
		if(serviceMode != null) {
			// Attach @ServiceMode annotated data
			ServiceModeAnnot smAnnot = ServiceModeAnnot.createWebServiceAnnotImpl();
			smAnnot.setValue(serviceMode.value());
			composite.setServiceModeAnnot(smAnnot);
		}
	}
	
	/**
	 * This method will be used to drive the setting of @SOAPBinding annotation
	 * data to the <code>DescriptionBuilderComposite</code>
	 * @param composite - <code>DescriptionBuildercomposite</code>
	 */
	private void attachSoapBindingAnnotation(DescriptionBuilderComposite composite) {
		ConverterUtils.attachSoapBindingAnnotation(composite, serviceClass);
	}
	
	/**
	 * This method will be used to attach @WebFault annotation data to the 
	 * <code>DescriptionBuilderComposite</code>
	 * @param composite - <code>DescriptionBuilderComposite</code>
	 */
	private void attachWebFaultAnnotation(DescriptionBuilderComposite composite) {
		WebFault webFault = (WebFault) ConverterUtils.getAnnotation(
				WebFault.class, serviceClass);
		if(webFault != null) {
			WebFaultAnnot webFaultAnnot = WebFaultAnnot.createWebFaultAnnotImpl();
			webFaultAnnot.setFaultBean(webFault.faultBean());
			webFaultAnnot.setName(webFault.name());
			webFaultAnnot.setTargetNamespace(webFault.targetNamespace());
			composite.setWebFaultAnnot(webFaultAnnot);
		}
	}
	
	/**
	 * This method will be used to attach @WebServiceRefs annotation data to the 
	 * <code>DescriptionBuilderComposite</code>
	 * @param composite - <code>DescriptionBuilderComposite</code>
	 */
	private void attachWebServiceRefsAnnotation(DescriptionBuilderComposite composite) {
		WebServiceRefs webServiceRefs = (WebServiceRefs) ConverterUtils.getAnnotation(
				WebServiceRefs.class, serviceClass);
		if(webServiceRefs != null) {
			WebServiceRef[] refs = webServiceRefs.value();
			for(WebServiceRef ref : refs) {
				WebServiceRefAnnot wsrAnnot = ConverterUtils.createWebServiceRefAnnot(
						ref);
				composite.setWebServiceRefAnnot(wsrAnnot);
			}
		}
	}
	
	/**
	 * This method will be used to drive the setting of @WebServiceRef annotation 
	 * data to the <code>DescriptionBuilderComposite</code>
	 * @param composite - <code>DescriptionBuilderComposite</code>
	 */
	private void attachWebServiceRefAnnotation(DescriptionBuilderComposite composite) {
		ConverterUtils.attachWebServiceRefAnnotation(composite, serviceClass);
		
	}
	
	private void establishClassHierarchy(Class rootClass) {
		classes.add(rootClass);
		if(rootClass.getSuperclass() != null && !rootClass.getSuperclass().getName().
				equals("java.lang.Object")) {
			classes.add(rootClass.getSuperclass());	
			establishInterfaceHierarchy(rootClass.getSuperclass().getInterfaces());
			establishClassHierarchy(rootClass.getSuperclass());
		}
	}
	
	private void establishInterfaceHierarchy(Class[] interfaces) {
		if(interfaces.length > 0) {
			for(Class inter : interfaces) {
				classes.add(inter);
				establishInterfaceHierarchy(inter.getInterfaces());
			}
		}
	}
}
