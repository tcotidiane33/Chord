package bdgl.jchord;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

/**
 * Classe utilitaire pour générer des hachages de clés.
 */
public class Hash {

    public static int KEY_LENGTH = 160;

    /**
     * Définit la longueur de la clé utilisée dans le hachage.
     *
     * @param keyLength La longueur de la clé à définir.
     */
    public static void setKeyLength(int keyLength) {
        KEY_LENGTH = keyLength;
    }

    public enum HashFunction {
        SHA1,
        CRC32,
        JAVA
    }

    private static HashFunction function = HashFunction.CRC32;
    private static final int SHA1_KEY_LENGTH = 160;
    private static final int CRC32_KEY_LENGTH = 64;
    private static final int JAVA_KEY_LENGTH = 32;

    public static byte[] generateHash(String identifier) throws NoSuchAlgorithmException {
        switch (function) {
            case SHA1:
                return generateSHA1Hash(identifier);
            case CRC32:
                return generateCRC32Hash(identifier);
            case JAVA:
                return generateJavaHash(identifier);
            default:
                throw new IllegalArgumentException("Unknown hash function");
        }
    }

    private static byte[] generateSHA1Hash(String identifier) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(identifier.getBytes());
        return md.digest();
    }

    private static byte[] generateCRC32Hash(String identifier) {
        CRC32 crc32 = new CRC32();
        crc32.update(identifier.getBytes());
        long code = crc32.getValue();
        byte[] value = new byte[CRC32_KEY_LENGTH / 8];
        for (int i = 0; i < value.length; i++) {
            value[value.length - i - 1] = (byte) ((code >> 8 * i) & 0xff);
        }
        return value;
    }

    private static byte[] generateJavaHash(String identifier) {
        int code = identifier.hashCode();
        byte[] value = new byte[JAVA_KEY_LENGTH / 8];
        for (int i = 0; i < value.length; i++) {
            value[value.length - i - 1] = (byte) ((code >> 8 * i) & 0xff);
        }
        return value;
    }

    public static HashFunction getFunction() {
        return function;
    }

    public static void setFunction(HashFunction hashFunction) {
        function = hashFunction;
    }

    public static int getKeyLength() {
        switch (function) {
            case SHA1:
                return SHA1_KEY_LENGTH;
            case CRC32:
                return CRC32_KEY_LENGTH;
            case JAVA:
                return JAVA_KEY_LENGTH;
            default:
                throw new IllegalArgumentException("Unknown hash function");
        }
    }
}
