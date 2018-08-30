/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Santi
 */
public interface IManejador {
    
    public void registrarMobUsuario(String usuario,String password,String email,BigDecimal numero);
    // Registra un usuario de aplicacion
    public void registrarDuenoRestaurante(String usuario,String password,String email,BigDecimal numero,BigDecimal mobile_number,String Adress);
    
    public int verificarLogin(String usuario,String password);
    
    public ArrayList<String> actualizarTiposDeRest();
    
    public ArrayList<String> actualizarPlatos();
    
    public DefaultTableModel buscarPorPrecio(long minimo,long maximo);
    public DefaultTableModel buscarPorDistancia(long min,long maximo);
    public DefaultTableModel buscarPorTipo(String tipo);
    public DefaultTableModel buscarPorPlato(String plato);
    
    public DefaultTableModel reportePlatosxRestaurante();
    
    public DefaultTableModel verTodos();
}
