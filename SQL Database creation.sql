/* 
  SQL FILE:
  SANTIAGO LIZARAZO
  SANTIAGO OTALORA
  JUAN PABLO RODRIGUEZ
*/

-----------------
-- DROP TABLES --
-----------------
drop table Usuario_Aud;
drop table admin cascade constraints;
drop table dish cascade constraints;
drop table dist_bet_rxr cascade constraints;
drop table dist_betw_uxr cascade constraints;
drop table language cascade constraints;
drop table menu cascade constraints;
drop table mob_user cascade constraints;
drop table rest_owner cascade constraints;
drop table restaurant cascade constraints;
drop table restaurant_type cascade constraints;
drop table updates cascade constraints;
drop table Usuario cascade constraints;
drop table user_reviewxrest cascade constraints;
---------------------
-- TABLES CREATION --
---------------------
create table Usuario_Aud(
  tipo_transaccion char(1),
  username_old         VARCHAR2 (20),
  password_old         VARCHAR2 (30),
  email_old            VARCHAR2 (50),
  Language_lang_id_old INTEGER,  
  username_new         VARCHAR2 (20),
  password_new         VARCHAR2 (30),
  email_new            VARCHAR2 (50),
  Language_lang_id_new INTEGER,
  check (tipo_transaccion in ('U','D','I'))
);

CREATE TABLE Admin
  ( username VARCHAR2 (20) NOT NULL
  ) ;
ALTER TABLE Admin ADD CONSTRAINT Admin_PK PRIMARY KEY ( username ) ;


CREATE TABLE Dish
  (
    name VARCHAR2 (20) NOT NULL ,
    description varchar(150) NULL ,
    price         NUMBER (12) NOT NULL ,
    Menu_rest_id  INTEGER NOT NULL ,
    Menu_menu_id  INTEGER NOT NULL ,
    Menu_username VARCHAR2 (20) NOT NULL
  ) ;
ALTER TABLE Dish ADD CONSTRAINT Dish_PK PRIMARY KEY ( Menu_rest_id, Menu_username, Menu_menu_id, name ) ;


CREATE TABLE Language
  (
    name    VARCHAR2 (20) NOT NULL ,
    lang_id INTEGER NOT NULL ,
    lang_info CLOB 
  ) ;
ALTER TABLE Language ADD CONSTRAINT Language_PK PRIMARY KEY ( lang_id ) ;


CREATE TABLE Menu
  (
    Restaurant_rest_id  INTEGER NOT NULL ,
    menu_id             INTEGER NOT NULL ,
    Restaurant_username VARCHAR2 (20) NOT NULL
  ) ;
CREATE UNIQUE INDEX Menu__IDX ON Menu
  (
    Restaurant_rest_id ASC , Restaurant_username ASC
  )
  ;
ALTER TABLE Menu ADD CONSTRAINT Menu_PK PRIMARY KEY ( Restaurant_rest_id, Restaurant_username, menu_id ) ;


CREATE TABLE Mob_user
  (
    username     VARCHAR2 (20) NOT NULL ,
    phone_number NUMBER (12)
  ) ;
ALTER TABLE Mob_user ADD CONSTRAINT Mob_user_PK PRIMARY KEY ( username ) ;


CREATE TABLE Rest_Owner
  (
    username      VARCHAR2 (20) NOT NULL ,
    phone_number  NUMBER (12) NOT NULL ,
    mobile_number NUMBER (12) ,
    adress        VARCHAR2 (50) NOT NULL ,
    verified      CHAR (1) NOT NULL
  ) ;
ALTER TABLE Rest_Owner ADD CONSTRAINT Rest_Owner_PK PRIMARY KEY ( username ) ;


CREATE TABLE Restaurant
  (
    rest_name     VARCHAR2 (30) NOT NULL ,
    adress        VARCHAR2 (20) NOT NULL ,
    mobile_phone  NUMBER (12) ,
    email_adress  VARCHAR2 (50) NOT NULL ,
    average_price NUMBER (12) NOT NULL ,
    rest_description VARCHAR(120) ,
    picture_restaurant BLOB ,
    phone_number            NUMBER (12) NOT NULL ,
    rest_id                 INTEGER NOT NULL ,
    Rest_Owner_username     VARCHAR2 (20) NOT NULL ,
    Restaurant_type_id_type INTEGER,
    estado			CHAR(1)  default ('N') NOT NULL
  ) ;
ALTER TABLE Restaurant ADD CONSTRAINT Restaurant_PK PRIMARY KEY ( rest_id, Rest_Owner_username ) ;


CREATE TABLE Restaurant_type
  (
    id_type INTEGER NOT NULL ,
    name    VARCHAR2 (30) NOT NULL
  ) ;
ALTER TABLE Restaurant_type ADD CONSTRAINT Restaurant_type_PK PRIMARY KEY ( id_type ) ;


CREATE TABLE Updates
  ( releases CLOB , version FLOAT (2) NOT NULL
  ) ;
ALTER TABLE Updates ADD CONSTRAINT Updates_PK PRIMARY KEY ( version ) ;


CREATE TABLE Usuario
  (
    username         VARCHAR2 (20) NOT NULL ,
    password         VARCHAR2 (30) NOT NULL ,
    email            VARCHAR2 (50) NOT NULL ,
    Language_lang_id INTEGER NOT NULL
  ) ;
ALTER TABLE Usuario ADD CONSTRAINT User_PK PRIMARY KEY ( username ) ;


CREATE TABLE dist_bet_RxR
  (
    Restaurant_rest_id   INTEGER NOT NULL ,
    Restaurant_username  VARCHAR2 (20) NOT NULL ,
    Restaurant_rest_id1  INTEGER NOT NULL ,
    Restaurant_username1 VARCHAR2 (20) NOT NULL ,
    distance             INTEGER NOT NULL
  ) ;
ALTER TABLE dist_bet_RxR ADD CONSTRAINT dist_bet_RxR_PK PRIMARY KEY ( Restaurant_rest_id, Restaurant_username, Restaurant_rest_id1, Restaurant_username1 ) ;


CREATE TABLE dist_betw_UxR
  (
    Mob_user_username   VARCHAR2 (20) NOT NULL ,
    Restaurant_rest_id  INTEGER NOT NULL ,
    Restaurant_username VARCHAR2 (20) NOT NULL ,
    distance            INTEGER NOT NULL
  ) ;
ALTER TABLE dist_betw_UxR ADD CONSTRAINT dist_betw_UxR_PK PRIMARY KEY ( Mob_user_username, Restaurant_rest_id, Restaurant_username ) ;


CREATE TABLE user_reviewXRest
  (
    Restaurant_rest_id             INTEGER NOT NULL ,
    Restaurant_Rest_Owner_username VARCHAR2 (20) NOT NULL ,
    Mob_user_username              VARCHAR2 (20) NOT NULL ,
    Calificacion                NUMBER (1) ,
    Tipo_Calificacion		VARCHAR2 (20) NOT NULL
  ) ;
ALTER TABLE user_reviewXRest ADD CONSTRAINT user_reviewXRest_PK PRIMARY KEY ( Restaurant_rest_id, Restaurant_Rest_Owner_username, Mob_user_username,Tipo_Calificacion ) ;


ALTER TABLE Admin ADD CONSTRAINT Admin_User_FK FOREIGN KEY ( username ) REFERENCES Usuario ( username ) ;

ALTER TABLE Dish ADD CONSTRAINT Dish_Menu_FK FOREIGN KEY ( Menu_rest_id, Menu_username, Menu_menu_id ) REFERENCES Menu ( Restaurant_rest_id, Restaurant_username, menu_id ) ;

ALTER TABLE dist_betw_UxR ADD CONSTRAINT FK_ASS_10 FOREIGN KEY ( Mob_user_username ) REFERENCES Mob_user ( username ) ;

ALTER TABLE dist_betw_UxR ADD CONSTRAINT FK_ASS_11 FOREIGN KEY ( Restaurant_rest_id, Restaurant_username ) REFERENCES Restaurant ( rest_id, Rest_Owner_username ) ;

ALTER TABLE user_reviewXRest ADD CONSTRAINT FK_ASS_13 FOREIGN KEY ( Restaurant_rest_id, Restaurant_Rest_Owner_username ) REFERENCES Restaurant ( rest_id, Rest_Owner_username ) ;

ALTER TABLE user_reviewXRest ADD CONSTRAINT FK_ASS_14 FOREIGN KEY ( Mob_user_username ) REFERENCES Mob_user ( username ) ;

ALTER TABLE dist_bet_RxR ADD CONSTRAINT FK_ASS_8 FOREIGN KEY ( Restaurant_rest_id, Restaurant_username ) REFERENCES Restaurant ( rest_id, Rest_Owner_username ) ;

ALTER TABLE dist_bet_RxR ADD CONSTRAINT FK_ASS_9 FOREIGN KEY ( Restaurant_rest_id1, Restaurant_username1 ) REFERENCES Restaurant ( rest_id, Rest_Owner_username ) ;

ALTER TABLE Menu ADD CONSTRAINT Menu_Restaurant_FK FOREIGN KEY ( Restaurant_rest_id, Restaurant_username ) REFERENCES Restaurant ( rest_id, Rest_Owner_username ) ;

ALTER TABLE Mob_user ADD CONSTRAINT Mob_user_User_FK FOREIGN KEY ( username ) REFERENCES Usuario ( username ) ;

ALTER TABLE Rest_Owner ADD CONSTRAINT Rest_Owner_User_FK FOREIGN KEY ( username ) REFERENCES Usuario ( username ) ;

ALTER TABLE Restaurant ADD CONSTRAINT Restaurant_Rest_Owner_FK FOREIGN KEY ( Rest_Owner_username ) REFERENCES Rest_Owner ( username ) ;

ALTER TABLE Restaurant ADD CONSTRAINT Restaurant_Restaurant_type_FK FOREIGN KEY ( Restaurant_type_id_type ) REFERENCES Restaurant_type ( id_type ) ;

ALTER TABLE Usuario ADD CONSTRAINT User_Language_FK FOREIGN KEY ( Language_lang_id ) REFERENCES Language ( lang_id ) ;


create or replace FUNCTION VALIDARUSUARIO 
(
  pusername IN VARCHAR2,
  ppassword IN VARCHAR2
) RETURN VARCHAR2 AS
  valor integer;
  tipo VARCHAR2(20):='NULL';
BEGIN
  select count(*) into valor from usuario natural join mob_user where username=pUSERNAME AND password=ppassword;
  if valor > 0 then tipo:= 'user';
  end if;
  select count(*) into valor from usuario natural join rest_owner where username=pUSERNAME AND password=ppassword;
  if valor > 0 then tipo:= 'rest_owner';
  end if;
  select count(*) into valor from usuario natural join admin where username=pUSERNAME AND password=ppassword;
  if valor > 0 then tipo:= 'admin';
  end if;
  
  
  
  RETURN tipo;
END VALIDARUSUARIO;
/
-----------------
-- DATA INSERT --
-----------------
create or replace trigger auditoria after delete or insert or delete on usuario for each row
begin
  if inserting then
    insert into Usuario_Aud values ('I',null,null,null,null,:new.username,:new.password,:new.email,:new.Language_lang_id);
  end if;
  if deleting then
    insert into Usuario_Aud values ('D',:old.username,:old.password,:old.email,:old.Language_lang_id,null,null,null,null);
  end if;
  if updating then
    insert into Usuario_Aud values ('U',:old.username,:old.password,:old.email,:old.Language_lang_id,:new.username,:new.password,:new.email,:new.Language_lang_id);
  end if;
end;
/
insert into Language values ('English',0,null);
insert into Language values ('Español',1,null);

insert into Usuario values ('Administrador','admin','admin@bd.com',1);
insert into Usuario values ('juancho','bd111','juanch@hotmail.com',1);
insert into Usuario values ('adrian','bd931','adrnn@hotmail.com',0);
insert into Usuario values ('admin2','add','da@hotmail.com',1);
insert into Usuario values ('juanelpanseroti','321','dsa@hotmail.com',0);
insert into Usuario values ('johan','32156','xaaa@hotmail.com',0);

insert into admin values ('Administrador');
insert into admin values ('admin2');

insert into rest_owner values('juancho',4608272,null,'Cll 10 No 5-35','F');
insert into rest_owner values('juanelpanseroti',4608279,null,'Cll 9 No 136-35','F');

insert into mob_user values ('johan',null);
insert into mob_user values ('adrian',null);

insert into restaurant_type values (0,'Comida Rapida');
insert into restaurant_type values (1,'Comida Peruana');
insert into restaurant_type values (2,'Comida Italiana');

insert into RESTAURANT values ('El Palacio del colesterol','Cra 7 No 50-20',5403978,'palacios@lomasfino.com',4000,null,null,3204567646,0,'juancho',0,'N');
insert into RESTAURANT values ('Viva peru','Cra 8 No 32-20',5432978,'peruperu@lomasfino.com',15000,null,null,3214567646,1,'juancho',1,'N');
insert into RESTAURANT values ('Espagueti','Cll 134 No 7-20',5432548,'italian@bambino.com',40000,null,null,3214587646,0,'juanelpanseroti',2,'N');


insert into menu values (0,0,'juancho');
insert into menu values (1,0,'juancho');

insert into dish values ('La rompe corazones',null,6000,0,0,'juancho');
insert into dish values ('Ataque cardiaco',null,4500,0,0,'juancho');
insert into dish values ('Cuy',null,15900,1,0,'juancho');
insert into dish values ('Asado Peruano',null,20000,1,0,'juancho');

insert into updates values (null,2);
insert into updates values (null,3);

insert into Dist_bet_RxR values (0,'juancho',1,'juancho',5);
insert into Dist_bet_RxR values (0,'juancho',0,'juanelpanseroti',3);
insert into Dist_bet_RxR values (1,'juancho',0,'juanelpanseroti',7);

insert into dist_betw_uxr values ('adrian',0,'juancho',3);
insert into dist_betw_uxr values ('adrian',1,'juancho',4);
insert into dist_betw_uxr values ('adrian',0,'juanelpanseroti',5);
insert into dist_betw_uxr values ('johan',0,'juancho',3);
insert into dist_betw_uxr values ('johan',1,'juancho',8);
insert into dist_betw_uxr values ('johan',0,'juanelpanseroti',9);

desc user_reviewxrest;



commit;

-------------
-- QUERIES --
-------------
-- 1 Por cada restaurante mostrar cual es su plato más caro y su plato más económico (Nombre del plato y valor), así como el valor promedio de sus platos. (5)
with restxmenu as (select adress,Rest_name,rest_id, restaurant.REST_OWNER_USERNAME, menu_id
                    from restaurant inner join menu on (rest_id=menu.RESTAURANT_REST_ID and restaurant.REST_OWNER_USERNAME=menu.RESTAURANT_USERNAME)),
xdish as (select adress,rest_name,rest_id,rest_owner_username,menu_id,dish.PRICE 
          from restxmenu inner join dish on (rest_id=menu_rest_id and rest_owner_username=menu_username and menu_id=menu_menu_id)),
plato_caro as (select max(price) as Plato_mas_caro,adress,rest_name,rest_id,rest_owner_username,menu_id
              from xdish group by (adress,rest_name,rest_id,rest_owner_username,menu_id)),
plato_barato as(select min(price) as Plato_mas_barato,adress,rest_name,rest_id,rest_owner_username,menu_id 
              from xdish group by (adress,rest_name,rest_id,rest_owner_username,menu_id)),
plato_avg as (select avg(price) as Promedio_platos,adress,rest_name,rest_id,rest_owner_username,menu_id
              from xdish group by (adress,rest_name,rest_id,rest_owner_username,menu_id))
select adress,rest_name,plato_mas_caro,plato_mas_barato,promedio_platos
from plato_caro natural inner join plato_barato natural inner join plato_avg
;

-- 2 Por cada usuario mostrar cual es el restaurante más cercano y el mas lejano.(5)
select mob_user_username as usuario,to_char(max(distance) || ' km' ) as rest_mas_lejos,to_char(min(distance) || ' km' ) as rest_mas_cercano 
from Dist_betw_uxR
group by mob_user_username;

-- 3 Por idioma cuántos usuarios han configurado el mismo como el idioma en el cual desean ver su aplicación.(5)

select Name,COUNT(*) from Usuario inner join LANGUAGE on (lang_id=language_lang_id) group by language_lang_id,LANGUAGE.NAME;

/* -- 4 Un listado de mejor a calificado a peor calificado por cada criterio. (Para este listado no fué creado un requerimiento funcional, pero el aplicativo necesita tenerlo,
por esta razón escriba su requerimiento funcional teniendo en cuenta que la calificación la puede hacer por precio, calidad de los platos, variedad del menú, atención, 
téngalo en cuenta para su modelo sabiendo que estos criterios son dinámicos y cambian muy frecuentemente en el tiempo y resuelva la consulta). (10) */
select*
from Usuario_Aud;

