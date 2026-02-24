/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.tallermirodiesel.model.enums;

/**
 * @author elyrr
 * Representa el estado actual de una orden de trabajo.
 * Coincide con el enum estado_orden_trabajo_enum de PostgreSQL.
 */
public enum EstadoOrdenTrabajoEnum {
    ABIERTA,
    EN_PROCESO,
    EN_ESPERA,
    FINALIZADA,
    ENTREGADA,
    CANCELADA
}