//import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
//import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.openssl.PEMParser;
//import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
//import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.security.Security;
//import java.security.Signature;
//import java.security.interfaces.RSAPrivateKey;
//import java.security.interfaces.RSAPublicKey;
//
//public class BouncyCastlePemUtils {
//
//    public static void main(String[] args) throws Exception {
//        String data = "this is data";
//        RSAPublicKey rsaPublicKey = readX509PublicKeySecondApproach();
//        RSAPrivateKey rsaPrivateKey = readPKCS8PrivateKeySecondApproach();
//
//        Security.addProvider(new BouncyCastleProvider());
//        String dataSign = signData(rsaPrivateKey, "data");
//        boolean status = verifySignature("data", dataSign, rsaPublicKey);
//
//        System.out.println(status);
//
//    }
//
//    public static String signData(RSAPrivateKey privateKey, String text) throws Exception {
//        Signature rsaSign = Signature.getInstance("SHA256withRSA", "BC");
//        rsaSign.initSign(privateKey);
//        rsaSign.update(text.getBytes("UTF-8"));
//        byte[] signature = rsaSign.sign();
//        return Base64.toBase64String(signature);
//    }
//
//    public static boolean verifySignature(String text, String signature, RSAPublicKey pubKey) throws Exception {
//        Signature rsaVerify = Signature.getInstance("SHA256withRSA", "BC");
//        rsaVerify.initVerify(pubKey);
//        rsaVerify.update(text.getBytes("UTF-8"));
//        return rsaVerify.verify(Base64.decode(signature));
//    }
//
//    public static RSAPublicKey readX509PublicKeySecondApproach() throws IOException {
//        try (FileReader keyReader = new FileReader("./public-key.pem")) {
//
//            PEMParser pemParser = new PEMParser(keyReader);
//            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
//            SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
//
//            return (RSAPublicKey) converter.getPublicKey(publicKeyInfo);
//        }
//    }
//
//    public static RSAPrivateKey readPKCS8PrivateKeySecondApproach() throws IOException {
//        try (FileReader keyReader = new FileReader("./private-key-pkcs8.pem")) {
//
//            PEMParser pemParser = new PEMParser(keyReader);
//            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
//            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());
//
//            return (RSAPrivateKey) converter.getPrivateKey(privateKeyInfo);
//        }
//    }
//
//}