package com.mvc.config;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mvc.model.categoria.CategoriaVO;
import com.mvc.model.configuracion.ConfiguracionVO;
import com.mvc.model.proovedor.ProveedorVO;
import com.mvc.model.rol.RolVO;
import com.mvc.model.usuario.UsuarioVO;
import com.mvc.repository.CategoriaRepository;
import com.mvc.repository.ConfiguracionRepository;
import com.mvc.repository.ProveedorRepository;
import com.mvc.repository.RolRepository;
import com.mvc.repository.UsuarioRepository;
import com.mvc.service.opcionMenu.OpcionMenuService;
import com.mvc.service.rol.RolService;
import com.mvc.service.usuario.UsuarioService;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initConfiguracion(ConfiguracionRepository configuracionRepository) {
        return args -> {
            if (configuracionRepository.findByClave("numero_factura").isEmpty()) {
                int añoActual = java.time.Year.now().getValue();
                String valorInicial = String.format("F-BC-%d%06d", añoActual, 1);

                ConfiguracionVO config = new ConfiguracionVO();
                config.setClave("numero_factura");
                config.setValor(valorInicial);
                configuracionRepository.save(config);

                System.out.println("⚙️ Configuración 'numero_factura' insertada con valor: " + valorInicial);
            }
            if (configuracionRepository.findByClave("impuesto_por_defecto").isEmpty()) {
                ConfiguracionVO config = new ConfiguracionVO();
                config.setClave("impuesto_por_defecto");
                config.setValor("21");
                configuracionRepository.save(config);

                System.out.println("⚙️ Configuración 'impuesto_por_defecto' insertada con valor: 21%");
            }
        };
    }

    @Bean
    public CommandLineRunner initDatosUsuarioRol(
            RolRepository rolRepository,
            UsuarioService usuarioService,
            OpcionMenuService opcionMenuService) {
        return args -> {
            // 1. Crear roles si no existen
            List<String> roles = List.of("CLIENTE", "EMPLEADO", "ADMIN");

            for (String nombre : roles) {
                if (rolRepository.findByNombre(nombre).isEmpty()) {
                    RolVO rol = new RolVO();
                    rol.setNombre(nombre);
                    rolRepository.save(rol);
                    System.out.println("🔑 Rol insertado: " + nombre);
                }
            }

            // 2. Crear usuarios si no existen
            usuarioService.crearUsuarioSiNoExiste("admin@bccomponentes.com", "12345678", "Admin", "ADMIN");
            usuarioService.crearUsuarioSiNoExiste("empleado@bccomponentes.com", "12345678", "Empleado", "EMPLEADO");

            // 3. Insertar opciones por defecto
            opcionMenuService.insertarOpcionesPorDefecto();
        };
    }

    @Bean
    public CommandLineRunner initCategorias(CategoriaRepository categoriaRepository) {
        return args -> {
            List<String> nombres = List.of("CPU", "GPU", "RAM", "PLACA", "FUENTE", "CAJA");

            for (String nombre : nombres) {
                boolean existe = categoriaRepository.existsByNombreIgnoreCase(nombre);
                if (!existe) {
                    CategoriaVO categoria = new CategoriaVO();
                    categoria.setNombre(nombre);
                    categoria.setBaja(false);
                    categoriaRepository.save(categoria);
                    System.out.println("📦 Categoría insertada: " + nombre);
                }
            }
        };
    }

    @Bean
    public CommandLineRunner initProveedor(ProveedorRepository proveedorRepository) {
        return args -> {
            String nombreProveedor = "BCcomponentes";
            boolean existe = proveedorRepository.existsByNombreIgnoreCase(nombreProveedor);
            if (!existe) {
                ProveedorVO proveedor = new ProveedorVO();
                proveedor.setNombre(nombreProveedor);
                proveedor.setBaja(false);
                proveedorRepository.save(proveedor);
                System.out.println("🏢 Proveedor insertado: " + nombreProveedor);
            }
        };
    }

}
