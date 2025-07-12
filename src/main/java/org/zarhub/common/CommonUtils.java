package org.zarhub.common;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.zarhub.exception.GeneralExceptionType;
import org.zarhub.exception.ZarHubException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CommonUtils {

    public static boolean isNull(Object o) {
        if (o instanceof String) {
            if (o == null ||
                    ((String) o).isEmpty() ||
                    ((String) o).isBlank() ||
                    ((String) o).length() == 0 ||
                    ((String) o).toLowerCase().trim().equals("null")
            )
                return true;
            return false;
        } else if (o instanceof List) {
            if (((List) o).isEmpty())
                return true;
        }
        return o == null ? true : false;
    }

    public static boolean executeQuery(DataSource dataSource, String query) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(query);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String loadSqlFromFile(String path) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(path);
        return new String(Files.readAllBytes(Paths.get(resource.getURI())), StandardCharsets.UTF_8);
    }

    public static void setNull(Object entity) throws Exception {
        if (isNull(entity)) {
            throw new ZarHubException(GeneralExceptionType.ENTITY_CANNOT_BE_NULL);
        }
        // get Class and fields
        Class<?> cls = entity.getClass();
        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {
            // access to private field
            field.setAccessible(true);

            // create getter method with field name
            String capitalizedFieldName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            String getterMethodName = "get" + capitalizedFieldName;
            Method getterMethod = cls.getDeclaredMethod(getterMethodName);

            //  get value from getter
            Object fieldValue = getterMethod.invoke(entity);

            if (isNull(fieldValue)) {
                String setterMethodName = "set" + capitalizedFieldName;
                Method setterMethod = cls.getDeclaredMethod(setterMethodName, field.getType());
                setterMethod.invoke(entity, (Object) null);
            }
        }
    }

    private static long get64LeastSignificantBitsForVersion1() {
        Random random = new Random();
        long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
        long variant3BitFlag = 0x8000000000000000L;
        return random63BitLong | variant3BitFlag;
    }

    private static long get64MostSignificantBitsForVersion1() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long time_low = (currentTimeMillis & 0x0000_0000_FFFF_FFFFL) << 32;
        final long time_mid = ((currentTimeMillis >> 32) & 0xFFFF) << 16;
        final long version = 1 << 12;
        final long time_hi = ((currentTimeMillis >> 48) & 0x0FFF);
        return time_low | time_mid | version | time_hi;
    }

    public static UUID generateUUID() {
        long most64SigBits = get64MostSignificantBitsForVersion1();
        long least64SigBits = get64LeastSignificantBitsForVersion1();
        return new UUID(most64SigBits, least64SigBits);
    }

    public static Long longValue(Object number) {
        if (isNull(number))
            return null;
        else if (number instanceof Number)
            return ((Number) number).longValue();
        else
            try {
                return Long.valueOf(number.toString().trim());
            } catch (NumberFormatException e) {
                return null;
            }
    }

    public static String getClassName(Object entity) {
        if (entity instanceof List)
            return ((List) entity).get(0).getClass().getName();
        if (entity instanceof Class)
            return ((Class<?>) entity).getName();
        return entity.getClass().getName();
    }

    public static String extractTableNameFromSql(String sql) {
        String lowerCaseSql = sql.toLowerCase().trim();
        if (lowerCaseSql.startsWith("insert into")) {
            return sql.split(" ")[2];
        } else if (lowerCaseSql.startsWith("update")) {
            return sql.split(" ")[1];
        } else if (lowerCaseSql.startsWith("delete from")) {
            return sql.split(" ")[2];
        } else if (lowerCaseSql.startsWith("delete")) {
            return sql.split(" ")[1];
        } else {
            throw new IllegalArgumentException("Unsupported SQL operation: " + sql);
        }
    }

    public static byte[] hexStr2Bytes(String hex) {
        // Adding one byte to get the right conversion
        // values starting with "0" can be converted
        byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();

        // Copy all the REAL bytes, not the "first"
        byte[] ret = new byte[bArray.length - 1];
        System.arraycopy(bArray, 1, ret, 0, ret.length);
        return ret;
    }

    public static byte[] hmac_sha(String crypto, byte[] keyBytes, byte[] text) {
        try {
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey =
                    new SecretKeySpec(keyBytes, "RAW");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
//            digest.update(s.getBytes());
            return byteArrayToHexString(digest.digest(s.getBytes()));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result +=
                    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static String toSHA1(byte[] convertMe) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return byteArrayToHexString(md.digest(convertMe));
    }

    public static String extractBasePath(String requestUrl) {
        URI uri = URI.create(requestUrl);
        return uri.getPath();
    }
}
