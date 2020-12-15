package module;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class ElGamal
{

    public BigInteger[] plainText;
    public BigInteger[] cypherText;
    public byte[] plainTextByte;

    BigInteger g;
    BigInteger a;
    BigInteger h;
    BigInteger r;
    BigInteger N;
    BigInteger Nm1;
    int keyLen=512;

    public void generateKey() {
        N = BigInteger.probablePrime(keyLen+2,new Random());
        a = new BigInteger(keyLen,new Random());
        g = new BigInteger(keyLen,new Random());
        h = g.modPow(a,N);
        Nm1  =N.subtract(BigInteger.ONE);
    }

    public BigInteger[] encrypt(byte[] message) {
        //generujemy nowe r dla każdego szyfrowania
        r = BigInteger.probablePrime(keyLen, new Random());
        while(true) {
            if (r.gcd(Nm1).equals(BigInteger.ONE)) {
                break;
            }
            else {
                r = r.nextProbablePrime();
            }
        }
        int signs = (a.bitLength()-1) / 8;
        boolean rest = false; //RESZTA
        int chunks = 0;
        if(message.length % signs != 0) {
            chunks = (message.length/ signs)+1;
            rest = true;
        }
        else {
            chunks = message.length/ signs;
        }
        BigInteger[] cipher = new BigInteger[chunks*2];
        if(!rest) {
            for (int i = 0, j = 0; i < chunks; i++, j+=2) {
                byte[] pom = subtable(message, signs * i, signs * (i + 1));
                cipher[j] = new BigInteger(1, pom);
                cipher[j] = cipher[j].multiply(h.modPow(r,N)).mod(N);//C2
                cipher[j+1] = g.modPow(r,N);//C1
            }
        }
        else {
            for (int i = 0, j = 0; i < chunks-1; i++, j+=2) {
                byte[] pom = subtable(message, signs * i, signs * (i + 1));
                cipher[j] = new BigInteger(1, pom);
                cipher[j] = cipher[j].multiply(h.modPow(r,N)).mod(N);//C2
                cipher[j+1] = g.modPow(r,N);//C1
            }
            byte[] pom = subtable(message, signs*(chunks-1), message.length);
            cipher[(chunks-1) * 2] = new BigInteger(1, pom);
            cipher[(chunks-1) * 2] = cipher[(chunks-1) * 2].multiply(h.modPow(r,N)).mod(N);//C2
            cipher[((chunks-1) * 2) + 1] = g.modPow(r,N);//C1
        }
        return cipher;
    }

    public BigInteger[] decryptToBigInt(BigInteger[] cipher) {
        int len= cipher.length / 2;
        BigInteger[] result = new BigInteger[len];
        for (int i = 0, j = 0; i < len; i++, j+=2)
            result[i] = cipher[j].multiply(cipher[j+1].modPow(a, N).modInverse(N)).mod(N);
        return result;
    }

    public static byte[] subtable(byte[] dane, int poczatek, int koniec) {
        return Arrays.copyOfRange(dane, poczatek, koniec);
    }


//    public static BigInteger stringToBigInt(String str) {
//        byte[] tab = new byte[str.length()];
//        for (int i = 0; i < tab.length; i++)
//            tab[i] = (byte)str.charAt(i);
//        return new BigInteger(1,tab);
//    }
//
//    public static String bigIntToString(BigInteger n) {
//        byte[] tab = n.toByteArray();
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < tab.length; i++)
//            sb.append((char)tab[i]);
//        return sb.toString();
//    }


    //GETTERS & SETTERS

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

}
