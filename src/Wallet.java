
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * icsd17222
 * icsd17159
 */
//klash gia ta kleidia rsa
public class Wallet {

    /**
     * @param args the command line arguments
     */
    public static PrivateKey privateKey;
    public static PublicKey publicKey;
        //h synarthsh(constructor) dhmiourgei ta private kai public key kai ta apothikeuei stis metavlhtes ths klashs
    public Wallet() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }
           //synarthsh h opoia apothikeyei ta kleidia se katallhlo arxeio wste na xrhshmopoihthoun kai stis epomenes ekteleseis
    public void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }
      //synarthsh h opoia dhmioyrgei to public key mesa apo ta bytes toy arxeiou to opoio einai apothikeumeno
    public static PublicKey getpublicFromFile(String filename)
            throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        X509EncodedKeySpec spec= new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
      //antistoixh synarthsh kai gia to private key
    public static PrivateKey getprivateFromFile(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        PKCS8EncodedKeySpec spec= new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
      //synarthseis oi opoies epistrefoun ta kleidia ths klashs
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, Exception {
        File f = new File("RSA"); 
        if (!f.exists()) { //se periptwsh pou o fakelos rsa den uparxei
            Wallet keyPairGenerator = new Wallet(); //dhmiourgounte ta kleidia kai apothikeuontai se antistoixa arxeia mesa ston fakelo
            keyPairGenerator.writeToFile("RSA/publicKey", keyPairGenerator.getPublicKey().getEncoded());
            keyPairGenerator.writeToFile("RSA/privateKey", keyPairGenerator.getPrivateKey().getEncoded());
        } else { //an uparxei o fakelos rsa pernei ta kleidia mesa apo ta antistoixa arxeia
            publicKey = getpublicFromFile("RSA/publicKey");
            privateKey = getprivateFromFile("RSA/privateKey");
        }
        new Main(false).setVisible(true);
        //trexei h klash main h opoia einai to menu ths efarmoghs

    }
}
