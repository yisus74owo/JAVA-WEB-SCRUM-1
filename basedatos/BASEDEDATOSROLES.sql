-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `mydb` ;

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`Roles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Roles` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Roles` (
  `idRol` INT NOT NULL,
  `Nombre_rol` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idRol`))
ENGINE = INNODB;
USE mydb;

-- -----------------------------------------------------
-- Table `mydb`.`Usuario`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Usuarios` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Usuarios` (
  `idUsuario` INT NOT NULL AUTO_INCREMENT,
  `Nombre_usuario` VARCHAR(45) NOT NULL,
  `Apellido_usuario` VARCHAR(45) NOT NULL,
  `Telefono` VARCHAR(15) NOT NULL,
  `Correo` VARCHAR(45) NOT NULL,
  `Direccion` VARCHAR(45) NULL,
  `Contraseña` VARCHAR(255) NOT NULL,
  `Estado_usuario` TINYINT NOT NULL,
  `Tipo_documento` ENUM('CC', 'NIT', 'PP', 'Otro') NOT NULL,
  `Documento_usuario` VARCHAR(20) NOT NULL,
  `Rol_idRol` INT NOT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE INDEX `Contraseña_UNIQUE` (`Contraseña` ASC),
  INDEX `fk_Usuario_Rol1_idx` (`Rol_idRol` ASC),
  CONSTRAINT `fk_Usuario_Rol1`
    FOREIGN KEY (`Rol_idRol`)
    REFERENCES `mydb`.`Roles` (`idRol`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Productos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Productos` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Productos` (
  `idProducto` INT NOT NULL AUTO_INCREMENT,
  `Nombre_Producto` VARCHAR(45) NOT NULL,
  `Categoria` ENUM('Medicamento', 'Alimento', 'Accesorio', 'Insumo') NOT NULL,
  `Marca_Producto` VARCHAR(45) NOT NULL,
  `Cantidad_Producto` INT NOT NULL,
  `Precio_Producto` DOUBLE NOT NULL,
  `Nombre_Proveedor` VARCHAR(45) NOT NULL,
  `Fecha_Caducidad` DATE NOT NULL,
  `Descripcion_Producto` VARCHAR(45) NOT NULL,
  `Stock_Producto` INT NOT NULL,
  PRIMARY KEY (`idProducto`))
ENGINE = INNODB;

SHOW CREATE TABLE productos;


-- -----------------------------------------------------
-- Table `mydb`.`Proveedor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Proveedor` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Proveedor` (
  `idProveedor` INT NOT NULL ,
  `Nombre_Proveedor` VARCHAR(45) NOT NULL,
  `Direccion_Proveedor` VARCHAR(45) NOT NULL,
  `Correo_Proveedor` VARCHAR(45) NOT NULL,
  `Representante_Legal` VARCHAR(45) NOT NULL,
  `Nit` VARCHAR(20) NOT NULL,
  `Categoria` VARCHAR(45) NOT NULL,
  `Telefono` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`idProveedor`),
  UNIQUE INDEX `idProveedor_UNIQUE` (`idProveedor` ASC))
ENGINE = INNODB;



-- -----------------------------------------------------
-- Table `mydb`.`Cotizacion`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Cotizacion` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Cotizacion` (
  `idCotizacion` INT NOT NULL,
  `Nombre_Proveedor` VARCHAR(45) NOT NULL,
  `ID_Producto` INT NOT NULL,
  `Cantidad_Producto` INT NOT NULL,
  `Proveedor_idProveedor` INT NOT NULL,
  PRIMARY KEY (`idCotizacion`),
  INDEX `fk_Cotizacion_Proveedor1_idx` (`Proveedor_idProveedor` ASC),
  CONSTRAINT `fk_Cotizacion_Proveedor1`
    FOREIGN KEY (`Proveedor_idProveedor`)
    REFERENCES `mydb`.`Proveedor` (`idProveedor`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Novedad_Inventario`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Novedad_Inventario` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Novedad_Inventario` (
  `ID_Novedad` INT NOT NULL AUTO_INCREMENT,
  `ID_Producto` INT NOT NULL,
  `ID_Insumo` INT NOT NULL,
  `Fecha_Novedad` DATE NOT NULL,
  `Descripcion` VARCHAR(45) NOT NULL,
  `Inventario_idInsumo` INT NOT NULL,
  `Productos_idProducto` INT NOT NULL,
  PRIMARY KEY (`ID_Novedad`, `Productos_idProducto`),
  INDEX `fk_Novedad_Inventario_Productos1_idx` (`Productos_idProducto` ASC),
  CONSTRAINT `fk_Novedad_Inventario_Productos1`
    FOREIGN KEY (`Productos_idProducto`)
    REFERENCES `mydb`.`Productos` (`idProducto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Novedad_Proveedores`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Novedad_Proveedores` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Novedad_Proveedores` (
  `idNovedad` INT NOT NULL,
  `Novedad_Descripcion` VARCHAR(45) NOT NULL,
  `Factura_Estado` VARCHAR(45) NOT NULL,
  `Proveedor_idProveedor` INT NOT NULL,
  PRIMARY KEY (`idNovedad`),
  INDEX `fk_Novedad_Proveedores_Proveedor1_idx` (`Proveedor_idProveedor` ASC),
  CONSTRAINT `fk_Novedad_Proveedores_Proveedor1`
    FOREIGN KEY (`Proveedor_idProveedor`)
    REFERENCES `mydb`.`Proveedor` (`idProveedor`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Servicios`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Servicios` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Servicios` (
  `idServicio` INT NOT NULL,
  `Nombre_Servicio` VARCHAR(45) NOT NULL,
  `Categoria_Servicio` VARCHAR(45) NOT NULL,
  `Precio_Servicio` DOUBLE NOT NULL,
  `Correo` VARCHAR(45) NOT NULL,
  `Nombre_Mascota` VARCHAR(10) NOT NULL,
  `Descripcion_Servicio` VARCHAR(45) NOT NULL,
  `Calendario_Servicio` DATE NOT NULL,
  PRIMARY KEY (`idServicio`))
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Informacion_Domicilio`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Informacion_Domicilio` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Informacion_Domicilio` (
  `Telefono` INT NOT NULL,
  `Correo` VARCHAR(45) NOT NULL,
  `Horario_Entrega` DATETIME(6) NOT NULL,
  `Direccion_Envio` VARCHAR(45) NOT NULL,
  `Estado_Entrega` VARCHAR(45) NOT NULL,
  `Auxiliar_idAuxiliar` INT NOT NULL,
  PRIMARY KEY (`Direccion_Envio`))
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Pasarela_Pago`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Pasarela_Pago` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Pasarela_Pago` (
  `Total` DOUBLE NOT NULL,
  `Metodo_Pago` VARCHAR(45) NOT NULL,
  `Direccion_Envio` VARCHAR(45) NOT NULL,
  `idPasarela_Pago` INT NOT NULL,
  `Informacion_Domicilio_Direccion_Envio` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idPasarela_Pago`),
  INDEX `fk_Pasarela_Pago_Informacion_Domicilio1_idx` (`Informacion_Domicilio_Direccion_Envio` ASC),
  CONSTRAINT `fk_Pasarela_Pago_Informacion_Domicilio1`
    FOREIGN KEY (`Informacion_Domicilio_Direccion_Envio`)
    REFERENCES `mydb`.`Informacion_Domicilio` (`Direccion_Envio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Cliente`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Cliente` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Cliente` (
  `idCliente` INT NOT NULL,
  `Nombre_Cliente` VARCHAR(45) NOT NULL,
  `Telefono__Cliente` VARCHAR(15) NOT NULL,
  `Correo_Cliente` VARCHAR(45) NOT NULL,
  `Direccion_Cliente` VARCHAR(45) NOT NULL,
  `Nombre_Mascota` VARCHAR(45) NOT NULL,
  `Usuario_idUsuario` INT NOT NULL,
  `Documento` VARCHAR(45) NOT NULL,
  `Contraseña` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idCliente`, `Usuario_idUsuario`),
  UNIQUE INDEX `idUsuario_UNIQUE` (`idCliente` ASC),
  INDEX `fk_Cliente_Usuario1_idx` (`Usuario_idUsuario` ASC),
  CONSTRAINT `fk_Cliente_Usuario1`
    FOREIGN KEY (`Usuario_idUsuario`)
    REFERENCES `mydb`.`Usuario` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Landing_page`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Landing_page` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Landing_page` (
  `idLanding_page` INT NOT NULL AUTO_INCREMENT,
  `Nombre_Producto` VARCHAR(45) NOT NULL,
  `Precio_Producto` DOUBLE NOT NULL,
  `Descripcion_Producto` VARCHAR(45) NOT NULL,
  `Imagen_Producto` BLOB NOT NULL,
  `Categoria` VARCHAR(45) NOT NULL,
  `Marca_Producto` VARCHAR(45) NOT NULL,
  `Cliente_idCliente` INT NOT NULL,
  INDEX `fk_Compra_Web_Cliente1_idx` (`Cliente_idCliente` ASC),
  PRIMARY KEY (`idLanding_page`),
  CONSTRAINT `fk_Compra_Web_Cliente1`
    FOREIGN KEY (`Cliente_idCliente`)
    REFERENCES `mydb`.`Cliente` (`idCliente`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Carrito_Compra`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Carrito_Compra` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Carrito_Compra` (
  `idCarrito_Compra` INT NOT NULL,
  `Total` DOUBLE NOT NULL,
  `Cantidad` INT NOT NULL,
  `Metodo_Pago` VARCHAR(45) NOT NULL,
  `Direccion_Envio` VARCHAR(45) NOT NULL,
  `Pasarela_Pago_idPasarela_Pago` INT NOT NULL,
  `Landing_page_idLanding_page` INT NOT NULL,
  PRIMARY KEY (`idCarrito_Compra`),
  INDEX `fk_Carrito_Compra_Pasarela_Pago1_idx` (`Pasarela_Pago_idPasarela_Pago` ASC),
  INDEX `fk_Carrito_Compra_Landing_page1_idx` (`Landing_page_idLanding_page` ASC),
  CONSTRAINT `fk_Carrito_Compra_Pasarela_Pago1`
    FOREIGN KEY (`Pasarela_Pago_idPasarela_Pago`)
    REFERENCES `mydb`.`Pasarela_Pago` (`idPasarela_Pago`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Carrito_Compra_Landing_page1`
    FOREIGN KEY (`Landing_page_idLanding_page`)
    REFERENCES `mydb`.`Landing_page` (`idLanding_page`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Notificaciones`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Notificaciones` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Notificaciones` (
  `Descripcion` VARCHAR(50) NOT NULL,
  `Cliente_idCliente` INT NOT NULL,
  INDEX `fk_Notificaciones_Cliente1_idx` (`Cliente_idCliente` ASC),
  CONSTRAINT `fk_Notificaciones_Cliente1`
    FOREIGN KEY (`Cliente_idCliente`)
    REFERENCES `mydb`.`Cliente` (`idCliente`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Historia_Clinica`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Historia_Clinica` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Historia_Clinica` (
  `idHistoria_Clinica` INT NOT NULL AUTO_INCREMENT,
  `Nombre_medico_veterinario` VARCHAR(45) NOT NULL,
  `Anamnesis` VARCHAR(45) NOT NULL,
  `Examen_Fisico_General` VARCHAR(45) NOT NULL,
  `Abordaje_Diagnostico` VARCHAR(45) NOT NULL,
  `Examenes_Resultados` VARCHAR(45) NOT NULL,
  `Diagnostico_Definitivo` VARCHAR(45) NOT NULL,
  `Plan_Terapeutico` VARCHAR(45) NOT NULL,
  `Pronostico` VARCHAR(45) NOT NULL,
  `Evolucion` VARCHAR(45) NOT NULL,
  `Observaciones` VARCHAR(45) NOT NULL,
  `Anexos` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idHistoria_Clinica`))
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Recetario`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Recetario` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Recetario` (
  `idRecetario` INT NOT NULL AUTO_INCREMENT,
  `Nombre_medico_veterinario` VARCHAR(45) NOT NULL,
  `Medicamento` VARCHAR(45) NOT NULL,
  `Dosis` DOUBLE NOT NULL,
  `Frecuencia` VARCHAR(45) NOT NULL,
  `Recomendaciones` VARCHAR(500) NOT NULL,
  `Dieta` VARCHAR(45) NOT NULL,
  `Observaciones` VARCHAR(45) NOT NULL,
  `Historia_Clinica_idHistoria_Clinica` INT NOT NULL,
  INDEX `fk_Mascota_has_Historia_Clinica_Historia_Clinica1_idx` (`Historia_Clinica_idHistoria_Clinica` ASC),
  PRIMARY KEY (`idRecetario`),
  CONSTRAINT `fk_Mascota_has_Historia_Clinica_Historia_Clinica1`
    FOREIGN KEY (`Historia_Clinica_idHistoria_Clinica`)
    REFERENCES `mydb`.`Historia_Clinica` (`idHistoria_Clinica`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Mascota`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Mascota` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Mascota` (
  `idMascota` INT NOT NULL,
  `Nombre_Mascota` VARCHAR(45) NOT NULL,
  `Raza` VARCHAR(45) NOT NULL,
  `Especie` VARCHAR(45) NOT NULL,
  `Sexo` VARCHAR(45) NOT NULL,
  `Peso` DOUBLE NOT NULL,
  `Color` VARCHAR(45) NOT NULL,
  `Edad` INT NOT NULL,
  `Observaciones` VARCHAR(45) NOT NULL,
  `Cliente_idCliente` INT NOT NULL,
  `Recetario_idRecetario` INT NULL,
  PRIMARY KEY (`idMascota`),
  INDEX `fk_Mascota_Cliente1_idx` (`Cliente_idCliente` ASC),
  INDEX `fk_Mascota_Recetario1_idx` (`Recetario_idRecetario` ASC),
  CONSTRAINT `fk_Mascota_Cliente1`
    FOREIGN KEY (`Cliente_idCliente`)
    REFERENCES `mydb`.`Cliente` (`idCliente`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Mascota_Recetario1`
    FOREIGN KEY (`Recetario_idRecetario`)
    REFERENCES `mydb`.`Recetario` (`idRecetario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Acciones`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Acciones` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Acciones` (
  `idAccion` INT NOT NULL AUTO_INCREMENT,
  `Accionescol` VARCHAR(45) NOT NULL,
  `Nombre_rol` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idAccion`))
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Privilegios`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Privilegios` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Privilegios` (
  `idPrivilegio_usuario` INT NOT NULL AUTO_INCREMENT,
  `Usuario_idUsuario` INT NOT NULL,
  `Acciones_idAccion` INT NOT NULL,
  `Fecha_creacion` DATETIME NOT NULL,
  INDEX `fk_Usuario_has_Acciones_Acciones1_idx` (`Acciones_idAccion` ASC),
  INDEX `fk_Usuario_has_Acciones_Usuario1_idx` (`Usuario_idUsuario` ASC),
  PRIMARY KEY (`idPrivilegio_usuario`),
  CONSTRAINT `fk_Usuario_has_Acciones_Usuario1`
    FOREIGN KEY (`Usuario_idUsuario`)
    REFERENCES `mydb`.`Usuario` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Usuario_has_Acciones_Acciones1`
    FOREIGN KEY (`Acciones_idAccion`)
    REFERENCES `mydb`.`Acciones` (`idAccion`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Cabecera_orden_compra`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Cabecera_orden_compra` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Cabecera_orden_compra` (
  `idOrden_Compra` INT NOT NULL AUTO_INCREMENT,
  `Proveedor_idProveedor` INT NOT NULL,
  `Productos_idProducto` INT NOT NULL,
  `Fecha` DATETIME NOT NULL,
  `Nombre_proveedor` VARCHAR(45) NOT NULL,
  `Correo` VARCHAR(45) NOT NULL,
  INDEX `fk_Proveedor_has_Productos_Productos1_idx` (`Productos_idProducto` ASC),
  INDEX `fk_Proveedor_has_Productos_Proveedor2_idx` (`Proveedor_idProveedor` ASC),
  PRIMARY KEY (`idOrden_Compra`),
  CONSTRAINT `fk_Proveedor_has_Productos_Proveedor2`
    FOREIGN KEY (`Proveedor_idProveedor`)
    REFERENCES `mydb`.`Proveedor` (`idProveedor`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Proveedor_has_Productos_Productos1`
    FOREIGN KEY (`Productos_idProducto`)
    REFERENCES `mydb`.`Productos` (`idProducto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Factura_venta`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Factura_venta` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Factura_venta` (
  `idFacura_venta` VARCHAR(45) NOT NULL,
  `Cliente_idCliente` INT NOT NULL,
  `Cliente_Usuario_idUsuario` INT NOT NULL,
  `Productos_idProducto` INT NOT NULL,
  `Nombre_cliente` VARCHAR(45) NOT NULL,
  `Fecha_factura` DATE NOT NULL,
  INDEX `fk_Cliente_has_Productos_Productos1_idx` (`Productos_idProducto` ASC),
  INDEX `fk_Cliente_has_Productos_Cliente1_idx` (`Cliente_idCliente` ASC, `Cliente_Usuario_idUsuario` ASC),
  PRIMARY KEY (`idFacura_venta`),
  CONSTRAINT `fk_Cliente_has_Productos_Cliente1`
    FOREIGN KEY (`Cliente_idCliente` , `Cliente_Usuario_idUsuario`)
    REFERENCES `mydb`.`Cliente` (`idCliente` , `Usuario_idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Cliente_has_Productos_Productos1`
    FOREIGN KEY (`Productos_idProducto`)
    REFERENCES `mydb`.`Productos` (`idProducto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Calendario`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Calendario` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Calendario` (
  `idCalendario` INT NOT NULL AUTO_INCREMENT,
  `Servicios_idServicio` INT NOT NULL,
  `Cliente_idCliente` INT NOT NULL,
  `Cliente_Usuario_idUsuario` INT NOT NULL,
  `Direccion` VARCHAR(45) NOT NULL,
  `Fecha` DATETIME NOT NULL,
  `Hora` TIME NOT NULL,
  `Nombre_mascota` VARCHAR(45) NOT NULL,
  `Estado` ENUM("Disponible", "No disponible") NOT NULL,
  INDEX `fk_Servicios_has_Cliente_Cliente1_idx` (`Cliente_idCliente` ASC, `Cliente_Usuario_idUsuario` ASC),
  INDEX `fk_Servicios_has_Cliente_Servicios1_idx` (`Servicios_idServicio` ASC),
  PRIMARY KEY (`idCalendario`),
  CONSTRAINT `fk_Servicios_has_Cliente_Servicios1`
    FOREIGN KEY (`Servicios_idServicio`)
    REFERENCES `mydb`.`Servicios` (`idServicio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Servicios_has_Cliente_Cliente1`
    FOREIGN KEY (`Cliente_idCliente` , `Cliente_Usuario_idUsuario`)
    REFERENCES `mydb`.`Cliente` (`idCliente` , `Usuario_idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Promocion`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Promocion` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Promocion` (
  `idPromocion` INT NOT NULL AUTO_INCREMENT,
  `Landing_page_idLanding_page` INT NOT NULL,
  `Productos_idProducto` INT NOT NULL,
  `Periodo` DATE NOT NULL,
  `Descripcion` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idPromocion`),
  INDEX `fk_Landing_page_has_Productos_Productos1_idx` (`Productos_idProducto` ASC),
  INDEX `fk_Landing_page_has_Productos_Landing_page1_idx` (`Landing_page_idLanding_page` ASC),
  CONSTRAINT `fk_Landing_page_has_Productos_Landing_page1`
    FOREIGN KEY (`Landing_page_idLanding_page`)
    REFERENCES `mydb`.`Landing_page` (`idLanding_page`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Landing_page_has_Productos_Productos1`
    FOREIGN KEY (`Productos_idProducto`)
    REFERENCES `mydb`.`Productos` (`idProducto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Detalle_factura_venta`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Detalle_factura_venta` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Detalle_factura_venta` (
  `idDetalle_factura_venta` INT NOT NULL,
  `Cantidad_productos` INT NOT NULL,
  `Detalle_productos` VARCHAR(45) NOT NULL,
  `Nombre_productos` VARCHAR(45) NOT NULL,
  `IVA` FLOAT NOT NULL,
  `Total` DOUBLE NOT NULL,
  `Descuentos` FLOAT NOT NULL,
  `Factura_venta_idFacura_venta` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idDetalle_factura_venta`, `Factura_venta_idFacura_venta`),
  INDEX `fk_Detalle_factura_venta_Factura_venta1_idx` (`Factura_venta_idFacura_venta` ASC),
  CONSTRAINT `fk_Detalle_factura_venta_Factura_venta1`
    FOREIGN KEY (`Factura_venta_idFacura_venta`)
    REFERENCES `mydb`.`Factura_venta` (`idFacura_venta`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `mydb`.`Detalle_orden_compra`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Detalle_orden_compra` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Detalle_orden_compra` (
  `idDetalle_factura_venta` INT NOT NULL,
  `Cantidad_productos` INT NOT NULL,
  `Detalle_productos` VARCHAR(45) NOT NULL,
  `Nombre_productos` VARCHAR(45) NOT NULL,
  `IVA` FLOAT NOT NULL,
  `Total` DOUBLE NOT NULL,
  `Descuentos` FLOAT NOT NULL,
  `Metodo_pago` VARCHAR(45) NOT NULL,
  `Orden_compra_idOrden_Compra` INT NOT NULL,
  PRIMARY KEY (`idDetalle_factura_venta`, `Orden_compra_idOrden_Compra`),
  INDEX `fk_Detalle_orden_compra_Orden_compra1_idx` (`Orden_compra_idOrden_Compra` ASC),
  CONSTRAINT `fk_Detalle_orden_compra_Orden_compra1`
    FOREIGN KEY (`Orden_compra_idOrden_Compra`)
    REFERENCES `mydb`.`Cabecera_orden_compra` (`idOrden_Compra`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;
USE mydb;

-- Si la tabla existe, elimínala
DROP TABLE IF EXISTS `Rol`;

-- Verifica si quedó algún tablespace residual
SELECT * FROM information_schema.innodb_sys_tables WHERE NAME LIKE '%mydb/rol%';

-- Si aparece en los resultados, descarta el tablespace manualmente
-- (esto es avanzado, solo si el problema persiste)

-- Luego crea la tabla nuevamente
CREATE TABLE `Rol` (
  `idRol` INT NOT NULL,
  `Nombre_rol` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idRol`)
) ENGINE=INNODB;

ALTER TABLE `Roles` DISCARD TABLESPACE;
SET FOREIGN_KEY_CHECKS = 1;
INSERT INTO Roles (idRol, Nombre_rol) VALUES 
(1, 'Administrador'),
(2, 'Veterinario'),
(3, 'Auxiliar');

SELECT u.idUsuario, u.Nombre_usuario, u.Correo, u.Contraseña, r.Nombre_rol 
FROM usuarios u 
JOIN roles r ON u.Rol_idRol = r.idRol;
DESCRIBE usuarios;