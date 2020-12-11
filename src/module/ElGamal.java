package module;

import java.lang.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.security.*;

public class ElGamal
{

//    public String plainText;
//    public String cypherText;

    public BigInteger[] plainText;
    public BigInteger[] cypherText;
    public byte[] plainTextByte;
    public String plainTextString;

    BigInteger g,a,h,r,rm1,N,Nm1;
    MessageDigest digest;
    int keyLen=512; //ta wartość daje długość a=512
    int ilZnHex=keyLen/4;//ilość znaków hex wyświetlanych w polu klucza
    Random random=new Random();

    public ElGamal()
    {
        try{
            digest=MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {ex.printStackTrace();}
    }


    public void generateKey()
    {
        N = BigInteger.probablePrime(keyLen+2,new Random());
        a = new BigInteger(keyLen,new Random());
        g = new BigInteger(keyLen,new Random());
        h=g.modPow(a,N);
        Nm1=N.subtract(BigInteger.ONE);
    }


    public BigInteger[] encrypt(byte[] message)
    {      //generujemy nowe r dla każdego szyfrowania
        r = BigInteger.probablePrime(keyLen,new Random());
        while(true)
            if (r.gcd(Nm1).equals(BigInteger.ONE))break;
            else r=r.nextProbablePrime();
        int ileZnakow = (a.bitLength()-1)/8;
        boolean reszta=false;
        int chunks=0;
        if(message.length % ileZnakow != 0){chunks = (message.length/ ileZnakow)+1;reszta=true;}
        else chunks = message.length/ ileZnakow;
        BigInteger[] cipher = new BigInteger[chunks*2];
        if(!reszta)
        {
            for (int i = 0, j=0; i < chunks; i++,j+=2)
            {
                byte[] pom = Auxx.podtablica(message, ileZnakow*i, ileZnakow*(i+1));
                cipher[j] = new BigInteger(1, pom);
                cipher[j] = cipher[j].multiply(h.modPow(r,N)).mod(N);//C2
                cipher[j+1] = g.modPow(r,N);//C1
            }
        }
        else
        {
            for (int i = 0, j=0; i < chunks-1; i++,j+=2)
            {
                byte[] pom = Auxx.podtablica(message, ileZnakow*i, ileZnakow*(i+1));
                cipher[j] = new BigInteger(1, pom);
                cipher[j] = cipher[j].multiply(h.modPow(r,N)).mod(N);//C2
                cipher[j+1] = g.modPow(r,N);//C1
            }
            byte[] pom = Auxx.podtablica(message, ileZnakow*(chunks-1), message.length);
            cipher[(chunks-1)*2] = new BigInteger(1, pom);
            cipher[(chunks-1)*2] = cipher[(chunks-1)*2].multiply(h.modPow(r,N)).mod(N);//C2
            cipher[((chunks-1)*2)+1] = g.modPow(r,N);//C1
        }

        return cipher;
    }

    public BigInteger[] encrypt(String plainText)
    {   //generujemy nowe r dla każdego szyfrowania
        r = BigInteger.probablePrime(keyLen,new Random());
        while(true)
            if (r.gcd(Nm1).equals(BigInteger.ONE))break;
            else r=r.nextProbablePrime();
        int ileZnakow = (N.bitLength()-1)/8;
        StringBuilder plainTextBuilder = new StringBuilder(plainText);
        while (plainTextBuilder.length() % ileZnakow != 0)
            plainTextBuilder.append(' ');
        plainText = plainTextBuilder.toString();
        int chunks = plainText.length()/ ileZnakow;
        BigInteger[] cipher = new BigInteger[chunks*2];
        for (int i = 0,j=0; i < chunks; i++,j+=2)
        {
            String s = plainText.substring(ileZnakow*i,ileZnakow*(i+1));
            cipher[j] = Auxx.stringToBigInt(s);
            cipher[j] = cipher[j].multiply(h.modPow(r,N)).mod(N);//C2
            cipher[j+1] = g.modPow(r,N);//C1
        }
        return cipher;
    }
//
//    public String encryptFromStringToString()
//    {
//        StringBuilder str = new StringBuilder();
//        BigInteger[] bi_table = encrypt(plainText);
//        for(int i = 0; i < bi_table.length; i++)
//            str.append(bi_table[i]).append("\n");
//        return str.toString();
//    }

//    public String decrypt(BigInteger[] cypherText)
//    {
//        StringBuilder s = new StringBuilder();
//        for (int i = 0; i < cypherText.length; i+=2)
//        { s.append(Auxx.bigIntToString(cypherText[i].multiply(cypherText[i + 1].modPow(a, N).modInverse(N)).mod(N)));
//        }
//        return s.toString();
//    }

    public BigInteger[] decryptToBigInt(BigInteger[] cipher)
    {
        int len=(int)cipher.length/2;
        BigInteger[] wynik = new BigInteger[len];
        for (int i = 0, j=0; i < len; i++,j+=2)
            wynik[i] = cipher[j].multiply(cipher[j+1].modPow(a, N).modInverse(N)).mod(N);
        return wynik;
    }

//    public String decryptFromStringToString(String cypherText)
//    {
//        String[] wiersze = cypherText.split("\n");
//        BigInteger[] bi_table = new BigInteger[wiersze.length];
//        for(int i = 0; i < wiersze.length; i++)
//            bi_table[i] = new BigInteger(wiersze[i]);
//        return decrypt(bi_table);
//    }


    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }

    public BigInteger getA() {
        return a;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }

    public BigInteger getH() {
        return h;
    }

    public void setH(BigInteger h) {
        this.h = h;
    }

    public BigInteger getN() {
        return N;
    }

    public void setN(BigInteger n) {
        N = n;
    }

    public BigInteger getNm1() {
        return Nm1;
    }

    public void setNm1(BigInteger nm1) {
        Nm1 = nm1;
    }

    public BigInteger[] getCypherText() {
        return cypherText;
    }

    public void setCypherText(BigInteger[] cypherText) {
        this.cypherText = cypherText;
    }

    public BigInteger[] getPlainText() {
        return plainText;
    }

    public void setPlainText(BigInteger[] plainText) {
        this.plainText = plainText;
    }

    public byte[] getPlainTextByte() {
        return plainTextByte;
    }

    public void setPlainTextByte(byte[] plainTextByte) {
        this.plainTextByte = plainTextByte;
    }

    public String getPlainTextString() {
        return plainTextString;
    }

    public void setPlainTextString(String plaintText) {
        this.plainTextString = plaintText;
    }
}
