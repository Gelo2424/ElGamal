package module;

import java.math.BigInteger;
import java.util.Random;

public class ElGamal
{

    public BigInteger[] cypherText;
    public byte[] plainTextByte;

    private static int keyLen=1024;

    BigInteger p;//
    BigInteger g;//PUBLIC KEY
    BigInteger y;//

    BigInteger x;//PRIVATE KEY

    BigInteger pm1;// = p - 1 // To generate k
    BigInteger k;// Relatively prime to pm1


    public void generateKey() {
        p = BigInteger.probablePrime(keyLen+2,new Random());
        //PRIVATE KEY
        x = new BigInteger(keyLen,new Random());
        //PUBLIC KEY
        g = new BigInteger(keyLen,new Random());
        y = g.modPow(x, p);


        pm1 = p.subtract(BigInteger.ONE);

    }

    private void generateK() {
        k = BigInteger.probablePrime(keyLen, new Random());
        //Moze trzeba sprawdzic czy K nie jest wieksze od p - 1(ewentualnie).
        while(true) {
            if (k.gcd(pm1).equals(BigInteger.ONE)) {
                break;
            }
            else {
                k = k.nextProbablePrime();
            }
        }
    }

    private BigInteger generateA() {
        BigInteger a = g.modPow(k, p);
        return a;
    }

    private BigInteger encryptBlock(BigInteger block) {
        BigInteger b = block.multiply(y.modPow(k, p)).mod(p);
        return b;
    }

    public BigInteger[] encrypt() {
        generateK();

        int howManyBlocks;
        int lastLength = plainTextByte.length % 127;
        if (lastLength != 0) {
            howManyBlocks = ( plainTextByte.length / 127 ) + 2;
        } else {
            howManyBlocks = ( plainTextByte.length / 127 ) + 1;
        }

        BigInteger[] cypher = new BigInteger[howManyBlocks];
        cypher[0] = generateA();

        int length = 127;
        for (int i = 1; i < howManyBlocks; i++) {
            byte[] arrToEncrypt = new byte[128];
            if (i == howManyBlocks-1) {
                length = lastLength;
            }
            System.arraycopy(plainTextByte, (i-1)*127, arrToEncrypt,1, length);//srcPos musi sie zmieniac
            arrToEncrypt[0] = 0x01;//Za długa wiadomość prawdopodobnie?
            cypher[i] = encryptBlock(new BigInteger(arrToEncrypt));
        }
        return cypher;
    }

    private BigInteger decryptBlock(BigInteger block, BigInteger a) {
        BigInteger m = block.multiply(a.modPow(x, p).modInverse(p)).mod(p);
        return m;
    }

    public byte[] decrypt() {
        BigInteger a = cypherText[0];
        int howManyBlocks = cypherText.length;
        BigInteger[] plainInteger = new BigInteger[howManyBlocks - 1];
        for (int i = 1; i < howManyBlocks; i++) {
            plainInteger[i-1] = decryptBlock(cypherText[i], a);
        }
        int howLong = (howManyBlocks - 2) * 127 + plainInteger[howManyBlocks-2].toByteArray().length-1;

        byte[] plainMessage = new byte[howLong];
        byte[] temp = new byte[127];

        for (int i = 0; i < howManyBlocks-1; i++) {
            System.arraycopy(plainInteger[i].toByteArray(), 1, temp, 0, 127);
            System.arraycopy(temp, 0, plainMessage,i*127, temp.length);
        }

        boolean areNulls = true;
        while (areNulls) {
            if (plainMessage[plainMessage.length-1] == 0) {
                byte[] tempPLain = new byte[plainMessage.length-1];
                System.arraycopy(plainMessage, 0, tempPLain, 0, tempPLain.length);
                plainMessage = tempPLain;
            } else {
                areNulls = false;
            }
        }
        return plainMessage;
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

    public byte[] getPlainTextByte() {
        return plainTextByte;
    }

    public void setPlainTextByte(byte[] plainTextByte) {
        this.plainTextByte = plainTextByte;
    }


}
