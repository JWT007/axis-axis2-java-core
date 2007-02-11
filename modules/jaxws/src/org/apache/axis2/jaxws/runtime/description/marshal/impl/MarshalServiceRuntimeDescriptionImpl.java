package org.apache.axis2.jaxws.runtime.description.marshal.impl;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.axis2.jaxws.ExceptionFactory;
import org.apache.axis2.jaxws.description.ServiceDescription;
import org.apache.axis2.jaxws.runtime.description.marshal.AnnotationDesc;
import org.apache.axis2.jaxws.runtime.description.marshal.MarshalServiceRuntimeDescription;
import org.apache.axis2.jaxws.utility.PropertyDescriptorPlus;
import org.apache.axis2.jaxws.utility.XMLRootElementUtil;


public class MarshalServiceRuntimeDescriptionImpl implements
        MarshalServiceRuntimeDescription {

    private ServiceDescription serviceDesc;
    private String key; 
    private TreeSet<String> packages;
    private Map<String, AnnotationDesc> annotationMap = null;
    private Map<Class, Map<String, PropertyDescriptorPlus>> pdMapCache = null;
    
    protected MarshalServiceRuntimeDescriptionImpl(String key,
                ServiceDescription serviceDesc) {
        this.serviceDesc = serviceDesc;
        this.key = key;
    }


    public ServiceDescription getServiceDescription() {
        return serviceDesc;
    }

    public String getKey() {
        return key;
    }

    public TreeSet<String> getPackages() {
        return packages;
    }

    void setPackages(TreeSet<String> packages) {
        this.packages = packages;
    }

    public AnnotationDesc getAnnotationDesc(Class cls) {
        String className = cls.getCanonicalName();
        AnnotationDesc aDesc = annotationMap.get(className);
        if (aDesc != null) {
            // Cache hit
            return aDesc;
        }
        // Cache miss...we cannot update the map because we don't want to introduce a sync call.
        aDesc = AnnotationDescImpl.create(cls);
        
        return aDesc;
    }
    
    
    void setAnnotationMap(Map<String, AnnotationDesc> map) {
        this.annotationMap = map;
    }


    public Map<String, PropertyDescriptorPlus> getPropertyDescriptorMap(Class cls) {
        // We are caching by class.  
        Map<String, PropertyDescriptorPlus> pdMap = pdMapCache.get(cls);
        if (pdMap != null) {
            // Cache hit
            return pdMap;
        }
        
        // Cache miss...this can occur if the classloader changed.
        // We cannot add this new pdMap at this point due to sync issues.
        try {
            pdMap = XMLRootElementUtil.createPropertyDescriptorMap(cls);
        } catch (Throwable t) {
            ExceptionFactory.makeWebServiceException(t);
        }
        return pdMap;
    }
    
    void setPropertyDescriptorMapCache(Map<Class, Map<String, PropertyDescriptorPlus>> cache) {
        this.pdMapCache = cache;
    }
    
    public String toString() {
        final String newline = "\n";
        StringBuffer string = new StringBuffer();
        
        string.append(newline);
        string.append("  MarshalServiceRuntime:" + getKey());
        string.append(newline);
        string.append("    Packages = " + getPackages().toString());
        for(Entry<String, AnnotationDesc> entry: this.annotationMap.entrySet()) {
            string.append(newline);
            string.append("    AnnotationDesc cached for:" + entry.getKey());
            string.append(entry.getValue().toString());
        }
        
        for(Entry<Class, Map<String, PropertyDescriptorPlus>> entry: this.pdMapCache.entrySet()) {
            string.append(newline);
            string.append("    PropertyDescriptorPlus Map cached for:" + entry.getKey().getCanonicalName());
            for (PropertyDescriptorPlus pdp:entry.getValue().values()) {
                string.append(newline);
                string.append("      propertyName   =" + pdp.getPropertyName());
                string.append(newline);
                string.append("        xmlName      =" + pdp.getXmlName());
                string.append(newline);
                string.append("        propertyType =" + pdp.getPropertyType().getCanonicalName());
                string.append(newline);
            }
        }
        
        return string.toString();
    }
}
