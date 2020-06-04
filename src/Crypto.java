
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

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
//synarthsh crypto h opoia afora kruptografikes synarthseis
public class Crypto {

    public static Cipher cipher;
    public static Cipher aescipher;

    public Crypto() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance("RSA");
        this.aescipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    }

   //synarthsh h opoia kanei encrypt ena string me to public key kai epistrefei to antistoixo string
    public String encryptText(String msg, PublicKey key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            UnsupportedEncodingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeBase64String(cipher.doFinal(msg.getBytes("UTF-8")));
    }
  //synarthsh h opoia kanei decrypt ena string me to private key kai epistrefei to antisoixo string
    
    public String decryptText(String msg, PrivateKey key)
            throws InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
    }
  //synarthsh h opoia ftiaxnei ena byte array kai pernaei ola ta stoixeia tou arxeiou se eauto
    public static byte[] filetobyte(File file) throws IOException {
        byte[] bytesArray = new byte[(int) file.length()];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AddCardUI.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(AddCardUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return bytesArray;
    }
  //synarthsh h opoia kanei encrypt ta antikeimena twn kartwn me to aes key kai ta apothikeyei se antistoixo arxeio
    public void aesencryptcard(Card c) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;   //dhmiourgeia twn stream kai appendable wste o header na xanagurnaei sthn arxh kathe fora pou ginontai polles eisagwges kartas
        FileOutputStream fos2 = null;
        AppendableObjectOutputStream aos = null;
        try {
            SecretKey aeskey = getaes(); //pernei to keidi aes kai kanei initialize twn cipher tou aes me to kleidi kai me ivparameter to parakatw
            this.aescipher.init(Cipher.ENCRYPT_MODE, aeskey, new IvParameterSpec(new String("encryptionIntVec").getBytes("UTF-8")));
            // Create stream
            File fc = new File(LoginUI.u.getUsername() + "/Cards");
            if (fc.createNewFile()) { //an to arxeio me tis kartes tou xrhsth den uparxei dhmiourgeite
                fos = new FileOutputStream(fc);
                oos = new ObjectOutputStream(fos);   //ftiaxnete ela sealed object ths kartas me twn cipher
                SealedObject sealed = new SealedObject(c, this.aescipher);
                oos.writeObject(sealed); //kai epeita pernaei sto arxeio
                
                oos.flush();
                oos.close();
                fos.close();

            } else { //an to arxeio me tis kartes uparxei prepei na kanooume append sto arxeio me ton parakatw troo
                fos2 = new FileOutputStream(fc, true);
                aos = new AppendableObjectOutputStream(fos2);
                SealedObject sealed = new SealedObject(c, this.aescipher);
                aos.writeObject(sealed);
               
                aos.flush();
                aos.close();
                fos2.close();
            }

        } catch (IOException | InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
     //synarthsh h opoia kanei decrypt to arxeio twn kartwn kai epistrefei tis kartes se mia lista
    public ArrayList<Card> aesdecryptcards() throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, IOException {

        ArrayList<Card> list = new ArrayList<Card>();  //arxikopoihsh metavlhtwn
        FileInputStream f2 = null;
        ObjectInputStream inputStream = null;
        try {
            SecretKey aeskey = getaes(); //pernei to kleidi aes kai kanei init ton cipher gia decrypt mode
            this.aescipher.init(Cipher.DECRYPT_MODE, aeskey, new IvParameterSpec(new String("encryptionIntVec").getBytes("UTF-8")));
            f2 = new FileInputStream(LoginUI.u.getUsername() + "/Cards");
            inputStream = new ObjectInputStream(f2); //thetei ta streams
            boolean state = true;
            while (state) { //oso einai true pernei to sealed object apo to input kai to kanei antikeimeno card..epeita to vazei sthn lista
                SealedObject sealedObject = (SealedObject) inputStream.readObject();
                Card c = (Card) sealedObject.getObject(this.aescipher);
                
                list.add(c);
            }

        } catch (EOFException ex) {
            //an teleiwsei to arxeio epistrefei thn lista
            
            return list;
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            f2.close();
            inputStream.close();
        }
        return list;
    }
//synarthsh h opoia pernei to kleidi aes apo to arxeio kai to epistrefei
    public SecretKey getaes() {
        SecretKey aeskey = null;
        try {
            File f = new File(LoginUI.u.getUsername() + "/Aes");
            byte[] aes = filetobyte(f); //vazei se bytearrray ta bytes apo to arxeio me to symmetriko kleidi
            //kanei decrypt to aes me thn xrhsh tou private key
            String aesstring = decryptText(Base64.encodeBase64String(aes), Wallet.privateKey);
            //dhmiourgei ta bytes tou decrypted aes key
            byte[] decodedKey = Base64.decodeBase64(aesstring);
            aeskey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            //dhmiourgei to secretkey kai sto telos to epistrefei
        } catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aeskey;
    }
  //sunarthsh gia thn akeraiothta twn arxeiwn
    public void saveuserfiles() {
        try {
              //dhmiourgei mia upografh gia hash kai rsa
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(Wallet.privateKey); //eisagei to privatekey sthn upografh
            byte[] secondhash=doublehash(); //ektelei thn sunarthsh doublehash
            signature.update(secondhash);  //kanei me ta bytes
            byte[] digitalSignature = signature.sign();  //dhmiourgei thn psifiakh upografh kai thn pernaei sto digitalsignature

            Crypto cr = new Crypto();  //pernei to kleidi aes
            SecretKey aeskey = cr.getaes();
            //kanei encrypt to digital signature me to summetriko kleidi 
            this.aescipher.init(Cipher.ENCRYPT_MODE, aeskey, new IvParameterSpec(new String("encryptionIntVec").getBytes("UTF-8")));
            byte[] aessignature = this.aescipher.doFinal(digitalSignature);
             //to pernaei se arxeio digitalsignature
            FileUtils.writeByteArrayToFile(new File(LoginUI.u.getUsername() + "/digitalsignature"), aessignature);

        } catch (NoSuchAlgorithmException | IOException | NoSuchPaddingException | InvalidKeyException | SignatureException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
//sunarthsh h opoia elegxei thn digital signature
    public void checkfiles() {
        File f = new File(LoginUI.u.getUsername() + "/digitalsignature");
        try {  //pernaei ta bytes toy arxeiou se metavlhth filebytes
            byte[] filebytes = filetobyte(f);
            SecretKey aeskey = getaes(); //pernei to aes tou xrhsth kai kanei decrypt 
            this.aescipher.init(Cipher.DECRYPT_MODE, aeskey, new IvParameterSpec(new String("encryptionIntVec").getBytes("UTF-8")));
            byte[] unaes = this.aescipher.doFinal(filebytes);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(Wallet.publicKey);  //vazei sthn digital signature to public key
            signature.update(doublehash()); //kanei thn sunarthsh doublehash kai h timh ths mpenei update sthn upografh
            boolean isCorrect = signature.verify(unaes); //an h upografh apo to arxeio einai idia me auth tou doublehash(dhladh verify)
            
            if(isCorrect==true)
            {
                JOptionPane.showMessageDialog(null,"Your files are intact");
            }
            else
            {
                JOptionPane.showMessageDialog(null,"Sorry you ve been hacked,decline all your cards");
            }

        } catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | SignatureException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 //synarthsh gia hash tou arxiou kartwn kai xana hash sto <username,digest>
    public byte[] doublehash() throws NoSuchAlgorithmException {
        File f = new File(LoginUI.u.getUsername() + "/Cards");
        byte[] secondhash = null;
        try {      //pernei ola ta dedomena apo to arxeio cards kai ta kanei hash me sha256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            DigestInputStream dis = new DigestInputStream(new FileInputStream(f), md);
            while (dis.read() != -1) ; //empty loop to clear the data
            md = dis.getMessageDigest();
            StringBuilder result = new StringBuilder();
            for (byte b : md.digest()) {
                result.append(String.format("%02x", b));
            }
           
    //dhmiourgei zeugos me username kai hash tou arxeiou
            Pair<String, String> pairb = new Pair<>(LoginUI.u.getUsername(), result.toString());
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //kanei xana hash sto zeugari
            secondhash = digest.digest(pairb.toString().trim().getBytes("UTF-8"));
        
        }  
          catch (NoSuchAlgorithmException | FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
       
     //epistrefei to hash tou zeugariou
        return secondhash;
       
    }

}
