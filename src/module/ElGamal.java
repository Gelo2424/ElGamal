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
    BigInteger x;
    BigInteger y;
    BigInteger k;
    BigInteger p;
    BigInteger pm1;
    int keyLen=512;

    public void generateKey() {
        //PRIVATE KEY
        x = new BigInteger(keyLen,new Random());

        //PUBLIC KEY
        g = new BigInteger(keyLen,new Random());
        y = g.modPow(x, p);

        p = BigInteger.probablePrime(keyLen+2,new Random());
        pm1 = p.subtract(BigInteger.ONE);
    }

    public BigInteger[] encrypt(byte[] message) {
        //nowe k dla kazdego szyfrowania
        k = BigInteger.probablePrime(keyLen, new Random());
        while(true) {
            if (k.gcd(pm1).equals(BigInteger.ONE)) {
                break;
            }
            else {
                k = k.nextProbablePrime();
            }
        }
        int signs = (x.bitLength()-1) / 8;
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
                cipher[j] = cipher[j].multiply(y.modPow(k, p)).mod(p);//C2
                cipher[j+1] = g.modPow(k, p);//C1
            }
        }
        else {
            for (int i = 0, j = 0; i < chunks-1; i++, j+=2) {
                byte[] pom = subtable(message, signs * i, signs * (i + 1));
                cipher[j] = new BigInteger(1, pom);
                cipher[j] = cipher[j].multiply(y.modPow(k, p)).mod(p);//C2
                cipher[j+1] = g.modPow(k, p);//C1
            }
            byte[] pom = subtable(message, signs*(chunks-1), message.length);
            cipher[(chunks-1) * 2] = new BigInteger(1, pom);
            cipher[(chunks-1) * 2] = cipher[(chunks-1) * 2].multiply(y.modPow(k, p)).mod(p);//C2
            cipher[((chunks-1) * 2) + 1] = g.modPow(k, p);//C1
        }
        return cipher;
    }

    public BigInteger[] decryptToBigInt(BigInteger[] cipher) {
        int len= cipher.length / 2;
        BigInteger[] result = new BigInteger[len];
        for (int i = 0, j = 0; i < len; i++, j+=2)
            result[i] = cipher[j].multiply(cipher[j+1].modPow(x, p).modInverse(p)).mod(p);
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

    public BigInteger getX() {
        return x;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public BigInteger getY() {
        return y;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public void setPm1(BigInteger pm1) {
        this.pm1 = pm1;
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
