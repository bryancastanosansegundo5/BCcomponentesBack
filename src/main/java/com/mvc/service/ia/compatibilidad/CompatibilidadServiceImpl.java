package com.mvc.service.ia.compatibilidad;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.messages.AssistantMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.model.ia.compatibilidad.CompatibilidadRequestDTO;
import com.mvc.model.ia.compatibilidad.CompatibilidadResponseDTO;
import com.mvc.model.producto.ProductoDTO;
import com.mvc.model.producto.ProductoVO;
import com.mvc.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompatibilidadServiceImpl implements ICompatibilidadService {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("classpath:templates/CompatibilidadPrompt.st")
    private Resource compatibilidadPromptTemplate;

    @Override
    public CompatibilidadResponseDTO comprobarCompatibilidad(CompatibilidadRequestDTO request) {
        try {
            System.out.println("🛒 [1] Recibiendo request: " + request);

            // 🔹 1. Buscar los productos que el usuario tiene en su carrito
            List<ProductoVO> productosCarrito = productoRepository.findAllById(request.getProductos());
            System.out.println("📦 [2] Productos en carrito encontrados: " + productosCarrito.size());

            // 🔹 2. Buscar todos los productos activos de la tienda
            List<ProductoVO> productosTienda = productoRepository.findByBajaFalse();
            System.out.println("🏬 [3] Productos activos en tienda: " + productosTienda.size());

            // 🔹 3. Mapear ambos a DTOs
            List<ProductoDTO> carritoDTO = productosCarrito.stream()
                    .map(this::convertirProductoAVistaIA)
                    .collect(Collectors.toList());
            System.out.println("🛠 [4] CarritoDTO mapeado: " + carritoDTO.size());

            List<ProductoDTO> tiendaDTO = productosTienda.stream()
                    .map(this::convertirProductoAVistaIA)
                    .collect(Collectors.toList());
            System.out.println("🛠 [5] TiendaDTO mapeado: " + tiendaDTO.size());

            // 🔹 4. Serializar las dos listas a JSON
            String productosCarritoJson = objectMapper.writeValueAsString(carritoDTO);
            String productosTiendaJson = objectMapper.writeValueAsString(tiendaDTO);
            System.out.println("📝 [6] JSON carrito generado: " + productosCarritoJson.length() + " caracteres");
            System.out.println("📝 [7] JSON tienda generado: " + productosTiendaJson.length() + " caracteres");

            // 🔹 5. Leer contenido del prompt como texto
            String templateRaw = new String(compatibilidadPromptTemplate.getInputStream().readAllBytes());
            System.out.println("📄 [8] Contenido del archivo .st cargado:");
            System.out.println(templateRaw);

            // 🔹 6. Crear PromptTemplate desde el String
            PromptTemplate promptTemplate = new PromptTemplate(templateRaw);
            System.out.println("🛠 [9] PromptTemplate creado correctamente.");

            Map<String, Object> variables = new HashMap<>();
            variables.put("productos_carrito", productosCarritoJson);
            variables.put("productos_tienda", productosTiendaJson);

            // 🔹 7. Crear el prompt real
            var prompt = promptTemplate.create(variables);
            System.out.println("💬 [10] Prompt final generado:");
            System.out.println(prompt.getContents());

            // 🔹 8. Llamar a la IA
            System.out.println("🤖 [11] Enviando prompt a la IA...");
            String respuestaJson = chatClient.prompt(prompt).call().content();
            System.out.println("✅ [12] Respuesta de la IA recibida:");
            System.out.println(respuestaJson);

            // 🔹 9. Parsear la respuesta a DTO
            CompatibilidadResponseDTO resultado = objectMapper.readValue(respuestaJson,
                    CompatibilidadResponseDTO.class);
            System.out.println("🎯 [13] Resultado parseado correctamente.");

            return resultado;

        } catch (Exception e) {
            System.out.println("💥 [ERROR] Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error comprobando compatibilidad de productos", e);
        }
    }

    private ProductoDTO convertirProductoAVistaIA(ProductoVO vo) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(vo.getId());
        dto.setNombre(vo.getNombre());
        dto.setDescripcion(vo.getDescripcion());
        dto.setCaracteristicas(vo.getCaracteristicas());
        dto.setPrecio(vo.getPrecio());
        dto.setStock(vo.getStock());
        dto.setImpuesto(vo.getImpuesto());
        dto.setBaja(vo.isBaja());
        dto.setSocket(vo.getSocket());
        dto.setTipoRam(vo.getTipoRam());
        dto.setPcie(vo.getPcie());
        dto.setPotenciaW(vo.getPotenciaW());
        dto.setConsumo(vo.getConsumo());
        dto.setChipset(vo.getChipset());
        dto.setVendidos(vo.getVendidos());
        dto.setFactorForma(vo.getFactorForma());
        dto.setCategoriaId(vo.getCategoria().getId());
        dto.setImagen(vo.getImagen());
        dto.setProveedorId(vo.getProveedor().getId());
        return dto;
    }
}
