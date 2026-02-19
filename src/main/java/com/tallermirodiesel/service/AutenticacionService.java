/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.service;

import com.tallermirodiesel.model.Usuario;

/**
 * @author elyrr
 */
public interface AutenticacionService {

    // Verifica las credenciales y devuelve el usuario completo si son correctas
    // Lanza una excepción si el usuario no existe, está inactivo o la contraseña es incorrecta
    Usuario login(String username, String password);

    // Actualiza la contraseña del usuario autenticado
    // Lanza una excepción si la contraseña actual es incorrecta
    boolean cambiarPassword(Long idUsuario, String passwordActual, String passwordNueva);
}