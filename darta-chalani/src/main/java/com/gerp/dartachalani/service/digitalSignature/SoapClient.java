package com.gerp.dartachalani.service.digitalSignature;

import com.gerp.dartachalani.service.digitalSignature.client.gen.VerifyDetached;
import com.gerp.dartachalani.service.digitalSignature.client.gen.VerifyDetachedResponse;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.bind.JAXBElement;


@Service
public class SoapClient {
    private Logger logger = LoggerFactory.getLogger(SoapClient.class);

    @Autowired
    public Jaxb2Marshaller marshaller;

    public WebServiceTemplate template;

    public VerificationInformation getResponse(VerifyDetached verify){
        template=new WebServiceTemplate(marshaller);
        JAXBElement<VerifyDetachedResponse> response= null;//(JAXBElement<VerifyDetachedResponse>)
               logger.info(" response "+(String) template.marshalSendAndReceive("http://emas.radiantnepal.com:8082/emas3/services/dsverifyWS",verify));

        JSONObject soapDatainJsonObject = XML.toJSONObject(response.getValue().getReturn());
        System.out.println(soapDatainJsonObject);

        VerificationInformation information=new VerificationInformation();

//        System.out.println("user name is"+soapDatainJsonObject.getJSONObject("verificationProfile")
//                .getJSONObject("transaction")
//                .getJSONObject("transactionStatus")
//                .getJSONObject("transactionStatusDetails")
//                .getJSONObject("certificate")
//                .get("commonName").toString());

        if(soapDatainJsonObject.getJSONObject("verificationProfile").get("status").equals("success")){

            information=getVerifificationInformation(soapDatainJsonObject);

        }else{
            information.setStatus(HttpStatus.EXPECTATION_FAILED);
            information.setMessage("signature verification process failed");
        }

        System.out.println(information.toString());

        return information;
    }

    private VerificationInformation getVerifificationInformation(JSONObject soapDatainJsonObject) {
        VerificationInformation information=new VerificationInformation();
        information.setMessage("success");
        information.setStatus(HttpStatus.OK);
        information.setSignature_name(soapDatainJsonObject.getJSONObject("verificationProfile")
                .getJSONObject("transaction")
                .getJSONObject("transactionStatus")
                .getJSONObject("transactionStatusDetails")
                .getJSONObject("certificate")
                .get("commonName").toString());

        information.setEmail(soapDatainJsonObject.getJSONObject("verificationProfile")
                .getJSONObject("transaction")
                .getJSONObject("transactionStatus")
                .getJSONObject("transactionStatusDetails")
                .getJSONObject("certificate")
                .get("email").toString());

        information.setOrganisation(soapDatainJsonObject.getJSONObject("verificationProfile")
                .getJSONObject("transaction")
                .getJSONObject("transactionStatus")
                .getJSONObject("transactionStatusDetails")
                .getJSONObject("certificate")
                .get("organisation").toString());

        information.setLocality(soapDatainJsonObject.getJSONObject("verificationProfile")
                .getJSONObject("transaction")
                .getJSONObject("transactionStatus")
                .getJSONObject("transactionStatusDetails")
                .getJSONObject("certificate")
                .get("locality").toString());


        information.setState(soapDatainJsonObject.getJSONObject("verificationProfile")
                .getJSONObject("transaction")
                .getJSONObject("transactionStatus")
                .getJSONObject("transactionStatusDetails")
                .getJSONObject("certificate")
                .get("state").toString());


        information.setIssuerName(soapDatainJsonObject.getJSONObject("verificationProfile")
                .getJSONObject("transaction")
                .getJSONObject("transactionStatus")
                .getJSONObject("transactionStatusDetails")
                .getJSONObject("certificate")
                .get("issuerCommonName").toString());


        return  information;
    }
}
