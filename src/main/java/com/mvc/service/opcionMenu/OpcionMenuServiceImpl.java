package com.mvc.service.opcionMenu;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mvc.model.opcionMenu.OpcionMenuDTO;
import com.mvc.model.opcionMenu.OpcionMenuVO;
import com.mvc.model.rol.RolVO;
import com.mvc.model.usuario.UsuarioVO;
import com.mvc.repository.OpcionMenuRepository;
import com.mvc.repository.RolRepository;
import com.mvc.repository.UsuarioRepository;

@Service
public class OpcionMenuServiceImpl implements OpcionMenuService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private OpcionMenuRepository opcionMenuRepository;

    @Override
public List<OpcionMenuDTO> getOpcionesGestionPorUsuario(Long usuarioId) {
    UsuarioVO usuario = usuarioRepository.findById(usuarioId).orElse(null);
    if (usuario == null) return List.of();

    List<String> rolesAIncluir = new ArrayList<>();

    if (usuario.getRol().getNombre().equalsIgnoreCase("ADMIN")) {
        rolesAIncluir.add("ADMIN");
        rolesAIncluir.add("EMPLEADO");
    } else if (usuario.getRol().getNombre().equalsIgnoreCase("EMPLEADO")) {
        rolesAIncluir.add("EMPLEADO");
    } else {
        return List.of(); // Cliente no tiene acceso a opciones de gestión
    }

    List<OpcionMenuDTO> resultado = new ArrayList<>();

    for (String nombreRol : rolesAIncluir) {
        RolVO rol = rolRepository.findByNombreIgnoreCase(nombreRol).orElse(null);
        if (rol == null) continue;

        List<OpcionMenuVO> opciones = opcionMenuRepository.findByRolAndUbicacion(rol, "LATERAL");

        for (OpcionMenuVO opcion : opciones) {
            if (opcion.getRuta().startsWith("/empleado") || opcion.getRuta().startsWith("/admin")) {
                resultado.add(new OpcionMenuDTO(
                    opcion.getNombre(),
                    opcion.getRuta(),
                    opcion.getRol().getId(),
                    opcion.getUbicacion()
                ));
            }
        }
    }

    return resultado;
}


    @Override
    public List<OpcionMenuDTO> getOpcionesPorRolNombre(String nombreRol) {
        RolVO rol = rolRepository.findByNombreIgnoreCase(nombreRol).orElse(null);
        if (rol == null) return List.of();

        List<OpcionMenuVO> opciones = opcionMenuRepository.findByRol(rol);
        List<OpcionMenuDTO> resultado = new ArrayList<>();
        for (OpcionMenuVO opcion : opciones) {
            resultado.add(new OpcionMenuDTO(opcion.getNombre(), opcion.getRuta(), opcion.getRol().getId(), opcion.getUbicacion()));
        }

        return resultado;
    }

    @Override
    public List<OpcionMenuDTO> getOpcionesPorRolYUbicacion(String nombreRol, String ubicacion) {
        List<String> rolesAIncluir = new ArrayList<>();

        if (nombreRol.equalsIgnoreCase("ADMIN")) {
            rolesAIncluir.add("ADMIN");
            rolesAIncluir.add("EMPLEADO");
            rolesAIncluir.add("CLIENTE");
        } else if (nombreRol.equalsIgnoreCase("EMPLEADO")) {
            rolesAIncluir.add("EMPLEADO");
            rolesAIncluir.add("CLIENTE");
        } else if (nombreRol.equalsIgnoreCase("CLIENTE")) {
            rolesAIncluir.add("CLIENTE");
        }

        List<OpcionMenuDTO> resultado = new ArrayList<>();

        for (String rol : rolesAIncluir) {
            RolVO rolVO = rolRepository.findByNombreIgnoreCase(rol).orElse(null);
            if (rolVO != null) {
                List<OpcionMenuVO> opciones = opcionMenuRepository.findByRolAndUbicacion(rolVO, ubicacion);
                for (OpcionMenuVO opcion : opciones) {
                    resultado.add(new OpcionMenuDTO(
                        opcion.getNombre(),
                        opcion.getRuta(),
                        opcion.getRol().getId(),
                        opcion.getUbicacion()
                    ));
                }
            }
        }

        return resultado;
    }

    @Override
    public void insertarOpcionesPorDefecto() {
        if (opcionMenuRepository.count() > 0) return;

        RolVO rolCliente = rolRepository.findByNombreIgnoreCase("CLIENTE").orElse(null);
        RolVO rolEmpleado = rolRepository.findByNombreIgnoreCase("EMPLEADO").orElse(null);
        RolVO rolAdmin = rolRepository.findByNombreIgnoreCase("ADMIN").orElse(null);

        opcionMenuRepository.save(new OpcionMenuVO("Inicio", "/", rolCliente, "HEADER"));
        opcionMenuRepository.save(new OpcionMenuVO("Catálogo", "/catalogo", rolCliente, "HEADER"));
        opcionMenuRepository.save(new OpcionMenuVO("Mi cesta", "/cesta", rolCliente, "HEADER"));

        opcionMenuRepository.save(new OpcionMenuVO("Mi perfil", "/perfil", rolCliente, "LATERAL"));
        opcionMenuRepository.save(new OpcionMenuVO("Mis pedidos", "/pedidos", rolCliente, "LATERAL"));
        opcionMenuRepository.save(new OpcionMenuVO("Mis valoraciones", "/valoraciones", rolCliente, "LATERAL"));

        opcionMenuRepository.save(new OpcionMenuVO("Productos", "/empleado/gestion-productos", rolEmpleado, "LATERAL"));
        opcionMenuRepository.save(new OpcionMenuVO("Pedidos", "/empleado/gestion-pedidos", rolEmpleado, "LATERAL"));
        opcionMenuRepository.save(new OpcionMenuVO("Clientes", "/empleado/gestion-clientes", rolEmpleado, "LATERAL"));
        opcionMenuRepository.save(new OpcionMenuVO("Proveedores", "/empleado/gestion-proveedores", rolEmpleado, "LATERAL"));

        opcionMenuRepository.save(new OpcionMenuVO("Empleados", "/admin/gestion-empleados", rolAdmin, "LATERAL"));
        opcionMenuRepository.save(new OpcionMenuVO("Administradores", "/admin/gestion-administradores", rolAdmin, "LATERAL"));
        opcionMenuRepository.save(new OpcionMenuVO("Configuración", "/admin/configuracion", rolAdmin, "LATERAL"));
        opcionMenuRepository.save(new OpcionMenuVO("Estadísticas", "/admin/estadisticas", rolAdmin, "LATERAL"));
        opcionMenuRepository.save(new OpcionMenuVO("Valoraciones", "/admin/gestion-valoraciones", rolAdmin, "LATERAL"));
        opcionMenuRepository.save(new OpcionMenuVO("Administracion de Logs", "/admin/logs", rolAdmin, "LATERAL"));


        System.out.println("✅ Opciones de menú insertadas correctamente.");
    }
}
