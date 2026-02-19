/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * @author elyrr
 */
public class PasswordHash {

    // Constructor privado: esta clase no se instancia, solo se usan sus métodos estáticos
    private PasswordHash() {
    }

    // Recibe la contraseña en texto plano y devuelve su hash encriptado
    public static String hashear(String passwordPlana) {
        return BCrypt.hashpw(passwordPlana, BCrypt.gensalt());
    }

    // Compara una contraseña en texto plano contra un hash guardado en la BD
    // Devuelve true si coinciden, false si no
    public static boolean verificar(String passwordPlana, String hashGuardado) {
        return BCrypt.checkpw(passwordPlana, hashGuardado);
    }
}