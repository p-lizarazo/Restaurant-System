/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Conexion;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Santi
 */
public class Manejador implements IManejador {
    
    private Connection conex;
    private String PKLogin;
    
    
    public Manejador(){
        try {
            conex = Conexion.getConnection();
            }   catch (SQLException e) {  
                            e.addSuppressed(e);
        }
        PKLogin=null;
    }

    @Override
    public void registrarMobUsuario(String usuario, String password, String email, BigDecimal numero) {
        PreparedStatement ps;
        try {
            ps = conex.prepareStatement("INSERT INTO USUARIO "
                    + "(USERNAME, PASSWORD, EMAIL, LANGUAGE_LANG_ID)"
                    + "VALUES( ?,  ?,  ?,  1)");
            ps.setString(1, usuario);
            ps.setString(2, password);
            ps.setString(3, email);
            
            ps.executeQuery();
            
            ps = conex.prepareStatement("INSERT INTO MOB_USER "
                    + "(USERNAME, PHONE_NUMBER)"
                    + "VALUES( ?,  ?)");
            ps.setString(1, usuario);
            ps.setBigDecimal(2, numero);
            ps.executeQuery();
            
                    } catch (SQLException e) {  
                        e.addSuppressed(e);
        }
        
    }


    @Override
    public void registrarDuenoRestaurante(String usuario, String password, String email, BigDecimal numeroFijo, BigDecimal mobile_number, String adress) {
        PreparedStatement ps;
        try {
            
            ps = conex.prepareStatement("INSERT INTO USUARIO "
                    + "(USERNAME, PASSWORD, EMAIL, LANGUAGE_LANG_ID)"
                    + "VALUES( ?,  ?,  ?,  1)");
            ps.setString(1, usuario);
            ps.setString(2, password);
            ps.setString(3, email);
            
            ps.executeQuery();
            
            ps = conex.prepareStatement("INSERT INTO REST_OWNER "
                    + "VALUES( ?,  ?,?,?,'W')");
            ps.setString(1, usuario);
            ps.setBigDecimal(2, numeroFijo);
            ps.setBigDecimal(3, mobile_number);
            ps.setString(4, adress);
            ps.executeQuery();
            
                    } catch (SQLException e) {  
                        e.addSuppressed(e);
        }
        
    }
    
    @Override
    public int verificarLogin(String usuario,String password){
        PreparedStatement ps;
        ResultSet rs;
        try{
            ps = conex.prepareStatement("select validarusuario(?,?) from dual");
            ps.setString(1, usuario);
            ps.setString(2, password);
            rs = ps.executeQuery();
            
            while(rs.next()){
                switch(rs.getString(1)){
                    case "user":
                        PKLogin=usuario;
                        return 0;
                    case "rest_owner":
                        PKLogin=usuario;
                            return 1;
                    case "admin":
                        PKLogin=usuario;
                        return 2;
                    case "NULL":
                        return -1;
                }
                    
                
            }
                  
            
        } catch(SQLException e){
            e.addSuppressed(e);
        }
        
        
        return 0;
    }
    @Override
      public ArrayList<String> actualizarTiposDeRest(){
          PreparedStatement ps;
          ResultSet rs;
          ArrayList<String> types = new ArrayList<>();
          try{
            ps = conex.prepareStatement("select name from restaurant_type ");
            rs = ps.executeQuery();
            
            while(rs.next()){
                    
                types.add(rs.getString("NAME"));
                
            }
                  
            
        } catch(SQLException e){
            e.addSuppressed(e);
        }        
          
          return types;
      };
    @Override
      public ArrayList<String> actualizarPlatos(){
          PreparedStatement ps;
          ResultSet rs;
          ArrayList<String> types = new ArrayList<>();
          try{
            ps = conex.prepareStatement("select distinct(name) from dish ");
            rs = ps.executeQuery();
            
            while(rs.next()){
                    
                types.add(rs.getString("NAME"));
                
            }
                  
            
        } catch(SQLException e){
            e.addSuppressed(e);
        }
          
          
          return types;
      };
        
    @Override
      public DefaultTableModel buscarPorPrecio(long minimo,long maximo){
        DefaultTableModel modelo = new DefaultTableModel();
        ResultSet rs;
        PreparedStatement ps;
        modelo.addColumn("Nombre del Restaurante");
        modelo.addColumn("Numero Telefonico");
        modelo.addColumn("Tipo de comida");
        modelo.addColumn("Precio promedio");     
        modelo.addColumn("Distancia");
        modelo.addColumn("Descripcion");
        int num_columnas=6;
        try {   
            ps = conex.prepareStatement("with xd as(select * from dish join restaurant on (MENU_USERNAME=rest_owner_username and menu_rest_id = rest_id) where price>= "+minimo+" and price<="+maximo+" )"
                    + " select distinct rest_name,phone_number,restaurant_type.name,average_price,distance,rest_description"
                    + " from DIST_BETW_UXR join xd on (restaurant_rest_id=rest_id and restaurant_username=REST_OWNER_USERNAME) join restaurant_type "
                    + "on (RESTAURANT_TYPE_ID_TYPE=id_type) where  MOB_USER_USERNAME=? order by distance,average_price,restaurant_type.name");
            ps.setString(1, PKLogin);
            rs=ps.executeQuery();
            while(rs.next()){
                Object[] fila = new Object[num_columnas];
                   for (int i = 0; i < num_columnas; i++) { 
                       fila[i] = rs.getObject(i + 1);            
                    }
                modelo.addRow(fila);
            }      
            
        } catch (SQLException e){
            e.addSuppressed(e);
            System.out.println("error");
        }
        return modelo;
      };
    public String buscarTipoRestaurante(String rest_actual)
    {
        PreparedStatement ps;
        ResultSet rs;
        String resultado=null;
        int rest_type_id=0;
        try{
            ps=conex.prepareStatement("select restaurant_type_id_type from restaurant where rest_name=? and Rest_Owner_username=?");
            ps.setString(1, rest_actual);
            ps.setString(2, PKLogin);
            rs=ps.executeQuery();
            while(rs.next())
            {
               rest_type_id=rs.getInt("restaurant_type_id_type");
            }
            ps = conex.prepareStatement("select name from restaurant_type where id_type=?");
            ps.setInt(1, rest_type_id);
            rs = ps.executeQuery();            
            while(rs.next()){                    
                resultado=rs.getString("name");
            }
        } catch(SQLException e){
            e.addSuppressed(e);
        }
        return resultado;
    }
    @Override
    public DefaultTableModel buscarPorDistancia(long min,long maximo){
        DefaultTableModel modelo = new DefaultTableModel();
        ResultSet rs;
        PreparedStatement ps;
        modelo.addColumn("Nombre del Restaurante");
        modelo.addColumn("Numero Telefonico");
        modelo.addColumn("Tipo de comida");
        modelo.addColumn("Precio promedio");     
        modelo.addColumn("Distancia");
        modelo.addColumn("Descripcion");
        int num_columnas=6;
        try {   
            ps = conex.prepareStatement("select rest_name,phone_number,restaurant_type.name,average_price,distance,rest_description from DIST_BETW_UXR "
                    + "join RESTAURANT on (restaurant_rest_id=rest_id and restaurant_username=REST_OWNER_USERNAME) join restaurant_type on (RESTAURANT_TYPE_ID_TYPE=id_type)"
                    + " where distance >=" +min+ " and distance <=" +maximo+" and MOB_USER_USERNAME= '"+PKLogin+"' order by distance,average_price,restaurant_type.name");
            rs=ps.executeQuery();
            while(rs.next()){
                Object[] fila = new Object[num_columnas];
                   for (int i = 0; i < num_columnas; i++) { 
                       fila[i] = rs.getObject(i + 1);            
                    }
                modelo.addRow(fila);
            }      
            
        } catch (SQLException e){
            e.addSuppressed(e);
            System.out.println("error");
        }
        return modelo;
    };
    @Override
    public DefaultTableModel buscarPorTipo(String tipo){
        DefaultTableModel modelo = new DefaultTableModel();
        ResultSet rs;
        PreparedStatement ps;
        modelo.addColumn("Nombre del Restaurante");
        modelo.addColumn("Numero Telefonico");
        modelo.addColumn("Tipo de comida");
        modelo.addColumn("Precio promedio");     
        modelo.addColumn("Distancia");
        modelo.addColumn("Descripcion");
        int num_columnas=6;
        try {   
            ps = conex.prepareStatement("select rest_name,phone_number,restaurant_type.name,average_price,distance,rest_description from DIST_BETW_UXR "
                    + "join RESTAURANT on (restaurant_rest_id=rest_id and restaurant_username=REST_OWNER_USERNAME) join restaurant_type on (RESTAURANT_TYPE_ID_TYPE=id_type)"
                    + " where restaurant_type.name ='" +tipo+ "' and MOB_USER_USERNAME= '"+PKLogin+"' order by distance,average_price,restaurant_type.name");
            rs=ps.executeQuery();
            while(rs.next()){
                Object[] fila = new Object[num_columnas];
                   for (int i = 0; i < num_columnas; i++) { 
                       fila[i] = rs.getObject(i + 1);            
                    }
                modelo.addRow(fila);
            }      
            
        } catch (SQLException e){
            e.addSuppressed(e);
            System.out.println("error");
        }
        return modelo;
    };
        
    /**
     *
     * @param plato
     * @return
     */
    @Override
    public DefaultTableModel buscarPorPlato(String plato){
         DefaultTableModel modelo = new DefaultTableModel();
        ResultSet rs;
        PreparedStatement ps;
        modelo.addColumn("Nombre del Restaurante");
        modelo.addColumn("Numero Telefonico");
        modelo.addColumn("Tipo de comida");
        modelo.addColumn("Precio promedio");     
        modelo.addColumn("Distancia");
        modelo.addColumn("Descripcion");
        int num_columnas=6;
        try {   
            ps = conex.prepareStatement("with xd as(select * from dish join restaurant on (MENU_USERNAME=rest_owner_username and menu_rest_id = rest_id) where dish.name=?) "
                    + "select rest_name,phone_number,restaurant_type.name,average_price,distance,rest_description "
                    + "from DIST_BETW_UXR join xd on (restaurant_rest_id=rest_id and restaurant_username=REST_OWNER_USERNAME) "
                    + "join restaurant_type on (RESTAURANT_TYPE_ID_TYPE=id_type) where  MOB_USER_USERNAME=? order by distance,average_price,restaurant_type.name");
            ps.setString(1, plato);
            ps.setString(2, PKLogin);
            rs=ps.executeQuery();
            while(rs.next()){
                Object[] fila = new Object[num_columnas];
                   for (int i = 0; i < num_columnas; i++) { 
                       fila[i] = rs.getObject(i + 1);            
                    }
                modelo.addRow(fila);
            }
        } catch (SQLException e){
            e.addSuppressed(e);
            System.out.println("error");
        }
        return modelo;
    };
    
    @Override
    public DefaultTableModel verTodos(){
        DefaultTableModel modelo = new DefaultTableModel();
        ResultSet rs;
        PreparedStatement ps;
        modelo.addColumn("Nombre del Restaurante");
        modelo.addColumn("Numero Telefonico");
        modelo.addColumn("Tipo de comida");
        modelo.addColumn("Precio promedio");     
        modelo.addColumn("Distancia");
        modelo.addColumn("Descripcion");
        int num_columnas=6;
        try {   
            ps = conex.prepareStatement("select rest_name,phone_number,restaurant_type.name,average_price,distance,rest_description from DIST_BETW_UXR "
                    + "join RESTAURANT on (restaurant_rest_id=rest_id and restaurant_username=REST_OWNER_USERNAME) join restaurant_type on (RESTAURANT_TYPE_ID_TYPE=id_type)"
                    + " where MOB_USER_USERNAME= '"+PKLogin+"' order by average_price,distance,restaurant_type.name");
            rs=ps.executeQuery();
            while(rs.next()){
                Object[] fila = new Object[num_columnas];
                   for (int i = 0; i < num_columnas; i++) { 
                       fila[i] = rs.getObject(i + 1);            
                    }
                modelo.addRow(fila);
            }      
            
        } catch (SQLException e){
            e.addSuppressed(e);
            System.out.println("error");
        }
        return modelo;
    };
      
    @Override
    public DefaultTableModel reportePlatosxRestaurante(){
        DefaultTableModel modelo = new DefaultTableModel();
        ResultSet rs;
        PreparedStatement ps;
        try {
            ps=conex.prepareStatement("select rest_name from restaurant");
            rs=ps.executeQuery();
            modelo.addColumn("//");
            while (rs.next()){
                modelo.addColumn(rs.getString(1)); 
            }      
            
            ps=conex.prepareStatement("select name from dish");
            rs=ps.executeQuery();
            while(rs.next()){
                Object[] fila = new Object[modelo.getColumnCount()];
                fila[0]=rs.getObject(1);
                modelo.addRow(fila);
            }   
            ps = conex.prepareStatement("select name,rest_name,price from dish join restaurant on (MENU_USERNAME=rest_owner_username and menu_rest_id = rest_id)");
            rs=ps.executeQuery();
            String platoFila;
            String restColumna;
            while(rs.next()){
                String nombreDelPlato=rs.getString(1);
                String nombreRestaurante=rs.getString(2);
                Object x=rs.getObject(3);
                for(int i=0;i<modelo.getRowCount();i++){
                    for(int j=1;j<modelo.getColumnCount();j++){
                        restColumna = modelo.getColumnName(j);
                        platoFila= (String) modelo.getValueAt(i, 0);
                        if(nombreRestaurante.equals(restColumna) && platoFila.equals(nombreDelPlato)){
                            modelo.setValueAt(x, i, j);
                        }
                    }
                }
            }
             for(int i=0;i<modelo.getRowCount();i++){
                    for(int j=1;j<modelo.getColumnCount();j++){
                        Object y = modelo.getValueAt(i,j);
                        if( y == null){
                            modelo.setValueAt("N/A", i, j);
                        }
                    }
            }
           
            
        } catch (SQLException ex) {
            Logger.getLogger(Manejador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modelo;
    }
    public ArrayList<String> actualizarNombresDeRestaurante(){
        PreparedStatement ps;
          ResultSet rs;
          ArrayList<String> types = new ArrayList<>();
          try{
            ps = conex.prepareStatement("select rest_name from restaurant where Rest_Owner_username=?");
            ps.setString(1, PKLogin);
            rs = ps.executeQuery();            
            while(rs.next()){                    
                types.add(rs.getString("rest_name"));
            }
        } catch(SQLException e){
            e.addSuppressed(e);
        }
        return types;
    };
    public String buscarNombreRestaurante(String rest_actual)
    {
        PreparedStatement ps;
        ResultSet rs;
        String resultado=null;
        int rest_id=0;
        try{
          ps=conex.prepareStatement("select rest_id from restaurant where rest_name=? and Rest_Owner_username=?");
          ps.setString(1, rest_actual);
          ps.setString(2, PKLogin);
          rs=ps.executeQuery();
          while(rs.next())
          {
             rest_id=rs.getInt("rest_id");
          }
          ps = conex.prepareStatement("select rest_name from restaurant where Rest_Owner_username=? and rest_id=?");
          ps.setString(1, PKLogin);
          ps.setInt(2,rest_id);
          rs = ps.executeQuery();            
          while(rs.next()){                    
              resultado=rs.getString("rest_name");
          }
        } catch(SQLException e){
            e.addSuppressed(e);
        }
        return resultado;
    }
    public Number buscarPrecioPromedioRestaurante(String rest_actual)
    {
        PreparedStatement ps;
        ResultSet rs;
        long resultado=0;
        int rest_id=0;
        try{
          ps=conex.prepareStatement("select rest_id from restaurant where rest_name=? and Rest_Owner_username=?");
          ps.setString(1, rest_actual);
          ps.setString(2, PKLogin);
          rs=ps.executeQuery();
          while(rs.next())
          {
             rest_id=rs.getInt("rest_id");
          }
          ps = conex.prepareStatement("select average_price from restaurant where Rest_Owner_username=? and rest_id=?");
          ps.setString(1, PKLogin);
          ps.setInt(2, rest_id);
          rs = ps.executeQuery();            
          while(rs.next()){                    
              resultado=rs.getLong("average_price");
          }
        } catch(SQLException e){
            e.addSuppressed(e);
        }
        return resultado;
    }
    public String buscarDireccionRestaurante(String rest_actual)
    {
        PreparedStatement ps;
        ResultSet rs;
        String resultado=null;
        int rest_id=0;
        try{
          ps=conex.prepareStatement("select rest_id from restaurant where rest_name=? and Rest_Owner_username=?");
          ps.setString(1, rest_actual);
          ps.setString(2, PKLogin);
          rs=ps.executeQuery();
          while(rs.next())
          {
             rest_id=rs.getInt("rest_id");
          }
          ps = conex.prepareStatement("select adress from restaurant where Rest_Owner_username=? and rest_id=?");
          ps.setString(1, PKLogin);
          ps.setInt(2, rest_id);
          rs = ps.executeQuery();            
          while(rs.next()){                    
              resultado=rs.getString("adress");
          }
        } catch(SQLException e){
            e.addSuppressed(e);
        }
        return resultado;
    }
      public String buscarEmailRestaurante(String rest_actual)
    {
        PreparedStatement ps;
        ResultSet rs;
        String resultado=null;
        int rest_id=0;
        try{
          ps=conex.prepareStatement("select rest_id from restaurant where rest_name=? and Rest_Owner_username=?");
          ps.setString(1, rest_actual);
          ps.setString(2, PKLogin);
          rs=ps.executeQuery();
          while(rs.next())
          {
             rest_id=rs.getInt("rest_id");
          }
          ps = conex.prepareStatement("select email_adress from restaurant where Rest_Owner_username=? and rest_id=?");
          ps.setString(1, PKLogin);
          ps.setInt(2,rest_id);
          rs = ps.executeQuery();            
          while(rs.next()){                    
              resultado=rs.getString("email_adress");
          }
        } catch(SQLException e){
            e.addSuppressed(e);
        }
        return resultado;
    }
    public Long buscarNumeroTelefonoRestaurante(String rest_actual)
    {
        PreparedStatement ps;
        ResultSet rs;
        long resultado=0;
        int rest_id=0;
        try{
          ps=conex.prepareStatement("select rest_id from restaurant where rest_name=? and Rest_Owner_username=?");
          ps.setString(1, rest_actual);
          ps.setString(2, PKLogin);
          rs=ps.executeQuery();
          while(rs.next())
          {
             rest_id=rs.getInt("rest_id");
          }
          ps = conex.prepareStatement("select phone_number from restaurant where Rest_Owner_username=? and rest_id=?");
          ps.setString(1, PKLogin);
          ps.setInt(2, rest_id);
          rs = ps.executeQuery();            
          while(rs.next()){                    
              resultado=rs.getLong("phone_number");
          }
        } catch(SQLException e){
            e.addSuppressed(e);
        }
        return resultado;
    }
      public Long buscarTelefonoMobilRestaurante(String rest_actual)
    {
        PreparedStatement ps;
        ResultSet rs;
        long resultado=0;
        int rest_id=0;
        try{
          ps=conex.prepareStatement("select rest_id from restaurant where rest_name=? and Rest_Owner_username=?");
          ps.setString(1, rest_actual);
          ps.setString(2, PKLogin);
          rs=ps.executeQuery();
          while(rs.next())
          {
             rest_id=rs.getInt("rest_id");
          }
          ps = conex.prepareStatement("select mobile_phone from restaurant where Rest_Owner_username=? and rest_id=?");
          ps.setString(1, PKLogin);
          ps.setInt(2,rest_id);
          rs = ps.executeQuery();            
          while(rs.next()){                    
              resultado=rs.getLong("mobile_phone");
          }
        } catch(SQLException e){
            e.addSuppressed(e);
        }
        return resultado;
    }    
       public String buscarDescripcionRestaurante(String rest_actual)
    {
        PreparedStatement ps;
        ResultSet rs;
        String resultado=null;
        int rest_id=0;
        try{
          ps=conex.prepareStatement("select rest_id from restaurant where rest_name=? and Rest_Owner_username=?");
          ps.setString(1, rest_actual);
          ps.setString(2, PKLogin);
          rs=ps.executeQuery();
          while(rs.next())
          {
             rest_id=rs.getInt("rest_id");
          }
          ps = conex.prepareStatement("select nvl(rest_description,'Ingrese una descripcion') as rest_description from restaurant where Rest_Owner_username=? and rest_id=?");
          ps.setString(1, PKLogin);
          ps.setInt(2,rest_id);
          rs = ps.executeQuery();            
          while(rs.next()){  
                resultado=rs.getString("rest_description");                
          }
        } catch(SQLException e){
            e.addSuppressed(e);
        }
        return resultado;
    }    
    
    public DefaultTableModel MenuInfoRestaurante(String NombreRestaurante){
        DefaultTableModel modelo = new DefaultTableModel();
        ResultSet rs;
        PreparedStatement ps;
        modelo.addColumn("Nombre");
        modelo.addColumn("Descripcion");
        modelo.addColumn("Precio");
        int num_columnas=3;
        try {   
            ps = conex.prepareStatement("select name,description,price \n" +
                "from Dish join Menu on (restaurant_rest_id=Menu_rest_id and restaurant_username=Menu_username and Menu_menu_id=menu_id) join restaurant on (rest_id=Menu_rest_id and restaurant_username=rest_owner_username)\n" +
                "where restaurant.REST_OWNER_USERNAME='"+PKLogin+ "'and restaurant.rest_name=?\n" +
                "order by name,description,price");
            ps.setString(1,NombreRestaurante);
            rs=ps.executeQuery();
            while(rs.next()){
                Object[] fila = new Object[num_columnas];
                   for (int i = 0; i < num_columnas; i++) { 
                       fila[i] = rs.getObject(i + 1);            
                    }
                modelo.addRow(fila);
            }      
            
        } catch (SQLException e){
            e.addSuppressed(e);
            System.out.println("error");
        }
        return modelo;
    };
    public DefaultListModel actualizarListaPlatos()
    {
        String[] nombrePlatos;
        PreparedStatement ps;
        DefaultListModel modelo=new DefaultListModel();
        ResultSet rs;
        int tam=0;
        try{
            ps=conex.prepareStatement("select name from dish");
            rs=ps.executeQuery();
            while(rs.next())
            {
                modelo.add(tam, rs.getObject("name"));
                tam++;
            }
        }catch(SQLException e){
            
        }
        return modelo;
    }
    public DefaultTableModel PlatosExistentes(){
        DefaultTableModel modelo = new DefaultTableModel();
        ResultSet rs;
        PreparedStatement ps;
        modelo.addColumn("Nombre");
        modelo.addColumn("Descripcion");
        modelo.addColumn("Precio");
        int num_columnas=3;
        try {   
            ps = conex.prepareStatement("select name,nvl(description,'ingrese una descripcion'), price from Dish");
            rs=ps.executeQuery();
            while(rs.next()){
                Object[] fila = new Object[num_columnas];
                   for (int i = 0; i < num_columnas; i++) { 
                       fila[i] = rs.getObject(i + 1);            
                    }
                modelo.addRow(fila);
            }      
            
        } catch (SQLException e){
            e.addSuppressed(e);
            System.out.println("error");
        }
        return modelo;
    };
    public void guardarDatosRestaurante(String rest_name,String adress, 
                                        String mobile_phone, String email_adress, 
                                        String average_price, String rest_description, 
                                        String phone_number, String type_rest,String rest_actual,
                                        String picture_restaurant)
    {
        PreparedStatement ps;
        ResultSet rs;
        int id_type=0;
        int rest_id=0;
        try{
          ps=conex.prepareStatement("select rest_id from restaurant where rest_name=? and Rest_Owner_username=?");
          ps.setString(1, rest_actual);
          ps.setString(2, PKLogin);
          rs=ps.executeQuery();
          while(rs.next())
          {
             rest_id=rs.getInt("rest_id");
          }
          ps=conex.prepareStatement("select id_type from restaurant_type where name=?");
          ps.setString(1, type_rest);          
          rs=ps.executeQuery(); 
          while(rs.next()){  
                id_type=rs.getInt("id_type");
          }
          ps= conex.prepareStatement("update restaurant set rest_name=? where rest_id=? and Rest_Owner_username=?");
          ps.setString(1, rest_name);
          ps.setInt(2, rest_id);
          ps.setString(3, PKLogin);
          ps.executeQuery();        
          
          ps= conex.prepareStatement("update restaurant set adress=? where rest_id=? and Rest_Owner_username=?");
          ps.setString(1, adress);
          ps.setInt(2, rest_id);
          ps.setString(3, PKLogin);
          ps.executeQuery();
          
          ps= conex.prepareStatement("update restaurant set mobile_phone=? where rest_id=? and Rest_Owner_username=?");
          ps.setLong(1, Long.parseLong(mobile_phone));
          ps.setInt(2, rest_id);
          ps.setString(3, PKLogin);
          ps.executeQuery();
          
          ps= conex.prepareStatement("update restaurant set email_adress=? where rest_id=? and Rest_Owner_username=?");
          ps.setString(1, email_adress);
          ps.setInt(2, rest_id);
          ps.setString(3, PKLogin);
          ps.executeQuery();
          
          ps= conex.prepareStatement("update restaurant set average_price=? where rest_id=? and Rest_Owner_username=?");
          ps.setLong(1, Long.parseLong(average_price));
          ps.setInt(2, rest_id);
          ps.setString(3, PKLogin);
          ps.executeQuery();
          
          ps= conex.prepareStatement("update restaurant set rest_description=? where rest_id=? and Rest_Owner_username=?");
          ps.setString(1, rest_description);
          ps.setInt(2, rest_id);
          ps.setString(3, PKLogin);
          ps.executeQuery();
          
          ps= conex.prepareStatement("update restaurant set phone_number=? where rest_id=? and Rest_Owner_username=?");
          ps.setLong(1, Long.parseLong(phone_number));
          ps.setInt(2, rest_id);
          ps.setString(3, PKLogin);
          ps.executeQuery();
          ps= conex.prepareStatement("update restaurant set restaurant_type_id_type=? where rest_id=? and Rest_Owner_username=?");
          ps.setInt(1, id_type);
          ps.setInt(2, rest_id);
          ps.setString(3, PKLogin);
          ps.executeQuery();
          
          if(!picture_restaurant.equals("N/A"))
          {
              String INSERT_PICTURE = "update restaurant set picture_restaurant=? where rest_id=? and Rest_Owner_username=?";
              File file;
              file = new File(picture_restaurant);
              InputStream fis;
              fis = new FileInputStream(file);
              ps=conex.prepareStatement(INSERT_PICTURE);
              ps.setBinaryStream(1,fis);
              ps.setInt(2,rest_id);
              ps.setString(3, PKLogin);
              ps.executeQuery();  
              fis.close();
          }
              
          
        } catch(IOException | NumberFormatException | SQLException e){
            e.addSuppressed(e);
        }
        
    }
    
    public Icon cargarFoto(String direccion){
        ImageIcon icon=new ImageIcon(direccion);        
        return icon;
    }

    /**
     *
     * @param name
     * @param description
     * @param nombreRestaurante
     * @param price
     */
    public void agregarPlatosArestaurante(String name, String description, long price, String nombreRestaurante)
    {
        PreparedStatement ps;
        ResultSet rs;
        int menu_rest_id=0;
        int menu_menu_id=0;
        try{
            ps=conex.prepareStatement("select rest_id, menu_id from restaurant join menu on rest_id=restaurant_rest_id where rest_name=? and rest_owner_username=?");
            ps.setString(1, nombreRestaurante);
            ps.setString(2, PKLogin);
            rs=ps.executeQuery();
            while(rs.next())
            {
                menu_rest_id=rs.getInt("rest_id");
                menu_menu_id=rs.getInt("menu_id");
            }
            ps=conex.prepareStatement("insert into dish values(?,?,?,?,?,?)");
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setLong(3, price);
            ps.setInt(4, menu_rest_id);
            ps.setInt(5, menu_menu_id);
            ps.setString(6, PKLogin);
            ps.executeQuery();
        }catch(SQLException e){
             e.addSuppressed(e);
        }
    }
    public BufferedImage actualizarImagenRestaurante(String NombreRestaurante)
    {
        PreparedStatement ps;
        ResultSet rs;
        String resultado=null;
        BufferedImage bufferedImage=null;
        try{
          ps = conex.prepareStatement("select picture_restaurant from restaurant where Rest_Owner_username=? and rest_name=?");
          ps.setString(1, PKLogin);
          ps.setString(2,NombreRestaurante);
          rs = ps.executeQuery();            
          while(rs.next()){  
              Blob blob=rs.getBlob("picture_restaurant");
              int blobLength=(int) blob.length();
              byte[] blobAsBytes = blob.getBytes(1,blobLength);
              bufferedImage = ImageIO.read(new ByteArrayInputStream(blobAsBytes));
             
          }
        } catch(IOException | SQLException e){
            e.addSuppressed(e);
        }
        return bufferedImage;
    }
    public void actualizarPlatillo(String nombreR, String nombreV,String nombreN, String descripcion,Long precio){
        PreparedStatement ps=null;
        ResultSet rs;
        int rest_id;
        try{
            ps=conex.prepareStatement("update dish set description = ?  where name=?");
            ps.setString(1, descripcion);
            ps.setString(2, nombreV);
            ps.executeQuery();
            
            ps=conex.prepareStatement("update dish set price = ?  where name=?");
            ps.setLong(1, precio);
            ps.setString(2, nombreV);
            ps.executeQuery();

            ps=conex.prepareStatement("update dish set name = ?  where name=?");
            ps.setString(1, nombreN);
            ps.setString(2, nombreV);
            ps.executeQuery();

        } catch(SQLException e){
            e.addSuppressed(e);
        }
    }
}
