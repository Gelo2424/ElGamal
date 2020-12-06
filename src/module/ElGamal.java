package module;

import java.lang.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.security.*;

public class ElGamal
{
    class ElGamalKeyException extends Exception {
        public ElGamalKeyException(String msg){super(msg);};
    }

    public String plainText;
    public String cypherText;

    BigInteger g,a,h,r,rm1,N,Nm1;
    MessageDigest digest;
    int keyLen=512; //ta wartość daje długość a=512
    int ilZnHex=keyLen/4;//ilość znaków hex wyświetlanych w polu klucza
    Random random=new Random();

    public ElGamal()
    {
        generateKey();
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


//    public BigInteger[] encrypt()
//    {      //generujemy nowe r dla każdego szyfrowania
//        r = BigInteger.probablePrime(keyLen,new Random());
//        while(true)
//            if (r.gcd(Nm1).equals(BigInteger.ONE))break;
//            else r=r.nextProbablePrime();
//        int ileZnakow = (a.bitLength()-1)/8;
//        boolean reszta=false;
//        int chunks=0;
//        if(plainText.length % ileZnakow != 0){chunks = (plainText.length/ ileZnakow)+1;reszta=true;}
//        else chunks = plainText.length/ ileZnakow;
//        BigInteger[] cipher = new BigInteger[chunks*2];
//        if(!reszta)
//        {
//            for (int i = 0, j=0; i < chunks; i++,j+=2)
//            {
//                byte[] pom = Auxx.podtablica(plainText, ileZnakow*i, ileZnakow*(i+1));
//                cipher[j] = new BigInteger(1, pom);
//                cipher[j] = cipher[j].multiply(h.modPow(r,N)).mod(N);//C2
//                cipher[j+1] = g.modPow(r,N);//C1
//            }
//        }
//        else
//        {
//            for (int i = 0, j=0; i < chunks-1; i++,j+=2)
//            {
//                byte[] pom = Auxx.podtablica(plainText, ileZnakow*i, ileZnakow*(i+1));
//                cipher[j] = new BigInteger(1, pom);
//                cipher[j] = cipher[j].multiply(h.modPow(r,N)).mod(N);//C2
//                cipher[j+1] = g.modPow(r,N);//C1
//            }
//            byte[] pom = Auxx.podtablica(plainText, ileZnakow*(chunks-1), plainText.length);
//            cipher[(chunks-1)*2] = new BigInteger(1, pom);
//            cipher[(chunks-1)*2] = cipher[(chunks-1)*2].multiply(h.modPow(r,N)).mod(N);//C2
//            cipher[((chunks-1)*2)+1] = g.modPow(r,N);//C1
//        }
//
//        return cipher;
//    }

    public BigInteger[] encrypt(String plainText)
    {   //generujemy nowe r dla każdego szyfrowania
        r = BigInteger.probablePrime(keyLen,new Random());
        while(true)
            if (r.gcd(Nm1).equals(BigInteger.ONE))break;
            else r=r.nextProbablePrime();
        int ileZnakow = (N.bitLength()-1)/8;
        while (plainText.length() % ileZnakow != 0)
            plainText += ' ';
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

    public String encryptFromStringToString()
    {
        StringBuilder str = new StringBuilder();
        BigInteger[] bi_table = encrypt(plainText);
        for(int i = 0; i < bi_table.length; i++)
            str.append(bi_table[i]).append("\n");
        return str.toString();
    }

    public String decrypt(BigInteger[] cypherText)
    {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < cypherText.length; i+=2)
        { s.append(Auxx.bigIntToString(cypherText[i].multiply(cypherText[i + 1].modPow(a, N).modInverse(N)).mod(N)));
        }
        return s.toString();
    }

    public BigInteger[] decryptToBigInt(BigInteger[] cipher)
    {
        int len=(int)cipher.length/2;
        BigInteger[] wynik = new BigInteger[len];
        for (int i = 0, j=0; i < len; i++,j+=2)
            wynik[i] = cipher[j].multiply(cipher[j+1].modPow(a, N).modInverse(N)).mod(N);
        return wynik;
    }

    public String decryptFromStringToString(String cypherText)
    {
        String[] wiersze = cypherText.split("\n");
        BigInteger[] bi_table = new BigInteger[wiersze.length];
        for(int i = 0; i < wiersze.length; i++)
            bi_table[i] = new BigInteger(wiersze[i]);
        return decrypt(bi_table);
    }


    public BigInteger[] podpisuj(byte[] tekst)
    {
        digest.update(tekst);
        BigInteger podpis=new BigInteger(1, digest.digest());
        //generujemy nowe r dla każdego podpisu
        r = BigInteger.probablePrime(keyLen,new Random());
        while(true)
            if (r.gcd(Nm1).equals(BigInteger.ONE))break;
            else r=r.nextProbablePrime();
        rm1=r.modInverse(Nm1);
        BigInteger S1=g.modPow(r, N);
        BigInteger S2=podpis.subtract(a.multiply(S1)).multiply(rm1).mod(Nm1);
        BigInteger wynik[]=new BigInteger[2];
        wynik[0]=S1;
        wynik[1]=S2;
        return wynik;
    }

    public BigInteger[] podpisuj(String tekst)
    {
        digest.update(tekst.getBytes());
        BigInteger podpis=new BigInteger(1, digest.digest());
        //generujemy nowe r dla każdego podpisu
        r = BigInteger.probablePrime(keyLen,new Random());
        while(true)
            if (r.gcd(Nm1).equals(BigInteger.ONE))break;
            else r=r.nextProbablePrime();
        rm1=r.modInverse(Nm1);
        BigInteger S1=g.modPow(r, N);
        BigInteger S2=podpis.subtract(a.multiply(S1)).multiply(rm1).mod(Nm1);
        BigInteger wynik[]=new BigInteger[2];
        wynik[0]=S1;
        wynik[1]=S2;
        return wynik;
    }


    public boolean weryfikujBigInt(byte[] tekstJawny, BigInteger[] podpis)
    {
        digest.update(tekstJawny);
        BigInteger hash = new BigInteger(1, digest.digest());
        BigInteger wynik1=g.modPow(hash, N);
        BigInteger wynik2=h.modPow(podpis[0], N).multiply(podpis[0].modPow(podpis[1], N)).mod(N);
        if(wynik1.compareTo(wynik2)==0)return true; else return false;
    }



    //zakładamy, że podpis jest w postaci hexadecymalnych znaków
    public boolean weryfikujString(String tekstJawny, String podpis)
    {
        digest.update(tekstJawny.getBytes());
        BigInteger hash = new BigInteger(1, digest.digest());
        String tab[]=podpis.split("\n");
        BigInteger S1=new BigInteger(1,Auxx.hexToBytes(tab[0]));
        BigInteger S2=new BigInteger(1,Auxx.hexToBytes(tab[1]));
        BigInteger wynik1=g.modPow(hash, N);
        BigInteger wynik2=h.modPow(S1, N).multiply(S1.modPow(S2, N)).mod(N);
        if(wynik1.compareTo(wynik2)==0)return true; else return false;
    }

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

    public String getPlainText() {
        return this.plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public String getCypherText() {
        return cypherText;
    }

    public void setCypherText(String cypherText) {
        this.cypherText = cypherText;
    }
}
