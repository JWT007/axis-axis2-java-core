import javax.xml.namespace.QName;

import org.apache.axis.context.MessageContext;
import org.apache.axis.engine.AxisFault;
import org.apache.axis.engine.Handler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.axis.handlers.AbstractHandler;

public class Handler2  extends AbstractHandler implements Handler {
    private Log log = LogFactory.getLog(getClass());
    private String message;
    private QName name;
    public Handler2() {
       this.message = "inside service 2";
    }
    public QName getName() {
        return name;
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.info("I am " + message + " Handler Running :)");
    }

    public void revoke(MessageContext msgContext) {
        log.info("I am " + message + " Handler Running :)");
    }

    public void setName(QName name) {
        this.name = name;
    }

}
