package com.mvc.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mvc.model.usuario.UsuarioVO;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioVO, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndClave(String email, String clave);

    Optional<UsuarioVO> findByEmail(String email);

    List<UsuarioVO> findByRolId(int rolId);

    List<UsuarioVO> findByRolIdAndNombreContainingIgnoreCase(int rolId, String nombre);

    List<UsuarioVO> findByRolIdAndApellidosContainingIgnoreCase(int rolId, String apellidos);

    List<UsuarioVO> findByRolIdAndEmailContainingIgnoreCase(int rolId, String email);

    List<UsuarioVO> findByRolIdAndTelefonoContainingIgnoreCase(int rolId, String telefono);

    List<UsuarioVO> findByRolIdAndProvinciaContainingIgnoreCase(int rolId, String provincia);

    List<UsuarioVO> findByRolIdAndBaja(int rolId, boolean baja);

    List<UsuarioVO> findByNombreContainingIgnoreCase(String nombre);

    List<UsuarioVO> findByProvinciaContainingIgnoreCase(String provincia);

    List<UsuarioVO> findByApellidosContainingIgnoreCase(String apellidos);

    List<UsuarioVO> findByEmailContainingIgnoreCase(String email);

    List<UsuarioVO> findByTelefonoContainingIgnoreCase(String telefono);

    List<UsuarioVO> findByBaja(boolean baja);

    @Query("SELECT COUNT(u) FROM UsuarioVO u")
Long contarTotalUsuarios();

}
