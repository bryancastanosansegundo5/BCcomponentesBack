package com.mvc.service.producto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.model.carrito.CarritoVO;
import com.mvc.model.configuracion.ConfiguracionVO;
import com.mvc.model.producto.ProductoDTO;
import com.mvc.model.producto.ProductoListadoDTO;
import com.mvc.model.producto.ProductoVO;
import com.mvc.model.usuario.UsuarioVO;
import com.mvc.repository.CarritoRepository;
import com.mvc.repository.CategoriaRepository;
import com.mvc.repository.ConfiguracionRepository;
import com.mvc.repository.DetalleRepository;
import com.mvc.repository.PedidoRepository;
import com.mvc.repository.ProductoRepository;
import com.mvc.repository.ProveedorRepository;
import com.mvc.service.carrito.CarritoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private DetalleRepository detalleRepository;
    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    private ConfiguracionRepository configuracionRepository;
    @Autowired
    private CarritoService carritoService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public void insertar(ProductoDTO dto) {
        ProductoVO producto = new ProductoVO();

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setCaracteristicas(dto.getCaracteristicas());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setConsumo(dto.getConsumo());
        if (dto.getImpuesto() == 0) {
            ConfiguracionVO config = configuracionRepository.findByClave("impuesto_por_defecto").orElse(null);
            if (config != null) {
                producto.setImpuesto(Double.parseDouble(config.getValor()));
            }
        } else {
            producto.setImpuesto(dto.getImpuesto());
        }
        producto.setBaja(dto.isBaja());
        producto.setImagen(dto.getImagen());
        producto.setSocket(dto.getSocket());
        producto.setTipoRam(dto.getTipoRam());
        producto.setPcie(dto.getPcie());
        producto.setPotenciaW(dto.getPotenciaW());
        producto.setChipset(dto.getChipset());
        producto.setFactorForma(dto.getFactorForma());

        producto.setCategoria(
                categoriaRepository.findById(dto.getCategoriaId())
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría no encontrada")));

        producto.setProveedor(
                proveedorRepository.findById(dto.getProveedorId())
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proveedor no encontrado")));

        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void actualizar(Long id, ProductoDTO dto) {
        ProductoVO producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setCaracteristicas(dto.getCaracteristicas());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setConsumo(dto.getConsumo());
        if (dto.getImpuesto() == 0) {
            ConfiguracionVO config = configuracionRepository.findByClave("impuesto_por_defecto").orElse(null);
            if (config != null) {
                producto.setImpuesto(Double.parseDouble(config.getValor()));
            }
        } else {
            producto.setImpuesto(dto.getImpuesto());
        }
        producto.setBaja(dto.isBaja());
        producto.setImagen(dto.getImagen());
        producto.setSocket(dto.getSocket());
        producto.setTipoRam(dto.getTipoRam());
        producto.setPcie(dto.getPcie());
        producto.setPotenciaW(dto.getPotenciaW());
        producto.setChipset(dto.getChipset());
        producto.setFactorForma(dto.getFactorForma());
        producto.setVendidos(dto.getVendidos());
        producto.setConsumo(dto.getConsumo());

        if (dto.getCategoriaId() != null) {
            producto.setCategoria(
                    categoriaRepository.findById(dto.getCategoriaId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "Categoría no encontrada")));
        }

        if (dto.getProveedorId() != null) {
            producto.setProveedor(
                    proveedorRepository.findById(dto.getProveedorId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "Proveedor no encontrado")));
        }

        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void borrarPorId(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProductoVO getById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public List<ProductoVO> getAll() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional
    public List<ProductoVO> getByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Page<ProductoDTO> getByCategoria(Long categoriaId, Pageable pageable) {
        Page<ProductoVO> productos = productoRepository.findByCategoriaId(categoriaId, pageable);

        return productos.map(this::transformarVOaDTO);
    }

    @Override
    @Transactional
    public List<ProductoVO> getByStockMenorA(int cantidad) {
        return productoRepository.findByStockLessThan(cantidad);
    }

    // Método que comprueba si la demanda en los carritos supera el stock del
    // producto
    @Override
    public void verificarStockEnCarritos(Long productoId) {
        int enCarritos = carritoService.contarTotalPorProducto(productoId);
        ProductoVO producto = productoRepository.findById(productoId).orElse(null);

        if (producto != null && producto.getStock() > 0 && enCarritos > producto.getStock()) {
            System.out.println("🚨 Demanda excedida para el producto: " + producto.getNombre());
            System.out.println("➡️ En carritos: " + enCarritos + " | Stock actual: " + producto.getStock());

            // Si se supera el stock, notifica automáticamente
            notificarUsuarios(productoId);
        }
    }

    @Transactional
    @Override
    public void notificarUsuarios(Long productoId) {
        List<CarritoVO> carritos = carritoRepository.findByProductoId(productoId);

        for (CarritoVO carrito : carritos) {
            UsuarioVO usuario = carrito.getUsuario();
            ProductoVO producto = carrito.getProducto();

            String mensajePlano = "¡Hola " + usuario.getNombre() + "! El producto '" +
                    producto.getNombre() + "' está a punto de agotarse.";

            // Creamos un objeto con el ID y el mensaje
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", producto.getId());
            payload.put("mensaje", mensajePlano);

            try {
                // Convertimos a JSON
                String mensajeJson = objectMapper.writeValueAsString(payload);

                System.out.println("📤 Notificando a usuario " + usuario.getEmail() + ": " + mensajePlano);

                // Enviamos el JSON al frontend
                messagingTemplate.convertAndSend("/avisos/stock/usuario/" + usuario.getId(), mensajeJson);
            } catch (Exception e) {
                System.err.println("❌ Error al convertir mensaje a JSON: " + e.getMessage());
            }
        }
    }

    @Override
    public List<ProductoDTO> getProductos() {
        return productoRepository.findAll().stream()
                .map(p -> {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setId(p.getId());
                    dto.setNombre(p.getNombre());
                    dto.setDescripcion(p.getDescripcion());
                    dto.setCaracteristicas(p.getCaracteristicas());
                    dto.setPrecio(p.getPrecio());
                    dto.setStock(p.getStock());
                    dto.setImpuesto(p.getImpuesto());
                    dto.setBaja(p.isBaja());
                    dto.setImagen(p.getImagen());
                    dto.setSocket(p.getSocket());
                    dto.setTipoRam(p.getTipoRam());
                    dto.setPcie(p.getPcie());
                    dto.setPotenciaW(p.getPotenciaW());
                    dto.setVendidos(p.getVendidos());
                    dto.setConsumo(p.getConsumo());
                    dto.setChipset(p.getChipset());
                    dto.setFactorForma(p.getFactorForma());
                    dto.setCategoriaId(p.getCategoria().getId());
                    dto.setProveedorId(p.getProveedor() != null ? p.getProveedor().getId() : null);
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> filtrarPorCampo(String campo, String valor) {
        List<ProductoVO> productos;

        switch (campo) {
            case "nombre" ->
                productos = productoRepository.findByNombreContainingIgnoreCase(valor);
            case "stock" -> {
                if (valor.contains("-")) {
                    String[] partes = valor.split("-");
                    int min = Integer.parseInt(partes[0]);
                    int max = partes.length > 1 && !partes[1].isBlank() ? Integer.parseInt(partes[1])
                            : Integer.MAX_VALUE;
                    productos = productoRepository.findByStockBetween(min, max);
                } else {
                    int exacto = Integer.parseInt(valor);
                    productos = productoRepository.findByStockGreaterThanEqual(exacto);
                }
            }
            case "id" -> {
                List<ProductoVO> todos = productoRepository.findAll();
                productos = todos.stream()
                        .filter(p -> String.valueOf(p.getId()).contains(valor))
                        .toList();
            }

            case "categoria" ->
                productos = productoRepository.findByCategoriaId(
                        categoriaRepository.findByNombreIgnoreCase(valor)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "Categoría no encontrada"))
                                .getId());

            case "proveedor" ->
                productos = productoRepository.findByProveedorId(
                        proveedorRepository.findByNombreContainingIgnoreCase(valor)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "Proveedor no encontrado"))
                                .getId());

            case "baja" -> {
                boolean activo = Boolean.parseBoolean(valor);
                productos = productoRepository.findByBaja(!activo);
            }

            default ->
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campo de búsqueda no válido");
        }

        return productos.stream()
                .map(this::transformarVOaDTO)
                .toList();
    }

    @Override
    public List<ProductoVO> getByIds(List<Long> ids) {
        return productoRepository.findAllById(ids);
    }

    @Override
    public Map<String, List<ProductoVO>> getRecomendaciones(List<Long> productoIds, Long usuarioId) {
        Map<String, List<ProductoVO>> resultado = new HashMap<>();

        // 🔁 Fallback si no hay usuario o historial
        if (usuarioId == null || productoIds == null || productoIds.isEmpty()) {
            // Cargar productos más vendidos (y activos)
            List<ProductoVO> masVendidos = productoRepository.findMasVendidosOrdenados().stream()
                    .filter(p -> !p.isBaja())
                    .toList();

            // Usar primeros 10 como "similares"
            List<ProductoVO> similaresFallback = masVendidos.stream().limit(10).toList();

            // El resto como "complementarios", sin repetir
            Set<Long> idsSimilares = similaresFallback.stream()
                    .map(ProductoVO::getId)
                    .collect(Collectors.toSet());

            List<ProductoVO> complementariosFallback = masVendidos.stream()
                    .filter(p -> !idsSimilares.contains(p.getId()))
                    .limit(10)
                    .collect(Collectors.toList());

            // Si hay menos de 10 complementarios, rellenar
            if (complementariosFallback.size() < 10) {
                for (ProductoVO producto : masVendidos) {
                    if (complementariosFallback.size() >= 10)
                        break;
                    if (!complementariosFallback.contains(producto)) {
                        complementariosFallback.add(producto);
                    }
                }
            }

            resultado.put("similares", similaresFallback);
            resultado.put("complementarios", complementariosFallback);
            return resultado;
        }

        // 🕑 Productos comprados por el usuario en los últimos 2 años
        LocalDateTime haceDosAnios = LocalDateTime.now().minusYears(2);
        List<Long> comprados = detalleRepository.findProductoIdsCompradosRecientemente(usuarioId, haceDosAnios);

        // 🔍 Separar los productos vistos por el usuario
        List<Long> vistosNoComprados = new ArrayList<>();
        List<Long> vistosYComprados = new ArrayList<>();
        for (Long id : productoIds) {
            if (comprados.contains(id))
                vistosYComprados.add(id);
            else
                vistosNoComprados.add(id);
        }

        // 📂 Categorías de los productos comprados, para quitarlas de complementarios
        Set<Long> categoriasCompradas = comprados.stream()
                .map(productoRepository::findById)
                .filter(Optional::isPresent)
                .map(opt -> opt.get().getCategoria().getId())
                .collect(Collectors.toSet());

        // ✅ Similares: productos de las mismas categorías vistas (no compradas)
        List<ProductoVO> similaresBrutos = productoRepository.findSimilaresPorProductoIds(vistosNoComprados).stream()
                .filter(p -> !p.isBaja())
                .toList();

        // 🔄 Agrupar por categoría para mezclar los resultados
        Map<Long, List<ProductoVO>> similaresAgrupados = similaresBrutos.stream()
                .collect(Collectors.groupingBy(p -> p.getCategoria().getId()));

        List<ProductoVO> similaresMezclados = new ArrayList<>();
        boolean seguirSimilares = true;
        int rondaSimilares = 0;
        while (seguirSimilares && similaresMezclados.size() < 10) {
            seguirSimilares = false;
            for (List<ProductoVO> grupo : similaresAgrupados.values()) {
                if (rondaSimilares < grupo.size()) {
                    similaresMezclados.add(grupo.get(rondaSimilares));
                    seguirSimilares = true;
                    if (similaresMezclados.size() == 10)
                        break;
                }
            }
            rondaSimilares++;
        }

        // ✅ Complementarios: productos de otras categorías, según lógica técnica
        List<ProductoVO> complementariosBrutos = productoRepository.findComplementariosPorProductoIds(vistosYComprados)
                .stream()
                .filter(p -> !categoriasCompradas.contains(p.getCategoria().getId()))
                .filter(p -> !p.isBaja())
                .toList();

        // 🔄 Mezclar por categorías también
        Map<Long, List<ProductoVO>> agrupados = complementariosBrutos.stream()
                .collect(Collectors.groupingBy(p -> p.getCategoria().getId()));

        List<ProductoVO> mezclaFinal = new ArrayList<>();
        boolean seguir = true;
        int ronda = 0;
        while (seguir && mezclaFinal.size() < 10) {
            seguir = false;
            for (List<ProductoVO> grupo : agrupados.values()) {
                if (ronda < grupo.size()) {
                    mezclaFinal.add(grupo.get(ronda));
                    seguir = true;
                    if (mezclaFinal.size() == 10)
                        break;
                }
            }
            ronda++;
        }

        // 🔁 Fallback si no hay suficientes resultados
        List<ProductoVO> masVendidos = productoRepository.findMasVendidosOrdenados().stream()
                .filter(p -> !p.isBaja())
                .toList();

        if (similaresMezclados.isEmpty()) {
            similaresMezclados = masVendidos.stream().limit(10).toList();
        }
        if (similaresMezclados.size() < 10) {
            Set<Long> idsYaUsados = similaresMezclados.stream()
                    .map(ProductoVO::getId)
                    .collect(Collectors.toSet());

            List<ProductoVO> extra = masVendidos.stream()
                    .filter(p -> !idsYaUsados.contains(p.getId()))
                    .limit(10 - similaresMezclados.size())
                    .toList();

            similaresMezclados.addAll(extra);
        }

        if (mezclaFinal.isEmpty()) {
            Set<Long> idsSimilares = similaresMezclados.stream()
                    .map(ProductoVO::getId)
                    .collect(Collectors.toSet());

            mezclaFinal = masVendidos.stream()
                    .filter(p -> !idsSimilares.contains(p.getId()))
                    .limit(10)
                    .toList();
        }

        resultado.put("similares", similaresMezclados);
        resultado.put("complementarios", mezclaFinal);
        return resultado;
    }

    @Override
public List<ProductoVO> getMasVendidos() {
    List<ProductoVO> lista = productoRepository.findMasVendidosOrdenados();
    if (lista.isEmpty()) {
        // Si no hay vendidos, devolvemos 12 productos aleatorios
        return productoRepository.findProductosAleatorios().stream()
                .limit(12)
                .toList();
    }
    return lista.size() > 12 ? lista.subList(0, 12) : lista;
}


    @Override
    public List<ProductoVO> getMenosVendidosConStock() {
        return productoRepository.findMenosVendidosConMasStock().stream()
                .limit(9)
                .toList();
    }

    public ProductoDTO transformarVOaDTO(ProductoVO producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setCaracteristicas(producto.getCaracteristicas());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setImpuesto(producto.getImpuesto());
        dto.setBaja(producto.isBaja());
        dto.setImagen(producto.getImagen());
        dto.setSocket(producto.getSocket());
        dto.setTipoRam(producto.getTipoRam());
        dto.setPcie(producto.getPcie());
        dto.setPotenciaW(producto.getPotenciaW());
        dto.setChipset(producto.getChipset());
        dto.setFactorForma(producto.getFactorForma());
        dto.setCategoriaId(producto.getCategoria().getId());
        dto.setProveedorId(producto.getProveedor() != null ? producto.getProveedor().getId() : null);
        dto.setConsumo(producto.getConsumo());
        return dto;
    }

    public ProductoVO transformarDTOaVO(ProductoDTO dto) {
        ProductoVO vo = new ProductoVO();
        vo.setNombre(dto.getNombre());
        vo.setDescripcion(dto.getDescripcion());
        vo.setCaracteristicas(dto.getCaracteristicas());
        vo.setPrecio(dto.getPrecio());
        vo.setStock(dto.getStock());
        vo.setImpuesto(dto.getImpuesto());
        vo.setBaja(dto.isBaja());
        vo.setImagen(dto.getImagen());
        vo.setSocket(dto.getSocket());
        vo.setTipoRam(dto.getTipoRam());
        vo.setPcie(dto.getPcie());
        vo.setPotenciaW(dto.getPotenciaW());
        vo.setConsumo(dto.getConsumo());
        vo.setChipset(dto.getChipset());
        vo.setFactorForma(dto.getFactorForma());

        vo.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElse(null));
        vo.setProveedor(
                dto.getProveedorId() != null ? proveedorRepository.findById(dto.getProveedorId()).orElse(null) : null);

        return vo;
    }

    @Override
    public Page<ProductoDTO> getPaginado(Pageable pageable) {
        return productoRepository.findByBajaFalse(pageable)
                .map(this::transformarVOaDTO);
    }

    @Override
    public List<ProductoDTO> obtenerTop10PorCategoria(Long id) {
        return productoRepository
                .findTop10ByCategoriaIdOrderByVendidosDesc(id)
                .stream()
                .map(this::transformarVOaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductoDTO> buscarCatalogoPublico(String nombre, Long categoriaId, Pageable pageable) {
        Page<ProductoVO> page = productoRepository.buscarCatalogoPublico(
                (nombre == null || nombre.isBlank()) ? null : nombre,
                categoriaId,
                pageable);
        return page.map(this::transformarVOaDTO);
    }

    @Override
    @Transactional
    public void importarDesdeExcel(MultipartFile archivoExcel) {
        try (InputStream inputStream = archivoExcel.getInputStream();
                Workbook libroExcel = new XSSFWorkbook(inputStream)) {
            Sheet hojaProductos = libroExcel.getSheetAt(0);

            for (int indiceFila = 1; indiceFila <= hojaProductos.getLastRowNum(); indiceFila++) {
                Row filaActual = hojaProductos.getRow(indiceFila);
                if (filaActual == null)
                    continue;

                ProductoDTO productoDTO = new ProductoDTO();
                productoDTO.setNombre(obtenerString(filaActual, 0));
                productoDTO.setDescripcion(obtenerString(filaActual, 1));
                productoDTO.setCaracteristicas(obtenerString(filaActual, 2));
                productoDTO.setPrecio(obtenerValorNumericoDesdeCelda(filaActual, 3));
                productoDTO.setStock((int) obtenerValorNumericoDesdeCelda(filaActual, 4));
                productoDTO.setImpuesto(obtenerValorNumericoDesdeCelda(filaActual, 5));
                productoDTO.setBaja("1".equals(obtenerString(filaActual, 6)));
                productoDTO.setSocket(obtenerString(filaActual, 7));
                productoDTO.setTipoRam(obtenerString(filaActual, 8));
                productoDTO.setPcie(obtenerString(filaActual, 9));
                productoDTO.setPotenciaW((int) obtenerValorNumericoDesdeCelda(filaActual, 10));
                productoDTO.setConsumo((int) obtenerValorNumericoDesdeCelda(filaActual, 11));
                productoDTO.setChipset(obtenerString(filaActual, 12));
                productoDTO.setVendidos((int) obtenerValorNumericoDesdeCelda(filaActual, 13));
                productoDTO.setFactorForma(obtenerString(filaActual, 14));
                productoDTO.setImagen(obtenerString(filaActual, 17));

                // Buscar categoría por nombre (columna 15)
                String nombreCategoria = obtenerString(filaActual, 15);
                productoDTO.setCategoriaId(
                        categoriaRepository.findByNombreIgnoreCase(nombreCategoria)
                                .map(categoria -> categoria.getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Categoría no encontrada: " + nombreCategoria)));

                // Buscar proveedor por nombre (columna 16)
                String nombreProveedor = obtenerString(filaActual, 16);
                productoDTO.setProveedorId(
                        proveedorRepository.findByNombreIgnoreCase(nombreProveedor)
                                .map(proveedor -> proveedor.getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Proveedor no encontrado: " + nombreProveedor)));

                ProductoVO producto = transformarDTOaVO(productoDTO);
                productoRepository.save(producto);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar el archivo Excel");
        }
    }

    private String obtenerString(Row fila, int columna) {
        Cell celda = fila.getCell(columna);
        return celda == null ? null
                : celda.getCellType() == CellType.STRING
                        ? celda.getStringCellValue()
                        : String.valueOf((int) celda.getNumericCellValue());
    }

    private double obtenerValorNumericoDesdeCelda(Row filaExcel, int indiceColumna) {
        Cell celda = filaExcel.getCell(indiceColumna);
    
        if (celda == null || celda.getCellType() == CellType.BLANK) return 0;
    
        if (celda.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(celda.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    
        return celda.getNumericCellValue();
    }
     

    @Override
public byte[] generarPlantillaExcel() {
    try (Workbook libroExcel = new XSSFWorkbook()) {
        Sheet hojaProductos = libroExcel.createSheet("Productos");

        // Cabecera
        Row filaCabecera = hojaProductos.createRow(0);
        String[] columnasCabecera = {
            "nombre*", "descripcion", "caracteristicas", "precio", "stock", "impuesto", "baja",
            "socket", "tipoRam", "pcie", "potenciaW", "consumo", "chipset", "vendidos",
            "factorForma", "categoriaNombre*", "proveedorNombre*", "imagen*"
        };

        for (int indiceColumna = 0; indiceColumna < columnasCabecera.length; indiceColumna++) {
            filaCabecera.createCell(indiceColumna).setCellValue(columnasCabecera[indiceColumna]);
        }

        // Fila de ejemplo
        Row filaEjemplo = hojaProductos.createRow(1);
        String[] valoresEjemplo = {
            "Intel Core i5-11400F", "Procesador 6 núcleos, 12 hilos", "Socket LGA1200", "179.99", "10", "21", "0",
            "LGA1200", "", "", "", "150", "", "0",
            "", "CPU", "BCcomponentes", "https://ruta.com/img.jpg"
        };

        for (int indiceColumna = 0; indiceColumna < valoresEjemplo.length; indiceColumna++) {
            filaEjemplo.createCell(indiceColumna).setCellValue(valoresEjemplo[indiceColumna]);
        }

        ByteArrayOutputStream salidaExcel = new ByteArrayOutputStream();
        libroExcel.write(salidaExcel);
        return salidaExcel.toByteArray();
    } catch (IOException e) {
        throw new RuntimeException("Error al generar la plantilla Excel", e);
    }
}

}
