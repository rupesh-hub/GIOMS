
package com.gerp.dartachalani.emas.ws.ds;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.gerp.dartachalani.emas.ws.ds package.
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Verify_QNAME = new QName("http://ds.ws.emas/", "verify");
    private final static QName _VerifyDetached_QNAME = new QName("http://ds.ws.emas/", "verifyDetached");
    private final static QName _VerifyDetachedResponse_QNAME = new QName("http://ds.ws.emas/", "verifyDetachedResponse");
    private final static QName _VerifyResponse_QNAME = new QName("http://ds.ws.emas/", "verifyResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.gerp.dartachalani.emas.ws.ds
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Verify }
     * 
     */
    public Verify createVerify() {
        return new Verify();
    }

    /**
     * Create an instance of {@link VerifyDetached }
     * 
     */
    public VerifyDetached createVerifyDetached() {
        return new VerifyDetached();
    }

    /**
     * Create an instance of {@link VerifyDetachedResponse }
     * 
     */
    public VerifyDetachedResponse createVerifyDetachedResponse() {
        return new VerifyDetachedResponse();
    }

    /**
     * Create an instance of {@link VerifyResponse }
     * 
     */
    public VerifyResponse createVerifyResponse() {
        return new VerifyResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Verify }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Verify }{@code >}
     */
    @XmlElementDecl(namespace = "http://ds.ws.emas/", name = "verify")
    public JAXBElement<Verify> createVerify(Verify value) {
        return new JAXBElement<Verify>(_Verify_QNAME, Verify.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyDetached }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link VerifyDetached }{@code >}
     */
    @XmlElementDecl(namespace = "http://ds.ws.emas/", name = "verifyDetached")
    public JAXBElement<VerifyDetached> createVerifyDetached(VerifyDetached value) {
        return new JAXBElement<VerifyDetached>(_VerifyDetached_QNAME, VerifyDetached.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyDetachedResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link VerifyDetachedResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ds.ws.emas/", name = "verifyDetachedResponse")
    public JAXBElement<VerifyDetachedResponse> createVerifyDetachedResponse(VerifyDetachedResponse value) {
        return new JAXBElement<VerifyDetachedResponse>(_VerifyDetachedResponse_QNAME, VerifyDetachedResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link VerifyResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ds.ws.emas/", name = "verifyResponse")
    public JAXBElement<VerifyResponse> createVerifyResponse(VerifyResponse value) {
        return new JAXBElement<VerifyResponse>(_VerifyResponse_QNAME, VerifyResponse.class, null, value);
    }

}
