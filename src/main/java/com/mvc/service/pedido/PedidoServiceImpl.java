package com.mvc.service.pedido;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.io.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mvc.model.detalle.DetalleVO;
import com.mvc.model.estadisticas.ResumenEstadisticasDTO;
import com.mvc.model.pedido.*;
import com.mvc.model.producto.ProductoResumenDTO;
import com.mvc.model.producto.ProductoVO;
import com.mvc.model.usuario.UsuarioVO;
import com.mvc.repository.*;
import com.mvc.service.carrito.CarritoService;
import com.mvc.service.config.ConfiguracionService;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private ConfiguracionService configuracionService;
    @Autowired
    private ConfiguracionRepository configuracionRepository;

    @Autowired
    private CarritoService carritoService;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DetalleRepository detalleRepository;
    @Autowired
    private ValoracionRepository valoracionRepository;

    @Override
    @Transactional
    public List<PedidoVO> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    @Override
    @Transactional
    public PedidoVO obtenerPorId(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public String insertar(PedidoDTO dto) {
        UsuarioVO usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Guardar dirección si el usuario lo solicita
        if (Boolean.TRUE.equals(dto.getDireccion().getGuardarComoPredeterminada())) {
            DireccionEnvioDTO dir = dto.getDireccion();
            usuario.setNombre(dir.getNombre());
            usuario.setApellidos(dir.getApellidos());
            usuario.setDireccion(dir.getDireccion());
            usuario.setPais(dir.getPais());
            usuario.setProvincia(dir.getProvincia());
            usuario.setPoblacion(dir.getPoblacion());
            usuario.setCodigoPostal(dir.getCodigoPostal());
            usuario.setTelefono(dir.getTelefono());
            usuarioRepository.save(usuario);
        }

        // Guardar tarjeta si el usuario lo solicita
        if (dto.getDatosTarjeta() != null && dto.getDatosTarjeta().isGuardar()) {
            DatosTarjetaDTO tarjeta = dto.getDatosTarjeta();
            usuario.setTitularTarjeta(tarjeta.getTitular());
            usuario.setNumeroTarjeta(tarjeta.getNumero());
            usuario.setFechaExpiracion(tarjeta.getFechaExpiracion());
            usuario.setCodigoSeguridad(tarjeta.getCodigoSeguridad());
            usuarioRepository.save(usuario);
        }

        // Transformar y guardar el pedido
        PedidoVO pedido = transformarDTOaVO(dto, usuario);
        pedidoRepository.save(pedido);

        // inicializamos antes del for para sumar el total de la factura
        double totalPedido = 0;

        // Guardar los productos comprados
        for (ProductoPedidoDTO prod : dto.getProductos()) {
            if (prod.getIdProducto() == null) {
                throw new RuntimeException("El id del producto es nulo");
            }

            ProductoVO producto = productoRepository.findById(prod.getIdProducto())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Producto no encontrado con id " + prod.getIdProducto()));

            if (producto.getStock() < prod.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No hay stock suficiente para el producto: " + producto.getNombre());
            }

            // Actualizar stock y baja automática
            int nuevoStock = producto.getStock() - prod.getCantidad();
            producto.setStock(nuevoStock);
            producto.setVendidos(producto.getVendidos() + prod.getCantidad());

            if (nuevoStock <= 0) {
                producto.setBaja(true);
            }

            DetalleVO detalle = new DetalleVO();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setUnidades(prod.getCantidad());
            detalle.setPrecioUnidad(prod.getPrecio());

            // Impuesto dinámico desde configuración
            double impuestoDefecto = 21;
            try {
                impuestoDefecto = Double.parseDouble(configuracionRepository
                        .findByClave("impuesto_por_defecto")
                        .orElseThrow()
                        .getValor());
            } catch (Exception e) {
                System.err.println("⚠️ Error al obtener impuesto_por_defecto, usando 21% por defecto");
            }

            detalle.setImpuesto(impuestoDefecto);

            double totalLinea = prod.getCantidad() * prod.getPrecio();
            detalle.setTotal(totalLinea);
            totalPedido += totalLinea;

            detalleRepository.save(detalle);
            productoRepository.save(producto);
        }

        pedido.setTotal(totalPedido);
        carritoService.vaciarPorUsuario(usuario.getId());
        return pedido.getNumFactura();
    }

    @Override
    @Transactional
    public PedidoVO buscarPorNumeroFactura(String numFactura) {
        return pedidoRepository.findByNumFactura(numFactura).orElse(null);
    }

    @Override
    @Transactional
    public ResumenPedidoCompletoDTO obtenerResumenPorFactura(String numFactura) {
        PedidoVO pedido = pedidoRepository.findWithDetallesByNumFactura(numFactura)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        List<ProductoResumenDTO> productos = pedido.getDetalles().stream()
                .map(detalle -> {
                    // System.out.println(detalle);
                    boolean yaValorado = valoracionRepository.existsByDetalle(detalle);
                    System.out.println("detalleId: " + detalle.getId() +
                   " productoId: " + detalle.getProducto().getId() +
                   " yaValorado: " + yaValorado);

                    return new ProductoResumenDTO(
                            detalle.getProducto().getId(), // productoId → va primero
                            detalle.getId(), // detalleId → va segundo
                            detalle.getUnidades(),
                            yaValorado);

                })
                .toList();
System.out.println(productos);
        return new ResumenPedidoCompletoDTO(
                pedido.getFecha().toString(),
                pedido.getMetodoPago(),
                pedido.getTransportista(),
                pedido.getNumFactura(),
                pedido.getTotal(),
                pedido.getTotal(),
                pedido.getEstado(),
                pedido.getFechaEntregaUsuario(),
                pedido.getNombre(),
                pedido.getApellidos(),
                pedido.getDireccion(),
                pedido.getAdicionales(),
                pedido.getCodigoPostal(),
                pedido.getPoblacion(),
                pedido.getProvincia(),
                pedido.getPais(),
                pedido.getTelefono(),
                productos);
    }

    @Override
    public List<PedidoListadoDTO> obtenerPedidosPorUsuario(Long usuarioId) {
        List<PedidoVO> pedidos = pedidoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);

        return pedidos.stream().map(pedido -> {
            PedidoListadoDTO dto = new PedidoListadoDTO();
            dto.setNumFactura(pedido.getNumFactura());
            dto.setFecha(pedido.getFecha());
            dto.setEstado(pedido.getEstado());
            dto.setTotal(pedido.getTotal());
            dto.setFechaEntregaUsuario(pedido.getFechaEntregaUsuario());

            List<String> imagenes = pedido.getDetalles().stream()
                    .map(det -> det.getProducto().getImagen())
                    .filter(Objects::nonNull)
                    .limit(4)
                    .collect(Collectors.toList());

            dto.setImagenesProductos(imagenes);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PedidoListadoDTO> obtenerTodosPedidos() {
        List<PedidoVO> pedidos = pedidoRepository.findAllWithDetallesAndProductos();

        return pedidos.stream().map(pedido -> {
            PedidoListadoDTO dto = new PedidoListadoDTO();
            dto.setNumFactura(pedido.getNumFactura());
            dto.setFecha(pedido.getFecha());
            dto.setEstado(pedido.getEstado());
            dto.setTotal(pedido.getTotal());
            dto.setFechaEntregaUsuario(pedido.getFechaEntregaUsuario());

            List<String> imagenes = pedido.getDetalles().stream()
                    .map(det -> det.getProducto().getImagen())
                    .filter(Objects::nonNull)
                    .limit(4)
                    .collect(Collectors.toList());

            dto.setImagenesProductos(imagenes);

            return dto;
        }).collect(Collectors.toList());
    }

    public PedidoVO transformarDTOaVO(PedidoDTO dto, UsuarioVO usuario) {
        PedidoVO pedido = new PedidoVO();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setMetodoPago(dto.getMetodoPago());
        pedido.setTransportista(dto.getTransportista());
        pedido.setTotal(dto.getTotal());
        pedido.setNumFactura(configuracionService.generarNumeroFactura());

        DireccionEnvioDTO direccion = dto.getDireccion();
        pedido.setNombre(direccion.getNombre());
        pedido.setApellidos(direccion.getApellidos());
        pedido.setDireccion(direccion.getDireccion());
        pedido.setAdicionales(direccion.getAdicionales());
        pedido.setCodigoPostal(direccion.getCodigoPostal());
        pedido.setPoblacion(direccion.getPoblacion());
        pedido.setProvincia(direccion.getProvincia());
        pedido.setPais(direccion.getPais());
        pedido.setTelefono(direccion.getTelefono());

        // ✅ Fecha seleccionada por el usuario
        pedido.setFechaEntregaUsuario(dto.getFechaEntregaUsuario());

        // Estado inicial del pedido
        pedido.setEstado("Pago aceptado");

        return pedido;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generarFacturaPdf(String numFactura) {
        ResumenPedidoCompletoDTO resumen = obtenerResumenPorFactura(numFactura);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 60, 40); // Márgenes ajustados
        PdfWriter.getInstance(doc, baos);
        doc.open();

        // Fuente
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        // Logo
        try {
            Image logo = Image.getInstance("src/main/resources/static/img/logoEmpresa/BCcomponentes_logo_sinfondo.png");
            logo.scaleToFit(120, 60);
            doc.add(logo);
        } catch (Exception e) {
            e.printStackTrace(); // Si falla el logo, continúa
        }

        // Título
        Paragraph titulo = new Paragraph("FACTURA SIMPLIFICADA", titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);
        doc.add(Chunk.NEWLINE);

        // Info factura
        PdfPTable info = new PdfPTable(3);
        info.setWidthPercentage(100);
        info.setWidths(new float[] { 2f, 2f, 3f });
        info.addCell(new PdfPCell(new Phrase("Nº de factura: " + resumen.getNumFactura(), normalFont)));
        info.addCell(new PdfPCell(new Phrase("Fecha: " + resumen.getFecha(), normalFont)));
        info.addCell(new PdfPCell(new Phrase("Forma de pago: " + resumen.getMetodoPago(), normalFont)));
        for (PdfPRow row : info.getRows()) {
            for (PdfPCell cell : row.getCells()) {
                if (cell != null) {
                    cell.setBorder(Rectangle.NO_BORDER);
                }
            }
        }

        doc.add(info);
        doc.add(Chunk.NEWLINE);

        // Tabla productos
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[] { 1.5f, 4f, 1.2f, 1f, 1.3f });
        tabla.addCell(new PdfPCell(new Phrase("Código", boldFont)));
        tabla.addCell(new PdfPCell(new Phrase("Artículo", boldFont)));
        tabla.addCell(new PdfPCell(new Phrase("Precio", boldFont)));
        tabla.addCell(new PdfPCell(new Phrase("Und", boldFont)));
        tabla.addCell(new PdfPCell(new Phrase("Total", boldFont)));

        double baseImponible = 0;
        for (ProductoResumenDTO p : resumen.getProductos()) {
            ProductoVO producto = productoRepository.findById(p.getId()).orElse(null);
            if (producto != null) {
                double subtotal = producto.getPrecio() * p.getCantidad();
                baseImponible += subtotal;
                tabla.addCell(String.valueOf(p.getId()));
                tabla.addCell(producto.getNombre());
                tabla.addCell(String.format("%.2f €", producto.getPrecio()));
                tabla.addCell(String.valueOf(p.getCantidad()));
                tabla.addCell(String.format("%.2f €", subtotal));
            }
        }
        doc.add(tabla);
        doc.add(Chunk.NEWLINE);

        // Totales
        double iva = baseImponible * 0.21;
        double totalFactura = baseImponible + iva;

        PdfPTable totales = new PdfPTable(2);
        totales.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totales.setTotalWidth(300);
        totales.setWidths(new float[] { 2f, 1f });
        totales.setLockedWidth(true);

        totales.addCell(new PdfPCell(new Phrase("Base Imponible", boldFont)));
        totales.addCell(new PdfPCell(new Phrase(String.format("%.2f €", baseImponible), normalFont)));

        totales.addCell(new PdfPCell(new Phrase("IVA (21%)", boldFont)));
        totales.addCell(new PdfPCell(new Phrase(String.format("%.2f €", iva), normalFont)));

        totales.addCell(new PdfPCell(new Phrase("TOTAL FACTURA", boldFont)));
        totales.addCell(new PdfPCell(new Phrase(String.format("%.2f €", totalFactura), normalFont)));

        doc.add(totales);

        doc.close();
        return baos.toByteArray();
    }

    @Override
    public List<VentasMensualesDTO> obtenerVentasPorMes() {
        return pedidoRepository.obtenerVentasAgrupadasPorMes();
    }

    @Override
    public ResumenEstadisticasDTO obtenerResumenGeneral() {
        Double total = pedidoRepository.obtenerTotalVentas();
        if (total == null)
            total = 0.0;

        Long totalPedidos = pedidoRepository.contarTotalPedidos();
        Long totalUsuarios = usuarioRepository.contarTotalUsuarios();

        return new ResumenEstadisticasDTO(total, totalPedidos, totalUsuarios);
    }

}
