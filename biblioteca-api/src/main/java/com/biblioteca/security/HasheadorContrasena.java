package com.biblioteca.security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Component;

// Las contraseñas nunca se guardan en texto plano: se guarda un hash PBKDF2 (viene incluido en Java)
@Component
public class HasheadorContrasena {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 600_000; // valor recomendado por OWASP para PBKDF2-SHA256
    private static final int LONGITUD_HASH_BITS = 256;
    private static final int LONGITUD_SAL_BYTES = 16;

    private final SecureRandom random = new SecureRandom();

    // Devuelve "iteraciones:sal:hash" (sal y hash en Base64)
    public String hashear(String contrasena) {
        byte[] sal = new byte[LONGITUD_SAL_BYTES];
        random.nextBytes(sal);
        byte[] hash = derivar(contrasena, sal, ITERACIONES, LONGITUD_HASH_BITS);
        Base64.Encoder base64 = Base64.getEncoder();
        return ITERACIONES + ":" + base64.encodeToString(sal) + ":" + base64.encodeToString(hash);
    }

    // Repite el cálculo con la misma sal y compara los resultados
    public boolean coincide(String contrasena, String guardado) {
        String[] partes = guardado.split(":");
        if (partes.length != 3) {
            return false;
        }
        int iteraciones = Integer.parseInt(partes[0]);
        byte[] sal = Base64.getDecoder().decode(partes[1]);
        byte[] esperado = Base64.getDecoder().decode(partes[2]);
        byte[] calculado = derivar(contrasena, sal, iteraciones, esperado.length * 8);
        // Comparación en tiempo constante: no revela cuántos bytes coinciden
        return MessageDigest.isEqual(esperado, calculado);
    }

    private byte[] derivar(String contrasena, byte[] sal, int iteraciones, int longitudBits) {
        PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(), sal, iteraciones, longitudBits);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña", e);
        } finally {
            spec.clearPassword();
        }
    }
}
