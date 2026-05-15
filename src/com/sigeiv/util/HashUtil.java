package com.sigeiv.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para hashear contrasenas con SHA-256.
 * Garantiza que las contrasenas nunca se almacenen en texto plano.
 * 
 * Entrada: String con la contrasena en texto plano
 * Proceso: Aplica el algoritmo SHA-256 y convierte a hexadecimal
 * Salida: String con el hash hexadecimal de 64 caracteres
 */
public class HashUtil {

    /**
     * Genera el hash SHA-256 de un texto.
     * @param texto Texto a hashear
     * @return Hash en formato hexadecimal
     */
    public static String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash SHA-256", e);
        }
    }
}





