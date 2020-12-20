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
        p = BigInteger.probablePrime(keyLen+2,new Random()); //Liczba pierwsza
        pm1 = p.subtract(BigInteger.ONE);

        //PRIVATE KEY
        x = new BigInteger(keyLen,new Random());

        //PUBLIC KEY
        g = new BigInteger(keyLen,new Random());
        y = g.modPow(x, p);
    }

    public BigInteger[] encrypt() {
        //nowe k dla kazdego szyfrowania
        k = BigInteger.probablePrime(keyLen, new Random());
        while(true) {
            if (k.gcd(pm1).equals(BigInteger.ONE)) { // k wzglednie pierwsze do pm1
                break;
            }
            else {
                k = k.nextProbablePrime();
            }
        }

        int signs = (x.bitLength()-1) / 8; // ilosc bajtow
        boolean rest = false; //RESZTA
        int block;
        if(plainTextByte.length % signs != 0) { // M % sings != 0
            block = (plainTextByte.length/ signs)+1; // ilosc blokow do szyfrowania - M % sings + 1 (bo reszta)
            rest = true;
        }
        else {
            block = plainTextByte.length/ signs; // ilosc blokow do szyfrowania - M % sings (bez reszty)
        }
        BigInteger[] cipher = new BigInteger[block*2]; // dwa razy wiecej miejsca bo obliczamy a i b
        if(!rest) { // Jezeli nie ma reszty
            for (int i = 0, j = 0; i < block; i++, j+=2) {
                byte[] pom = subtable(plainTextByte, signs * i, signs * (i + 1)); // Bierzemy pierwszy block
                cipher[j] = new BigInteger(1, pom); // Message
                cipher[j] = cipher[j].multiply(y.modPow(k, p)).mod(p);//b
                cipher[j+1] = g.modPow(k, p);//a
            }
        }
        else {
            for (int i = 0, j = 0; i < block-1; i++, j+=2) {
                byte[] pom = subtable(plainTextByte, signs * i, signs * (i + 1));
                cipher[j] = new BigInteger(1, pom);
                cipher[j] = cipher[j].multiply(y.modPow(k, p)).mod(p);//C2
                cipher[j+1] = g.modPow(k, p);//C1
            }
            //DODATKOWY BLOCK
            byte[] pom = subtable(plainTextByte, signs*(block-1), plainTextByte.length);
            cipher[(block-1) * 2] = new BigInteger(1, pom);
            cipher[(block-1) * 2] = cipher[(block-1) * 2].multiply(y.modPow(k, p)).mod(p);//C2
            cipher[((block-1) * 2) + 1] = g.modPow(k, p);//C1
        }
        return cipher;
    }

    public BigInteger[] decryptToBigInt() {
        int len = cypherText.length / 2;
        BigInteger[] result = new BigInteger[len];
        for (int i = 0, j = 0; i < len; i++, j+=2) {
            result[i] = cypherText[j].multiply(cypherText[j+1].modPow(x, p).modInverse(p)).mod(p);
        }
        return result;
    }

    public static byte[] subtable(byte[] dane, int poczatek, int koniec) {
        return Arrays.copyOfRange(dane, poczatek, koniec);
    }

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
